package com.app.android.hearthikesview.sample;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;

import com.app.android.hearthikesview.HeartLikeSurfaceView;

public class MainActivity extends AppCompatActivity {

    private HeartLikeSurfaceView mLikeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLikeView = (HeartLikeSurfaceView) findViewById(R.id.likeview);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mLikeView.add(1000);
            }
        },4000L);


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_UP){
            mLikeView.click();
        }
        return super.onTouchEvent(event);
    }

}
