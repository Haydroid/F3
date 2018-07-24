package com.act.f6test;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.act.f3api.F3CardPosition;
import com.act.f3api.F3MoveMethod;

/**
 * Created by chy on 15-5-12.
 */
public class TestThread extends Thread {
    final int MSG_DISPENSING = 1;
    final int MSG_READING = 2;
    final int MSG_READCOMPL = 3;
    final int MSG_RETAINNING = 4;
    final int MSG_WAITTAKEOUT = 5;
    final int MSG_SENSOR = 6;
    final int MSG_RETURNBOX = 7;

    byte [] recvdata = new byte[256];
    static TestStep step;

    enum  TestStep {
        START,
        DISP,
        READ,
        EJECT,
        RETAIN,
        WAITTAKEOUT,
        SCAN,
        STOP
    }

    Context mContext;
    TestPlanKind mKind;
    TextView mTvOutput;

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (mTvOutput.getLineCount() > 512) {
                mTvOutput.setText("");
            }
            switch (msg.what) {
                case MSG_DISPENSING:
                    mTvOutput.append("正在发卡...\n");
                    break;

                case MSG_READING:
                    mTvOutput.append("正在读卡...\n");
                    break;

                case MSG_READCOMPL:
                    String data;
                    String text;
                    /*(if(Application.testThread != null) {
                        F3TrackData[] tracks = (F3TrackData[]) msg.obj;
                        for (int i = 0; i < tracks.length; i++) {
                            if (tracks[i].getStatus() != null) {
                                data = tracks[i].getData();
                                text = String.format("磁道 #%d [%03d][%s]\n", tracks[i].getSource(), data.length(), data);
                            } else {
                                data = Application.getTrackStatusDesc(tracks[i].getStatus());
                                text = String.format("磁道 #%d [%s]\n", tracks[i].getSource(), data);
                            }

                            mTvOutput.append(text);
                        }
                    }*/
                    break;

                case MSG_RETAINNING:
                    mTvOutput.append("正在回收卡...\n\n");
                    break;

                case MSG_WAITTAKEOUT:
                    mTvOutput.append("请取卡...\n\n");
                    break;
                case MSG_SENSOR:
                   if( InformDisplay() == -1) step = TestStep.STOP;
                    break;
                case MSG_RETURNBOX:

                    if(recvdata[0] == '2'){
                        mTvOutput.append("回收箱已满。\n\n");
                        step = TestStep.STOP;
                    };
                    break;
            }


            super.handleMessage(msg);
        }
    };

    public TestThread(Context context, TextView tvOutput, TestPlanKind kind) {
        super();
        mContext = context;
        mTvOutput = tvOutput;
        mKind = kind;
    }

    @Override
    public void run() {

        F3CardPosition pos;
        boolean flag = false;
        int lRet;
        String str = "";
        step = TestStep.SCAN;

        try {
            Looper.prepare();
            while (!isInterrupted()) {

                str = String.format("step =====" );
                Log.i("自动化测试", str);
                //sleep(500);
                switch(step) {
                    case  SCAN:
                        step = TestStep.START;
                        //recvdata = Application.cr.GetCardBoxStatus();
                        sendMessage(MSG_SENSOR, null);
                        //recvdata = Application.cr.GetRetainBinStatus();
                        sendMessage(MSG_RETURNBOX, null);
                        break;
                    case START:

                        break;

                    case DISP:
                        sendMessage(MSG_DISPENSING, null);
                        //Application.cr.dispenseCard();
                        step = TestStep.READ;
                        break;
                    case READ:
                        if (mKind == TestPlanKind.TPK_DISP_READ_EJECT)
                            step = TestStep.EJECT;
                        else
                            step = TestStep.RETAIN;
                        break;

                    case EJECT:
                        Application.cr.moveCard(F3MoveMethod.MOVE_TOCARDOUT.getValue());
                        step = TestStep.WAITTAKEOUT;
                        break;

                    case RETAIN:
                        sendMessage(MSG_RETAINNING, null);
                        //Application.cr.moveCard(F6MoveMethod.MOVE_RECYCLE.getValue());
                        step = TestStep.SCAN;
                        sleep(1000);
                        break;

                    case WAITTAKEOUT:
                        pos = Application.cr.getCardPosition();

                        break;
                    case STOP:
                        return;
                }

                Thread.sleep(500);
            }
            Looper.loop();
        } catch (Exception e) {
            Application.showError(e.getMessage(), mContext);
        }
    }

    private void sendMessage(int what, Object data) {
        Message msg = new Message();
        msg.what = what;
        msg.obj = data;
        mHandler.sendMessage(msg);
    }

    public int InformDisplay()
    {
        Log.i("自动化测试", "recvdata[0] : "+ recvdata[0] );
        byte bStatus = recvdata[0];
        if( bStatus == '0' )
        {
            mTvOutput.append("发卡箱：卡空\n\n");
            return -1;
        }else if(bStatus == 'A'){
            mTvOutput.append("发卡箱：未到位\n\n");
            return -1;
        }
        return 0;
    }

}
