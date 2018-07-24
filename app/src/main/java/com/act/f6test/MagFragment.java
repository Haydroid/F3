package com.act.f6test;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.act.f3api.F3CardPosition;


/**
 * A simple {@link Fragment} subclass.
 */
public class MagFragment extends Fragment {

    View mView;
    TextView mTvData1;
    TextView mTvData2;
    TextView mTvData3;

    public MagFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_mag, null);
        mTvData1 = (TextView)mView.findViewById(R.id.txtData1);
        mTvData2 = (TextView) mView.findViewById(R.id.txtData2);
        mTvData3 = (TextView) mView.findViewById(R.id.txtData3);


        final Button btnRead = (Button) mView.findViewById(R.id.btnRead);
        btnRead.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //showTracks(Application.cr.readTracks());
            }
        });

        final Button btnReread = (Button) mView.findViewById(R.id.btnReread);
        btnReread.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    F3CardPosition pos = Application.cr.getCardPosition();
                    if (pos.bLaneStatus == 0x30) {
                        Application.showInfo("机内无卡。", "信息", mView.getContext());
                        return;
                    }
                } catch (Exception e) {
                    Application.showError(e.getMessage(), mView.getContext());
                }
                /*Application.cr.writeTracks((byte)0x31,mTvData1.getText().toString().getBytes());
                Application.cr.writeTracks((byte)0x32,mTvData2.getText().toString().getBytes());
                Application.cr.writeTracks((byte)0x33,mTvData3.getText().toString().getBytes());
                showTracks(Application.cr.readTracks());
                */
            }
        });

        return mView;
    }

    /*private void showTracks(F3TrackData[] tracks) {
        TextView[] textViews = {mTvData1, mTvData2, mTvData3};

        for (int i = 0; i < tracks.length; i++) {
            textViews[i].setText(tracks[i].getData());
            textViews[i].setTextColor(0xff0000ff);
        }
    }*/

    private void clearTextViews() {
        TextView[] textViews = {mTvData1, mTvData2, mTvData3};
        for (int i = 0; i < textViews.length; i++)
            textViews[i].setText("");
    }
}
