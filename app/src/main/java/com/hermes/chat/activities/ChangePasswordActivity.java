package com.hermes.chat.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hermes.chat.BaseApplication;
import com.hermes.chat.R;
import com.hermes.chat.models.User;
import com.hermes.chat.utils.Helper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChangePasswordActivity extends AppCompatActivity {
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.oldPassword)
    EditText oldPassword;
    @BindView(R.id.newPassword)
    EditText newPassword;
    @BindView(R.id.confirmPassword)
    EditText confirmPassword;
    @BindView(R.id.btnSubmit)
    Button btnSubmit;
    private Helper helper;
    private ProgressDialog progressDialog;
    private AlertDialog alert11;
    private TextView resend;
    private Button btnDlgSubmit;
    private EditText otp;
    private TextView dialogTitle;
    private TextView myOtpExpiresTXT;
    private CountDownTimer countDownTimer;
    private String mVerificationId;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ButterKnife.bind(this);
        uiInit();
    }

    private void uiInit() {
        helper = new Helper(this);
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        try {
            title.setText(Helper.getChangePwd(ChangePasswordActivity.this).getLblChangePassword());
            btnSubmit.setText(Helper.getChangePwd(ChangePasswordActivity.this).getLblSubmit());
            oldPassword.setHint(Helper.getChangePwd(ChangePasswordActivity.this).getLblEnterCurrentPassowrd());
            newPassword.setHint(Helper.getChangePwd(ChangePasswordActivity.this).getLblEnterNewPassowrd());
            confirmPassword.setHint(Helper.getChangePwd(ChangePasswordActivity.this).getLblEnterConfirmPassowrd());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.back, R.id.btnSubmit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.btnSubmit:
                validate();
                break;
        }
    }

    private void validate() {
        if (oldPassword.getText().toString().isEmpty()) {
            Toast.makeText(ChangePasswordActivity.this,
                    Helper.getChangePwd(ChangePasswordActivity.this).getLblEnterCurrentPassowrd(), Toast.LENGTH_SHORT).show();
        } else if (newPassword.getText().toString().isEmpty()) {
            Toast.makeText(ChangePasswordActivity.this,
                    Helper.getChangePwd(ChangePasswordActivity.this).getLblEnterNewPassowrd(), Toast.LENGTH_SHORT).show();
        } else if (oldPassword.getText().toString().equals(newPassword.getText().toString())) {
            Toast.makeText(ChangePasswordActivity.this,
                    Helper.getChangePwd(ChangePasswordActivity.this).getErrCurrentNewNotSame(), Toast.LENGTH_SHORT).show();
        } else if (confirmPassword.getText().toString().isEmpty()) {
            Toast.makeText(ChangePasswordActivity.this,
                    Helper.getChangePwd(ChangePasswordActivity.this).getLblEnterConfirmPassowrd(), Toast.LENGTH_SHORT).show();
        } else if (!newPassword.getText().toString().equals(confirmPassword.getText().toString())) {
            Toast.makeText(ChangePasswordActivity.this,
                    Helper.getChangePwd(ChangePasswordActivity.this).getErrNewConfirmSame(), Toast.LENGTH_SHORT).show();
        } else if (!oldPassword.getText().toString().equalsIgnoreCase(helper.getPassword())) {
            Toast.makeText(ChangePasswordActivity.this,
                    Helper.getChangePwd(ChangePasswordActivity.this).getErrCurrentNotCorrect(), Toast.LENGTH_SHORT).show();
        } else if (newPassword.getText().toString().length() < 5 || confirmPassword.getText()
                .toString().length() < 5) {
            Toast.makeText(this, "Password must be more than 5",
                    Toast.LENGTH_LONG).show();
        } else {
            initiateAuth(helper.getPhoneNumberForVerification());
        }
    }

    private void initiateAuth(String phone) {
        showProgress(1);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phone, 60, TimeUnit.SECONDS, this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onCodeAutoRetrievalTimeOut(String s) {
                        super.onCodeAutoRetrievalTimeOut(s);
                    }

                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        progressDialog.dismiss();

                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        progressDialog.dismiss();
                        Toast.makeText(ChangePasswordActivity.this,
                                        "Something went wrong! The format of the phone number provided is incorrect", Toast.LENGTH_LONG)
                                .show();
                    }

                    @Override
                    public void onCodeSent(String verificationId, PhoneAuthProvider.
                            ForceResendingToken forceResendingToken) {
                        super.onCodeSent(verificationId, forceResendingToken);
                        progressDialog.dismiss();
                        mVerificationId = verificationId;
                        showDialog();

                    }
                });

    }


    private void showProgress(int i) {

        String title = (i == 1) ? Helper.getLoginData(ChangePasswordActivity.this).getLblSendingOtp()
                : Helper.getLoginData(ChangePasswordActivity.this).getLblVerifyingOtp();
        String message = (i == 1) ? (Helper.getLoginData(ChangePasswordActivity.this).getLblOtpSentTo() + "\n" +
                helper.getPhoneNumberForVerification()) : Helper.getLoginData(ChangePasswordActivity.this).getLblVerifyingOtp()
                + "...";

        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void showDialog() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = getLayoutInflater();
        final View view = layoutInflater.inflate(R.layout.dialog_change_password, null);
        otp = view.findViewById(R.id.otp);
        dialogTitle = view.findViewById(R.id.title);
        myOtpExpiresTXT = view.findViewById(R.id.otp_expires_txt);
        resend = view.findViewById(R.id.resend);
        btnDlgSubmit = view.findViewById(R.id.btnDlgSubmit);
        alert.setView(view);
        alert11 = alert.create();

        startCountdown();

        btnDlgSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otpStr = otp.getText().toString();
                if (TextUtils.isEmpty(otpStr)) {
                    Toast.makeText(ChangePasswordActivity.this,
                            Helper.getLoginData(ChangePasswordActivity.this).getErrEnterOtp(),
                            Toast.LENGTH_SHORT).show();
                } else if (!TextUtils.isEmpty(otpStr) && !TextUtils.isEmpty(mVerificationId))
                    signInWithPhoneAuthCredential(PhoneAuthProvider.getCredential(mVerificationId,
                            otpStr));
            }
        });


        alert11.show();
        Window window = alert11.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    private void startCountdown() {
        resend.setOnClickListener(null);
        myOtpExpiresTXT.setText(Helper.getLoginData(ChangePasswordActivity.this).getLblResendOtp());
        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long l) {
                if (resend != null) {
                    resend.setText(String.valueOf(l / 1000));
                }
            }

            @Override
            public void onFinish() {
                if (resend != null) {
                    resend.setText("Resend");
                    myOtpExpiresTXT.setText(Helper.getLoginData(ChangePasswordActivity.this).getLblNotReceiveCode());
                    resend.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            initiateAuth(helper.getPhoneNumberForVerification());
                        }
                    });
                }
            }
        }.start();
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        showProgress(2);
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(task -> {
            progressDialog.dismiss();
            if (task.isSuccessful()) {
                alert11.dismiss();
                updatePassword(confirmPassword.getText().toString());
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                if (e.getMessage() != null && e.getMessage().contains("invalid")) {
                    Toast.makeText(ChangePasswordActivity.this, Helper.getLoginData(
                                    ChangePasswordActivity.this).getLblInvalidOtp(),
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ChangePasswordActivity.this, e.getMessage() != null ? "\n" +
                            e.getMessage() : "", Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();

            }
        });
    }

    private void updatePassword(String password) {
        try {
            progressDialog.show();
            User user = helper.getLoggedInUser();
            user.setPassword(password);
            BaseApplication.getUserRef().child(helper.getPhoneNumberForVerification()).
                    setValue(user)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            helper.setPassword(password);
                            progressDialog.dismiss();
                            Toast.makeText(ChangePasswordActivity.this,
                                    Helper.getChangePwd(ChangePasswordActivity.this).getLblPwdSuccess(),
                                    Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ChangePasswordActivity.this,
                                    UserNameSignInActivity.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(ChangePasswordActivity.this,
                                    "Try again later",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
