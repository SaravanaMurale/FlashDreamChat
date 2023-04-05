package com.hermes.chat.pushnotification;

import android.content.Context;
import android.util.Log;

import com.hermes.chat.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendFirebaseNotification {
    private String TAG = "NOTIFICATION TAG";
    private String NOTIFICATION_TITLE, NOTIFICATION_MESSAGE;
    private Message notificationTask;
    private String[] registration_ids;
    private Context mContext;

    public SendFirebaseNotification(Context context, String NOTIFICATION_TITLE,
                                    String NOTIFICATION_MESSAGE, String[] registration_ids) {
        this.mContext = context;
        this.NOTIFICATION_TITLE = NOTIFICATION_TITLE;
        this.NOTIFICATION_MESSAGE = NOTIFICATION_MESSAGE;
        this.registration_ids = registration_ids;
    }

    public SendFirebaseNotification(Context context, Message notificationTask){
        this.mContext = context;
        this.notificationTask = notificationTask;
    }

    public void triggerMessage() {
        try {
            sendNotification(notificationTask);
        } catch (Exception e) {
            Log.e(TAG, "onCreate: " + e.getMessage());
        }
    }

   /* public void triggerMessage() {
        try {
            Notification notification = new Notification(NOTIFICATION_TITLE, NOTIFICATION_MESSAGE);
            //   Data data = new Data(NOTIFICATION_MESSAGE, NOTIFICATION_TITLE);

            String json = "{\"apns\": {\n" +
                    "         \"payload\": {\n" +
                    "             \"aps\": {\n" +
                    "                 \"mutable-content\": 1\n" +
                    "             }\n" +
                    "         },\n" +
                    "         \"fcm_options\": {\n" +
                    "             \"image\": \" \"\n" +
                    "         }\n" +
                    "       }\n" +
                    "}";

            JSONObject jsonObject = new JSONObject(json);


            Data data = new Data(NOTIFICATION_TITLE, NOTIFICATION_MESSAGE,
                    " ");

            notificationTask = new Message(registration_ids, "", data, notification,
                    new apns(new apns.FcmOptions(" ")));
            sendNotification(notificationTask);
        } catch (Exception e) {
            Log.e(TAG, "onCreate: " + e.getMessage());
        }
    }*/

    private void sendNotification(Message notification) {
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        Call<Message> call = apiService.fcmSend("key=" + mContext.getString(R.string.firebase_server_key), notification);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                Log.d("success", "success");
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                Log.d("onFailure", "onFailure");
            }
        });
    }
}