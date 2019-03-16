package com.example.user.mathquizz;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

/**
 * Created by User on 3/5/2018.
 */

public class Setting extends Activity implements View.OnClickListener{

    private Button backBtn, resetBtn;
    private ToggleButton musicBtn, sfxBtn;

    private boolean musicChecked = true, sfxChecked = true, backgroundMusicIsOn = true;
    private SharedPreferences musicCheckedPrefs, sfxCheckedPrefs, musicStatePrefs, volumePrefs;

    private int volume = 100;

    private View view;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_setting);

        view = findViewById(R.id.setting);
        view.setOnTouchListener(new OnSwipeTouchListener(this){
            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                musicStatePrefs.edit().putBoolean("musicToggle", backgroundMusicIsOn).apply();
                Intent backIntent = new Intent(Setting.this, MainActivity.class);
                Setting.this.startActivity(backIntent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
        });

        backBtn = findViewById(R.id.back_btn);
        resetBtn = findViewById(R.id.reset_btn);
        musicBtn = findViewById(R.id.music_btn);
        sfxBtn = findViewById(R.id.sfx_btn);

        backBtn.setOnClickListener(this);
        resetBtn.setOnClickListener(this);

        MusicManager.createSfx(this);

        musicCheckedPrefs = getSharedPreferences("musicChecked", 0);
        sfxCheckedPrefs = getSharedPreferences("sfxChecked", 0);
        musicStatePrefs = getSharedPreferences("musicState", 0);
        volumePrefs = getSharedPreferences("volume", 0);

        if(musicCheckedPrefs.contains("music") || sfxCheckedPrefs.contains("sfx")) {

            musicBtn.setChecked(musicCheckedPrefs.getBoolean("music", true));
            musicChecked = musicCheckedPrefs.getBoolean("music", true);
            sfxBtn.setChecked(sfxCheckedPrefs.getBoolean("sfx", true));
            sfxChecked = sfxCheckedPrefs.getBoolean("sfx", true);
        }
        else {
            musicBtn.setChecked(true);
            sfxBtn.setChecked(true);
        }

        if (musicStatePrefs.contains("musicToggle")){
            backgroundMusicIsOn = musicStatePrefs.getBoolean("musicToggle", true);
        }

        if (volumePrefs.contains("volume")){
            volume = volumePrefs.getInt("volume", 0);
//            System.out.println("vol pref set"+ volume);
            MusicManager.setSfxVolume(volumePrefs.getInt("volume", 0), volumePrefs.getInt("volume", 0));
        }

        musicBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    MusicManager.startUI();
                    musicChecked = true;
                    backgroundMusicIsOn = true;
                    musicCheckedPrefs.edit().putBoolean("music", musicChecked).apply();
                    musicStatePrefs.edit().putBoolean("musicToggle", backgroundMusicIsOn).apply();
                    MusicManager.setBackgroundMusicIsOn(true);
                    MusicManager.backgroundMusicIsCurrentlyPlayed = true;
                    MusicManager.startBackgroundMusic(getBaseContext());
                }
                else {
                    MusicManager.startUI();
                    musicChecked = false;
                    backgroundMusicIsOn = false;
                    musicCheckedPrefs.edit().putBoolean("music", musicChecked).apply();
                    musicStatePrefs.edit().putBoolean("musicToggle", backgroundMusicIsOn).apply();
                    MusicManager.setBackgroundMusicIsOn(false);
                    MusicManager.backgroundMusicIsCurrentlyPlayed = false;
                    MusicManager.stopBackgroundMusic();
                }
            }
        });



        sfxBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    MusicManager.createSfx(getBaseContext());
                    MusicManager.startUI();
                    sfxChecked = true;
                    volume = 100;
                    MusicManager.setSfxVolume(volume, volume);
                    sfxCheckedPrefs.edit().putBoolean("sfx", sfxChecked).apply();
                    volumePrefs.edit().putInt("volume", volume).apply();
//                    System.out.println("sfx check");
                }
                else {
                    MusicManager.createSfx(getBaseContext());
                    MusicManager.startUI();
                    sfxChecked = false;
                    volume = 0;
                    MusicManager.setSfxVolume(volume, volume);
                    sfxCheckedPrefs.edit().putBoolean("sfx", sfxChecked).apply();
                    volumePrefs.edit().putInt("volume", volume).apply();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        musicCheckedPrefs.edit().putBoolean("music", musicChecked).apply();
//        System.out.println("destroy music" + musicCheckedPrefs.getBoolean("music", true));
        sfxCheckedPrefs.edit().putBoolean("sfx", sfxChecked).apply();
//        System.out.println("destroy sfx" + sfxCheckedPrefs.getBoolean("music", true));
        volumePrefs.edit().putInt("volume", volume).apply();
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.back_btn){
            MusicManager.startUI();
//            System.out.println("setting act: " + backgroundMusicIsOn);
            musicStatePrefs.edit().putBoolean("musicToggle", backgroundMusicIsOn).apply();
            Intent backIntent = new Intent(this, MainActivity.class);
            this.startActivity(backIntent);
//            System.out.println("volume: " + volumePrefs.getInt("volume", 0));
            finish();
        }
        else if (view.getId() == R.id.reset_btn){
            musicChecked = true;
            sfxChecked = true;
            musicBtn.setChecked(musicChecked);
            sfxBtn.setChecked(sfxChecked);
//            MusicManager.setSfxVolume(100, 100);
//            volumePrefs.edit().putInt("volume", 100).apply();
            MusicManager.startUI();


//            backgroundMusicIsOn = true;
//            MusicManager.setBackgroundMusicIsOn(true);
//            MusicManager.backgroundMusicIsCurrentlyPlayed = true;

//            System.out.println("volume: " + volumePrefs.getInt("volume", 0));



        }
    }
}
