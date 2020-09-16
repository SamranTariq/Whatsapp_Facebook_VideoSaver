package com.example.whatsapp_facebook_videosaver;

import android.os.AsyncTask;

public interface VideoDownloader {

    String createDirectory();

    String getVideoId(String link);

    void DownloadVideo();
}