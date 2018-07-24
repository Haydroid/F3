#include "stdafx.h"
#include "f3_error.h"
#include "f3_idcrdr.h"


class AutoLock
{
private:
    pthread_mutex_t *m_lptx;
    bool m_manual;

public:
    AutoLock(pthread_mutex_t *lptx, bool manual = false)
        : m_lptx(lptx), m_manual(manual) {
        pthread_mutex_lock( m_lptx );
    }

    ~AutoLock() {
    	if (!m_manual)
        	pthread_mutex_unlock( m_lptx );
    }

    void release() {
    	pthread_mutex_unlock( m_lptx );
    }
};

static speed_t getBaudrate(jint baudrate)
{
	switch(baudrate) {
	case 1200: return B1200;
	case 2400: return B2400;
	case 4800: return B4800;
	case 9600: return B9600;
	case 19200: return B19200;
	case 38400: return B38400;
	case 57600: return B57600;
	case 115200: return B115200;
	default: return -1;
	}
}

static jint setRespData(JNIEnv *env, jobject respData, jbyte *rdtPtr, int rdtLen)
{
	jclass cls;
	jfieldID fid;
	jbyteArray ba;

	cls = env->FindClass("com/bonc/vtm/hardware/card/CardDriver$ReceiveData");
	if (cls == NULL)
		return F3_E_UNKNOWN_ERROR;
	fid = env->GetFieldID(cls, "buffer", "[B");
	if (fid == NULL)
		return F3_E_UNKNOWN_ERROR;

	LOGD("rdtLen = %d  %x %x %x ",rdtLen ,*rdtPtr,*(rdtPtr+1),*(rdtPtr+2));

	ba = env->NewByteArray(rdtLen);
	if (ba == NULL) return F3_E_NO_MEMORY;

	env->SetByteArrayRegion(ba, 0, rdtLen, rdtPtr);
	env->SetObjectField(respData, fid, ba);
	return 0;
}

static const char* getMessage(int ret)
{
	switch (ret) {

		case F3_E_NOT_CONNECT		: return ("未建立连接");
		case F3_E_PORT_UNAVAILABLE	: return ("指定的COM端口不存在，或者被其它程序占用");
		case F3_E_DEV_NOT_RECOGNIZED		: return ("设备未就绪");
		case F3_E_COMM_ERROR			: return ("通讯错误。");
		case F3_E_COMM_TIMEOUT			: return ("通信超时");
		case F3_E_UNKNOWN_ERROR	: return ("检测到一个内部错误，但原因不明");
		case F3_E_MESSAGE_TOO_LONG		: return ("命令消息或接收的响应消息的长度超过了1024个字符");
		case F3_E_NO_MEMORY		: return ("没有足够的内存来完成当前的操作");
		case F3_E_INVALID_HANDLE	: return ("提供的句柄无效");
		case F3_E_INVALID_PARAMETER		: return ("提供的一个或多个参数无效");
		case F3_E_BUFFER_TOO_SMALL	: return ("接收数据的缓冲区太小");
		case F3_E_UNDEFINED_COMMAND			: return ("未定义的命令");
		case F3_E_DISABLED_COMMAND		: return ("命令不能在当前的状态下执行");
		case F3_E_UNSUPPORTED_COMMAND		: return ("不支持命令");
		case F3_E_CONTACT_NO_RELEASE		: return ("IC触点未释放");
		case F3_E_SENSOR_ABNORMALITY		: return ("传感器异常");
		case F3_E_CARD_JAMMED	: return ("卡片堵塞");
		case F3_E_TOO_LONG_CARD	: return ("插入到机内的卡片太长");
		case F3_E_TOO_SHORT_CARD	:return ("插入到机内的卡片太短");
		case F3_E_CARD_WITHDRAWN	: return ("回收卡时卡片被拿走");
		case F3_E_IC_SOLENOID_ERROR		: return ("IC电磁线圈错误");
		case F3_E_CANT_MOVED_TO_IC_POS			: return ("不能移动卡到IC触点位");
		case F3_E_CARD_POSITION_CHANGE	: return ("卡片位置被人为改变");
		case F3_E_COUNTER_OVERFLOW			: return ("回收卡计数器溢出 ");
		case F3_E_CARD_BOX_EMPTY		: return ("卡箱空");
		case F3_E_CAPTURE_BOX_FULL			: return ("回收箱满 ");
		case F3_E_NO_CARD_IN :        return ("机内无卡");
		default: return ("命令失败");
	}
}


static void triggException(JNIEnv *env, int ret)
{
	jclass cls = env->FindClass("java/lang/Exception");
	if (cls != NULL) {
		env->ThrowNew(cls, getMessage(ret));
		env->DeleteLocalRef(cls);
	}
}

F2IDCReader *idcr = NULL;

extern "C" JNIEXPORT jint JNICALL Java_com_bonc_vtm_hardware_card_CardDriver_csConnect
    (JNIEnv *env, jobject thiz, jstring portName, jint baudRate)
{
	char *s = NULL;
	jint ret;
	speed_t speed;

	LOGD("baudRate = %d  speed = %d",baudRate,speed);

    s = (char*)env->GetStringUTFChars(portName, 0);
    if (s == NULL) {
        ret = F3_E_NO_MEMORY;
        goto EXIT;
    }

	speed = getBaudrate(baudRate);
	if (speed == -1) {
		ret = F3_E_UNKNOWN_ERROR;
		goto EXIT;
	}
	idcr = new F2IDCReader();
	if (idcr == NULL) {
        ret = F3_E_NO_MEMORY;
        goto EXIT;
    }
    ret = idcr->connect(s, speed);
    if (ret != 0) {
    	delete idcr;
    	goto EXIT;
    }
EXIT:
    if (s != NULL)
        env->ReleaseStringUTFChars(portName, s);
    if (ret < 0) triggException(env, ret);
    return ret;
}

extern "C" JNIEXPORT void JNICALL Java_com_bonc_vtm_hardware_card_CardDriver_csDisconnect
    (JNIEnv *env, jobject thiz, jint handle)
{
	jint ret;
	//idcr = (F6IDCReader*)handle;
	if (idcr == 0) {
	triggException(env, F3_E_NOT_CONNECT);
	return;
	}
	AutoLock lock(&idcr->mutex, true);
	idcr->disconnect();
	lock.release();
	delete idcr;
    idcr = NULL;
}

extern "C" JNIEXPORT void JNICALL Java_com_bonc_vtm_hardware_card_CardDriver_csExecute
    (JNIEnv *env, jobject thiz, jint handle,jbyteArray cmpData, jint timeOut, jobject resData, jint rdtOffset, jboolean bStatus)
{
	//F6IDCReader *idcr;
	jbyte *cmpPtr = NULL;
	int cmpLen = 0;
	int ret = 0;
	//idcr = (F6IDCReader*)handle;
	if (idcr == 0) {
	   triggException(env, F3_E_NOT_CONNECT);
	   return;
	}
	AutoLock lock(&idcr->mutex);

	if (cmpData != NULL) {
		cmpLen = env->GetArrayLength(cmpData);
		cmpPtr = env->GetByteArrayElements(cmpData, NULL);
	}

	ret = idcr->execute((BYTE*)cmpPtr, cmpLen, timeOut, rdtOffset, bStatus);
	if (ret != 0)
		goto EXIT;

	if (resData != NULL)
		ret = setRespData(env, resData, (jbyte*)idcr->rdtptr, idcr->rdtlen);
EXIT:
	if (cmpPtr != NULL)
		env->ReleaseByteArrayElements(cmpData, cmpPtr, 0);

    if (ret != 0){
                 triggException(env, ret);
    }
}


///////////

extern "C" JNIEXPORT void JNICALL Java_com_bonc_vtm_hardware_card_CardDriver_csExecuteEncode
(JNIEnv *env, jobject thiz, jint handle,jbyteArray cmpData, jint timeOut, jobject resData)
{

}