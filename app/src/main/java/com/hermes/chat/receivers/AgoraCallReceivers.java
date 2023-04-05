package com.hermes.chat.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hermes.chat.audio.openacall.model.ConstantApp;
import com.hermes.chat.audio.openacall.ui.AgoraIncomingCallActivity;

public class AgoraCallReceivers extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        Intent callIntent = new Intent(context, AgoraIncomingCallActivity.class);
        callIntent.putExtra(ConstantApp.ACTION_KEY_CHANNEL_NAME, intent.getStringExtra("body"));
        callIntent.putExtra("data", intent.getStringExtra("data"));
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(callIntent);
    }
}
