package com.hermes.chat.services;

import static android.app.Notification.DEFAULT_ALL;
import static com.hermes.chat.BaseApplication.ACTION_DECLINE;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.hermes.chat.BaseApplication;
import com.hermes.chat.R;
import com.hermes.chat.activities.MainActivity;
import com.hermes.chat.audio.openacall.model.ConstantApp;
import com.hermes.chat.audio.openacall.ui.AgoraIncomingCallActivity;
import com.hermes.chat.models.User;
import com.hermes.chat.receivers.AgoraCallReceivers;
import com.hermes.chat.utils.AudioPlayer;
import com.hermes.chat.utils.Helper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private final String ADMIN_CHANNEL_ID = "admin_channel";
    Helper helper;
    NotificationManager notificationManager;
    int notificationID = 0;
    long[] pattern = {0, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500};

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e("NEW_TOKEN", s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.e("FCM", "onMessageReceived");
        helper = new Helper(this);
        receiveMessage(remoteMessage);
       /* new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (SinchHelpers.isSinchPushPayload(remoteMessage.getData())) {
                    if (helper.isBackground()) {
                        try {

                            Intent intent = new Intent(getApplicationContext(), CallReceivers.class);
                            JSONObject json = new JSONObject(remoteMessage.getData());
                            JSONObject json1 = null;
                            json1 = new JSONObject(json.getString("sinch"));
                            intent.putExtra("CALL_ID", json1.get("user_id").toString());
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setAction("com.chat.booa.CUSTOM_INTENT");
                            sendBroadcast(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    receiveMessage(remoteMessage);
                }
            }
        }, 2000);*/


        /*if (!remoteMessage.getData().containsKey("title")) {
            Intent intentNotification = new Intent();
            try {
                if (helper.isBackground()) {
                   *//* JSONObject json = new JSONObject(remoteMessage.getData());
                    JSONObject json1 = null;
                    json1 = new JSONObject(json.getString("sinch"));
                    intentNotification.putExtra("CALL_ID", json1.get("user_id").toString());
                    intentNotification.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intentNotification.setAction("com.from.notification");
                    sendBroadcast(intentNotification);*//*

                    Intent intent = new Intent(this, CallReceivers.class);
                    JSONObject json = new JSONObject(remoteMessage.getData());
                    JSONObject json1 = null;
                    json1 = new JSONObject(json.getString("sinch"));
                    intentNotification.putExtra("CALL_ID", json1.get("user_id").toString());
                    intentNotification.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setAction("com.chat.booa.CUSTOM_INTENT");
                    sendBroadcast(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        } else {*/
        // receiveMessage(remoteMessage);
        //  }


        if (helper.isLoggedIn()) {
            Intent intent = new Intent(this, FirebaseChatService.class);
            PendingIntent pendingIntent = PendingIntent.getService(this, 99, intent, PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_MUTABLE);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 500, pendingIntent);
            Log.e("FCM", "scheduled");
        }
    }

/*    public void receiveMessage(RemoteMessage remoteMessage) {
        try {
            String contactname = "";

            if (helper.isBackground() && !FirebaseChatService.isChatServiceStarted)
                new FirebaseChatService(getApplicationContext());

            if (!remoteMessage.getData().get("title").startsWith(Helper.GROUP_PREFIX) &&
                    helper.getCacheMyUsers().containsKey(remoteMessage.getData().get("title"))) {
                contactname = helper.getCacheMyUsers().get(remoteMessage.getData().get("title")).getNameToDisplay();
            }
            if (!helper.isUserMute(contactname)) {
                final Intent intent = new Intent(this, MainActivity.class);
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                int notificationID = new Random().nextInt(3000);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    setupChannels(notificationManager);
                }

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                        PendingIntent.FLAG_ONE_SHOT);

                Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),
                        R.drawable.ic_logo_);


                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_logo_)
                        .setLargeIcon(largeIcon)
                        .setContentTitle(remoteMessage.getData().get("title").startsWith(Helper.GROUP_PREFIX) ? remoteMessage.getData().get("title")
                                : contactname.isEmpty() ? remoteMessage.getData().get("title") : contactname)
                        .setContentText(remoteMessage.getData().get("body"))
                        .setAutoCancel(true);
                try {
                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                    r.play();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    notificationBuilder.setColor(getResources().getColor(R.color.colorPrimaryDark));
                }
                notificationManager.notify(notificationID, notificationBuilder.build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels(NotificationManager notificationManager) {
        CharSequence adminChannelName = "New notification";
        String adminChannelDescription = "Device to device notification";

        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel(ADMIN_CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_HIGH);
        adminChannel.setDescription(adminChannelDescription);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.RED);
        adminChannel.enableVibration(true);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(adminChannel);
        }
    }


    public void receiveMessage(RemoteMessage remoteMessage) {
        try {
            String contactname = "";
            final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                    getApplicationContext(), "channel_01");

            final Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + getApplicationContext().getPackageName() + "/raw/notification");
            final int icon = R.drawable.ic_logo_;
            if (remoteMessage.getData().get("title").equalsIgnoreCase("Audio Call")) {
                JSONObject json = new JSONObject(remoteMessage.getData());
                Intent callIntent = new Intent();
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                callIntent = new Intent(getApplicationContext(), AgoraIncomingCallActivity.class);
                callIntent.putExtra("data", json.toString());
                callIntent.putExtra(ConstantApp.ACTION_KEY_CHANNEL_NAME, remoteMessage.getData().get("body"));
                final PendingIntent resultPendingIntent =
                        PendingIntent.getActivity(getApplicationContext(), (int) System.currentTimeMillis(), callIntent, PendingIntent.FLAG_ONE_SHOT);

                Helper helper = new Helper(getApplicationContext());
                User userMe = helper.getLoggedInUser();
                if (userMe != null && userMe.getBlockedUsersIds() != null
                        && !userMe.getBlockedUsersIds().contains(json.getString("fromId"))) {


                    notificationID = (int) System.currentTimeMillis();
                    PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                    boolean isScreenOn = pm.isScreenOn();
                    if (!isScreenOn || helper.isBackground()) {
                        showSmallNotification(mBuilder, icon, "Audio call", "Incoming Audio Call", String.valueOf(System.currentTimeMillis()),
                                alarmSound, resultPendingIntent, true, notificationID);
                    } else {
                        callBroadCast(remoteMessage.getData().get("body"), json.toString());
                    }
                }


            } else if (remoteMessage.getData().get("title").equalsIgnoreCase("Video Call")) {
                JSONObject json = new JSONObject(remoteMessage.getData());
                Intent callIntent = new Intent();
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                callIntent = new Intent(getApplicationContext(), AgoraIncomingCallActivity.class);
                callIntent.putExtra("data", json.toString());
                callIntent.putExtra(ConstantApp.ACTION_KEY_CHANNEL_NAME, remoteMessage.getData().get("body"));
                final PendingIntent resultPendingIntent =
                        PendingIntent.getActivity(getApplicationContext(), (int) System.currentTimeMillis(), callIntent, PendingIntent.FLAG_ONE_SHOT);
                Helper helper = new Helper(getApplicationContext());
                User userMe = helper.getLoggedInUser();
                if (userMe != null && userMe.getBlockedUsersIds() != null
                        && !userMe.getBlockedUsersIds().contains(json.getString("fromId"))) {


                    notificationID = (int) System.currentTimeMillis();
                    PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                    boolean isScreenOn = pm.isScreenOn();
                    if (!isScreenOn || helper.isBackground()) {
                        showSmallNotification(mBuilder, icon, "Video Call", "Incoming Video Call", String.valueOf(System.currentTimeMillis()),
                                alarmSound, resultPendingIntent, true, notificationID);
                    } else {
                        callBroadCast(remoteMessage.getData().get("body"), json.toString());
                    }
                }

            } else if (remoteMessage.getData().get("title").equalsIgnoreCase("Group Audio call")) {
                JSONObject json = new JSONObject(remoteMessage.getData());
                Intent callIntent = new Intent();
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                callIntent = new Intent(getApplicationContext(), AgoraIncomingCallActivity.class);
                callIntent.putExtra("data", json.toString());
                callIntent.putExtra(ConstantApp.ACTION_KEY_CHANNEL_NAME, remoteMessage.getData().get("body"));
                final PendingIntent resultPendingIntent =
                        PendingIntent.getActivity(getApplicationContext(), (int) System.currentTimeMillis(), callIntent, PendingIntent.FLAG_ONE_SHOT);


                Helper helper = new Helper(getApplicationContext());
                notificationID = (int) System.currentTimeMillis();
                PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                boolean isScreenOn = pm.isScreenOn();
                if (!isScreenOn || helper.isBackground()) {
                    showSmallNotification(mBuilder, icon, "Audio call", "Incoming Audio Call", String.valueOf(System.currentTimeMillis()),
                            alarmSound, resultPendingIntent, true, notificationID);
                } else {
                    callBroadCast(remoteMessage.getData().get("body"), json.toString());
                }

            } else if (remoteMessage.getData().get("title").equalsIgnoreCase("Group Video call")) {
                JSONObject json = new JSONObject(remoteMessage.getData());
                Intent callIntent = new Intent();
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                callIntent = new Intent(getApplicationContext(), AgoraIncomingCallActivity.class);
                callIntent.putExtra("data", json.toString());
                callIntent.putExtra(ConstantApp.ACTION_KEY_CHANNEL_NAME, remoteMessage.getData().get("body"));
                final PendingIntent resultPendingIntent =
                        PendingIntent.getActivity(getApplicationContext(), (int) System.currentTimeMillis(), callIntent, PendingIntent.FLAG_ONE_SHOT);

                Helper helper = new Helper(getApplicationContext());
                notificationID = (int) System.currentTimeMillis();
                PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                boolean isScreenOn = pm.isScreenOn();
                if (!isScreenOn || helper.isBackground()) {
                    showSmallNotification(mBuilder, icon, "Video Call", "Incoming Video Call", String.valueOf(System.currentTimeMillis()),
                            alarmSound, resultPendingIntent, true, notificationID);
                } else {
                    callBroadCast(remoteMessage.getData().get("body"), json.toString());
                }

            } else if (remoteMessage.getData().get("title").equalsIgnoreCase("User Declined")) {
                Intent intentUpdate = new Intent(ACTION_DECLINE);
                intentUpdate.putExtra("type", "User Declined");
                if (BaseApplication.mAudioPlayer != null) {
                    BaseApplication.mAudioPlayer.stopRingtone();
                }
                if (BaseApplication.vibrator != null)
                    BaseApplication.vibrator.cancel();
                notificationManager = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
                if (notificationManager != null)
                    notificationManager.cancelAll();
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intentUpdate);
            } else {
                /*if (helper.isBackground() && !FirebaseChatService.isChatServiceStarted)
                    new FirebaseChatService(getApplicationContext());*/

                if (!remoteMessage.getData().get("title").startsWith(Helper.GROUP_PREFIX) &&
                        helper.getCacheMyUsers().containsKey(remoteMessage.getData().get("title"))) {
                    contactname = helper.getCacheMyUsers().get(remoteMessage.getData().get("title")).getName();
                }

                if (!helper.isUserMute(contactname) && Helper.CURRENT_CHAT_ID == null
                        || !Helper.CURRENT_CHAT_ID.equals(contactname)) {
                    final Intent intent = new Intent(this, MainActivity.class);
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    int notificationID = new Random().nextInt(3000);

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        setupChannels(notificationManager);
                    }

                    // intent.putExtra("extradatauser", helper.getCacheMyUsers().get(remoteMessage.getData().get("title")));
                    // intent.putParcelableArrayListExtra("extradatalist", new ArrayList<>());
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                            PendingIntent.FLAG_ONE_SHOT);

                    Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),
                            R.drawable.ic_logo_);


                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_logo_)
                            .setLargeIcon(largeIcon)
                            .setContentIntent(pendingIntent)
                            .setContentTitle(remoteMessage.getData().get("title").startsWith(Helper.GROUP_PREFIX) ? remoteMessage.getData().get("title")
                                    : contactname.isEmpty() ? remoteMessage.getData().get("title") : contactname)
                            .setContentText(remoteMessage.getData().get("body") == null ? remoteMessage.getData().get("message")
                                    : remoteMessage.getData().get("body"))
                            .setAutoCancel(true);
                    try {
                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                        r.play();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        notificationBuilder.setColor(getResources().getColor(R.color.colorPrimaryDark));
                    }

                    notificationManager.notify(notificationID, notificationBuilder.build());
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callBroadCast(String body, String json) {
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        switch (am.getRingerMode()) {
            case AudioManager.RINGER_MODE_SILENT:
                Log.i("MyApp", "Silent mode");
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                BaseApplication.vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                BaseApplication.vibrator.vibrate(pattern, 4);
                break;
            case AudioManager.RINGER_MODE_NORMAL:
                BaseApplication.mAudioPlayer = new AudioPlayer(this);
                BaseApplication.mAudioPlayer.playRingtone();
                break;
        }
        Intent intent = new Intent(this, AgoraCallReceivers.class);
        intent.putExtra("body", body);
        intent.putExtra("data", json);
        intent.setAction("com.chat.booa.CUSTOM_INTENT");
        sendBroadcast(intent);
    }

    private void showSmallNotification(NotificationCompat.Builder mBuilder, int icon,
                                       String title, String message, String timeStamp, Uri alarmSound,
                                       PendingIntent resultPendingIntent, boolean ongoing, int id) {
        BaseApplication.isCall = true;
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.addLine(message);
        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;


        notificationManager = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            notificationManager = getSystemService(NotificationManager.class);
            /* Create or update. */
            NotificationChannel notificationChannel = new NotificationChannel("channel_01",
                    getApplicationContext().getResources().getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("channel_01");
            //  notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            //notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notificationManager.createNotificationChannel(notificationChannel);
        }

        Notification notification;
        notification = mBuilder.setTicker(title)
                .setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title != null ? title.isEmpty() ?
                        getApplicationContext().getResources().getString(R.string.app_name) :
                        title :
                        getApplicationContext().getResources().getString(R.string.app_name))
                .setContentIntent(resultPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setOngoing(ongoing)
                .setWhen(System.currentTimeMillis())
                .setDefaults(DEFAULT_ALL)
                .setShowWhen(true)
                .setSmallIcon(R.drawable.ic_logo_)
                .setChannelId("channel_01") // set channel id
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), icon))
                .setContentText(message)
                .build();

        notificationManager.notify(id, notification);


        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = Build.VERSION.SDK_INT >= 20 ? pm.isInteractive() : pm.isScreenOn(); // check if screen is on
        if (!isScreenOn) {
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "myApp:notificationLock");
            wl.acquire(3000); //set your time in milliseconds
        }


        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        switch (am.getRingerMode()) {
            case AudioManager.RINGER_MODE_SILENT:
                Log.i("MyApp", "Silent mode");
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                BaseApplication.vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    BaseApplication.vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    BaseApplication.vibrator.vibrate(500);
                }*/

                BaseApplication.vibrator.vibrate(pattern, 4);
                break;
            case AudioManager.RINGER_MODE_NORMAL:
                BaseApplication.mAudioPlayer = new AudioPlayer(this);
                BaseApplication.mAudioPlayer.playRingtone();
                break;
        }
    }
}
