package com.hermes.chat.services;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.hermes.chat.BaseApplication;
import com.hermes.chat.R;
import com.hermes.chat.activities.ChatActivity;
import com.hermes.chat.models.Attachment;
import com.hermes.chat.models.AttachmentTypes;
import com.hermes.chat.models.Chat;
import com.hermes.chat.models.Group;
import com.hermes.chat.models.Message;
import com.hermes.chat.models.MessageNewArrayList;
import com.hermes.chat.models.MyString;
import com.hermes.chat.models.StatusNew;
import com.hermes.chat.models.User;
import com.hermes.chat.models.solochat;
import com.hermes.chat.pushnotification.Aps;
import com.hermes.chat.pushnotification.Data;
import com.hermes.chat.pushnotification.SendFirebaseNotification;
import com.hermes.chat.utils.FirebaseUploader;
import com.hermes.chat.utils.Helper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;

public class FirebaseChatService {
    private static final String CHANNEL_ID_GROUP = "my_channel_02";
    private static final String CHANNEL_ID_USER = "my_channel_03";

    private Helper helper;
    private String myId, msgUserID = "";
    private Realm rChatDb;
    public static HashMap<String, User> userHashMap = new HashMap<>();
    private HashMap<String, Group> groupHashMap = new HashMap<>();
    ;
    private User userMe;
    String replyId = "0";
    private int i = 0;
    private Context context;
    public static boolean isChatServiceStarted = false;

    public FirebaseChatService() {
    }

    public FirebaseChatService(Context context) {
        if (!User.validate(userMe)) {
            isChatServiceStarted = true;
            this.context = context;
            if (!User.validate(userMe)) {
                initVars();
                if (User.validate(userMe)) {
                    initVars();
                    myId = userMe.getId();
                    rChatDb = Helper.getRealmInstance();
                    registerUserUpdates();
                    registerGroupUpdates();
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS)
                            == PackageManager.PERMISSION_GRANTED) {
                        if (!FetchMyUsersService.STARTED) {
                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            if (firebaseUser != null) {
                                firebaseUser.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                                        if (task.isSuccessful()) {
                                            String idToken = task.getResult().getToken();
                                            new FetchMyUsersService(context, userMe.getId());
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
            }
        }
        LocalBroadcastManager.getInstance(context).registerReceiver(uploadAndSendReceiver,
                new IntentFilter(Helper.UPLOAD_AND_SEND));
        LocalBroadcastManager.getInstance(context).registerReceiver(logoutReceiver,
                new IntentFilter(Helper.BROADCAST_LOGOUT));
    }

    private BroadcastReceiver logoutReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(uploadAndSendReceiver);
        }
    };

    private BroadcastReceiver uploadAndSendReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals(Helper.UPLOAD_AND_SEND)) {
                Group group = null;
                Attachment attachment = intent.getParcelableExtra("attachment");
                if (intent.getParcelableExtra("chatDataGroup") != null) {
                    group = intent.getParcelableExtra("chatDataGroup");
                }
                int type = intent.getIntExtra("attachment_type", -1);
                String attachmentFilePath = intent.getStringExtra("attachment_file_path");
                String attachmentChatChild = intent.getStringExtra("attachment_chat_child");
                String attachmentRecipientId = intent.getStringExtra("attachment_recipient_id");
                replyId = intent.getStringExtra("attachment_reply_id");
                msgUserID = intent.getStringExtra("new_msg_id");
//                uploadAndSend(new File(attachmentFilePath), attachment, type, attachmentChatChild,
//                        attachmentRecipientId, group, msgUserID);
                uploadAndSend(new File(attachmentFilePath), attachment, type, attachmentChatChild,
                        attachmentRecipientId, group, msgUserID, intent.getStringExtra("statusUrl"));
            }
        }
    };

    private void initVars() {
        helper = new Helper(context);
        Realm.init(context);
        userMe = helper.getLoggedInUser();
    }


    private void uploadAndSend(final File fileToUpload, final Attachment attachment, final int attachmentType,
                               final String chatChild, final String recipientId, final Group group,
                               final String new_msg_id, final String statusUrl) {
        if (!fileToUpload.exists())
            return;
        final String fileName = Uri.fromFile(fileToUpload).getLastPathSegment();
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child(context.getString(R.string.app_name)).child(AttachmentTypes.getTypeName(attachmentType)).child(fileName);
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                //If file is already uploaded
                Attachment attachment1 = attachment;
                if (attachment1 == null) attachment1 = new Attachment();
                attachment1.setName(fileName);
                attachment1.setUrl(uri.toString());
                attachment1.setBytesCount(fileToUpload.length());
                sendMessage(null, attachmentType, attachment1, chatChild, recipientId, group, new_msg_id, statusUrl);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                //Elase upload and then send message
                FirebaseUploader firebaseUploader = new FirebaseUploader(new FirebaseUploader.UploadListener() {
                    @Override
                    public void onUploadFail(String message) {
                        Log.e("DatabaseException", message);
                    }

                    @Override
                    public void onUploadSuccess(String downloadUrl) {
                        Attachment attachment1 = attachment;
                        if (attachment1 == null) attachment1 = new Attachment();
                        attachment1.setName(fileToUpload.getName());
                        attachment1.setUrl(downloadUrl);
                        attachment1.setBytesCount(fileToUpload.length());
                        sendMessage(null, attachmentType, attachment1, chatChild, recipientId, group, new_msg_id, statusUrl);
                    }

                    @Override
                    public void onUploadProgress(int progress) {

                    }

                    @Override
                    public void onUploadCancelled() {

                    }
                }, storageReference);
                firebaseUploader.uploadOthers(context, fileToUpload);
            }
        });
    }

    private void sendMessage(String messageBody, @AttachmentTypes.AttachmentType int attachmentType,
                             Attachment attachment, String chatChild, String userOrGroupId, Group group,
                             String new_msg_id, String statusUrl) {
        //Create message object
        String body = "";
        String[] registration_ids = new String[]{};
        Helper.deleteMessageFromRealm(rChatDb, new_msg_id);
        Message message = new Message();
        message.setAttachmentType(attachmentType);
        if (attachmentType != AttachmentTypes.NONE_TEXT)
            message.setAttachment(attachment);
        message.setBody(messageBody);
        message.setDate(System.currentTimeMillis());
        message.setSenderId(userMe.getId());
        message.setSenderName(userMe.getName());
        message.setSent(true);
        message.setDelivered(false);
        message.setRecipientId(userOrGroupId);
        message.setId(BaseApplication.getChatRef().child(chatChild).push().getKey());
        message.setReplyId(replyId);
        message.setStatusUrl(statusUrl);
        if (attachmentType == AttachmentTypes.CONTACT)
            body = context.getString(R.string.contact);
        else if (attachmentType == AttachmentTypes.VIDEO)
            body = context.getString(R.string.video);
        else if (attachmentType == AttachmentTypes.IMAGE)
            body = context.getString(R.string.image);
        else if (attachmentType == AttachmentTypes.AUDIO)
            body = context.getString(R.string.audio);
        else if (attachmentType == AttachmentTypes.DOCUMENT)
            body = context.getString(R.string.document);
        else if (attachmentType == AttachmentTypes.RECORDING)
            body = context.getString(R.string.recording);

        if (!userOrGroupId.startsWith(Helper.GROUP_PREFIX) && userHashMap.get(userOrGroupId).getBlockedUsersIds() != null
                && userHashMap.get(userOrGroupId).getBlockedUsersIds().contains(userMe.getId())) {
            message.setBlocked(true);
        }

        if (group != null && group.getUserIds() != null) {
            ArrayList<String> userIds = new ArrayList<>();
            for (String user : group.getUserIds()) {
                if (group.getGrpExitUserIds() == null) {
                    userIds.add(user);
                } else if (group.getGrpExitUserIds() != null && !group.getGrpExitUserIds().contains(user))
                    userIds.add(user);
            }
            message.setUserIds(userIds);
            ArrayList<String> readUserIds = new ArrayList<>();
            readUserIds.add(userMe.getId());
            message.setReadUserIds(readUserIds);
            registration_ids = new String[userIds.size()];
            for (int i = 0; i < userIds.size(); i++) {
                if (!userIds.get(i).equalsIgnoreCase(userMe.getId())) {
                    registration_ids[i] = userHashMap.get(userIds.get(i)).getDeviceToken();

                    com.hermes.chat.pushnotification.Notification notification = new com.hermes.chat.pushnotification.Notification(group.getName(), body);
                    Data data = new Data(group.getName(), body,
                            " ");
                    if (userHashMap.get(userIds.get(i)).getOsType() != null && !userHashMap.get(userIds.get(i)).getOsType().isEmpty() &&
                            userHashMap.get(userIds.get(i)).getOsType().equalsIgnoreCase("iOS")) {
                        com.hermes.chat.pushnotification.Message notificationTask =
                                new com.hermes.chat.pushnotification.Message(new String[]{userHashMap.get(userIds.get(i)).getDeviceToken()},
                                        "", data, notification,
                                        new Aps(" ", " "));

                        new SendFirebaseNotification(context, notificationTask).triggerMessage();
                    } else {
                        com.hermes.chat.pushnotification.Message notificationTask =
                                new com.hermes.chat.pushnotification.Message(new String[]{userHashMap.get(userIds.get(i)).getDeviceToken()},
                                        "", data,
                                        new Aps(" ", " "));
                        new SendFirebaseNotification(context, notificationTask).triggerMessage();
                    }

                }
            }
        } else {
            registration_ids = new String[1];
            registration_ids[0] = userHashMap.get(userOrGroupId).getDeviceToken();

            com.hermes.chat.pushnotification.Notification notification = new com.hermes.chat.pushnotification.Notification(userMe.getId(), body);
            Data data = new Data(userMe.getId(), body,
                    " ");
            if (userHashMap.get(userOrGroupId).getOsType() != null && !userHashMap.get(userOrGroupId).getOsType().isEmpty()
                    && userHashMap.get(userOrGroupId).getOsType().equalsIgnoreCase("iOS")) {
                com.hermes.chat.pushnotification.Message notificationTask =
                        new com.hermes.chat.pushnotification.Message(registration_ids,
                                "", data, notification,
                                new Aps(" ", " "));
                if (!message.isBlocked())
                    new SendFirebaseNotification(context, notificationTask).triggerMessage();
            } else {
                com.hermes.chat.pushnotification.Message notificationTask =
                        new com.hermes.chat.pushnotification.Message(registration_ids,
                                "", data,
                                new Aps(" ", " "));
                if (!message.isBlocked())
                    new SendFirebaseNotification(context, notificationTask).triggerMessage();
            }
        }


        //Add messages in chat child
        BaseApplication.getChatRef().child(chatChild).child(message.getId()).setValue(message);
    }

    private void registerGroupUpdates() {
        BaseApplication.getGroupRef().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                try {
                    Group group = dataSnapshot.getValue(Group.class);
                    if (Group.validate(group) && group.getUserIds().contains(myId)) {
                        if (!groupHashMap.containsKey(group.getId())) {
                            groupHashMap.put(group.getId(), group);
                            broadcastGroup("added", group);
                            checkAndNotify(group);
                            registerChatUpdates(true, group.getId());
                        }
                    }
                } catch (Exception ex) {
                    Log.e("GROUP", "invalid group");
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                try {
                    Group group = dataSnapshot.getValue(Group.class);
                    if (Group.validate(group)) {
                        if (group.getUserIds().contains(myId)) {
                            if (!groupHashMap.containsKey(group.getId()))
                                groupHashMap.put(group.getId(), group);
                            registerChatUpdates(true, group.getId());
                            broadcastGroup("changed", group);
                            updateGroupInDb(group);
                        } else if (groupHashMap.containsKey(group.getId())) {
                            registerChatUpdates(false, group.getId());
                            groupHashMap.remove(group.getId());
                            broadcastGroup("changed", group);
                            updateGroupInDb(group);
                        }
                    }
                } catch (Exception ex) {
                    Log.e("GROUP", "invalid group");
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.e("GROUP", "GROUPRemoved");
                try {
                    Group group = dataSnapshot.getValue(Group.class);
                    if (groupHashMap.containsKey(group.getId())) {
                        registerChatUpdates(false, group.getId());
                        groupHashMap.remove(group.getId());
                        broadcastGroup("changed", group);
                        if (group != null && group.getId() != null) {
                            Helper.deleteGroupFromRealm(rChatDb, group.getId());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void checkAndNotify(final Group group) {
        //rChatDb.beginTransaction();
        rChatDb.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Chat thisGroupChat = rChatDb.where(Chat.class)
                        .equalTo("myId", myId)
                        .equalTo("groupId", group.getId()).findFirst();
                if (thisGroupChat == null) {
                    if (!group.getUserIds().get(0).equals(new MyString(myId)) && !helper.getSharedPreferenceHelper().getBooleanPreference(Helper.GROUP_NOTIFIED, false)) {
                        notifyNewGroup(group);
                        helper.getSharedPreferenceHelper().setBooleanPreference(Helper.GROUP_NOTIFIED, true);
                    }
                    thisGroupChat = rChatDb.createObject(Chat.class);
                    thisGroupChat.setGroup(rChatDb.copyToRealm(groupHashMap.get(group.getId())));
                    thisGroupChat.setGroupId(group.getId());
                    thisGroupChat.setMessages(new RealmList<Message>());
                    thisGroupChat.setMyId(myId);
                    thisGroupChat.setRead(false);
                    long millis = System.currentTimeMillis();
                    thisGroupChat.setLastMessage("Created on " + Helper.getDateTime(millis));
                    thisGroupChat.setTimeUpdated(millis);
                }
            }
        });

        //rChatDb.commitTransaction();
    }

    private void notifyNewGroup(Group group) {
        // Construct the Intent you want to end up at
        Intent chatActivity = ChatActivity.newIntent(context, null, group);
        // Construct the PendingIntent for your Notification
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // This uses android:parentActivityName and
        // android.support.PARENT_ACTIVITY meta-data by default
        stackBuilder.addNextIntentWithParentStack(chatActivity);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(99, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID_GROUP, "new group notification", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
            notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID_GROUP);
        } else {
            notificationBuilder = new NotificationCompat.Builder(context);
        }

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notificationBuilder.setSmallIcon(R.drawable.ic_logo_)
                .setContentTitle("Group: " + group.getName())
                .setContentText("You have been added to new group called " + group.getName())
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        int msgId = Integer.parseInt(group.getId().substring(group.getId().length() - 4, group.getId().length() - 1));
        notificationManager.notify(msgId, notificationBuilder.build());
    }

    private void registerUserUpdates() {
        BaseApplication.getUserRef().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                try {
                    User user = dataSnapshot.getValue(User.class);
                    if (User.validate(user)) {
                        if (!userHashMap.containsKey(user.getId())) {
                            userHashMap.put(user.getId(), user);
                            broadcastUser("added", user);
                            registerChatUpdates(true, user.getId());
                        }
                    }
                } catch (Exception ex) {
                    Log.e("USER", "invalid user");
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                try {
                    User user = dataSnapshot.getValue(User.class);
                    if (User.validate(user)) {
                        if (userHashMap.containsKey(user.getId()))
                            userHashMap.put(user.getId(), user);
                        broadcastUser("changed", user);
                        updateUserInDb(user);
                    }
                } catch (Exception ex) {
                    Log.e("USER", "invalid user");
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


    private void updateUserInDb(final User value) {
//update in database
        if (!TextUtils.isEmpty(myId)) {
            if (myId.equalsIgnoreCase(value.getId())) {
                final Chat chat = rChatDb.where(Chat.class).equalTo("myId", myId).findFirst();
                if (chat.getUser() != null) {
                    rChatDb.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            ArrayList<solochat> solochats = new ArrayList<>();
                            solochats.addAll(value.getSolochat());
                            chat.getUser().setSolochat(solochats);
// helper.setLoggedInUser(chat.getUser());
                        }
                    });
                }
// User updated = rChatDb.copyToRealm(value);
// chat.setUser(updated);
            } else {
                final Chat chat = rChatDb.where(Chat.class).equalTo("myId", myId).equalTo("userId", value.getId()).findFirst();
                if (chat != null) {
// rChatDb.beginTransaction();
                    rChatDb.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            User updated = rChatDb.copyToRealm(value);
                            updated.setNameInPhone(chat.getUser().getNameInPhone());
                            chat.setUser(updated);
                        }
                    });

//rChatDb.commitTransaction();
                }
                final StatusNew statusQuery =
                        rChatDb.where(StatusNew.class).equalTo("userId", value.getId()).findFirst();
                if (statusQuery != null) {
                    rChatDb.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            User updated = rChatDb.copyToRealm(value);
                            updated.setNameInPhone(statusQuery.getUser().getNameInPhone());
                            statusQuery.setUser(updated);
                        }
                    });
                }
            }

            final Chat chat = rChatDb.where(Chat.class).equalTo("myId", myId).findFirst();
            if (chat.getUser() != null) {
                rChatDb.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

                        ArrayList<String> blockedUsers = new ArrayList<>();
                        blockedUsers.addAll(value.getBlockedUsersIds());
                        chat.getUser().setBlockedUsersIds(blockedUsers);
                    }
                });
            }

        }
    }

    private void updateGroupInDb(final Group group) {
        if (!TextUtils.isEmpty(myId)) {
            final Chat chat = rChatDb.where(Chat.class).equalTo("myId", myId).equalTo("groupId", group.getId()).findFirst();
            if (chat != null) {
                //rChatDb.beginTransaction();
                rChatDb.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        if (group.getUserIds() != null && group.getUserIds().contains(userMe.getId())) {
                            chat.setGroup(rChatDb.copyToRealm(group));
                        } else {
                            chat.setGroup(null);
                        }
                    }
                });

                // rChatDb.commitTransaction();
            }
        }
    }

    private void registerChatUpdates(boolean register, String id) {
        if (!TextUtils.isEmpty(myId) && !TextUtils.isEmpty(id)) {
            // DatabaseReference idChatRef = BaseApplication.getChatRef().child(id.startsWith(Helper.GROUP_PREFIX) ? id : Helper.getChatChild(myId, id));
            DatabaseReference idChatRef = BaseApplication.getChatRef().child(id.startsWith(Helper.GROUP_PREFIX) ? id : myId + "-" + id);
            DatabaseReference idChatRef1 = BaseApplication.getChatRef().child(id.startsWith(Helper.GROUP_PREFIX) ? id : id + "-" + myId);
            if (register) {
                idChatRef.addChildEventListener(chatUpdateListener);
                idChatRef1.addChildEventListener(chatUpdateListener);
            } else {
                idChatRef.removeEventListener(chatUpdateListener);
                idChatRef1.removeEventListener(chatUpdateListener);
            }
        }
    }


    private ChildEventListener chatUpdateListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            try {
                //  if (!dataSnapshot.getKey().equalsIgnoreCase("chatDelete")) {
                Message message = dataSnapshot.getValue(Message.class);

           /* if (message.isDelivered() || (message.getRecipientId().startsWith(Helper.GROUP_PREFIX) && !groupHashMap.containsKey(message.getRecipientId())))
                return;  */
                if (message != null && message.getId() != null) {
                    Message result = rChatDb.where(Message.class).equalTo("id", message.getId()).findFirst();
                    if (result == null && !TextUtils.isEmpty(myId) && helper.isLoggedIn()) {
                        if (!message.getRecipientId().startsWith(Helper.GROUP_PREFIX) && message.isBlocked()
                                && message.getSenderId().equalsIgnoreCase(userMe.getId()))
                            saveMessage(message);
                        else if (!message.getRecipientId().startsWith(Helper.GROUP_PREFIX) && !message.isBlocked())
                            saveMessage(message);
                        else if (message.getRecipientId().startsWith(Helper.GROUP_PREFIX))
                            saveMessage(message);

                        if (!message.getRecipientId().startsWith(Helper.GROUP_PREFIX) &&
                                !message.getSenderId().equals(myId) && !message.isDelivered() && !message.isBlocked())
                            BaseApplication.getChatRef().child(dataSnapshot.getRef().getParent().getKey())
                                    .child(message.getId()).child("delivered").setValue(true);
                    /*saveMessage(message);
                    if (!message.getRecipientId().startsWith(Helper.GROUP_PREFIX) && !message.getSenderId().equals(myId) && !message.isDelivered())
                        BaseApplication.getChatRef().child(dataSnapshot.getRef().getParent().getKey()).child(message.getId()).child("delivered").setValue(true);*/
                    }
                }
                // }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            try {


                //  if (!dataSnapshot.getKey().equalsIgnoreCase("chatDelete")) {
                final Message message = dataSnapshot.getValue(Message.class);
                if (message != null && message.getId() != null) {
                    final Message result = rChatDb.where(Message.class).equalTo("id", message.getId()).findFirst();
                    if (result != null) {
                        //  rChatDb.beginTransaction();
                        rChatDb.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                result.setReadMsg(message.isReadMsg());
                                result.setDelivered(message.isDelivered());
                                result.setDelete(message.getDelete());
                                if (message.getUserIds() != null) {
                                    ArrayList<String> userIds = new ArrayList<>();
                                    userIds.addAll(message.getUserIds());
                                    result.setUserIds(userIds);
                                }
                                if (message.getReadUserIds() != null) {
                                    ArrayList<String> userIds = new ArrayList<>();
                                    userIds.addAll(message.getReadUserIds());
                                    result.setReadUserIds(userIds);
                                }
                            }
                        });

                        // rChatDb.commitTransaction();

                    }
                }
                //}
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            try {
                // if (!dataSnapshot.getKey().equalsIgnoreCase("chatDelete")) {
                Message message = dataSnapshot.getValue(Message.class);
                if (message != null && message.getId() != null) {
                    Helper.deleteMessageFromRealm(rChatDb, message.getId());

                    String userOrGroupId = myId.equals(message.getSenderId()) ? message.getRecipientId() : message.getSenderId();
                    final Chat chat = Helper.getChat(rChatDb, myId, userOrGroupId).findFirst();
                    if (chat != null) {
                        //rChatDb.beginTransaction();
                        rChatDb.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                RealmList<Message> realmList = chat.getMessages();
                                if (realmList.size() == 0)
                                    RealmObject.deleteFromRealm(chat);
                                else {
                                    chat.setLastMessage(realmList.get(realmList.size() - 1).getBody());
                                    chat.setTimeUpdated(realmList.get(realmList.size() - 1).getDate());
                                }
                            }
                        });

                        // rChatDb.commitTransaction();
                    }
                }
                ///  }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    //Status

    private void registerStatusUpdates(boolean register, String id) {
        if (!TextUtils.isEmpty(myId) && !TextUtils.isEmpty(id)) {
            DatabaseReference idStatusRef = BaseApplication.getStatusRef().child(id);
            if (register) {
                idStatusRef.addChildEventListener(chatStatusListener);
            } else {
                idStatusRef.removeEventListener(chatStatusListener);
            }
        }
    }

    private ChildEventListener chatStatusListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            final MessageNewArrayList messageNewArrayList = dataSnapshot.getValue(MessageNewArrayList.class);

        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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
    };

    private void updateMessage(Message message) {
        String userOrGroupId = message.getRecipientId().startsWith(Helper.GROUP_PREFIX) ?
                message.getRecipientId() : myId.equals(message.getSenderId()) ? message.getRecipientId()
                : message.getSenderId();
        Chat chat = Helper.getChat(rChatDb, myId, userOrGroupId).findFirst();
        if (chat == null) {
            chat = rChatDb.createObject(Chat.class);
            if (userOrGroupId.startsWith(Helper.GROUP_PREFIX)) {
                chat.setGroup(rChatDb.copyToRealm(groupHashMap.get(userOrGroupId)));
                chat.setGroupId(userOrGroupId);
                chat.setUser(null);
                chat.setUserId(null);
            } else {
                chat.setUser(rChatDb.copyToRealm(userHashMap.get(userOrGroupId)));
                chat.setUserId(userOrGroupId);
                chat.setGroup(null);
                chat.setGroupId(null);
            }
            RealmList<Message> msgList = new RealmList<>();
            msgList.add(message);
            chat.setMessages(msgList);
            chat.setLastMessage(message.getBody());
            chat.setMyId(myId);
            chat.setTimeUpdated(message.getDate());
        }
    }

    private void saveMessage(final Message message) {
        if (message.getAttachment() != null && !TextUtils.isEmpty(message.getAttachment().getUrl())
                && !TextUtils.isEmpty(message.getAttachment().getName())) {
            String idToCompare = "loading" + message.getAttachment().getBytesCount() + message.getAttachment().getName();
//            String idToCompare = msgUserID;
            Helper.deleteMessageFromRealm(rChatDb, idToCompare);
        }

        final String userOrGroupId = message.getRecipientId()
                .startsWith(Helper.GROUP_PREFIX) ? message.getRecipientId() : myId.equals(message.getSenderId()) ? message.getRecipientId() : message.getSenderId();
        final Chat[] chat = {Helper.getChat(rChatDb, myId, userOrGroupId).findFirst()};
        if (userMe != null && userMe.getSolochat().size() > 0 && !userOrGroupId.startsWith(Helper.GROUP_PREFIX)) {
            ArrayList<String> chatSoloIds = new ArrayList<>();
            for (int i = 0; i < userMe.getSolochat().size(); i++) {
                chatSoloIds.add(userMe.getSolochat().get(i).getPhoneNo());
            }
            for (solochat soloChatIds : userMe.getSolochat()) {
                if (userOrGroupId.contains(soloChatIds.getPhoneNo()) &&
                        message.getDate() > soloChatIds.getTimeStamp()) {
                    storeInLocalDB(message, userOrGroupId, chat);
                } /*else if (!chatSoloIds.contains(userOrGroupId)) {
                    storeInLocalDB(message, userOrGroupId, chat);
                }*/
            }
            if (!chatSoloIds.contains(userOrGroupId)) {
                storeInLocalDB(message, userOrGroupId, chat);
            }
        } else {
            storeInLocalDB(message, userOrGroupId, chat);
        }
    }

    private void storeInLocalDB(final Message message, final String userOrGroupId, final Chat[] chat) {
        rChatDb.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                if (chat[0] == null) {
                    chat[0] = rChatDb.createObject(Chat.class);
                    if (userOrGroupId.startsWith(Helper.GROUP_PREFIX)) {
                        chat[0].setGroup(rChatDb.copyToRealm(groupHashMap.get(userOrGroupId)));
                        chat[0].setGroupId(userOrGroupId);
                        chat[0].setUser(null);
                        chat[0].setUserId(null);
                        //!message.getGrpDeletedMsgIds().contains(myId) &&
                        if (message.getUserIds() != null
                                && message.getUserIds().contains(userMe.getId())) {
                            chat[0].setLastMessage(message.getBody());
                        }
                    } else {
                        chat[0].setUser(rChatDb.copyToRealm(userHashMap.get(userOrGroupId)));
                        chat[0].setUserId(userOrGroupId);
                        chat[0].setGroup(null);
                        chat[0].setGroupId(null);
                        if (!message.getDelete().contains(myId)) {
                            chat[0].setLastMessage(message.getBody());
                        }
                    }
                    chat[0].setMessages(new RealmList<Message>());
                    chat[0].setMyId(myId);
                    chat[0].setTimeUpdated(message.getDate());
                }

                if (!message.getSenderId().equals(myId))
                    chat[0].setRead(false);
                chat[0].setTimeUpdated(message.getDate());
                chat[0].getMessages().add(message);
                if (userOrGroupId.startsWith(Helper.GROUP_PREFIX)) {
                    //message.getGrpDeletedMsgIds() == null &&
                    if (message.getUserIds() != null
                            && message.getUserIds().contains(userMe.getId())) {
                        chat[0].setLastMessage(message.getBody());
                    } else if (message.getUserIds() != null
                            && message.getUserIds().contains(userMe.getId())) {
                        //message.getGrpDeletedMsgIds() != null && !message.getGrpDeletedMsgIds().contains(myId) &&
                        chat[0].setLastMessage(message.getBody());
                    }
                } else if (!message.getDelete().contains(myId)) {
                    chat[0].setLastMessage(message.getBody());
                }
//                chat[0].setLastMessage(message.getBody());
            }
        });

        // rChatDb.commitTransaction();
       /* if (userOrGroupId.startsWith(Helper.GROUP_PREFIX) && message.getUserIds() != null && message.getUserIds().contains(userMe.getId())) {
            showNotifications(message, userOrGroupId, chat);
        } else if (!userOrGroupId.startsWith(Helper.GROUP_PREFIX) && message.getUserIds() == null) {
            showNotifications(message, userOrGroupId, chat);
        }*/

        /*if (userOrGroupId.startsWith(Helper.GROUP_PREFIX) && message.getUserIds() != null && message.getUserIds().contains(userMe.getId())) {
            showNotifications(message, userOrGroupId, chat);
        } else if (!userOrGroupId.startsWith(Helper.GROUP_PREFIX) && message.getUserIds() == null && !message.isBlocked()) {
            showNotifications(message, userOrGroupId, chat);
        }*/
    }

    private void showNotifications(Message message, String userOrGroupId, Chat[] chat) {
        if (!message.isDelivered() && !message.getSenderId().equals(myId)
                && !helper.isUserMute(message.getSenderId())
                && (Helper.CURRENT_CHAT_ID == null
                || !Helper.CURRENT_CHAT_ID.equals(userOrGroupId))) {
            // Construct the Intent you want to end up at
            Intent chatActivity = null;// = ChatActivity.newIntent(this, null,  ? chat.getGroup() : chat.getUser());
            if (userOrGroupId.startsWith(Helper.GROUP_PREFIX))
                chatActivity = ChatActivity.newIntent(context, null, chat[0].getGroup());
            else
                chatActivity = ChatActivity.newIntent(context, null, chat[0].getUser());
            // Construct the PendingIntent for your Notification
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            // This uses android:parentActivityName and
            // android.support.PARENT_ACTIVITY meta-data by default
            stackBuilder.addNextIntentWithParentStack(chatActivity);
            PendingIntent pendingIntent = stackBuilder.getPendingIntent(99, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder notificationBuilder = null;
            String channelId = userOrGroupId.startsWith(Helper.GROUP_PREFIX) ? CHANNEL_ID_GROUP : CHANNEL_ID_USER;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId, "New message notification", NotificationManager.IMPORTANCE_DEFAULT);
                if (notificationManager != null) {
                    notificationManager.createNotificationChannel(channel);
                }
                notificationBuilder = new NotificationCompat.Builder(context, channelId);
            } else {
                notificationBuilder = new NotificationCompat.Builder(context);
            }
            String contactname = "";
            if (helper.getCacheMyUsers() != null) {
                if (!userOrGroupId.startsWith(Helper.GROUP_PREFIX) && helper.getCacheMyUsers().containsKey(chat[0].getUser().getName())) {
                    contactname = helper.getCacheMyUsers().get(chat[0].getUser().getName()).getNameToDisplay();
                }
            }
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            notificationBuilder.setSmallIcon(R.drawable.ic_logo_)
                    .setContentTitle(userOrGroupId.startsWith(Helper.GROUP_PREFIX) ? chat[0].getGroup().getName()
                            : contactname.isEmpty() ? chat[0].getUser().getName() : contactname)
                    .setContentText(getContent(message))
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);
            int msgId = 0;
            try {
                msgId = Integer.parseInt(message.getSenderId());
            } catch (NumberFormatException ex) {
                msgId = Integer.parseInt(message.getSenderId().substring(message.getSenderId().length() / 2));
            }
            notificationManager.notify(msgId, notificationBuilder.build());
        }
    }

    private String getContent(Message message) {
        if (message.getAttachmentType() == AttachmentTypes.AUDIO) {
            return context.getString(R.string.audio);
        } else if (message.getAttachmentType() == AttachmentTypes.RECORDING) {
            return context.getString(R.string.recording);
        } else if (message.getAttachmentType() == AttachmentTypes.VIDEO) {
            return context.getString(R.string.video);
        } else if (message.getAttachmentType() == AttachmentTypes.IMAGE) {
            return context.getString(R.string.image);
        } else if (message.getAttachmentType() == AttachmentTypes.CONTACT) {
            return context.getString(R.string.contact);
        } else if (message.getAttachmentType() == AttachmentTypes.LOCATION) {
            return context.getString(R.string.location);
        } else if (message.getAttachmentType() == AttachmentTypes.DOCUMENT) {
            return context.getString(R.string.document);
        } else if (message.getAttachmentType() == AttachmentTypes.NONE_TEXT) {
            return message.getBody();
        }
        return " ";
    }

    private void broadcastUser(String what, User value) {
        Intent intent = new Intent(Helper.BROADCAST_USER);
        intent.putExtra("data", value);
        intent.putExtra("what", what);
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
        localBroadcastManager.sendBroadcast(intent);
    }

    private void broadcastGroup(String what, Group value) {
        Intent intent = new Intent(Helper.BROADCAST_GROUP);
        intent.putExtra("data", value);
        intent.putExtra("what", what);
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
        localBroadcastManager.sendBroadcast(intent);
    }

}
