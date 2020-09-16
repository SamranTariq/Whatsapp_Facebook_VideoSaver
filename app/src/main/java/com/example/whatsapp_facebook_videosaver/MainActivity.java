package com.example.whatsapp_facebook_videosaver;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.whatsapp_facebook_videosaver.ui.main.SectionsPagerAdapter;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "";
    /** variable **/
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermission();
        setContentView(R.layout.activity_main);
        /********* Hooks *********/
        Toolbar toolbar=findViewById(R.id.tool);
        drawerLayout=findViewById(R.id.main);
        navigationView=findViewById(R.id.nav_drawer_view);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = findViewById(R.id.fab);
        /********* Toolbar *******/
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        /********* Navigation menu(Drawer) *******/
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open_navigation,R.string.close_navigation);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setHomeButtonEnabled(true);     // For Custom Icon of Navigation Button & it's necessary to write this code after the "toggle.syncState();" otherwise desire icon not display.**/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // For Custom Icon of Navigation Button **/
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menutwo);  // For Custom Icon of Navigation Button **/
        navigationView.setNavigationItemSelectedListener(this);
        //navigationView.setCheckedItem(R.id.home_nav1);   // Check Home By Default **/
    }

    /************  On Back_pressed  Close NavigationDrawer First if open then close app *******/
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.home_nav2:
                Intent intent=new Intent(MainActivity.this,AllStatus.class);
                startActivity(intent);
                break;
            case R.id.home_nav3:
                Intent intentimg=new Intent(MainActivity.this,Images.class);
                startActivity(intentimg);
                break;
            case R.id.home_nav4:
                Intent intentvideo=new Intent(MainActivity.this,Videos.class);
                startActivity(intentvideo);
                break;
            case R.id.home_nav5:
                Intent intentgallery=new Intent(MainActivity.this,Gallery.class);
                startActivity(intentgallery);
                break;
            case R.id.home_nav9:
                Toast.makeText(this,"done",Toast.LENGTH_LONG).show();
                break;
            case R.id.facebookvideo:
                Toast.makeText(this,"video",Toast.LENGTH_LONG).show();
                break;
            case R.id.facebookgallery:
                Toast.makeText(this,"gallery",Toast.LENGTH_LONG).show();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    /*************  Option Navigation Right Side of the toolbar  ************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.optionmenu,menu);
        return true;
    }
    public void checkPermission(){
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(MainActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }
}
