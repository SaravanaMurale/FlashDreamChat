package com.hermes.chat.adapters;

import static com.hermes.chat.utils.Helper.createRandomChannelName;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hermes.chat.BaseApplication;
import com.hermes.chat.R;
import com.hermes.chat.activities.MainActivity;
import com.hermes.chat.audio.openacall.model.ConstantApp;
import com.hermes.chat.audio.openacall.ui.AgoraSingleAudioCallActivity;
import com.hermes.chat.audio.openacall.ui.AgoraSingleVideoCallActivity;
import com.hermes.chat.fragments.MyCallsFragment;
import com.hermes.chat.interfaces.ContextualModeInteractor;
import com.hermes.chat.interfaces.OnUserGroupItemClick;
import com.hermes.chat.models.CallLogFireBaseModel;
import com.hermes.chat.models.LogCall;
import com.hermes.chat.models.User;
import com.hermes.chat.pushnotification.Aps;
import com.hermes.chat.pushnotification.Data;
import com.hermes.chat.pushnotification.Notification;
import com.hermes.chat.pushnotification.SendFirebaseNotification;
import com.hermes.chat.utils.Helper;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;

public class LogCallAdapter extends RecyclerView.Adapter<LogCallAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<LogCall> dataList;
    private ContextualModeInteractor contextualModeInteractor;
    private OnUserGroupItemClick itemClickListener;
    private ArrayList<User> myUsers;
    private User userMe, user;
    private FragmentManager manager;
    private Helper helper;
    protected String[] permissionsSinch = {Manifest.permission.RECORD_AUDIO,
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
            Manifest.permission.READ_PHONE_STATE};

    public LogCallAdapter(Context context, ArrayList<LogCall> dataList, ArrayList<User> myUsers,
                          User loggedInUser, FragmentManager manager, Helper helper) {
        this.context = context;
        this.dataList = dataList;
        this.userMe = loggedInUser;
        this.myUsers = myUsers;
        this.manager = manager;
        this.helper = helper;
        if (context instanceof ContextualModeInteractor) {
            this.contextualModeInteractor = (ContextualModeInteractor) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement ContextualModeInteractor");
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_item_log_call, parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.setData(dataList.get(position));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView userImage, aCallLogImg, callTypeImg;
        private TextView time, duration, userName;
        private RelativeLayout user_details_container;

        MyViewHolder(View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.userImage);
            time = itemView.findViewById(R.id.time);
            duration = itemView.findViewById(R.id.duration);
            userName = itemView.findViewById(R.id.userName);
            aCallLogImg = itemView.findViewById(R.id.img_calllog);
            callTypeImg = itemView.findViewById(R.id.callTypeImg);
            user_details_container = itemView.findViewById(R.id.user_details_container);

            user_details_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (contextualModeInteractor.isContextualMode()) {
                        toggleSelection(dataList.get(getAdapterPosition()), getAdapterPosition());
                    }
                }
            });
            user_details_container.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    contextualModeInteractor.enableContextualMode();
                    toggleSelection(dataList.get(getAdapterPosition()), getAdapterPosition());
                    return true;
                }
            });
        }

     /*   public void setData(final LogCall logCall) {
            try {

                if (logCall.isSelected())
                    user_details_container.setBackgroundResource(R.drawable.message_selected_item_border);
                else
                    user_details_container.setBackgroundResource(R.drawable.message_item_border);

                if (myUsers.size() != 0) {
                    for (int i = 0; i < myUsers.size(); i++) {
                       *//* if (logCall.getUserId() != null) {
                            logCall.setUserId(logCall.getUserId().replace("dreamchat-", ""));

                            if (!logCall.getUserId().contains("+"))
                                logCall.setUserId("+" + logCall.getUserId());

                            if (myUsers.get(i).getId().equalsIgnoreCase(logCall.getUserId()))
                                user = myUsers.get(i);
                        }*//*

                        if (myUsers.get(i).getId().equalsIgnoreCase(logCall.getUserId().get(0)))
                            user = myUsers.get(i);
                    }
//            Glide.with(context).load(logCall.getUser().getImage()).apply(new RequestOptions().placeholder(R.drawable.ic_placeholder)).into(userImage);
                    if (user.getImage() != null && !user.getImage().isEmpty())
                        if (user.getBlockedUsersIds() != null && !user.getBlockedUsersIds().contains(MainActivity.userId))
                            Picasso.get()
                                    .load(user.getImage())
                                    .tag(this)
                                    .error(R.drawable.ic_avatar)
                                    .placeholder(R.drawable.ic_avatar)
                                    .into(userImage);
                        else
                            Picasso.get()
                                    .load(R.drawable.ic_avatar)
                                    .tag(this)
                                    .error(R.drawable.ic_avatar)
                                    .placeholder(R.drawable.ic_avatar)
                                    .into(userImage);
                    else
                        Picasso.get()
                                .load(R.drawable.ic_avatar)
                                .tag(this)
                                .error(R.drawable.ic_avatar)
                                .placeholder(R.drawable.ic_avatar)
                                .into(userImage);
                }
                callTypeImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        makeCall(logCall.isVideo(), logCall.getUserId().get(0));
                    }
                });


                userName.setText(logCall.getUser().getNameToDisplay());

                try {
                    if (logCall.getDuration() != null) {
                        if (!logCall.getDuration().isEmpty()) {
                            String duration = logCall.getDuration().split(":")[0] + "m " +
                                    logCall.getDuration().split(":")[1] + "s";
                            time.setText(duration + " " + Helper.getDateTime(logCall.getTimeUpdated()));
                        } else
                            time.setText(Helper.getDateTime(logCall.getTimeUpdated()));
                    } else {
                        time.setText(Helper.getDateTime(logCall.getTimeUpdated()));
                    }
                } catch (Exception e) {
                    time.setText(Helper.getDateTime(logCall.getTimeUpdated()));
                }

//            time.setCompoundDrawablesWithIntrinsicBounds(logCall.getStatus().equals("CANCELED") ? R.drawable.ic_call_missed_24dp : logCall.getStatus().equals("DENIED") || logCall.getStatus().equals("IN") ? R.drawable.ic_call_received_24dp : logCall.getStatus().equals("OUT") ? R.drawable.ic_call_made_24dp : 0, 0, 0, 0);
                if (logCall.getStatus().equalsIgnoreCase("CANCELED")) {
                    aCallLogImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_call_missed_24dp));
                } else if (logCall.getStatus().equalsIgnoreCase("DENIED")) {
                    aCallLogImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_call_missed_24dp));
                } else if (logCall.getStatus().equalsIgnoreCase("IN")) {
                    aCallLogImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_in));
                } else if (logCall.getStatus().equalsIgnoreCase("OUT")) {
                    aCallLogImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_out));
                }

                duration.setText(formatTimespan(logCall.getTimeDuration()));

                if (logCall.isVideo()) {
                    callTypeImg.setBackgroundResource(R.drawable.ic_videocam_white_24dp);
                } else {
                    callTypeImg.setBackgroundResource(R.drawable.ic_call_white_24dp);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }*/


        public void setData(final LogCall logCall) {
            try {

                if (logCall.isSelected())
                    user_details_container.setBackgroundResource(R.drawable.message_selected_item_border);
                else
                    user_details_container.setBackgroundResource(R.drawable.message_item_border);

                if (myUsers.size() != 0) {
                    for (int i = 0; i < myUsers.size(); i++) {
                        if (myUsers.get(i).getId().equalsIgnoreCase(logCall.getUserId().get(0)))
                            user = myUsers.get(i);
                    }

                    if (user.getImage() != null && !user.getImage().isEmpty())
                        if (user.getBlockedUsersIds() != null && !user.getBlockedUsersIds().contains(MainActivity.userId))
                            Picasso.get()
                                    .load(user.getImage())
                                    .tag(this)
                                    .error(R.drawable.ic_avatar)
                                    .placeholder(R.drawable.ic_avatar)
                                    .into(userImage);
                        else
                            Picasso.get()
                                    .load(R.drawable.ic_avatar)
                                    .tag(this)
                                    .error(R.drawable.ic_avatar)
                                    .placeholder(R.drawable.ic_avatar)
                                    .into(userImage);
                    else
                        Picasso.get()
                                .load(R.drawable.ic_avatar)
                                .tag(this)
                                .error(R.drawable.ic_avatar)
                                .placeholder(R.drawable.ic_avatar)
                                .into(userImage);
                }
                userName.setText(logCall.getUser().getNameToDisplay());


                callTypeImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        makeCall(logCall.isVideo(), logCall.getUserId().get(0));
                    }
                });

                try {
                    if (logCall.getDuration() != null) {
                        if (!logCall.getDuration().isEmpty()) {
                            String duration = logCall.getDuration().split(":")[0] + "m " +
                                    logCall.getDuration().split(":")[1] + "s";
                            time.setText(duration + " " + Helper.getDateTime(logCall.getTimeUpdated()));
                        } else
                            time.setText(Helper.getDateTime(logCall.getTimeUpdated()));
                    } else {
                        time.setText(Helper.getDateTime(logCall.getTimeUpdated()));
                    }
                } catch (Exception e) {
                    time.setText(Helper.getDateTime(logCall.getTimeUpdated()));
                }


//            time.setCompoundDrawablesWithIntrinsicBounds(logCall.getStatus().equals("CANCELED") ? R.drawable.ic_call_missed_24dp : logCall.getStatus().equals("DENIED") || logCall.getStatus().equals("IN") ? R.drawable.ic_call_received_24dp : logCall.getStatus().equals("OUT") ? R.drawable.ic_call_made_24dp : 0, 0, 0, 0);
                if (logCall.getStatus().equalsIgnoreCase("CANCELED")) {
                    aCallLogImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_call_missed_24dp));
                } else if (logCall.getStatus().equalsIgnoreCase("DENIED")) {
                    aCallLogImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_call_missed_24dp));
                } else if (logCall.getStatus().equalsIgnoreCase("IN")) {
                    aCallLogImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_in));
                } else if (logCall.getStatus().equalsIgnoreCase("OUT")) {
                    aCallLogImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_out));
                }

                duration.setText(formatTimespan(logCall.getTimeDuration()));

                if (logCall.isVideo()) {
                    callTypeImg.setBackgroundResource(R.drawable.ic_videocam_white_24dp);
                } else {
                    callTypeImg.setBackgroundResource(R.drawable.ic_call_white_24dp);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        private String formatTimespan(int totalSeconds) {
            long minutes = totalSeconds / 60;
            long seconds = totalSeconds % 60;
            return String.format(Locale.US, "%02d:%02d", minutes, seconds);
        }
    }

    private void makeCall(boolean callIsVideo, String userId) {
        HashSet<User> hashSet = new HashSet<User>();
        hashSet.addAll(MainActivity.myUsers);
        MainActivity.myUsers.clear();
        MainActivity.myUsers.addAll(hashSet);

        for (User user : MainActivity.myUsers) {
            if (user.getId().equalsIgnoreCase(userId)) {
                if (userMe != null && userMe.getBlockedUsersIds() != null
                        && userMe.getBlockedUsersIds().contains(user.getId())) {
                    Helper.unBlockAlert(user.getName(), userMe, context,
                            helper, user.getId(), manager);
                } else {
                    if (callIsVideo) {
                        forwardToRoom("Video call", callIsVideo, userId, user);
                        break;
                    } else {
                        forwardToRoom("Audio call", callIsVideo, userId, user);
                        break;
                    }
                }
            }
        }

        // placeCall(b, userId);
    }

    public void forwardToRoom(String type, boolean callIsVideo, String userId, User user) {
        ArrayList<String> userList = new ArrayList<>();
        userList.add(userId);
        CallLogFireBaseModel callLogFireBaseModel = new CallLogFireBaseModel(userList,
                "", "", System.currentTimeMillis(), "OUT",
                callIsVideo, userMe.getId(), "single");
        String id = BaseApplication.getCallsRef().child(userMe.getId()).push().getKey();
        callLogFireBaseModel.setId(id);
        BaseApplication.getCallsRef().child(userMe.getId()).child(id).setValue(callLogFireBaseModel);

        String[] registration_ids = new String[]{};
        String channel = createRandomChannelName();
        Notification notification = new Notification(type, userMe.getId(), userMe.getId(),
                userId, "incoming.wav", "", channel);
        Data data = null;
        if (permissionsAvailable(permissionsSinch)) {
            if (user.getWebqrcode() != null && !user.getWebqrcode().isEmpty()) {
                data = new Data(type, channel, " ", userMe.getId(), userId, "", "");
                String call_type = "";
                if (type.equalsIgnoreCase("Audio call"))
                    call_type = "audio";
                else
                    call_type = "video";

                BaseApplication.getUserRef().child(user.getId()).child("incomingcall").setValue(
                        "user_type=onetoone&call_type=" + call_type + "&channelname=" + channel +
                                "&caller=" + userMe.getId() + "&receiver=" +
                                userId + "&group=");
              /*  BaseApplication.getUserRef().child(user.getId()).child("channelname").setValue(
                        "channel_name=" + channel + "&caller=" + userMe.getId() + "&receiver=" + userId);*/
            } else {
                registration_ids = new String[1];
                registration_ids[0] = user.getDeviceToken();


                if (user.getOsType() != null && !user.getOsType().isEmpty() && user.getOsType().equalsIgnoreCase("iOS")) {
                    data = new Data(type, userMe.getId(), " ", userMe.getId(), userId, "", channel);
                    com.hermes.chat.pushnotification.Message notificationTask =
                            new com.hermes.chat.pushnotification.Message(registration_ids,
                                    "", data, notification,
                                    new Aps(" ", "incoming.wav"));

                    new SendFirebaseNotification(context, notificationTask).triggerMessage();
                } else {
                    data = new Data(type, channel, " ", userMe.getId(), userId, "", "");
                    com.hermes.chat.pushnotification.Message notificationTask =
                            new com.hermes.chat.pushnotification.Message(registration_ids,
                                    "", data,
                                    new Aps(" ", " "));

                    new SendFirebaseNotification(context, notificationTask).triggerMessage();
                }
            }
            BaseApplication.mAudioSettings.mChannelName = channel;
            Intent i;
            if (type.equalsIgnoreCase("Audio call"))
                i = new Intent(context, AgoraSingleAudioCallActivity.class);
            else
                i = new Intent(context, AgoraSingleVideoCallActivity.class);

            i.putExtra(ConstantApp.ACTION_KEY_CHANNEL_NAME, channel);
            i.putExtra("id", id);
            String str = new Gson().toJson(data);
            i.putExtra("data", str);
            context.startActivity(i);
        } else {
            ActivityCompat.requestPermissions((Activity) context, permissionsSinch, 69);
        }
    }

 /*   private void makeCall(boolean b, String userId, User user) {
        if (user != null && userMe != null && userMe.getBlockedUsersIds() != null
                && userMe.getBlockedUsersIds().contains(user.getId())) {
            Helper.unBlockAlert(user.getNameToDisplay(), userMe, context,
                    helper, user.getId(), manager);
        } else
            placeCall(b, userId);
    }

    private void placeCall(boolean isVideoCall, String userId) {
        if (permissionsAvailable(permissionsSinch)) {
            try {
                Call call = isVideoCall ? ((MainActivity) context).getSinchRef().callUserVideo(userId)
                        : ((MainActivity) context).getSinchRef().callUser(userId);
                if (call == null) {
                    // Service failed for some reason, show a Toast and abort
                    Toast.makeText(context, "Service is not started. Try stopping the service and starting it again before placing a call.", Toast.LENGTH_LONG).show();
                    return;
                }
                String callId = call.getCallId();

                for (User user : MainActivity.myUsers) {
                    if (user != null && user.getId() != null && user.getId().equalsIgnoreCase(userId)) {
                        context.startActivity(CallScreenActivity.newIntent(context, user, callId, "OUT"));
                    }
                }

            } catch (Exception e) {
                Log.e("CHECK", e.getMessage());
                //ActivityCompat.requestPermissions(this, new String[]{e.getRequiredPermission()}, 0);
            }
        } else {
            ActivityCompat.requestPermissions((Activity) context, permissionsSinch, 69);
        }
    }*/

    protected boolean permissionsAvailable(String[] permissions) {
        boolean granted = true;
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                granted = false;
                break;
            }
        }
        return granted;
    }

    private void toggleSelection(LogCall logCall, int position) {
        logCall.setSelected(!logCall.isSelected());
        notifyItemChanged(position);

        if (logCall.isSelected())
            MyCallsFragment.selectedCount++;
        else
            MyCallsFragment.selectedCount--;

        contextualModeInteractor.updateSelectedCount(MyCallsFragment.selectedCount);
    }

    public void updateCount() {
        contextualModeInteractor.updateSelectedCount(MyCallsFragment.selectedCount);
    }

    public void disableContextualMode() {
        MyCallsFragment.selectedCount = 0;
        for (int i = 0; i < dataList.size(); i++) {
            if (dataList.get(i).isSelected()) {
                dataList.get(i).setSelected(false);
                notifyItemChanged(i);
            }
        }
    }
}
