package com.hermes.chat.services;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.util.Log;
import android.util.Patterns;

import com.hermes.chat.BaseApplication;
import com.hermes.chat.models.Contact;
import com.hermes.chat.models.User;
import com.hermes.chat.utils.Helper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class FetchMyUsersService {
    private ArrayList<Contact> myContacts;
    private ArrayList<User> myUsers, finalUserList;
    private String myId;
    public static boolean STARTED = false;
    private Context context;

    public FetchMyUsersService(Context context, String myId) {
        try {
            STARTED = true;
            this.myId = myId;
            this.context = context;
            /*context.getContentResolver().registerContentObserver
                    (ContactsContract.Contacts.CONTENT_URI, true, new MyContentObserver());*/
//            fetchMyContacts();
            registerUserUpdates();
//            broadcastMyContacts();
            STARTED = false;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void broadcastMyUsers() {
        if (this.finalUserList != null) {
            Intent intent = new Intent(Helper.BROADCAST_MY_USERS);
            intent.putParcelableArrayListExtra("data", this.finalUserList);
            LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
            localBroadcastManager.sendBroadcast(intent);
        }
    }

    private void broadcastMyContacts() {
        if (this.finalUserList != null) {
            Intent intent = new Intent(Helper.BROADCAST_MY_CONTACTS);
            intent.putParcelableArrayListExtra("data", this.myContacts);
            LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
            localBroadcastManager.sendBroadcast(intent);
        }
    }

    private void fetchMyContacts() {
        myContacts = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);
        if (cursor != null && !cursor.isClosed()) {
            cursor.getCount();
            while (cursor.moveToNext()) {
                int hasPhoneNumber = cursor.getInt(cursor.getColumnIndex(ContactsContract.
                        Contacts.HAS_PHONE_NUMBER));
                if (hasPhoneNumber == 1) {
                    String number = cursor.getString(cursor.getColumnIndex(ContactsContract.
                            CommonDataKinds.Phone.NUMBER)).replaceAll("\\s+", "");
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.
                            Contacts.DISPLAY_NAME_PRIMARY));
                    if (Patterns.PHONE.matcher(number).matches()) {
                        boolean hasPlus = String.valueOf(number.charAt(0)).equals("+");
                        number = number.replaceAll("[\\D]", "");
                        if (hasPlus) {
                            number = "+" + number;
                        }
                        Contact contact = new Contact(number, name);
                        if (!myContacts.contains(contact))
                            myContacts.add(contact);
                    }
                }
            }
            cursor.close();
        }
        registerUserUpdates();
    }

    private void registerUserUpdates() {
        try {
            myUsers = new ArrayList<>();
            BaseApplication.getUserRef().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String print = "";
                    User user;
                    ArrayList<User> users= new ArrayList<>();
                    ArrayList<String> connectList = new ArrayList<>();
                    finalUserList = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        try {
                            user = snapshot.getValue(User.class);
                            if (user.getId() != null){

                                users.add(user);
                                if(user.getId().equals(myId)){
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

                    finalUserList.addAll(myUsers);
                    Collections.sort(finalUserList, new Comparator<User>() {
                        @Override
                        public int compare(User user1, User user2) {
                            return user1.getNameToDisplay().compareToIgnoreCase(user2.getNameToDisplay());
                        }
                    });
                    broadcastMyUsers();


                }
               /* public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        try {
                            User user = snapshot.getValue(User.class);
                            myUsers.add(user);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    finalUserList = new ArrayList<>();
                    for (Contact savedContact : new ArrayList<>(myContacts)) {
                        for (User user : myUsers) {
                            if (user != null && user.getId() != null && !user.getId().equals(myId)) {
                                if (Helper.contactMatches(user.getId(), savedContact.getPhoneNumber())) {
                                    user.setNameInPhone(savedContact.getName());
                                    finalUserList.add(user);
                                    break;
                                }
                            }
                        }
                    }
                    Collections.sort(finalUserList, new Comparator<User>() {
                        @Override
                        public int compare(User user1, User user2) {
                            return user1.getNameToDisplay().compareToIgnoreCase(user2.getNameToDisplay());
                        }
                    });
                    broadcastMyUsers();
                }*/

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private class MyContentObserver extends ContentObserver {
        public MyContentObserver() {
            super(null);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            Log.d("", "A change has happened");
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            Log.d("", uri.toString());
//            fetchMyContacts();
//            broadcastMyContacts();
        }
    }
}
