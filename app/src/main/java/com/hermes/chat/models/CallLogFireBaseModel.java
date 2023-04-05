package com.hermes.chat.models;

import java.util.ArrayList;

public class CallLogFireBaseModel {

    private ArrayList<String> callerId;
    private String callerImg;
    private String callerName;
    private long currentMills;
    private String inOrOut;
    private boolean isVideo;
    private String userId;
    private String id;
    private String duration;
    private String type;

    public CallLogFireBaseModel() {
    }

    public CallLogFireBaseModel(ArrayList<String> callerId, String callerImg, String callerName,
                                long currentMills, String inOrOut, boolean isVideo, String userId, String type) {
        this.callerId = callerId;
        this.callerImg = callerImg;
        this.callerName = callerName;
        this.currentMills = currentMills;
        this.inOrOut = inOrOut;
        this.isVideo = isVideo;
        this.userId = userId;
        this.type = type;
    }

    public ArrayList<String> getCallerId() {
        return callerId;
    }

    public void setCallerId(ArrayList<String> callerId) {
        this.callerId = callerId;
    }

    public String getCallerImg() {
        return callerImg;
    }

    public void setCallerImg(String callerImg) {
        this.callerImg = callerImg;
    }

    public String getCallerName() {
        return callerName;
    }

    public void setCallerName(String callerName) {
        this.callerName = callerName;
    }

    public long getCurrentMills() {
        return currentMills;
    }

    public void setCurrentMills(long currentMills) {
        this.currentMills = currentMills;
    }

    public String getInOrOut() {
        return inOrOut;
    }

    public void setInOrOut(String inOrOut) {
        this.inOrOut = inOrOut;
    }


    public boolean isVideo() {
        return isVideo;
    }

    public void setVideo(boolean isVideo) {
        this.isVideo = isVideo;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
