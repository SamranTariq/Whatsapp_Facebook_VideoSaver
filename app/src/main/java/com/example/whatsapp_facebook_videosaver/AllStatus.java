package com.example.whatsapp_facebook_videosaver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;

public class AllStatus extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<model> allarraylist;
    Handler handler=new Handler();
    ProgressBar progressBar;
    alladapter alladapter;
    TextView openwhatsapp;
    ImageButton back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_status);
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
        recyclerView=(RecyclerView)findViewById(R.id.recyclerviewallstatus);
        progressBar=findViewById(R.id.load);
        allarraylist=new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,4));
        allstatus();
    }

    private void allstatus() {
        if (constant.STATUS_DIRECTORY.exists()){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    File[] status=constant.STATUS_DIRECTORY.listFiles();
                    if (status!=null && status.length>0){
                        Arrays.sort(status);
                        for (final File statusfile:status){
                            model statusmodel=new model(statusfile,statusfile.getName(),statusfile.getAbsolutePath(),Uri.fromFile(statusfile));
                            statusmodel.setThumb(getThumbnail(statusmodel));
                                allarraylist.add(statusmodel);
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.INVISIBLE);
                                Log.d("one","one");
                                alladapter=new alladapter(getBaseContext(),allarraylist,AllStatus.this);
                                recyclerView.setAdapter(alladapter);
                                alladapter.notifyDataSetChanged();
                                Log.d("one","two");
                            }
                        });
                    }
                    else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(getBaseContext(),"No Directory",Toast.LENGTH_LONG).show();
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
                    constant.TUMBSIZE,
                    constant.TUMBSIZE);
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
        copyimage(model.getFile(),filesaver);
        Toast.makeText(this,"Complete",Toast.LENGTH_LONG).show();
        Intent intent=new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(filesaver));
        sendBroadcast(intent);
    }

    private void copyimage(File file, File filesaver) throws IOException {
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
        Intent intent=new Intent(this,fullscreen.class);
        intent.setData(Uri.fromFile(filesaver));
        intent.putExtra("valuemodel", model.getPath());
        //    intent.putExtra("valuemodel", model);
        startActivity(intent);
    }

    public void videoshow(model model) {
        File file=new File(String.valueOf(constant.STATUS_DIRECTORY));
        File filesaver=new File(model.getPath());
        Uri path=model.getUri();
        Intent intent=new Intent(this,fullscreenvideo.class);
        intent.setData(path);
        intent.putExtra("valuemodel", model.getPath());
        //    intent.putExtra("valuemodel", model);
        startActivity(intent);
    }
}