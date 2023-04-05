package com.hermes.chat.pushnotification;

public class Notification {
    private String title;
    private String body;
    private String sound = "default";
    private String fromId;
    private String toId;
    private String userIds;
    private String channelName;

    public Notification(String title, String body) {
        this.title = title;
        this.body = body;
    }
    public Notification(String title, String body, String fromId, String toId) {
        this.title = title;
        this.body = body;
        this.fromId = fromId;
        this.toId = toId;
    }
    public Notification(String title, String body, String fromId, String toId,
                        String sound, String userIds,String channelName) {
        this.title = title;
        this.body = body;
        this.fromId = fromId;
        this.toId = toId;
        this.sound = sound;
        this.userIds = userIds;
        this.channelName = channelName;
    }
}