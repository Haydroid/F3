package com.act.f6test;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Vector;


/**
 * A simple {@link Fragment} subclass.
 */
public class CpuFragment extends Fragment {

    View mView;
    Spinner mSpinCmds;
    TextView mTvATR;
    TextView mTvResp;

    public CpuFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_cpu, null);
        mSpinCmds = (Spinner) mView.findViewById(R.id.spinApduCmds);
        mTvATR = (TextView) mView.findViewById(R.id.txtATR);
        mTvResp = (TextView) mView.findViewById(R.id.txtApduResp);
        mTvATR.setText("");

        Vector<APDUCmdItem> vec = Application.getPredCmdItems();
        ArrayAdapter<APDUCmdItem> adapter = new ArrayAdapter<APDUCmdItem>
                (mView.getContext(), R.layout.support_simple_spinner_dropdown_item, vec);
        mSpinCmds.setAdapter(adapter);

        /*final Button btnTouchOn = (Button) mView.findViewById(R.id.btnTouchOn);
        btnTouchOn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    Application.cr.btnTouchOn();
                } catch (Exception e) {
                    Application.showError(e.getMessage(), mView.getContext());
                }
            }
        });

        final Button btnTouchOff = (Button) mView.findViewById(R.id.btnTouchOff);
        btnTouchOff.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    Application.cr.btnTouchOff();
                } catch (Exception e) {
                    Application.showError(e.getMessage(), mView.getContext());
                }
            }
        });*/


        final Button btnPowerOn = (Button) mView.findViewById(R.id.btnPowerOn);
        btnPowerOn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    byte[] atr = Application.cr.chipPowerOn();
                    String text = Utils.bytesToHexString(atr);
                    mTvATR.setText(text);
                } catch (Exception e) {
                    Application.showError(e.getMessage(), mView.getContext());
                }
            }
        });

        final Button btnPowerOff = (Button) mView.findViewById(R.id.btnPowerOff);
        btnPowerOff.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    Application.cr.chipPowerOff();
                    mTvResp.setText("释放成功。");
                } catch (Exception e) {
                    Application.showError(e.getMessage(), mView.getContext());
                }
            }
        });

        final Button btnTransmit = (Button) mView.findViewById(R.id.btnTransmit);
        btnTransmit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    APDUCmdItem cmd = (APDUCmdItem) mSpinCmds.getSelectedItem();
                    byte[] rdata = Application.cr.chipIo(cmd.getCmdData());
                    String text = Utils.bytesToHexString(rdata);
                    mTvResp.setText(text);
                } catch (Exception e) {
                    Application.showError(e.getMessage(), mView.getContext());
                }
            }
        });

        return mView;
    }


}
