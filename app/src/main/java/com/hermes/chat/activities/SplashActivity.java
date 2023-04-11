package com.hermes.chat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.daasuu.ahp.AnimateHorizontalProgressBar;
import com.hermes.chat.R;
import com.hermes.chat.utils.Helper;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        AnimateHorizontalProgressBar progressBar = (AnimateHorizontalProgressBar) findViewById(R.id.animate_progress_bar);
        progressBar.setMax(1500);
        progressBar.setProgressWithAnim(1500);

        final Helper helper = new Helper(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /*Intent startHomeIntent = new Intent(SplashActivity.this,MainActivity.class);
                Intent startLoginIntent = new Intent(SplashActivity.this,UserNameSignInActivity.class);

                if(helper.getLoggedInUser() != null){

                    startActivity(startHomeIntent);
                    finish();
                }else {
                    if(helper.getLoggedInUser().getIs_new() !=1){
                    startActivity(startLoginIntent);
                    }
                }*/
                startActivity(new Intent(SplashActivity.this, helper.getLoggedInUser()
                        != null && helper.getLoggedInUser().getIs_new() !=1 ? MainActivity.class : UserNameSignInActivity.class));
                finish();
            }
        }, 1500);
    }
}
