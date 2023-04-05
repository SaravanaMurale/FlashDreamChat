package com.hermes.chat.audio.openacall.ui;

import static com.hermes.chat.BaseApplication.ACTION_DECLINE;
import static com.hermes.chat.BaseApplication.config;
import static com.hermes.chat.BaseApplication.event;
import static com.hermes.chat.BaseApplication.rtcEngine;
import static com.hermes.chat.BaseApplication.worker;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.hermes.chat.BaseApplication;
import com.hermes.chat.R;
import com.hermes.chat.audio.openacall.model.AGEventHandler;
import com.hermes.chat.audio.openacall.model.ConstantApp;
import com.hermes.chat.models.User;
import com.hermes.chat.pushnotification.Aps;
import com.hermes.chat.pushnotification.Data;
import com.hermes.chat.pushnotification.Notification;
import com.hermes.chat.pushnotification.SendFirebaseNotification;
import com.hermes.chat.services.FirebaseChatService;
import com.hermes.chat.utils.Helper;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;


public class AgoraSingleAudioCallActivity extends AgoraBaseActivity implements AGEventHandler {

    private final static Logger log = LoggerFactory.getLogger(AgoraSingleAudioCallActivity.class);

    private volatile boolean mAudioMuted = false;
    LocalBroadcastManager localBroadcastManager;
    public IntentFilter intentFilter;
    private volatile int mAudioRouting = -1; // Default
    private ImageView userImage1;
    private TextView remoteUser;
    private TextView txt_calling;
    private JSONObject mJSONObject;
    private Helper helper;
    private ImageView switch_speaker_id;
    private User user;
    private User userMe;
    TextView timerTextView;
    long startTime = 0;
    String id;
    boolean isUserJoined = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.agora_activity_audio);
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_DECLINE);
        intentFilter.addAction(BaseApplication.ACTION_TYPE);
        userImage1 = findViewById(R.id.userImage1);
        remoteUser = findViewById(R.id.remoteUser);
        txt_calling = findViewById(R.id.txt_calling);
        timerTextView = findViewById(R.id.timerTextView);
        switch_speaker_id = findViewById(R.id.switch_speaker_id);
        helper = new Helper(this);
        userMe = helper.getLoggedInUser();
        switch_speaker_id.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_speaker));
        switch_speaker_id.setTag(R.drawable.ic_speaker);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    @Override
    protected void initUIandEvent() {
        event().addEventHandler(this);

        Intent i = getIntent();

        String channelName = i.getStringExtra(ConstantApp.ACTION_KEY_CHANNEL_NAME);
        id = i.getStringExtra("id");
        try {
            mJSONObject = new JSONObject(getIntent().getStringExtra("data"));
            txt_calling.setText(Helper.getCallsData(AgoraSingleAudioCallActivity.this).getLblVoiceCall());
            //  helper.setCacheMyUsers(MainActivity.myUsers);
            String url = " ";
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
            url = user.getImage();
            remoteUser.setText(user.getNameInPhone() != null ? user.getNameInPhone() : user.getId());

            if (url != null && !url.isEmpty()) {
                Picasso.get()
                        .load(url)
                        .tag(this)
                        .placeholder(R.drawable.ic_username)
                        .into(userImage1);
            } else {
                userImage1.setBackgroundResource(R.drawable.ic_username);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        worker().joinChannel(channelName, config().mUid);

        optional();

        LinearLayout bottomContainer = (LinearLayout) findViewById(R.id.bottom_container);
        FrameLayout.MarginLayoutParams fmp = (FrameLayout.MarginLayoutParams) bottomContainer.getLayoutParams();
        fmp.bottomMargin = virtualKeyHeight() + 16;
    }

    private Handler mMainHandler;

    private static final int UPDATE_UI_MESSAGE = 0x1024;

    StringBuffer mMessageCache = new StringBuffer();

    private void notifyMessageChanged(String msg) {
        if (mMessageCache.length() > 10000) { // drop messages
            mMessageCache = new StringBuffer(mMessageCache.substring(10000 - 40));
        }

        mMessageCache.append(System.currentTimeMillis()).append(": ").append(msg).append("\n"); // append timestamp for messages

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    return;
                }

                if (mMainHandler == null) {
                    mMainHandler = new Handler(getMainLooper()) {
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            if (isFinishing()) {
                                return;
                            }

                        }
                    };
                }

                mMainHandler.removeMessages(UPDATE_UI_MESSAGE);
                Message envelop = new Message();
                envelop.what = UPDATE_UI_MESSAGE;
                envelop.obj = mMessageCache.toString();
                mMainHandler.sendMessageDelayed(envelop, 1000l);
            }
        });
    }

    private void optional() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
    }

    private void optionalDestroy() {
    }

    public void onSwitchSpeakerClicked(View view) {
        log.info("onSwitchSpeakerClicked " + view + " " + mAudioMuted + " " + mAudioRouting);

        RtcEngine rtcEngine = rtcEngine();

        if (getDrawableId(switch_speaker_id) == R.drawable.ic_speaker) {
            switch_speaker_id.setTag(R.drawable.ic_speaker_off);
            switch_speaker_id.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_speaker_off));
        } else {
            switch_speaker_id.setTag(R.drawable.ic_speaker);
            switch_speaker_id.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_speaker));
        }
        rtcEngine.setEnableSpeakerphone(mAudioRouting != 3);
    }

    @Override
    protected void deInitUIandEvent() {
        optionalDestroy();

        doLeaveChannel();
        event().removeEventHandler(this);
    }

    /**
     * Allows a user to leave a channel.
     * <p>
     * After joining a channel, the user must call the leaveChannel method to end the call before
     * joining another channel. This method returns 0 if the user leaves the channel and releases
     * all resources related to the call. This method call is asynchronous, and the user has not
     * exited the channel when the method call returns. Once the user leaves the channel,
     * the SDK triggers the onLeaveChannel callback.
     * <p>
     * A successful leaveChannel method call triggers the following callbacks:
     * <p>
     * The local client: onLeaveChannel.
     * The remote client: onUserOffline, if the user leaving the channel is in the
     * Communication channel, or is a BROADCASTER in the Live Broadcast profile.
     */
    private void doLeaveChannel() {
        if (!isUserJoined)
            quitCall();
        BaseApplication.isCall = false;
        BaseApplication.getUserRef().child(user.getId()).child("incomingcall").setValue("");
        BaseApplication.getUserRef().child(userMe.getId())
                .child("call_status").setValue(true);
        BaseApplication.getCallsRef().child(userMe.getId()).child(id).child("duration")
                .setValue(timerTextView.getText().toString());
        worker().leaveChannel(config().mChannel);
        timerHandler.removeCallbacks(timerRunnable);
        finish();
    }

    public void onEndCallClicked(View view) {
        log.info("onEndCallClicked " + view);
        doLeaveChannel();
        //  quitCall();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        log.info("onBackPressed");
        doLeaveChannel();
        // quitCall();
    }

    private void quitCall() {

        User toUser = null;
        try {
            if (mJSONObject.getString("toId").equalsIgnoreCase(userMe.getId())) {
                toUser = helper.getCacheMyUsers().get(mJSONObject.getString("fromId"));
            } else {
                toUser = user;
            }

            if (toUser == null) {
                toUser = FirebaseChatService.userHashMap.get(mJSONObject.getString("fromId"));
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

            new SendFirebaseNotification(AgoraSingleAudioCallActivity.this, notificationTask).triggerMessage();
        } else {
            com.hermes.chat.pushnotification.Message notificationTask =
                    new com.hermes.chat.pushnotification.Message(registration_ids,
                            "", data,
                            new Aps(" ", " "));

            new SendFirebaseNotification(AgoraSingleAudioCallActivity.this, notificationTask).triggerMessage();
        }


    }

    public void onVoiceMuteClicked(View view) {
        log.info("onVoiceMuteClicked " + view + " audio_status: " + mAudioMuted);

        RtcEngine rtcEngine = rtcEngine();
        rtcEngine.muteLocalAudioStream(mAudioMuted = !mAudioMuted);

        ImageView iv = (ImageView) view;

        if (mAudioMuted) {
            iv.setImageResource(R.drawable.ic_mic_off);
        } else {
            iv.setImageResource(R.drawable.ic_mic_on);
        }
    }

    @Override
    public void onJoinChannelSuccess(String channel, final int uid, int elapsed) {
        if (BaseApplication.mAudioPlayer != null)
            BaseApplication.mAudioPlayer.stopRingtone();

        if (BaseApplication.vibrator != null)
            BaseApplication.vibrator.cancel();

        String msg = "onJoinChannelSuccess " + channel + " " + (uid & 0xFFFFFFFFL) + " " + elapsed;
        log.debug(msg);

        notifyMessageChanged(msg);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    return;
                }
                rtcEngine().muteLocalVideoStream(true);
                rtcEngine().muteLocalAudioStream(mAudioMuted);
            }
        });
    }

    @Override
    public void onUserOffline(int uid, int reason) {
        String msg = "onUserOffline " + (uid & 0xFFFFFFFFL) + " " + reason;
        log.debug(msg);
        if (BaseApplication.mAudioPlayer != null)
            BaseApplication.mAudioPlayer.stopRingtone();

        if (BaseApplication.vibrator != null)
            BaseApplication.vibrator.cancel();
        BaseApplication.isCall = false;
        finish();
        notifyMessageChanged(msg);

    }

    @Override
    public void onExtraCallback(final int type, final Object... data) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    return;
                }

                doHandleExtraCallback(type, data);
            }
        });
    }

    @Override
    public void onUserJoined(int uid) {
        Log.e("", "");
        isUserJoined = true;
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);
    }

    private void doHandleExtraCallback(int type, Object... data) {
        int peerUid;
        boolean muted;

        switch (type) {
            case AGEventHandler.EVENT_TYPE_ON_USER_AUDIO_MUTED: {
                peerUid = (Integer) data[0];
                muted = (boolean) data[1];

                notifyMessageChanged("mute: " + (peerUid & 0xFFFFFFFFL) + " " + muted);
                break;
            }

            case AGEventHandler.EVENT_TYPE_ON_AUDIO_QUALITY: {
                peerUid = (Integer) data[0];
                int quality = (int) data[1];
                short delay = (short) data[2];
                short lost = (short) data[3];

                notifyMessageChanged("quality: " + (peerUid & 0xFFFFFFFFL) + " " + quality + " " + delay + " " + lost);
                break;
            }

            case AGEventHandler.EVENT_TYPE_ON_SPEAKER_STATS: {
                IRtcEngineEventHandler.AudioVolumeInfo[] infos = (IRtcEngineEventHandler.AudioVolumeInfo[]) data[0];

                if (infos.length == 1 && infos[0].uid == 0) { // local guy, ignore it
                    break;
                }

                StringBuilder volumeCache = new StringBuilder();
                for (IRtcEngineEventHandler.AudioVolumeInfo each : infos) {
                    peerUid = each.uid;
                    int peerVolume = each.volume;

                    if (peerUid == 0) {
                        continue;
                    }

                    volumeCache.append("volume: ").append(peerUid & 0xFFFFFFFFL).append(" ").append(peerVolume).append("\n");
                }

                if (volumeCache.length() > 0) {
                    String volumeMsg = volumeCache.substring(0, volumeCache.length() - 1);
                    notifyMessageChanged(volumeMsg);

                    if ((System.currentTimeMillis() / 1000) % 10 == 0) {
                        log.debug(volumeMsg);
                    }
                }
                break;
            }

            case AGEventHandler.EVENT_TYPE_ON_AGORA_MEDIA_ERROR: {
                int error = (int) data[0];
                String description = (String) data[1];

                notifyMessageChanged(error + " " + description);

                break;
            }

            case AGEventHandler.EVENT_TYPE_ON_AUDIO_ROUTE_CHANGED: {
                notifyHeadsetPlugged((int) data[0]);

                break;
            }
        }
    }

    public void notifyHeadsetPlugged(final int routing) {
        log.info("notifyHeadsetPlugged " + routing);

        mAudioRouting = routing;

        ImageView iv = (ImageView) findViewById(R.id.switch_speaker_id);
        if (mAudioRouting == 3) { // Speakerphone
            iv.setImageResource(R.drawable.ic_speaker);
        } else {
            iv.setImageResource(R.drawable.ic_speaker_off);
        }
    }


    public BroadcastReceiver mCallBroadcastReceiver = new BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(Context context, Intent intent) {
            String result = intent.getStringExtra("type");
            if (!result.isEmpty() && result.equalsIgnoreCase("User Declined")) {
                if (BaseApplication.mAudioPlayer != null)
                    BaseApplication.mAudioPlayer.stopRingtone();

                if (BaseApplication.vibrator != null)
                    BaseApplication.vibrator.cancel();

                finish();
                BaseApplication.isCall = false;
            } else {
                BaseApplication.mAudioPlayer.playProgressTone();
                Toast.makeText(context, "incoming", Toast.LENGTH_SHORT).show();
            }
        }
    };

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

    private int getDrawableId(ImageView iv) {
        return (Integer) iv.getTag();
    }

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
