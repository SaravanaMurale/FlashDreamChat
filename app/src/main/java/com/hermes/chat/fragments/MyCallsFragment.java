package com.hermes.chat.fragments;

import static com.hermes.chat.utils.Helper.getCallsData;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hermes.chat.BaseApplication;
import com.hermes.chat.R;
import com.hermes.chat.activities.MainActivity;
import com.hermes.chat.adapters.LogCallAdapter;
import com.hermes.chat.interfaces.HomeIneractor;
import com.hermes.chat.models.Contact;
import com.hermes.chat.models.LogCall;
import com.hermes.chat.models.User;
import com.hermes.chat.services.FirebaseCallService;
import com.hermes.chat.utils.ConfirmationDialogFragment;
import com.hermes.chat.utils.Helper;
import com.hermes.chat.views.MyRecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

public class MyCallsFragment extends Fragment {
    private MyRecyclerView recyclerView, missedRecyclerView;
    public LogCallAdapter chatAdapter;
    public LogCallAdapter missedCallAdapter;
    // private SwipeRefreshLayout mySwipeRefreshLayout;
    private Realm rChatDb;
    private User userMe;
    private RealmResults<LogCall> resultList;
    public static ArrayList<LogCall> callDataList = new ArrayList<>();
    private ArrayList<LogCall> logCallDataList = new ArrayList<>();
    private ArrayList<LogCall> missedCallDataList = new ArrayList<>();
    private LinearLayout emptyView;
    private TextView missedText;
    private TextView otherCallText;
    private ImageView emptyImage;
    private TextView emptyText;
    private NestedScrollView nestedScrollView;
    private FragmentManager manager;
    private Helper helper;
    private static String CONFIRM_TAG = "confirmtag";
    public static int selectedCount = 0;
    private Context context;
    private MainActivity activity;

    private RealmChangeListener<RealmResults<LogCall>> chatListChangeListener = new RealmChangeListener<RealmResults<LogCall>>() {
        @Override
        public void onChange(RealmResults<LogCall> element) {
            if (element != null && element.isValid() && element.size() > 0) {

                callDataList.clear();
                callDataList.addAll(rChatDb.copyFromRealm(element));

                for (int i = 0; i < callDataList.size(); i++) {
                    if (callDataList.get(i).getStatus().equalsIgnoreCase("CANCELED") ||
                            callDataList.get(i).getStatus().equalsIgnoreCase("DENIED")) {
                        missedCallDataList.add(callDataList.get(i));
                    } else {
                        logCallDataList.add(callDataList.get(i));
                    }
                }
                setUserNamesAsInPhone();
            }
        }
    };
    private HomeIneractor homeInteractor;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        activity = (MainActivity) getActivity();
        try {
            homeInteractor = (HomeIneractor) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement HomeIneractor");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
//        homeInteractor = null;
       /* if (resultList != null)
            resultList.removeChangeListener(chatListChangeListener);*/
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helper = new Helper(getContext());
        userMe = homeInteractor.getUserMe();
        Realm.init(getContext());
        rChatDb = Helper.getRealmInstance();
        manager = getChildFragmentManager();
        Fragment frag = manager.findFragmentByTag("DELETE_TAG");
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
        localBroadcastManager.registerReceiver(callReceiver, new IntentFilter(Helper.BROADCAST_CALLS));
    }

    private BroadcastReceiver callReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.getAction() != null && intent.getAction().equals(Helper.BROADCAST_CALLS)) {
                    callDataList.clear();
                    missedCallDataList.clear();
                    logCallDataList.clear();

                    callDataList.addAll(FirebaseCallService.logCall);
                    HashSet<LogCall> hashSet = new HashSet<>();
                    hashSet.addAll(callDataList);
                    callDataList.clear();
                    callDataList.addAll(hashSet);

                    for (int i = 0; i < callDataList.size(); i++) {
                        if (callDataList.get(i).getStatus().equalsIgnoreCase("CANCELED") ||
                                callDataList.get(i).getStatus().equalsIgnoreCase("DENIED")) {
                            missedCallDataList.add(callDataList.get(i));
                        } else {
                            logCallDataList.add(callDataList.get(i));
                        }
                    }
                    Collections.reverse(logCallDataList);
                    Collections.reverse(missedCallDataList);
                    if (chatAdapter != null && missedCallAdapter != null) {
                        chatAdapter.notifyDataSetChanged();
                        missedCallAdapter.notifyDataSetChanged();
                    }
                    setUserNamesAsInPhone();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_call_list, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        missedRecyclerView = view.findViewById(R.id.missedRecyclerView);
        missedText = view.findViewById(R.id.missedText);
        otherCallText = view.findViewById(R.id.otherCallText);
        emptyView = view.findViewById(R.id.emptyView);
        emptyImage = view.findViewById(R.id.emptyImage);
        nestedScrollView = view.findViewById(R.id.scroll);
        emptyText = view.findViewById(R.id.emptyText);
        emptyImage.setBackgroundResource(R.drawable.ic_call_green_24dp);
        emptyText.setText(getCallsData(getContext()).getLblCallsEmpty());
        missedText.setText(getCallsData(getContext()).getLblMissedCall());
        otherCallText.setText(getCallsData(getContext()).getLblOtherCall());
      /*  mySwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_lay);
        mySwipeRefreshLayout.setRefreshing(false);*/
    /*    recyclerView.setEmptyView(view.findViewById(R.id.emptyView));
        recyclerView.setEmptyImageView(((ImageView) view.findViewById(R.id.emptyImage)));
        recyclerView.setEmptyTextView(((TextView) view.findViewById(R.id.emptyText)));
        recyclerView.setEmptyImage(R.drawable.ic_call_green_24dp);
        recyclerView.setEmptyText(getString(R.string.empty_log_call_list));*/

        recyclerView.setNestedScrollingEnabled(false);
        missedRecyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        missedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


      /*  mySwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    RealmQuery<LogCall> query = rChatDb.where(LogCall.class).equalTo("myId", userMe.getId());//Query from chats whose owner is logged in user
                    resultList = query.isNotNull("user").sort("timeUpdated", Sort.DESCENDING).findAll();//ignore forward list of messages and get rest sorted according to time

                    logCallDataList.clear();
                    missedCallDataList.clear();
                    callDataList.clear();
                    callDataList.addAll(rChatDb.copyFromRealm(resultList));

                    for (int i = 0; i < callDataList.size(); i++) {
                        if (callDataList.get(i).getStatus().equalsIgnoreCase("CANCELED") ||
                                callDataList.get(i).getStatus().equalsIgnoreCase("DENIED")) {
                            missedCallDataList.add(callDataList.get(i));
                        } else {
                            logCallDataList.add(callDataList.get(i));
                        }
                    }
                    chatAdapter = new LogCallAdapter(getActivity(), logCallDataList);
                    recyclerView.setAdapter(chatAdapter);
                    missedCallAdapter = new LogCallAdapter(getActivity(), missedCallDataList);
                    missedRecyclerView.setAdapter(missedCallAdapter);

                    resultList.addChangeListener(chatListChangeListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                setUserNamesAsInPhone();
                mySwipeRefreshLayout.setRefreshing(false);
            }
        });*/
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }


    @Override
    public void onResume() {
        super.onResume();
        try {
            RealmQuery<LogCall> query = rChatDb.where(LogCall.class).equalTo("myId", userMe.getId());
            resultList = query.isNotNull("user").sort("timeUpdated", Sort.DESCENDING).findAll();

            logCallDataList.clear();
            missedCallDataList.clear();
            callDataList.clear();
            // callDataList.addAll(rChatDb.copyFromRealm(resultList));
            callDataList.addAll(FirebaseCallService.logCall);

            for (int i = 0; i < callDataList.size(); i++) {
                if (callDataList.get(i).getStatus().equalsIgnoreCase("CANCELED") ||
                        callDataList.get(i).getStatus().equalsIgnoreCase("DENIED")) {
                    missedCallDataList.add(callDataList.get(i));
                } else {
                    logCallDataList.add(callDataList.get(i));
                }
            }
            Collections.sort(logCallDataList, new LogCall());
            chatAdapter = new LogCallAdapter(getActivity(), logCallDataList, MainActivity.myUsers,
                    helper.getLoggedInUser(), manager, helper);
            recyclerView.setAdapter(chatAdapter);
            Collections.sort(missedCallDataList, new LogCall());
            missedCallAdapter = new LogCallAdapter(getActivity(), missedCallDataList,
                    MainActivity.myUsers, helper.getLoggedInUser(), manager, helper);
            missedRecyclerView.setAdapter(missedCallAdapter);

            resultList.addChangeListener(chatListChangeListener);

        } catch (Exception e) {
            e.printStackTrace();
        }
        setUserNamesAsInPhone();
    }

    public void setUserNamesAsInPhone() {
        try {
            ArrayList<LogCall> tempList = new ArrayList<>();
            tempList.addAll(logCallDataList);
            tempList.addAll(missedCallDataList);
            if (homeInteractor != null && tempList != null) {
                for (LogCall logCall : tempList) {
                    User user = logCall.getUser();
                    if (user != null) {
                        if (helper.getCacheMyUsers() != null && helper.getCacheMyUsers().containsKey(user.getId())) {
                            user.setNameInPhone(helper.getCacheMyUsers().get(user.getId()).getNameToDisplay());
                        } else {
                            for (Contact savedContact : homeInteractor.getLocalContacts()) {
                                if (Helper.contactMatches(user.getId(), savedContact.getPhoneNumber())) {
                                    if (user.getNameInPhone() == null || !user.getNameInPhone().equals(savedContact.getName())) {
                                        user.setNameInPhone(savedContact.getName());
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            if (missedCallDataList.size() > 0) {
                missedText.setVisibility(View.VISIBLE);
            } else
                missedText.setVisibility(View.GONE);

            if (logCallDataList.size() > 0) {
                otherCallText.setVisibility(View.VISIBLE);
            } else
                otherCallText.setVisibility(View.GONE);

            if (missedCallDataList.size() == 0 && logCallDataList.size() == 0) {
                emptyView.setVisibility(View.VISIBLE);
                nestedScrollView.setVisibility(View.GONE);
            } else {
                emptyView.setVisibility(View.GONE);
                nestedScrollView.setVisibility(View.VISIBLE);
            }

            if (chatAdapter != null)
                chatAdapter.notifyDataSetChanged();

            if (missedCallAdapter != null)
                missedCallAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

            try {
                RealmQuery<LogCall> query = rChatDb.where(LogCall.class).equalTo("myId", userMe.getId());
                resultList = query.isNotNull("user").sort("timeUpdated", Sort.DESCENDING).findAll();

                logCallDataList.clear();
                missedCallDataList.clear();
                callDataList.clear();
                // callDataList.addAll(rChatDb.copyFromRealm(resultList));
                callDataList.addAll(FirebaseCallService.logCall);

                for (int i = 0; i < callDataList.size(); i++) {
                    if (callDataList.get(i).getStatus().equalsIgnoreCase("CANCELED") ||
                            callDataList.get(i).getStatus().equalsIgnoreCase("DENIED")) {
                        missedCallDataList.add(callDataList.get(i));
                    } else {
                        logCallDataList.add(callDataList.get(i));
                    }
                }
                Collections.sort(logCallDataList, new LogCall());
                chatAdapter = new LogCallAdapter(getActivity(), logCallDataList, MainActivity.myUsers,
                        helper.getLoggedInUser(), manager, helper);
                recyclerView.setAdapter(chatAdapter);
                Collections.sort(missedCallDataList, new LogCall());
                missedCallAdapter = new LogCallAdapter(getActivity(), missedCallDataList,
                        MainActivity.myUsers, helper.getLoggedInUser(), manager, helper);
                missedRecyclerView.setAdapter(missedCallAdapter);

                resultList.addChangeListener(chatListChangeListener);

            } catch (Exception e) {
                e.printStackTrace();
            }
            setUserNamesAsInPhone();


        }
    }

    public void disableContextualMode() {
        if (chatAdapter != null && missedCallAdapter != null) {
            chatAdapter.disableContextualMode();
            missedCallAdapter.disableContextualMode();
        }
    }

    public void deleteCalls() {

        final FragmentManager manager = getActivity().getSupportFragmentManager();
        Fragment frag = manager.findFragmentByTag(CONFIRM_TAG);
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        final ConfirmationDialogFragment confirmationDialogFragment = ConfirmationDialogFragment
                .newInstance(getCallsData(activity).getLblDeleteLog(),
                        getCallsData(activity).getLblDeleteMessage(),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                for (LogCall call : callDataList) {
                                    if (call.isSelected()) {
                                        try {
                                            BaseApplication.getCallsRef().child(userMe.getId())
                                                    .child(call.getId()).removeValue();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                                activity.action_checkBox.setChecked(false);
                                activity.disableContextualMode();
                                disableContextualMode();
                            }
                        },
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                activity.disableContextualMode();
                                disableContextualMode();
                            }
                        });
        confirmationDialogFragment.show(manager, CONFIRM_TAG);
    }

    public void notifyChange() {
        chatAdapter.notifyDataSetChanged();
        missedCallAdapter.notifyDataSetChanged();
    }
}
