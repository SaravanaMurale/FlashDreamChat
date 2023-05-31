package com.hermes.chat.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.daasuu.ahp.AnimateHorizontalProgressBar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.hermes.chat.BaseApplication;
import com.hermes.chat.R;
import com.hermes.chat.pushnotification.ApiClient;
import com.hermes.chat.pushnotification.ApiInterface;
import com.hermes.chat.pushnotification.Message;
import com.hermes.chat.utils.Helper;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";

    final String[] gcpKey = {""};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);



        AnimateHorizontalProgressBar progressBar = (AnimateHorizontalProgressBar) findViewById(R.id.animate_progress_bar);
        progressBar.setMax(1500);
        progressBar.setProgressWithAnim(1500);

        BaseApplication.getKeyData().addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("Key", "onChildAdded: ");
                List<String> items;
                HashMap<String, String> list = new HashMap<>();
                list = (HashMap<String, String>) dataSnapshot.getValue();
                for (String keys: list.keySet()) {
                    if(keys.equalsIgnoreCase("gcp")){
                        gcpKey[0] = list.get(keys).toString();
                        SharedPreferences prefs = getSharedPreferences(String.valueOf(R.string.app_name), Context.MODE_PRIVATE);
                        prefs.edit().putString("gcp",gcpKey[0]).apply();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        BaseApplication.getDisappearRef().addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("Key", "onChildAdded: ");
                /*HashMap<Object, Object> list = new HashMap<>();
                list = (HashMap<Object, Object>) dataSnapshot.getValue();
                List<String> items = new ArrayList<>();*/
                try{
                    SharedPreferences prefs = getSharedPreferences(String.valueOf(R.string.app_name), Context.MODE_PRIVATE);
                    prefs.edit().putString("appId", dataSnapshot.getValue().toString()).apply();
                } catch (Exception e){

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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


                if(getSharedPreferences(String.valueOf(R.string.app_name), Context.MODE_PRIVATE)!=null){
                    SharedPreferences prefs = getSharedPreferences(String.valueOf(R.string.app_name), Context.MODE_PRIVATE);
                    if(prefs.getString("LoggedIn", "").equalsIgnoreCase("true")){
                        startActivity(new Intent(SplashActivity.this, helper.getLoggedInUser()
                                != null && helper.getLoggedInUser().getIs_new() !=1 ? MainActivity.class : UserNameSignInActivity.class));
                        finish();
                    } else {
                        startActivity(new Intent(SplashActivity.this, UserNameSignInActivity.class));
                        finish();
                    }
                } else {
                    startActivity(new Intent(SplashActivity.this, UserNameSignInActivity.class));
                    finish();
                }


            }
        }, 1500);
    }
}
