package com.hermes.chat.activities;

/*import static com.dreamguys.dreamschat.services.SinchService.APP_KEY;
import static com.dreamguys.dreamschat.services.SinchService.APP_SECRET;*/

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.hermes.chat.BaseApplication;
import com.hermes.chat.R;
import com.hermes.chat.adapters.MenuUsersRecyclerAdapter;
import com.hermes.chat.adapters.ViewPagerAdapter;
import com.hermes.chat.fragments.GroupCreateDialogFragment;
import com.hermes.chat.fragments.MyCallsFragment;
import com.hermes.chat.fragments.MyGroupsFragment;
import com.hermes.chat.fragments.MyStatusFragment;
import com.hermes.chat.fragments.MyUsersFragment;
import com.hermes.chat.fragments.OptionsFragment;
import com.hermes.chat.fragments.UserSelectDialogFragment;
import com.hermes.chat.interfaces.ContextualModeInteractor;
import com.hermes.chat.interfaces.HomeIneractor;
import com.hermes.chat.interfaces.OnUserGroupItemClick;
import com.hermes.chat.interfaces.PushTokenRegistrationCallback;
import com.hermes.chat.interfaces.UserGroupSelectionDismissListener;
import com.hermes.chat.models.Contact;
import com.hermes.chat.models.Group;
import com.hermes.chat.models.LogCall;
import com.hermes.chat.models.Message;
import com.hermes.chat.models.Status;
import com.hermes.chat.models.User;
import com.hermes.chat.services.FetchMyUsersService;
import com.hermes.chat.services.FirebaseCallService;
import com.hermes.chat.services.FirebaseStatusService;
import com.hermes.chat.utils.Helper;
import com.hermes.chat.views.SwipeControlViewPager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mxn.soul.flowingdrawer_core.ElasticDrawer;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import io.realm.Realm;

public class MainActivity extends BaseActivity implements HomeIneractor, OnUserGroupItemClick,
        View.OnClickListener, ContextualModeInteractor, UserGroupSelectionDismissListener,
        PushTokenRegistrationCallback/*, UserRegistrationCallback*/ {

    private static final String TAG = "MainActivity";
    private final int CONTACTS_REQUEST_CODE = 321;
    private static final int REQUEST_CODE_CHAT_FORWARD = 99;
    private static String USER_SELECT_TAG = "userselectdialog";
    private static String OPTIONS_MORE = "optionsmore";
    private static String GROUP_CREATE_TAG = "groupcreatedialog";
    private static String CONFIRM_TAG = "confirmtag";

    private ImageView usersImage, backImage, dialogUserImage;
    private RecyclerView menuRecyclerView;
    private SwipeRefreshLayout swipeMenuRecyclerView;
    private FlowingDrawer drawerLayout;
    private EditText searchContact;
    private TextView invite, selectedCount;
    private RelativeLayout toolbarContainer, cabContainer;
    public OptionsFragment fragment = null;
    public MyStatusFragment statusFragment = null;
    public MyCallsFragment callsFragment = null;
    private TabLayout tabLayout;
    private SwipeControlViewPager viewPager;

    private FloatingActionButton floatingActionButton;
    private CoordinatorLayout coordinatorLayout;

    private MenuUsersRecyclerAdapter menuUsersRecyclerAdapter;
    private ArrayList<Contact> contactsData = new ArrayList<>();
    public static ArrayList<User> myUsers = new ArrayList<>();
    private ArrayList<Group> myGroups = new ArrayList<>();
    private ArrayList<Status> myStatus = new ArrayList<>();
    private ArrayList<Message> messageForwardList = new ArrayList<>();
    private UserSelectDialogFragment userSelectDialogFragment;
    private ViewPagerAdapter adapter;
    public static String userId;
    private ProgressDialog dialog;
    private long mSigningSequence = 1;
    public CheckBox action_checkBox;

    private DatabaseReference idChatRef;

    public DatabaseReference getDatabaseRef() {
        return statusRef;
    }

    public DatabaseReference getUserDatabaseRef() {
        return usersRef;
    }

    public Realm getRealmRef() {
        return rChatDb;
    }

//    public SinchService.SinchServiceInterface getSinchRef() {
//        return getSinchServiceInterface();
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try
                {
                    userMe = dataSnapshot.getValue(User.class);
                    helper.setLoggedInUser(userMe);
                }catch (Exception e){

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("TAG", databaseError.getMessage());
            }
        };
        idChatRef = BaseApplication.getUserRef().child(userMe.getId());
        idChatRef.addValueEventListener(valueEventListener);*/


        if(userMe.getAdminblock()){
            checkBlocked();
        }else {
            initUi();
        }


        //setup recyclerview in drawer layout




     /*   FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(MainActivity.this,
                new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {

                    }
                });*/
    }

    public void checkBlocked()
    {


          /*  BaseApplication.getUserRef().child(userMe.getId()).addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    User user= snapshot.getValue(User.class);
                    if(user.getAdminblock()){*/
                          try {
//                idChatRef.removeEventListener(valueEventListener);
                helper.setCacheMyUsers(new ArrayList<>());
                FirebaseAuth.getInstance().signOut();
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(Helper.BROADCAST_LOGOUT));
//                                    sinchServiceInterface.stopClient();
                helper.clearPhoneNumberForVerification();
                helper.logout();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Helper.getRealmInstance().executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.deleteAll();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
                        SharedPreferences prefs = getSharedPreferences(String.valueOf(R.string.app_name), Context.MODE_PRIVATE);
                        prefs.edit().putString("LoggedIn","false").apply();
                        Intent mIntent = new Intent(MainActivity.this, UserNameSignInActivity.class);
                        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mIntent);
                        finish();
                   /* }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error)
                {

                }
            });*/


    }

    /*private void loadAdd() {
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }*/

    private void initUi() {
        SharedPreferences prefs = getSharedPreferences(String.valueOf(R.string.app_name), Context.MODE_PRIVATE);
        prefs.edit().putString("LoggedIn","true").apply();
        usersImage = findViewById(R.id.users_image);
        action_checkBox = findViewById(R.id.action_checkBox);
        action_checkBox.setVisibility(View.GONE);
        menuRecyclerView = findViewById(R.id.menu_recycler_view);
        swipeMenuRecyclerView = findViewById(R.id.menu_recycler_view_swipe_refresh);
        drawerLayout = findViewById(R.id.drawer_layout);
        searchContact = findViewById(R.id.searchContact);
        invite = findViewById(R.id.invite);
        toolbarContainer = findViewById(R.id.toolbarContainer);
        cabContainer = findViewById(R.id.cabContainer);
        selectedCount = findViewById(R.id.selectedCount);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        floatingActionButton = findViewById(R.id.addConversation);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        backImage = findViewById(R.id.back_button);
        drawerLayout.setTouchMode(ElasticDrawer.TOUCH_MODE_BEZEL);
        dialog = new ProgressDialog(MainActivity.this);
        dialog.setMessage("Syncing. . .");
        dialog.setCancelable(false);
        dialog.setCancelable(false);
        dialog.show();



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try
                {
                    if (dialog != null && dialog.isShowing())
                        dialog.dismiss();
                }catch (Exception e){

                }

            }
        }, 4000);

       /* UserController userController = Sinch.getUserControllerBuilder()
                .context(getApplicationContext())
                .applicationKey(APP_KEY)
                .userId(userMe.getId())
                .environmentHost(ENVIRONMENT)
                .build();
        userController.unregisterPushToken();
        userController.registerUser(this, this);*/

        action_checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    if (MyCallsFragment.callDataList != null) {
                        for (LogCall call : MyCallsFragment.callDataList) {
                            if (!call.isSelected()) {
                                call.setSelected(true);
                                callsFragment.selectedCount++;
                            }
                        }
                    }
                } else {
                    if (MyCallsFragment.callDataList != null) {
                        for (LogCall call : MyCallsFragment.callDataList) {
                            if (call.isSelected()) {
                                call.setSelected(false);
                                callsFragment.selectedCount--;
                            }
                        }
                        callsFragment.selectedCount = 0;
                    }
                }
                callsFragment.notifyChange();
                callsFragment.missedCallAdapter.updateCount();
                callsFragment.chatAdapter.updateCount();
            }
        });
        userId = userMe.getId();

        setupMenu();

        //If its a url then load it, else Make a text drawable of user's name
        setProfileImage(usersImage);
        usersImage.setOnClickListener(this);
        backImage.setOnClickListener(this);
        invite.setOnClickListener(this);
        findViewById(R.id.action_delete).setOnClickListener(this);
        floatingActionButton.setOnClickListener(this);
        floatingActionButton.setVisibility(View.VISIBLE);

        fetchContacts();
        setupViewPager();

        markOnline(true);
        //  loadAdd();

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isComplete()) {

                    String token = task.getResult();
                    Log.i("FCM Token", token);
                    userMe.setDeviceToken(token);
                    userMe.setOsType(getString(R.string.osType));
                    userMe.setTimeStamp(System.currentTimeMillis());
                    userMe.setOnline(true);
//                    userMe.setConnect_list(userMe.getConnect_list());
                    BaseApplication.getUserRef().child(userMe.getId()).setValue(userMe)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    helper.setLoggedInUser(userMe);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });

                }

            }
        });
    }

    private void setupViewPager() {
        statusFragment = new MyStatusFragment();
        callsFragment = new MyCallsFragment();
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new MyUsersFragment(MainActivity.this), Helper.getHomeData
                (MainActivity.this).getLblMenuChat());
        adapter.addFrag(new MyGroupsFragment(MainActivity.this), Helper.getHomeData
                (MainActivity.this).getLblMenuGroups());
        adapter.addFrag(statusFragment, Helper.getHomeData
                (MainActivity.this).getLblMenuStatus());
        adapter.addFrag(callsFragment, Helper.getHomeData
                (MainActivity.this).getLblMenuCalls());
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (i == 2)
                    floatingActionButton.hide();
                else
                    floatingActionButton.show();
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

    }

    private void setupMenu() {
        menuRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        menuUsersRecyclerAdapter = new MenuUsersRecyclerAdapter(this, myUsers, helper.getLoggedInUser());
        menuRecyclerView.setAdapter(menuUsersRecyclerAdapter);
        swipeMenuRecyclerView.setRefreshing(false);
        swipeMenuRecyclerView.setColorSchemeResources(R.color.colorAccent);
        swipeMenuRecyclerView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchContacts();
            }
        });
        searchContact.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                menuUsersRecyclerAdapter.getFilter().filter(editable.toString());
            }
        });
    }

    private void setProfileImage(ImageView imageView) {
        if (userMe != null)
            if (userMe.getImage() != null && !userMe.getImage().isEmpty()) {
                Picasso.get()
                        .load(userMe.getImage())
                        .tag(this)
                        .error(R.drawable.ic_avatar)
                        .placeholder(R.drawable.ic_avatar)
                        .into(imageView);
            } else if (group != null && group.getImage() != null && !group.getImage().isEmpty()) {
                Picasso.get()
                        .load(group.getImage())
                        .tag(this)
                        .placeholder(R.drawable.ic_avatar)
                        .into(imageView);

            }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CONTACTS_REQUEST_CODE:
//                fetchContacts();
                break;
        }
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
                                                Log.d(TAG, "onDataChange: "+users.get(i).getId());

                                            }
                                        }catch (Exception e){

                                        }
                                    }
                                }

                                setupMenu();
                                new FetchMyUsersService(MainActivity.this, userMe.getId());
                                new FirebaseCallService(MainActivity.this, userMe.getId());

                                Log.d(TAG, "onDataChangevalue: "+print);


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

    }
    /*private void fetchContacts() {
        *//*if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            if (!FetchMyUsersService.STARTED) {
                if (!swipeMenuRecyclerView.isRefreshing())
                    swipeMenuRecyclerView.setRefreshing(true);
                new FetchMyUsersService(MainActivity.this, userMe.getId());
                new FirebaseCallService(MainActivity.this, userMe.getId());
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, CONTACTS_REQUEST_CODE);
        }*//*
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        markOnline(false);
    }

    @Override
    public void onBackPressed() {
        if (ElasticDrawer.STATE_CLOSED != drawerLayout.getDrawerState()) {
            drawerLayout.closeMenu(true);
        } else if (isContextualMode()) {
            disableContextualMode();
        } else if (viewPager.getCurrentItem() != 0) {
            viewPager.post(new Runnable() {
                @Override
                public void run() {
                    viewPager.setCurrentItem(0);
                }
            });
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (REQUEST_CODE_CHAT_FORWARD):
                if (resultCode == Activity.RESULT_OK) {
                    //show forward dialog to choose users
                    messageForwardList.clear();
                    ArrayList<Message> temp = data.getParcelableArrayListExtra("FORWARD_LIST");
                    messageForwardList.addAll(temp);
                    userSelectDialogFragment = UserSelectDialogFragment.newInstance(this, myUsers);
                    FragmentManager manager = getSupportFragmentManager();
                    Fragment frag = manager.findFragmentByTag(USER_SELECT_TAG);
                    if (frag != null) {
                        manager.beginTransaction().remove(frag).commit();
                    }
                    userSelectDialogFragment.show(manager, USER_SELECT_TAG);
                }
                break;
            case (203):
                if (fragment != null)
                    fragment.onActivityResult(requestCode, resultCode, data);
                else if (statusFragment != null)
                    statusFragment.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private void sortMyGroupsByName() {
        Collections.sort(myGroups, new Comparator<Group>() {
            @Override
            public int compare(Group group1, Group group2) {
                return group1.getName().compareToIgnoreCase(group2.getName());
            }
        });
    }

    private void sortMyUsersByName() {
        Collections.sort(myUsers, new Comparator<User>() {
            @Override
            public int compare(User user1, User user2) {
                return user1.getNameToDisplay().compareToIgnoreCase(user2.getNameToDisplay());
            }
        });
    }

    @Override
    void userAdded(User value) {
        if (value.getId().equals(userMe.getId()))
            return;
        else if (helper.getCacheMyUsers() != null && helper.getCacheMyUsers().containsKey(value.getId())) {
            value.setNameInPhone(helper.getCacheMyUsers().get(value.getId()).getNameToDisplay());
            addUser(value);
        } else {
//            for (Contact savedContact : contactsData) {
//                if (Helper.contactMatches(value.getId(), savedContact.getPhoneNumber())) {
            for (int i = 0;i<myUsers.size();i++) {
                    value.setNameInPhone(myUsers.get(i).getName());
                    addUser(value);
                    helper.setCacheMyUsers(myUsers);
                    break;
                }
//            }
        }
    }

    @Override
    void groupAdded(Group group) {
        if (!myGroups.contains(group)) {
            Log.d(TAG, "groupAdded: "+group.getName());
            myGroups.add(group);
            sortMyGroupsByName();
        }
    }

    @Override
    void userUpdated(User value) {
        if (value.getId().equals(userMe.getId())) {
            userMe = value;
            helper.setLoggedInUser(userMe);
            setProfileImage(usersImage);
        } else if (helper.getCacheMyUsers() != null && helper.getCacheMyUsers().containsKey(value.getId())) {
            value.setNameInPhone(helper.getCacheMyUsers().get(value.getId()).getNameToDisplay());
            updateUser(value);
        } else {
            for (Contact savedContact : contactsData) {
                if (Helper.contactMatches(value.getId(), savedContact.getPhoneNumber())) {
                    value.setNameInPhone(savedContact.getName());
                    updateUser(value);
                    helper.setCacheMyUsers(myUsers);
                    break;
                }
            }
        }
    }

    private void updateUser(User value) {
        int existingPos = myUsers.indexOf(value);
        if (existingPos != -1) {
            myUsers.set(existingPos, value);
            menuUsersRecyclerAdapter.notifyItemChanged(existingPos);
            refreshUsers(existingPos);
        }
    }

    @Override
    void groupUpdated(Group group) {
        int existingPos = myGroups.indexOf(group);
        if (existingPos != -1) {
            myGroups.set(existingPos, group);
            //menuUsersRecyclerAdapter.notifyItemChanged(existingPos);
            //refreshUsers(existingPos);
        }
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

    private void addUser(User value) {
        if (myUsers.contains(value)) {
            if(!myUsers.contains(value)){
                myUsers.add(value);
            }

            sortMyUsersByName();
            menuUsersRecyclerAdapter.notifyDataSetChanged();
            refreshUsers(-1);
        }

       /* if (!myUsers.contains(value)) {
            ArrayList<String> connect_list = new ArrayList<>();
            if(value.getId().equals(userMe)){
                for(int i=0;i<value.getConnect_list().size();i++){
                    connect_list.add(value.getConnect_list().get(i));
                }
            }
            for(int i=0;i<myUsers.size();i++){
                for(int j=0;j<connect_list.size();j++){
                    try
                    {
                        if(connect_list.get(j).equals(myUsers.get(i).getId())){
                            myUsers.add(myUsers.get(i));
                            Log.d(TAG, "onDataChange: "+myUsers.get(i).getId());

                        }
                    }catch (Exception e){

                    }

                }
            }
            sortMyUsersByName();
            menuUsersRecyclerAdapter.notifyDataSetChanged();
            refreshUsers(-1);
        }*/
    }


    @Override
    public void OnUserClick(final User user, int position, View userImage) {
        if (ElasticDrawer.STATE_CLOSED != drawerLayout.getDrawerState()) {
            drawerLayout.closeMenu(true);
        }
        if (userImage == null) {
            userImage = usersImage;
        }
        Intent intent = ChatActivity.newIntent(this, messageForwardList, user);
        if (Build.VERSION.SDK_INT > 21) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this,
                    userImage, "backImage");
            startActivityForResult(intent, REQUEST_CODE_CHAT_FORWARD, options.toBundle());
        } else {
            startActivityForResult(intent, REQUEST_CODE_CHAT_FORWARD);
            overridePendingTransition(0, 0);
        }

        if (userSelectDialogFragment != null)
            userSelectDialogFragment.dismiss();
    }

    @Override
    public void OnGroupClick(Group group, int position, View userImage) {
        Intent intent = ChatActivity.newIntent(this, messageForwardList, group);
        if (userImage == null) {
            userImage = usersImage;
        }
        if (Build.VERSION.SDK_INT > 21) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, userImage, "backImage");
            startActivityForResult(intent, REQUEST_CODE_CHAT_FORWARD, options.toBundle());
        } else {
            startActivityForResult(intent, REQUEST_CODE_CHAT_FORWARD);
            overridePendingTransition(0, 0);
        }

        if (userSelectDialogFragment != null)
            userSelectDialogFragment.dismiss();
    }

    private void refreshUsers(int pos) {
        Fragment frag = getSupportFragmentManager().findFragmentByTag(USER_SELECT_TAG);
        if (frag != null) {
            userSelectDialogFragment.refreshUsers(pos);
        }
    }

    private void markOnline(boolean b) {
        //Mark online boolean as b in firebase
        usersRef.child(userMe.getId()).child("timeStamp").setValue(System.currentTimeMillis());
        usersRef.child(userMe.getId()).child("online").setValue(b);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_button:
                drawerLayout.openMenu(true);
                break;
            case R.id.addConversation:
                switch (viewPager.getCurrentItem()) {
                    case 0:
//                        drawerLayout.openMenu(true);
                        Intent callIntent = new Intent(MainActivity.this, ContactActivity.class);
                        startActivity(callIntent);
                        break;
                    case 1:
                        for (int i = 0; i < myUsers.size(); i++) {
                            myUsers.get(i).setSelected(false);
                        }
                        GroupCreateDialogFragment.newInstance(this, userMe, myUsers)
                                .show(getSupportFragmentManager(), GROUP_CREATE_TAG);
                        break;
                    case 3:
//                        drawerLayout.openMenu(true);
                        Intent aCallIntent = new Intent(MainActivity.this, CallListActivity.class);
                        startActivity(aCallIntent);
                        break;
                }
                break;
            case R.id.users_image:
                if (userMe != null) {
                    fragment = new OptionsFragment();
                    fragment.show(getSupportFragmentManager(),
                            OPTIONS_MORE);
                }
                break;
            case R.id.invite:
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.invitation_title));
                    shareIntent.putExtra(Intent.EXTRA_TEXT, String.format(getString(R.string.invitation_text), getPackageName()));
                    startActivity(Intent.createChooser(shareIntent, "Share using.."));
                } catch (Exception ignored) {
                }
                break;
            case R.id.action_delete:
//                FragmentManager manager = getSupportFragmentManager();
//                Fragment frag = manager.findFragmentByTag(CONFIRM_TAG);
//                if (frag != null) {
//                    manager.beginTransaction().remove(frag).commit();
//                }
//
//                ConfirmationDialogFragment confirmationDialogFragment = ConfirmationDialogFragment.newInstance("Delete chat",
//                        "Continue deleting selected chats?",
//                        new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                ((MyUsersFragment) adapter.getItem(0)).deleteSelectedChats();
//                                ((MyGroupsFragment) adapter.getItem(1)).deleteSelectedChats();
//                                disableContextualMode();
//                            }
//                        },
//                        new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                disableContextualMode();
//                            }
//                        });
//                confirmationDialogFragment.show(manager, CONFIRM_TAG);

                if (viewPager.getCurrentItem() == 0) {
                    ((MyUsersFragment) adapter.getItem(0)).deleteSelectedChats();
                } else if (viewPager.getCurrentItem() == 1) {
                    ((MyGroupsFragment) adapter.getItem(1)).deleteSelectedChats();
                } else if (viewPager.getCurrentItem() == 3) {
                    ((MyCallsFragment) adapter.getItem(3)).deleteCalls();
                }
                break;
        }
    }

    @Override
    public void onUserGroupSelectDialogDismiss() {
        messageForwardList.clear();
//        if (helper.getSharedPreferenceHelper().getBooleanPreference(Helper.GROUP_CREATE, false)) {
//            helper.getSharedPreferenceHelper().setBooleanPreference(Helper.GROUP_CREATE, false);
//            GroupCreateDialogFragment.newInstance(this, userMe, myUsers).show(getSupportFragmentManager(), GROUP_CREATE_TAG);
//        }
    }

    @Override
    public void selectionDismissed() {
        //do nothing..
    }

    @Override
    public void myUsersResult(ArrayList<User> myUsers) {
        helper.setCacheMyUsers(myUsers);
        this.myUsers.clear();
        this.myUsers.addAll(myUsers);

        if (!FirebaseStatusService.isStatusServiceStarted)
            new FirebaseStatusService(MainActivity.this);

        refreshUsers(-1);
        menuUsersRecyclerAdapter.notifyDataSetChanged();
        swipeMenuRecyclerView.setRefreshing(false);
    }

    public boolean checkServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void myContactsResult(ArrayList<Contact> myContacts) {
        contactsData.clear();
        contactsData.addAll(myContacts);
        MyUsersFragment myUsersFragment = ((MyUsersFragment) adapter.getItem(0));
        if (myUsersFragment != null) myUsersFragment.setUserNamesAsInPhone();
        MyCallsFragment myCallsFragment = ((MyCallsFragment) adapter.getItem(3));
        if (myCallsFragment != null) myCallsFragment.setUserNamesAsInPhone();
    }

    public void disableContextualMode() {
        cabContainer.setVisibility(View.GONE);
        toolbarContainer.setVisibility(View.VISIBLE);
        if (viewPager.getCurrentItem() == 3) {
            action_checkBox.setVisibility(View.GONE);
            action_checkBox.setChecked(false);
        }
        ((MyUsersFragment) adapter.getItem(0)).disableContextualMode();
        ((MyGroupsFragment) adapter.getItem(1)).disableContextualMode();
        ((MyCallsFragment) adapter.getItem(3)).disableContextualMode();
        viewPager.setSwipeAble(true);
    }

    @Override
    public void enableContextualMode() {
        cabContainer.setVisibility(View.VISIBLE);
        toolbarContainer.setVisibility(View.GONE);
        viewPager.setSwipeAble(false);
        if (viewPager.getCurrentItem() == 3) {
            action_checkBox.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean isContextualMode() {
        return cabContainer.getVisibility() == View.VISIBLE;
    }

    @Override
    public void updateSelectedCount(int count) {
        if (count > 0) {
            selectedCount.setText(String.format("%d selected", count));
        } else {
            disableContextualMode();
        }
    }

    @Override
    public User getUserMe() {
        return userMe;
    }

    @Override
    public ArrayList<Contact> getLocalContacts() {
        return contactsData;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void tokenRegistered() {
        Log.e("", "");
    }



 /*   @Override
    public void tokenRegistrationFailed(SinchError sinchError) {
        Log.e("", "");
    }*/

/*    @Override
    public void onCredentialsRequired(ClientRegistration clientRegistration) {
        Log.e("", "");
        String toSign = userMe.getId() + APP_KEY + mSigningSequence + APP_SECRET;
        String signature;
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("SHA-1");
            byte[] hash = messageDigest.digest(toSign.getBytes("UTF-8"));
            signature = Base64.encodeToString(hash, Base64.DEFAULT).trim();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e.getCause());
        }

        clientRegistration.register(signature, mSigningSequence++);
    }*/

  /*  @Override
    public void onUserRegistered() {
        Log.e("", "");
    }

    @Override
    public void onUserRegistrationFailed(SinchError sinchError) {
        Log.e("", "");
    }*/
}
