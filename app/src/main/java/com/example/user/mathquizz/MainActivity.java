package com.example.user.mathquizz;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.SoundPool;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GestureDetector.OnGestureListener {

    private Button playBtn, settingBtn, highBtn;

    private String[] levelNames = {"Easy", "Medium", "Hard"};

    private int ui;

    private SharedPreferences musicStatePrefs, volumePrefs;

    private GestureDetector gestureDetector;

    private View view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        gestureDetector = new GestureDetector(this, this);

        view = findViewById(R.id.main);
        view.setOnTouchListener(new OnSwipeTouchListener(this){
            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                Intent settingIntent = new Intent(MainActivity.this, Setting.class);
                MainActivity.this.startActivity(settingIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });

        playBtn = findViewById(R.id.play_btn);
        settingBtn = findViewById(R.id.setting_btn);
        highBtn = findViewById(R.id.high_btn);

        playBtn.setOnClickListener(this);
        settingBtn.setOnClickListener(this);
        highBtn.setOnClickListener(this);

        MusicManager.createSfx(this);

        musicStatePrefs = getSharedPreferences("musicState", 0);
        volumePrefs = getSharedPreferences("volume", 0);
        if (volumePrefs.contains("volume")){
            MusicManager.setSfxVolume(volumePrefs.getInt("volume", 0), volumePrefs.getInt("volume", 0));
        }
//        System.out.println("music manager: " + MusicManager.backgroundMusicIsCurrentlyPlayed);
//        System.out.println("main act: " + musicStatePrefs.getBoolean("musicToggle", true));

        MusicManager.setBackgroundMusicIsOn(musicStatePrefs.getBoolean("musicToggle", true));
        if(MusicManager.backgroundMusicIsOn && !MusicManager.backgroundMusicIsCurrentlyPlayed){
            MusicManager.startBackgroundMusic(this);
            MusicManager.backgroundMusicIsCurrentlyPlayed = true;
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.play_btn){
            MusicManager.startUI();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Choose a level")
                    .setSingleChoiceItems(levelNames, 0, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            //start gameplay
                            startPlay(which);
                        }
                    });
            AlertDialog ad = builder.create();
            ad.show();
        }
        else if(view.getId() == R.id.setting_btn){
            MusicManager.startUI();
            Intent settingIntent = new Intent(this, Setting.class);
            this.startActivity(settingIntent);
            finish();
        }
        else if(view.getId() == R.id.high_btn){
            MusicManager.startUI();
            Intent highIntent = new Intent(this, HighScores.class);
            this.startActivity(highIntent);
            finish();
        }
    }

    private void startPlay(int chosenLevel)
    {
        MusicManager.startUI();
        Intent playIntent = new Intent(this, PlayGame.class);
        playIntent.putExtra("level", chosenLevel);
        this.startActivity(playIntent);
        finish();
    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        if(motionEvent.getX() - motionEvent1.getX() > 50){
            Toast.makeText(this, "Swipe Left", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (motionEvent1.getX() - motionEvent.getX() > 50){
            Toast.makeText(this, "Swipe Right", Toast.LENGTH_SHORT).show();
            return true;
        }
        else {
            return true;
        }
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }
}
