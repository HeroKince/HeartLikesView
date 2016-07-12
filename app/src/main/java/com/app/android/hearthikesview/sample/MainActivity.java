package com.app.android.hearthikesview.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;

import com.app.android.hearthikesview.HeartLikeSurfaceView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private HeartLikeSurfaceView mLikeView;
    private Timer mTimer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLikeView = (HeartLikeSurfaceView) findViewById(R.id.likeview);
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                mLikeView.post(new Runnable() {
                    @Override
                    public void run() {
                        mLikeView.click();
                    }
                });
            }
        }, 500, 200);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_UP){
            mLikeView.click();
        }
        return super.onTouchEvent(event);
    }

}
