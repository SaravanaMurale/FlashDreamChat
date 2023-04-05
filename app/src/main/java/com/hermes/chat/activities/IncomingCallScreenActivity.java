package com.hermes.chat.activities;

import com.hermes.chat.models.Contact;
import com.hermes.chat.models.Group;
import com.hermes.chat.models.Status;
import com.hermes.chat.models.User;

import java.util.ArrayList;

public class IncomingCallScreenActivity extends BaseActivity {
    @Override
    void myUsersResult(ArrayList<User> myUsers) {

    }

    @Override
    void myContactsResult(ArrayList<Contact> myContacts) {

    }

    @Override
    void userAdded(User valueUser) {

    }

    @Override
    void groupAdded(Group valueGroup) {

    }

    @Override
    void userUpdated(User valueUser) {

    }

    @Override
    void groupUpdated(Group valueGroup) {

    }

    @Override
    void statusAdded(Status status) {

    }

    @Override
    void statusUpdated(Status status) {

    }

    @Override
    void onSinchConnected() {

    }

    @Override
    void onSinchDisconnected() {

    }/* {
    static final String TAG = IncomingCallScreenActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSION_CALL = 951;
    private static final String CHANNEL_ID_USER_MISSCALL = "my_channel_04";

    private String[] recordPermissions = {Manifest.permission.VIBRATE, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private String mCallId;
    private AudioPlayer mAudioPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_call_screen);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

       *//* Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);*//*

        mAudioPlayer = new AudioPlayer(this);
        mAudioPlayer.playRingtone();

        Intent intent = getIntent();
        mCallId = intent.getStringExtra(SinchService.CALL_ID);
        mCallId = callId;
        findViewById(R.id.answerButton).setOnClickListener(mClickListener);
        findViewById(R.id.declineButton).setOnClickListener(mClickListener);

        if (fromNotification) {
            if (SinchService.mAudioPlayer != null) {
                SinchService.mAudioPlayer.stopRingtone();
            }
        }

    }

    @Override
    void onSinchConnected() {
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.addCallListener(new SinchCallListener());

            HashMap<String, User> myUsers = helper.getCacheMyUsers();
            if (myUsers != null && myUsers.containsKey(call.getRemoteUserId())) {
                user = myUsers.get(call.getRemoteUserId());
            }

            TextView remoteUser = (TextView) findViewById(R.id.remoteUser);
            ImageView userImage1 = findViewById(R.id.userImage1);
            ImageView userImage2 = findViewById(R.id.userImage2);
            remoteUser.setText(user != null ? user.getNameToDisplay() : call.getRemoteUserId());
            if (user != null && !user.getImage().isEmpty()) {
//                Glide.with(this).load(user.getImage()).apply(new RequestOptions().placeholder(R.drawable.ic_placeholder)).into(userImage1);
//                Glide.with(this).load(user.getImage()).apply(RequestOptions.circleCropTransform().placeholder(R.drawable.ic_placeholder)).into(userImage2);
                Picasso.get()
                        .load(user.getImage())
                        .tag(this)
                        .placeholder(R.drawable.ic_avatar)
                        .into(userImage2);
            } else {
                userImage2.setBackgroundResource(R.drawable.ic_avatar);
            }
            TextView callingType = findViewById(R.id.txt_calling);
            callingType.setText((call.getDetails().isVideoOffered() ? " " +
                    Helper.getCallsData(IncomingCallScreenActivity.this).getLblVideoCall()
                    : " ") + Helper.getCallsData(IncomingCallScreenActivity.this).getLblVoiceCall());
        } else {
            Log.e(TAG, "Started with invalid callId, aborting");
            finish();
        }
    }

    private void answerClicked() {
        mAudioPlayer.stopRingtone();
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            try {
                call.answer();
                startActivity(CallScreenActivity.newIntent(this, user, mCallId, "IN"));
                finish();
            } catch (Exception e) {
                Log.e("CHECK", e.getMessage());
                //ActivityCompat.requestPermissions(this, new String[]{e.getRequiredPermission()}, 0);
            }
        } else {
            finish();
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            answerClicked();
        } else {
            Toast.makeText(this, "This application needs permission to use your microphone to function properly.", Toast.LENGTH_LONG).show();
        }
    }

    private void declineClicked() {
        mAudioPlayer.stopRingtone();
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.hangup();
        }
        finish();
    }

    private boolean recordPermissionsAvailable() {
        boolean available = true;
        for (String permission : recordPermissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                available = false;
                break;
            }
        }
        return available;
    }

    private class SinchCallListener implements CallListener {

        @Override
        public void onCallEnded(Call call) {
            CallEndCause cause = call.getDetails().getEndCause();
            Log.d(TAG, "Call ended, cause: " + cause.toString());
            if (cause.toString().equals("CANCELED") || cause.toString().equals("DENIED")) {
                LogCall logCall = null;
                if (user == null) {
                    user = new User(call.getRemoteUserId(), call.getRemoteUserId(), getString(R.string.app_name), "");
                }

                CallLogFireBaseModel callLogFireBaseModel = new CallLogFireBaseModel(user.getId(),
                        "", "", System.currentTimeMillis(), cause.toString(),
                        call.getDetails().isVideoOffered(), userMe.getId());
                String id = BaseApplication.getCallsRef().child(userMe.getId()).push().getKey();
                callLogFireBaseModel.setId(id);
                BaseApplication.getCallsRef().child(userMe.getId()).child(id).setValue(callLogFireBaseModel);

                rChatDb.beginTransaction();
                logCall = new LogCall(user, System.currentTimeMillis(),
                        0, call.getDetails().isVideoOffered(), cause.toString(), userMe.getId(),
                        user.getId(), callLogFireBaseModel.getId(),
                        callLogFireBaseModel.getDuration());

                rChatDb.copyToRealm(logCall);
                rChatDb.commitTransaction();

                if (cause.toString().equals("CANCELED")) {
                    notifyMisscall(logCall);
                }
            }
            mAudioPlayer.stopRingtone();
            finish();
        }

        @Override
        public void onCallEstablished(Call call) {
            Log.d(TAG, "Call established");
        }

        @Override
        public void onCallProgressing(Call call) {
            Log.d(TAG, "Call progressing");
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
            // Send a push through your push provider here, e.g. GCM
        }

    }

    private void notifyMisscall(LogCall logCall) {
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 56, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID_USER_MISSCALL, "Dreams Chat misscall notification", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
            notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID_USER_MISSCALL);
        } else {
            notificationBuilder = new NotificationCompat.Builder(this);
        }

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notificationBuilder.setSmallIcon(R.drawable.ic_logo_)
                .setContentTitle(logCall.getUser().getNameToDisplay())
                .setContentText("Gave you a miss call")
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        int msgId = 0;
        try {
            String id = logCall.getUser().getId().replace("dreamschat-","");
         //   msgId = Integer.parseInt(logCall.getUser().getId());
            msgId = Integer.parseInt(id);
        } catch (NumberFormatException ex) {
            msgId = Integer.parseInt(logCall.getUser().getId().substring(logCall.getUser().getId().length() / 2));
        }
        notificationManager.notify(msgId, notificationBuilder.build());
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.answerButton:
                    if (recordPermissionsAvailable()) {
                        answerClicked();
                    } else {
                        ActivityCompat.requestPermissions(IncomingCallScreenActivity.this, recordPermissions, REQUEST_PERMISSION_CALL);
                    }
                    break;
                case R.id.declineButton:
                    declineClicked();
                    break;
            }
        }
    };

    @Override
    void onSinchDisconnected() {

    }

    @Override
    void myUsersResult(ArrayList<User> myUsers) {

    }

    @Override
    void myContactsResult(ArrayList<Contact> myContacts) {

    }

    @Override
    void userAdded(User valueUser) {

    }

    @Override
    void groupAdded(Group valueGroup) {

    }

    @Override
    void userUpdated(User valueUser) {

    }

    @Override
    void groupUpdated(Group valueGroup) {

    }

    @Override
    void statusAdded(Status status) {

    }

    @Override
    void statusUpdated(Status status) {

    }*/
}
