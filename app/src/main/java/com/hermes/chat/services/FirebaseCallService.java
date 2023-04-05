package com.hermes.chat.services;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.hermes.chat.BaseApplication;
import com.hermes.chat.models.CallLogFireBaseModel;
import com.hermes.chat.models.LogCall;
import com.hermes.chat.models.User;
import com.hermes.chat.utils.Helper;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;

import io.realm.RealmList;

public class FirebaseCallService {
    public static ArrayList<LogCall> logCall = new ArrayList<>();
    Context context;
    LogCall log;

    public FirebaseCallService(Context context, String myId) {
        this.context = context;
        getMyCalls(myId);
    }

    private void getMyCalls(String myId) {
        logCall = new ArrayList<>();
        BaseApplication.getCallsRef().child(myId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                CallLogFireBaseModel callLog = dataSnapshot.getValue(CallLogFireBaseModel.class);
                saveCallLog(callLog, myId);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                CallLogFireBaseModel callLog = dataSnapshot.getValue(CallLogFireBaseModel.class);
                saveCallLog(callLog, myId);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                try {
                    CallLogFireBaseModel callLog = dataSnapshot.getValue(CallLogFireBaseModel.class);
                    removeLog(callLog);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void saveCallLog(CallLogFireBaseModel callLog, String myId) {
        User user = FirebaseChatService.userHashMap.get(callLog.getCallerId().get(0));

        RealmList<String> list = new RealmList<>();
        list.addAll(callLog.getCallerId());
        logCall.add(new LogCall(user, callLog.getCurrentMills(), 0,
                callLog.isVideo(), callLog.getInOrOut(), myId, list,
                callLog.getId(), callLog.getDuration()));
    }

    private void removeLog(CallLogFireBaseModel callLog) {
        for (LogCall logs : logCall) {
            if (logs.getId().equalsIgnoreCase(callLog.getId())) {
                log = logs;
                break;
            }
        }
        logCall.remove(log);
        broadcastCall();
    }

    private void broadcastCall() {
        Intent intent = new Intent(Helper.BROADCAST_CALLS);
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
        localBroadcastManager.sendBroadcast(intent);
    }
}
