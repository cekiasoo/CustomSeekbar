package com.ce.customseekbar;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private CustomSeekBar mHorCustomSeekBar;
    private CustomSeekBar mCustomSeekBar;

    private int mHorProgress = 0;
    private int mProgress = 0;

    private int mSecondaryProgress = 0;

    private final static int WHAT_UPDATE_HOR_PROGRESS = 1000;
    private final static int WHAT_UPDATE_VER_PROGRESS = WHAT_UPDATE_HOR_PROGRESS + 1;

    private android.os.Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int _What = msg.what;
            switch (_What) {
                case WHAT_UPDATE_HOR_PROGRESS:
                    if (mHorProgress < 100) {
                        if (mSecondaryProgress < 100) {
                            mSecondaryProgress += 2;
                            mHorCustomSeekBar.setSecondaryProgress(mSecondaryProgress);
                        }
                        mHorCustomSeekBar.setProgress(++mHorProgress);
                        mHandler.sendEmptyMessageDelayed(WHAT_UPDATE_HOR_PROGRESS, 50);
                    }
                    break;
                case WHAT_UPDATE_VER_PROGRESS:
                    if (mProgress < 100) {
                        mCustomSeekBar.setProgress(++mProgress);
                        mHandler.sendEmptyMessageDelayed(WHAT_UPDATE_VER_PROGRESS, 50);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHorCustomSeekBar =(CustomSeekBar) findViewById(R.id.custom_seek_bar_hor);
        mCustomSeekBar =(CustomSeekBar) findViewById(R.id.custom_seek_bar);
        mHandler.sendEmptyMessageDelayed(WHAT_UPDATE_HOR_PROGRESS, 50);
        mHandler.sendEmptyMessageDelayed(WHAT_UPDATE_VER_PROGRESS, 50);
    }

    @Override
    protected void onDestroy() {
        mHandler.removeMessages(WHAT_UPDATE_HOR_PROGRESS);
        mHandler.removeMessages(WHAT_UPDATE_VER_PROGRESS);
        super.onDestroy();
    }
}
