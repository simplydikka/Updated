package com.solodroid.androidebookappdemo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import com.solodroid.androidebookappdemo.R;

public class ActivitySplash extends AppCompatActivity {

    private ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        new CountDownTimer(3000, 1000) {

            @Override
            public void onFinish() {
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
                finish();

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onTick(long millisUntilFinished) {

            }
        }.start();
    }

}