package com.hermes.chat.pushnotification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiInterface {

    @POST("fcm/send")
    Call<Message> fcmSend(@Header("Authorization") String token, @Body Message message);

}