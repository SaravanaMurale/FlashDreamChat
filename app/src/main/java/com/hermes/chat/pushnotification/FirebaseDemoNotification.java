package com.hermes.chat.pushnotification;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.hermes.chat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;


public class FirebaseDemoNotification extends AppCompatActivity {

    private static final String TAG = "FirebaseDemoNotificatio";
    EditText edtTitle;
    EditText edtMessage;
    String toToken = "";
    String[] registration_ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_notification);
        edtTitle = findViewById(R.id.edtTitle);
        edtMessage = findViewById(R.id.edtMessage);
        Button btnSend = findViewById(R.id.btnSend);

       /* FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isComplete()) {
                    String token = task.getResult();
                    Log.i("FCM Token", token);
                    toToken = token;
                }

            }
        });*/

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TAG", "FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        toToken = task.getResult();
                        Log.d(TAG, "onComplete: "+toToken);

                    }
                });


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //   registration_ids = new String[]{toToken};
                Log.d(TAG, "onClick: "+toToken);
                registration_ids = new String[]{toToken,
                        "cDFc59XDVkJWqSJimaNgTd:APA91bHk6KlaUC0V3_MZU_urywOZb-lUKqfLsswJ1eTS9WZ71KJWBEEARgE_d7MyiraMkMWjfpTezeK3bUiCaDUEBYVTuw3E5V62T8U2cWq1Zt56hgsL86RJNtDGn6bEhFdES-lcfudT"};
                new SendFirebaseNotification(FirebaseDemoNotification.this,
                        edtTitle.getText().toString(),
                        edtMessage.getText().toString(),
                        registration_ids)
                        .triggerMessage();
            }
        });
    }
}

