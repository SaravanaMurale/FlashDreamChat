package com.hermes.chat.pushnotification;

public class Data {

    private String title;
    private String body;
    private String image;
    private String fromId;
    private String toId;
    private String userIds;
    private String channelName;

    public Data(String title, String body) {
        this.title = title;
        this.body = body;
    }

    public Data(String title, String body, String image) {
        this.title = title;
        this.body = body;
        this.image = image;
    }

    public Data(String title, String body, String image, String fromId, String toId,
                String userIds,String channelName) {
        this.title = title;
        this.body = body;
        this.image = image;
        this.fromId = fromId;
        this.toId = toId;
        this.userIds = userIds;
        this.channelName = channelName;
    }
}