package com.hermes.chat.receivers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.hermes.chat.R;
import com.hermes.chat.activities.IncomingCallScreenActivity;
import com.hermes.chat.models.User;
import com.hermes.chat.services.FirebaseChatService;

import java.util.Objects;

public class CallReceivers extends BroadcastReceiver {

    public static final String CALL_ID = "CALL_ID";

    @Override
    public void onReceive(Context context, Intent intent) {
        /*SinchService.fromNotification = true;
        SinchService.mAudioPlayer = new AudioPlayer(context);
        SinchService.mAudioPlayer.playRingtone();*/
        Log.d("", "");
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();
        if (!isScreenOn) {
        /*    PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
                    | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "MyLock");
            wl.acquire(10000);
            PowerManager.WakeLock wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyCpuLock");
*/
//            wl_cpu.acquire(10000);
        }
        Intent call = new Intent(context, IncomingCallScreenActivity.class);
        call.putExtra(CALL_ID, intent.getExtras().getString(CALL_ID));
        call.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //  SinchService.this.startActivity(intent);

        User user = FirebaseChatService.userHashMap.get(intent.getExtras().getString(CALL_ID));

        String name = user != null ? user.getNameToDisplay() : user.getId();
        name = "Incoming Call from " + name;
        startActivityNotification(context, 123, context.getString(R.string.app_name),
                name, call);
    }

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
}
