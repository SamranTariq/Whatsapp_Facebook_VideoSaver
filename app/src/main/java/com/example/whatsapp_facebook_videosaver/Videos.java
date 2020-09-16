package com.example.whatsapp_facebook_videosaver;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;

public class Videos extends AppCompatActivity {
    RecyclerView recyclerView;
    Handler handler=new Handler();
    ArrayList<model> videoarraylist;
    videoadapter videoadapter;
    ProgressBar progressBar;
    TextView openwhatsapp;
    ImageButton back;
//    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos);
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
        videoarraylist=new ArrayList<>();
        recyclerView=findViewById(R.id.recyclerviewvideo);
        progressBar=findViewById(R.id.loading);
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,1));
        videostatus();
    }

    private void videostatus() {
        if (constant.STATUS_DIRECTORY.exists()){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    File[] status=constant.STATUS_DIRECTORY.listFiles();
                    if (status!=null && status.length>0){
                        Arrays.sort(status);
                        for (final File statusfile:status){
                            model statusmodel=new model(statusfile,statusfile.getName(),statusfile.getAbsolutePath(), Uri.fromFile(statusfile));
                            statusmodel.setThumb(getThumbnail(statusmodel));
                            if (statusmodel.isVideo()){
                                videoarraylist.add(statusmodel);
                            }
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("one","one");
                                progressBar.setVisibility(View.GONE);
                                videoadapter=new videoadapter(getBaseContext(),videoarraylist,Videos.this);
                                recyclerView.setAdapter(videoadapter);
                                videoadapter.notifyDataSetChanged();
                                Log.d("one","two");
                            }
                        });
                    }
                    else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getBaseContext(),"not exist",Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }).start();
        }
    }
    private Bitmap getThumbnail(model statusmodel) {
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

    public void download(model model) throws IOException {
        File file=new File(constant.APP_DIR);
        if (!file.exists()){
            file.mkdirs();
        }
        File filesaver=new File(file+File.separator+model.getTitle());
        if (filesaver.exists()){
            filesaver.delete();
        }
        copyvideo(model.getFile(),filesaver);
        Toast.makeText(this,"Complete",Toast.LENGTH_LONG).show();
        Intent intent=new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(filesaver));
        sendBroadcast(intent);
    }

    private void copyvideo(File file, File filesaver) throws IOException {
        if (!filesaver.getParentFile().exists()){
            filesaver.getParentFile().mkdirs();
        }
        if (!filesaver.exists()){
            filesaver.createNewFile();
        }
        FileChannel source=null;
        FileChannel destination=null;
        source=new FileInputStream(file).getChannel();
        destination=new FileOutputStream(filesaver).getChannel();
        destination.transferFrom(source,0,source.size());
        source.close();
        destination.close();
    }
    public void show(model model) {
        File file=new File(String.valueOf(constant.STATUS_DIRECTORY));
        File filesaver=new File(model.getPath());
        Uri path=model.getUri();
        Intent intent=new Intent(this,fullscreenvideo.class);
        intent.setData(path);
        //    intent.putExtra("valuemodel", model);
        startActivity(intent);
    }

    public void share(model model) {
        Toast.makeText(this,"Wait",Toast.LENGTH_LONG).show();
        File file=new File(model.getPath());
       /* String path = model.getPath();

        ContentValues content = new ContentValues(4);
        content.put(MediaStore.Video.VideoColumns.DATE_ADDED,
                System.currentTimeMillis() / 1000);
        content.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
        content.put(MediaStore.Video.Media.DATA, path);

        ContentResolver resolver = getApplicationContext().getContentResolver();
        uri = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, content);*/
        Uri imageUri = FileProvider.getUriForFile(
                this,
                "com.example.homefolder.example.provider", //(use your app signature + ".provider" )
                file);
       // Uri uri=model.getUri();
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("video/*");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Hey this is the video subject");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, "Hey this is the video text");
        sharingIntent.putExtra(Intent.EXTRA_STREAM,imageUri);
        startActivity(Intent.createChooser(sharingIntent,"Share Video"));
    }
    @Override
    protected void onDestroy() {
        /*try {
            int size = 0;
            size = getContentResolver().delete(uri,
                    null, null);
            if (size == 0) {
                Log.e("NOT DELETED", "NOT DELETED");
            } else {
                Log.e("DELETED", "DELETED");
            }
        }catch (Exception e){
        }*/
        super.onDestroy();
    }

    public void status(model model) {
        Toast.makeText(this,"Wait",Toast.LENGTH_LONG).show();
        File file=new File(model.getPath());
        Uri imageUri = FileProvider.getUriForFile(
                this,
                "com.example.homefolder.example.provider", //(use your app signature + ".provider" )
                file);
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("video/*");
        sharingIntent.setPackage("com.whatsapp");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Hey this is the video subject");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, "Hey this is the video text");
        sharingIntent.putExtra(Intent.EXTRA_STREAM,imageUri);
        startActivity(Intent.createChooser(sharingIntent,"Share Video"));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}