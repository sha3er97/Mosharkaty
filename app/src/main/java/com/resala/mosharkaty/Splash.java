package com.resala.mosharkaty;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class Splash extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        int SPLASH_DISPLAY_LENGTH = 1000; // Duration of wait
        new Handler()
                .postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                Splash.this.startActivity(new Intent(Splash.this, LoginActivity.class));
                                Splash.this.finish();
                            }
                        },
                        SPLASH_DISPLAY_LENGTH);
    }
}
