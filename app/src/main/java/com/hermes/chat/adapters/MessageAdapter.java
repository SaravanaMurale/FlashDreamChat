package com.hermes.chat.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.hermes.chat.BaseApplication;
import com.hermes.chat.R;
import com.hermes.chat.activities.MainActivity;
import com.hermes.chat.interfaces.OnMessageItemClick;
import com.hermes.chat.models.AttachmentTypes;
import com.hermes.chat.models.Message;
import com.hermes.chat.models.User;
import com.hermes.chat.utils.FileUtils;
import com.hermes.chat.utils.Helper;
import com.hermes.chat.viewHolders.BaseMessageViewHolder;
import com.hermes.chat.viewHolders.MessageAttachmentAudioViewHolder;
import com.hermes.chat.viewHolders.MessageAttachmentContactViewHolder;
import com.hermes.chat.viewHolders.MessageAttachmentDocumentViewHolder;
import com.hermes.chat.viewHolders.MessageAttachmentImageViewHolder;
import com.hermes.chat.viewHolders.MessageAttachmentLocationViewHolder;
import com.hermes.chat.viewHolders.MessageAttachmentRecordingViewHolder;
import com.hermes.chat.viewHolders.MessageAttachmentVideoViewHolder;
import com.hermes.chat.viewHolders.MessageTextViewHolder;
import com.hermes.chat.viewHolders.MessageTypingViewHolder;

import java.util.ArrayList;
import java.util.HashMap;

public class MessageAdapter extends RecyclerView.Adapter<BaseMessageViewHolder> {
    private static final String TAG = "MessageAdapter";
    private Helper helper;
    private OnMessageItemClick itemClickListener;
    private MessageAttachmentRecordingViewHolder.RecordingViewInteractor recordingViewInteractor;
    private String myId;
    private Context context;
    private ArrayList<Message> messages = new ArrayList<>();
    private View newMessage;
    private HashMap<String, User> myUsersNameInPhoneMap;
    private ImageView statusImg;
    private RelativeLayout statusLay;
    TextView statusText;
    SharedPreferences pref;
    String msgTime;

    public static final int MY = 0x00000000;
    public static final int OTHER = 0x0000100;

    public MessageAdapter(Context context, ArrayList<Message> messages, String myId, View newMessage) {

       /* pref = context.getSharedPreferences(
                String.valueOf(R.string.app_name), Context.MODE_PRIVATE);
        msgTime = pref.getString("disappear", "");*/
//        this.msgTime = msgTime;
        this.context = context;
        this.messages = messages;
        this.myId = myId;
        this.newMessage = newMessage;
        this.helper = new Helper(context);
        this.myUsersNameInPhoneMap = helper.getCacheMyUsers();

        if (context instanceof OnMessageItemClick) {
            this.itemClickListener = (OnMessageItemClick) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnUserGroupItemClick");
        }

        if (context instanceof MessageAttachmentRecordingViewHolder.RecordingViewInteractor) {
            this.recordingViewInteractor = (MessageAttachmentRecordingViewHolder.RecordingViewInteractor) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement RecordingViewInteractor");
        }
    }

    @Override
    public BaseMessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        viewType &= 0x00000FF;
        switch (viewType) {
            case AttachmentTypes.RECORDING:
                return new MessageAttachmentRecordingViewHolder(LayoutInflater.from(context).inflate(R.layout.item_message_attachment_recording, parent, false), itemClickListener, recordingViewInteractor, messages);
            case AttachmentTypes.AUDIO:
                return new MessageAttachmentAudioViewHolder(LayoutInflater.from(context).inflate(R.layout.item_message_attachment_audio, parent, false), itemClickListener, messages);
            case AttachmentTypes.CONTACT:
                return new MessageAttachmentContactViewHolder(LayoutInflater.from(context).inflate(R.layout.item_message_attachment_contact, parent, false), itemClickListener, messages);
            case AttachmentTypes.DOCUMENT:
                return new MessageAttachmentDocumentViewHolder(LayoutInflater.from(context).inflate(R.layout.item_message_attachment_document, parent, false), itemClickListener, messages);
            case AttachmentTypes.IMAGE:
                return new MessageAttachmentImageViewHolder(LayoutInflater.from(context).inflate(R.layout.item_message_attachment_image, parent, false), itemClickListener, messages);
            case AttachmentTypes.LOCATION:
                return new MessageAttachmentLocationViewHolder(LayoutInflater.from(context).inflate(R.layout.item_message_attachment_location, parent, false), itemClickListener, messages);
            case AttachmentTypes.VIDEO:
                return new MessageAttachmentVideoViewHolder(LayoutInflater.from(context).inflate(R.layout.item_message_attachment_video, parent, false), itemClickListener, messages);
            case AttachmentTypes.NONE_TYPING:
                return new MessageTypingViewHolder(LayoutInflater.from(context).inflate(R.layout.item_message_typing, parent, false));
            case AttachmentTypes.NONE_TEXT:
            default:
                return new MessageTextViewHolder(LayoutInflater.from(context).inflate(R.layout.item_message_text, parent, false), newMessage, itemClickListener, messages);
        }
    }

    @Override
    public void onBindViewHolder(BaseMessageViewHolder holder, int position) {
        try {
         /*   if(){

            }*/
//            Log.d(TAG, "onBindViewHolder: "+FileUtils.getTime(msgTime));

                holder.setData(messages.get(position), position, myUsersNameInPhoneMap, MainActivity.myUsers);

                if (messages.get(position).isSelected())
                    holder.parentLayout.setBackgroundColor(Color.parseColor("#d2b2e5"));
                else
                    holder.parentLayout.setBackgroundColor(Color.parseColor("#00000000"));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemViewType(int position) {
        try {
            if (getItemCount() == 0) {
                return super.getItemViewType(position);
            } else {
                Message message = messages.get(position);
                int userType;
                if (message.getSenderId().equals(myId))
                    userType = MY;
                else
                    userType = OTHER;
                return message.getAttachmentType() | userType;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        ArrayList<Message> filter = new ArrayList<>();
        Log.d(TAG, "getItemCount: "+messages.size());
//        return messages.size();


                for(int i=0;i<messages.size();i++){
                    try
                    {
                        if(messages.get(i).getDisappearing_message() == null){
                            filter.add(messages.get(i));
                            
                        } else if(messages.get(i).getDisappearing_message().equalsIgnoreCase("Turn off")){
                            filter.add(messages.get(i));
                        } else if(!FileUtils.compareDate(FileUtils.getTime(messages.get(i).getDisappearing_message().replace(" Minutes","")),Helper.getDateTime(messages.get(i).getDate()))){
//                    Log.d(TAG, "setData: smaller");
                                filter.add(messages.get(i));
                            } else {
                          /*  if (messages.get(i).getUserIds() != null) {
                                ArrayList<String> deleteUSerIds = new ArrayList<>();
                                deleteUSerIds.addAll(messages.get(i).getUserIds());
                                if (deleteUSerIds.contains(userMe.getId())) {
                                    deleteUSerIds.remove(userMe.getId());
                                }
                                BaseApplication.getChatRef().child(chatChild).child(msg.getId()).child("userIds").setValue(deleteUSerIds);
                                senderIdDelete = msg.getSenderId();
                                recipientIdDelete = msg.getRecipientId();
                                msgID = chatChild;
                                bodyDelete = msg.getBody();
                                dateDelete = msg.getDate();
                                delete = true;
                                Helper.deleteMessageFromRealm(rChatDb, msg.getId());
//                                                Helper.updateMessageFromRealm(rChatDb, msg);
                            }*/
                        }


                    }catch (Exception e){

                    }

                }
                messages.clear();
                messages.addAll(filter);
                return filter.size();



    }
}
