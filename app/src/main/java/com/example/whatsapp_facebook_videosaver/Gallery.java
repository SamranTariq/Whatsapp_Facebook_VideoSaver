package com.example.whatsapp_facebook_videosaver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.geniusforapp.fancydialog.FancyAlertDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class Gallery extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<gallerymodel> gallerylist;
    ProgressBar progressBar;
    Handler handler=new Handler();
    galleryadapter galleryadapter;
    private Activity activity;
    TextView openwhatsapp;
    ImageButton back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        back=(ImageButton)findViewById(R.id.imageButtonback);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        openwhatsapp=(TextView)findViewById(R.id.openwhatsapp);
        openwhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.whatsapp");
                startActivity(launchIntent);
            }
        });
        recyclerView=(RecyclerView)findViewById(R.id.recyclerview);
        progressBar=findViewById(R.id.load);
        gallerylist=new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,4));
        imagestatus();
    }
    private void imagestatus() {
        if (constant.APP_DIRECTORY.exists()){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    File[] status=constant.APP_DIRECTORY.listFiles();
                    if (status!=null && status.length>0){
                        Arrays.sort(status);
                        for (final File statusfile:status){
                            gallerymodel statusmodel=new gallerymodel(statusfile,statusfile.getName(),statusfile.getAbsolutePath(),Uri.fromFile(statusfile));
                            statusmodel.setThumb(getThumbnail(statusmodel));
                                gallerylist.add(statusmodel);
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("one","one");
                                progressBar.setVisibility(View.INVISIBLE);
                                galleryadapter=new galleryadapter(getBaseContext(),gallerylist);
                                recyclerView.setAdapter(galleryadapter);
                                galleryadapter.notifyDataSetChanged();
                                Log.d("one","two");
                            }
                        });
                    }
                    else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(getBaseContext(),"not ecist",Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }).start();
        }
    }

    private Bitmap getThumbnail(gallerymodel statusmodel) {
        if (statusmodel.isVideo()){
            return ThumbnailUtils.createVideoThumbnail(statusmodel.getFile().getAbsolutePath(),
                    MediaStore.Video.Thumbnails.MICRO_KIND);
        }
        else
        {
            return ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(statusmodel.getFile().getAbsolutePath()),
                    constant.TUMBSIZEVIDO,
                    constant.TUMBSIZEVIDO);
        }
    }

    public void deleteid(gallerymodel model) {

    }
}