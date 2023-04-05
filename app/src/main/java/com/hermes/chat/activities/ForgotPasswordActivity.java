package com.hermes.chat.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hermes.chat.BaseApplication;
import com.hermes.chat.R;
import com.hermes.chat.models.Country;
import com.hermes.chat.models.User;
import com.hermes.chat.utils.Helper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ForgotPasswordActivity extends AppCompatActivity {
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.layout_signin_country_hint_TXT)
    TextView layoutSigninCountryHintTXT;
    @BindView(R.id.countryCode)
    SearchableSpinner spinnerCountryCodes;
    @BindView(R.id.layout_signin_mob_number_hint_TXT)
    TextView layoutSigninMobNumberHintTXT;
    @BindView(R.id.layout_registration_country_code_TXT)
    TextView layoutRegistrationCountryCodeTXT;
    @BindView(R.id.phoneNumber)
    EditText phoneNumber;
    @BindView(R.id.btnSubmit)
    Button btnSubmit;
    @BindView(R.id.mobileNumberLayout)
    RelativeLayout mobileNumberLayout;
    @BindView(R.id.otpLayout)
    RelativeLayout otpLayout;
    @BindView(R.id.btnOtpSubmit)
    Button btnOtpSubmit;
    @BindView(R.id.otp)
    EditText otp;
    @BindView(R.id.btnPwdSubmit)
    Button btnPwdSubmit;
    @BindView(R.id.passwordLayout)
    RelativeLayout passwordLayout;
    @BindView(R.id.resend)
    TextView resend;
    @BindView(R.id.otp_expires_txt)
    TextView otpExpiresTxt;
    @BindView(R.id.newPassword)
    EditText newPassword;
    private ProgressDialog progressDialog;
    private String mVerificationId;
    private FirebaseAuth mAuth;
    private CountDownTimer countDownTimer;
    private String phoneNumberStr;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ButterKnife.bind(this);

        uiInit();
    }

    private void uiInit() {
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        setupCountryCodes();

        try {
            title.setText(Helper.getLoginData(ForgotPasswordActivity.this).getLblForgotPassword());
            layoutSigninCountryHintTXT.setText(Helper.getLoginData(ForgotPasswordActivity.this).getLblCountry());
            layoutSigninMobNumberHintTXT.setText(Helper.getLoginData(ForgotPasswordActivity.this).getLblEnterMobileno());
            phoneNumber.setHint(Helper.getLoginData(ForgotPasswordActivity.this).getLblEnterMobileno());
            btnSubmit.setText(Helper.getChangePwd(ForgotPasswordActivity.this).getLblSubmit());
            otpExpiresTxt.setText(Helper.getLoginData(ForgotPasswordActivity.this).getLblResendOtp());
            resend.setText(Helper.getLoginData(ForgotPasswordActivity.this).getLblResendCode());
            btnOtpSubmit.setText(Helper.getChangePwd(ForgotPasswordActivity.this).getLblSubmit());
            newPassword.setHint(Helper.getChangePwd(ForgotPasswordActivity.this).getLblEnterNewPassowrd());
            btnPwdSubmit.setText(Helper.getChangePwd(ForgotPasswordActivity.this).getLblSubmit());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @OnClick({R.id.back, R.id.btnSubmit, R.id.btnOtpSubmit, R.id.btnPwdSubmit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.btnSubmit:
                phoneNumberStr = ((Country) spinnerCountryCodes.getSelectedItem()).getDialCode() +
                        phoneNumber.getText().toString().replaceAll("\\s+", "");
                if (((Country) spinnerCountryCodes.getSelectedItem()).getDialCode().isEmpty()) {
                    Toast.makeText(ForgotPasswordActivity.this,
                            Helper.getLoginData(
                                    ForgotPasswordActivity.this).getErrSelectCountryCode(), Toast.LENGTH_SHORT).show();
                    return;
                } else if (phoneNumber.getText().toString().replaceAll("\\s+", "").isEmpty()) {
                    Toast.makeText(ForgotPasswordActivity.this,
                            Helper.getLoginData(
                                    ForgotPasswordActivity.this).getErrEnterPhoneNumber(), Toast.LENGTH_SHORT).show();
                    return;
                }
                initiateAuth(phoneNumberStr);
                break;
            case R.id.btnOtpSubmit:
                String otpStr = otp.getText().toString();
                if (TextUtils.isEmpty(otpStr)) {
                    Toast.makeText(ForgotPasswordActivity.this,
                            Helper.getLoginData(
                                    ForgotPasswordActivity.this).getErrEnterOtp(), Toast.LENGTH_SHORT).show();
                    return;
                } else if (!TextUtils.isEmpty(mVerificationId))
                    signInWithPhoneAuthCredential(PhoneAuthProvider.getCredential(mVerificationId, otpStr));
                break;
            case R.id.btnPwdSubmit:
                if (newPassword.getText().toString().isEmpty()) {
                    Toast.makeText(ForgotPasswordActivity.this,
                            Helper.getLoginData(
                                    ForgotPasswordActivity.this).getLblEnterPassowrd(),
                            Toast.LENGTH_SHORT).show();
                } else if (newPassword.getText().toString().length() < 5) {
                    Toast.makeText(this, "Password must be more than 5",
                            Toast.LENGTH_LONG).show();
                } else {
                    updatePassword(newPassword.getText().toString());
                }
                break;
        }
    }

    private void updatePassword(String password) {
        BaseApplication.getUserRef().child(phoneNumberStr).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    try {
                        User user = dataSnapshot.getValue(User.class);
                        user.setPassword(password);
                        BaseApplication.getUserRef().child(phoneNumberStr).setValue(user)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(ForgotPasswordActivity.this, Helper.getLoginData(
                                                        ForgotPasswordActivity.this).getLblPasswordSuccess(),
                                                Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(ForgotPasswordActivity.this,
                                                UserNameSignInActivity.class));
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, Helper.getLoginData(
                                    ForgotPasswordActivity.this).getErrUserNotFound(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
                        Toast.makeText(ForgotPasswordActivity.this, "Something went wrong! The format of the phone number provided is incorrect\"",
                                Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(verificationId, forceResendingToken);
                        progressDialog.dismiss();
                        mVerificationId = verificationId;
                        mobileNumberLayout.setVisibility(View.GONE);
                        otpLayout.setVisibility(View.VISIBLE);
                        startCountdown();
                    }
                });

    }

    private void startCountdown() {
        resend.setOnClickListener(null);
        otpExpiresTxt.setText(Helper.getLoginData(
                ForgotPasswordActivity.this).getLblResendOtp());
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
                    otpExpiresTxt.setText(Helper.getLoginData(
                            ForgotPasswordActivity.this).getLblNotReceiveCode());
                    resend.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            initiateAuth(phoneNumberStr);
                        }
                    });
                }
            }
        }.start();
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        showProgress(2);
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    otpLayout.setVisibility(View.GONE);
                    passwordLayout.setVisibility(View.VISIBLE);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                if (e.getMessage() != null && e.getMessage().contains("invalid")) {
                    Toast.makeText(ForgotPasswordActivity.this, Helper.getLoginData(
                            ForgotPasswordActivity.this).getLblInvalidOtp(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, e.getMessage() != null ? "\n" +
                            e.getMessage() : "", Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();

            }
        });
    }

    private void showProgress(int i) {
        String title = (i == 1) ? Helper.getLoginData(
                ForgotPasswordActivity.this).getLblSendingOtp()
                : Helper.getLoginData(
                ForgotPasswordActivity.this).getLblVerifyingOtp();
        progressDialog.setTitle(title);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void setupCountryCodes() {
        ArrayList<Country> countries = Helper.getCountries(ForgotPasswordActivity.this);
        if (countries != null) {
            ArrayList<Country> aCountries1 = new ArrayList<>();
            Country country = new Country("", Helper.getLoginData(
                    ForgotPasswordActivity.this).getLblSelectCountry(), "");
            aCountries1 = Helper.getCountries(ForgotPasswordActivity.this);
            aCountries1.add(0, country);

            final ArrayAdapter<Country> adapter = new ArrayAdapter<Country>(ForgotPasswordActivity.this,
                    android.R.layout.simple_spinner_item, aCountries1);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCountryCodes.setAdapter(adapter);

            spinnerCountryCodes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0) {
                        layoutRegistrationCountryCodeTXT.setText("");
                    } else {
                        final String aDialCode = ((Country) spinnerCountryCodes.getSelectedItem()).getDialCode();
                        layoutRegistrationCountryCodeTXT.setText(aDialCode);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }
    }

}
