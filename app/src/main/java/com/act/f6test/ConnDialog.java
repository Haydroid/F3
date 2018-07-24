package com.act.f6test;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.Vector;

/**
 * Created by chy on 15-4-22.
 */
public class ConnDialog extends Dialog {
    Context mContext;
    Spinner mSpinPorts;
    Spinner mSpinBaudRates;
    OnCloseListener mCloseListener = null;

    public interface OnCloseListener {
        void onClose(boolean isOk);
    }

    public ConnDialog(Context context) {
        super(context);
        mContext = context;
    }

    public void setCloseListener(OnCloseListener listener) {
        mCloseListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conn);
        setTitle("连接 F3-1300");
        setCancelable(false);

        mSpinPorts = (Spinner) findViewById(R.id.spinPorts);
        mSpinBaudRates = (Spinner) findViewById(R.id.spinBaudRates);

       SerialPortFinder finder = new SerialPortFinder();
        Vector<SerialPortItem> ports = finder.getAllPorts();
        ArrayAdapter<SerialPortItem> adapter1 = new ArrayAdapter<SerialPortItem>
                (mContext, R.layout.support_simple_spinner_dropdown_item, ports);
        mSpinPorts.setAdapter(adapter1);

        /*SerialPortFinder finder = new SerialPortFinder();
        Vector<SerialPortItem> ports = new Vector<SerialPortItem>();
        //finder.getAllPorts();
        ports.add(new SerialPortItem("COM0", "/dev/ttyS0"));
        ports.add(new SerialPortItem("COM1", "/dev/ttyS1"));
        ports.add(new SerialPortItem("COM2", "/dev/ttyS2"));
        ports.add(new SerialPortItem("COM3", "/dev/ttyS3"));

        ArrayAdapter<SerialPortItem> adapter1 = new ArrayAdapter<SerialPortItem>
                (mContext, R.layout.support_simple_spinner_dropdown_item, ports);
        mSpinPorts.setAdapter(adapter1);
        */
        Vector<String> baudRates = new Vector<String>();
        baudRates.add("9600");
        baudRates.add("19200");
        baudRates.add("38400");
        baudRates.add("57600");
        baudRates.add("115200");
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>
                (mContext, R.layout.support_simple_spinner_dropdown_item, baudRates);
        mSpinBaudRates.setAdapter(adapter2);

        SharedPreferences sp = mContext.getSharedPreferences("conn_pref", Context.MODE_PRIVATE);
        String lastPortName = sp.getString("PortName", "");
        int lastBaudRate = sp.getInt("BaudRate", 9600);
        for (int i = 0; i < mSpinPorts.getCount(); i++) {
            if (((SerialPortItem) mSpinPorts.getItemAtPosition(i)).getName().equalsIgnoreCase(lastPortName)) {
                mSpinPorts.setSelection(i);
                break;
            }
        }

        for (int i = 0; i < mSpinBaudRates.getCount(); i++) {
            if (Integer.parseInt((String) mSpinBaudRates.getItemAtPosition(i)) == lastBaudRate) {
                mSpinBaudRates.setSelection(i);
                break;
            }
        }

        final Button OkButton = (Button) findViewById(R.id.btnOK);
        OkButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    SerialPortItem port = (SerialPortItem) mSpinPorts.getSelectedItem();
                    String baudRate = (String) mSpinBaudRates.getSelectedItem();

                    Application.cr.connect(port.getFullPath(), Integer.valueOf(baudRate));
                    dismiss();

                    SharedPreferences sp = mContext.getSharedPreferences("conn_pref", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("PortName", port.getName());
                    editor.putInt("BaudRate", Integer.parseInt(baudRate));
                    editor.commit();

                    if (mCloseListener != null)
                        mCloseListener.onClose(true);
                } catch (Exception e) {
                    Application.showError(e.getMessage(), mContext);
                }
            }
        });

        final Button CancelButton = (Button) findViewById(R.id.btnCancel);
        CancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    dismiss();
                    if (mCloseListener != null)
                        mCloseListener.onClose(false);

                    System.exit(0);
                } catch (Exception e) {
                    Application.showError(e.getMessage(), mContext);
                }
            }
        });
    }

}