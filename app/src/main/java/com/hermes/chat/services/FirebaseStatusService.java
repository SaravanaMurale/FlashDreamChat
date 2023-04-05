package com.hermes.chat.services;

import android.content.Context;
import android.util.Log;

import com.hermes.chat.BaseApplication;
import com.hermes.chat.activities.MainActivity;
import com.hermes.chat.models.AttachmentList;
import com.hermes.chat.models.MessageNewArrayList;
import com.hermes.chat.models.StatusImageList;
import com.hermes.chat.models.StatusImageNew;
import com.hermes.chat.models.StatusNew;
import com.hermes.chat.models.User;
import com.hermes.chat.utils.Helper;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import io.realm.Realm;
import io.realm.RealmList;

public class FirebaseStatusService {

    private static final String CHANNEL_ID_MAIN = "service";
    private User userMe;
    private Helper helper;
    private String myId;
    private Realm rChatDb;
    private Context context;
    public static boolean isStatusServiceStarted = false;

    public FirebaseStatusService(Context context) {
        isStatusServiceStarted = true;
        this.context = context;
        if (!User.validate(userMe)) {
            initVars();
            if (User.validate(userMe)) {
                myId = userMe.getId();
                rChatDb = Helper.getRealmInstance();

                if (MainActivity.myUsers.size() > 0) {
                    for (User user : MainActivity.myUsers) {
                        registerStatusUpdates(user);
                    }

                }
            }
        }

    }

    private void initVars() {
        helper = new Helper(context);
        Realm.init(context);
        userMe = helper.getLoggedInUser();

    }

    private void registerStatusUpdates(User user) {
        BaseApplication.getStatusRef().child(user.getId()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                try {
                    Log.e("start", "start");
                    final MessageNewArrayList messageNewArrayList = dataSnapshot.getValue(MessageNewArrayList.class);
                    rChatDb.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            MessageNewArrayList message = messageNewArrayList;
                            StatusNew statusQuery =
                                    rChatDb.where(StatusNew.class).equalTo("userId", message.getSenderId()).findFirst();
                            if (statusQuery == null) {
                                StatusImageNew statusImage = rChatDb.createObject(StatusImageNew.class);
                                statusImage.setAttachmentType(message.getAttachmentType());
                                AttachmentList attachment1 = rChatDb.createObject(AttachmentList.class);
                                attachment1.setBytesCount(message.getAttachment().getBytesCount());
                                attachment1.setData(message.getAttachment().getData());
                                attachment1.setName(message.getAttachment().getName());
                                                   /* RealmList<StatusImageList> realmList = new RealmList<>();
                                                    for (int i = 0; i < message.getAttachment().getUrlList().size(); i++) {
                                                        realmList.add(message.getAttachment().getUrlList().get(i));
                                                    }*/
                                RealmList<StatusImageList> realmList = new RealmList<>();
                                for (int i = 0; i < message.getAttachment().getUrlList().size(); i++) {
                                    StatusImageList statusImageList = rChatDb.createObject(StatusImageList.class);
                                    statusImageList.setUrl(message.getAttachment().getUrlList().get(i).getUrl());
                                    statusImageList.setExpiry(message.getAttachment().getUrlList().get(i).isExpiry());
                                    statusImageList.setUploadTime(message.getAttachment().getUrlList().get(i).getUploadTime());
                                    realmList.add(statusImageList);
                                }

                                attachment1.setUrlList(realmList);
                                statusImage.setAttachment(attachment1);
                                statusImage.setBody(message.getBody());
                                statusImage.setDate(message.getDate());
                                statusImage.setSenderId(message.getSenderId());
                                statusImage.setSenderName(message.getSenderName());
                                statusImage.setSent(false);
                                statusImage.setDelivered(false);
                                statusImage.setId(message.getId());

                                StatusNew status = rChatDb.createObject(StatusNew.class);
                                status.getStatusImages().add(statusImage);
                                status.setLastMessage(message.getBody());
                                status.setMyId(message.getId());
                                status.setUser(rChatDb.copyToRealm(user));
                                status.setUserId(message.getSenderId());
                                status.setTimeUpdated(message.getDate());
                                status.setLastMessage(message.getBody());
                            } else {
                                AttachmentList attachment1 = statusQuery.getStatusImages().get(0).getAttachment();
                                                   /* RealmList<StatusImageList> realmList = new RealmList<>();
                                                    for (int i = 0; i < message.getAttachment().getUrlList().size(); i++) {
                                                        realmList.add(message.getAttachment().getUrlList().get(i));
                                                    }*/
                                RealmList<StatusImageList> realmList = new RealmList<>();
                                for (int i = 0; i < message.getAttachment().getUrlList().size(); i++) {
                                    StatusImageList statusImageList = rChatDb.createObject(StatusImageList.class);
                                    statusImageList.setUrl(message.getAttachment().getUrlList().get(i).getUrl());
                                    statusImageList.setExpiry(message.getAttachment().getUrlList().get(i).isExpiry());
                                    statusImageList.setUploadTime(message.getAttachment().getUrlList().get(i).getUploadTime());
                                    realmList.add(statusImageList);
                                }
                                attachment1.setUrlList(realmList);
                                attachment1.setName(message.getAttachment().getName());
                                statusQuery.getStatusImages().get(0).setAttachment(attachment1);
                                statusQuery.setTimeUpdated(message.getDate());
                                statusQuery.setStatusImages(statusQuery.getStatusImages());

                            }
                            //    Log.e("i==>>", "" + i);
                        }
                    });


                    Log.e("end", "end");
                } catch (Exception ex) {
                    Log.e("Status==>>", ex.getMessage());
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d("onChildChanged=>>", "onChildChanged");
                try {
                    final MessageNewArrayList message = dataSnapshot.getValue(MessageNewArrayList.class);
                    //    StatusNew statusQuery1 = rChatDb.where(StatusNew.class).equalTo("userId", message.getSenderId()).findFirst();
                    // if (message.getAttachment().getUrlList().size() !=
                    //          statusQuery1.getStatusImages().get(0).getAttachment().getUrlList().size()) {
                    //------------
                    rChatDb.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            StatusNew statusQuery = rChatDb.where(StatusNew.class).equalTo("userId", message.getSenderId()).findFirst();
                            if (statusQuery == null) {
                                StatusImageNew statusImage = rChatDb.createObject(StatusImageNew.class);
                                statusImage.setAttachmentType(message.getAttachmentType());
                                AttachmentList attachment1 = rChatDb.createObject(AttachmentList.class);
                                attachment1.setBytesCount(message.getAttachment().getBytesCount());
                                attachment1.setData(message.getAttachment().getData());
                                attachment1.setName(message.getAttachment().getName());
                                                /*RealmList<StatusImageList> realmList = new RealmList<>();
                                                for (int i = 0; i < message.getAttachment().getUrlList().size(); i++) {
                                                    realmList.add(message.getAttachment().getUrlList().get(i));
                                                }*/
                                RealmList<StatusImageList> realmList = new RealmList<>();
                                for (int i = 0; i < message.getAttachment().getUrlList().size(); i++) {
                                    StatusImageList statusImageList = rChatDb.createObject(StatusImageList.class);
                                    statusImageList.setUrl(message.getAttachment().getUrlList().get(i).getUrl());
                                    statusImageList.setExpiry(message.getAttachment().getUrlList().get(i).isExpiry());
                                    statusImageList.setUploadTime(message.getAttachment().getUrlList().get(i).getUploadTime());
                                    realmList.add(statusImageList);
                                }
                                attachment1.setUrlList(realmList);
                                statusImage.setAttachment(attachment1);
                                statusImage.setBody(message.getBody());
                                statusImage.setDate(message.getDate());
                                statusImage.setSenderId(message.getSenderId());
                                statusImage.setSenderName(message.getSenderName());
                                statusImage.setSent(false);
                                statusImage.setDelivered(false);
                                statusImage.setId(message.getId());

                                StatusNew status = rChatDb.createObject(StatusNew.class);
                                status.getStatusImages().add(statusImage);
                                status.setLastMessage(message.getBody());
                                status.setMyId(message.getId());
                                status.setUser(rChatDb.copyToRealm(user));
                                status.setUserId(message.getSenderId());
                                status.setTimeUpdated(message.getDate());
                                status.setLastMessage(message.getBody());
                            } else {
                                AttachmentList attachment1 = statusQuery.getStatusImages().get(0).getAttachment();
                                               /* RealmList<StatusImageList> realmList = new RealmList<>();
                                                for (int i = 0; i < message.getAttachment().getUrlList().size(); i++) {
                                                    realmList.add(message.getAttachment().getUrlList().get(i));
                                                }*/
                                RealmList<StatusImageList> realmList = new RealmList<>();
                                Log.e("size", "" + message.getAttachment().getUrlList().size());
                                for (int i = 0; i < message.getAttachment().getUrlList().size(); i++) {
                                    StatusImageList statusImageList = rChatDb.createObject(StatusImageList.class);
                                    statusImageList.setUrl(message.getAttachment().getUrlList().get(i).getUrl());
                                    statusImageList.setExpiry(message.getAttachment().getUrlList().get(i).isExpiry());
                                    statusImageList.setUploadTime(message.getAttachment().getUrlList().get(i).getUploadTime());
                                    realmList.add(statusImageList);
                                }
                                attachment1.setUrlList(realmList);
                                attachment1.setName(message.getAttachment().getName());
                                statusQuery.getStatusImages().get(0).setAttachment(attachment1);
                                statusQuery.setTimeUpdated(message.getDate());
                                statusQuery.setStatusImages(statusQuery.getStatusImages());
                            }
                        }
                    });

                    //  break;
                    //-------------------
                    //  }
                } catch (Exception ex) {
                    Log.e("Status==>>", ex.getMessage());
                }

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
