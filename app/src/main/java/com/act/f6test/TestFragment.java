package com.act.f6test;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
public class TestFragment extends Fragment {

    View mView;
    Spinner mSpinPlans;
    TextView mTvOutput;

    public TestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_test, null);
        mSpinPlans = (Spinner) mView.findViewById(R.id.spinPlans);
        mTvOutput = (TextView) mView.findViewById(R.id.txtOutput);

        Vector<TestPlanItem> plans = new Vector<TestPlanItem>();
        plans.add(new TestPlanItem(TestPlanKind.TPK_DISP_READ_EJECT, "重复发卡、读卡、弹卡"));
        plans.add(new TestPlanItem(TestPlanKind.TPK_DISP_READ_RETAIN, "重复发卡、读卡、回收卡"));

        ArrayAdapter<TestPlanItem> spinAdapter = new ArrayAdapter<TestPlanItem>
                (mView.getContext(), R.layout.support_simple_spinner_dropdown_item, plans);
        mSpinPlans.setAdapter(spinAdapter);

        final Button btnStartTest = (Button) mView.findViewById(R.id.btnStartTest);
        btnStartTest.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    //if(comm.LINK == 4){Application.showError("未建立连接。", mView.getContext());return;}
                    if (Application.testThread == null) {
                        TestPlanItem plan = (TestPlanItem) mSpinPlans.getSelectedItem();
                        Application.testThread = new TestThread(mView.getContext(), mTvOutput, plan.getKind());

                        Log.i("自动化测试", "线程开始");

                        Application.testThread.start();
                        btnStartTest.setEnabled(false);
                    }
                    else {
                        Application.showError("正在测试中。", mView.getContext());
                    }
                } catch (Exception e) {
                    Application.showError(e.getMessage(), mView.getContext());
                }
            }
        });

        final Button btnStopTest = (Button) mView.findViewById(R.id.btnStopTest);
        btnStopTest.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    if (Application.testThread == null) {
                        btnStartTest.setEnabled(true);
                        return;
                    }

                    Application.testThread.interrupt();
                    Application.testThread = null;
                    btnStartTest.setEnabled(true);
                } catch (Exception e) {
                    Application.showError(e.getMessage(), mView.getContext());
                }
            }
        });

        return mView;
    }

}
