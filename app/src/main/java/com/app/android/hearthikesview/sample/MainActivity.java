package com.app.android.hearthikesview.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;

import com.app.android.hearthikesview.HeartLikeSurfaceView;
import com.app.android.hearthikesview.HeartLikeView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private HeartLikeView mLikeView;
    private Timer mTimer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLikeView = (HeartLikeView) findViewById(R.id.likeview);
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                mLikeView.post(new Runnable() {
                    @Override
                    public void run() {
                        mLikeView.showHeartView();
                    }
                });
            }
        }, 100, 100);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_UP){
            mLikeView.showHeartView();
        }
        return super.onTouchEvent(event);
    }

}
