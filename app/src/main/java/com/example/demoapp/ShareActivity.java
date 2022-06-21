package com.example.demoapp;

import static com.example.demoapp.RealPathUtil.getDataColumn;
import static com.example.demoapp.RealPathUtil.isDownloadsDocument;
import static com.example.demoapp.RealPathUtil.isExternalStorageDocument;
import static com.example.demoapp.RealPathUtil.isMediaDocument;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;


public class ShareActivity extends AppCompatActivity {
    ImageView profile_photo;
    TextView txt_view;
    VideoView videoView;
    DownloadManager manager;
    DownloadManager.Request request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        profile_photo = findViewById(R.id.profile_photo);
        txt_view = findViewById(R.id.txt_view);
        videoView = findViewById(R.id.videoView);
        onSharedIntent();


    }

    private void onSharedIntent() {
        Intent receiverdIntent = getIntent();
        String receivedAction = receiverdIntent.getAction();
        String receivedType = receiverdIntent.getType();
//video--file,uri....docu
        //video compression
        if (receivedAction.equals(Intent.ACTION_SEND)) {

            // check mime type
            //TODO===HANDLE TEXT MIME TYPE
            if (receivedType.startsWith("text/")) {

                String receivedText = receiverdIntent.getStringExtra(Intent.EXTRA_TEXT);
                if (receivedText != null) {

                    if (URLUtil.isValidUrl(receivedText)) {
                        manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                        request = new DownloadManager.Request(Uri.parse(receivedText));
                        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                        request.setTitle("title");
                        request.setDescription("Downloading file please wait.....");
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "titleS");
                        manager.enqueue(request);


                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(receivedText));
                        startActivity(browserIntent);

                    } else {

                        txt_view.setText(receivedText.trim());
                    }

                }


                //TODO===HANDLE IMAGE MIME TYPE
            } else if (receivedType.startsWith("image/")) {

                Uri receiveUri = (Uri) receiverdIntent
                        .getParcelableExtra(Intent.EXTRA_STREAM);

                //skype image=content://com.skype.raider.fileprovider/SkypeDownload/processed-dd99ec34-75e1-4f35-9f17-f6690cfdc4f8_ajDvqc5p.jpeg
                //whatsapp image=content://com.whatsapp.provider.media/item/fc1d7593-60b8-4a1a-8be2-26d375109903
                //storage=content://media/external/images/media/49799

                if (receiveUri != null) {
                    //do your stuff
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), receiveUri);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    profile_photo.setImageBitmap(bitmap);
                    Log.e("TAG", receiveUri.toString());
                }


                //TODO===HANDLE VIDEO MIME TYPE
            } else if (receivedType.startsWith("video/")) {
                //Skype==content://com.skype.raider.fileprovider/SkypeDownload/original-956E4EF0-933C-4D16-8D90-12102467B000-3.mp4


                Uri receiveUri = (Uri) receiverdIntent
                        .getParcelableExtra(Intent.EXTRA_STREAM);


                if (receiveUri != null) {
                    // sets the resource from the
                    // videouri to the videoView

                    videoView.setVideoURI(receiveUri);

                    // creating object of
                    // media controller class
                    MediaController mediaController = new MediaController(this);

                    // sets the anchor view
                    // anchor view for the videoView
                    mediaController.setAnchorView(videoView);

                    // sets the media player to the videoView
                    mediaController.setMediaPlayer(videoView);

                    // sets the media controller to the videoView
                    videoView.setMediaController(mediaController);

                    // starts the video
                    videoView.start();
                }


                //TODO===HANDLE APPLICATION MIME TYPE
            } else if (receivedType.startsWith("application/")) {

                Uri receiveUri = (Uri) receiverdIntent
                        .getParcelableExtra(Intent.EXTRA_STREAM);

                if (receiveUri != null) {


                    Intent pdfOpenintent = new Intent(Intent.ACTION_VIEW);
                    pdfOpenintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    pdfOpenintent.setDataAndType(receiveUri, "application/pdf");
                    try {
                        startActivity(pdfOpenintent);
                    } catch (ActivityNotFoundException e) {

                    }

                }

            } else if (receivedAction.equals(Intent.ACTION_MAIN)) {

                Log.e("TAG", "onSharedIntent: nothing shared");

            }
        }
    }


}


