package com.hermes.chat.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.hermes.chat.BaseApplication;
import com.hermes.chat.R;
import com.hermes.chat.models.Country;
import com.hermes.chat.models.User;
import com.hermes.chat.utils.Helper;
import com.hermes.chat.utils.KeyboardUtil;
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
import com.google.firebase.messaging.FirebaseMessaging;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SignInActivity extends AppCompatActivity {
    private SearchableSpinner spinnerCountryCodes;
    private EditText etPhone;
    private static final int REQUEST_CODE_SMS = 123;
    private Helper helper;
    private EditText otpCode;
    private KeyboardUtil keyboardUtil;
    private String phoneNumberInPrefs = null, verificationCode = null;
    private ProgressDialog progressDialog;
    private TextView verificationMessage, retryTimer, myOtpexpiresTXT;
    private CountDownTimer countDownTimer;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseAuth mAuth;
    private boolean authInProgress;
    private TextView myCountryCodeTXT;
    private String deviceToken = "";
    private ArrayList<User> users;
    private EditText username;
    private EditText email;
    private EditText password;

    private TextView layout_registration_hint_TXT;
    private TextView layout_signin_country_hint_TXT;
    private TextView layout_signin_mob_number_hint_TXT;
    private TextView tvEmail;
    private TextView tvUsername;
    private TextView tvPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helper = new Helper(this);

        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.VIBRATE,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.MODIFY_AUDIO_SETTINGS,
                        Manifest.permission.READ_PHONE_STATE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();

        User user = helper.getLoggedInUser();
     /*   if (user != null) {    //Check if user if logged in
//            done();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {*/
        //init vars
        phoneNumberInPrefs = helper.getPhoneNumberForVerification();
        keyboardUtil = KeyboardUtil.getInstance(this);
        progressDialog = new ProgressDialog(this);

        //if there is number to authenticate in preferences then initiate
        setContentView(TextUtils.isEmpty(phoneNumberInPrefs) ? R.layout.activity_sign_in_1 : R.layout.activity_sign_in_2);
        if (TextUtils.isEmpty(phoneNumberInPrefs)) {
            //setup number selection
            spinnerCountryCodes = findViewById(R.id.countryCode);
            etPhone = findViewById(R.id.phoneNumber);
            username = findViewById(R.id.username);
            email = findViewById(R.id.email);
            password = findViewById(R.id.password);
            myCountryCodeTXT = findViewById(R.id.layout_registration_country_code_TXT);
            layout_registration_hint_TXT = findViewById(R.id.layout_registration_hint_TXT);
            layout_signin_country_hint_TXT = findViewById(R.id.layout_signin_country_hint_TXT);
            layout_signin_mob_number_hint_TXT = findViewById(R.id.layout_signin_mob_number_hint_TXT);
            tvEmail = findViewById(R.id.tvEmail);
            tvUsername = findViewById(R.id.tvUsername);
            tvPassword = findViewById(R.id.tvPassword);

            try {
                layout_registration_hint_TXT.setText(Helper.getLoginData(SignInActivity.this)
                        .getLblEnterMobileno());
                layout_signin_country_hint_TXT.setText(Helper.getLoginData(SignInActivity.this)
                        .getLblCountry());
                layout_signin_mob_number_hint_TXT.setText(Helper.getLoginData(SignInActivity.this)
                        .getLblYourMobNo());
                etPhone.setHint(Helper.getLoginData(SignInActivity.this)
                        .getLblYourMobNo());
                tvEmail.setText(Helper.getLoginData(SignInActivity.this)
                        .getLblEmail());
                email.setHint(Helper.getLoginData(SignInActivity.this)
                        .getLblEmail());
                tvUsername.setText(Helper.getLoginData(SignInActivity.this)
                        .getLblUsername());
                username.setHint(Helper.getLoginData(SignInActivity.this)
                        .getLblUsername());
                tvPassword.setText(Helper.getLoginData(SignInActivity.this)
                        .getLblPassword());
                password.setHint(Helper.getLoginData(SignInActivity.this)
                        .getLblPassword());


                username.setText(helper.getUserName());
                email.setText(helper.getEmail());
                password.setText(helper.getPassword());
            } catch (Exception e) {
                e.printStackTrace();
            }


            setupCountryCodes();
            fetchUsers();

            etPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (!b) {
                        for (User items : users) {
                            if (items.getId() != null && items.getId().equalsIgnoreCase(((Country) spinnerCountryCodes
                                    .getSelectedItem()).getDialCode()
                                    + etPhone.getText().toString().replaceAll("\\s+", ""))) {
                                Toast.makeText(SignInActivity.this,
                                        Helper.getLoginData(SignInActivity.this).getErrMobAlreadyRegistered(), Toast.LENGTH_SHORT).show();
                                etPhone.setText("");
                                return;
                            }
                        }
                    }
                }
            });

            username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        for (User items : users) {
                            if (!items.getUsername().isEmpty() && items.getUsername().equals(username.getText().toString())) {
                                Toast.makeText(SignInActivity.this, Helper.getLoginData(
                                        SignInActivity.this).getErrUsernameAlreadyExist(), Toast.LENGTH_SHORT).show();
                                // username.requestFocus();
                                username.setText("");
                                return;
                            }
                        }
                    }
                }
            });

            email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (!b) {
                        for (User items : users) {
                            if (!items.getEmail().isEmpty() && items.getEmail().equals(email.getText().toString())) {
                                Toast.makeText(SignInActivity.this, "Email already exist",
                                        Toast.LENGTH_SHORT).show();
                                // username.requestFocus();
                                email.setText("");
                                return;
                            }
                        }
                    }
                }
            });

        } else {
            //initiate authentication
            mAuth = FirebaseAuth.getInstance();
            retryTimer = findViewById(R.id.resend);
            myOtpexpiresTXT = findViewById(R.id.otp_expires_txt);
            verificationMessage = findViewById(R.id.verificationMessage);
            otpCode = findViewById(R.id.otp);
            findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    back();
                }
            });
            findViewById(R.id.changeNumber).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    helper.clearPhoneNumberForVerification();
                    recreate();
                }
            });
            initiateAuth(phoneNumberInPrefs);
        }
        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(phoneNumberInPrefs)) {
                    submit();
                } else {
                    //force authenticate

                    String otp = otpCode.getText().toString();
                    if (!TextUtils.isEmpty(otp) && !TextUtils.isEmpty(mVerificationId))
                        signInWithPhoneAuthCredential(PhoneAuthProvider.getCredential(mVerificationId, otp));

                    //verifyOtp(otpCode[0].getText().toString() + otpCode[1].getText().toString() + otpCode[2].getText().toString() + otpCode[3].getText().toString());
                }
            }
        });
        //  }

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isComplete()) {
                    String token = task.getResult();
                    Log.i("FCM Token", token);
                    deviceToken = token;
                }

            }
        });
     /*   FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this,
                new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {

                    }
                });*/
    }

    private void showProgress(int i) {
        String title = (i == 1) ? Helper.getLoginData(
                SignInActivity.this).getLblSendingOtp() : Helper.getLoginData(
                SignInActivity.this).getLblVerifyingOtp();
        String message = (i == 1) ? (Helper.getLoginData(
                SignInActivity.this).getLblSentTo() + "\n" + phoneNumberInPrefs) : Helper.getLoginData(
                SignInActivity.this).getLblVerifyingOtp();
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
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
                        signInWithPhoneAuthCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        authInProgress = false;
                        progressDialog.dismiss();
                        countDownTimer.cancel();
                        Log.e("ERR_MESSAGE", "Something went wrong" + ((e.getMessage() != null) ? ("\n" + e.getMessage()) : ""));
                        if ((e.getMessage() != null) && e.getMessage().contains("E.164")) {
                            verificationMessage.setText(Helper.getLoginData(
                                    SignInActivity.this).getLblIncorrect());
                        } else {
                            verificationMessage.setText(Helper.getLoginData(
                                    SignInActivity.this).getLblWrongMsg() + ((e.getMessage() != null) ? ("\n" + e.getMessage()) : ""));
                        }

                        retryTimer.setVisibility(View.VISIBLE);
                        retryTimer.setText(Helper.getLoginData(
                                SignInActivity.this).getLblResendCode());
                        myOtpexpiresTXT.setText(Helper.getLoginData(
                                SignInActivity.this).getLblNotReceiveCode());
                        retryTimer.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                initiateAuth(phoneNumberInPrefs);
                            }
                        });
//                        Toast.makeText(SignInActivity.this, "Something went wrong something went wrong! The format of the phone number provided is incorrect", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(verificationId, forceResendingToken);
                        authInProgress = true;
                        progressDialog.dismiss();
                        mVerificationId = verificationId;
                        mResendToken = forceResendingToken;
                        myOtpexpiresTXT.setText(Helper.getLoginData(
                                SignInActivity.this).getLblResendOtp());
                        verificationMessage.setText(String.format(Helper.getLoginData(
                                SignInActivity.this).getLblSentTo() + " %s", phoneNumberInPrefs));
//                        retryTimer.setVisibility(View.GONE);
                    }
                });
        startCountdown();
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        showProgress(2);
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    progressDialog.setMessage(Helper.getLoginData(
                            SignInActivity.this).getLblLoggingYouIn());
                    login();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                if (e.getMessage() != null && e.getMessage().contains("invalid")) {
                    Toast.makeText(SignInActivity.this, Helper.getLoginData(
                            SignInActivity.this).getLblInvalidOtp(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(SignInActivity.this, e.getMessage() != null ? "\n" + e.getMessage() : "", Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
                authInProgress = false;
            }
        });
    }

    private void login() {
        authInProgress = true;

        BaseApplication.getUserRef().child(phoneNumberInPrefs).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    try {
                        User user = dataSnapshot.getValue(User.class);
                        user.setDeviceToken(deviceToken);
                        user.setOsType(getString(R.string.osType));
                        if (User.validate(user)) {
                            helper.setLoggedInUser(user);
                            done();
                        } else {
                            createUser(new User(phoneNumberInPrefs, phoneNumberInPrefs,
                                    getString(R.string.default_status) + " " + getString(R.string.app_name),
                                    "", deviceToken, getString(R.string.osType), helper.getUserName(), helper.getPassword(),
                                    helper.getEmail(), ""));
                        }
                    } catch (Exception ex) {
                        createUser(new User(phoneNumberInPrefs, phoneNumberInPrefs,
                                getString(R.string.default_status) + " " + getString(R.string.app_name),
                                "", deviceToken, getString(R.string.osType), helper.getUserName(), helper.getPassword(),
                                helper.getEmail(), ""));
                    }
                } else {
                    createUser(new User(phoneNumberInPrefs, phoneNumberInPrefs,
                            getString(R.string.default_status) + " " + getString(R.string.app_name),
                            "", deviceToken, getString(R.string.osType), helper.getUserName(), helper.getPassword(),
                            helper.getEmail(), ""));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void createUser(final User newUser) {
        BaseApplication.getUserRef().child(phoneNumberInPrefs).setValue(newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                helper.setLoggedInUser(newUser);
                done();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignInActivity.this, "Something went wrong, unable to create user.", Toast.LENGTH_LONG).show();
            }
        });
    }


    private void back() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(Helper.getLoginData(
                SignInActivity.this).getLblCancelVerify());
        builder.setMessage(Helper.getLoginData(
                SignInActivity.this).getLblVerifyInprogress());
        builder.setNegativeButton(Helper.getLoginData(
                SignInActivity.this).getLblYes(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                helper.clearPhoneNumberForVerification();
                recreate();
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton(Helper.getLoginData(
                SignInActivity.this).getLblNo(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        if (progressDialog.isShowing() || authInProgress) {
            builder.create().show();
        } else {
            helper.clearPhoneNumberForVerification();
            recreate();
        }
    }

    private void startCountdown() {
        retryTimer.setOnClickListener(null);
        myOtpexpiresTXT.setText(Helper.getLoginData(
                SignInActivity.this).getLblResendOtp());
        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long l) {
                if (retryTimer != null) {
                    retryTimer.setText(String.valueOf(l / 1000));
                }
            }

            @Override
            public void onFinish() {
                if (retryTimer != null) {
                    retryTimer.setText("Resend");
                    myOtpexpiresTXT.setText(Helper.getLoginData(
                            SignInActivity.this).getLblNotReceiveCode());
                    retryTimer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            initiateAuth(phoneNumberInPrefs);
                        }
                    });
                }
            }
        }.start();
    }

    private void setupCountryCodes() {
        ArrayList<Country> countries = Helper.getCountries(SignInActivity.this);
        if (countries != null) {

            ArrayList<Country> aCountries1 = new ArrayList<>();
            Country country = new Country("", Helper.getLoginData(
                    SignInActivity.this).getLblSelectCountry(), "");

            aCountries1 = Helper.getCountries(SignInActivity.this);
            aCountries1.add(0, country);
//            aCountries1.addAll(aCountries1);

            final ArrayAdapter<Country> adapter = new ArrayAdapter<Country>(SignInActivity.this,
                    android.R.layout.simple_spinner_item, aCountries1);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCountryCodes.setAdapter(adapter);

            spinnerCountryCodes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0) {
                        myCountryCodeTXT.setText("");
                    } else {
                        final String aDialCode = ((Country) spinnerCountryCodes.getSelectedItem()).getDialCode();
                        myCountryCodeTXT.setText(aDialCode);
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }


    }

    //Go to main activity
    private void done() {
        startActivity(new Intent(this, PrivacyPolicyActivity.class));
        finish();
    }

    public void submit() {
        //Validate and confirm number country codes selected
        try {
            if (spinnerCountryCodes.getSelectedItem() == null ||
                    (myCountryCodeTXT.getText().toString().trim().equalsIgnoreCase(""))) {
                Toast.makeText(this, Helper.getLoginData(
                                SignInActivity.this).getErrSelectCountryCode(),
                        Toast.LENGTH_LONG).show();
            } else if (TextUtils.isEmpty(etPhone.getText().toString())) {
                Toast.makeText(this, Helper.getLoginData(
                                SignInActivity.this).getErrEnterPhoneNumber(),
                        Toast.LENGTH_LONG).show();
            } else if (TextUtils.isEmpty(email.getText().toString())) {
                Toast.makeText(this, Helper.getLoginData(
                                SignInActivity.this).getErrEnterEmail(),
                        Toast.LENGTH_LONG).show();
            } else if (!Helper.isValidEmail(email.getText().toString())) {
                Toast.makeText(this, Helper.getLoginData(
                                SignInActivity.this).getErrEnterValidEmail(),
                        Toast.LENGTH_LONG).show();
            } else if (TextUtils.isEmpty(username.getText().toString())) {
                Toast.makeText(this, Helper.getLoginData(
                                SignInActivity.this).getLblEnterUsername(),
                        Toast.LENGTH_LONG).show();
            } else if (TextUtils.isEmpty(password.getText().toString())) {
                Toast.makeText(this, Helper.getLoginData(
                                SignInActivity.this).getLblEnterPassowrd(),
                        Toast.LENGTH_LONG).show();
            } else if (password.getText().toString().length() < 5) {
                Toast.makeText(this, "Password must be more than 5",
                        Toast.LENGTH_LONG).show();
            } else {
                for (User items : users) {
                    if (items.getId() != null) {
                        if (items.getId().equalsIgnoreCase(((Country) spinnerCountryCodes
                                .getSelectedItem()).getDialCode()
                                + etPhone.getText().toString().replaceAll("\\s+", ""))) {
                            Toast.makeText(SignInActivity.this, Helper.getLoginData(
                                            SignInActivity.this).getErrMobAlreadyRegistered(),
                                    Toast.LENGTH_SHORT).show();
                            etPhone.setText("");
                            return;
                        } else if (items.getUsername().equals(username.getText().toString())) {
                            Toast.makeText(SignInActivity.this, Helper.getLoginData(
                                            SignInActivity.this).getErrUsernameAlreadyExist(),
                                    Toast.LENGTH_SHORT).show();
                            //username.requestFocus();
                            username.setText("");
                            return;
                        } else if (items.getEmail().equals(email.getText().toString())) {
                            Toast.makeText(SignInActivity.this, "Email already exist",
                                    Toast.LENGTH_SHORT).show();
                            // username.requestFocus();
                            email.setText("");
                            return;
                        }
                    }
                }
                final String phoneNumber = ((Country) spinnerCountryCodes.getSelectedItem()).getDialCode() + etPhone.getText().toString().replaceAll("\\s+", "");

                if (isValidPhoneNumber(etPhone.getText().toString().replaceAll("\\s+", ""))) {
                /*boolean status = validateUsing_libphonenumber(((Country) spinnerCountryCodes.getSelectedItem()).getCode(),
                        etPhone.getText().toString().replaceAll("\\s+", ""));*/
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(phoneNumber);
                    builder.setMessage(Helper.getLoginData(
                            SignInActivity.this).getErrOtpSent());
                    builder.setPositiveButton(Helper.getLoginData(
                            SignInActivity.this).getLblYes(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            helper.setPhoneNumberForVerification(phoneNumber);
                            helper.setUserName(username.getText().toString());
                            helper.setEmail(email.getText().toString());
                            helper.setPassword(password.getText().toString());
                            recreate();
                            dialogInterface.dismiss();
                        }
                    });
                    builder.setNegativeButton(Helper.getLoginData(
                            SignInActivity.this).getLblEdit(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            etPhone.requestFocus();
                            keyboardUtil.openKeyboard();
                            dialogInterface.dismiss();
                        }
                    });
                    builder.create().show();

                } else {
                    Toast.makeText(this, Helper.getLoginData(
                            SignInActivity.this).getErrInvalidMobNo(), Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, Helper.getLoginData(
                    SignInActivity.this).getErrInvalidMobNo(), Toast.LENGTH_LONG).show();
        }
    }

    private boolean isValidPhoneNumber(CharSequence phoneNumber) {
        if (!TextUtils.isEmpty(phoneNumber)) {
            return Patterns.PHONE.matcher(phoneNumber).matches();
        }
        return false;
    }

    private void fetchUsers() {
        users = new ArrayList<>();
        progressDialog.show();
        BaseApplication.getUserRef()
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                progressDialog.dismiss();
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    try {
                                        User user = snapshot.getValue(User.class);
                                        users.add(user);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
    }

}
