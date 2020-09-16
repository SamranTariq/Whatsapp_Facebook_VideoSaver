package com.example.whatsapp_facebook_videosaver;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class fullscreen extends AppCompatActivity {
    ImageButton back, share,status,download;
    ImageView imageView;
    File filenew;
    //String value;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String s = extras.getString("valuemodel");
            filenew= new File(s);
            //The key argument here must match that used in the other activity
        }
        imageView = (ImageView) findViewById(R.id.full);
        Intent callingactivityintent = getIntent();
        if (callingactivityintent != null) {
            Uri imageuri = callingactivityintent.getData();
            //file=new File(String.valueOf(imageuri));
            if (imageuri != null && imageView != null) {
                Picasso.get().load(imageuri).into(imageView);
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
                finish();
            }
        });
        /**share**/
        share = findViewById(R.id.imageButtonshare);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(fullscreen.this,"wait",Toast.LENGTH_LONG).show();
               // File file=new File(String.valueOf(imageView.getDrawable()));
                Uri imageUri = FileProvider.getUriForFile(
                        fullscreen.this,
                        "com.example.homefolder.example.provider", //(use your app signature + ".provider" )
                        filenew);
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_STREAM, imageUri);
                startActivity(Intent.createChooser(intent, "Share Image"));
            }
        });
        /**status**/
        status=findViewById(R.id.imageButtonstatus);
        status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(fullscreen.this,"wait",Toast.LENGTH_LONG).show();
                // File file=new File(String.valueOf(imageView.getDrawable()));
                Uri imageUri = FileProvider.getUriForFile(
                        fullscreen.this,
                        "com.example.homefolder.example.provider", //(use your app signature + ".provider" )
                        filenew);
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/*");
                intent.setPackage("com.whatsapp");
                intent.putExtra(Intent.EXTRA_STREAM, imageUri);
                startActivity(Intent.createChooser(intent, "Share Image"));
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
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}