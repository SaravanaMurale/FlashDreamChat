package com.hermes.chat.audio.openacall.model;

public interface AGEventHandler {
    void onJoinChannelSuccess(String channel, int uid, int elapsed);

    void onUserOffline(int uid, int reason);

    void onExtraCallback(int type, Object... data);

    void onUserJoined(int uid);

    int EVENT_TYPE_ON_USER_AUDIO_MUTED = 7;

    int EVENT_TYPE_ON_SPEAKER_STATS = 8;

    int EVENT_TYPE_ON_AGORA_MEDIA_ERROR = 9;

    int EVENT_TYPE_ON_AUDIO_QUALITY = 10;

    int EVENT_TYPE_ON_APP_ERROR = 13;

    int EVENT_TYPE_ON_AUDIO_ROUTE_CHANGED = 18;
}
