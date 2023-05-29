package com.hermes.chat.activities;

import static com.hermes.chat.utils.Helper.LANGUAGE;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.hermes.chat.BaseApplication;
import com.hermes.chat.R;
import com.hermes.chat.adapters.LanguageListAdapter;
import com.hermes.chat.models.LanguageListModel;
import com.hermes.chat.models.User;
import com.hermes.chat.utils.Helper;
import com.hermes.chat.utils.LocaleUtils;
import com.hermes.chat.utils.SessionHandler;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

public class UserNameSignInActivity extends AppCompatActivity {

    private static final String TAG = "UserNameSignInActivity";
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
    private DatabaseReference idChatRef;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_username_login);
        ButterKnife.bind(this);
        Log.d(TAG, "onCreate: created");
        SharedPreferences prefs = getSharedPreferences(String.valueOf(R.string.app_name), Context.MODE_PRIVATE);
        prefs.edit().putString("LoggedIn", "false");
        uiInit();
    }

    private void uiInit() {
        mAuth = FirebaseAuth.getInstance();
        Dexter.withActivity(this)
                .withPermissions(
                        /*Manifest.permission.READ_CONTACTS,*/
                        Manifest.permission.POST_NOTIFICATIONS,
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
                progressDialog.setMessage("Loading...");
//                fetchUsers();
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
                    "Flash Chat User Name", Toast.LENGTH_SHORT).show();
        } else if (password.getText().toString().isEmpty()) {
            Toast.makeText(UserNameSignInActivity.this,
                    "Password", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.show();
            mAuth.signInWithEmailAndPassword(username.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(
                            new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(
                                        @NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        fetchUsers();

                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(UserNameSignInActivity.this,
                                                "This user is not authorized, Please check email and password", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
        }
    }

    private void authenticate() {
        Log.d(TAG, "users count: " + users.size());
        for (User item : users) {
            Log.d(TAG, "validate: " + item.getIs_new() + " user email " + item.getEmail() + " === new " + item.getAdminblock());
            if (item.getEmail().equals(username.getText().toString().trim())) {
                if (!item.getAdminblock()) {
                    isFound = true;
//                    if (item.getPassword().equals(password.getText().toString())) {
                    helper.setLoggedInUser(item);
                    helper.setEmail(item.getEmail());
                    helper.setUserName(item.getUsername());
//                    helper.setPassword(item.getPassword());
                    helper.setPhoneNumberForVerification(item.getId());
                    progressDialog.dismiss();
                    callLang(item.getIs_new());


                    return;
                    /*} else {
                        progressDialog.dismiss();
                        isFound = false;
                        Toast.makeText(UserNameSignInActivity.this, Helper.
                                        getLoginData(UserNameSignInActivity.this).getErrPasswordNotCorrect(),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }*/
                } else {
                    progressDialog.dismiss();
                    isFound = false;
                    Toast.makeText(UserNameSignInActivity.this, "Blocked by Admin",
                            Toast.LENGTH_SHORT).show();
                    try {
//                idChatRef.removeEventListener(valueEventListener);
                        helper.setCacheMyUsers(new ArrayList<>());
                        FirebaseAuth.getInstance().signOut();
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(Helper.BROADCAST_LOGOUT));
//                                    sinchServiceInterface.stopClient();
                        helper.clearPhoneNumberForVerification();
                        helper.logout();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        Helper.getRealmInstance().executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                realm.deleteAll();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    fetchUsers();
                    return;
                }

            }
               /* if (item.getUsername().equals(username.getText().toString())) {
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
                }*/
        }
        if (!isFound) {
            progressDialog.dismiss();
            Toast.makeText(UserNameSignInActivity.this, "Flash Chat User not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchUsers() {
        users = new ArrayList<>();
        progressDialog.show();

        /*BaseApplication.getUserRef().addValueEventListener(new ValueEventListener()
                                                           {
                                                               @Override
                                                               public void onDataChange(@NonNull DataSnapshot snapshot)
                                                               {
                                                                   String print = "";
                                                                   User user = snapshot.getValue(User.class);
                                                                   print = print + "\n" + "user: "+user.getUsername() +"  pass: "+ user.getPassword() + " new "+ user.getIs_new()+" block "+user.getAdminblock();
                                                                   Log.d(TAG, "onDataChange: "+print);
                                                               }

                                                               @Override
                                                               public void onCancelled(@NonNull DatabaseError error)
                                                               {

                                                               }
                                                           });*/

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
                                            if (user.getId() != null) {
                                                /*if(user.getDisappearing_list()!=null && user.getDisappearing_list().size()>0){
                                                    RealmList<HashMap<String,String>> disappList = new RealmList<>();
                                                    for(int i=0;i<user.getDisappearing_list().size();i++){
                                                        HashMap<String,String> list= user.getDisappearing_list().get(i);
                                                        disappList.add(list);
                                                    }
                                                    user.setDisappearing_list(disappList);

                                                }*/
                                                users.add(user);
                                                print = print + "\n" + "user: " + user.getUsername() + "  pass: " + user.getPassword() + " new " + user.getIs_new() + " block " + user.getAdminblock();
                                                Log.d(TAG, "onDataChange: " + print);
                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    authenticate();
                                    Log.d(TAG, "users count: " + users.size());

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Log.d(TAG, "onDataChange: catch");
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
        progressDialog.dismiss();
    }

    private void callLang(int is_new) {


        if (!SessionHandler.getInstance().getBoolean(UserNameSignInActivity.this, LANGUAGE)) {
//                    loadLanguageDialog(items);
            setLocale("English-en", "en", 0, is_new);

        } else {
            try {
              //  fetchUsers();
                if (is_new == 1) {
                    SharedPreferences prefs = getSharedPreferences(String.valueOf(R.string.app_name), Context.MODE_PRIVATE);
                    prefs.edit().putString("LoggedIn", "true").apply();
                    prefs.edit().putString("Password", password.getText().toString()).apply();
                    Intent changePass = new Intent(UserNameSignInActivity.this, ChangePasswordActivity.class);
                    changePass.putExtra("from", "login");
                    startActivity(changePass);
                    finish();
                } else {
                    SharedPreferences prefs = getSharedPreferences(String.valueOf(R.string.app_name), Context.MODE_PRIVATE);
                    prefs.edit().putString("LoggedIn", "true").apply();
                    prefs.edit().putString("Password", password.getText().toString()).apply();
                    startActivity(new Intent(UserNameSignInActivity.this, MainActivity.class));
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

//        BaseApplication.getLangRef().addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Log.e("Key", "onChildAdded: ");
//                HashMap<Object, Object> list = new HashMap<>();
//                list = (HashMap<Object, Object>) dataSnapshot.getValue();
//                items = new ArrayList<>();
//                for (Object keys : list.keySet()) {
//                    items.add((String) keys);
//                }
//                dismiss();
//
//                if (!SessionHandler.getInstance().getBoolean(UserNameSignInActivity.this, LANGUAGE)) {
////                    loadLanguageDialog(items);
//
//                } else {
//
//                    try {
//                        Helper.getRealmInstance().executeTransaction(new Realm.Transaction() {
//                            @Override
//                            public void execute(Realm realm) {
//                                realm.deleteAll();
//                            }
//                        });
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                    try {
//                        title.setText(Helper.getLoginData(UserNameSignInActivity.this).getLblLogin());
//                        //username.setHint(Helper.getLoginData(UserNameSignInActivity.this).getLblUsername());
//                        //New...
//                        username.setHint(Helper.getLoginData(UserNameSignInActivity.this).getLblEmail());
//                        password.setHint(Helper.getLoginData(UserNameSignInActivity.this).getLblPassword());
//                        btnSubmit.setText(Helper.getLoginData(UserNameSignInActivity.this).getLblLogin());
//                        btnRegister.setText(Helper.getLoginData(UserNameSignInActivity.this).getLblRegister());
////                        forgotPassword.setText(Helper.getLoginData(UserNameSignInActivity.this).getLblForgotPassword());
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//                fetchUsers();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
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

    public void setLocale(String localeName, String code, int position, int is_new) {
        Log.d(TAG, "setLocale: " + localeName + " === " + code + " === " + position);
        pos = position;
        showPDialog();
        SessionHandler.getInstance().saveBoolean(UserNameSignInActivity.this, LANGUAGE, true);
        LocaleUtils.setLocale(new Locale(code));
        LocaleUtils.updateConfigActivity(this, getBaseContext().getResources().getConfiguration());
        getLanguageData(localeName, is_new);
        dismiss();

    }


    public void setLocale(String localeName, String code) {
        showPDialog();
        SessionHandler.getInstance().saveBoolean(UserNameSignInActivity.this, LANGUAGE, true);
        LocaleUtils.setLocale(new Locale(code));
        LocaleUtils.updateConfigActivity(this, getBaseContext().getResources().getConfiguration());
        if (dialog != null)
            dialog.dismiss();
        //getLanguageData(localeName);
    }

    private void getLanguageData(String localeName, int is_new) {
        showPDialog();

        BaseApplication.getLangRef().child(localeName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Object object = dataSnapshot.getValue(Object.class);
                String json = new Gson().toJson(object);
                LanguageListModel languageListModel = new Gson().fromJson(json, LanguageListModel.class);
                Helper.setLangInPref(languageListModel, UserNameSignInActivity.this);
                dismiss();

                if (is_new == 1) {
                    SharedPreferences prefs = getSharedPreferences(String.valueOf(R.string.app_name), Context.MODE_PRIVATE);
                    prefs.edit().putString("LoggedIn", "true").apply();
                    prefs.edit().putString("Password", password.getText().toString()).apply();
                    Intent changePass = new Intent(UserNameSignInActivity.this, ChangePasswordActivity.class);
                    changePass.putExtra("from", "login");
                    startActivity(changePass);
                    finish();
                } else {
                    SharedPreferences prefs = getSharedPreferences(String.valueOf(R.string.app_name), Context.MODE_PRIVATE);
                    prefs.edit().putString("LoggedIn", "true").apply();
                    prefs.edit().putString("Password", password.getText().toString()).apply();
                    startActivity(new Intent(UserNameSignInActivity.this, MainActivity.class));
                    finish();
                }
               /* startActivity(new Intent(UserNameSignInActivity.this, helper.getLoggedInUser()
                        != null ? MainActivity.class : UserNameSignInActivity.class));
                finish();*/

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Log.d(TAG, "onCancelled: " + databaseError);
            }
        });
        dismiss();
    }
}
