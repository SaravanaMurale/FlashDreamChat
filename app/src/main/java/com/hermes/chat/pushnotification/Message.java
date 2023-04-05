package com.hermes.chat.pushnotification;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Message {

    @SerializedName("registration_ids")
    @Expose
    private String[] registration_ids;
    private String collapseKey;
    @SerializedName("notification")
    @Expose
    private Notification notification;
    @SerializedName("data")
    @Expose
    private Data data;
    @SerializedName("aps")
    @Expose
    private Aps Aps;

    @SerializedName("priority")
    @Expose
    private String priority = "high";
    @SerializedName("content_available")
    @Expose
    private Boolean contentAvailable = true;
    @SerializedName("mutable_content")
    @Expose
    private Boolean mutableContent = true;

    /*public Message(String[] registration_ids, String collapseKey, JSONObject fromJson, Data data, Notification notification) {
        this.registration_ids = registration_ids;
        this.collapseKey = collapseKey;
        this.aps = fromJson;
        this.notification = notification;
        this.data = data;
    }*/

    /*public Message(String[] registration_ids, String collapseKey, Data data, Notification notification) {
        this.registration_ids = registration_ids;
        this.collapseKey = collapseKey;
        this.notification = notification;
        this.data = data;
    }*/
    public Message(String[] registration_ids, String collapseKey, Data data, Notification notification,
                   Aps Aps) {
        this.registration_ids = registration_ids;
        this.collapseKey = collapseKey;
        this.notification = notification;
        this.data = data;
        this.Aps = Aps;
    }

    public Message(String[] registration_ids, String collapseKey, Data data,
                   Aps Aps) {
        this.registration_ids = registration_ids;
        this.collapseKey = collapseKey;
        this.data = data;
        this.Aps = Aps;
    }

    /*public Message(String[] registration_ids, String collapseKey, Aps aps, Data data, Notification notification) {
        this.registration_ids = registration_ids;
        this.collapseKey = collapseKey;
        this.aps = aps;
        this.notification = notification;
        this.data = data;
    }*/
}