package com.act.f6test;

import android.app.AlertDialog;
import android.content.Context;

/**
 * Created by Administrator on 2017/2/22 0022.
 */
public class MessageBox {
    public static void showError(String strMsg, Context context) {
        showDialog(strMsg, "ERROR", android.R.drawable.ic_dialog_alert, context);
    }

    public static void showError(int msgId, Context context) {
        showDialog(context.getString(msgId), "ERROR", android.R.drawable.ic_dialog_alert, context);
    }

    public static void showInfo(String strMsg, Context context) {
        showDialog(strMsg, context.getPackageName(), android.R.drawable.ic_dialog_info, context);
    }

    public static void showInfo(String strMsg, String strTitle, Context context) {
        showDialog(strMsg, strTitle, android.R.drawable.ic_dialog_info, context);
    }

    private static void showDialog(String strMsg, String strTitle, int iconId, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(strTitle);
        builder.setMessage(strMsg);
        builder.setPositiveButton("OK", null);
        builder.setIcon(iconId);
        builder.show();
    }
}
