package com.hermes.chat.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionHandler {
    private static final SessionHandler ourInstance = new SessionHandler();

    public static SessionHandler getInstance() {
        return ourInstance;
    }

    private SessionHandler() {
    }


    public SharedPreferences sharedPreferencesInstance(Context mContext) {
        return mContext.getSharedPreferences("LOCALDATA", Context.MODE_PRIVATE);
    }


    public void save(Context mContext, String key, String value) {
        mContext.getSharedPreferences("LOCALDATA", Context.MODE_PRIVATE).edit().putString(key, value).apply();
    }



    public void saveInt(Context mContext, String key, int value) {
        mContext.getSharedPreferences("LOCALDATA", Context.MODE_PRIVATE).edit().putInt(key, value).commit();
    }

    public void saveBoolean(Context mContext, String key, boolean value) {
        mContext.getSharedPreferences("LOCALDATA", Context.MODE_PRIVATE).edit().putBoolean(key, value).commit();
    }

    public boolean getBoolean(Context mContext, String key) {
        return mContext.getSharedPreferences("LOCALDATA", Context.MODE_PRIVATE).getBoolean(key, false);
    }

    public void remove(Context mContext, String key) {
        mContext.getSharedPreferences("LOCALDATA", Context.MODE_PRIVATE).edit().remove(key).commit();
    }

    public void removeAll(Context mContext) {
        mContext.getSharedPreferences("LOCALDATA", Context.MODE_PRIVATE).edit().clear().commit();
    }

    public String get(Context mContext, String key) {
        return mContext.getSharedPreferences("LOCALDATA", Context.MODE_PRIVATE).getString(key, null);
    }

    public int getInt(Context mContext, String key) {
        return mContext.getSharedPreferences("LOCALDATA", Context.MODE_PRIVATE).getInt(key, 0);
    }




}
