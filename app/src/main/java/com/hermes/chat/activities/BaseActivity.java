package com.hermes.chat.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.hermes.chat.BaseApplication;
import com.hermes.chat.models.Contact;
import com.hermes.chat.models.Group;
import com.hermes.chat.models.Status;
import com.hermes.chat.models.User;
import com.hermes.chat.services.FirebaseChatService;
import com.hermes.chat.services.SinchService;
import com.hermes.chat.utils.Helper;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import io.realm.Realm;


public abstract class BaseActivity extends AppCompatActivity implements ServiceConnection {
    protected String[] permissionsRecord = {Manifest.permission.VIBRATE, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    protected String[] permissionsContact = {Manifest.permission.READ_CONTACTS};
    protected String[] permissionsStorage = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    protected String[] permissionsCamera = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    protected String[] permissionsSinch = {Manifest.permission.RECORD_AUDIO, Manifest.permission.MODIFY_AUDIO_SETTINGS, Manifest.permission.READ_PHONE_STATE};
    protected User userMe, user;
    protected Group group;
    protected Helper helper;
    protected Realm rChatDb;

    public static FirebaseChatService firebaseChatService;

    protected DatabaseReference usersRef, groupRef, chatRef, statusRef;
    private FirebaseApp secondApp;
    private FirebaseDatabase secondDatabase;
//    public static SinchService.SinchServiceInterface mSinchServiceInterface;

    //Group updates receiver(new or updated)
    private BroadcastReceiver groupReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals(Helper.BROADCAST_GROUP)) {
                Group group = intent.getParcelableExtra("data");
                String what = intent.getStringExtra("what");
                switch (what) {
                    case "added":
                        groupAdded(group);
                        break;
                    case "changed":
                        groupUpdated(group);
                        break;
                }
            }
        }
    };

    //User updates receiver(new or updated)
    private BroadcastReceiver userReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals(Helper.BROADCAST_USER)) {
                User user = intent.getParcelableExtra("data");
                String what = intent.getStringExtra("what");
                switch (what) {
                    case "added":
                        userAdded(user);
                        break;
                    case "changed":
                        userUpdated(user);
                        Intent local = new Intent("custom-event-name");
                        local.putExtra("status", user.getStatus());
                        LocalBroadcastManager.getInstance(BaseActivity.this).sendBroadcast(local);
                        break;
                }
            }
        }
    };

    private BroadcastReceiver statusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals(Helper.BROADCAST_STATUS)) {
                Status status = intent.getParcelableExtra("data");
                String what = intent.getStringExtra("what");
                switch (what) {
                    case "added":
                        statusAdded(status);
                        break;
                    case "changed":
                        statusUpdated(status);
                        break;
                }
            }
        }
    };

    private BroadcastReceiver myUsersReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<User> myUsers = intent.getParcelableArrayListExtra("data");
            if (myUsers != null) {
                myUsersResult(myUsers);
            }
        }
    };

    private BroadcastReceiver myContactsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
           /* ArrayList<Contact> myContacts = intent.getParcelableArrayListExtra("data");
            if (myContacts != null) {
                myContactsResult(myContacts);
            }*/
            ArrayList<User> myUsers = intent.getParcelableArrayListExtra("data");
            if (myUsers != null) {
                myUsersResult(myUsers);
            }
        }
    };

    abstract void myUsersResult(ArrayList<User> myUsers);

    abstract void myContactsResult(ArrayList<Contact> myContacts);

    abstract void userAdded(User valueUser);

    abstract void groupAdded(Group valueGroup);

    abstract void userUpdated(User valueUser);

    abstract void groupUpdated(Group valueGroup);

    abstract void statusAdded(Status status);

    abstract void statusUpdated(Status status);

    abstract void onSinchConnected();

    abstract void onSinchDisconnected();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helper = new Helper(this);
        userMe = helper.getLoggedInUser();

        Realm.init(this);
        rChatDb = Helper.getRealmInstance();
        usersRef = BaseApplication.getUserRef();
        groupRef = BaseApplication.getGroupRef();
        chatRef = BaseApplication.getChatRef();
        statusRef = BaseApplication.getStatusRef();

        if (!FirebaseChatService.isChatServiceStarted)
            firebaseChatService = new FirebaseChatService(BaseActivity.this);

        FirebaseApp.initializeApp(this);
        //  getApplicationContext().bindService(new Intent(this, SinchService.class), this, BIND_AUTO_CREATE);

    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(userReceiver, new IntentFilter(Helper.BROADCAST_USER));
        localBroadcastManager.registerReceiver(groupReceiver, new IntentFilter(Helper.BROADCAST_GROUP));
        localBroadcastManager.registerReceiver(myContactsReceiver, new IntentFilter(Helper.BROADCAST_MY_CONTACTS));
        localBroadcastManager.registerReceiver(myUsersReceiver, new IntentFilter(Helper.BROADCAST_MY_USERS));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.unregisterReceiver(userReceiver);
        localBroadcastManager.unregisterReceiver(groupReceiver);
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        if (SinchService.class.getName().equals(componentName.getClassName())) {
//            mSinchServiceInterface = (SinchService.SinchServiceInterface) iBinder;
            onSinchConnected();

            if (userMe.getName() != null) {
//                mSinchServiceInterface.startClient(userMe.getName());
            }

        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        if (SinchService.class.getName().equals(componentName.getClassName())) {
//            mSinchServiceInterface = null;
            onSinchDisconnected();
        }
    }

   /* protected SinchService.SinchServiceInterface getSinchServiceInterface() {
        return mSinchServiceInterface;
    }*/

    protected boolean permissionsAvailable(String[] permissions) {
        boolean granted = true;
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                granted = false;
                break;
            }
        }
        return granted;
    }
}
