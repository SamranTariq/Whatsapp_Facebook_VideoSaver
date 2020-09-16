package com.example.whatsapp_facebook_videosaver;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

/**
 * Created by Tushar on 9/10/2017.
 */

public class IntroActivity extends AppIntro {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(AppIntroFragment.newInstance("STEP 1", "Tap to open Facebook.", R.drawable.intro1, Color.parseColor("#3b5998")));
        addSlide(AppIntroFragment.newInstance("STEP 2", "Tap on Video to Download and Stream", R.drawable.intro2, Color.parseColor("#4caf50")));
        showSkipButton(false);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {

    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        SharedPreferences.Editor editor = getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE).edit();
        editor.putBoolean("AppIntro", false);
        editor.commit();
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
    }
}
