package com.example.whatsapp_facebook_videosaver;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class fullscreenvideo extends AppCompatActivity {
    ImageButton back, share,status,download;
    VideoView VideoView;
    Uri uri,seconduri;
    Images images;
    File filenew;
    //String value;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreenvideo);
        VideoView = (VideoView) findViewById(R.id.full);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String s = extras.getString("valuemodel");
            filenew= new File(s);
            //The key argument here must match that used in the other activity
        }

        Intent callingactivityintent = getIntent();
        if (callingactivityintent != null) {
            Uri imageuri = callingactivityintent.getData();
            if (imageuri != null && VideoView != null) {
                Log.d("uri", String.valueOf(imageuri));
                MediaController mediaController=new MediaController(this);
                //VideoView.setVideoURI(imageuri);
                VideoView.setVideoPath(String.valueOf(imageuri));
                VideoView.setVisibility(View.VISIBLE);
                VideoView.setMediaController(mediaController);
                VideoView.setZOrderOnTop(true);
                VideoView.setBackgroundColor(Color.TRANSPARENT);
                VideoView.start();

            } else {
                Toast.makeText(this, "immage uri null", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "no calling activity intent", Toast.LENGTH_LONG).show();
        }
        /***back button***/
        back = findViewById(R.id.imageButtonback);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoView.stopPlayback();
                finish();
            }
        });
        /**share**/
        share = findViewById(R.id.imageButtonshare);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(fullscreenvideo.this,"Wait",Toast.LENGTH_LONG).show();
                Uri imageUri = FileProvider.getUriForFile(
                        fullscreenvideo.this,
                        "com.example.homefolder.example.provider", //(use your app signature + ".provider" )
                        filenew);
                // Uri uri=model.getUri();
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("video/*");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Hey this is the video subject");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, "Hey this is the video text");
                sharingIntent.putExtra(Intent.EXTRA_STREAM,imageUri);
                startActivity(Intent.createChooser(sharingIntent,"Share Video"));
            }
        });
        /**status**/
        status=findViewById(R.id.imageButtonstatus);
        status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(fullscreenvideo.this,"Wait",Toast.LENGTH_LONG).show();
                Uri imageUri = FileProvider.getUriForFile(
                        fullscreenvideo.this,
                        "com.example.homefolder.example.provider", //(use your app signature + ".provider" )
                        filenew);
                // Uri uri=model.getUri();
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("video/*");
                sharingIntent.setPackage("com.whatsapp");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Hey this is the video subject");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, "Hey this is the video text");
                sharingIntent.putExtra(Intent.EXTRA_STREAM,imageUri);
                startActivity(Intent.createChooser(sharingIntent,"Share Video"));
            }
        });
        /** Downlaod **/
        download=findViewById(R.id.imageButton5);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar snackbar1=null;
                snackbar1.make(v,"DOWNLOAD:: Please Go Back And Click On Download Button To Save Image.",Snackbar.LENGTH_INDEFINITE).setAction("Next", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Snackbar snackbar1=null;
                        snackbar1.make(v,"DELETE:: Please visit 'MY GALLERY' and click on delete button.",Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        }).show();
                    }
                }).show();
             /*   BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();
                final String bitmappath = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "WhatsBookStatusImage", null);
                Intent intent=new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(Uri.fromFile(new File(bitmappath)));
                sendBroadcast(intent);*/
                //  model model=imagelist.get(getAdapterPosition());
                //    images.download(model);
                //      BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
                //    Bitmap bitmap = bitmapDrawable.getBitmap();
                //    File mFile = new File("/WhatsBookStatusDirectory");
               /* File file=new File(constant.APP_DIR);
                if (!file.exists()){
                    file.mkdirs();
                }*/

            }
        });
    }

    @Override
    protected void onDestroy() {
        try {
            int size = 0;
            size = getContentResolver().delete(uri,
                    null, null);
            if (size == 0) {
                Log.e("NOT DELETED", "NOT DELETED");
            } else {
                Log.e("DELETED", "DELETED");
            }
        }catch (Exception e){
        }
        try {
            int sizesecond = 0;
            sizesecond = getContentResolver().delete(seconduri,
                    null, null);
            if (sizesecond == 0) {
                Log.e("NOT DELETED", "NOT DELETED");
            } else {
                Log.e("DELETED", "DELETED");
            }
        }catch (Exception e){
        }
        super.onDestroy();
    }
}