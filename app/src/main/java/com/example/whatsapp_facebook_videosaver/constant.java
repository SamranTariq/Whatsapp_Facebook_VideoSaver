package com.example.whatsapp_facebook_videosaver;

import android.os.Environment;

import java.io.File;

public class constant {
    public static File STATUS_DIRECTORY=new File(Environment.getExternalStorageDirectory() + File.separator+"WhatsApp/Media/.Statuses");
    public static File APP_DIRECTORY=new File(Environment.getExternalStorageDirectory() + File.separator+"WhatsBookStatusDirectory");
    public static final String APP_DIR =Environment.getExternalStorageDirectory()+File.separator+"WhatsBookStatusDirectory";
    public static final int TUMBSIZE=128;
    public static final int TUMBSIZEVIDO=360;
}