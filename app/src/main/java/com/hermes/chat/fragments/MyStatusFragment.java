package com.hermes.chat.fragments;

import static android.app.Activity.RESULT_OK;
import static com.hermes.chat.utils.Helper.getStatusData;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.UploadTask;
import com.hermes.chat.BaseApplication;
import com.hermes.chat.R;
import com.hermes.chat.activities.ChatActivity;
import com.hermes.chat.activities.MainActivity;
import com.hermes.chat.activities.MyStatusActivity;
import com.hermes.chat.activities.StatusStoriesActivity;
import com.hermes.chat.activities.UserNameSignInActivity;
import com.hermes.chat.adapters.StatusAdapter;
import com.hermes.chat.interfaces.HomeIneractor;
import com.hermes.chat.models.Attachment;
import com.hermes.chat.models.AttachmentArrayList;
import com.hermes.chat.models.AttachmentList;
import com.hermes.chat.models.AttachmentTypes;
import com.hermes.chat.models.Contact;
import com.hermes.chat.models.MessageNew;
import com.hermes.chat.models.MessageNewArrayList;
import com.hermes.chat.models.StatusImageList;
import com.hermes.chat.models.StatusImageNew;
import com.hermes.chat.models.StatusNew;
import com.hermes.chat.models.User;
import com.hermes.chat.utils.CircularStatusView;
import com.hermes.chat.utils.FileUtils;
import com.hermes.chat.utils.FirebaseUploader;
import com.hermes.chat.utils.Helper;
import com.hermes.chat.viewHolders.BaseMessageViewHolder;
import com.hermes.chat.views.MyRecyclerView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

public class MyStatusFragment extends Fragment implements ImagePickerCallback {

    private static final String TAG = "MyStatusFragment";
    boolean isCacheEnabled = true;
    boolean isImmersiveEnabled = true;
    boolean isTextEnabled = false;
    long storyDuration = 5000L;

    private MyRecyclerView recyclerView;
    private Button clear;
    private StatusAdapter statusAdapter;
    private SwipeRefreshLayout mySwipeRefreshLayout;

    private Realm rChatDb;
    private User userMe;
    private ArrayList<User> myUsers = new ArrayList<>();

    private RealmResults<StatusNew> resultList;
    private ArrayList<StatusNew> chatDataList = new ArrayList<>();
    private ArrayList<StatusNew> filterDataList = new ArrayList<>();
    private ArrayList<StatusNew> finalDataList = new ArrayList<>();

    private HomeIneractor homeInteractor;
    private Helper helper;

    private ImagePicker imagePicker;
    private CameraImagePicker cameraPicker;
    private String pickerPath;
    protected String[] permissionsStorage = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    protected String[] permissionsCamera = {Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private String chatChild;
    private Context context;

    private RealmList<StatusImageNew> statusImagesList;
    private ArrayList<StatusImageNew> statusImageArrayList = new ArrayList<>();
    private ArrayList<MessageNew> messageArrayList = new ArrayList<>();
    private int count = 0;
    private int i = 0, uploadInt = 0, uploadCount = 0;
    private ProgressDialog dialog;
    private CircleImageView img;
    private ArrayList<Contact> myContacts;
    private List<ChosenImage> list = new ArrayList<>();
    private RealmList<StatusImageList> urlList = new RealmList<>();
    private TextView statusText;
    private TextView statusBadge;
    private boolean flag = false;
    private CircularStatusView statusCircular;
    int cnt = 0;
    private ImageView ivMore;
    public static StatusNew statusNew;
    private TextView info;
    private TextView titleNewStatus;

    File image;
    AlertDialog.Builder builder;

    private RealmChangeListener<RealmResults<StatusNew>> chatListChangeListener =
            new RealmChangeListener<RealmResults<StatusNew>>() {
                @Override
                public void onChange(RealmResults<StatusNew> element) {
                    if (element != null && element.isValid() && element.size() > 0) {
                        chatDataList.clear();
                        chatDataList.addAll(rChatDb.copyFromRealm(element));
                        Log.d(TAG, "onChange: 1"+chatDataList.size());
                        statusNew = null;
                        fillAdapter();
                        setUserNamesAsInPhone();
                    } else if (element.size() ==0 ){
                        //New...
                        //No Status From your Users, Make Empty List
                        chatDataList.clear();
                        i = 0;
                        statusNew = null;
                        fillAdapter();
                    }
                }
            };

    private String[] resources;
    private String[] myResources;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        fetchContacts();
        try {
            homeInteractor = (HomeIneractor) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement HomeIneractor");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helper = new Helper(getContext());
        userMe = homeInteractor.getUserMe();

        try
        {
            if(userMe.getAdminblock()){
                checkBlocked();
            }
        }catch (Exception E){

        }

        Realm.init(getContext());
        builder = new AlertDialog.Builder(requireContext());
        rChatDb = Helper.getRealmInstance();
        dialog = new ProgressDialog(context);
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_status, container, false);

        view.findViewById(R.id.fab_chat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                openDialog();
                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU) {
                    chooseDialog();
                }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    chooseDialog();
                   /* if (FileUtils.checkPermission(requireActivity())) {
                        chooseDialog();
                    }else {
                        requestPermission();
                    }*/
                }
                        /*if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.R) {
                            chooseDialog();
                        }*/else {
                    openImageClick();
                }
            }
        });
        view.findViewById(R.id.chat_badge).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                openDialog();
                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU) {
                    chooseDialog();
                }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

                    chooseDialog();
                    /*if (FileUtils.checkPermission(requireActivity())) {
                        chooseDialog();
                    }else {
                        requestPermission();
                    }*/
                }
                        /*if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.R) {
                            chooseDialog();
                        }*/else {
                    openImageClick();
                }
            }
        });


        view.findViewById(R.id.user_details_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myResources != null && myResources.length > 0) {
                    Intent a = new Intent(getContext(), StatusStoriesActivity.class);
                    a.putExtra(StatusStoriesActivity.STATUS_RESOURCES_KEY, myResources);
                    a.putExtra(StatusStoriesActivity.STATUS_DURATION_KEY, storyDuration);
                    a.putExtra(StatusStoriesActivity.IS_IMMERSIVE_KEY, isImmersiveEnabled);
                    a.putExtra(StatusStoriesActivity.IS_CACHING_ENABLED_KEY, isCacheEnabled);
                    a.putExtra(StatusStoriesActivity.IS_TEXT_PROGRESS_ENABLED_KEY, isTextEnabled);
                    a.putExtra(StatusStoriesActivity.USER_NAME, "My Status");
                    a.putExtra(StatusStoriesActivity.URL, statusNew.getUser().getImage());
                    StatusStoriesActivity.FROM = true;
                    a.putExtra(StatusStoriesActivity.RECIPIENT_ID, statusNew.getStatusImages().get(0).getSenderId());
                    startActivity(a);
                }
            }
        });
        recyclerView = view.findViewById(R.id.recycler_view);
        img = view.findViewById(R.id.fab_chat);
        clear = view.findViewById(R.id.clear);
        info = view.findViewById(R.id.message);
        titleNewStatus = view.findViewById(R.id.titleNewStatus);
        statusText = view.findViewById(R.id.user_name);
        statusBadge = view.findViewById(R.id.statusBadge);
        mySwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_lay);
        ivMore = view.findViewById(R.id.ivMore);
        statusCircular = view.findViewById(R.id.statusCircular);
        statusCircular.setPortionsColor(context.getResources().getColor(R.color.colorPrimary));
        mySwipeRefreshLayout.setRefreshing(false);
        recyclerView.setEmptyView(view.findViewById(R.id.emptyView));
        recyclerView.setEmptyImageView(((ImageView) view.findViewById(R.id.emptyImage)));
        recyclerView.setEmptyTextView(((TextView) view.findViewById(R.id.emptyText)));
        ((TextView) view.findViewById(R.id.emptyText)).setText(getStatusData(getContext())
                .getLblStatusEmpty());

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        statusText.setText(getStatusData(getContext()).getLblMyStatus());
        info.setText(getStatusData(getContext()).getLblTabToAddStatus());
        titleNewStatus.setText(getStatusData(getContext()).getLblNewStatus());

        mySwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {

                    RealmQuery<StatusNew> query = rChatDb.where(StatusNew.class)/*.equalTo("myId", userMe.getId())*/;//Query from chats whose owner is logged in user
                    resultList = query.isNotNull("userId").sort("timeUpdated", Sort.DESCENDING).findAll();//ignore forward list of messages and get rest sorted according to time

                    chatDataList.clear();
                    chatDataList.addAll(rChatDb.copyFromRealm(resultList));
                    Log.d(TAG, "onChange: 2"+chatDataList.size());
                    i = 0;
                    statusNew = null;
                    fillAdapter();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mySwipeRefreshLayout.setRefreshing(false);

                setUserNamesAsInPhone();

            }
        });

        ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MyStatusActivity.class));
            }
        });

        return view;
    }

    private void requestPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            //Android is 11(R) or above
            try {

                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", requireContext().getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent,201);
            }
            catch (Exception e){
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent,201);
            }
        }

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
                        }/*else if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.R){*/
                            /*Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(intent, 1537);*/
                        else {
                            androidRGallery();
                        }

                    }
                })
                .setNegativeButton("Camera", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                        File path = new File(requireContext().getFilesDir(), "/FlashChat/Camera");
                        if (!path.exists()) path.mkdirs();
                        image = new File(path, FileUtils.getFileName());
                        Uri imageUri = FileProvider.getUriForFile(requireContext().getApplicationContext(), getString(R.string.authority), image);
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
                        if(intent.resolveActivity(requireContext().getPackageManager()) != null) {
                            startActivityForResult(intent,
                                    1515);
                        }
//                        startActivityForResult(intent, 1515);
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

    private void openDialog() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setActivityTitle("Profile picture")
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .setCropMenuCropButtonTitle("Done")
                .setShowCropOverlay(true)
                .setAllowFlipping(false)
                .start(getActivity());
    }

  /*  @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            try {
                RealmQuery<StatusNew> query = rChatDb.where(StatusNew.class);
                resultList = query.isNotNull("userId").sort("timeUpdated", Sort.DESCENDING).findAll();
                chatDataList.clear();
                chatDataList.addAll(rChatDb.copyFromRealm(resultList));
                fillAdapter();
            } catch (Exception e) {
                e.printStackTrace();
            }
            setUserNamesAsInPhone();
        }
    }*/

    @Override
    public void onResume()
    {
        super.onResume();
        userMe = helper.getLoggedInUser();
        if(userMe.getAdminblock()){
           checkBlocked();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            RealmQuery<StatusNew> query = rChatDb.where(StatusNew.class)/*.equalTo("myId", userMe.getId())*/;/*.equalTo("myId", userMe.getId())*/;//Query from chats whose owner is logged in user
            resultList = query.isNotNull("userId").sort("timeUpdated", Sort.DESCENDING).findAll();//ignore forward list of messages and get rest sorted according to time
            chatDataList.clear();
            chatDataList.addAll(rChatDb.copyFromRealm(resultList));
            Log.d(TAG, "onChange: 3"+chatDataList.size());
            resultList.addChangeListener(chatListChangeListener);
            statusNew = null;
            fillAdapter();

        } catch (Exception e) {
            e.printStackTrace();
        }
        setUserNamesAsInPhone();
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rChatDb.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.delete(StatusNew.class);
                    }
                });
            }
        });

        if (userMe != null && userMe.getImage() != null && !userMe.getImage().isEmpty()) {
            Picasso.get()
                    .load(userMe.getImage())
                    .tag(this)
                    .error(R.drawable.ic_avatar)
                    .placeholder(R.drawable.ic_avatar)
                    .into(img);
        } else {
            Picasso.get()
                    .load(R.drawable.ic_avatar)
                    .tag(this)
                    .error(R.drawable.ic_avatar)
                    .placeholder(R.drawable.ic_avatar)
                    .into(img);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    private void fillAdapter() {
        try
        {
            chatChild = userMe.getId();
        }catch (Exception e){

        }

        filterDataList.clear();
        fetchContacts();
        for (StatusNew status : chatDataList) {
            /*for (Contact contact : myContacts) {
                if (Helper.contactMatches(status.getUserId(), contact.getPhoneNumber())) {
                    filterDataList.add(status);
                }*/
            for (User contact : myUsers) {
                if (Helper.contactMatches(status.getUserId(), contact.getId())) {
                    filterDataList.add(status);
                }
               /* if (status.getUserId().equals(Contact.getPhoneNumber())) {
                    filterDataList.add(status);
                }*/
            }
        }

        for (StatusNew status : chatDataList) {
            if (!filterDataList.contains(status) && status.getUserId().equals(userMe.getId())) {
                filterDataList.add(status);
            }
        }
        finalDataList.clear();

        for (int i = 0; i < filterDataList.size(); i++) {
            for (int j = 0; j < filterDataList.get(i).getStatusImages().size(); j++) {
                AttachmentList attachmentList = filterDataList.get(i).getStatusImages().get(j).getAttachment();
                RealmList<StatusImageList> imageListsModel = new RealmList<>();
                for (StatusImageList statusImageList : attachmentList.getUrlList()) {
                    long time = diff(statusImageList.getUploadTime());
                    if (time <= 23) {
                        imageListsModel.add(statusImageList);
                    }
                }
                if (imageListsModel.size() > 0) {
                    flag = false;
                    attachmentList.setUrlList(imageListsModel);
                    filterDataList.get(i).getStatusImages().get(j).setAttachment(attachmentList);
                } else {
                    flag = true;
                }
            }
            if (!flag) {
                filterDataList.get(i).setStatusImages(filterDataList.get(i).getStatusImages());
                StatusNew statusNew = filterDataList.get(i);
                userMe = helper.getLoggedInUser();
                try {
                    if (statusNew.getUser().getBlockedUsersIds() != null &&
                            !statusNew.getUser().getBlockedUsersIds().contains(userMe.getId()) &&
                            userMe.getBlockedUsersIds() != null && statusNew.getUser() != null &&
                            !userMe.getBlockedUsersIds().contains(statusNew.getUser().getId()))
                        finalDataList.add(statusNew);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        for (int i = 0; i < finalDataList.size(); i++) {
            if (finalDataList.get(i).getUser().getNameToDisplay().equalsIgnoreCase(userMe.getNameToDisplay())) {
                statusNew = finalDataList.get(i);
                finalDataList.remove(i);
                i = i - 1;
            }
        }

        if (statusNew != null) {
            if (statusNew.getUser() != null) {
                for (int i = 0; i < statusNew.getStatusImages().size(); i++) {
                    int count = 0;
                    for (int j = 0; j < statusNew.getStatusImages().get(i).getAttachment().getUrlList().size(); j++) {
                        if (statusNew.getStatusImages().get(i).getAttachment().getUrlList().get(j).isExpiry())
                            count = count + 1;
                    }
                    myResources = new String[count];
                    int arrLength = 0;
                    for (int j = 0; j < statusNew.getStatusImages().get(i).getAttachment().getUrlList().size(); j++) {
                        if (statusNew.getStatusImages().get(i).getAttachment().getUrlList().get(j).isExpiry()) {
                            myResources[arrLength] = statusNew.getStatusImages().get(i).getAttachment().getUrlList().get(j).getUrl();
                            arrLength++;
                        }
                    }
                }
                if (myResources != null && myResources.length > 0) {
                    int count = myResources.length;
                    statusCircular.setPortionsCount(count);
                    Picasso.get()
                            .load(myResources[myResources.length - 1])
                            .tag(this)
                            .error(R.drawable.ic_avatar)
                            .placeholder(R.drawable.ic_avatar)
                            .into(img);
                    statusBadge.setVisibility(View.VISIBLE);
                    ivMore.setVisibility(View.VISIBLE);
                } else {
                    statusBadge.setVisibility(View.GONE);
                    ivMore.setVisibility(View.GONE);
                }
            }
        } else {
            statusBadge.setVisibility(View.GONE);
            ivMore.setVisibility(View.GONE);
            myResources = null;
            Picasso.get()
                    .load(R.drawable.ic_avatar)
                    .tag(this)
                    .error(R.drawable.ic_avatar)
                    .placeholder(R.drawable.ic_avatar)
                    .into(img);
        }

        statusAdapter = new StatusAdapter(this.context, finalDataList, MyStatusFragment.this);
        recyclerView.setAdapter(statusAdapter);
        resultList.addChangeListener(chatListChangeListener);
    }

    private long diff(long time) {
        long now = System.currentTimeMillis();
        long diff = now - time;
        return diff / (60 * 60 * 1000);
    }

    public void openImagePick() {
        if (permissionsAvailable(permissionsStorage)) {
            imagePicker = new ImagePicker(this);
            imagePicker.shouldGenerateMetadata(true);

            imagePicker.shouldGenerateThumbnails(true);
            imagePicker.allowMultiple();
            imagePicker.setImagePickerCallback(this);
            imagePicker.pickImage();
        } else {
            ActivityCompat.requestPermissions(getActivity(), permissionsStorage, 36);
        }
    }

    void openImageClick() {
        if (permissionsAvailable(permissionsCamera)) {
            cameraPicker = new CameraImagePicker(this);
            cameraPicker.shouldGenerateMetadata(true);
            cameraPicker.shouldGenerateThumbnails(true);
            cameraPicker.setImagePickerCallback(this);
            pickerPath = cameraPicker.pickImage();
        } else {
            ActivityCompat.requestPermissions(getActivity(), permissionsCamera, 47);
        }
    }

    public void navigateStatusStories(int position) {
        statusImageArrayList.clear();
        statusImagesList = finalDataList.get(position).getStatusImages();
        statusImageArrayList.addAll(statusImagesList);

        for (int i = 0; i < statusImageArrayList.size(); i++) {

            int count = 0;
            for (int j = 0; j < statusImageArrayList.get(i).getAttachment().getUrlList().size(); j++) {
                if (statusImageArrayList.get(i).getAttachment().getUrlList().get(j).isExpiry())
                    count = count + 1;
            }
            resources = new String[count];
            int arrLength = 0;
            for (int j = 0; j < statusImageArrayList.get(i).getAttachment().getUrlList().size(); j++) {
                if (statusImageArrayList.get(i).getAttachment().getUrlList().get(j).isExpiry()) {
                    resources[arrLength] = statusImageArrayList.get(i).getAttachment().getUrlList().get(j).getUrl();
                    arrLength++;
                }
            }
        }

        StatusStoriesActivity.FROM = false;
        Intent a = new Intent(getContext(), StatusStoriesActivity.class);
        a.putExtra(StatusStoriesActivity.STATUS_RESOURCES_KEY, resources);
        a.putExtra(StatusStoriesActivity.STATUS_DURATION_KEY, storyDuration);
        a.putExtra(StatusStoriesActivity.IS_IMMERSIVE_KEY, isImmersiveEnabled);
        a.putExtra(StatusStoriesActivity.IS_CACHING_ENABLED_KEY, isCacheEnabled);
        a.putExtra(StatusStoriesActivity.IS_TEXT_PROGRESS_ENABLED_KEY, isTextEnabled);
        Log.d(TAG, "navigateStatusStories: "+finalDataList.get(position).getUser().getName());
        a.putExtra(StatusStoriesActivity.USER_NAME, finalDataList.get(position).getUser().getName());
        a.putExtra(StatusStoriesActivity.URL, finalDataList.get(position).getUser().getImage());
        a.putExtra(StatusStoriesActivity.RECIPIENT_ID, finalDataList.get(position).getUserId());
        startActivity(a);
    }


    public void setUserNamesAsInPhone() {
        if (homeInteractor != null && chatDataList != null) {
            for (StatusNew chat : chatDataList) {
                User user = chat.getUser();
                if (user != null) {
                    if (helper.getCacheMyUsers() != null && helper.getCacheMyUsers().containsKey(user.getId())) {
                        user.setNameInPhone(helper.getCacheMyUsers().get(user.getId()).getNameToDisplay());
                    } else {
                        for (User savedContact : myUsers) {
                            /*if (Helper.contactMatches(user.getId(), savedContact.getPhoneNumber())) {
                                if (user.getNameInPhone() == null || !user.getNameInPhone().equals(savedContact.getName())) {*/
                                    user.setNameInPhone(savedContact.getName());
                               /* }
                                break;*/
                           /* }*/
                        }
                    }
                }
            }
        }
        if (statusAdapter != null)
            statusAdapter.notifyDataSetChanged();
    }

    private void fetchContacts() {
        BaseApplication.getUserRef()
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String print = "";
                                User user;
                                ArrayList<User> users= new ArrayList<>();
                                ArrayList<String> connectList = new ArrayList<>();
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    try {
                                        user = snapshot.getValue(User.class);
                                        if (user.getId() != null){

                                            users.add(user);
                                            if(user.getId().equals(userMe.getId())){
                                                connectList.addAll(user.getConnect_list());
                                            }

                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                myUsers.clear();
                                for(int i=0;i<users.size();i++){
                                    for(int j=0;j<connectList.size();j++){
                                        try
                                        {
                                            if(connectList.get(j).equals(users.get(i).getId())){
                                                myUsers.add(users.get(i));
                                                Log.d(TAG, "onDataChange: "+users.get(i).getId());
                                                setUserNamesAsInPhone();
                                            }
                                        }catch (Exception e){

                                        }

                                    }
//                                    break;
                                }


//                                setupMenu();


//                                Log.d(TAG, "onDataChangevalue: "+print);


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

    }

    /*private void fetchMyContacts() {
        myContacts = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        if (cursor != null && !cursor.isClosed()) {
            cursor.getCount();
            while (cursor.moveToNext()) {
                int hasPhoneNumber = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                if (hasPhoneNumber == 1) {
                    String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll("\\s+", "");
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY));
                    if (Patterns.PHONE.matcher(number).matches()) {
                        boolean hasPlus = String.valueOf(number.charAt(0)).equals("+");
                        number = number.replaceAll("[\\D]", "");
                        if (hasPlus) {
                            number = "+" + number;
                        }
                        Contact contact = new Contact(number, name);
                        if (!myContacts.contains(contact))
                            myContacts.add(contact);
                    }
                }
            }
            cursor.close();
        }
    }*/

    protected boolean permissionsAvailable(String[] permissions) {
        boolean granted = true;
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(getContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                granted = false;
                break;
            }
        }
        return granted;
    }


    @Override
    public void onImagesChosen(List<ChosenImage> list) {
        dialog.setCancelable(false);
        dialog.setMessage("Loading...");
        dialog.show();
        this.list.clear();
        urlList.clear();
        this.list = list;
        uploadInt = 0;
        callUpload();
    }


    private void callUpload() {
        if (list != null && !list.isEmpty()) {
            for (uploadInt = uploadInt; uploadInt < list.size(); ) {
                Uri originalFileUri = Uri.parse(list.get(uploadInt).getOriginalPath());
                File tempFile = new File(getActivity().getCacheDir(), originalFileUri.getLastPathSegment());
                try {
                    //  uploadImage(SiliCompressor.with(getContext()).compress(originalFileUri.toString(), tempFile));
                    //   uploadImage(compressImage(originalFileUri.getPath()));
                    uploadImage(originalFileUri.getPath());
                } catch (Exception ex) {
                    uploadImage(originalFileUri.getPath());
                }
                break;
            }
        }
    }

    private void uploadImage(String filePath) {
        newFileUploadTask(filePath, AttachmentTypes.IMAGE, null);
    }

    private void newFileUploadTask(String filePath,
                                   @AttachmentTypes.AttachmentType final int attachmentType, final AttachmentList attachment) {
        final File fileToUpload = new File(filePath);
        final String fileName = Uri.fromFile(fileToUpload).getLastPathSegment();

        AttachmentList preSendAttachment = attachment;//Create/Update attachment
        if (preSendAttachment == null) preSendAttachment = new AttachmentList();
        preSendAttachment.setName(fileName);
        preSendAttachment.setBytesCount(fileToUpload.length());
        preSendAttachment.setUrlList(new RealmList<StatusImageList>());
        checkAndCopy("/" + getString(R.string.app_name) + "/" + AttachmentTypes.getTypeName(attachmentType) + "/.sent/", fileToUpload);//Make a copy
        chatChild = userMe.getId();
        uploadAndSend(new File(filePath), attachment, attachmentType, chatChild);
    }

    private void uploadAndSend(final File fileToUpload, final AttachmentList attachment, final int attachmentType, final String chatChild) {
        if (!fileToUpload.exists())
            return;
        final String fileName = Uri.fromFile(fileToUpload).getLastPathSegment();
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child(getString(R.string.app_name)).child(AttachmentTypes.getTypeName(attachmentType)).child(fileName);
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                //If file is already uploaded
                if (dialog.isShowing())
                    dialog.dismiss();
                AttachmentList attachment1 = attachment;
                if (attachment1 == null) attachment1 = new AttachmentList();
                attachment1.setName(fileName);
                StatusImageList imageList = new StatusImageList();
                imageList.setUrl(uri.toString());
                imageList.setExpiry(true);
                imageList.setUploadTime(System.currentTimeMillis());
                urlList.add(imageList);
                attachment1.setUrlList(urlList);
                attachment1.setBytesCount(fileToUpload.length());
                uploadInt++;
                prepareMessage(null, attachmentType, attachment1);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                //Elase upload and then send message
                FirebaseUploader firebaseUploader = new FirebaseUploader(new FirebaseUploader.UploadListener() {
                    @Override
                    public void onUploadFail(String message) {
                        if (dialog.isShowing())
                            dialog.dismiss();
                        Log.e("DatabaseException", message);
                    }

                    @Override
                    public void onUploadSuccess(String downloadUrl) {
                        if (dialog.isShowing())
                            dialog.dismiss();
                        AttachmentList attachment1 = attachment;
                        if (attachment1 == null) attachment1 = new AttachmentList();
                        attachment1.setName(fileToUpload.getName());
                        StatusImageList imageList = new StatusImageList();
                        imageList.setUrl(downloadUrl);
                        imageList.setExpiry(true);
                        imageList.setUploadTime(System.currentTimeMillis());
                        urlList.add(imageList);
                        attachment1.setUrlList(urlList);
                        attachment1.setBytesCount(fileToUpload.length());
                        uploadInt++;
                        prepareMessage(null, attachmentType, attachment1);
                    }

                    @Override
                    public void onUploadProgress(int progress) {

                    }

                    @Override
                    public void onUploadCancelled() {

                    }
                }, storageReference);
                firebaseUploader.uploadOthers(context, fileToUpload);
            }
        });
    }

    private void sendMessage(String messageBody, @AttachmentTypes.AttachmentType int attachmentType,
                             AttachmentList attachment) {

        MessageNew message = new MessageNew();
        message.setAttachmentType(attachmentType);
        if (attachmentType != AttachmentTypes.NONE_TEXT)
            message.setAttachment(attachment);
        else
            BaseMessageViewHolder.animate = true;
        message.setBody(messageBody);
        message.setDate(System.currentTimeMillis());
        message.setSenderId(userMe.getId());
        message.setSenderName(userMe.getName());
        message.setSent(true);
        message.setDelivered(false);
        message.setId(((MainActivity) context).getDatabaseRef().child(chatChild).push().getKey());

        ((MainActivity) context).getDatabaseRef().child(chatChild).child(message.getId()).setValue(message);
        if (dialog.isShowing())
            dialog.dismiss();
    }

    @Override
    public void onError(String s) {
        Toast.makeText(getContext(), "" + s, Toast.LENGTH_SHORT).show();
    }


    private void prepareMessage(final String body, final int attachmentType, final AttachmentList attachment) {

        rChatDb.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                final StatusNew statusQuery = rChatDb.where(StatusNew.class).equalTo("userId", userMe.getId()).findFirst();
                if (statusQuery == null) {
                    StatusImageNew statusImage = rChatDb.createObject(StatusImageNew.class);
                    statusImage.setAttachmentType(attachmentType);
                    AttachmentList attachment1 = rChatDb.createObject(AttachmentList.class);
                    attachment1.setBytesCount(attachment.getBytesCount());
                    attachment1.setData(attachment.getData());
                    attachment1.setName(attachment.getName());

                    RealmList<StatusImageList> realmList = new RealmList<>();
                    for (int i = 0; i < attachment.getUrlList().size(); i++) {
                        StatusImageList statusImageList = rChatDb.createObject(StatusImageList.class);
                        statusImageList.setUrl(attachment.getUrlList().get(i).getUrl());
                        statusImageList.setExpiry(attachment.getUrlList().get(i).isExpiry());
                        statusImageList.setUploadTime(attachment.getUrlList().get(i).getUploadTime());
                        realmList.add(statusImageList);
                    }


                    attachment1.setUrlList(realmList);
                    statusImage.setAttachment(attachment1);
                    statusImage.setBody(body);
                    statusImage.setDate(System.currentTimeMillis());
                    statusImage.setSenderId(userMe.getId());
                    statusImage.setSenderName(userMe.getName());
                    statusImage.setSent(false);
                    statusImage.setDelivered(false);
                    statusImage.setId(attachment.getBytesCount() + attachment.getName());

                    StatusNew status = rChatDb.createObject(StatusNew.class);
                    status.getStatusImages().add(statusImage);
                    status.setLastMessage(body);
                    status.setMyId(((MainActivity) context).getDatabaseRef().child(chatChild).push().getKey());
                    status.setTimeUpdated(System.currentTimeMillis());
                    status.setUser(rChatDb.copyToRealm(userMe));
                    status.setUserId(userMe.getId());
                    status.setTimeUpdated(System.currentTimeMillis());
                    status.setLastMessage(body);
                    sendMessage(null, attachmentType, attachment);
                } else {
                    AttachmentList attachment1 = statusQuery.getStatusImages().get(0).getAttachment();
                    RealmList<StatusImageList> realmList = new RealmList<>();
                    for (int i = 0; i < attachment1.getUrlList().size(); i++) {
                        StatusImageList statusImageList = rChatDb.createObject(StatusImageList.class);
                        statusImageList.setUrl(attachment1.getUrlList().get(i).getUrl());
                        statusImageList.setExpiry(attachment1.getUrlList().get(i).isExpiry());
                        statusImageList.setUploadTime(attachment1.getUrlList().get(i).getUploadTime());
                        realmList.add(statusImageList);
                    }

                    for (int i = 0; i < attachment.getUrlList().size(); i++) {
                        StatusImageList statusImageList = rChatDb.createObject(StatusImageList.class);
                        statusImageList.setUrl(attachment.getUrlList().get(i).getUrl());
                        statusImageList.setExpiry(attachment.getUrlList().get(i).isExpiry());
                        statusImageList.setUploadTime(attachment.getUrlList().get(i).getUploadTime());
                        realmList.add(statusImageList);
                    }

                    attachment1.setUrlList(realmList);
                    attachment1.setName(attachment.getName());
                    statusQuery.setTimeUpdated(System.currentTimeMillis());
                    statusQuery.getStatusImages().get(0).setAttachment(attachment1);
                    statusQuery.setStatusImages(statusQuery.getStatusImages());

                    RealmQuery<StatusNew> query = rChatDb.where(StatusNew.class);
                    RealmResults<StatusNew> resultList = query.isNotNull("userId")
                            .sort("timeUpdated", Sort.DESCENDING).findAll();
                    chatDataList.clear();
                    chatDataList.addAll(rChatDb.copyFromRealm(resultList));
                    Log.d(TAG, "onChange: 4"+chatDataList.size());
                    i = 0;
                    statusNew = null;
                    fillAdapter();
                    final DatabaseReference databaseReference = ((MainActivity) context).getDatabaseRef().child(chatChild);
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                String key = dataSnapshot1.getKey();
                                MessageNewArrayList value = dataSnapshot1.getValue(MessageNewArrayList.class);
                                value.setAttachmentType(attachmentType);
                                if (attachmentType != AttachmentTypes.NONE_TEXT) {
                                    AttachmentList attachmentList = statusQuery.getStatusImages().get(0).getAttachment();
                                    AttachmentArrayList attachmentArrayList = new AttachmentArrayList();
                                    attachmentArrayList.setBytesCount(attachment.getBytesCount());
                                    attachmentArrayList.setData(attachment.getData());
                                    attachmentArrayList.setName(attachment.getName());
                                    ArrayList<StatusImageList> arrayList = new ArrayList<>();
                                    if (attachmentList.getUrlList().size() > 0) {
                                        arrayList.addAll(attachmentList.getUrlList());
                                    }
                                    attachmentArrayList.setUrlList(arrayList);
                                    value.setAttachment(attachmentArrayList);
                                } else
                                    BaseMessageViewHolder.animate = true;
                                value.setDate(System.currentTimeMillis());

                                ((MainActivity) context).getDatabaseRef().child(chatChild).child(key).setValue(value);
                                //databaseReference.child(key).setValue(value);
                                if (dialog.isShowing())
                                    dialog.dismiss();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.i("TAG_ERROR", databaseError.getMessage());
                        }
                    });
                }
            }
        });
    }


    private void checkAndCopy(String directory, File source) {
        //Create and copy file content
        File file = new File(Environment.getExternalStorageDirectory(), directory);
        boolean dirExists = file.exists();
        if (!dirExists)
            dirExists = file.mkdirs();
        if (dirExists) {
            try {
                file = new File(Environment.getExternalStorageDirectory() + directory, Uri.fromFile(source).getLastPathSegment());
                boolean fileExists = file.exists();
                if (!fileExists)
                    fileExists = file.createNewFile();
                if (fileExists && file.length() == 0) {
                    FileUtils.copyFile(source, file);
                }
            } catch (IOException e) {
                if (dialog.isShowing())
                    dialog.dismiss();
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == AppCompatActivity.RESULT_OK) {
            switch (requestCode) {
                case Picker.PICK_IMAGE_DEVICE:
                    if (imagePicker == null) {
                        imagePicker = new ImagePicker(this);
                        imagePicker.setImagePickerCallback(this);
                    }
                    imagePicker.submit(data);
                    break;
                case Picker.PICK_IMAGE_CAMERA:
                    if (cameraPicker == null) {
                        cameraPicker = new CameraImagePicker(this);
                        cameraPicker.setImagePickerCallback(this);
                        cameraPicker.reinitialize(pickerPath);
                    }
                    cameraPicker.submit(data);
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
                    dialog.setCancelable(false);
                    dialog.setMessage("Loading...");
                    dialog.show();
                    urlList.clear();
                    uploadImage(result.getUri().getPath());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(getActivity(),
                        "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void onSelectFromGalleryResult(Intent data,String type) {
        try {

            if (data != null || getDeviceName().equalsIgnoreCase("samsung")) {

                Uri uri;
                String path = "";

                if (type.equalsIgnoreCase("Camera")) {
                    //uri = Uri.fromFile(image);
                    path = image.getAbsolutePath();
                }else {
                    path = FileUtils.getRealPathFromURI(requireContext(),data.getData());
                }

                switch (type){
                    case "Image":
                    case "Camera":
                        uploadImage(path);
                        break;


                }
            }else {
                if (type.equalsIgnoreCase("Camera")) {
                    uploadImage(image.getAbsolutePath());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*private void onSelectFromGalleryResult(Intent data, String type)
    {
        try {


            Log.d(TAG, "onSelectFromGalleryResult: "+getDeviceName());

            if (data != null || data.getData() != null || getDeviceName().equalsIgnoreCase("samsung")) {

                Uri uri;
                String path = "";

                if (type.equalsIgnoreCase("Camera")) {
                    //uri = Uri.fromFile(image);
                    path = data.getData().getPath();
//                    path = data.getData().getPath();
                   *//* if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.R){
                        path = data.getData().getPath();
                    } else {
                        path = image.getAbsolutePath();
                    }*//*

                }else {
                    path = FileUtils.getRealPathFromURI(requireContext(),data.getData());
                }

                switch (type){
                    case "Image":
                    case "Camera":
                        uploadImage(path);
                        break;

                }
            } else
            {
                if (type.equalsIgnoreCase("Camera"))
                {
                    uploadImage(image.getAbsolutePath());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/



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

}