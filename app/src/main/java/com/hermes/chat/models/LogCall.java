package com.hermes.chat.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.annotations.Ignore;
import io.realm.annotations.RealmClass;

@RealmClass
public class LogCall implements RealmModel, Comparator<LogCall> {
    private User user;
    private long timeUpdated;
    private int timeDurationSeconds;
    private String status, myId;
    RealmList<String> userId;
    private boolean isVideo;
    @Ignore
    private boolean selected;
    private String id;
    private String duration;
    public LogCall() {
    }

    public LogCall(User user, long timeUpdated, int timeDurationSeconds, boolean isVideo,
                   String status, String myId, RealmList<String> userId, String id, String duration) {
        this.user = user;
        this.timeUpdated = timeUpdated;
        this.timeDurationSeconds = timeDurationSeconds;
        this.isVideo = isVideo;
        this.status = status;
        this.myId = myId;
        this.userId = userId;
        this.id = id;
        this.duration = duration;
    }

    public User getUser() {
        return user;
    }

    public long getTimeUpdated() {
        return timeUpdated;
    }

    public int getTimeDuration() {
        return timeDurationSeconds;
    }

    public int getTimeDurationSeconds() {
        return timeDurationSeconds;
    }

    public String getMyId() {
        return myId;
    }

    public RealmList<String> getUserId() {
        return userId;
    }

    public void setUserId(RealmList<String> userId) {
        this.userId = userId;
    }


    public boolean isVideo() {
        return isVideo;
    }

    public String getStatus() {
        return status;
    }
    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    @Override
    public int compare(LogCall o1, LogCall o2) {

        Date d1 = null, d2 = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd_MMM_yyyy_hh_mm_ss");


        String substr1 =  sdf.format(new Date(o1.timeUpdated));
        String substr2 = sdf.format(new Date(o2.timeUpdated));
        try {
            d1 = sdf.parse(substr1);
            d2 = sdf.parse(substr2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return (int) (d2.getTime() - d1.getTime());
    }
}
