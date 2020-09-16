package com.example.whatsapp_facebook_videosaver;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.daimajia.numberprogressbar.NumberProgressBar;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.Drawer;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.capitalize;

public class onfacebook extends AppCompatActivity {

    private static final String LOG_TAG = "EXAMPLE";
    @SuppressLint("StaticFieldLeak")
    private WebView webo;
    Toolbar mToolbar;
    private static String URL = "https://www.facebook.com/login/";
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private Button streamButton, downloadButton, cancelButton;
    private ProgressBar progress;
    private SwipeRefreshLayout recyclerLayout;
    ProgressDialog mProgressDialog;
    String fileN = null ;
    public static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 123;
    boolean result;
    String urlString;
    Drawer resultDrawer;
    AccountHeader headerResult;
    NativeExpressAdView adNativeView, largeNativeAd;
    VideoController mVideoController, largeMVideoController;
    Dialog main_dialog, downloadDialog;

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(Constant.theme);
        setContentView(R.layout.activity_onfacebook);
        gotoFB();
        if (getAppIntro(this)) {
            Intent i = new Intent(this, IntroActivity.class);
            startActivity(i);
        }
        progress = (ProgressBar) findViewById(R.id.progressBar);
        progress.setVisibility(View.GONE);
        result = checkPermission();
        if(result){
            checkFolder();
        }
        if (!isConnectingToInternet(this)) {
            Toast.makeText(this, "Please Connect to Internet", Toast.LENGTH_LONG).show();
        }
    }

    private boolean getAppIntro(onfacebook mainActivity) {
        SharedPreferences preferences;
        preferences = mainActivity.getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE);
        return preferences.getBoolean("AppIntro", true);
    }

    @JavascriptInterface
    public void processVideo(String str, String str2) {
        Log.e("WEBVIEWJS", "RUN");
        Log.e("WEBVIEWJS", str);
        Bundle args = new Bundle();
        args.putString("vid_data", str);
        urlString = str;
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                // put your code to show dialog here.
                LayoutInflater dialogLayout = LayoutInflater.from(onfacebook.this);
                View DialogView = dialogLayout.inflate(R.layout.dialog_download, null);
                main_dialog = new Dialog(onfacebook.this, R.style.MyDialogTheme);
                main_dialog.setContentView(DialogView);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(main_dialog.getWindow().getAttributes());
                lp.width = (getResources().getDisplayMetrics().widthPixels);
                lp.height = (int)(getResources().getDisplayMetrics().heightPixels*0.65);
                main_dialog.getWindow().setAttributes(lp);
                streamButton = (Button) DialogView.findViewById(R.id.streamButton);
                downloadButton = (Button)DialogView.findViewById(R.id.downloadButton);
                cancelButton = (Button) DialogView.findViewById(R.id.cancelButton);
                // Set its video options.
                adNativeView = (NativeExpressAdView)DialogView.findViewById(R.id.nativeAD);
                adNativeView.setVideoOptions(new VideoOptions.Builder()
                        .setStartMuted(true)
                        .build());

                // The VideoController can be used to get lifecycle events and info about an ad's video
                // asset. One will always be returned by getVideoController, even if the ad has no video
                // asset.
                mVideoController = adNativeView.getVideoController();
                mVideoController.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                    @Override
                    public void onVideoEnd() {
                        Log.d(LOG_TAG, "Video playback is finished.");
                        super.onVideoEnd();
                    }
                });

                // Set an AdListener for the AdView, so the Activity can take action when an ad has finished
                // loading.
                adNativeView.setAdListener(new AdListener() {
                    @Override
                    public void onAdLoaded() {
                        if (mVideoController.hasVideoContent()) {
                            Log.d(LOG_TAG, "Received an ad that contains a video asset.");
                        } else {
                            Log.d(LOG_TAG, "Received an ad that does not contain a video asset.");
                        }
                    }
                });


                adNativeView.loadAd(new AdRequest.Builder().addTestDevice("0224C93FFD644350DCD7F3D7557C6A5C").build());

                main_dialog.setCancelable(false);
                main_dialog.setCanceledOnTouchOutside(false);
                main_dialog.show();

                streamButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(onfacebook.this, "Streaming", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(onfacebook.this, VideoPlayer.class);
                        intent.putExtra("video_url",urlString);
                        startActivity(intent);
                        main_dialog.dismiss();
                    }
                });
                downloadButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(onfacebook.this, "Downloading", Toast.LENGTH_SHORT).show();
                        newDownload(urlString);
                        main_dialog.dismiss();
                    }
                });
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        main_dialog.dismiss();
                    }
                });
            }
        });
    }

    public void setValue(int progress) {
        this.progress.setProgress(progress);
        if (progress>=100) // code to be added
            this.progress.setVisibility(View.GONE);
    }

    private void createPopupDialog() {
    }

    public static boolean isConnectingToInternet(Context context) {

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    public void gotoFB(){
        webo = (WebView) findViewById(R.id.webViewFb);
        webo.getSettings().setJavaScriptEnabled(true);
        webo.addJavascriptInterface(this, "FBDownloader");
        webo.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progress.setVisibility(View.VISIBLE);
                onfacebook.this.setValue(newProgress);
                super.onProgressChanged(view, newProgress);
            }
        });
        webo.setWebViewClient(new WebViewClient() {



            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progress.setVisibility(View.VISIBLE);
                onfacebook.this.progress.setProgress(0);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                onfacebook.this.webo.loadUrl("javascript:(function() { var el = document.querySelectorAll('div[data-sigil]');for(var i=0;i<el.length; i++){var sigil = el[i].dataset.sigil;if(sigil.indexOf('inlineVideo') > -1){delete el[i].dataset.sigil;var jsonData = JSON.parse(el[i].dataset.store);el[i].setAttribute('onClick', 'FBDownloader.processVideo(\"'+jsonData['src']+'\");');}}})()");
                Log.e("WEBVIEWFIN", url);
                progress.setVisibility(View.GONE);
                onfacebook.this.progress.setProgress(100);
                super.onPageFinished(view, url);
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                onfacebook.this.webo.loadUrl("javascript:(function prepareVideo() { var el = document.querySelectorAll('div[data-sigil]');for(var i=0;i<el.length; i++){var sigil = el[i].dataset.sigil;if(sigil.indexOf('inlineVideo') > -1){delete el[i].dataset.sigil;console.log(i);var jsonData = JSON.parse(el[i].dataset.store);el[i].setAttribute('onClick', 'FBDownloader.processVideo(\"'+jsonData['src']+'\",\"'+jsonData['videoID']+'\");');}}})()");
                onfacebook.this.webo.loadUrl("javascript:( window.onload=prepareVideo;)()");
            }
        });
        CookieManager cookieManager = CookieManager.getInstance();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.createInstance(this);
        }
        cookieManager.setAcceptCookie(true);
        webo.loadUrl(URL);
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    public class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context) {
            this.context = context;
        }
        private NumberProgressBar bnp;
        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                java.net.URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                int fileLength = connection.getContentLength();

                input = connection.getInputStream();
                fileN = "FbDownloader_" + UUID.randomUUID().toString().substring(0, 10) + ".mp4";
                File filename = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                        Constants.FOLDER_NAME, fileN);
                output = new FileOutputStream(filename);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    if (fileLength > 0)
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            LayoutInflater dialogLayout = LayoutInflater.from(onfacebook.this);
            View DialogView = dialogLayout.inflate(R.layout.progress_dialog, null);
            downloadDialog = new Dialog(onfacebook.this, R.style.CustomAlertDialog);
            downloadDialog.setContentView(DialogView);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(downloadDialog.getWindow().getAttributes());
            lp.width = (getResources().getDisplayMetrics().widthPixels);
            lp.height = (int)(getResources().getDisplayMetrics().heightPixels*0.65);
            downloadDialog.getWindow().setAttributes(lp);

            final Button cancel = (Button) DialogView.findViewById(R.id.cancel_btn);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //stopping the Asynctask
                    cancel(true);
                    downloadDialog.dismiss();

                }
            });
            largeNativeAd = (NativeExpressAdView) DialogView.findViewById(R.id.adView);
            largeNativeAd.loadAd(new AdRequest.Builder().addTestDevice("0224C93FFD644350DCD7F3D7557C6A5C").build());
            downloadDialog.setCancelable(false);
            downloadDialog.setCanceledOnTouchOutside(false);
            bnp = (NumberProgressBar)DialogView.findViewById(R.id.number_progress_bar);
            bnp.setProgress(0);
            bnp.setMax(100);
            downloadDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            bnp.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            downloadDialog.dismiss();
            if (result != null)
                Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();
            else
                Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();
            MediaScannerConnection.scanFile(onfacebook.this,
                    new String[]{Environment.getExternalStorageDirectory().getAbsolutePath() +
                            Constants.FOLDER_NAME + fileN}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String newpath, Uri newuri) {
                            Log.i("ExternalStorage", "Scanned " + newpath + ":");
                            Log.i("ExternalStorage", "-> uri=" + newuri);
                        }
                    });

        }
    }

    public void newDownload(String url) {
        final DownloadTask downloadTask = new DownloadTask(onfacebook.this);
        downloadTask.execute(url);
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean checkPermission() {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(onfacebook.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(onfacebook.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(onfacebook.this);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("Write Storage permission is necessary to Download Images and Videos!!!");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(onfacebook.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions(onfacebook.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }
    public void checkAgain() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(onfacebook.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(onfacebook.this);
            alertBuilder.setCancelable(true);
            alertBuilder.setTitle("Permission necessary");
            alertBuilder.setMessage("Write Storage permission is necessary to Download Images and Videos!!!");
            alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(onfacebook.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
                }
            });
            AlertDialog alert = alertBuilder.create();
            alert.show();
        } else {
            ActivityCompat.requestPermissions(onfacebook.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkFolder();
                } else {
                    //code for deny
                    checkAgain();
                }
                break;
        }
    }

    public void checkFolder() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/FBDownloader/";
        File dir = new File(path);
        boolean isDirectoryCreated = dir.exists();
        if (!isDirectoryCreated) {
            isDirectoryCreated = dir.mkdir();
        }
        if (isDirectoryCreated) {
            // do something\
            Log.d("Folder", "Already Created");
        }
    }
}































/*
import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.capitalize;

public class onfacebook extends AppCompatActivity {

    private static final String LOG_TAG = "EXAMPLE";
    @SuppressLint("StaticFieldLeak")
    private WebView webo;
    Toolbar mToolbar;
    private static String URL = "https://www.facebook.com/login/";
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private Button streamButton, downloadButton, cancelButton;
    private ProgressBar progress;
    private SwipeRefreshLayout recyclerLayout;
    ProgressDialog mProgressDialog;
    String fileN = null ;
    public static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 123;
    boolean result;
    String urlString;
    Drawer resultDrawer;
    AccountHeader headerResult;
    NativeExpressAdView adNativeView, largeNativeAd;
    VideoController mVideoController, largeMVideoController;
    Dialog main_dialog, downloadDialog;

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(Constant.theme);
        setContentView(R.layout.activity_onfacebook);
        gotoFB();
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setBackgroundColor(Constant.color);
        setSupportActionBar(mToolbar);

        if (getAppIntro(this)) {
            Intent i = new Intent(this, IntroActivity.class);
            startActivity(i);
        }
        progress = (ProgressBar) findViewById(R.id.progressBar);
        progress.setVisibility(View.GONE);
        result = checkPermission();
        if(result){
            checkFolder();
        }
        if (!isConnectingToInternet(this)) {
            Toast.makeText(this, "Please Connect to Internet", Toast.LENGTH_LONG).show();
        }

        ColorDrawable cd = new ColorDrawable(Constant.color);
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(cd)
                .withSelectionListEnabledForSingleProfile(false)
                .withAlternativeProfileHeaderSwitching(false)
                .withCompactStyle(false)
                .withDividerBelowHeader(false)
                .withProfileImagesVisible(true)
                .addProfiles(new ProfileDrawerItem().withIcon(R.mipmap.ic_launcher).withName(getResources().getString(R.string.app_name)).withEmail(getResources()
                        .getString(R.string.developer_email)))
                .build();
        resultDrawer = new DrawerBuilder()
                .withActivity(this)
                .withSelectedItem(-1)
                .withFullscreen(true)
                .withAccountHeader(headerResult)
                .withActionBarDrawerToggle(true)
                .withCloseOnClick(true)
                .withMultiSelect(false)
                .withTranslucentStatusBar(true)
                .withToolbar(mToolbar)
                .addDrawerItems(
                        new PrimaryDrawerItem().withSelectable(false).withName(R.string.app_name),
                        new PrimaryDrawerItem().withSelectable(false).withName("Gallery").withIcon(R.drawable.ic_home_black_24dp).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                            @Override
                            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                              //  Intent intent = new Intent(onfacebook.this, GalleryActivity.class);
                                //startActivity(intent);
                                return false;
                            }
                        }),
                        new PrimaryDrawerItem().withSelectable(false).withName("Recommend to Friends").withIcon(R.drawable.ic_share_black_24dp).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                            @Override
                            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                final String shareappPackageName = getPackageName();
                                Intent sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out " +getResources().getString(R.string.app_name) + " App at: https://play.google.com/store/apps/details?id=" + shareappPackageName);
                                sendIntent.setType("text/plain");
                                startActivity(sendIntent);
                                return false;
                            }
                        }),
                        new PrimaryDrawerItem().withSelectable(false).withName("Rate Us").withIcon(R.drawable.ic_thumb_up_black_24dp).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                            @Override
                            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                final String appPackageName = getPackageName();
                                try {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                } catch (ActivityNotFoundException anfe) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                }
                                return false;
                            }
                        }),
                        new PrimaryDrawerItem().withSelectable(false).withName("Settings").withIcon(R.drawable.ic_settings_applications_black_24dp).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                            @Override
                            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                Intent intent = new Intent(onfacebook.this, SettingsActivity.class);
                                startActivity(intent);
                                return false;
                            }
                        }),
                        new PrimaryDrawerItem().withSelectable(false).withName("Feedback").withIcon(R.drawable.ic_feedback_black_24dp).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                            @Override
                            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                DisplayMetrics displaymetrics = new DisplayMetrics();
                                getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                                int height = displaymetrics.heightPixels;
                                int width = displaymetrics.widthPixels;
                                PackageManager manager = getApplicationContext().getPackageManager();
                                PackageInfo info = null;
                                try {
                                    info = manager.getPackageInfo(getPackageName(), 0);
                                } catch (PackageManager.NameNotFoundException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                                String version = info.versionName;
                                Intent i = new Intent(Intent.ACTION_SEND);
                                i.setType("message/rfc822");
                                i.putExtra(Intent.EXTRA_EMAIL, new String[]{getResources().getString(R.string.developer_email)});
                                i.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name) + version);
                                i.putExtra(Intent.EXTRA_TEXT,
                                        "\n" + " Device :" + getDeviceName() +
                                                "\n" + " System Version:" + Build.VERSION.SDK_INT +
                                                "\n" + " Display Height  :" + height + "px" +
                                                "\n" + " Display Width  :" + width + "px" +
                                                "\n\n" + "Have a problem? Please share it with us and we will do our best to solve it!" +
                                                "\n");
                                startActivity(Intent.createChooser(i, "Send Email"));
                                return false;
                            }
                        }),
                        new PrimaryDrawerItem().withSelectable(false).withName("Exit").withIcon(R.drawable.ic_exit_to_app_black_24dp).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                            @Override
                            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                finish();
                                return false;
                            }
                        })
                ).
                        withSavedInstance(savedInstanceState)
                .build();

    }

    private boolean getAppIntro(onfacebook mainActivity) {
        SharedPreferences preferences;
        preferences = mainActivity.getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE);
        return preferences.getBoolean("AppIntro", true);
    }

    private String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    @JavascriptInterface
    public void processVideo(String str, String str2) {
        Log.e("WEBVIEWJS", "RUN");
        Log.e("WEBVIEWJS", str);
        Bundle args = new Bundle();
        args.putString("vid_data", str);
        urlString = str;
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                // put your code to show dialog here.
                LayoutInflater dialogLayout = LayoutInflater.from(onfacebook.this);
                View DialogView = dialogLayout.inflate(R.layout.dialog_download, null);
                main_dialog = new Dialog(onfacebook.this, R.style.MyDialogTheme);
                main_dialog.setContentView(DialogView);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(main_dialog.getWindow().getAttributes());
                lp.width = (getResources().getDisplayMetrics().widthPixels);
                lp.height = (int)(getResources().getDisplayMetrics().heightPixels*0.65);
                main_dialog.getWindow().setAttributes(lp);
                streamButton = (Button) DialogView.findViewById(R.id.streamButton);
                downloadButton = (Button)DialogView.findViewById(R.id.downloadButton);
                cancelButton = (Button) DialogView.findViewById(R.id.cancelButton);
                // Set its video options.
                adNativeView = (NativeExpressAdView)DialogView.findViewById(R.id.nativeAD);
                adNativeView.setVideoOptions(new VideoOptions.Builder()
                        .setStartMuted(true)
                        .build());

                // The VideoController can be used to get lifecycle events and info about an ad's video
                // asset. One will always be returned by getVideoController, even if the ad has no video
                // asset.
                mVideoController = adNativeView.getVideoController();
                mVideoController.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                    @Override
                    public void onVideoEnd() {
                        Log.d(LOG_TAG, "Video playback is finished.");
                        super.onVideoEnd();
                    }
                });

                // Set an AdListener for the AdView, so the Activity can take action when an ad has finished
                // loading.
                adNativeView.setAdListener(new AdListener() {
                    @Override
                    public void onAdLoaded() {
                        if (mVideoController.hasVideoContent()) {
                            Log.d(LOG_TAG, "Received an ad that contains a video asset.");
                        } else {
                            Log.d(LOG_TAG, "Received an ad that does not contain a video asset.");
                        }
                    }
                });


                adNativeView.loadAd(new AdRequest.Builder().addTestDevice("0224C93FFD644350DCD7F3D7557C6A5C").build());

                main_dialog.setCancelable(false);
                main_dialog.setCanceledOnTouchOutside(false);
                main_dialog.show();

//                dialogBuilder = new AlertDialog.Builder(MainActivity.this , R.style.MyDialogTheme);
//                View view = getLayoutInflater().inflate(R.layout.dialog_download, null);
//                streamButton = (Button) view.findViewById(R.id.streamButton);
//                downloadButton = (Button)view.findViewById(R.id.downloadButton);
//                cancelButton = (Button) view.findViewById(R.id.cancelButton);
//                adNativeView = (NativeExpressAdView)view.findViewById(R.id.nativeAD);
//                dialogBuilder.setView(view);
//                dialog = dialogBuilder.create();
//                adNativeView = (NativeExpressAdView)view.findViewById(R.id.nativeAD);
//                AdRequest request = new AdRequest.Builder()
//                        .addTestDevice("C69095F3C24675F5F8C9B5031B0E6EEB")
//                        .build();
//                adNativeView.loadAd(request);
//                dialog.show();
                streamButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(onfacebook.this, "Streaming", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(onfacebook.this, VideoPlayer.class);
                        intent.putExtra("video_url",urlString);
                        startActivity(intent);
                        main_dialog.dismiss();
                    }
                });
                downloadButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(onfacebook.this, "Downloading", Toast.LENGTH_SHORT).show();
                        newDownload(urlString);
                        main_dialog.dismiss();
                    }
                });
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        main_dialog.dismiss();
                    }
                });
            }
        });
    }

    public void setValue(int progress) {
        this.progress.setProgress(progress);
        if (progress>=100) // code to be added
            this.progress.setVisibility(View.GONE);
    }

    private void createPopupDialog() {
    }

    public static boolean isConnectingToInternet(Context context) {

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.action_go_to_fb:
                gotoFB();
                break;
            case R.id.action_refresh:
                if(webo != null){
                    webo.reload();
                }
                break;
            default:
                break;
        }
        return true;
    }

    public void gotoFB(){
        webo = (WebView) findViewById(R.id.webViewFb);
        webo.clearCache(true);
        webo.clearHistory();
        webo.getSettings().setJavaScriptEnabled(true);
        webo.getSettings().setPluginState(WebSettings.PluginState.ON);
        webo.getSettings().setMediaPlaybackRequiresUserGesture(true);
        webo.addJavascriptInterface(this, "FBDownloader");
        webo.setHorizontalScrollBarEnabled(false);
        WebSettings webSettings = webo.getSettings();
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setDomStorageEnabled(true);
       // webo.onPause();
     //   webo.pauseTimers();

        webo.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                progress.setVisibility(View.VISIBLE);
//                MainActivity.this.progress.setProgress(0);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                onfacebook.this.webo.loadUrl("javascript:(function() { var el = document.querySelectorAll('div[data-sigil]');for(var i=0;i<el.length; i++){var sigil = el[i].dataset.sigil;if(sigil.indexOf('inlineVideo') > -1){delete el[i].dataset.sigil;var jsonData = JSON.parse(el[i].dataset.store);el[i].setAttribute('onClick', 'FBDownloader.processVideo(\"'+jsonData['src']+'\");');}}})()");
//              Log.e("WEBVIEWFIN", url);
                progress.setVisibility(View.GONE);
//                MainActivity.this.progress.setProgress(100);
                super.onPageFinished(view, url);
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                onfacebook.this.webo.loadUrl("javascript:(function prepareVideo() { var el = document.querySelectorAll('div[data-sigil]');for(var i=0;i<el.length; i++){var sigil = el[i].dataset.sigil;if(sigil.indexOf('inlineVideo') > -1){delete el[i].dataset.sigil;console.log(i);var jsonData = JSON.parse(el[i].dataset.store);el[i].setAttribute('onClick', 'FBDownloader.processVideo(\"'+jsonData['src']+'\",\"'+jsonData['videoID']+'\");');}}})()");
                onfacebook.this.webo.loadUrl("javascript:( window.onload=prepareVideo;)()");
            }

        });
        CookieManager cookieManager = CookieManager.getInstance();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.createInstance(this);
        }
        cookieManager.setAcceptCookie(true);
        webo.loadUrl(URL);
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    public class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context) {
            this.context = context;
        }
        private NumberProgressBar bnp;
        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                java.net.URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                int fileLength = connection.getContentLength();

                input = connection.getInputStream();
                fileN = "FbDownloader_" + UUID.randomUUID().toString().substring(0, 10) + ".mp4";
                File filename = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                        Constants.FOLDER_NAME, fileN);
                output = new FileOutputStream(filename);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    if (fileLength > 0)
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            LayoutInflater dialogLayout = LayoutInflater.from(onfacebook.this);
            View DialogView = dialogLayout.inflate(R.layout.progress_dialog, null);
            downloadDialog = new Dialog(onfacebook.this, R.style.CustomAlertDialog);
            downloadDialog.setContentView(DialogView);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(downloadDialog.getWindow().getAttributes());
            lp.width = (getResources().getDisplayMetrics().widthPixels);
            lp.height = (int)(getResources().getDisplayMetrics().heightPixels*0.65);
            downloadDialog.getWindow().setAttributes(lp);

            final Button cancel = (Button) DialogView.findViewById(R.id.cancel_btn);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //stopping the Asynctask
                    cancel(true);
                    downloadDialog.dismiss();

                }
            });
            largeNativeAd = (NativeExpressAdView) DialogView.findViewById(R.id.adView);
//            largeNativeAd.setVideoOptions(new VideoOptions.Builder()
//                    .setStartMuted(true)
//                    .build());
//            mVideoController = largeNativeAd.getVideoController();
//            largeMVideoController.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
//                @Override
//                public void onVideoEnd() {
//                    Log.d(LOG_TAG, "Video playback is finished.");
//                    super.onVideoEnd();
//                }
//            });
//            largeNativeAd.setAdListener(new AdListener() {
//                @Override
//                public void onAdLoaded() {
//                    if (largeMVideoController.hasVideoContent()) {
//                        Log.d(LOG_TAG, "Received an ad that contains a video asset.");
//                    } else {
//                        Log.d(LOG_TAG, "Received an ad that does not contain a video asset.");
//                    }
//                }
//            });
            largeNativeAd.loadAd(new AdRequest.Builder().addTestDevice("0224C93FFD644350DCD7F3D7557C6A5C").build());
            downloadDialog.setCancelable(false);
            downloadDialog.setCanceledOnTouchOutside(false);
            bnp = (NumberProgressBar)DialogView.findViewById(R.id.number_progress_bar);
            bnp.setProgress(0);
            bnp.setMax(100);
            downloadDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            bnp.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            downloadDialog.dismiss();
            if (result != null)
                Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();
            else
                Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();
            MediaScannerConnection.scanFile(onfacebook.this,
                    new String[]{Environment.getExternalStorageDirectory().getAbsolutePath() +
                            Constants.FOLDER_NAME + fileN}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String newpath, Uri newuri) {
                            Log.i("ExternalStorage", "Scanned " + newpath + ":");
                            Log.i("ExternalStorage", "-> uri=" + newuri);
                        }
                    });

        }
    }

    public void newDownload(String url) {
        final DownloadTask downloadTask = new DownloadTask(onfacebook.this);
        downloadTask.execute(url);
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean checkPermission() {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(onfacebook.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(onfacebook.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(onfacebook.this);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("Write Storage permission is necessary to Download Images and Videos!!!");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(onfacebook.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions(onfacebook.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }
    public void checkAgain() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(onfacebook.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(onfacebook.this);
            alertBuilder.setCancelable(true);
            alertBuilder.setTitle("Permission necessary");
            alertBuilder.setMessage("Write Storage permission is necessary to Download Images and Videos!!!");
            alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(onfacebook.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
                }
            });
            AlertDialog alert = alertBuilder.create();
            alert.show();
        } else {
            ActivityCompat.requestPermissions(onfacebook.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkFolder();
                } else {
                    //code for deny
                    checkAgain();
                }
                break;
        }
    }

    public void checkFolder() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/FBDownloader/";
        File dir = new File(path);
        boolean isDirectoryCreated = dir.exists();
        if (!isDirectoryCreated) {
            isDirectoryCreated = dir.mkdir();
        }
        if (isDirectoryCreated) {
            // do something\
            Log.d("Folder", "Already Created");
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        // put your code here...
//        if (mAdView != null) {
//            mAdView.resume();
//        }

        Log.d("chk","1.1");
        webo.onResume();
        webo.resumeTimers();
        //webo.stopLoading();

        Log.d("chk","1");


    }
    @Override
    public void onPause(){
//        if (mAdView != null) {
//            mAdView.pause();
//        }
        super.onPause();
        // put your code here...
        webo.onPause();
        webo.pauseTimers();

        Log.d("chk","2");

    }
    @Override
    public void onStop(){
        super.onStop();

    }
    @Override
    protected void onDestroy() {
//        if (mAdView != null) {
//            mAdView.destroy();
//        }
        super.onDestroy();
        Log.d("chk","3");
        webo.loadUrl("about:blank");
        webo.stopLoading();
        webo.setWebChromeClient(null);
        webo.setWebViewClient(null);
        webo.destroy();
        webo=null;
    }

}*/
/*
import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebBackForwardList;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.common.util.IOUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.Drawer;

import org.json.JSONArray;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.UUID;

public class onfacebook extends AppCompatActivity{
    private String postUrl = "http://www.google.com";
    private ProgressBar progressBar;
    private float m_downX;
  //  private static AutoCompleteTextView SearchText;
    final Activity activity = this;
    FloatingActionButton fab;
    private boolean AdblockEnabled = true;
    private BottomNavigationView bottomNavigationView;
    private CountDownTimer timer;
    //private Boolean SearchHasFocus = false;
    private View bottomSheet;
    private View webViewCon;
    private String SessionID;
    SharedPreferences sharedPrefs;
    public Boolean WebviewLoaded=false;

    RelativeLayout adViewContainer;
    public static final int REQUEST_PERMISSION_CODE = 1001;
    public static final String REQUEST_PERMISSION = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final String LOG_TAG = "EXAMPLE";
    @SuppressLint("StaticFieldLeak")
    private WebView webo;
    Toolbar mToolbar;
    private static String URL = "https://www.facebook.com/login/";
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private Button streamButton, downloadButton, cancelButton;
    private ProgressBar progress;
    private SwipeRefreshLayout recyclerLayout;
    ProgressDialog mProgressDialog;
    String fileN = null ;
    public static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 123;
    boolean result;
    String urlString;
    Drawer resultDrawer;
    AccountHeader headerResult;
    NativeExpressAdView adNativeView, largeNativeAd;
    VideoController mVideoController, largeMVideoController;
    Dialog main_dialog, downloadDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(Constant.theme);
        setContentView(R.layout.activity_onfacebook);

        if (getAppIntro(this)) {
            Intent i = new Intent(this, IntroActivity.class);
            startActivity(i);
        }
        progress = (ProgressBar) findViewById(R.id.progressBar);
        progress.setVisibility(View.GONE);
        result = checkPermission();
        if(result){
            checkFolder();
        }
        if (!isConnectingToInternet(this)) {
            Toast.makeText(this, "Please Connect to Internet", Toast.LENGTH_LONG).show();
        }
        gotoFB();
    }

    private boolean getAppIntro(onfacebook mainActivity) {
        SharedPreferences preferences;
        preferences = mainActivity.getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE);
        return preferences.getBoolean("AppIntro", true);
    }
    @JavascriptInterface
    public void processVideo(String str, String str2) {
        Log.e("WEBVIEWJS", "RUN");
        Log.e("WEBVIEWJS", str);
        Bundle args = new Bundle();
        args.putString("vid_data", str);
        urlString = str;
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                // put your code to show dialog here.
                LayoutInflater dialogLayout = LayoutInflater.from(onfacebook.this);
                View DialogView = dialogLayout.inflate(R.layout.dialog_download, null);
                main_dialog = new Dialog(onfacebook.this, R.style.MyDialogTheme);
                main_dialog.setContentView(DialogView);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(main_dialog.getWindow().getAttributes());
                lp.width = (getResources().getDisplayMetrics().widthPixels);
                lp.height = (int)(getResources().getDisplayMetrics().heightPixels*0.65);
                main_dialog.getWindow().setAttributes(lp);
                streamButton = (Button) DialogView.findViewById(R.id.streamButton);
                downloadButton = (Button)DialogView.findViewById(R.id.downloadButton);
                cancelButton = (Button) DialogView.findViewById(R.id.cancelButton);
                // Set its video options.
                adNativeView = (NativeExpressAdView)DialogView.findViewById(R.id.nativeAD);
                adNativeView.setVideoOptions(new VideoOptions.Builder()
                        .setStartMuted(true)
                        .build());

                // The VideoController can be used to get lifecycle events and info about an ad's video
                // asset. One will always be returned by getVideoController, even if the ad has no video
                // asset.
                mVideoController = adNativeView.getVideoController();
                mVideoController.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                    @Override
                    public void onVideoEnd() {
                        Log.d(LOG_TAG, "Video playback is finished.");
                        super.onVideoEnd();
                    }
                });

                // Set an AdListener for the AdView, so the Activity can take action when an ad has finished
                // loading.
                adNativeView.setAdListener(new AdListener() {
                    @Override
                    public void onAdLoaded() {
                        if (mVideoController.hasVideoContent()) {
                            Log.d(LOG_TAG, "Received an ad that contains a video asset.");
                        } else {
                            Log.d(LOG_TAG, "Received an ad that does not contain a video asset.");
                        }
                    }
                });
                adNativeView.loadAd(new AdRequest.Builder().addTestDevice("0224C93FFD644350DCD7F3D7557C6A5C").build());
                main_dialog.setCancelable(false);
                main_dialog.setCanceledOnTouchOutside(false);
                main_dialog.show();
                streamButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(onfacebook.this, "Streaming", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(onfacebook.this, VideoPlayer.class);
                        intent.putExtra("video_url",urlString);
                        startActivity(intent);
                        main_dialog.dismiss();
                    }
                });
                downloadButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(onfacebook.this, "Downloading", Toast.LENGTH_SHORT).show();
                        newDownload(urlString);
                        main_dialog.dismiss();
                    }
                });
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(onfacebook.this,"1",Toast.LENGTH_SHORT).show();
                        main_dialog.setCancelable(true);
                        main_dialog.setCanceledOnTouchOutside(true);
                        main_dialog.cancel();
                        Toast.makeText(onfacebook.this,"22222222222",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void setValue(int progress) {
        this.progress.setProgress(progress);
        if (progress>=100) // code to be added
            this.progress.setVisibility(View.GONE);
    }

    private void createPopupDialog() {
    }

    public static boolean isConnectingToInternet(Context context) {

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }
    public void gotoFB(){
        webo = (WebView) findViewById(R.id.webViewFb);
        webo.clearCache(true);
        webo.clearHistory();
        webo.getSettings().setJavaScriptEnabled(true);
        webo.getSettings().setPluginState(WebSettings.PluginState.ON);
        webo.getSettings().setMediaPlaybackRequiresUserGesture(false);
        webo.setHorizontalScrollBarEnabled(false);
        WebSettings webSettings = webo.getSettings();
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setDomStorageEnabled(true);
        webo.addJavascriptInterface(this, "FBDownloader");
        webo.setWebViewClient(new WebViewClient() {
            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(url.contains("video.flhe2")){
                }
                if(url.startsWith("intent://")){
                    try {
                        @SuppressLint("WrongConstant") Intent furl = Intent.parseUri(url, 1);
                        if (getPackageManager().getLaunchIntentForPackage(furl.getPackage()) != null) {
                            startActivity(furl);
                            return true;
                        }
                        Intent intent = new Intent("android.intent.action.VIEW");
                        intent.setData(Uri.parse("market://details?id=" + furl.getPackage()));
                        startActivity(intent);
                        return true;
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }

                }else if (url.startsWith("market://")) {
                    try {
                        @SuppressLint("WrongConstant") Intent r1 = Intent.parseUri(url, 1);
                        if (r1 == null) {
                            return true;
                        }
                        startActivity(r1);
                        return true;
                    } catch (Throwable e22) {
                        e22.printStackTrace();
                        return true;
                    }
                }else{
                //    SearchText.setText(url);
                    webo.loadUrl(url);
                    WebviewLoaded=false;
                }
                return true;
            }

            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url=request.getUrl().toString();
                if(url.contains("video.flhe2")){
                }
                if(url.startsWith("intent://")){
                    try {
                        @SuppressLint("WrongConstant") Intent furl = Intent.parseUri(url, 1);
                        if (getPackageManager().getLaunchIntentForPackage(furl.getPackage()) != null) {
                            startActivity(furl);
                            return true;
                        }
                        Intent intent = new Intent("android.intent.action.VIEW");
                        intent.setData(Uri.parse("market://details?id=" + furl.getPackage()));
                        startActivity(intent);
                        return true;
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }

                }else if (url.startsWith("market://")) {
                    try {
                        @SuppressLint("WrongConstant") Intent r1 = Intent.parseUri(url, 1);
                        if (r1 == null) {
                            return true;
                        }
                        startActivity(r1);
                        return true;
                    } catch (Throwable e22) {
                        e22.printStackTrace();
                        return true;
                    }
                }else{
               //     SearchText.setText(url);
                    webo.loadUrl(url);
                    WebviewLoaded=false;}
                return true;
            }
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                progress.setVisibility(View.VISIBLE);
//                onfacebook.this.progress.setProgress(0);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                //  view.loadUrl("javascript:window.android.onUrlChange(window.location.href);");
                webo.loadUrl("javascript:(function() { var el = document.querySelectorAll('div[data-sigil]');for(var i=0;i<el.length; i++){var sigil = el[i].dataset.sigil;if(sigil.indexOf('inlineVideo') > -1){delete el[i].dataset.sigil;var jsonData = JSON.parse(el[i].dataset.store);el[i].setAttribute('onClick', 'FBDownloader.processVideo(\"'+jsonData['src']+'\");');}}})()");
//                onfacebook.this.progress.setProgress(100);
                super.onPageFinished(view, url);
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                if(webo.getUrl()!=null) {

                    if (webo.getUrl().contains("facebook.com")) {
                        webo.loadUrl("javascript:(function prepareVideo() { var el = document.querySelectorAll('div[data-sigil]');for(var i=0;i<el.length; i++){var sigil = el[i].dataset.sigil;if(sigil.indexOf('inlineVideo') > -1){delete el[i].dataset.sigil;console.log(i);var jsonData = JSON.parse(el[i].dataset.store);el[i].setAttribute('onClick', 'FBDownloader.processVideo(\"'+jsonData['src']+'\",\"'+jsonData['videoID']+'\");');}}})()");
                        webo.loadUrl("javascript:( window.onload=prepareVideo;)()");
                    }
                }
            }
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                progressBar.setVisibility(View.GONE);
                //  WebviewLoaded=true;
            }
        });
        webo.setWebChromeClient(new WebChromeClient() {});

        webo.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        UpdateUi();
                    }
                }, 1000);

                if (event.getPointerCount() > 1) {
                    //Multi touch detected
                    return true;
                }

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        // save the x
                        m_downX = event.getX();
                    }
                    break;

                    case MotionEvent.ACTION_MOVE:
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP: {
                        // set x so that it doesn't move
                        event.setLocation(m_downX, event.getY());
                    }
                    break;
                }

                return false;
            }
        });

        CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        CookieSyncManager.getInstance().startSync();
        webo.loadUrl(URL);
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    public class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context) {
            this.context = context;
        }
        private NumberProgressBar bnp;
        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                java.net.URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                int fileLength = connection.getContentLength();

                input = connection.getInputStream();
                fileN = "FbDownloader_" + UUID.randomUUID().toString().substring(0, 10) + ".mp4";
                File filename = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                        Constants.FOLDER_NAME, fileN);
                output = new FileOutputStream(filename);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    if (fileLength > 0)
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            LayoutInflater dialogLayout = LayoutInflater.from(onfacebook.this);
            View DialogView = dialogLayout.inflate(R.layout.progress_dialog, null);
            downloadDialog = new Dialog(onfacebook.this, R.style.CustomAlertDialog);
            downloadDialog.setContentView(DialogView);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(downloadDialog.getWindow().getAttributes());
            lp.width = (getResources().getDisplayMetrics().widthPixels);
            lp.height = (int)(getResources().getDisplayMetrics().heightPixels*0.65);
            downloadDialog.getWindow().setAttributes(lp);

            final Button cancel = (Button) DialogView.findViewById(R.id.cancel_btn);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //stopping the Asynctask
                    Log.d("Cancel","ok");
                    cancel(true);
                    downloadDialog.dismiss();

                }
            });
            largeNativeAd = (NativeExpressAdView) DialogView.findViewById(R.id.adView);
            largeNativeAd.loadAd(new AdRequest.Builder().addTestDevice("0224C93FFD644350DCD7F3D7557C6A5C").build());
            downloadDialog.setCancelable(false);
            downloadDialog.setCanceledOnTouchOutside(false);
            bnp = (NumberProgressBar)DialogView.findViewById(R.id.number_progress_bar);
            bnp.setProgress(0);
            bnp.setMax(100);
            downloadDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            bnp.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            downloadDialog.dismiss();
            if (result != null)
                Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();
            else
                Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();
            MediaScannerConnection.scanFile(onfacebook.this,
                    new String[]{Environment.getExternalStorageDirectory().getAbsolutePath() +
                            Constants.FOLDER_NAME + fileN}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String newpath, Uri newuri) {
                            Log.i("ExternalStorage", "Scanned " + newpath + ":");
                            Log.i("ExternalStorage", "-> uri=" + newuri);
                        }
                    });

        }
    }

    public void newDownload(String url) {
        final DownloadTask downloadTask = new DownloadTask(onfacebook.this);
        downloadTask.execute(url);
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean checkPermission() {
        checkFolder();
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(onfacebook.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(onfacebook.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(onfacebook.this);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("Write Storage permission is necessary to Download Images and Videos!!!");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(onfacebook.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions(onfacebook.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }
    public void checkAgain() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(onfacebook.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(onfacebook.this);
            alertBuilder.setCancelable(true);
            alertBuilder.setTitle("Permission necessary");
            alertBuilder.setMessage("Write Storage permission is necessary to Download Images and Videos!!!");
            alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(onfacebook.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
                }
            });
            AlertDialog alert = alertBuilder.create();
            alert.show();
        } else {
            ActivityCompat.requestPermissions(onfacebook.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkFolder();
                } else {
                    //code for deny
                    checkAgain();
                }
                break;
        }
    }

    public void checkFolder() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/FBDownloader/";
        File dir = new File(path);
        boolean isDirectoryCreated = dir.exists();
        if (!isDirectoryCreated) {
            isDirectoryCreated = dir.mkdir();
        }
        if (isDirectoryCreated) {
            // do something\
            Log.d("Folder", "Already Created");
        }
    }
    public void UpdateUi(){

        String WebUrl = webo.getUrl();
   //     String SearchUrl = SearchText.getText().toString();
        //  iUtils.ShowToast(MainActivity.this,WebUrl);
        /*if(!SearchHasFocus) {
//    if (!WebUrl.equals(SearchUrl)) {
            if(WebviewLoaded) {
                SearchText.setText(WebUrl);
            }
        }*/
    /*}
    @Override
    public void onResume(){
        super.onResume();
        webo.onResume();
        webo.resumeTimers();


    }
    @Override
    public void onPause(){
        super.onPause();
        // put your code here...
        webo.onPause();
        webo.pauseTimers();
    }
    @Override
    public void onStop(){
        super.onStop();

    }
    @Override
    protected void onDestroy() {
//        if (mAdView != null) {
//            mAdView.destroy();
//        }
        super.onDestroy();
        webo.loadUrl("about:blank");
        webo.stopLoading();
        webo.setWebChromeClient(null);
        webo.setWebViewClient(null);
        webo.destroy();
        webo=null;
    }
}*/
/*
import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.capitalize;

public class onfacebook extends AppCompatActivity {

    private static final String LOG_TAG = "EXAMPLE";
    @SuppressLint("StaticFieldLeak")
    private WebView webo;
    Toolbar mToolbar;
    private static String URL = "https://www.facebook.com/login/";
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private Button streamButton, downloadButton, cancelButton;
    private ProgressBar progress;
    private SwipeRefreshLayout recyclerLayout;
    ProgressDialog mProgressDialog;
    String fileN = null ;
    public static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 123;
    boolean result;
    String urlString;
    Drawer resultDrawer;
    AccountHeader headerResult;
    NativeExpressAdView adNativeView, largeNativeAd;
    VideoController mVideoController, largeMVideoController;
    Dialog main_dialog, downloadDialog;

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(Constant.theme);
        setContentView(R.layout.activity_onfacebook);

        if (getAppIntro(this)) {
            Intent i = new Intent(this, IntroActivity.class);
            startActivity(i);
        }
        progress = (ProgressBar) findViewById(R.id.progressBar);
        progress.setVisibility(View.GONE);
        result = checkPermission();
        if(result){
            checkFolder();
        }
        if (!isConnectingToInternet(this)) {
            Toast.makeText(this, "Please Connect to Internet", Toast.LENGTH_LONG).show();
        }
        gotoFB();
    }

    private boolean getAppIntro(onfacebook mainActivity) {
        SharedPreferences preferences;
        preferences = mainActivity.getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE);
        return preferences.getBoolean("AppIntro", true);
    }
    @JavascriptInterface
    public void processVideo(String str, String str2) {
        Log.e("WEBVIEWJS", "RUN");
        Log.e("WEBVIEWJS", str);
        Bundle args = new Bundle();
        args.putString("vid_data", str);
        urlString = str;
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                // put your code to show dialog here.
                LayoutInflater dialogLayout = LayoutInflater.from(onfacebook.this);
                View DialogView = dialogLayout.inflate(R.layout.dialog_download, null);
                main_dialog = new Dialog(onfacebook.this, R.style.MyDialogTheme);
                main_dialog.setContentView(DialogView);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(main_dialog.getWindow().getAttributes());
                lp.width = (getResources().getDisplayMetrics().widthPixels);
                lp.height = (int)(getResources().getDisplayMetrics().heightPixels*0.65);
                main_dialog.getWindow().setAttributes(lp);
                streamButton = (Button) DialogView.findViewById(R.id.streamButton);
                downloadButton = (Button)DialogView.findViewById(R.id.downloadButton);
                cancelButton = (Button) DialogView.findViewById(R.id.cancelButton);
                // Set its video options.
                adNativeView = (NativeExpressAdView)DialogView.findViewById(R.id.nativeAD);
                adNativeView.setVideoOptions(new VideoOptions.Builder()
                        .setStartMuted(true)
                        .build());

                // The VideoController can be used to get lifecycle events and info about an ad's video
                // asset. One will always be returned by getVideoController, even if the ad has no video
                // asset.
                mVideoController = adNativeView.getVideoController();
                mVideoController.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                    @Override
                    public void onVideoEnd() {
                        Log.d(LOG_TAG, "Video playback is finished.");
                        super.onVideoEnd();
                    }
                });

                // Set an AdListener for the AdView, so the Activity can take action when an ad has finished
                // loading.
                adNativeView.setAdListener(new AdListener() {
                    @Override
                    public void onAdLoaded() {
                        if (mVideoController.hasVideoContent()) {
                            Log.d(LOG_TAG, "Received an ad that contains a video asset.");
                        } else {
                            Log.d(LOG_TAG, "Received an ad that does not contain a video asset.");
                        }
                    }
                });
                adNativeView.loadAd(new AdRequest.Builder().addTestDevice("0224C93FFD644350DCD7F3D7557C6A5C").build());
                main_dialog.setCancelable(false);
                main_dialog.setCanceledOnTouchOutside(false);
                main_dialog.show();
                streamButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(onfacebook.this, "Streaming", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(onfacebook.this, VideoPlayer.class);
                        intent.putExtra("video_url",urlString);
                        startActivity(intent);
                        main_dialog.dismiss();
                    }
                });
                downloadButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(onfacebook.this, "Downloading", Toast.LENGTH_SHORT).show();
                        newDownload(urlString);
                        main_dialog.dismiss();
                    }
                });
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(onfacebook.this,"1",Toast.LENGTH_SHORT).show();
                        main_dialog.setCancelable(true);
                        main_dialog.setCanceledOnTouchOutside(true);
                        main_dialog.cancel();
                        Toast.makeText(onfacebook.this,"22222222222",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void setValue(int progress) {
        this.progress.setProgress(progress);
        if (progress>=100) // code to be added
            this.progress.setVisibility(View.GONE);
    }

    private void createPopupDialog() {
    }

    public static boolean isConnectingToInternet(Context context) {

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }
    public void gotoFB(){
        webo = (WebView) findViewById(R.id.webViewFb);
        webo.clearCache(true);
        webo.clearHistory();
        webo.getSettings().setJavaScriptEnabled(true);
        webo.getSettings().setPluginState(WebSettings.PluginState.ON);
        webo.getSettings().setMediaPlaybackRequiresUserGesture(false);
        webo.setHorizontalScrollBarEnabled(false);
        WebSettings webSettings = webo.getSettings();
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setDomStorageEnabled(true);
        webo.addJavascriptInterface(this, "FBDownloader");
        webo.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                progress.setVisibility(View.VISIBLE);
//                onfacebook.this.progress.setProgress(0);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                //  view.loadUrl("javascript:window.android.onUrlChange(window.location.href);");
                webo.loadUrl("javascript:(function() { var el = document.querySelectorAll('div[data-sigil]');for(var i=0;i<el.length; i++){var sigil = el[i].dataset.sigil;if(sigil.indexOf('inlineVideo') > -1){delete el[i].dataset.sigil;var jsonData = JSON.parse(el[i].dataset.store);el[i].setAttribute('onClick', 'FBDownloader.processVideo(\"'+jsonData['src']+'\");');}}})()");
//                onfacebook.this.progress.setProgress(100);
                super.onPageFinished(view, url);
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                if(webo.getUrl()!=null) {

                    if (webo.getUrl().contains("facebook.com")) {
                        webo.loadUrl("javascript:(function prepareVideo() { var el = document.querySelectorAll('div[data-sigil]');for(var i=0;i<el.length; i++){var sigil = el[i].dataset.sigil;if(sigil.indexOf('inlineVideo') > -1){delete el[i].dataset.sigil;console.log(i);var jsonData = JSON.parse(el[i].dataset.store);el[i].setAttribute('onClick', 'FBDownloader.processVideo(\"'+jsonData['src']+'\",\"'+jsonData['videoID']+'\");');}}})()");
                        webo.loadUrl("javascript:( window.onload=prepareVideo;)()");
                    }
                }
            }
        });
        CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        CookieSyncManager.getInstance().startSync();
        webo.loadUrl(URL);
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    public class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context) {
            this.context = context;
        }
        private NumberProgressBar bnp;
        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                java.net.URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                int fileLength = connection.getContentLength();

                input = connection.getInputStream();
                fileN = "FbDownloader_" + UUID.randomUUID().toString().substring(0, 10) + ".mp4";
                File filename = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                        Constants.FOLDER_NAME, fileN);
                output = new FileOutputStream(filename);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    if (fileLength > 0)
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            LayoutInflater dialogLayout = LayoutInflater.from(onfacebook.this);
            View DialogView = dialogLayout.inflate(R.layout.progress_dialog, null);
            downloadDialog = new Dialog(onfacebook.this, R.style.CustomAlertDialog);
            downloadDialog.setContentView(DialogView);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(downloadDialog.getWindow().getAttributes());
            lp.width = (getResources().getDisplayMetrics().widthPixels);
            lp.height = (int)(getResources().getDisplayMetrics().heightPixels*0.65);
            downloadDialog.getWindow().setAttributes(lp);

            final Button cancel = (Button) DialogView.findViewById(R.id.cancel_btn);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //stopping the Asynctask
                    Log.d("Cancel","ok");
                    cancel(true);
                    downloadDialog.dismiss();

                }
            });
            largeNativeAd = (NativeExpressAdView) DialogView.findViewById(R.id.adView);
            largeNativeAd.loadAd(new AdRequest.Builder().addTestDevice("0224C93FFD644350DCD7F3D7557C6A5C").build());
            downloadDialog.setCancelable(false);
            downloadDialog.setCanceledOnTouchOutside(false);
            bnp = (NumberProgressBar)DialogView.findViewById(R.id.number_progress_bar);
            bnp.setProgress(0);
            bnp.setMax(100);
            downloadDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            bnp.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            downloadDialog.dismiss();
            if (result != null)
                Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();
            else
                Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();
            MediaScannerConnection.scanFile(onfacebook.this,
                    new String[]{Environment.getExternalStorageDirectory().getAbsolutePath() +
                            Constants.FOLDER_NAME + fileN}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String newpath, Uri newuri) {
                            Log.i("ExternalStorage", "Scanned " + newpath + ":");
                            Log.i("ExternalStorage", "-> uri=" + newuri);
                        }
                    });

        }
    }

    public void newDownload(String url) {
        final DownloadTask downloadTask = new DownloadTask(onfacebook.this);
        downloadTask.execute(url);
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean checkPermission() {
        checkFolder();
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(onfacebook.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(onfacebook.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(onfacebook.this);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("Write Storage permission is necessary to Download Images and Videos!!!");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(onfacebook.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions(onfacebook.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }
    public void checkAgain() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(onfacebook.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(onfacebook.this);
            alertBuilder.setCancelable(true);
            alertBuilder.setTitle("Permission necessary");
            alertBuilder.setMessage("Write Storage permission is necessary to Download Images and Videos!!!");
            alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(onfacebook.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
                }
            });
            AlertDialog alert = alertBuilder.create();
            alert.show();
        } else {
            ActivityCompat.requestPermissions(onfacebook.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkFolder();
                } else {
                    //code for deny
                    checkAgain();
                }
                break;
        }
    }

    public void checkFolder() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/FBDownloader/";
        File dir = new File(path);
        boolean isDirectoryCreated = dir.exists();
        if (!isDirectoryCreated) {
            isDirectoryCreated = dir.mkdir();
        }
        if (isDirectoryCreated) {
            // do something\
            Log.d("Folder", "Already Created");
        }
    }
}*/