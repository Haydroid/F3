#include "stdafx.h"
#include "f3_error.h"
#include "f3_idcrdr.h"

//
// 传输控制字符
//
#define TCC_ACK		0x06
#define TCC_NAK		0x15
#define TCC_ENQ		0x05
#define TCC_EOT		0x04
#define TCC_STX		0xF2
#define TCC_ETX		0x03
#define CMT	'C'


#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, TAG, fmt, ##args)

F2IDCReader::F2IDCReader()
{
	pthread_mutex_init(&mutex, NULL);
	isConnect = false;
}

F2IDCReader::~F2IDCReader()
{
	pthread_mutex_destroy(&mutex);
}

static int  GetResults(BYTE e0, BYTE e1)
{
#define ERRMAP(_e0, _e1, _res) \
	if (e0 == _e0 && e1 == _e1)	return _res;

#include "errmap.h"

	return F3_E_UNKNOWN_ERROR;
}

int F2IDCReader::connect(char *portName, speed_t speed)
{
	LOGD("串口 :%s\n  " ,portName);
    m_fd = open(portName, O_RDWR | O_NOCTTY | O_NDELAY);
    if (m_fd == -1)
        return F3_E_PORT_UNAVAILABLE;

	int iret =  configPort(m_fd, speed);
    if ( iret  != 0){
        LOGD("串口配置失败!! :%d " ,iret );
    	close(m_fd);
		m_fd = 0;
    	return F3_E_PORT_UNAVAILABLE;
    }
	fcntl(m_fd, F_SETFL, 0);
	BYTE bd[] = {0x31,0x30};
	int ret = execute(bd,2,0,0,0);
	LOGD("m_fd = %d portName = %s   speed = %d execute: ret = %d ", m_fd,portName,speed,ret);
	if(ret != 0 ){
		close(m_fd);
		return ret;
	}
	if (isConnect == false) {
        close(m_fd);
        return 0;
    }
    return 0;
}

int F2IDCReader::disconnect()
{
	close(m_fd);
	return 0;
}

int F2IDCReader::execute(BYTE *cmpBuffer, int cmpLength, int timeOut, int rdtOffset, bool bStatus)
{
	    int ret;
		ret = sendCommand(cmpBuffer, cmpLength);
		if (ret != 0)return ret;
	    isConnect = true;
	    ret = recvResponse(timeOut, rdtOffset, bStatus);
	    if(ret != 0) return  ret;

	    ret = sendENQ();
	    if(ret != 0) return  ret;
        return ret;

}


int F2IDCReader::sendCommand(BYTE *cmpData, int cmpLength)
{
	int nTextLength = 3;
	nTextLength += (cmpLength-2);

	if (cmpLength > MAX_MSG_LENGTH - 6)
		return F3_E_MESSAGE_TOO_LONG;

	int msgLength = 0;
	m_msgBuffer[msgLength++] = TCC_STX;
	m_msgBuffer[msgLength++] = 0;
	m_msgBuffer[msgLength++] = ((nTextLength ) >> 8) & 0xFF;
	m_msgBuffer[msgLength++] = (nTextLength) & 0xFF;
	m_msgBuffer[msgLength++] = CMT;

	if (cmpData != NULL && cmpLength > 0) {
		memcpy(&m_msgBuffer[msgLength], cmpData, cmpLength);
		msgLength += cmpLength;
	}
	m_msgBuffer[msgLength++] = TCC_ETX;
	msgLength++;//for BCC
	m_msgBuffer[msgLength -1 ] = getBCC(m_msgBuffer, msgLength);

	return writeData(m_msgBuffer, msgLength);
}

int F2IDCReader::recvACK()
{
	int ret;
	BYTE b;
	fd_set fds;
	ret = waitBytesAvailable(50, &fds);
	if (ret != 0) {
		if (ret == F3_E_COMM_TIMEOUT)
			return F3_E_DEV_NOT_RECOGNIZED;
		return ret;
	}

	while (true)
	{
		ret = readData(&b, 1, &fds);
		LOGD(" readData = %d ",ret);
		if (ret <= 0)
			return F3_E_UNKNOWN_ERROR;
		if (b != TCC_ACK) {
			LOGD("接收数据下（ACK）：= %x  ret = %d ",b,ret);
			return F3_E_UNKNOWN_ERROR;
		}else break;
	}
	return 0;

}

int F2IDCReader::sendENQ()
{
	BYTE b = TCC_ENQ;
	return writeData(&b, 1);
}

int F2IDCReader::recvResponse(int timeOut, int rdtOffset, bool bStatus)
{
	int ret;
	int msgLength;
	fd_set fds;
	memset(m_msgBuffer,0,sizeof(m_msgBuffer));
	if (timeOut == 0)
		ret = waitBytesAvailable(5000, &fds);
	else
		ret = waitBytesAvailable(timeOut, &fds);
	//LOGE("ret1 = %d ",ret);
	if (ret != 0)
		return ret;
	ret = readData(m_msgBuffer,256, &fds);
	if (ret <= 0) return F3_E_COMM_TIMEOUT;
	int  length = 0 ;
	if(m_msgBuffer[0] == TCC_ACK && m_msgBuffer[1] == TCC_STX  && ret > 4)
	{
		length = (m_msgBuffer[3] << 8) | m_msgBuffer[4];//总长度
		msgLength =  length+6;
		if (msgLength > MAX_MSGSIZE || msgLength < 11 ) return F3_E_COMM_ERROR;
		// 检查异或校验
		BYTE bcc = getBCC(&m_msgBuffer[1], msgLength);
		LOGD("  %02x bcc = %02x ",m_msgBuffer[msgLength],bcc);
		if (m_msgBuffer[msgLength] != bcc)
			return F3_E_COMM_ERROR;

		int  RDTLen = msgLength - 12;
		if (m_msgBuffer[5] == 'N') // 0x4E
		{
			rdtlen = 0;
			rdtptr = NULL;
			return GetResults(m_msgBuffer[8], m_msgBuffer[9]);
		}else if (m_msgBuffer[5] != 'P' )
			return F3_E_COMM_ERROR;

		if(length > 0 && m_msgBuffer[5] == 'P') {
			if(!bStatus) {
				rdtptr = &m_msgBuffer[11]; //
				rdtlen = RDTLen;
			}else {

				rdtptr = &m_msgBuffer[8]; //
				rdtlen = msgLength - 8;
			}
		}
		if(rdtlen == 0)  rdtlen = 1;
		return 0;
	}
	return F3_E_COMM_ERROR;
}

int F2IDCReader::readData(BYTE *buffer, int bytesToRead, fd_set *fds)
{
	int count = 0;
	int bytesRead =0,ReturnLenth = 0;
	while (true) {
		if (FD_ISSET(m_fd, fds)){
			bytesRead = read(m_fd, &buffer[count], bytesToRead - count);
			//LOGE("readData : bytesRead = %d  %02x",bytesRead,buffer[count]);
			count += bytesRead;
		}
		else {
			LOGE("readData error: FS_ISSET");
			return F3_E_UNKNOWN_ERROR	;
		}
		if(count >= 5 ){
			ReturnLenth =  (buffer[3] << 8) | buffer[4];
			if( (ReturnLenth+7) == count) break;
		}
	}
	logCommData(false, buffer, count);
	return count;
}

int F2IDCReader::writeData(BYTE *buffer, int bytesToWrite)
{
    int bytesWritten = write(m_fd, buffer, bytesToWrite);
    if (bytesWritten != bytesToWrite)
        return F3_E_UNKNOWN_ERROR	;
	logCommData(true, buffer, bytesToWrite);
    return 0;
}

int F2IDCReader::waitBytesAvailable(int timeOut, fd_set *fds)
{
    int nfds;
	timeval tv;
	tv.tv_sec = 10;
	tv.tv_usec = timeOut * 100;

	FD_ZERO(fds);
	FD_SET(m_fd, fds);
	nfds = select(m_fd + 1, fds, NULL, NULL, &tv);
	if (nfds < 0)
		return F3_E_UNKNOWN_ERROR	;
	if (nfds == 0)
		return F3_E_COMM_TIMEOUT;
	return 0;
}

BYTE F2IDCReader::getBCC(BYTE *buffer, int length)
{
	BYTE bcc = 0;
	for (int i = 0; i < length -1; i++ )
		bcc ^= buffer[i];
	return bcc;
}

int F2IDCReader::configPort(int fd, speed_t speed)
{
	struct termios options;
	if (tcgetattr(fd, &options) != 0)
		return -1;
	cfmakeraw(&options);
	if (cfsetispeed(&options, speed) != 0)
		return -1;
	if (cfsetospeed(&options, speed) != 0)
		return -1;

	options.c_cflag |= CS8; //8位数据位
	options.c_cflag &= ~PARENB;//无奇偶校验
	options.c_cflag &= ~CSTOPB;// 1位停止位

	if (tcflush(fd, TCIOFLUSH) == -1) return -1;
	if (tcsetattr(fd, TCSANOW, &options) != 0)
		return -1;

	return 0;
}

static __inline__ char hexChar(BYTE digit)
{
	if (digit <= 9)
		return '0' + digit;
	else
		return 'A' + (digit - 10);
}

void F2IDCReader::logCommData(bool isSend, BYTE *buffer, int length)
{
	const int W = 3;
	const int L = length * W;
    char *hstr = (char*)malloc(L + 1);

    if (hstr != NULL) {
        for (int i = 0; i < length; i++ ) {
            hstr[i * W] = hexChar(buffer[i] >> 4);
            hstr[i * W + 1] = hexChar(buffer[i] % 16);
            hstr[i * W + 2] = ' ';
        }
	    hstr[L] = 0;
	    LOGD("%s : %s\n", isSend ? "S" : "R", hstr);
        free(hstr);
	}
}
