package com.hermes.chat;

import android.app.Application;
import android.content.Context;
import android.os.Vibrator;
import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.multidex.MultiDex;

import com.hermes.chat.audio.openacall.model.CurrentUserSettings;
import com.hermes.chat.audio.openacall.model.EngineConfig;
import com.hermes.chat.audio.openacall.model.MyEngineEventHandler;
import com.hermes.chat.audio.openacall.model.WorkerThread;
import com.hermes.chat.models.User;
import com.hermes.chat.receivers.ConnectivityReceiver;
import com.hermes.chat.utils.AudioPlayer;
import com.hermes.chat.utils.Helper;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.google.GoogleEmojiProvider;

import io.agora.rtc.RtcEngine;

public class BaseApplication extends Application implements LifecycleObserver {

    private static FirebaseApp secondApp;
    private static FirebaseDatabase secondDatabase;
    private static DatabaseReference userRef, chatRef, groupsRef, statusRef, callsRef, langRef;
    public static boolean isInBackground = false;
    protected Helper helper;
    protected User userMe;

    public static AudioPlayer mAudioPlayer;
    public static Vibrator vibrator;
    public static final String ACTION_TYPE = "ACTION_TYPE";
    public static final String ACTION_DECLINE = "com.chat.booa.DECLINE";
    public static long backgroundTimeStamp = 0;
    public static boolean isCall = false;

    private RtcEngine mRtcEngine;

    @Override
    protected void attachBaseContext(Context base) {
        MultiDex.install(this);
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        helper = new Helper(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        ConnectivityReceiver.init(this);
        EmojiManager.install(new GoogleEmojiProvider());
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        init();
    }

    private void init() {

        initWorkerThread();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        userRef = firebaseDatabase.getReference(Helper.REF_DATA).child(Helper.REF_USER);
        chatRef = firebaseDatabase.getReference(Helper.REF_DATA).child(Helper.REF_CHAT);
        groupsRef = firebaseDatabase.getReference(Helper.REF_DATA).child(Helper.REF_GROUP);
        statusRef = firebaseDatabase.getReference(Helper.REF_DATA).child(Helper.REF_STATUS_NEW);
        callsRef = firebaseDatabase.getReference(Helper.REF_DATA).child(Helper.REF_CALLS);
        langRef = firebaseDatabase.getReference(Helper.REF_DATA).child(Helper.REF_LANGUAGES);

      /*  FirebaseOptions options = new FirebaseOptions.Builder()
                .setApplicationId(getString(R.string.google_app_id))
                .setApiKey("AIzaSyCkl9gl4BwTALfBL2TkGZKFQO5LBml3Ib8")
         //     .setDatabaseUrl("https://dreamschat-livenew.firebaseio.com/") // Live
              .setDatabaseUrl("https://dreamschat-with-username.firebaseio.com/")
                .build();
        try {
         //   secondApp = FirebaseApp.initializeApp(this, options, "dreamschat-livenew");
            secondApp = FirebaseApp.initializeApp(this, options, "dreamschat-with-username");
            secondDatabase = FirebaseDatabase.getInstance(secondApp);
        } catch (IllegalStateException e) {
        //    secondDatabase = FirebaseDatabase.getInstance(FirebaseApp.getInstance("dreamschat-livenew"));
            secondDatabase = FirebaseDatabase.getInstance(FirebaseApp.getInstance("dreamschat-with-username"));
        }*/


    }


    public static DatabaseReference getUserRef() {
        if (userRef == null) {
            userRef = FirebaseDatabase.getInstance().getReference(Helper.REF_DATA).child(Helper.REF_USER);
//            userRef = secondDatabase.getReference(Helper.REF_DATA).child(Helper.REF_USER);
            userRef.keepSynced(true);
        }
        return userRef;
    }

    public static DatabaseReference getChatRef() {
        if (chatRef == null) {
            chatRef = FirebaseDatabase.getInstance().getReference(Helper.REF_DATA).child(Helper.REF_CHAT);
//            chatRef = secondDatabase.getReference(Helper.REF_DATA).child(Helper.REF_CHAT);
            chatRef.keepSynced(true);
        }
        return chatRef;
    }

    public static DatabaseReference getGroupRef() {
        if (groupsRef == null) {
            groupsRef = FirebaseDatabase.getInstance().getReference(Helper.REF_DATA).child(Helper.REF_GROUP);
//            groupsRef = secondDatabase.getReference(Helper.REF_DATA).child(Helper.REF_GROUP);
            groupsRef.keepSynced(true);
        }
        return groupsRef;
    }

    public static DatabaseReference getStatusRef() {
        if (statusRef == null) {
            statusRef = FirebaseDatabase.getInstance().getReference(Helper.REF_DATA).child(Helper.REF_STATUS_NEW);
//            statusRef = secondDatabase.getReference(Helper.REF_DATA).child(Helper.REF_STATUS_NEW);
            statusRef.keepSynced(true);
        }
        return statusRef;
    }

    public static DatabaseReference getCallsRef() {
        if (callsRef == null) {
            callsRef = FirebaseDatabase.getInstance().getReference(Helper.REF_DATA).child(Helper.REF_CALLS);
//            callsRef = secondDatabase.getReference(Helper.REF_DATA).child(Helper.REF_CALLS);
            callsRef.keepSynced(true);
        }
        return callsRef;
    }

    public static DatabaseReference getLangRef() {
        if (langRef == null) {
            langRef = FirebaseDatabase.getInstance().getReference(Helper.REF_DATA).child(Helper.REF_LANGUAGES);
//            langRef = secondDatabase.getReference(Helper.REF_DATA).child(Helper.REF_LANGUAGES);
            langRef.keepSynced(true);
        }
        return langRef;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private void onAppBackgrounded() {
        markOnline(false);
        isInBackground = true;
        helper.setBackground(true);
        Helper.CURRENT_CHAT_ID = null;
        Log.d("MyApp", "App in background");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private void onAppForegrounded() {
        markOnline(true);
        isInBackground = false;
        helper.setBackground(false);
        Log.d("MyApp", "App in foreground");
    }

    private void markOnline(boolean b) {
        helper = new Helper(this);
        userMe = helper.getLoggedInUser();
        if (userMe != null && userMe.getId() != null) {
            getUserRef().child(userMe.getId()).child("timeStamp").setValue(System.currentTimeMillis());
            getUserRef().child(userMe.getId()).child("online").setValue(b);
        }
    }

    public static final CurrentUserSettings mAudioSettings = new CurrentUserSettings();

    private static WorkerThread mWorkerThread;

    public synchronized void initWorkerThread() {
        if (mWorkerThread == null) {
            mWorkerThread = new WorkerThread(getApplicationContext());
            mWorkerThread.start();

            mWorkerThread.waitForReady();
        }
    }

    public static final MyEngineEventHandler event() {
        return getWorkerThread().eventHandler();
    }

    public static synchronized WorkerThread getWorkerThread() {
        return mWorkerThread;
    }

    public static final WorkerThread worker() {
        return getWorkerThread();
    }

    public static final EngineConfig config() {
        return getWorkerThread().getEngineConfig();
    }

    public static RtcEngine rtcEngine() {
        return getWorkerThread().getRtcEngine();
    }

}
