package com.act.f3api;
import android.util.Log;

import static java.lang.Thread.sleep;

/**
 * Created by xsh on 18-01-13.
 */
public class F3IDCReader {
    private class RDATA {
        public byte[] buffer ;
        public int ReviceDataLen;
    }
    private static final String TAG = "f3api:";
    private String mPortName = null;
    private int mHandle = 0;
    private static int LINK = -1;
    private static final int LINK_RS232 = 0;
    private native static int csConnect(String portName, int baudRate) throws Exception;
    private native static void csDisconnect(int handle) throws Exception;
    private native static void csExecute(int handle, byte[] cmpData, int timeOut, RDATA resData, int rdtOffset, boolean bStatus) throws Exception;
    private native static void csExecuteEncode(int handle, byte[] cmpData, int timeOut, RDATA resData) throws Exception;
    public static final int USBDATALEN = 64;
    static {
        System.loadLibrary("F3jniA");
    }
    public F3IDCReader() {
    }
     //@串口连接
    //@参数1：portName 串口名称 如： ttyS0
    // @参数2： baudRate 波特率 默认9600
    //@返回值：成功返回true 失败返回false
    public boolean connect(String portName, int baudRate) throws Exception {
        if (mHandle != 0
                && mPortName != null
                && mPortName.equalsIgnoreCase(portName))
            return false;
        LINK = LINK_RS232;
        String str = "";
        switch (LINK) {
            case LINK_RS232:
                F3IDCReader Rs232Connect = new F3IDCReader();
                str = String.format("portName = %s  baudRate = %d ",portName,baudRate);
                Log.i(TAG, str);
                mHandle = Rs232Connect.csConnect(portName, baudRate); // /dev/ttyS0  /dev/ttyCOM0
                mPortName = portName;
                break;
        }
        return true;
    }

    //@断开连接
    //@参数：无
    //@返回值：无 ，失败抛出异常提示
    public void disconnect() throws Exception {
        if (LINK == LINK_RS232) {
            csDisconnect(mHandle);
        }
        mHandle = 0;
        mPortName = null;
        LINK = -1;
    }

    //@复位设备，并返回固件版本号
    //@参数1： action 复位设备的方式，有：
    //         RESET_NOACTION(0x33) 复位无动作
    //         RESET_FRONT_CARD(0x30) 复位移动卡到前端
    //         RESET_RETURN_CARD(0x31) 复位回收卡片
    //@返回值： 成功返回设备固件版本，失败抛出异常提示
    public String reset(F3ResetAction action) throws Exception {
        RDATA resData = new RDATA();
        byte[] bd = new byte[]{0x30, action.getValue()};
        execute(bd, resData, 0, false);
        if (resData.buffer == null) return null;
        return new String(resData.buffer);
    }
    //@使能前端进卡
    //@参数：无
    //@返回值：无，失败抛出异常提示
    public  void  PermitInsertion() throws Exception {
        RDATA resData = new RDATA();
        byte[] bd = new byte[]{0x33, 0x30};
         execute(bd, resData, 0,false);
    }

    //@卡片移动操作
    //@参数1：method卡片移动的方式 ，有如下值：
    //       MOVE_TOCARDOUT(0x39)  前端弹出卡片
    //       MOVE_TOFRONTEND(0x30) 前端出卡口
    //       MOVE_TORFREAD(0x32) 射频位
    //       CAPTURE_TO_BOX(0x33) 回收
    //       MOVE_TOICCPOS(0x31) 移动IC位
    //@返回值：无 失败抛出异常提示
    public void moveCard(byte method) throws Exception {
        RDATA resData = new RDATA();
        byte[] bd = new byte[]{0x32, method};
        execute(bd, resData, 0, false);
    }

    //@禁止前端进卡
    //@参数：无
    //@返回值：无 失败抛出异常提示
    public void DenieInsertion() throws Exception {
        RDATA resData = new RDATA();
        byte[] bd = new byte[]{0x33, 0x31};
        execute(bd, resData, 0, false);
    }

    //@查询卡机状态
    //@参数：无
    //@返回值： 成功返回值 F3CardPosition 卡机状态值，如下：
    //         bLaneStatus  卡片位置 : 机内无卡(0x30), 卡在出卡口位(0x31),机内有卡(0x32)
    //         bCardBoxStatus 卡箱状态: 卡箱空(0x30), 卡箱卡少(0x31),卡箱卡足(0x32)
    //    	  fCaptureBoxFull  回收箱状态： 满（true）,未满（false）
    //     失败抛出异常提示
    public F3CardPosition getCardPosition() throws Exception {
        RDATA resData = new RDATA();
        byte[] bd = new byte[]{0x31, 0x30};
        F3CardPosition values = new F3CardPosition();
        execute(bd, resData, 0, true);

        values.bLaneStatus = resData.buffer[0];
        values.bCardBoxStatus = resData.buffer[1];
        if(resData.buffer[2] == '0')
        values.fCaptureBoxFull = false;
        if(resData.buffer[2] == '1')
            values.fCaptureBoxFull = true;
        return values;
    }

    //@cpu卡上电
    //@参数：无
    //@返回值： 成功返回ATR ，失败抛出异常
    public byte[] chipPowerOn() throws Exception {
        RDATA resData = new RDATA();
        byte[] bd = new byte[]{0x51, 0x30, 0x30,0x30};
        execute(bd, resData, 0, false);
        //byte[] atr = new byte[bd.length - 2];
        byte[] atr = new byte[resData.buffer.length - 2];
        System.arraycopy(resData, 2, atr, 0, resData.buffer.length - 2);
        return atr;
    }

    //@cpu卡下电
    //@参数：无
    //@返回值： 成功返回ATR ，失败抛出异常
    public void chipPowerOff() throws Exception {
        RDATA resData = new RDATA();
        byte[] bd = new byte[]{0x51, 0x31};
        execute(bd, resData, 0, false);
    }
    //@APDU数据交换
    //@参数1：indata 需要下发APDU命令
    //@返回值： 成功返回PDU命令响应 ，失败抛出异常
    public byte[] chipIo(byte[] indata) throws Exception {
        RDATA resData = new RDATA();
        byte []RBuf = null;
        byte[] bd = new byte[]{0x51, 0x33};
        byte[] bc = new byte[indata.length + bd.length];
        System.arraycopy(bd, 0, bc, 0, bd.length);
        System.arraycopy(indata, 0, bc, bd.length, indata.length);
        execute(bc, resData, 0, false);
        if(resData.buffer.length> 0) {
            RBuf = new byte[resData.buffer.length];
            System.arraycopy(resData.buffer,0, RBuf, 0,resData.buffer.length);
        }else return null;
        return RBuf;
    }

    //@命令报文发送
    //@参数1：cmdA 需要下发命令
    //@参数2：resData返回的数据BUF 和长度
    //参数3：rdtOffset 接收数据的偏移量（备用，默认0）
    //参数4： bStatus 状态值（备用，默认0）
    //@返回值： 成功返回命令响应 ，失败抛出异常
    public byte[] execute(byte[] cmdA, RDATA resData, int rdtOffset, boolean bStatus) throws Exception {

        switch (LINK) {
            case 0:
                csExecute(mHandle, cmdA, 0, resData, rdtOffset, bStatus);
                Log.i("csExecute： ", "csExecutelen:"+ resData.buffer.length);
                if (resData.buffer.length != 0) return resData.buffer;
        }
        return null;
    }
}
