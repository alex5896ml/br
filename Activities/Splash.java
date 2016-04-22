package com.mobmedianet.trackergps.Project.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.mobmedianet.trackergps.R;


public class Splash extends Activity {
    /*
    Constants
     */
    private static final int splashtime = 2; // splash duration

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Runnable mRunnable = new Delayer();
        Handler mHandler = new Handler();
        mHandler.postDelayed(mRunnable, splashtime * 1000);//Execute after splash time Seconds
        //fontsOverride.setDefaultFont(this, "DEFAULT", "RoboRegular.ttf");

    }

    // Delayer class
    private class Delayer implements Runnable {

        @Override
        public void run() {
            Intent i = new Intent(Splash.this, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        }
    }

}
