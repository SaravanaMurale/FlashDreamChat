package com.hermes.chat.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.hermes.chat.BaseApplication;
import com.hermes.chat.R;
import com.hermes.chat.models.Chat;
import com.hermes.chat.models.Country;
import com.hermes.chat.models.LanguageListModel;
import com.hermes.chat.models.Message;
import com.hermes.chat.models.Status;
import com.hermes.chat.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Random;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmQuery;

/**
 * Created by a_man on 5/5/2017.
 */

public class Helper {
    private static final String USER = "USER";
    private static final String USER_MUTE = "USER_MUTE";
    private static final String SEND_OTP = "SEND_OTP";
    private static final String USERNAME = "USERNAME";
    private static final String EMAIL = "EMAIL";
    private static final String PASSWORD = "PASSWORD";
    public static final String BROADCAST_USER = "com.dreamguys.dreamschat.services.USER";
    public static final String BROADCAST_MY_CONTACTS = "com.dreamguys.dreamschat.MY_CONTACTS";
    public static final String BROADCAST_MY_USERS = "com.dreamguys.dreamschat.MY_USERS";
    public static final String BROADCAST_DOWNLOAD_EVENT = "com.dreamguys.dreamschat.DOWNLOAD_EVENT";
    public static final String BROADCAST_GROUP = "com.dreamguys.dreamschat.services.GROUP";
    public static final String BROADCAST_LOGOUT = "com.dreamguys.dreamschat.services.LOGOUT";
    public static final String UPLOAD_AND_SEND = "com.dreamguys.dreamschat.services.UPLOAD_N_SEND";
    public static final String FETCH_MY_USERS = "com.dreamguys.dreamschat.services.FETCH_MY_USERS";
    public static final String BROADCAST_CALLS = "com.chat.booa.services.CALSS";
    public static final String REF_DATA = "data";
    public static final String REF_USER = "users";
    public static final String GROUP_CREATE = "group_create";
    public static final String GROUP_PREFIX = "group";
    public static final String GROUP_NOTIFIED = "group_notified";
    public static final String USER_NAME_CACHE = "usercachemap";
    public static final String REF_CHAT = "chats";
    public static final String REF_GROUP = "groups";
    public static final String REF_STATUS = "status";
    public static final String REF_STATUS_NEW = "userstatus";
    public static final String REF_CALLS = "calls";
    public static final String REF_LANGUAGES = "languages";
    public static final String BROADCAST_STATUS = "com.dreamguys.dreamschat.services.STATUS";
    public static String CURRENT_CHAT_ID;
    public static boolean CHAT_CAB = false;
    public static final String LANGUAGE = "LANGUAGE";


    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;


    private SharedPreferenceHelper sharedPreferenceHelper;
    private Gson gson;
    private HashSet<String> muteUsersSet;
    private HashMap<String, User> myUsersNameInPhoneMap;

    public static final String splash = "splash";
    public static final String loginpage = "loginpage";
    public static final String homepage = "homepage";
    public static final String chatpage = "chatpage";
    public static final String grouppage = "grouppage";
    public static final String profiledetail = "profiledetail";
    public static final String creategrouppage = "creategrouppage";
    public static final String statuspage = "statuspage";
    public static final String callspage = "callspage";
    public static final String settingspage = "settingspage";
    public static final String changepasswordpage = "changepasswordpage";
    public static final String commonPage = "commonPage";
    public static final String schedulepage = "schedulepage";
    public static final String passcodepage = "passcodepage";

    //New...
    //Groups UserIds....
    public static final String userIds = "userIds";

    public Helper(Context context) {
        sharedPreferenceHelper = new SharedPreferenceHelper(context);
        gson = new Gson();
    }

    public static String getDateTime(Long milliseconds) {
        return new SimpleDateFormat("dd MMM hh:mm a", Locale.getDefault()).format(new Date(milliseconds));
    }

    public static String getTime(Long milliseconds) {
        return new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date(milliseconds));
    }

    public static boolean isImage(Context context, String url) {
        return getMimeType(context, url).startsWith("image");
    }

    public static String getMimeType(Context context, Uri uri) {
        String mimeType = null;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = context.getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType;
    }

    public static String getMimeType(Context context, String url) {
        String mimeType;
        Uri uri = Uri.parse(url);
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = context.getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());
        }
        return mimeType;
    }

    public static String getChatChild(String userId, String myId) {
        //example: userId="9" and myId="5" -->> chat child = "5-9"
        String[] temp = {userId, myId};
        Arrays.sort(temp);
        return temp[0] + "-" + temp[1];
    }

    public User getLoggedInUser() {
        String savedUserPref = sharedPreferenceHelper.getStringPreference(USER);
        if (savedUserPref != null)
            return gson.fromJson(savedUserPref, new TypeToken<User>() {
            }.getType());
        return null;
    }

    public void setLoggedInUser(User user) {
        sharedPreferenceHelper.setStringPreference(USER, gson.toJson(user, new TypeToken<User>() {
        }.getType()));
    }

    public void logout() {
        clearPassword();
        clearUserName();
        clearEmail();
        clearPhoneNumberForVerification();
        sharedPreferenceHelper.clearPreference(USER);
        sharedPreferenceHelper.clearPreference(USER_NAME_CACHE);
    }

    public void setPhoneNumberForVerification(String phone) {
        sharedPreferenceHelper.setStringPreference(SEND_OTP, phone);
    }

    public String getPhoneNumberForVerification() {
        return sharedPreferenceHelper.getStringPreference(SEND_OTP);
    }

    public void clearPhoneNumberForVerification() {
        sharedPreferenceHelper.clearPreference(SEND_OTP);
    }

    public void setPassword(String password) {
        sharedPreferenceHelper.setStringPreference(PASSWORD, password);
    }

    public String getPassword() {
        return sharedPreferenceHelper.getStringPreference(PASSWORD);
    }

    public void clearPassword() {
        sharedPreferenceHelper.clearPreference(PASSWORD);
    }

    public void setUserName(String userName) {
        sharedPreferenceHelper.setStringPreference(USERNAME, userName);
    }

    public String getUserName() {
        return sharedPreferenceHelper.getStringPreference(USERNAME);
    }

    public void clearUserName() {
        sharedPreferenceHelper.clearPreference(USERNAME);
    }

    public void setEmail(String email) {
        sharedPreferenceHelper.setStringPreference(EMAIL, email);
    }

    public String getEmail() {
        return sharedPreferenceHelper.getStringPreference(EMAIL);
    }

    public void clearEmail() {
        sharedPreferenceHelper.clearPreference(EMAIL);
    }

    public boolean isLoggedIn() {
        return sharedPreferenceHelper.getStringPreference(USER) != null;
    }

    public void setBackground(boolean type) {
        sharedPreferenceHelper.setBooleanPreference("Background", type);
    }

    public boolean isBackground() {
        return sharedPreferenceHelper.getBooleanPreference("Background", false);
    }

    public void setUserMute(String userId, boolean mute) {
        if (muteUsersSet == null) {
            String muteUsersPref = sharedPreferenceHelper.getStringPreference(USER_MUTE);
            if (muteUsersPref != null) {
                muteUsersSet = gson.fromJson(muteUsersPref, new TypeToken<HashSet<String>>() {
                }.getType());
            } else {
                muteUsersSet = new HashSet<>();
            }
        }

        if (mute)
            muteUsersSet.add(userId);
        else
            muteUsersSet.remove(userId);

        sharedPreferenceHelper.setStringPreference(USER_MUTE, gson.toJson(muteUsersSet, new TypeToken<HashSet<String>>() {
        }.getType()));
    }

    public boolean isUserMute(String userId) {
        String muteUsersPref = sharedPreferenceHelper.getStringPreference(USER_MUTE);
        if (muteUsersPref != null) {
            HashSet<String> muteUsersSet = gson.fromJson(muteUsersPref, new TypeToken<HashSet<String>>() {
            }.getType());
            return muteUsersSet.contains(userId);
        } else {
            return false;
        }
    }

    public static void loadUrl(Context context, String url) {
        Uri uri = Uri.parse(url);
// create an intent builder
        CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();
// Begin customizing
// set toolbar colors
        intentBuilder.setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimary));
        intentBuilder.setSecondaryToolbarColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));

        intentBuilder.addDefaultShareMenuItem();
        intentBuilder.enableUrlBarHiding();
// build custom tabs intent
        CustomTabsIntent customTabsIntent = intentBuilder.build();
// launch the url
        customTabsIntent.launchUrl(context, uri);
    }

    public static boolean contactMatches(String userPhone, String phoneNumber) {
        if (userPhone.length() < 8 || phoneNumber.length() < 8)
            return false;
//        String reverseUserNumber = new StringBuffer(userPhone).reverse().toString().substring(0, 7);
//        String reversePhoneNumber = new StringBuffer(phoneNumber).reverse().toString().substring(0, 7);
//        return reversePhoneNumber.equals(reverseUserNumber);
        return userPhone.substring(userPhone.length() - 7, userPhone.length()).equals(phoneNumber.substring(phoneNumber.length() - 7, phoneNumber.length()));
    }

    public static Realm getRealmInstance() {
        RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
        Realm realm = Realm.getInstance(config);
        return realm;
    }

    public static RealmQuery<Chat> getChat(Realm rChatDb, String myId, String userId) {
        return rChatDb.where(Chat.class).equalTo("myId", myId).equalTo(userId.startsWith(GROUP_PREFIX) ? "groupId" : "userId", userId);
    }

    public static RealmQuery<Chat> getGroupChat(Realm rChatDb, String myId, String userId) {
        return rChatDb.where(Chat.class).equalTo(userId.startsWith(GROUP_PREFIX) ? "groupId" : "userId", userId);
    }

    public static RealmQuery<Status> getStatus(Realm rChatDb, String myId) {
        return rChatDb.where(Status.class).equalTo("myId", myId);
    }

    public static RealmQuery<Status> getStatus(Realm rChatDb) {
        return rChatDb.where(Status.class);
    }


    public SharedPreferenceHelper getSharedPreferenceHelper() {
        return sharedPreferenceHelper;
    }

    public HashMap<String, User> getCacheMyUsers() {
        if (this.myUsersNameInPhoneMap != null) {
            return this.myUsersNameInPhoneMap;
        } else {
            String inPrefs = sharedPreferenceHelper.getStringPreference(USER_NAME_CACHE);
            if (inPrefs != null) {
                this.myUsersNameInPhoneMap = new Gson().fromJson(inPrefs, new TypeToken<HashMap<String, User>>() {
                }.getType());
                return this.myUsersNameInPhoneMap;
            } else {
                return null;
            }
        }
    }

    public void setCacheMyUsers(ArrayList<User> myUsers) {
        if (this.myUsersNameInPhoneMap == null) {
            this.myUsersNameInPhoneMap = new HashMap<>();
        }
        this.myUsersNameInPhoneMap.clear();
        User me = getLoggedInUser();
        me.setNameInPhone("You");
        this.myUsersNameInPhoneMap.put(me.getId(), me);
        for (User user : myUsers) {
            this.myUsersNameInPhoneMap.put(user.getId(), user);
        }
        sharedPreferenceHelper.setStringPreference(USER_NAME_CACHE, new Gson().toJson(this.myUsersNameInPhoneMap, new TypeToken<HashMap<String, User>>() {
        }.getType()));
    }

    public static void openShareIntent(Context context, @Nullable View itemview, String shareText) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        if (itemview != null) {
            try {
                Uri imageUri = getImageUri(context, itemview, "postBitmap.jpeg");
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_STREAM, imageUri);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } catch (IOException e) {
                intent.setType("text/plain");
                e.printStackTrace();
            }
        } else {
            intent.setType("text/plain");
        }
        intent.putExtra(Intent.EXTRA_TEXT, shareText);
        context.startActivity(Intent.createChooser(intent, "Share Via:"));
    }

    private static Uri getImageUri(Context context, View view, String fileName) throws IOException {
        Bitmap bitmap = loadBitmapFromView(view);
        File pictureFile = new File(context.getExternalCacheDir(), fileName);
        FileOutputStream fos = new FileOutputStream(pictureFile);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
        fos.close();
        return Uri.parse("file://" + pictureFile.getAbsolutePath());
    }

    private static Bitmap loadBitmapFromView(View v) {
        v.clearFocus();
        v.setPressed(false);

        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);

        // Reset the drawing cache background color to fully transparent
        // for the duration of this operation
        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);

        if (color != 0) {
            v.destroyDrawingCache();
        }
        v.buildDrawingCache();
        Bitmap cacheBitmap = v.getDrawingCache();
        if (cacheBitmap == null) {
            v.setDrawingCacheEnabled(true);
            return v.getDrawingCache();
        }

        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

        // Restore the view
        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);

        return bitmap;
    }

    public static void openPlayStore(Context context) {
        final String appPackageName = context.getPackageName(); // getPackageName() from Context or Activity object
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    public static void openSupportMail(Context context) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", context.getString(R.string.support_mail), null));
//        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
//        emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
        context.startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    public static int getDisplayWidth(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    public static void closeKeyboard(Context context, View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null)
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void deleteMessageFromRealm(Realm rChatDb, String msgId) {
        final Message result = rChatDb.where(Message.class).equalTo("id", msgId).findFirst();
        if (result != null) {
            //rChatDb.beginTransaction();
            rChatDb.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmObject.deleteFromRealm(result);
                }
            });

            //rChatDb.commitTransaction();
        }
    }

    public static void updateMessageFromRealm(final Realm rChatDb, final Message msg) {
//        final Message result = rChatDb.where(Message.class).equalTo("id", msgId).findFirst();
        if (msg != null) {
            //rChatDb.beginTransaction();
            rChatDb.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    rChatDb.insertOrUpdate(msg);
//                    RealmObject.deleteFromRealm(result);
                }
            });

            //rChatDb.commitTransaction();
        }
    }

    public static void deleteGroupFromRealm(Realm rChatDb, String msgId) {
        final Chat result = rChatDb.where(Chat.class).equalTo("groupId", msgId).findFirst();
        if (result != null) {
            //rChatDb.beginTransaction();
            rChatDb.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmObject.deleteFromRealm(result);
                }
            });

            //rChatDb.commitTransaction();
        }
    }


    public static void readMessageFromRealm(Realm rChatDb, String msgId) {
        final Message result = rChatDb.where(Message.class).equalTo("id", msgId).findFirst();
        if (result != null) {
            //rChatDb.beginTransaction();
            rChatDb.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    result.setReadMsg(true);
                }
            });

            //rChatDb.commitTransaction();
        }
    }

    public static String timeFormater(float time) {
        Long secs = (long) (time / 1000);
        Long mins = (long) ((time / 1000) / 60);
        Long hrs = (long) (((time / 1000) / 60) / 60); /* Convert the seconds to String * and format to ensure it has * a leading zero when required */
        secs = secs % 60;
        String seconds = String.valueOf(secs);
        if (secs == 0) {
            seconds = "00";
        }
        if (secs < 10 && secs > 0) {
            seconds = "0" + seconds;
        } /* Convert the minutes to String and format the String */
        mins = mins % 60;
        String minutes = String.valueOf(mins);
        if (mins == 0) {
            minutes = "00";
        }
        if (mins < 10 && mins > 0) {
            minutes = "0" + minutes;
        } /* Convert the hours to String and format the String */
        String hours = String.valueOf(hrs);
        if (hrs == 0) {
            hours = "00";
        }
        if (hrs < 10 && hrs > 0) {
            hours = "0" + hours;
        }

        return hours + ":" + minutes + ":" + seconds;
//        String milliseconds = String.valueOf((long) time);
//        if (milliseconds.length() == 2) {
//            milliseconds = "0" + milliseconds;
//        }
//        if (milliseconds.length() <= 1) {
//            milliseconds = "00";
//        }
//        milliseconds = milliseconds.substring(milliseconds.length() - 3, milliseconds.length() - 2); /* Setting the timer text to the elapsed time */
    }


    public static String getTimeAgo(long time, Context ctx) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();

       /* if (time > now || time <= 0) {
            return null;
        }
*/
        // TODO: localize
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } /*else if (diff < 2 * MINUTE_MILLIS) {
            return "a min ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " mins ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "1 hour ago";
        }*/ else if (diff < 24 * HOUR_MILLIS) {
            SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            return formatter.format(calendar.getTime());
            //return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "Yesterday";
        } else {
            //return diff / DAY_MILLIS + " days ago";
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            return formatter.format(calendar.getTime());
        }
    }

    public static String getTimeAgoLastSeen(long time, Context ctx) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();

       /* if (time > now || time <= 0) {
            return null;
        }*/

        // TODO: localize
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            //return diff / DAY_MILLIS + " days ago";
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            return formatter.format(calendar.getTime());
        }
    }

    public static String getFormattedDate(long smsTimeInMilis) {
        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeInMillis(smsTimeInMilis);

        Calendar now = Calendar.getInstance();

        final String timeFormatString = "h:mm aa";
        final String dateTimeFormatString = "dd/MM/yyyy";
        if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE)) {
            return "Today, " + DateFormat.format(timeFormatString, smsTime);
        } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1) {
            return "Yesterday, " + DateFormat.format(timeFormatString, smsTime);
        } else {
            return DateFormat.format(dateTimeFormatString, smsTime).toString();
        }
    }

    public static String getChatFormattedDate(long smsTimeInMilis) {
        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeInMillis(smsTimeInMilis);

        Calendar now = Calendar.getInstance();

        final String timeFormatString = "h:mm aa";
        final String dateTimeFormatString = "dd/MM/yyyy";
        if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE)) {
            return "" + DateFormat.format(timeFormatString, smsTime);
        } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1) {
            return "Yesterday";
        } else {
            return DateFormat.format(dateTimeFormatString, smsTime).toString();
        }
    }

    public static RealmList<String> getRandomElement(RealmList<String> list,
                                                     int totalItems) {
        Random rand = new Random();

        // create a temporary list for storing
        // selected element
        RealmList<String> newList = new RealmList<>();
        for (int i = 0; i < totalItems; i++) {

            // take a raundom index between 0 to size
            // of given List
            int randomIndex = rand.nextInt(list.size());

            // add element in temporary list
            if (!newList.contains(list.get(randomIndex)))
                newList.add(list.get(randomIndex));

            // Remove selected element from orginal list
//            list.remove(randomIndex);
        }
        return newList;
    }

    public static void unBlockAlert(String name, User userMe, Context context, Helper helper,
                                    String userId, FragmentManager manager) {
        String UNBLOCK_TAG = "UNBLOCK_TAG";

        ConfirmationDialogFragment confirmationDialogFragment = ConfirmationDialogFragment
                .newInstance(Helper.getProfileData(context).getLblUnblock(),
                        String.format(Helper.getProfileData(context).getLblWantToUnblock()+" %s",
                        name), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (userMe.getBlockedUsersIds().contains(userId)) {
                                    userMe.getBlockedUsersIds().remove(userId);
                                }

                                BaseApplication.getUserRef().child(userMe.getId()).child("blockedUsersIds")
                                        .setValue(userMe.getBlockedUsersIds())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                helper.setLoggedInUser(userMe);
                                                Toast.makeText(context, Helper.getProfileData(context).getLblUnblocked(), Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(context, Helper.getProfileData(context).getLblUnableToUnblock(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        },
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        });
        confirmationDialogFragment.show(manager, UNBLOCK_TAG);
    }

    public static ArrayList<Country> getCountries(Context context) {
        ArrayList<Country> toReturn = new ArrayList<>();
        try {
            JSONArray countrArray = new JSONArray(readEncodedJsonString(context));
            toReturn = new ArrayList<>();
            for (int i = 0; i < countrArray.length(); i++) {
                JSONObject jsonObject = countrArray.getJSONObject(i);
                String countryName = jsonObject.getString("name");
                String countryDialCode = jsonObject.getString("dial_code");
                String countryCode = jsonObject.getString("code");
                Country country = new Country(countryCode, countryName, countryDialCode);
                toReturn.add(country);
            }
            Collections.sort(toReturn, new Comparator<Country>() {
                @Override
                public int compare(Country lhs, Country rhs) {
                    return lhs.getName().compareTo(rhs.getName());
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return toReturn;
    }

    public static String readEncodedJsonString(Context context) throws IOException {
        String base64 = context.getResources().getString(R.string.countries_code);
        byte[] data = Base64.decode(base64, Base64.DEFAULT);
        return new String(data, "UTF-8");
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public synchronized static void setLangInPref(LanguageListModel langData, Context mContext) {
        if (langData != null) {
            try {
                if (langData.getSplash() != null) {
                    String localeData = new Gson().toJson(langData.getSplash());
                    SessionHandler.getInstance().save(mContext, splash, localeData);
                }
                if (langData.getLoginpage() != null) {
                    String localeData = new Gson().toJson(langData.getLoginpage());
                    SessionHandler.getInstance().save(mContext, loginpage, localeData);
                }
                if (langData.getHomepage() != null) {
                    String localeData = new Gson().toJson(langData.getHomepage());
                    SessionHandler.getInstance().save(mContext, homepage, localeData);
                }
                if (langData.getChatpage() != null) {
                    String localeData = new Gson().toJson(langData.getChatpage());
                    SessionHandler.getInstance().save(mContext, chatpage, localeData);
                }
                if (langData.getGrouppage() != null) {
                    String localeData = new Gson().toJson(langData.getGrouppage());
                    SessionHandler.getInstance().save(mContext, grouppage, localeData);
                }
                if (langData.getProfiledetail() != null) {
                    String localeData = new Gson().toJson(langData.getProfiledetail());
                    SessionHandler.getInstance().save(mContext, profiledetail, localeData);
                }
                if (langData.getCreategrouppage() != null) {
                    String localeData = new Gson().toJson(langData.getCreategrouppage());
                    SessionHandler.getInstance().save(mContext, creategrouppage, localeData);
                }
                if (langData.getStatuspage() != null) {
                    String localeData = new Gson().toJson(langData.getStatuspage());
                    SessionHandler.getInstance().save(mContext, statuspage, localeData);
                }
                if (langData.getCallspage() != null) {
                    String localeData = new Gson().toJson(langData.getCallspage());
                    SessionHandler.getInstance().save(mContext, callspage, localeData);
                }
                if (langData.getSettingspage() != null) {
                    String localeData = new Gson().toJson(langData.getSettingspage());
                    SessionHandler.getInstance().save(mContext, settingspage, localeData);
                }
                if (langData.getChangepasswordpage() != null) {
                    String localeData = new Gson().toJson(langData.getChangepasswordpage());
                    SessionHandler.getInstance().save(mContext, changepasswordpage, localeData);
                }
                if (langData.getCommon() != null) {
                    String localeData = new Gson().toJson(langData.getCommon());
                    SessionHandler.getInstance().save(mContext, commonPage, localeData);
                }
                if (langData.getSchedulepage() != null) {
                    String localeData = new Gson().toJson(langData.getSchedulepage());
                    SessionHandler.getInstance().save(mContext, schedulepage, localeData);
                }
                if (langData.getPasscodepage() != null) {
                    String localeData = new Gson().toJson(langData.getPasscodepage());
                    SessionHandler.getInstance().save(mContext, passcodepage, localeData);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static LanguageListModel.Splash getSplashData(Context mContext) {
        return new Gson().fromJson(SessionHandler.getInstance().get(mContext,
                splash), LanguageListModel.Splash.class);
    }

    public static LanguageListModel.Loginpage getLoginData(Context mContext) {
        return new Gson().fromJson(SessionHandler.getInstance().get(mContext,
                loginpage), LanguageListModel.Loginpage.class);
    }

    public static LanguageListModel.Homepage getHomeData(Context mContext) {
        return new Gson().fromJson(SessionHandler.getInstance().get(mContext,
                homepage), LanguageListModel.Homepage.class);
    }

    public static LanguageListModel.Chatpage getChatData(Context mContext) {
        return new Gson().fromJson(SessionHandler.getInstance().get(mContext,
                chatpage), LanguageListModel.Chatpage.class);
    }

    public static LanguageListModel.Grouppage getGroupData(Context mContext) {
        return new Gson().fromJson(SessionHandler.getInstance().get(mContext,
                grouppage), LanguageListModel.Grouppage.class);
    }

    public static LanguageListModel.Profiledetail getProfileData(Context mContext) {
        return new Gson().fromJson(SessionHandler.getInstance().get(mContext,
                profiledetail), LanguageListModel.Profiledetail.class);
    }

    public static LanguageListModel.Creategrouppage getCreateGroupData(Context mContext) {
        return new Gson().fromJson(SessionHandler.getInstance().get(mContext,
                creategrouppage), LanguageListModel.Creategrouppage.class);
    }

    public static LanguageListModel.Statuspage getStatusData(Context mContext) {
        return new Gson().fromJson(SessionHandler.getInstance().get(mContext,
                statuspage), LanguageListModel.Statuspage.class);
    }

    public static LanguageListModel.Callspage getCallsData(Context mContext) {
        return new Gson().fromJson(SessionHandler.getInstance().get(mContext,
                callspage), LanguageListModel.Callspage.class);
    }

    public static LanguageListModel.Settingspage getSettingsData(Context mContext) {
        return new Gson().fromJson(SessionHandler.getInstance().get(mContext,
                settingspage), LanguageListModel.Settingspage.class);
    }

    public static LanguageListModel.ChangePasswordPage getChangePwd(Context mContext) {
        return new Gson().fromJson(SessionHandler.getInstance().get(mContext,
                changepasswordpage), LanguageListModel.ChangePasswordPage.class);
    }

    public static LanguageListModel.Common getCommonPage(Context mContext) {
        return new Gson().fromJson(SessionHandler.getInstance().get(mContext,
                commonPage), LanguageListModel.Common.class);
    }

    public static LanguageListModel.Schedulepage getSchedulePage(Context mContext) {
        return new Gson().fromJson(SessionHandler.getInstance().get(mContext,
                schedulepage), LanguageListModel.Schedulepage.class);
    }

    public static LanguageListModel.Passcodepage getPassCodePage(Context mContext) {
        return new Gson().fromJson(SessionHandler.getInstance().get(mContext,
                passcodepage), LanguageListModel.Passcodepage.class);
    }


    public static String createRandomChannelName() {
        int count = 15;
        Random random = new Random();
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";

        StringBuilder stringBuilder = new StringBuilder(count);
        int i = 0;
        while (i < count) {
            stringBuilder.append(characters.charAt(random.nextInt(characters.length())));
            i++;
        }
        return stringBuilder.toString();
    }
}
