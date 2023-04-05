package com.hermes.chat.audio.openacall.ui;

import static com.hermes.chat.BaseApplication.ACTION_DECLINE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.hermes.chat.BaseApplication;
import com.hermes.chat.R;
import com.hermes.chat.audio.openacall.model.ConstantApp;
import com.hermes.chat.models.CallLogFireBaseModel;
import com.hermes.chat.models.User;
import com.hermes.chat.pushnotification.Aps;
import com.hermes.chat.pushnotification.Data;
import com.hermes.chat.pushnotification.Notification;
import com.hermes.chat.pushnotification.SendFirebaseNotification;
import com.hermes.chat.utils.Helper;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class AgoraIncomingCallActivity extends AppCompatActivity {

    private JSONObject mJSONObject;
    private Helper helper;
    User user, userMe;
    private TextView txt_calling;
    private boolean callIsVideo, isDecline;
    LocalBroadcastManager localBroadcastManager;
    public IntentFilter intentFilter;
    String id;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_call_screen);

        uiInit();

    }

    private void uiInit() {
        helper = new Helper(this);
        userMe = helper.getLoggedInUser();

        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_DECLINE);
        intentFilter.addAction(BaseApplication.ACTION_TYPE);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        findViewById(R.id.answerButton).setOnClickListener(mClickListener);
        findViewById(R.id.declineButton).setOnClickListener(mClickListener);
        txt_calling = findViewById(R.id.txt_calling);

        try {

            mJSONObject = new JSONObject(getIntent().getStringExtra("data"));
            type = mJSONObject.getString("title");

            isDecline = type.equalsIgnoreCase("audio call") ||
                    type.equalsIgnoreCase("Video Call");

            if (mJSONObject.getString(
                    "title").equalsIgnoreCase("audio call")
                    || mJSONObject.getString("title").equalsIgnoreCase("Group Audio call")) {
                callIsVideo = false;
                txt_calling.setText(
                        Helper.getCallsData(AgoraIncomingCallActivity.this).getLblVoiceCall());
            } else if (mJSONObject.getString("title").equalsIgnoreCase("Video Call") ||
                    mJSONObject.getString("title").equalsIgnoreCase("Group Video call")) {
                callIsVideo = true;
                txt_calling.setText(
                        Helper.getCallsData(AgoraIncomingCallActivity.this).getLblVideoCall());
            }


            if (helper.getCacheMyUsers() != null &&
                    helper.getCacheMyUsers().containsKey(mJSONObject.getString("fromId"))) {
                user = helper.getCacheMyUsers().get(mJSONObject.getString("fromId"));
                TextView remoteUser = findViewById(R.id.remoteUser);
                remoteUser.setText(user.getNameInPhone());
                ImageView userImage2 = findViewById(R.id.userImage2);
                if (user != null && !user.getImage().isEmpty()) {
                    Picasso.get()
                            .load(user.getImage())
                            .tag(this)
                            .placeholder(R.drawable.ic_username)
                            .into(userImage2);
                } else {
                    userImage2.setBackgroundResource(R.drawable.ic_username);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.answerButton:

                    //id = BaseApplication.getCallsRef().child(userMe.getId()).push().getKey();
                    BaseApplication.getUserRef().child(userMe.getId())
                            .child("call_status").setValue(false);
                    if (BaseApplication.mAudioPlayer != null)
                        BaseApplication.mAudioPlayer.stopRingtone();

                    if (BaseApplication.vibrator != null)
                        BaseApplication.vibrator.cancel();

                    Intent callIntent = null;
                    try {
                        if (mJSONObject.getString("title").equalsIgnoreCase("Audio call")) {
                            callLog("IN", "single");
                            callIntent = new Intent(AgoraIncomingCallActivity.this,
                                    AgoraSingleAudioCallActivity.class);
                        } else if (mJSONObject.getString("title").equalsIgnoreCase("Video Call")) {
                            callLog("IN", "single");
                            callIntent = new Intent(AgoraIncomingCallActivity.this,
                                    AgoraSingleVideoCallActivity.class);
                        }

                        callIntent.putExtra(ConstantApp.ACTION_KEY_CHANNEL_NAME,
                                getIntent().getStringExtra(ConstantApp.ACTION_KEY_CHANNEL_NAME));
                        callIntent.putExtra("data",
                                getIntent().getStringExtra("data"));
                        callIntent.putExtra("id",
                                id);
                        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(callIntent);
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;
                case R.id.declineButton:
                    if (isDecline)
                        decline();
                    else {
                        if (BaseApplication.mAudioPlayer != null)
                            BaseApplication.mAudioPlayer.stopRingtone();
                        if (BaseApplication.vibrator != null)
                            BaseApplication.vibrator.cancel();

                        finish();
                    }
                    break;
            }
        }
    };

    private void decline() {
        BaseApplication.isCall = false;
        if (BaseApplication.mAudioPlayer != null)
            BaseApplication.mAudioPlayer.stopRingtone();

        if (BaseApplication.vibrator != null)
            BaseApplication.vibrator.cancel();

        BaseApplication.getUserRef().child(user.getId()).child("incomingcall").setValue("");
        BaseApplication.getUserRef().child(userMe.getId())
                .child("call_status").setValue(true);


        User toUser = null;
        try {
            if (mJSONObject.getString("toId").equalsIgnoreCase(userMe.getId())) {
                toUser = helper.getCacheMyUsers().get(mJSONObject.getString("fromId"));
            } else {
                toUser = user;
            }

            if (mJSONObject.getString("title").equalsIgnoreCase("Audio call") ||
                    mJSONObject.getString("title").equalsIgnoreCase("Group Video call")) {
                callLog("DENIED", "single");
            } else {
                callLog("DENIED", "group");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        String[] registration_ids = new String[]{};
        registration_ids = new String[1];
        registration_ids[0] = toUser.getDeviceToken();

        Notification notification = new Notification("User Declined", type, "", "");
        Data data = new Data("User Declined", type, " ", "", "", "", "");
        if (toUser.getOsType() != null && !toUser.getOsType().isEmpty() && toUser.getOsType().equalsIgnoreCase("iOS")) {
            com.hermes.chat.pushnotification.Message notificationTask =
                    new com.hermes.chat.pushnotification.Message(registration_ids,
                            "", data, notification,
                            new Aps(" ", " "));

            new SendFirebaseNotification(AgoraIncomingCallActivity.this, notificationTask).triggerMessage();
        } else {
            com.hermes.chat.pushnotification.Message notificationTask =
                    new com.hermes.chat.pushnotification.Message(registration_ids,
                            "", data,
                            new Aps(" ", " "));

            new SendFirebaseNotification(AgoraIncomingCallActivity.this, notificationTask).triggerMessage();
        }


        finish();
    }

    private void callLog(String type, String callType) {
        try {
            id = BaseApplication.getCallsRef().child(userMe.getId()).push().getKey();
            CallLogFireBaseModel callLogFireBaseModel = null;
            if (callType.equalsIgnoreCase("single")) {
                ArrayList<String> userList = new ArrayList<>();
                userList.add(user.getId());
                callLogFireBaseModel = new CallLogFireBaseModel(userList,
                        "", "", System.currentTimeMillis(), type,
                        callIsVideo, userMe.getId(), callType);
            } else {
                List<String> list = new LinkedList<String>(Arrays.asList(new
                        GsonBuilder().create().fromJson(mJSONObject.getString("userIds"),
                        String[].class)));

                // list.remove(userMe.getId());

                callLogFireBaseModel = new CallLogFireBaseModel(new ArrayList<String>(list),
                        "", mJSONObject.getString("toId"),
                        System.currentTimeMillis(), type,
                        callIsVideo, userMe.getId(), "group");
            }


            callLogFireBaseModel.setId(id);
            BaseApplication.getCallsRef().child(userMe.getId()).child(id).setValue(callLogFireBaseModel);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        localBroadcastManager.registerReceiver(mCallBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (localBroadcastManager != null) {
            localBroadcastManager.unregisterReceiver(mCallBroadcastReceiver);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (localBroadcastManager != null) {
            localBroadcastManager.unregisterReceiver(mCallBroadcastReceiver);
        }
    }

    public BroadcastReceiver mCallBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String result = intent.getStringExtra("type");
                if (!result.isEmpty() && result.equalsIgnoreCase("User Declined")) {
                    if (mJSONObject.getString("title").equalsIgnoreCase("Audio call") ||
                            mJSONObject.getString("title").equalsIgnoreCase("Video call")) {
                        callLog("DENIED", "single");
                    } else {
                        callLog("DENIED", "group");
                    }
                    if (BaseApplication.mAudioPlayer != null)
                        BaseApplication.mAudioPlayer.stopRingtone();
                    if (BaseApplication.vibrator != null)
                        BaseApplication.vibrator.cancel();

                    finish();
                } else {
                    BaseApplication.mAudioPlayer.playProgressTone();
                    Toast.makeText(context, "incoming", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}
