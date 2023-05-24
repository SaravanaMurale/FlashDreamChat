package com.hermes.chat.audio.openacall.ui;

import static com.hermes.chat.BaseApplication.ACTION_DECLINE;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.hermes.chat.BaseApplication;
import com.hermes.chat.R;
import com.hermes.chat.activities.MainActivity;
import com.hermes.chat.audio.openacall.model.ConstantApp;
import com.hermes.chat.models.User;
import com.hermes.chat.pushnotification.Aps;
import com.hermes.chat.pushnotification.Data;
import com.hermes.chat.pushnotification.Notification;
import com.hermes.chat.pushnotification.SendFirebaseNotification;
import com.hermes.chat.services.FirebaseChatService;
import com.hermes.chat.utils.Helper;

import org.json.JSONException;
import org.json.JSONObject;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.RtcEngineConfig;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;

public class AgoraSingleVideoCallActivity extends AppCompatActivity {
    private RtcEngine mRtcEngine;
    private JSONObject mJSONObject;
    private Helper helper;
    User user, userMe;
    LocalBroadcastManager localBroadcastManager;
    public IntentFilter intentFilter;
    private TextView remoteUser;
    // Permissions
    private static final int PERMISSION_REQ_ID = 22;
    private static final String[] REQUESTED_PERMISSIONS = {Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA};

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    TextView timerTextView;
    long startTime = 0;
    String id;
    boolean isUserJoined = false;

    // Handle SDK Events
    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // set first remote user to the main bg video container
                    setupRemoteVideoStream(uid);
                }
            });
        }

        // remote user has left channel
        @Override
        public void onUserOffline(int uid, int reason) { // Tutorial Step 7
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onRemoteUserLeft();
                }
            });
        }

        // remote user has toggled their video
        @Override
        public void onUserMuteVideo(final int uid, final boolean toggle) { // Tutorial Step 10
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onRemoteUserVideoToggle(uid, toggle);
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.agora_single_call_activity);
        helper = new Helper(this);
        userMe = helper.getLoggedInUser();
        remoteUser = findViewById(R.id.remoteUser);
        timerTextView = findViewById(R.id.timerTextView);
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_DECLINE);
        intentFilter.addAction(BaseApplication.ACTION_TYPE);
        if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID)) {
            initAgoraEngine();
        }

        Intent i = getIntent();
        String channelName = i.getStringExtra(ConstantApp.ACTION_KEY_CHANNEL_NAME);
        id = i.getStringExtra("id");
        try {
            mJSONObject = new JSONObject(i.getStringExtra("data"));

            if (userMe.getId().equalsIgnoreCase(mJSONObject.getString("toId"))) {
                if (helper.getCacheMyUsers() != null &&
                        helper.getCacheMyUsers().containsKey(mJSONObject.getString("fromId"))) {
                    user = helper.getCacheMyUsers().get(mJSONObject.getString("fromId"));
                }
                if (user == null)
                    user = FirebaseChatService.userHashMap.get(mJSONObject.get("fromId"));
            } else {
                if (helper.getCacheMyUsers() != null &&
                        helper.getCacheMyUsers().containsKey(mJSONObject.getString("toId"))) {
                    user = helper.getCacheMyUsers().get(mJSONObject.getString("toId"));
                }
                if (user == null)
                    user = FirebaseChatService.userHashMap.get(mJSONObject.get("toId"));
            }
            if (user != null)
                remoteUser.setText(user.getNameInPhone() != null ? user.getNameInPhone() : user.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mRtcEngine.joinChannel(null, channelName, "Extra Optional Data", 0);
        setupLocalVideoFeed();
        findViewById(R.id.audioBtn).setVisibility(View.VISIBLE);
        findViewById(R.id.leaveBtn).setVisibility(View.VISIBLE);
        findViewById(R.id.videoBtn).setVisibility(View.VISIBLE);

        findViewById(R.id.switchVideo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRtcEngine.switchCamera();
            }
        });
    }

    private void initAgoraEngine() {
       /* try {
            mRtcEngine = RtcEngine.create(getBaseContext(), getString(R.string.private_app_id), mRtcEventHandler);
        } catch (Exception e) {
            Log.e(LOG_TAG, Log.getStackTraceString(e));

            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }*/

        try {
            RtcEngineConfig config = new RtcEngineConfig();
            SharedPreferences prefs = getSharedPreferences(String.valueOf(R.string.app_name), Context.MODE_PRIVATE);
            config.mAppId = prefs.getString("appId", "");
            config.mContext = this;
            config.mEventHandler = mRtcEventHandler;
            config.mAreaCode = 0xFFFFFFFF;
            mRtcEngine = RtcEngine.create(config);
        } catch (Exception e) {
            Log.e("TAG", Log.getStackTraceString(e));
            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
        setupSession();
    }

    private void setupSession() {
        mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_COMMUNICATION);

        mRtcEngine.enableVideo();

        mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(VideoEncoderConfiguration.VD_640x480, VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_30,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT));
    }

    private void setupLocalVideoFeed() {

        // setup the container for the local user
        FrameLayout videoContainer = findViewById(R.id.floating_video_container);
        SurfaceView videoSurface = RtcEngine.CreateRendererView(getBaseContext());
        videoSurface.setZOrderMediaOverlay(true);
        videoContainer.addView(videoSurface);
        mRtcEngine.setupLocalVideo(new VideoCanvas(videoSurface, VideoCanvas.RENDER_MODE_FILL, 0));
    }

    private void setupRemoteVideoStream(int uid) {
        isUserJoined = true;
        // setup ui element for the remote stream
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);
        FrameLayout videoContainer = findViewById(R.id.bg_video_container);
        // ignore any new streams that join the session
        if (videoContainer.getChildCount() >= 1) {
            return;
        }

        SurfaceView videoSurface = RtcEngine.CreateRendererView(getBaseContext());
        videoContainer.addView(videoSurface);
        mRtcEngine.setupRemoteVideo(new VideoCanvas(videoSurface, VideoCanvas.RENDER_MODE_FILL, uid));
        mRtcEngine.setRemoteSubscribeFallbackOption(Constants.STREAM_FALLBACK_OPTION_AUDIO_ONLY);

    }

    public void onAudioMuteClicked(View view) {
        ImageView btn = (ImageView) view;
        if (btn.isSelected()) {
            btn.setSelected(false);
            btn.setImageResource(R.drawable.ic_mic_off);
        } else {
            btn.setSelected(true);
            btn.setImageResource(R.drawable.ic_mic_on);
        }

        mRtcEngine.muteLocalAudioStream(btn.isSelected());
    }

    public void onVideoMuteClicked(View view) {
        ImageView btn = (ImageView) view;
        if (btn.isSelected()) {
            btn.setSelected(false);
            btn.setImageResource(R.drawable.ic_video_off);
        } else {
            btn.setSelected(true);
            btn.setImageResource(R.drawable.ic_video_on);
        }

        mRtcEngine.muteLocalVideoStream(btn.isSelected());

        FrameLayout container = findViewById(R.id.floating_video_container);
        container.setVisibility(btn.isSelected() ? View.GONE : View.VISIBLE);
        SurfaceView videoSurface = (SurfaceView) container.getChildAt(0);
        videoSurface.setZOrderMediaOverlay(!btn.isSelected());
        videoSurface.setVisibility(btn.isSelected() ? View.GONE : View.VISIBLE);
    }

    // join the channel when user clicks UI button
    public void onjoinChannelClicked(View view) {

    }

    public void onLeaveChannelClicked(View view) {
        //  leaveChannel();
        removeVideo(R.id.floating_video_container);
        removeVideo(R.id.bg_video_container);
        findViewById(R.id.audioBtn).setVisibility(View.GONE);
        findViewById(R.id.leaveBtn).setVisibility(View.GONE);
        findViewById(R.id.videoBtn).setVisibility(View.GONE);
        onRemoteUserLeft();
    }

    private void leaveChannel() {
        User toUser = null;
        try {
            if (mJSONObject.getString("toId").equalsIgnoreCase(userMe.getId())) {
                toUser = helper.getCacheMyUsers().get(mJSONObject.getString("fromId"));
            } else {
                toUser = user;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String[] registration_ids = new String[]{};
        registration_ids = new String[1];
        registration_ids[0] = toUser.getDeviceToken();

        Notification notification = new Notification("User Declined", "", "", "");
        Data data = new Data("User Declined", "", " ", "", "", "", "");
        if (toUser.getOsType() != null && !toUser.getOsType().isEmpty() && toUser.getOsType().equalsIgnoreCase("iOS")) {
            com.hermes.chat.pushnotification.Message notificationTask =
                    new com.hermes.chat.pushnotification.Message(registration_ids,
                            "", data, notification,
                            new Aps(" ", " "));

            new SendFirebaseNotification(AgoraSingleVideoCallActivity.this, notificationTask).triggerMessage();
        } else {
            com.hermes.chat.pushnotification.Message notificationTask =
                    new com.hermes.chat.pushnotification.Message(registration_ids,
                            "", data,
                            new Aps(" ", " "));

            new SendFirebaseNotification(AgoraSingleVideoCallActivity.this, notificationTask).triggerMessage();
        }
    }

    private void removeVideo(int containerID) {
        FrameLayout videoContainer = findViewById(containerID);
        videoContainer.removeAllViews();
    }

    private void onRemoteUserVideoToggle(int uid, boolean toggle) {
        FrameLayout videoContainer = findViewById(R.id.bg_video_container);

        SurfaceView videoSurface = (SurfaceView) videoContainer.getChildAt(0);
        videoSurface.setVisibility(toggle ? View.GONE : View.VISIBLE);

        // add an icon to let the other user know remote video has been disabled
        if (toggle) {
            ImageView noCamera = new ImageView(this);
            noCamera.setImageResource(R.drawable.video_disabled);
            videoContainer.addView(noCamera);
        } else {
            ImageView noCamera = (ImageView) videoContainer.getChildAt(1);
            if (noCamera != null) {
                videoContainer.removeView(noCamera);
            }
        }
    }

    private void onRemoteUserLeft() {
        if (!isUserJoined)
            leaveChannel();
        BaseApplication.isCall = false;
        removeVideo(R.id.bg_video_container);
        if (BaseApplication.mAudioPlayer != null)
            BaseApplication.mAudioPlayer.stopRingtone();

        if (BaseApplication.vibrator != null)
            BaseApplication.vibrator.cancel();

        BaseApplication.getUserRef().child(user.getId()).child("incomingcall").setValue("");
        BaseApplication.getUserRef().child(userMe.getId())
                .child("call_status").setValue(true);
        BaseApplication.getCallsRef().child(userMe.getId()).child(id).child("duration")
                .setValue(timerTextView.getText().toString());
        timerHandler.removeCallbacks(timerRunnable);
        mRtcEngine.leaveChannel();
        finish();

    }


    public boolean checkSelfPermission(String permission, int requestCode) {
        Log.i(LOG_TAG, "checkSelfPermission " + permission + " " + requestCode);
        if (ContextCompat.checkSelfPermission(this,
                permission)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    REQUESTED_PERMISSIONS,
                    requestCode);
            return false;
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(LOG_TAG, "onRequestPermissionsResult " + grantResults[0] + " " + requestCode);

        switch (requestCode) {
            case PERMISSION_REQ_ID: {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                    Log.i(LOG_TAG, "Need permissions " + Manifest.permission.RECORD_AUDIO + "/" + Manifest.permission.CAMERA);
                    break;
                }

                initAgoraEngine();
                break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // leaveChannel();
        RtcEngine.destroy();
        mRtcEngine = null;
        if (localBroadcastManager != null) {
            localBroadcastManager.unregisterReceiver(mCallBroadcastReceiver);
        }
    }

    public final void showLongToast(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
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

    public BroadcastReceiver mCallBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String result = intent.getStringExtra("type");
            if (!result.isEmpty() && result.equalsIgnoreCase("User Declined")) {
                if (BaseApplication.mAudioPlayer != null)
                    BaseApplication.mAudioPlayer.stopRingtone();

                if (BaseApplication.vibrator != null)
                    BaseApplication.vibrator.cancel();

                BaseApplication.isCall = false;
                finish();
            } else {
                BaseApplication.mAudioPlayer.playProgressTone();
                Toast.makeText(context, "incoming", Toast.LENGTH_SHORT).show();
            }
        }
    };

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            String min = minutes <= 9 ? "0" + minutes : String.valueOf(minutes);
            timerTextView.setText(String.format("%s:%02d", min, seconds));
            timerHandler.postDelayed(this, 500);
        }
    };
}
