package com.hermes.chat.activities;

import static com.hermes.chat.utils.Helper.LANGUAGE;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hermes.chat.BaseApplication;
import com.hermes.chat.R;
import com.hermes.chat.adapters.LanguageListAdapter;
import com.hermes.chat.models.LanguageListModel;
import com.hermes.chat.models.User;
import com.hermes.chat.utils.Helper;
import com.hermes.chat.utils.LocaleUtils;
import com.hermes.chat.utils.SessionHandler;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserNameSignInActivity extends AppCompatActivity {

    private static final String TAG ="UserNameSignInActivity" ;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.username)
    EditText username;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.btnSubmit)
    Button btnSubmit;
    @BindView(R.id.btnRegister)
    Button btnRegister;
    @BindView(R.id.forgotPassword)
    TextView forgotPassword;
    private ArrayList<User> users;
    private ProgressDialog progressDialog;
    private boolean isFound = false;
    private Helper helper;
    ProgressDialog dlg;
    android.app.AlertDialog dialog;
    List<String> items;
    public static int pos = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_username_login);
        ButterKnife.bind(this);
        Log.d(TAG, "onCreate: created");
        uiInit();
    }

    private void uiInit() {

        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.VIBRATE,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT,
                        Manifest.permission.MODIFY_AUDIO_SETTINGS,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.READ_MEDIA_AUDIO,
                        Manifest.permission.READ_MEDIA_VIDEO)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        callLang();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();
        helper = new Helper(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);


    }


    @OnClick({R.id.btnSubmit, R.id.btnRegister, R.id.forgotPassword})
    public void onViewClicked(View view) {
        Log.d(TAG, "onViewClicked: ");
        switch (view.getId()) {
            case R.id.btnSubmit:
                progressDialog.setMessage(Helper.getLoginData(UserNameSignInActivity.this).getLblEnterLoading());
                validate();
                break;
            case R.id.btnRegister:
                Log.d(TAG, "onViewClicked: ");
                startActivity(new Intent(UserNameSignInActivity.this, SignInActivity.class));
                break;
            case R.id.forgotPassword:
                startActivity(new Intent(UserNameSignInActivity.this, ForgotPasswordActivity.class));
                break;
        }
    }

    private void validate() {
        if (username.getText().toString().isEmpty()) {
            Toast.makeText(UserNameSignInActivity.this,
                    Helper.getLoginData(UserNameSignInActivity.this).getLblUsername(), Toast.LENGTH_SHORT).show();
        } else if (password.getText().toString().isEmpty()) {
            Toast.makeText(UserNameSignInActivity.this,
                    Helper.getLoginData(UserNameSignInActivity.this).getLblPassword(), Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.show();
            for (User item : users) {
                if (item.getUsername().equals(username.getText().toString())) {
                    isFound = true;
                    if (item.getPassword().equals(password.getText().toString())) {
                        helper.setLoggedInUser(item);
                        helper.setEmail(item.getEmail());
                        helper.setUserName(item.getUsername());
                        helper.setPassword(item.getPassword());
                        helper.setPhoneNumberForVerification(item.getId());
                        progressDialog.dismiss();
                        startActivity(new Intent(UserNameSignInActivity.this, MainActivity.class));
                        finish();
                        return;
                    } else {
                        progressDialog.dismiss();
                        isFound = false;
                        Toast.makeText(UserNameSignInActivity.this, Helper.
                                        getLoginData(UserNameSignInActivity.this).getErrPasswordNotCorrect(),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
            if (!isFound) {
                progressDialog.dismiss();
                Toast.makeText(UserNameSignInActivity.this, Helper.
                        getLoginData(UserNameSignInActivity.this).getErrUserNotFound(), Toast.LENGTH_SHORT).show();
            }
        }
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
                                try {
                                    String print = "";
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        try {
                                            User user = snapshot.getValue(User.class);
                                            if (user.getId() != null){
                                                users.add(user);
                                                print = print + "\n" + "user: "+user.getUsername() +"  pass: "+ user.getPassword();
                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    Log.d(TAG, "onDataChangevalue: "+print);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Log.d(TAG, "onDataChange: catch");
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
    }

    private void callLang() {
        showPDialog();
        BaseApplication.getLangRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("Key", "onChildAdded: ");
                HashMap<Object, Object> list = new HashMap<>();
                list = (HashMap<Object, Object>) dataSnapshot.getValue();
                items = new ArrayList<>();
                for (Object keys : list.keySet()) {
                    items.add((String) keys);
                }
                dismiss();

                if (!SessionHandler.getInstance().getBoolean(UserNameSignInActivity.this, LANGUAGE)) {
                    loadLanguageDialog(items);
                } else {
                    fetchUsers();
                    try {
                        title.setText(Helper.getLoginData(UserNameSignInActivity.this).getLblLogin());
                        //username.setHint(Helper.getLoginData(UserNameSignInActivity.this).getLblUsername());
                        //New...
                        username.setHint(Helper.getSettingsData(UserNameSignInActivity.this).getLblProfileName());
                        password.setHint(Helper.getLoginData(UserNameSignInActivity.this).getLblPassword());
                        btnSubmit.setText(Helper.getLoginData(UserNameSignInActivity.this).getLblLogin());
                        btnRegister.setText(Helper.getLoginData(UserNameSignInActivity.this).getLblRegister());
                        forgotPassword.setText(Helper.getLoginData(UserNameSignInActivity.this).getLblForgotPassword());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void loadLanguageDialog(List<String> languageList) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(UserNameSignInActivity.this);
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_language_list, null);
        alertDialogBuilder.setView(view);
        alertDialogBuilder.setCancelable(false);
        RecyclerView rvLanguageList = view.findViewById(R.id.rv_languagelist);
        TextView title = view.findViewById(R.id.title);
        //  title.setText(getSettingsData(getActivity()).getLblChangeLanguage());
        rvLanguageList.setLayoutManager(new LinearLayoutManager(UserNameSignInActivity.this,
                LinearLayoutManager.VERTICAL, false));
        rvLanguageList.setHasFixedSize(true);
        rvLanguageList.setAdapter(new LanguageListAdapter(UserNameSignInActivity.this, languageList,
                UserNameSignInActivity.this));

        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.50);

        dialog = alertDialogBuilder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void showPDialog() {
        dlg = new ProgressDialog(UserNameSignInActivity.this);
        dlg.setMessage("Loading...");
        dlg.setCancelable(false);
        dlg.show();
    }

    private void dismiss() {
        if (dlg != null && dlg.isShowing()) {
            dlg.dismiss();
        }
    }

    public void setLocale(String localeName, String code, int position) {
        pos = position;
        showPDialog();
        SessionHandler.getInstance().saveBoolean(UserNameSignInActivity.this, LANGUAGE, true);
        LocaleUtils.setLocale(new Locale(code));
        LocaleUtils.updateConfigActivity(this, getBaseContext().getResources().getConfiguration());
        if (dialog != null)
            dialog.dismiss();
        getLanguageData(localeName);
    }


    public void setLocale(String localeName, String code) {
        showPDialog();
        SessionHandler.getInstance().saveBoolean(UserNameSignInActivity.this, LANGUAGE, true);
        LocaleUtils.setLocale(new Locale(code));
        LocaleUtils.updateConfigActivity(this, getBaseContext().getResources().getConfiguration());
        if (dialog != null)
            dialog.dismiss();
        getLanguageData(localeName);
    }

    private void getLanguageData(String localeName) {

        BaseApplication.getLangRef().child(localeName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Object object = dataSnapshot.getValue(Object.class);
                String json = new Gson().toJson(object);
                LanguageListModel languageListModel = new Gson().fromJson(json, LanguageListModel.class);
                Helper.setLangInPref(languageListModel, UserNameSignInActivity.this);
                dismiss();
                startActivity(new Intent(UserNameSignInActivity.this, helper.getLoggedInUser()
                        != null ? MainActivity.class : UserNameSignInActivity.class));
                finish();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
