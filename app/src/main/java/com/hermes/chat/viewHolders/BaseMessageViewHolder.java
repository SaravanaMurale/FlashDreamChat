package com.hermes.chat.viewHolders;

import static com.hermes.chat.adapters.MessageAdapter.OTHER;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hermes.chat.R;
import com.hermes.chat.interfaces.OnMessageItemClick;
import com.hermes.chat.models.AttachmentTypes;
import com.hermes.chat.models.DownloadFileEvent;
import com.hermes.chat.models.Message;
import com.hermes.chat.models.User;
import com.hermes.chat.utils.GeneralUtils;
import com.hermes.chat.utils.Helper;

import java.util.ArrayList;
import java.util.HashMap;



public class BaseMessageViewHolder extends RecyclerView.ViewHolder {
    protected static int lastPosition;
    public static boolean animate;
    protected static View newMessageView;
    private int attachmentType;
    protected static Context context;
    private String nameToDisplay = "";
    private static int _48dpInPx = -1;
    private Message message;
    private OnMessageItemClick itemClickListener;
    private HashMap<String, User> myUsersNameInPhoneMap;

    TextView time, senderName;
    CardView cardView;
    private static ImageView statusImg;
    private static RelativeLayout statusLay;
    static TextView statusText;
    private ArrayList<Message> messages;
    public LinearLayout linearLayoutMessageText;
    public FrameLayout parentLayout;

    public BaseMessageViewHolder(View itemView, OnMessageItemClick itemClickListener, ArrayList<Message> messages) {
        super(itemView);
        this.messages = messages;
        if (itemClickListener != null)
            this.itemClickListener = itemClickListener;
        context = itemView.getContext();
        time = itemView.findViewById(R.id.time);
        senderName = itemView.findViewById(R.id.senderName);
        statusImg = itemView.findViewById(R.id.statusImg);
        statusText = itemView.findViewById(R.id.statusText);
        statusLay = itemView.findViewById(R.id.statusLay);
        cardView = itemView.findViewById(R.id.card_view);
        linearLayoutMessageText = itemView.findViewById(R.id.ll_parent_message_text);
        parentLayout = itemView.findViewById(R.id.parentLayout);
        if (_48dpInPx == -1) _48dpInPx = GeneralUtils.dpToPx(itemView.getContext(), 48);
    }

    public BaseMessageViewHolder(View itemView, int attachmentType, OnMessageItemClick itemClickListener) {
        super(itemView);
        this.itemClickListener = itemClickListener;
        this.attachmentType = attachmentType;
    }

    public BaseMessageViewHolder(View itemView, View newMessage, OnMessageItemClick itemClickListener, ArrayList<Message> messages) {
        this(itemView, itemClickListener, messages);
        this.itemClickListener = itemClickListener;
        if (newMessageView == null) newMessageView = newMessage;
    }

    protected boolean isMine() {
        return (getItemViewType() & OTHER) != OTHER;
    }

    public void setData(final Message message, int position, final HashMap<String, User> myUsersNameInPhoneMap,
                        ArrayList<User> myUsers) {
        try {
            this.message = message;

            if (attachmentType == AttachmentTypes.NONE_TYPING)
                return;
            time.setText(Helper.getTime(message.getDate()));
            if (message.getRecipientId().startsWith(Helper.GROUP_PREFIX)) {
                nameToDisplay = message.getSenderName();
                if (myUsersNameInPhoneMap != null && myUsersNameInPhoneMap.containsKey(message.getSenderId())) {
                    nameToDisplay = myUsersNameInPhoneMap.get(message.getSenderId()).getNameToDisplay();
                }

                if (isMine())
                    senderName.setVisibility(View.VISIBLE);
                senderName.setText(isMine() ? "You" : nameToDisplay);

            } else {
                senderName.setVisibility(View.GONE);
            }
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) linearLayoutMessageText.getLayoutParams();
            if (isMine()) {
                if (!message.getRecipientId().startsWith(Helper.GROUP_PREFIX)) {
                    time.setCompoundDrawablesWithIntrinsicBounds(0, 0, message.isSent() ?
                            (message.isDelivered() ? (message.isReadMsg() ? R.drawable.ic_done_all_blue
                                    : R.drawable.ic_done_all_black) : R.drawable.ic_done_black) :
                            R.drawable.ic_waiting, 0);
                }
                layoutParams.gravity = Gravity.RIGHT;
                layoutParams.leftMargin = _48dpInPx;
                time.setTextColor(context.getResources().getColor(android.R.color.black));
                //time.setCompoundDrawablesWithIntrinsicBounds(0, 0, message.isSent() ? (message.isDelivered() ? R.drawable.ic_done_all_black : R.drawable.ic_done_black) : R.drawable.ic_waiting, 0);


            } else {
                time.setTextColor(context.getResources().getColor(android.R.color.black));
                layoutParams.gravity = Gravity.LEFT;
                layoutParams.rightMargin = _48dpInPx;
//                itemView.startAnimation(AnimationUtils.makeInAnimation(itemView.getContext(), true));
            }

            /*if (message.getStatusUrl() != null && !message.getStatusUrl().isEmpty()) {
                statusLay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        StatusStoriesActivity.FROM = true;
                        Intent a = new Intent(context, StatusStoriesActivity.class);
                        a.putExtra(StatusStoriesActivity.STATUS_RESOURCES_KEY,
                                new String[]{message.getStatusUrl()});
                        a.putExtra(StatusStoriesActivity.STATUS_DURATION_KEY, 5000L);
                        a.putExtra(StatusStoriesActivity.IS_IMMERSIVE_KEY, true);
                        a.putExtra(StatusStoriesActivity.IS_CACHING_ENABLED_KEY, true);
                        a.putExtra(StatusStoriesActivity.IS_TEXT_PROGRESS_ENABLED_KEY, false);
                        a.putExtra(StatusStoriesActivity.USER_NAME,
                                myUsersNameInPhoneMap.get(message.getRecipientId()).getNameToDisplay());
                        a.putExtra(StatusStoriesActivity.URL,
                                myUsersNameInPhoneMap.get(message.getRecipientId()).getImage());
                        a.putExtra(StatusStoriesActivity.RECIPIENT_ID, "");
                        context.startActivity(a);
                    }
                });
            }*/
            linearLayoutMessageText.setLayoutParams(layoutParams);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void callIntent(String[] statusUrl, String nameToDisplay) {

    }

    void onItemClick(boolean b) {
        if (itemClickListener != null && message != null) {
            if (b)
                itemClickListener.OnMessageClick(message, getAdapterPosition());
            else
                itemClickListener.OnMessageLongClick(message, getAdapterPosition());
        }
    }

    void broadcastDownloadEvent(DownloadFileEvent downloadFileEvent) {
        Intent intent = new Intent(Helper.BROADCAST_DOWNLOAD_EVENT);
        intent.putExtra("data", downloadFileEvent);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    void broadcastDownloadEvent() {
        Intent intent = new Intent(Helper.BROADCAST_DOWNLOAD_EVENT);
        intent.putExtra("data", new DownloadFileEvent(message.getAttachmentType(), message.getAttachment(), getAdapterPosition()));
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public boolean checkPermission() {
        int result1 = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(context, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1003);
        }
    }
}