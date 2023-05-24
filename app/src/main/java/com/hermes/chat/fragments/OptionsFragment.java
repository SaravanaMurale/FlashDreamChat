package com.hermes.chat.fragments;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.hermes.chat.utils.Helper.LANGUAGE;
import static com.hermes.chat.utils.Helper.getProfileData;
import static com.hermes.chat.utils.Helper.getSettingsData;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hermes.chat.BaseApplication;
import com.hermes.chat.BuildConfig;
import com.hermes.chat.R;
import com.hermes.chat.activities.BaseActivity;
import com.hermes.chat.activities.ChangePasswordActivity;
import com.hermes.chat.activities.MainActivity;
import com.hermes.chat.activities.ProfileActivity;
import com.hermes.chat.activities.SplashActivity;
import com.hermes.chat.activities.UserNameSignInActivity;
import com.hermes.chat.adapters.DisappearingListAdapter;
import com.hermes.chat.adapters.LanguageListAdapter;
import com.hermes.chat.models.Attachment;
import com.hermes.chat.models.AttachmentTypes;
import com.hermes.chat.models.LanguageListModel;
import com.hermes.chat.models.User;
import com.hermes.chat.services.FirebaseChatService;
import com.hermes.chat.utils.ConfirmationDialogFragment;
import com.hermes.chat.utils.FirebaseUploader;
import com.hermes.chat.utils.Helper;
import com.hermes.chat.utils.LocaleUtils;
import com.hermes.chat.utils.SessionHandler;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.kbeanie.multipicker.api.CameraImagePicker;
import com.kbeanie.multipicker.api.ImagePicker;
import com.kbeanie.multipicker.api.Picker;
import com.kbeanie.multipicker.api.callbacks.ImagePickerCallback;
import com.kbeanie.multipicker.api.entity.ChosenImage;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;


public class OptionsFragment extends BaseFullDialogFragment implements ImagePickerCallback {
    private static final String TAG = "OptionsFragment";
    protected String[] permissionsCamera = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private User userMe;
    private ImagePicker imagePicker;
    private CameraImagePicker cameraPicker;
    private String pickerPath;
    private static String CONFIRM_TAG = "confirmtag";
    private static String PRIVACY_TAG = "privacytag";
    private static final int REQUEST_CODE_MEDIA_PERMISSION = 999;
    private static final int REQUEST_CODE_PICKER = 4321;
    private ImageView userImage;
    private Helper helper;
    // private DatabaseReference usersRef, dataRef;
//    private SinchService.SinchServiceInterface sinchServiceInterface;
    ProgressBar myProgressBar;
    private Context context;
    private boolean fromFlag = false;
    TextView tvVersion;
    private DatabaseReference idChatRef;
    private EditText userName;
    private TextView userStatus;

    ProgressDialog dlg;
    AlertDialog dialog;

    private TextView title;
    private TextView wallpaper;
    private TextView changePassword;
    private TextView changeLanguage;
    private TextView share, disappearing_message;
    private TextView rate;
    private TextView privacy;
    private TextView invite_friends;
    private TextView contact;
    private TextView logout;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helper = new Helper(getContext());
        userMe = helper.getLoggedInUser();
        if(userMe.getAdminblock()){
            checkBlocked();
        }
        idChatRef = BaseApplication.getUserRef().child(userMe.getId());
        idChatRef.addValueEventListener(valueEventListener);
//        sinchServiceInterface = BaseActivity.mSinchServiceInterface;
    }

    private void checkBlocked()
    {


          /*  BaseApplication.getUserRef().child(userMe.getId()).addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    User user= snapshot.getValue(User.class);
                    if(user.getAdminblock()){*/
        try {
//                idChatRef.removeEventListener(valueEventListener);
            helper.setCacheMyUsers(new ArrayList<>());
            FirebaseAuth.getInstance().signOut();
            LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(new Intent(Helper.BROADCAST_LOGOUT));
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
        SharedPreferences prefs = context.getSharedPreferences(String.valueOf(R.string.app_name), Context.MODE_PRIVATE);
        prefs.edit().putString("LoggedIn","false");
        Intent mIntent = new Intent(context, UserNameSignInActivity.class);
        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mIntent);
        requireActivity().finish();
                   /* }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error)
                {

                }
            });*/


    }

    @Override
    public void onResume() {
        super.onResume();
        userName.setText(userMe.getNameToDisplay());

        userStatus.setText(userMe.getStatus());

        if (userMe.getImage() != null && !userMe.getImage().isEmpty())
            Picasso.get()
                    .load(userMe.getImage())
                    .tag(this)
                    .error(R.drawable.ic_avatar)
                    .placeholder(R.drawable.ic_avatar)
                    .into(userImage);
        else
            userImage.setBackgroundResource(R.drawable.ic_avatar);
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            try
            {
                userMe = dataSnapshot.getValue(User.class);
                helper.setLoggedInUser(userMe);
            }catch (Exception e){

            }

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.i("TAG", databaseError.getMessage());
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_options, container);
        userImage = view.findViewById(R.id.userImage);
        myProgressBar = view.findViewById(R.id.progressBar);
        userName = view.findViewById(R.id.userName);
        userStatus = view.findViewById(R.id.userStatus);
        tvVersion = view.findViewById(R.id.tv_version);

        title = view.findViewById(R.id.title);
        wallpaper = view.findViewById(R.id.wallpaper);
//        disappearing_message = view.findViewById(R.id.disappearing_message);
        share = view.findViewById(R.id.share);
        rate = view.findViewById(R.id.rate);
        privacy = view.findViewById(R.id.privacy);
        invite_friends = view.findViewById(R.id.invite_friends);
        contact = view.findViewById(R.id.contact);
        logout = view.findViewById(R.id.logout);
        changeLanguage = view.findViewById(R.id.changeLanguage);
        changePassword = view.findViewById(R.id.changePassword);
        disappearing_message = view.findViewById(R.id.disappearing_message);


        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(getActivity()
                    .getPackageName(), 0);
            String version = pInfo.versionName;
            tvVersion.setText("Version : " + version);

            title.setText(getSettingsData(getContext()).getLblSettings());
            wallpaper.setText(getSettingsData(getContext()).getLblWallpaper());
            share.setText(getSettingsData(getContext()).getLblShareApp());
            rate.setText(getSettingsData(getContext()).getLblRateApp());
            privacy.setText(getSettingsData(getContext()).getLblPrivacy());
            invite_friends.setText(getSettingsData(getContext()).getLblInvite());
            contact.setText(getSettingsData(getContext()).getLblContactUs());
            logout.setText(getSettingsData(getContext()).getLblLogout());
            changeLanguage.setText(getSettingsData(getContext()).getLblChangeLanguage());
            changePassword.setText(getSettingsData(getContext()).getLblChangePassword());
        } catch (Exception e) {
            e.printStackTrace();
        }

        view.findViewById(R.id.done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Helper.closeKeyboard(getContext(), view);
                updateUserNameAndStatus(userName.getText().toString().trim(), userStatus.getText()
                        .toString().trim());
            }
        });
       /* userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fromFlag = false;
                pickProfileImage();
            }
        });*/
        view.findViewById(R.id.imageLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, ProfileActivity.class));
            }
        });
        view.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Helper.closeKeyboard(getContext(), view);
                dismiss();
            }
        });
        view.findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Helper.openShareIntent(getContext(), null,
                        getSettingsData(getContext()).getLblShareAppContent() +
                                "https://play.google.com/store/apps/details?id=" +
                                BuildConfig.APPLICATION_ID);
            }
        });
        view.findViewById(R.id.rate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Helper.openPlayStore(getContext());
            }
        });
        view.findViewById(R.id.contact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Helper.openSupportMail(getContext());
            }
        });
        view.findViewById(R.id.invite_friends).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.invitation_title));
                    shareIntent.putExtra(Intent.EXTRA_TEXT, getSettingsData(getContext())
                            .getLblInviteContent() + "\n"
                            + "https://play.google.com/store/apps/details?id=" +
                            BuildConfig.APPLICATION_ID);
                    startActivity(Intent.createChooser(shareIntent, "Share using.."));
                } catch (Exception ignored) {
                }

            }
        });

        view.findViewById(R.id.privacy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new PrivacyPolicyDialogFragment().show(getChildFragmentManager(), PRIVACY_TAG);
            }
        });
        view.findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConfirmationDialogFragment confirmationDialogFragment = ConfirmationDialogFragment.newInstance(
                        getSettingsData(getContext()).getLblLogout(),
                        getSettingsData(getContext()).getLblWantToLogout(),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                try {
                                    idChatRef.removeEventListener(valueEventListener);
                                    helper.setCacheMyUsers(new ArrayList<>());
                                    FirebaseAuth.getInstance().signOut();
                                    LocalBroadcastManager.getInstance(getContext()).sendBroadcast(new Intent(Helper.BROADCAST_LOGOUT));
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

                                FirebaseChatService.isChatServiceStarted = false;
                                BaseActivity.firebaseChatService.removeListener();

                                Intent mIntent = new Intent(getContext(), UserNameSignInActivity.class);
                                mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(mIntent);
                                getActivity().finish();
                            }
                        },
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                            }
                        });
                confirmationDialogFragment.show(getChildFragmentManager(), CONFIRM_TAG);
            }
        });

        view.findViewById(R.id.wallpaper).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromFlag = true;
                pickWallpaperImage();
            }
        });

        view.findViewById(R.id.changePassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent changeIntent = new Intent(requireContext(), ChangePasswordActivity.class);
                changeIntent.putExtra("from", "option");
                startActivity(changeIntent);
            }
        });

        view.findViewById(R.id.disappearing_message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callDisappearingMessage();
            }
        });

        changeLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callLang();
            }
        });
        return view;
    }

    private void callDisappearingMessage()
    {
        {
            showPDialog();
            BaseApplication.getDisappearRef().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.e("Key", "onChildAdded: ");
                    HashMap<Object, Object> list = new HashMap<>();
                    list = (HashMap<Object, Object>) dataSnapshot.getValue();
                    List<String> items = new ArrayList<>();
                    for (Object keys : list.keySet()) {
                        items.add((String) keys);
                    }
                    dismissDialog();
                    loadDisappearing(items);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }

    private void loadDisappearing(List<String> items)
    {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        List<String> data = new ArrayList<>();

        for(int i =0;i<items.size();i++ ){
            data.add(items.get(i));
        }
        data.add("Turn off");
        items.clear();
        items.addAll(data);
        LayoutInflater inflater = (LayoutInflater) getActivity().
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_language_list, null);
        alertDialogBuilder.setView(view);
        alertDialogBuilder.setCancelable(false);
        RecyclerView rvLanguageList = view.findViewById(R.id.rv_languagelist);
        TextView title = view.findViewById(R.id.title);
          title.setText("Choose Options");
        rvLanguageList.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false));
        rvLanguageList.setHasFixedSize(true);
        rvLanguageList.setAdapter(new DisappearingListAdapter(getActivity(), items,
                OptionsFragment.this));

        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.50);

        dialog = alertDialogBuilder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
    public void addToSharedPref(String msg){
        Log.d(TAG, "addToSharedPref: "+msg);
//        userMe.setDisappearing_message();
        BaseApplication.getUserRef().child(userMe.getId()).child("disappearing_message").setValue(msg);
        /*SharedPreferences prefs = requireContext().getSharedPreferences(String.valueOf(R.string.app_name), Context.MODE_PRIVATE);
        prefs.edit().putString("disappear", msg).apply();*/
        dialog.dismiss();
    }

    private void callLang() {
        showPDialog();
        BaseApplication.getLangRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("Key", "onChildAdded: ");
                HashMap<Object, Object> list = new HashMap<>();
                list = (HashMap<Object, Object>) dataSnapshot.getValue();
                List<String> items = new ArrayList<>();
                for (Object keys : list.keySet()) {
                    items.add((String) keys);
                }
                dismissDialog();
                loadLanguageDialog(items);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void showPDialog() {
        dlg = new ProgressDialog(getActivity());
        dlg.setMessage("Loading. . .");
        dlg.setCancelable(false);
        dlg.show();
    }

    private void dismissDialog() {
        if (dlg != null && dlg.isShowing()) {
            dlg.dismiss();
        }
    }

    private void updateUserNameAndStatus(String updatedName, String updatedStatus) {
        if (TextUtils.isEmpty(updatedName)) {
            Toast.makeText(getContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(updatedStatus)) {
            Toast.makeText(getContext(), "Status cannot be empty", Toast.LENGTH_SHORT).show();
        } else if (!userMe.getName().equals(updatedName) || !userMe.getStatus().equals(updatedStatus)) {
            userMe.setName(updatedName);
            userMe.setStatus(updatedStatus);
            BaseApplication.getUserRef().child(userMe.getId()).setValue(userMe).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    if (helper != null)
                        helper.setLoggedInUser(userMe);
                    toast("Updated!");
                }
            });
        } else {
            Toast.makeText(getContext(), "Details already updated!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_MEDIA_PERMISSION) {
            if (mediaPermissions().isEmpty()) {
                pickProfileImage();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Picker.PICK_IMAGE_DEVICE:
                    if (imagePicker == null) {
                        imagePicker = new ImagePicker(this);
                    }
                    imagePicker.submit(data);
                    break;
                case Picker.PICK_IMAGE_CAMERA:
                    if (cameraPicker == null) {
                        cameraPicker = new CameraImagePicker(this);
                        cameraPicker.reinitialize(pickerPath);
                    }
                    cameraPicker.submit(data);
                    break;
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                try {
                    wallpaperUploadTask(result.getUri(), AttachmentTypes.IMAGE, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(getActivity(), "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void userImageUploadTask(final File fileToUpload, @AttachmentTypes.AttachmentType final int attachmentType, final Attachment attachment) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(getString(R.string.app_name)).child("ProfileImage").child(userMe.getId());
        FirebaseUploader firebaseUploader = new FirebaseUploader(new FirebaseUploader.UploadListener() {
            @Override
            public void onUploadFail(String message) {
                myProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onUploadSuccess(String downloadUrl) {
                userMe = helper.getLoggedInUser();
                myProgressBar.setVisibility(View.GONE);
                userMe.setImage(downloadUrl);
                helper.setLoggedInUser(userMe);
                BaseApplication.getUserRef().child(userMe.getId()).setValue(userMe).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (helper != null)
                            helper.setLoggedInUser(userMe);
                        toast("Profile image updated");
                    }
                });
            }

            @Override
            public void onUploadProgress(int progress) {
                Log.e("IMAGE_PROGRESS", "" + progress);
            }

            @Override
            public void onUploadCancelled() {

            }
        }, storageReference);
        firebaseUploader.setReplace(true);
        firebaseUploader.uploadImage(getContext(), fileToUpload);
    }

    private void wallpaperUploadTask(final Uri fileToUpload, @AttachmentTypes.AttachmentType final int attachmentType, final Attachment attachment) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child(getString(R.string.app_name)).child("Wallpaper").child(userMe.getId());
        FirebaseUploader firebaseUploader = new FirebaseUploader(new FirebaseUploader.UploadListener() {
            @Override
            public void onUploadFail(String message) {
                myProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onUploadSuccess(String downloadUrl) {
                userMe = helper.getLoggedInUser();
                myProgressBar.setVisibility(View.GONE);
                userMe.setWallpaper(downloadUrl);
                helper.setLoggedInUser(userMe);
                BaseApplication.getUserRef().child(userMe.getId()).setValue(userMe).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (helper != null)
                            helper.setLoggedInUser(userMe);
                        toast("Wallpaper set successfully");
                    }
                });
            }

            @Override
            public void onUploadProgress(int progress) {
                Log.e("IMAGE_PROGRESS", "" + progress);
            }

            @Override
            public void onUploadCancelled() {

            }
        }, storageReference);
        firebaseUploader.setReplace(true);
        firebaseUploader.uploadImage(getContext(), fileToUpload);
    }

    private void toast(String message) {
        if (getContext() != null)
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void pickWallpaperImage() {
        if (mediaPermissions().isEmpty()) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
            alertDialog.setMessage(getProfileData(getActivity()).getLblGetImgFrom());
            alertDialog.setPositiveButton(getProfileData(getActivity()).getLblCamera(),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            CropImage.activity()
                                    .setGuidelines(CropImageView.Guidelines.ON)
                                    .setActivityTitle("Wallpaper")
                                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                                    .setCropMenuCropButtonTitle("Done")
                                    .setShowCropOverlay(true)
                                    .setAllowFlipping(false)
                                    .start(getActivity());
                        }
                    });
            alertDialog.setNegativeButton(getProfileData(getActivity()).getLblGallery(),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            CropImage.activity()
                                    .setGuidelines(CropImageView.Guidelines.ON)
                                    .setActivityTitle("Wallpaper")
                                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                                    .setCropMenuCropButtonTitle("Done")
                                    .setShowCropOverlay(true)
                                    .setAllowFlipping(false)
                                    .start(getActivity());
                        }
                    });
            alertDialog.setNeutralButton(getSettingsData(getActivity()).getLbl_remove_wallpaper(),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            userMe = helper.getLoggedInUser();
                            userMe.setWallpaper("");
                            helper.setLoggedInUser(userMe);
                            BaseApplication.getUserRef().child(userMe.getId()).setValue(userMe)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            if (helper != null)
                                                helper.setLoggedInUser(userMe);
                                            toast("Wallpaper removed");
                                        }
                                    });
                        }
                    });
            alertDialog.create().show();
        } else {
            requestPermissions(permissionsCamera, REQUEST_CODE_MEDIA_PERMISSION);
        }
    }

    private void pickProfileImage() {
        if (mediaPermissions().isEmpty()) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
            alertDialog.setMessage(getProfileData(getActivity()).getLblGetImgFrom());
            alertDialog.setPositiveButton(getProfileData(getActivity()).getLblCamera(),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();

                            cameraPicker = new CameraImagePicker(OptionsFragment.this);
                            cameraPicker.shouldGenerateMetadata(true);
                            cameraPicker.shouldGenerateThumbnails(true);
                            cameraPicker.setImagePickerCallback(OptionsFragment.this);
                            pickerPath = cameraPicker.pickImage();
                        }
                    });
            alertDialog.setNegativeButton(getProfileData(getActivity()).getLblGallery(), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();

                    imagePicker = new ImagePicker(OptionsFragment.this);
                    imagePicker.shouldGenerateMetadata(true);
                    imagePicker.shouldGenerateThumbnails(true);
                    imagePicker.setImagePickerCallback(OptionsFragment.this);
                    imagePicker.pickImage();
                }
            });
            alertDialog.create().show();
        } else {
            requestPermissions(permissionsCamera, REQUEST_CODE_MEDIA_PERMISSION);
        }
    }

    @Override
    public void onImagesChosen(List<ChosenImage> images) {
        File fileToUpload = new File(Uri.parse(images.get(0).getOriginalPath()).getPath());
//        Glide.with(this).load(fileToUpload).apply(new RequestOptions().placeholder(R.drawable.ic_logo_large)).into(userImage);
        if (fromFlag) {
            //   wallpaperUploadTask(fileToUpload, AttachmentTypes.IMAGE, null);
        } else {
            Picasso.get()
                    .load(fileToUpload)
                    .tag(this)
                    .placeholder(R.drawable.ic_avatar)
                    .into(userImage);
            myProgressBar.setVisibility(View.VISIBLE);
            userImageUploadTask(fileToUpload, AttachmentTypes.IMAGE, null);
        }
    }

    @Override
    public void onError(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // You have to save path in case your activity is killed.
        // In such a scenario, you will need to re-initialize the CameraImagePicker
        outState.putString("picker_path", pickerPath);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("picker_path")) {
                pickerPath = savedInstanceState.getString("picker_path");
            }
        }
    }

    private List<String> mediaPermissions() {
        List<String> missingPermissions = new ArrayList<>();
        for (String permission : permissionsCamera) {
            if (ActivityCompat.checkSelfPermission(getContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        return missingPermissions;
    }

  /*  public static OptionsFragment newInstance(SinchService.SinchServiceInterface sinchServiceInterface) {
        OptionsFragment fragment = new OptionsFragment();
        fragment.sinchServiceInterface = sinchServiceInterface;
        return fragment;
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((MainActivity) getActivity()).fragment = null;
    }

    public void loadLanguageDialog(List<String> languageList) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = (LayoutInflater) getActivity().
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_language_list, null);
        alertDialogBuilder.setView(view);
        alertDialogBuilder.setCancelable(false);
        RecyclerView rvLanguageList = view.findViewById(R.id.rv_languagelist);
        TextView title = view.findViewById(R.id.title);
        //  title.setText(getSettingsData(getActivity()).getLblChangeLanguage());
        rvLanguageList.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false));
        rvLanguageList.setHasFixedSize(true);
        rvLanguageList.setAdapter(new LanguageListAdapter(getActivity(), languageList,
                OptionsFragment.this));

        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.50);

        dialog = alertDialogBuilder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void setLocale(String localeName, String code) {
        showPDialog();
        LocaleUtils.setLocale(new Locale(code));
        LocaleUtils.updateConfigActivity(getActivity(), getActivity().getBaseContext().
                getResources().getConfiguration());
        SessionHandler.getInstance().saveBoolean(getActivity(), LANGUAGE, true);
        dialog.dismiss();
        getLanguageData(localeName);
    }

    private void getLanguageData(String localeName) {

        BaseApplication.getLangRef().child(localeName).addListenerForSingleValueEvent
                (new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Object object = dataSnapshot.getValue(Object.class);
                        String json = new Gson().toJson(object);
                        LanguageListModel languageListModel = new Gson().fromJson(json, LanguageListModel.class);
                        Helper.setLangInPref(languageListModel, getActivity());
                        dismissDialog();
                        Intent callAct = new Intent(getActivity(), SplashActivity.class);
                        callAct.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(callAct);
                        getActivity().finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

}
