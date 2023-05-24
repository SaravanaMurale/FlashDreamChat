package com.hermes.chat.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hermes.chat.BaseApplication;
import com.hermes.chat.R;
import com.hermes.chat.models.Attachment;
import com.hermes.chat.models.AttachmentTypes;
import com.hermes.chat.models.Contact;
import com.hermes.chat.models.Group;
import com.hermes.chat.models.Status;
import com.hermes.chat.models.User;
import com.hermes.chat.utils.FileUtils;
import com.hermes.chat.utils.FirebaseUploader;
import com.hermes.chat.utils.Helper;
import com.hermes.chat.utils.KeyboardUtil;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
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
import java.util.List;

import static com.hermes.chat.utils.Helper.getCommonPage;
import static com.hermes.chat.utils.Helper.getLoginData;
import static com.hermes.chat.utils.Helper.getSettingsData;

public class ProfileActivity extends BaseActivity implements ImagePickerCallback {
    protected String[] permissionsCamera = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private ImagePicker imagePicker;
    private String pickerPath;
    private CameraImagePicker cameraPicker;
    private static final int REQUEST_CODE_MEDIA_PERMISSION = 999;
    private ImageView profileImg;
    private ProgressBar myProgressBar;
    private TextView profileStatus;
    private TextView mobileNumber;
    private TextView profileName;
    private TextView userName;
    private AlertDialog alert11;

    private TextView title;
    private TextView profileTitle;
    private TextView profileStatusTitle;
    private TextView phoneTitle;
    private TextView userTitle;

    AlertDialog.Builder builder;
    File image;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        builder = new AlertDialog.Builder(this);
        uiInit();
    }

    private void uiInit() {
        profileImg = findViewById(R.id.profileImg);
        myProgressBar = findViewById(R.id.progressBar);
        profileStatus = findViewById(R.id.profileStatus);
        mobileNumber = findViewById(R.id.mobileNumber);
        profileName = findViewById(R.id.profileName);
        userName = findViewById(R.id.userName);

        title = findViewById(R.id.title);
        profileTitle = findViewById(R.id.profileTitle);
        profileStatusTitle = findViewById(R.id.profileStatusTitle);
        phoneTitle = findViewById(R.id.phoneTitle);
        userTitle = findViewById(R.id.userTitle);
        setLang();
        profileStatus.setText(userMe.getStatus());
        mobileNumber.setText(userMe.getId());
        profileName.setText(userMe.getProfileName());
        userName.setText(userMe.getUsername());

        loadImages();

        findViewById(R.id.changeProfile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.S) {
                    chooseDialog();
                }else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.R) {

                    chooseDialog();

                } else {
                    openImageClick();
                }
                /*CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setActivityTitle("Profile picture")
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .setCropMenuCropButtonTitle("Done")
                        .setShowCropOverlay(true)
                        .setAllowFlipping(false)
                        .start(ProfileActivity.this);*/
            }
        });

        findViewById(R.id.profileNameLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog("name", profileName.getText().toString());
            }
        });

        findViewById(R.id.aboutLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog("about", profileStatus.getText().toString());
            }
        });

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void openImageClick()
    {
    }

    private void chooseDialog() {
        builder.setMessage("Choose Image Picking Options") .setTitle("Choose Image")
                .setCancelable(true)
                .setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU) {
                            Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
                            intent.setType("image/*");
                            startActivityForResult(intent, 1537);
                        }
                        else {
                            androidRGallery();
                        }

                    }
                })
                .setNegativeButton("Camera", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                        File path = new File(getFilesDir(), "/FlashChat/Camera");
                        if (!path.exists()) path.mkdirs();
                        image = new File(path, FileUtils.getFileName());
                        Uri imageUri = FileProvider.getUriForFile(getApplicationContext(), getString(R.string.authority), image);
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                      /*  Bundle newExtras = new Bundle();
                        if (imageUri != null) {
                            newExtras.putParcelable(MediaStore.EXTRA_OUTPUT, imageUri);
                        } else {
                            newExtras.putBoolean("return-data", true);
                        }*/
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//                        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        //intent.putExtra(MediaStore.EXTRA_OUTPUT);
                       /* if(intent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(intent,
                                    1515);
                        }*/
                        startActivityForResult(intent, 1515);
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void androidRGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent,1537);
    }

    private void setLang() {
        title.setText(getSettingsData(ProfileActivity.this).getLblProfile());
        profileTitle.setText(getLoginData(ProfileActivity.this).getLblUsername());
        profileStatusTitle.setText(getSettingsData(ProfileActivity.this).getLblAbout());
        profileStatus.setHint(getSettingsData(ProfileActivity.this).getLblEnterStatus());
        phoneTitle.setText(getSettingsData(ProfileActivity.this).getLblPhone());
        userTitle.setText(getSettingsData(ProfileActivity.this).getLblProfileName());
    }


    private void pickProfileImage() {
        if (mediaPermissions().isEmpty()) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProfileActivity.this);
            alertDialog.setMessage("Get image from");
            alertDialog.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();

                    cameraPicker = new CameraImagePicker(ProfileActivity.this);
                    cameraPicker.shouldGenerateMetadata(true);
                    cameraPicker.shouldGenerateThumbnails(true);
                    cameraPicker.setImagePickerCallback(ProfileActivity.this);
                    pickerPath = cameraPicker.pickImage();
                }
            });
            alertDialog.setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();

                    imagePicker = new ImagePicker(ProfileActivity.this);
                    imagePicker.shouldGenerateMetadata(true);
                    imagePicker.shouldGenerateThumbnails(true);
                    imagePicker.setImagePickerCallback(ProfileActivity.this);
                    imagePicker.pickImage();
                }
            });
            alertDialog.create().show();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissionsCamera, REQUEST_CODE_MEDIA_PERMISSION);
            }
        }
    }


    private List<String> mediaPermissions() {
        List<String> missingPermissions = new ArrayList<>();
        for (String permission : permissionsCamera) {
            if (ActivityCompat.checkSelfPermission(ProfileActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        return missingPermissions;
    }

    @Override
    void myUsersResult(ArrayList<User> myUsers) {

    }

    @Override
    void myContactsResult(ArrayList<Contact> myContacts) {

    }

    @Override
    void userAdded(User valueUser) {

    }

    @Override
    void groupAdded(Group valueGroup) {

    }

    @Override
    void userUpdated(User valueUser) {

    }

    @Override
    void groupUpdated(Group valueGroup) {

    }

    @Override
    void statusAdded(Status status) {

    }

    @Override
    void statusUpdated(Status status) {

    }

    @Override
    void onSinchConnected() {

    }

    @Override
    void onSinchDisconnected() {

    }

    @Override
    public void onImagesChosen(List<ChosenImage> images) {
        File fileToUpload = new File(Uri.parse(images.get(0).getOriginalPath()).getPath());
        Picasso.get()
                .load(fileToUpload)
                .tag(this)
                .placeholder(R.drawable.ic_avatar)
                .into(profileImg);
        myProgressBar.setVisibility(View.VISIBLE);
        //   userImageUploadTask(fileToUpload, AttachmentTypes.IMAGE, null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
                case 201:
                    androidRGallery();
                    break;
                case 1515:
                    Log.d("OS_13","IMAGE");
                    onSelectFromGalleryResult(data,"Camera");
                    break;
                case 1537:
                    Log.d("OS_13","IMAGE");
                    onSelectFromGalleryResult(data,"Image");
                    break;
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                try {
                    myProgressBar.setVisibility(View.VISIBLE);
                    userImageUploadTask(result.getUri(), AttachmentTypes.IMAGE, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(ProfileActivity.this,
                        "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }
    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    private void onSelectFromGalleryResult(Intent data,String type) {
        try {
           Uri uri =null;
            String path = "";
            if (data != null || getDeviceName().contains("samsung")) {

//                String path = "";

                if (type.equalsIgnoreCase("Camera")) {
                    path = image.getAbsolutePath();
                    uri = Uri.fromFile(new File(path));
                }else {
                    uri = data.getData();
                    path = FileUtils.getRealPathFromURI(this,data.getData());
                }

                switch (type){
                    case "Image":
                    case "Camera":
                        uploadImage(uri);
                        break;

                }
            }else {
                if (type.equalsIgnoreCase("Camera")) {

                    path = image.getAbsolutePath();
                    uri = Uri.fromFile(new File(path));
                    uploadImage(uri);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void uploadImage(Uri filePath) {

        userImageUploadTask(filePath, AttachmentTypes.IMAGE, null);
    }

/*    private void newFileUploadTask(String filePath,
                                   @AttachmentTypes.AttachmentType final int attachmentType, final Attachment attachment) {
        if (myAttachmentLLY.getVisibility() == View.VISIBLE) {
            myAttachmentLLY.setVisibility(View.GONE);
            //  addAttachment.animate().setDuration(400).rotationBy(-45).start();
        }

        final File fileToUpload = new File(filePath);
        final String fileName = Uri.fromFile(fileToUpload).getLastPathSegment();

        Attachment preSendAttachment = attachment;//Create/Update attachment
        if (preSendAttachment == null) preSendAttachment = new Attachment();
        preSendAttachment.setName(fileName);
        preSendAttachment.setBytesCount(fileToUpload.length());
        preSendAttachment.setUrl("loading");
        prepareMessage(null, attachmentType, preSendAttachment);
        String timeFinal="Turn off";
        if(!userTime.equalsIgnoreCase("Turn off")){
            timeFinal = userTime;
        } else if (!time.equalsIgnoreCase("Turn off")){
            timeFinal = time;
        } else {
            timeFinal = userTime;
        }

        checkAndCopy("/" + getString(R.string.app_name) + "/" +
                AttachmentTypes.getTypeName(attachmentType) + "/.sent/", fileToUpload);//Make a copy

        Intent intent = new Intent(Helper.UPLOAD_AND_SEND);
        intent.putExtra("attachment", attachment);
        intent.putExtra("disappear",timeFinal);
        intent.putExtra("adminBlk",adminBlocked);
        if (group != null) {
            intent.putExtra("chatDataGroup", group);
        }
        intent.putExtra("userIds", attachment);
        intent.putExtra("attachment_type", attachmentType);
        intent.putExtra("attachment_file_path", filePath);
        intent.putExtra("attachment_file_path", filePath);
        intent.putExtra("attachment_recipient_id", userOrGroupId);
        intent.putExtra("attachment_chat_child", chatChild);
        intent.putExtra("attachment_reply_id", replyId);
        intent.putExtra("new_msg_id", newMsgID);
        intent.putExtra("statusUrl", "");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        replyLay.setVisibility(View.GONE);
        replyId = "0";
        KeyboardUtil.getInstance(this).closeKeyboard();
    }*/

    @Override
    public void onError(String s) {

    }

    private void userImageUploadTask(final Uri fileToUpload, @AttachmentTypes.AttachmentType final int attachmentType, final Attachment attachment) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child(getString(R.string.app_name)).child("ProfileImage").child(userMe.getId());
        FirebaseUploader firebaseUploader = new FirebaseUploader(new FirebaseUploader.UploadListener() {
            @Override
            public void onUploadFail(String message) {
                myProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onUploadSuccess(String downloadUrl) {
                myProgressBar.setVisibility(View.GONE);
                userMe.setImage(downloadUrl);
                helper.setLoggedInUser(userMe);
                BaseApplication.getUserRef().child(userMe.getId()).setValue(userMe).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (helper != null)
                            helper.setLoggedInUser(userMe);
                        Toast.makeText(ProfileActivity.this, getSettingsData(ProfileActivity.this).getLblProfileImgUpdated(),
                                Toast.LENGTH_SHORT).show();
                        loadImages();
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
        firebaseUploader.uploadImage(ProfileActivity.this, fileToUpload);
    }


    private void showDialog(final String name, String str) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = getLayoutInflater();
        final View view = layoutInflater.inflate(R.layout.dialog_group_name, null);
        final EditText edittext = view.findViewById(R.id.edt_groupName);
        final TextView title = view.findViewById(R.id.title);
        final Button cancel = view.findViewById(R.id.cancel);
        final Button save = view.findViewById(R.id.save);
        save.setText(getCommonPage(ProfileActivity.this).getLblDone());
        cancel.setText(getCommonPage(ProfileActivity.this).getLblCancel());
        alert.setView(view);
        alert11 = alert.create();

        edittext.setText(str);
        if (name.equalsIgnoreCase("about"))
            title.setText(getSettingsData(ProfileActivity.this).getLblEnterStatus());
        else
            title.setText(getSettingsData(ProfileActivity.this).getLblEnterName());


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.equalsIgnoreCase("about")) {
                    KeyboardUtil.getInstance(ProfileActivity.this).closeKeyboard();
                    updateStatus(edittext.getText().toString().trim());
                } else if (name.equalsIgnoreCase("name")) {
                    KeyboardUtil.getInstance(ProfileActivity.this).closeKeyboard();
                    updateName(edittext.getText().toString().trim());
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //New...
                KeyboardUtil.getInstance(ProfileActivity.this).closeKeyboard();
                alert11.dismiss();
            }
        });

        alert11.show();
        Window window = alert11.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    private void updateStatus(String updatedStatus) {
        if (TextUtils.isEmpty(updatedStatus)) {
            Toast.makeText(ProfileActivity.this, getSettingsData(ProfileActivity.this).getErrStatus(),
                    Toast.LENGTH_SHORT).show();
        } else {
            userMe.setStatus(updatedStatus);
            BaseApplication.getUserRef().child(userMe.getId()).setValue(userMe).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    if (helper != null)
                        helper.setLoggedInUser(userMe);
                    profileStatus.setText(userMe.getStatus());
                    Toast.makeText(ProfileActivity.this, getSettingsData(ProfileActivity.this).getLblUpdated(),
                            Toast.LENGTH_SHORT).show();
                    if (alert11.isShowing()) {
                        alert11.dismiss();
                    }
                }
            });
        }
    }

    private void updateName(String updatedName) {
        if (TextUtils.isEmpty(updatedName)) {
            Toast.makeText(ProfileActivity.this, getSettingsData(ProfileActivity.this).getErrName(),
                    Toast.LENGTH_SHORT).show();
        } else {
            userMe.setProfileName(updatedName);
            BaseApplication.getUserRef().child(userMe.getId()).setValue(userMe).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    if (helper != null)
                        helper.setLoggedInUser(userMe);
                    profileName.setText(userMe.getProfileName());
                    Toast.makeText(ProfileActivity.this, getSettingsData(ProfileActivity.this).getLblUpdated(),
                            Toast.LENGTH_SHORT).show();
                    if (alert11.isShowing()) {
                        alert11.dismiss();
                    }
                }
            });
        }
    }

    private void loadImages() {
        if (userMe.getImage() != null && !userMe.getImage().isEmpty())
            Picasso.get()
                    .load(userMe.getImage())
                    .tag(this)
                    .error(R.drawable.ic_avatar)
                    .placeholder(R.drawable.ic_avatar)
                    .into(profileImg);
        else
            Picasso.get()
                    .load(R.drawable.ic_avatar)
                    .tag(this)
                    .error(R.drawable.ic_avatar)
                    .placeholder(R.drawable.ic_avatar)
                    .into(profileImg);

    }
}
