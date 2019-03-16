package com.example.user.mathquizz;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Collection;
import java.util.HashMap;

/**
 * Created by User on 5/5/2018.
 */

public class MusicManager {

    private static MediaPlayer backgroundPlayer, uiPlayer, correctPlayer, wrongPlayer, changePlayer;
    private static SharedPreferences musicStatePrefs;
    public static boolean backgroundMusicIsOn = false;
    public static boolean backgroundMusicIsCurrentlyPlayed;

    public static void startBackgroundMusic(Context context){
        backgroundPlayer = MediaPlayer.create(context, R.raw.background);
        backgroundPlayer.setLooping(true);
        backgroundPlayer.setVolume(100,100);
        backgroundPlayer.start();
    }

    public static void stopBackgroundMusic(){
        backgroundPlayer.stop();
    }

    public static void setBackgroundMusicIsOn(boolean b){
        backgroundMusicIsOn = b;
    }

    public static void createSfx(Context context){
        uiPlayer = MediaPlayer.create(context, R.raw.ui);
        correctPlayer = MediaPlayer.create(context, R.raw.correct);
        wrongPlayer = MediaPlayer.create(context, R.raw.wrong);
        changePlayer = MediaPlayer.create(context, R.raw.change);
    }

    public static void startUI(){
        uiPlayer.start();
    }

    public static void startCorrect(){
        correctPlayer.start();
    }

    public static void startWrong(){

        wrongPlayer.start();
    }

    public static void startChange(){

        changePlayer.start();
    }

    public static void setSfxVolume(int l, int r){
        uiPlayer.setVolume(l, r);
        correctPlayer.setVolume(l, r);
        wrongPlayer.setVolume(l, r);
        changePlayer.setVolume(l, r);
    }
}
