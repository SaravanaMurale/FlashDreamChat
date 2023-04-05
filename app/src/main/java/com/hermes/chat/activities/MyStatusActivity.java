package com.hermes.chat.activities;

import static com.hermes.chat.utils.Helper.getStatusData;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hermes.chat.BaseApplication;
import com.hermes.chat.R;
import com.hermes.chat.adapters.MyStatusAdapter;
import com.hermes.chat.fragments.MyStatusFragment;
import com.hermes.chat.models.AttachmentList;
import com.hermes.chat.models.MessageNewArrayList;
import com.hermes.chat.models.StatusImageList;
import com.hermes.chat.models.StatusNew;
import com.hermes.chat.models.User;
import com.hermes.chat.utils.ConfirmationDialogFragment;
import com.hermes.chat.utils.Helper;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;


public class MyStatusActivity extends AppCompatActivity {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.myStatusRecycler)
    RecyclerView myStatusRecycler;
    User userMe;
    StatusImageList item;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_status);
        ButterKnife.bind(this);

        uiInit();
    }

    private void uiInit() {
        Helper helper = new Helper(this);
        userMe = helper.getLoggedInUser();
        MyStatusAdapter adapter = new MyStatusAdapter(MyStatusActivity.this,
                MyStatusFragment.statusNew.getStatusImages().get(0).getAttachment().getUrlList());
        myStatusRecycler.setAdapter(adapter);
        title.setText(getStatusData(MyStatusActivity.this).getLblMyStatus());
        findViewById(R.id.back_button).setOnClickListener(v -> finish());
    }


    public void delete(StatusImageList statusImageList) {
        for (StatusImageList urlList : MyStatusFragment.statusNew.getStatusImages()
                .get(0).getAttachment().getUrlList()) {
            if (urlList.getUrl().equals(statusImageList.getUrl())) {
                item = urlList;
            }
        }

        MyStatusFragment.statusNew.getStatusImages()
                .get(0).getAttachment().getUrlList().remove(item);

        BaseApplication.getStatusRef().child(userMe.getId()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.e("", "");
                MessageNewArrayList msg = dataSnapshot.getValue(MessageNewArrayList.class);
                if (MyStatusFragment.statusNew != null) {
                    if (MyStatusFragment.statusNew.getStatusImages()
                            .get(0).getAttachment().getUrlList().size() == 0) {
                        BaseApplication.getStatusRef().child(userMe.getId()).removeValue();
                    } else {
                        BaseApplication.getStatusRef().child(userMe.getId()).child(msg.getId()).
                                child("attachment").child("urlList").setValue(MyStatusFragment.statusNew.getStatusImages()
                                        .get(0).getAttachment().getUrlList());
                    }
                } else {
                    BaseApplication.getStatusRef().child(userMe.getId()).removeValue();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                loadAdapter();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                loadAdapter();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.e("", "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("", "");
            }
        });

        Helper helper = new Helper(MyStatusActivity.this);
        Realm rChatDb = helper.getRealmInstance();
        StatusNew statusQuery =
                rChatDb.where(StatusNew.class).equalTo("userId", userMe.getId()).findFirst();
        rChatDb.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                if (MyStatusFragment.statusNew != null) {
                    if (MyStatusFragment.statusNew.getStatusImages()
                            .get(0).getAttachment().getUrlList().size() == 0) {
                        RealmObject.deleteFromRealm(statusQuery);
                    } else {
                        AttachmentList attachment1 = statusQuery.getStatusImages().get(0).getAttachment();
                        RealmList<StatusImageList> realmList = new RealmList<>();
                        for (int i = 0; i < MyStatusFragment.statusNew.getStatusImages()
                                .get(0).getAttachment().getUrlList().size(); i++) {
                            StatusImageList statusImageList = rChatDb.createObject(StatusImageList.class);
                            statusImageList.setUrl(MyStatusFragment.statusNew.getStatusImages()
                                    .get(0).getAttachment().getUrlList().get(i).getUrl());
                            statusImageList.setExpiry(MyStatusFragment.statusNew.getStatusImages()
                                    .get(0).getAttachment().getUrlList().get(i).isExpiry());
                            statusImageList.setUploadTime(MyStatusFragment.statusNew.getStatusImages()
                                    .get(0).getAttachment().getUrlList().get(i).getUploadTime());
                            realmList.add(statusImageList);
                        }
                        attachment1.setUrlList(realmList);
                    }
                }
            }
        });
    }

    public void showDeleteDialog(StatusImageList statusImageList) {
        final FragmentManager manager = getSupportFragmentManager();
        Fragment frag = manager.findFragmentByTag("CONFIRM_TAG");
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }
        final ConfirmationDialogFragment confirmationDialogFragment = ConfirmationDialogFragment
                .newInstance(getStatusData(MyStatusActivity.this).getLblDeleteStatus(),
                        getStatusData(MyStatusActivity.this).getLblDeleteConfirmation(),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                delete(statusImageList);
                            }
                        },
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        });
        confirmationDialogFragment.show(manager, "CONFIRM_TAG");
    }

    private void loadAdapter() {
        if (MyStatusFragment.statusNew != null) {
            MyStatusAdapter adapter = new MyStatusAdapter(MyStatusActivity.this,
                    MyStatusFragment.statusNew.getStatusImages().get(0).getAttachment().getUrlList());
            myStatusRecycler.setAdapter(adapter);
        } else {
            finish();
        }
    }
}
