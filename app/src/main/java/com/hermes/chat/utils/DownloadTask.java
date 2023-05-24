package com.hermes.chat.utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import androidx.core.content.FileProvider;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.hermes.chat.R;
import com.hermes.chat.models.AttachmentTypes;
import com.hermes.chat.viewHolders.MessageAttachmentRecordingViewHolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadTask {

    private static final String TAG = "Download Task";
    private final Context context;

    private String downloadUrl;
    private String downloadFileName = "";
    private int attachmentType;
    private ProgressDialog progressDialog;
    private MessageAttachmentRecordingViewHolder.RecordingViewInteractor recordingViewInteractor;
    private int adapterPosition;
    File outputFile = null;

    public DownloadTask(Context context, String downloadUrl, String name, int attachmentType) {
        this.context = context;
        this.downloadUrl = downloadUrl;
        this.attachmentType = attachmentType;
        //downloadFileName = downloadUrl.getUrl().substring(downloadUrl.getUrl().lastIndexOf('/'));
        downloadFileName = name;
        Log.e(TAG, downloadFileName);
        //Start Downloading Task
        new DownloadingTask().execute();
    }

    public DownloadTask(Context context, String url, String name, int attachmentType,
                        MessageAttachmentRecordingViewHolder.RecordingViewInteractor
                                recordingViewInteractor, int adapterPosition) {
        this.context = context;
        this.downloadUrl = url;
        this.attachmentType = attachmentType;
        //downloadFileName = downloadUrl.getUrl().substring(downloadUrl.getUrl().lastIndexOf('/'));
        downloadFileName = name;
        this.recordingViewInteractor = recordingViewInteractor;
        this.adapterPosition = adapterPosition;
        Log.e(TAG, downloadFileName);
        //Start Downloading Task
        new DownloadingTask().execute();
    }

    public boolean isSDCardPresent() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    private class DownloadingTask extends AsyncTask<Void, Void, Void> {

        File apkStorage = null;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Downloading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void result) {
            try {
                progressDialog.dismiss();
                if (outputFile != null) {
                    String filePath = outputFile.getAbsolutePath();
                    new AlertDialog.Builder(context)
                            .setTitle("Download successful")
                            .setMessage("File path : " + filePath)
                            .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                            .setPositiveButton("Open", (dialog, which) -> {
                                try {
                                    dialog.dismiss();

                                    File file = new File(filePath);
                                    MimeTypeMap map = MimeTypeMap.getSingleton();
                                    /*String ext = MimeTypeMap.getFileExtensionFromUrl(file.getName());
                                    String type = map.getMimeTypeFromExtension(ext);*/
                                    String example = file.toString();
                                    String type = map.getMimeTypeFromExtension(example.substring(example.lastIndexOf(".") + 1));
                                    if (type == null)
                                        type = "*/*";

                                  /*  if (type.contains("wav")) {

                                        convertAudio(filePath, adapterPosition, outputFile);
                                        *//*recordingViewInteractor.playRecording(new
                                                        File(file.getPath()),
                                                downloadFileName
                                                        .replace(".wav", ".mp3"),
                                                adapterPosition);*//*
                                    } else {*/

                                    //New...
                                    //Mee...
                                    if(type.contains(".pdf") || type.contains("pdf")) {
                                        Log.d("downloadUrl",downloadUrl);
                                        Intent intent=new Intent(Intent.ACTION_VIEW);
                                        intent.setDataAndType(Uri.parse(downloadUrl),type);
                                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                        // start activity
                                        context.startActivity(intent);
                                    }else {
                                        Intent intent = new Intent(Intent.ACTION_VIEW);

                                        // android.os.FileUriExposedException
                                        /*Uri data = Uri.fromFile(file);*/

                                        Uri data = FileProvider.getUriForFile(context,
                                                //BuildConfig.APPLICATION_ID + ".provider", file);
                                                context.getString(R.string.authority), file);

                                        intent.setDataAndType(data, type);
                                        //intent.setDataAndType(data, Helper.getMimeType(context, data));
                                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                        context.startActivity(intent);
                                    }

                                 //   }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            })
                            .create().show();
                } else {
                    Toast.makeText(context, "Download Failed", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "Download Failed", Toast.LENGTH_SHORT).show();
            }

            super.onPostExecute(result);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                //Get File if SD card is present
                if (isSDCardPresent()) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

                        apkStorage = context.getFilesDir();
                        String fileName = "/" + context.getString(R.string.app_name) + "/" + AttachmentTypes.getTypeName(
                                attachmentType);

                        apkStorage = new File(apkStorage,fileName);

                    }else {

                        //  apkStorage = new File(Environment.getExternalStorageDirectory() + "/" + "doccure");
                        apkStorage = new File(Environment.getExternalStorageDirectory() + "/" +
                                context.getString(R.string.app_name) + "/" + AttachmentTypes.getTypeName(
                                attachmentType));

                    }

                } else
                    Toast.makeText(context, "Oops!! There is no SD Card.", Toast.LENGTH_SHORT).show();

                //If File is not present create directory
                if (!apkStorage.exists()) {
//                    apkStorage.mkdir();
                    apkStorage.mkdirs();
                    Log.e(TAG, "Directory Created.");
                }

                outputFile = new File(apkStorage, downloadFileName);//Create Output file in Main File

                //Create New File if not present
                if (!outputFile.exists()) {
                    outputFile.createNewFile();
                    Log.e(TAG, "File Created");

                    URL url = new URL(downloadUrl);//Create Download URl
                    HttpURLConnection c = (HttpURLConnection) url.openConnection();//Open Url Connection
                    c.setRequestMethod("GET");//Set Request Method to "GET" since we are grtting data
                    c.connect();//connect the URL Connection

                    //If Connection response is not OK then show Logs
                    if (c.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        Log.e(TAG, "Server returned HTTP " + c.getResponseCode()
                                + " " + c.getResponseMessage());

                    }

                    FileOutputStream fos = new FileOutputStream(outputFile);//Get OutputStream for NewFile Location

                    InputStream is = c.getInputStream();//Get InputStream for connection

                    byte[] buffer = new byte[1024];//Set buffer type
                    int len1 = 0;//init length
                    while ((len1 = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, len1);//Write new file
                    }

                    //Close all connection after doing task
                    fos.close();
                    is.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                outputFile = null;
                Log.e(TAG, "Download Error Exception " + e.getMessage());
            }
            return null;
        }
    }

    private void convertAudio(String downloadFileName, int adapterPosition, File filePath) {
       /* IConvertCallback callback = new IConvertCallback() {
            @Override
            public void onSuccess(File convertedFile) {
                // Toast.makeText(context, "SUCCESS: " + convertedFile.getPath(), Toast.LENGTH_LONG).show();
                recordingViewInteractor.playRecording(new File(convertedFile.getPath()),
                        downloadFileName
                                .replace(".wav", ".mp3"), adapterPosition);
            }

            @Override
            public void onFailure(Exception error) {
                Toast.makeText(context, "ERROR: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        };
        AndroidAudioConverter.with(context)
                .setFile(filePath)
                .setFormat(AudioFormat.MP3)
                .setCallback(callback)
                .convert();*/

        /*new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FileInputStream fis = new FileInputStream(filePath);
                    int minBufferSize = AudioTrack.getMinBufferSize(16_000,
                            AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
                    AudioTrack at = new AudioTrack(AudioManager.STREAM_MUSIC, 16_000,
                            AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                            minBufferSize, AudioTrack.MODE_STREAM);

                    int i = 0;
                    byte[] music = null;
                    try {
                        music = new byte[512];
                        at.play();

                        while ((i = fis.read(music)) != -1)
                            at.write(music, 0, i);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    at.stop();
                    at.release();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }).start();*/

        int i = 0;
        byte[] music = null;
        int minBufferSize = AudioTrack.getMinBufferSize(16_000,
                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
        AudioTrack at = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
                AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT,
                minBufferSize, AudioTrack.MODE_STREAM);
        try{

            FileInputStream fis = new FileInputStream(filePath);



            music = new byte[512];
            at.play();

            while((i = fis.read(music)) != -1)
                at.write(music, 0, i);

        } catch (IOException e) {
            e.printStackTrace();
        }

        at.stop();
        at.release();
    }


}