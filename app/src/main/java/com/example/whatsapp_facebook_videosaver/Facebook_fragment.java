package com.example.whatsapp_facebook_videosaver;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

public class Facebook_fragment extends Fragment {
    /** variable **/
    ImageView imageViewall,imageViewimage;
    public static ImageView downloadericon;
    public static EditText link;
    public static TextView downlaod,onfb;
    public static ProgressBar progressBar;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.facebook_fragment, container, false);
        /********* Hooks *********/
        imageViewall = view.findViewById(R.id.imageViewall);
        imageViewimage = view.findViewById(R.id.imageViewimages);
        link = view.findViewById(R.id.editTextvideourl);
        downlaod = view.findViewById(R.id.textViewlinkdownload);
        downloadericon = view.findViewById(R.id.imageviewdownload);
        progressBar = view.findViewById(R.id.progress);
        onfb = view.findViewById(R.id.textView7);
        /************ on image click on cardview **************/
        imageViewall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar snackbar1=null;
                snackbar1.make(v,"Place Your Action Here",Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                }).show();
            }
        });
        imageViewimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar snackbar1=null;
                snackbar1.make(v,"Place Your Action Here",Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                }).show();
            }
        });
        /************ on Downlaod **************/
        downlaod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String URL = link.getText().toString();
                FbVideoDownloader downloader = new FbVideoDownloader(getActivity(),URL);
                downloader.DownloadVideo();
                downlaod.setVisibility(View.INVISIBLE);
                downloadericon.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
            }
        });
        /*******onfb***********/
        onfb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),onfacebook.class);
                getActivity().startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        Log.d("re","resume");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d("re","pause");
        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d("re","back");
        super.onActivityResult(requestCode, resultCode, data);
    }

}