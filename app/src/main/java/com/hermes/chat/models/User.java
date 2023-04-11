package com.hermes.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;

import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.annotations.Ignore;
import io.realm.annotations.RealmClass;

/**
 * Created by a_man on 5/4/2017.
 */

@RealmClass
public class User implements Parcelable, RealmModel {
    @Ignore
    private boolean online;
    @Exclude
    @Ignore
    private String nameInPhone;
    /* @Ignore
     private boolean typing;*/
    @Ignore
    private String typing;

    @Ignore
    @Exclude
    private boolean selected;

    private String id, name, status, image;

    private String wallpaper;
    @Exclude
    @Ignore
    private String profileName;

    private long timestamp;

    public RealmList<solochat> solochat = new RealmList<>();
    private RealmList<String> blockedUsersIds = new RealmList<>();
    private RealmList<String> connectList = new RealmList<>();
    private String deviceToken = " ";
    private String osType = " ";
    private String username = "";
    private String password = "";
    private String email = "";
    private int is_new;


    @Ignore
    @Exclude
    private boolean call_status = true;
    private String webqrcode = "";

    public User() {
    }



    protected User(Parcel in) {
        online = in.readByte() != 0;
        nameInPhone = in.readString();
        typing = in.readString();
        //typing = in.readByte() != 0;
        selected = in.readByte() != 0;
        id = in.readString();
        name = in.readString();
        status = in.readString();
        image = in.readString();
        wallpaper = in.readString();
        timestamp = in.readLong();
        is_new = in.readInt();
        ArrayList<String> blockedUsersIds = in.createStringArrayList();
        this.blockedUsersIds = new RealmList<>();
        this.blockedUsersIds.addAll(blockedUsersIds);
        ArrayList<String> connect_list = in.createStringArrayList();
        this.connectList = new RealmList<>();
        this.connectList.addAll(connect_list);
        deviceToken = in.readString();
        osType = in.readString();
        profileName = in.readString();
        username = in.readString();
        password = in.readString();
        email = in.readString();
        call_status = in.readByte() != 0;
        webqrcode = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public User(String id, String name, String status, String image) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.online = false;
        this.image = image;
        //  this.typing = false;
        this.typing = "";
    }



    public User(String id, String name, String status, String image, String deviceToken, String osType,
                String userName, String password, String email, String webqrcode) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.online = false;
        this.image = image;
        // this.typing = false;
        this.typing = "";
        this.deviceToken = deviceToken;
        this.osType = osType;
        this.username = userName;
        this.password = password;
        this.email = email;
        this.webqrcode = webqrcode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return id.equals(user.id);
    }

    public RealmList<String> getConnect_list()
    {
        return connectList;
    }

    public void setConnect_list(ArrayList<String> connect_list)
    {
        this.connectList.addAll(connect_list);
    }

    public int getIs_new()
    {
        return is_new;
    }

    public void setIs_new(int is_new)
    {
        this.is_new = is_new;
    }

    public String getNameInPhone() {
        return nameInPhone;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public String isTyping() {
        return typing;
    }

    public void setTyping(String typing) {
        this.typing = typing;
    }

    public void setNameInPhone(String nameInPhone) {
        this.nameInPhone = nameInPhone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isOnline() {
        return online;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public String getNameToDisplay() {
        return (this.nameInPhone != null) ? this.nameInPhone : this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getWallpaper() {
        return wallpaper;
    }

    public void setWallpaper(String wallpaper) {
        this.wallpaper = wallpaper;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public long getTimeStamp() {
        return timestamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timestamp = timeStamp;
    }

    public RealmList<solochat> getSolochat() {
        return solochat;
    }

    public void setSolochat(ArrayList<solochat> solochat) {
        this.solochat = new RealmList<>();
        this.solochat.addAll(solochat);
    }

    public RealmList<String> getBlockedUsersIds() {
        return blockedUsersIds;
    }

    public void setBlockedUsersIds(ArrayList<String> blockedUsersIds) {
        this.blockedUsersIds.addAll(blockedUsersIds);
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getOsType() {
        return osType;
    }

    public void setOsType(String osType) {
        this.osType = osType;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isCall_status() {
        return call_status;
    }

    public void setCall_status(boolean call_status) {
        this.call_status = call_status;
    }

    public String getWebqrcode() {
        return webqrcode;
    }

    public void setWebqrcode(String webqrcode) {
        this.webqrcode = webqrcode;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeByte((byte) (online ? 1 : 0));
        parcel.writeString(nameInPhone);
        parcel.writeString(typing);
        //  parcel.writeByte((byte) (typing ? 1 : 0));
        parcel.writeByte((byte) (selected ? 1 : 0));
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeInt(is_new);
        parcel.writeString(status);
        parcel.writeString(image);
        parcel.writeString(wallpaper);
        parcel.writeLong(timestamp);
        ArrayList<String> blockedIds = new ArrayList<>();
        if (this.blockedUsersIds != null) {
            blockedIds.addAll(this.blockedUsersIds);
            parcel.writeStringList(blockedIds);
        }
//        parcel.writeList(connectList);
//        parcel.writeStringList(connectList);
        ArrayList<String> connect_list = new ArrayList<>();
        if (this.connectList != null) {
            connect_list.addAll(this.connectList);
            parcel.writeStringList(connect_list);
        }
        parcel.writeString(deviceToken);
        parcel.writeString(osType);
        parcel.writeString(profileName);
        parcel.writeString(username);
        parcel.writeString(password);
        parcel.writeString(email);
        parcel.writeByte((byte) (call_status ? 1 : 0));
    }

    public static boolean validate(User user) {
        return user != null && user.getId() != null && user.getName() != null && user.getStatus() != null;
    }

}