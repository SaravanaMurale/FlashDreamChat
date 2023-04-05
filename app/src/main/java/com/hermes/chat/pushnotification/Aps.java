package com.hermes.chat.pushnotification;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Aps {
    @SerializedName("alert")
    @Expose
    private String alert;
    @SerializedName("sound")
    @Expose
    private String sound;

    public Aps(String alert, String sound) {
        this.alert = "great match!";
        this.sound = "default";
    }

    public String getAlert() {
        return alert;
    }

    public void setAlert(String alert) {
        this.alert = alert;
    }

    public String getSound() {
        return sound;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }
    /*@SerializedName("fcm_options")
    @Expose
    private FcmOptions fcmOptions;

    public Aps(FcmOptions fcmOptions) {
        this.fcmOptions = fcmOptions;
    }

    public static class FcmOptions {
        @SerializedName("image")
        @Expose
        private String image;

        public FcmOptions(String image) {
            this.image = image;
        }
    }*/
}