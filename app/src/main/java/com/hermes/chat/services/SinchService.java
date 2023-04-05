package com.hermes.chat.services;

import com.hermes.chat.R;
/*import com.sinch.android.rtc.AudioController;
import com.sinch.android.rtc.Beta;
import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchClientListener;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallListener;
import com.sinch.android.rtc.video.VideoController;*/

public class SinchService /*extends Service */{/*
//    private static final String APP_KEY = "";
//    private static final String APP_SECRET = "";
//    private static final String ENVIRONMENT = "sandbox.sinch.com"; //clientapi.sinch.com

    //private static final String APP_KEY = "f06ae4f2-4980-40aa-89ca-9b98d80d70c4"; //Old
    //private static final String APP_SECRET = "r30M/QPOx0+2f+AmlwyuSQ=="; //Old
    public static final String ENVIRONMENT = "clientapi.sinch.com";


    public static final String APP_KEY = "6efe86f5-d1f8-43c1-9c53-dd71d12c6555"; //New
    public static final String APP_SECRET = "vyGfjPc8ukSZ8GCzwXarjg=="; //New
//    public static final String APP_KEY = "6e88b06c-2204-42b8-8c5c-194ee338c5a7"; //Tamil
//    public static final String APP_SECRET = "D64bj4bZVUKUGmvYqeu04Q=="; //Tamil
//    public static final String ENVIRONMENT = "sandbox.sinch.com";//Tamil

    public static final String CALL_ID = "CALL_ID";
    static final String TAG = SinchService.class.getSimpleName();

    private SinchServiceInterface mSinchServiceInterface = new SinchServiceInterface();
    private SinchClient mSinchClient;
    private String mUserId;

    private StartFailedListener mListener;
    private Helper helper;
    private User userMe;
    private BroadcastReceiver broadcastReceiver;
    public static String callId = "";
    public static boolean fromNotification = false;
    public static AudioPlayer mAudioPlayer;
    User user = null;

    @Override
    public void onCreate() {
        super.onCreate();
        helper = new Helper(this);
        userMe = helper.getLoggedInUser();
        if (User.validate(userMe)) {
            start(userMe.getId());
        }
        callReceiver();
        registerReceiver(broadcastReceiver,
                new IntentFilter("com.from.notification"));
    }

    @Override
    public void onDestroy() {
        if (mSinchClient != null && mSinchClient.isStarted()) {
            mSinchClient.terminate();
        }
        super.onDestroy();
    }

    private void start(String userName) {
        userName = userName.replace("+", "dreamchat-");
        if (mSinchClient == null) {
            mUserId = userName;
            mSinchClient = Sinch.getSinchClientBuilder().context(getApplicationContext()).userId(userName)
//            mSinchClient = Sinch.getSinchClientBuilder().context(getApplicationContext()).userId("dreams-919566875206")
                    .applicationKey(APP_KEY)
                    .applicationSecret(APP_SECRET)
                    .environmentHost(ENVIRONMENT).build();

            mSinchClient.setSupportCalling(true);
//            mSinchClient.setSupportManagedPush(true);
            mSinchClient.setSupportActiveConnectionInBackground(true);
            mSinchClient.startListeningOnActiveConnection();

            mSinchClient.addSinchClientListener(new MySinchClientListener());
            // Permission READ_PHONE_STATE is needed to respect native calls.
            mSinchClient.getCallClient().setRespectNativeCalls(false);
            mSinchClient.getCallClient().addCallClientListener(new SinchCallClientListener());
            mSinchClient.setSupportManagedPush(true);
            mSinchClient.start();
            Beta.createManagedPush(mSinchClient);
        }
    }

    private void stop() {
        if (mSinchClient != null) {
            mSinchClient.terminate();
            mSinchClient = null;
        }
    }

    private boolean isStarted() {
        return (mSinchClient != null && mSinchClient.isStarted());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mSinchServiceInterface;
    }


    public class SinchServiceInterface extends Binder {

        public Call callUserVideo(String userId) {
            if (mSinchClient == null) {
                return null;
            }
            return mSinchClient.getCallClient().callUserVideo(userId);
        }

        public Call callUser(String userId) {
            if (mSinchClient == null) {
                return null;
            }
            return mSinchClient.getCallClient().callUser(userId);
        }

        public VideoController getVideoController() {
            if (!isStarted()) {
                return null;
            }
            return mSinchClient.getVideoController();
        }

        public AudioController getAudioController() {
            if (!isStarted()) {
                return null;
            }
            return mSinchClient.getAudioController();
        }

        public boolean isStarted() {
            return SinchService.this.isStarted();
        }

        public void startClient(String userName) {
            start(userName);
        }

        public void stopClient() {
            stop();
        }

        public void setStartListener(StartFailedListener listener) {
            mListener = listener;
        }

        public Call getCall(String callId) {
            return mSinchClient.getCallClient().getCall(callId);
        }
    }

    public interface StartFailedListener {
        void onStartFailed(SinchError error);

        void onStarted();
    }

    *//* @Override
     public int onStartCommand(Intent intent, int flags, int startId) {

         Notification notification = new NotificationCompat.Builder(this)
                 .setSmallIcon(R.drawable.ic_logo_)
                 .setContentText(getString(R.string.app_name))
                 .build();
         startForeground(100, notification);


         return START_STICKY;
     }
 *//*
    private class MySinchClientListener implements SinchClientListener {

        @Override
        public void onClientFailed(SinchClient client, SinchError error) {
            if (mListener != null) {
                mListener.onStartFailed(error);
            }
            mSinchClient.terminate();
            mSinchClient = null;
        }

        @Override
        public void onClientStarted(SinchClient client) {
            Log.d(TAG, "SinchClient started");
            if (mListener != null) {
                mListener.onStarted();
            }
        }

        @Override
        public void onClientStopped(SinchClient client) {
            Log.d(TAG, "SinchClient stopped");
        }

        @Override
        public void onLogMessage(int level, String area, String message) {
            switch (level) {
                case Log.DEBUG:
                    Log.d(area, message);
                    break;
                case Log.ERROR:
                    Log.e(area, message);
                    break;
                case Log.INFO:
                    Log.i(area, message);
                    break;
                case Log.VERBOSE:
                    Log.v(area, message);
                    break;
                case Log.WARN:
                    Log.w(area, message);
                    break;
            }
        }

        @Override
        public void onRegistrationCredentialsRequired(SinchClient client, ClientRegistration clientRegistration) {
        }
    }

    private class SinchCallClientListener implements CallClientListener {

        @Override
        public void onIncomingCall(CallClient callClient, Call call) {
            try {

                Log.d(TAG, "Incoming call");

                helper = new Helper(SinchService.this);
                HashMap<String, User> myUsers = helper.getCacheMyUsers();
                if (myUsers != null && myUsers.containsKey(call.getRemoteUserId())) {
                    user = myUsers.get(call.getRemoteUserId());
                }
                if (user == null) {
                    user = FirebaseChatService.userHashMap.get(call.getRemoteUserId());
                }
                userMe = helper.getLoggedInUser();
                if (user == null) {
                    call(call);
                } else if (userMe != null && userMe.getBlockedUsersIds() != null &&
                        !userMe.getBlockedUsersIds().contains(user.getId())) {
                    call(call);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void call(Call call) {
        Intent intent = new Intent(SinchService.this, IncomingCallScreenActivity.class);
        intent.putExtra(CALL_ID, call.getCallId());
        callId = call.getCallId();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();
        if (!isScreenOn || BaseApplication.isInBackground) {
            Intent intentNotification = new Intent();
            intentNotification.putExtra(CALL_ID, call.getCallId());
            intentNotification.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intentNotification.setAction("com.from.notification");
            sendBroadcast(intentNotification);
            Call call1 = BaseActivity.mSinchServiceInterface.getCall(call.getCallId());
            if (call1 != null)
                call1.addCallListener(new SinchCallListener());

        } else {
            fromNotification = false;
            SinchService.this.startActivity(intent);
        }
    }

    private void callReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent data) {
                fromNotification = true;
                mAudioPlayer = new AudioPlayer(SinchService.this);
                mAudioPlayer.playRingtone();
                Log.d("", "");
                PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                boolean isScreenOn = pm.isScreenOn();
                if (!isScreenOn) {
                    PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
                            | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "MyLock");
                    wl.acquire(10000);
                    PowerManager.WakeLock wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyCpuLock");

                    wl_cpu.acquire(10000);
                }
                Intent intent = new Intent(SinchService.this, IncomingCallScreenActivity.class);
                intent.putExtra(CALL_ID, data.getExtras().getString(CALL_ID));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //  SinchService.this.startActivity(intent);
                String name = user != null ? user.getNameToDisplay() : user.getId();
                name = "Incoming Call from " + name;
                startActivityNotification(context, 123, getString(R.string.app_name),
                        name, intent);
            }
        };
    }

    // notification method to support opening activities on Android 10
    public static void startActivityNotification(Context context, int notificationID,
                                                 String title, String message, Intent intent) {

        NotificationManager mNotificationManager =
                (NotificationManager)
                        context.getSystemService(Context.NOTIFICATION_SERVICE);
        //Create GPSNotification builder
        NotificationCompat.Builder mBuilder;

        //Initialise ContentIntent
        Intent ContentIntent = new Intent(context, IncomingCallScreenActivity.class);
        intent.putExtra(CALL_ID, intent.getExtras().getString(CALL_ID));
        ContentIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("NotClick", true);
        PendingIntent ContentPendingIntent = PendingIntent.getActivity(context,
                0,
                ContentIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder = new NotificationCompat.Builder(context, "Call")
                .setSmallIcon(R.drawable.ic_logo_)
                .setContentTitle(title)
                .setContentText(message)
                .setColor(context.getResources().getColor(R.color.colorPrimaryDark))
                .setAutoCancel(true)
                .setContentIntent(ContentPendingIntent)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel("CHANNEL_ID",
                    "Activity Opening Notification",
                    NotificationManager.IMPORTANCE_HIGH);
            mChannel.enableLights(true);
            mChannel.enableVibration(true);
            mChannel.setDescription("Activity opening notification");

            mBuilder.setChannelId("CHANNEL_ID");

            Objects.requireNonNull(mNotificationManager).createNotificationChannel(mChannel);
        }

        Objects.requireNonNull(mNotificationManager).notify("TAG_NOTIFICATION", notificationID,
                mBuilder.build());
    }

    private class SinchCallListener implements CallListener {

        @Override
        public void onCallEnded(Call call) {
            if (SinchService.mAudioPlayer != null) {
                SinchService.mAudioPlayer.stopRingtone();
            }
            NotificationManager notificationManager = (NotificationManager)
                    getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancelAll();
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
*/
}