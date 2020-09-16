package com.example.whatsapp_facebook_videosaver;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

public class Whatsapp_fragment extends Fragment {
    /** variable **/
    ImageView imageViewall,imageViewimage,imageViewvideo,imageViewgallery;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.whatsapp_fragment, container, false);
        /********* Hooks *********/
        imageViewall = view.findViewById(R.id.imageViewall);
        imageViewimage = view.findViewById(R.id.imageViewimages);
        imageViewvideo = view.findViewById(R.id.imageViewvideo);
        imageViewgallery = view.findViewById(R.id.imageViewgallery);
        /************ on image click on cardview **************/
        imageViewall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),AllStatus.class);
                startActivity(intent);
            }
        });
        imageViewimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),Images.class);
                startActivity(intent);
            }
        });
        imageViewvideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),Videos.class);
                startActivity(intent);
            }
        });
        imageViewgallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),Gallery.class);
                startActivity(intent);
            }
        });
        return view;
    }
}