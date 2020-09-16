package com.example.whatsapp_facebook_videosaver;

import android.graphics.Bitmap;
import android.net.Uri;

import java.io.File;
import java.net.URI;

public class model {
    private static final String MP4=".mp4";
    private final File file;
    private Bitmap thumb;
    Uri uri;
    private String title,path;
    private boolean video;

    public model(File file, String title, String path,Uri uri) {
        this.file = file;
        this.title = title;
        this.path = path;
        this.uri=uri;
        this.video=file.getName().endsWith(MP4);
    }

    public File getFile() {
        return file;
    }

    public Bitmap getThumb() {
        return thumb;
    }

    public void setThumb(Bitmap thumb) {
        this.thumb = thumb;
    }
    public String getTitle() {
        return title;
    }

    public String getPath() {
        return path;

    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public boolean isVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }
}
