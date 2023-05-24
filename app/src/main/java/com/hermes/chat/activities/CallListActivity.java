package com.hermes.chat.activities;

import static com.hermes.chat.utils.Helper.createRandomChannelName;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.hermes.chat.BaseApplication;
import com.hermes.chat.R;
import com.hermes.chat.adapters.CallListAdapter;
import com.hermes.chat.audio.openacall.model.ConstantApp;
import com.hermes.chat.audio.openacall.ui.AgoraSingleAudioCallActivity;
import com.hermes.chat.audio.openacall.ui.AgoraSingleVideoCallActivity;
import com.hermes.chat.models.CallLogFireBaseModel;
import com.hermes.chat.models.Contact;
import com.hermes.chat.models.Group;
import com.hermes.chat.models.Status;
import com.hermes.chat.models.User;
import com.hermes.chat.pushnotification.Aps;
import com.hermes.chat.pushnotification.Data;
import com.hermes.chat.pushnotification.Notification;
import com.hermes.chat.pushnotification.SendFirebaseNotification;
import com.hermes.chat.services.FetchMyUsersService;
import com.hermes.chat.services.FirebaseCallService;
import com.hermes.chat.utils.Helper;
import com.google.gson.Gson;

import java.util.ArrayList;

public class CallListActivity extends BaseActivity {
    private ArrayList<User> myUsers = new ArrayList<>();
    private SwipeRefreshLayout swipeMenuRecyclerView;
    private SearchView searchView;
    private RecyclerView callListRecyclerView;
    private CallListAdapter callListAdapter;
    private final int CONTACTS_REQUEST_CODE = 321;
    private ImageView back_button;
    private TextView title;
    private Helper helper;
    private TextView emptyText;
    private TextView titleNewCall;


    @Override
    void myUsersResult(ArrayList<User> myUsers) {
        helper.setCacheMyUsers(myUsers);
        this.myUsers.clear();
        this.myUsers.addAll(myUsers);
        try {
            callListAdapter.updateReceiptsList(this.myUsers);
        } catch (Exception e) {
            e.printStackTrace();
        }
        swipeMenuRecyclerView.setRefreshing(false);
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
        if (userMe.getId().equalsIgnoreCase(valueUser.getId())) {
            valueUser.setNameInPhone(helper.getLoggedInUser().getNameInPhone());
            helper.setLoggedInUser(valueUser);
            MainActivity.myUsers.size();
            callListAdapter = new CallListAdapter(CallListActivity.this, MainActivity.myUsers,
                    helper.getLoggedInUser());
            callListRecyclerView.setAdapter(callListAdapter);
        } else {
            int existingPos = MainActivity.myUsers.indexOf(valueUser);
            if (existingPos != -1) {
                valueUser.setNameInPhone(MainActivity.myUsers.get(existingPos).getNameInPhone());
                MainActivity.myUsers.set(existingPos, valueUser);
                helper.setCacheMyUsers(MainActivity.myUsers);
                callListAdapter = new CallListAdapter(CallListActivity.this, MainActivity.myUsers,
                        helper.getLoggedInUser());
                callListRecyclerView.setAdapter(callListAdapter);
            }
        }
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
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_list);

        uiInit();

    }

    private void uiInit() {
        helper = new Helper(CallListActivity.this);
        swipeMenuRecyclerView = findViewById(R.id.callListSwipeRefresh);
        searchView = findViewById(R.id.searchView);
        back_button = findViewById(R.id.back_button);
        title = findViewById(R.id.title);
        callListRecyclerView = findViewById(R.id.callListRecyclerView);
        emptyText = findViewById(R.id.emptyText);
        titleNewCall = findViewById(R.id.titleNewCall);

        //searchview color change
        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.colorIcon));
        searchEditText.setHint("Search User");
        searchEditText.setHintTextColor(getResources().getColor(R.color.colorIcon));

        callListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchView.setIconified(true);
//        ImageView searchIcon = searchView.findViewById(R.id.search_button);
//        searchIcon.setImageDrawable(ContextCompat.getDrawable(CallListActivity.this, R.drawable.ic_search_white));
//        SearchView.SearchAutoComplete searchAutoComplete = searchView.findViewById(R.id.search_src_text);
//        searchAutoComplete.setHintTextColor(getResources().getColor(android.R.color.white));
//        searchAutoComplete.setHint(Helper.getCallsData(CallListActivity.this).getLblSearchTitle());
        emptyText.setText(Helper.getCallsData(CallListActivity.this).getLblEnterUsernameSearch());
        titleNewCall.setText(Helper.getCallsData(CallListActivity.this).getLblCreateNewCall());
//        searchAutoComplete.setTextColor(getResources().getColor(android.R.color.white));

        callListAdapter = new CallListAdapter(CallListActivity.this, myUsers, helper.getLoggedInUser());
        callListRecyclerView.setAdapter(callListAdapter);
        fetchContacts();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                callListAdapter.getFilter().filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                callListAdapter.getFilter().filter(s);
                return false;
            }
        });

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    back_button.setVisibility(View.GONE);
                    title.setVisibility(View.GONE);
                } else {
                    back_button.setVisibility(View.VISIBLE);
                    title.setVisibility(View.GONE);
                    searchView.setIconified(true);
                }
            }
        });

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });
    }

    private void fetchContacts() {
        BaseApplication.getUserRef()
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String print = "";
                                User user;
                                ArrayList<User> users= new ArrayList<>();
                                ArrayList<String> connectList = new ArrayList<>();
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    try {
                                        user = snapshot.getValue(User.class);
                                        if (user.getId() != null){

                                            users.add(user);
                                            if(user.getId().equals(userMe.getId())){
                                                connectList.addAll(user.getConnect_list());
                                            }

                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                for(int i=0;i<users.size();i++){
                                    for(int j=0;j<connectList.size();j++){
                                        try
                                        {
                                            if(connectList.get(j).equals(users.get(i).getId())){
                                                myUsers.add(users.get(i));

                                            }
                                        }catch (Exception e){

                                        }
                                    }
                                }
                                callListAdapter.updateReceiptsList(myUsers);
                                /*new FetchMyUsersService(CallListActivity.this, userMe.getId());
                                new FirebaseCallService(CallListActivity.this, userMe.getId());*/




                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

    }

    /*private void fetchContacts() {
        *//*if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {*//*
           *//* if (!FetchMyUsersService.STARTED) {
                if (!swipeMenuRecyclerView.isRefreshing())
                    swipeMenuRecyclerView.setRefreshing(true);
                new FetchMyUsersService(CallListActivity.this, userMe.getId());
            }*//*
        *//*} else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS},
                    CONTACTS_REQUEST_CODE);
        }*//*
    }*/

    public void forwardToRoom(String type, boolean callIsVideo, User user) {
        BaseApplication.getUserRef().child(userMe.getId())
                .child("call_status").setValue(false);
        ArrayList<String> userList = new ArrayList<>();
        userList.add(user.getId());
        CallLogFireBaseModel callLogFireBaseModel = new CallLogFireBaseModel(userList,
                "", "", System.currentTimeMillis(), "OUT",
                callIsVideo, userMe.getId(), "single");
        String id = BaseApplication.getCallsRef().child(userMe.getId()).push().getKey();
        callLogFireBaseModel.setId(id);
        BaseApplication.getCallsRef().child(userMe.getId()).child(id).setValue(callLogFireBaseModel);


        String[] registration_ids = new String[]{};
        String channel = createRandomChannelName();
        Notification notification = new Notification(type, userMe.getId(), userMe.getId(),
                user.getId(), "incoming.wav", "", channel);
        Data data = null;
        if (permissionsAvailable(permissionsSinch)) {
            if (user.getWebqrcode() != null && !user.getWebqrcode().isEmpty()) {

               /* if (channel.length() > 16) {
                    channel = channel.substring(0, 15);
                }*/
                data = new Data(type, channel, " ", userMe.getId(), user.getId(), "", "");
                String call_type = "";
                if (type.equalsIgnoreCase("Audio call"))
                    call_type = "audio";
                else
                    call_type = "video";

                BaseApplication.getUserRef().child(user.getId()).child("incomingcall").setValue(
                        "user_type=onetoone&call_type=" + call_type + "&channelname=" + channel +
                                "&caller=" + userMe.getId() + "&receiver=" +
                                user.getId() + "&group=");

              /*  BaseApplication.getUserRef().child(user.getId()).child("channelname").setValue(
                        "channel_name=" + channel + "&caller=" + userMe.getId() + "&receiver=" + user.getId());*/
            } else {
                registration_ids = new String[1];
                registration_ids[0] = user.getDeviceToken();

                if (user.getOsType() != null && !user.getOsType().isEmpty() && user.getOsType().equalsIgnoreCase("iOS")) {
                    data = new Data(type, userMe.getId(), " ", userMe.getId(), user.getId(), "", channel);
                    com.hermes.chat.pushnotification.Message notificationTask =
                            new com.hermes.chat.pushnotification.Message(registration_ids,
                                    "", data, notification,
                                    new Aps(" ", "incoming.wav"));

                    new SendFirebaseNotification(CallListActivity.this, notificationTask).triggerMessage();
                } else {
                    data = new Data(type, channel, " ", userMe.getId(), user.getId(), "", "");
                    com.hermes.chat.pushnotification.Message notificationTask =
                            new com.hermes.chat.pushnotification.Message(registration_ids,
                                    "", data,
                                    new Aps(" ", " "));

                    new SendFirebaseNotification(CallListActivity.this, notificationTask).triggerMessage();
                }
            }
            BaseApplication.mAudioSettings.mChannelName = channel;
            Intent i;
            if (type.equalsIgnoreCase("Audio call"))
                i = new Intent(CallListActivity.this, AgoraSingleAudioCallActivity.class);
            else
                i = new Intent(CallListActivity.this, AgoraSingleVideoCallActivity.class);

            i.putExtra(ConstantApp.ACTION_KEY_CHANNEL_NAME, channel);
            i.putExtra("id", id);
            String str = new Gson().toJson(data);
            i.putExtra("data", str);
            startActivity(i);
        } else {
            ActivityCompat.requestPermissions(this, permissionsSinch, 69);
        }
    }

    /*public void makeCall(boolean b, User user) {
        FragmentManager manager = getSupportFragmentManager();
        Fragment frag = manager.findFragmentByTag("DELETE_TAG");
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }
        if (user != null && userMe != null && userMe.getBlockedUsersIds() != null
                && userMe.getBlockedUsersIds().contains(user.getId())) {
            Helper.unBlockAlert(user.getNameToDisplay(), userMe, CallListActivity.this,
                    helper, user.getId(), manager);
        } else
            placeCall(b, user);
    }*/

  /*  private void placeCall(boolean callIsVideo, User user) {
        if (permissionsAvailable(permissionsSinch)) {
            try {
                Call call = callIsVideo ? getSinchServiceInterface().callUserVideo(user.getId())
                        : getSinchServiceInterface().callUser(user.getId());
                if (call == null) {
                    // Service failed for some reason, show a Toast and abort
                    Toast.makeText(this, "Service is not started." +
                            " Try stopping the service and starting it again before placing a call.", Toast.LENGTH_LONG).show();
                    return;
                }
                String callId = call.getCallId();
                startActivity(CallScreenActivity.newIntent(this, user, callId, "OUT"));
            } catch (Exception e) {
                Log.e("CHECK", e.getMessage());
                //ActivityCompat.requestPermissions(this, new String[]{e.getRequiredPermission()}, 0);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissionsSinch, 69);
        }
    }*/
}
