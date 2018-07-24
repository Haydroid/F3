package com.act.f6test;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import com.act.f3api.F3IDCReader;

import java.util.Vector;

/**
 * Created by chy on 15-4-21.
 */
public class Application {
    static F3IDCReader cr = new F3IDCReader();
    static Activity mainView = null;
    static TestThread testThread = null;
    static Context CTX = null;

    static final APDUCmdItem CMD_SELECT_PSE = new APDUCmdItem("1.SELECT(PSE)(选择)",
            new byte[]{0x00, (byte) 0xA4, 0x04, 0x00, 0x0E, 0x31, 0x50, 0x41, 0x59, 0x2E, 0x53, 0x59, 0x53, 0x2E, 0x44, 0x44, 0x46, 0x30, 0x31, 0x00});

    static final APDUCmdItem CMD_SELECT_AID = new APDUCmdItem("1.SELECT(AID)(选择)",
            new byte[]{0x00, (byte) 0xA4, 0x04, 0x00, 0x08, (byte) 0xA0, 0x00, 0x00, 0x03, 0x33, 0x01, 0x01, 01, 0x00});

    static final APDUCmdItem CMD_SELECT_AID2 = new APDUCmdItem("1.SELECT(AID2)(选择)",
            new byte[]{0x00, (byte) 0xA4, 0x04, 0x00, 0x07, (byte) 0xA0, 0x00, 0x00, 0x03, 0x33, 0x01, 0x01, 01});

    static final APDUCmdItem CMD_SELECT_COMMD = new APDUCmdItem("1.SELECT(选择)",
            new byte[]{0x00, (byte) 0xA4, 0x04, 0x00, 0x06,(byte) 0xA0, 0x00, 0x00, 0x03, 0x33, 0x01});

    static final APDUCmdItem CMD_READ_RECORD = new APDUCmdItem("2.READ RECORD(读记录)",
            new byte[]{0x00, (byte) 0xB2, 0x01, 0x14, 0x00});

    static final APDUCmdItem CMD_READ_RECORD2 = new APDUCmdItem("2.READ RECORD2(读记录)",
            new byte[]{0x00, (byte) 0xB2, 0x02, 0x14, 0x00});

    static final APDUCmdItem CMD_GET_DATA1 = new APDUCmdItem("3.GET DATA(余额查询)",
            new byte[]{(byte) 0x80, (byte) 0xCA, (byte) 0x9F, 0x79, 0x00});

    static final APDUCmdItem CMD_GET_DATA2 = new APDUCmdItem("3.GET DATA(日志记录)",
            new byte[]{(byte) 0x80, (byte) 0xCA, (byte) 0x9F, 0x4F, 0x00});

    static final APDUCmdItem CMD_CHARGE = new APDUCmdItem("4.",
            new byte[]{(byte) 0x00, (byte) 0x84, (byte) 0x00, 0x00, 0x08});

    public static void showError(String strMsg, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("错误");
        builder.setMessage(strMsg);
        builder.setPositiveButton("确定", null);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.show();
    }

    public static void showInfo(String strMsg, String strTitle, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(strTitle);
        builder.setMessage(strMsg);
        builder.setPositiveButton("确定", null);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.show();
    }

    public static Vector<APDUCmdItem> getPredCmdItems() {
        Vector<APDUCmdItem> cmds = new Vector<APDUCmdItem>();
        cmds.add(CMD_SELECT_PSE);
        cmds.add(CMD_SELECT_AID); //
        cmds.add(CMD_SELECT_AID2);
        cmds.add(CMD_SELECT_COMMD);
        cmds.add(CMD_READ_RECORD);
        cmds.add(CMD_READ_RECORD2);
        cmds.add(CMD_GET_DATA1);
        cmds.add(CMD_GET_DATA2);
        cmds.add(CMD_CHARGE);
        return cmds;
    }

    /*public static String getTrackStatusDesc(F3TrackStatus status) {
        switch (status) {
            case TRACK_DATAOK:
                return "数据正常";
            case TRACK_SS_ERROR:
                return "SS 错误";
            case TRACK_ES_ERROR:
                return "ES 错误";
            case TRACK_VRC_ERROR:
                return "VRC 错误";
            case TRACK_LRC_ERROR:
                return "LRC 错误";
            case TRACK_NODATA:
                return "空白轨";
            default:
                return "未知状态";
        }
    }*/

}
