package com.example.user.mathquizz;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by User on 29/4/2018.
 */

public class HighScores extends Activity implements View.OnClickListener{

    private Button backBtn;
    private SharedPreferences volumePrefs;
    private ScoreDatabase scoreDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_high);


        backBtn = findViewById(R.id.back_btn);

        scoreDatabase = new ScoreDatabase(this);

        backBtn.setOnClickListener(this);

        MusicManager.createSfx(this);


        SharedPreferences scorePrefs = getSharedPreferences(PlayGame.HIGH_SCORE_PREFS, 0);
        volumePrefs = getSharedPreferences("volume", 0);
        if (volumePrefs.contains("volume")){
            MusicManager.setSfxVolume(volumePrefs.getInt("volume", 0), volumePrefs.getInt("volume", 0));
        }

        TextView scoreView = findViewById(R.id.high_scores_list);

//        String[] savedScores = scorePrefs.getString("highScores", "").split("\\|");
//
//        StringBuilder scoreBuild = new StringBuilder("");
//        for(String score : savedScores){
//            scoreBuild.append(score+"\n");
//        }
//
//        scoreView.setText(scoreBuild.toString());

        ArrayList<String> theList = new ArrayList<>();
        Cursor data = scoreDatabase.getListContents();
        if (data.getCount() == 0){
            System.out.println("empty");
        }
        else {
            while (data.moveToNext()){
                theList.add(data.getString(1) + " - " + data.getString(2));
                System.out.println(data.getString(1) + " - " + data.getString(2));
            }
        }
        StringBuilder scoreBuild = new StringBuilder("");
        for(String score : theList){
            scoreBuild.append(score+"\n");
        }
        scoreView.setText(scoreBuild.toString());
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.back_btn){
            MusicManager.startUI();
            Intent backIntent = new Intent(this, MainActivity.class);
            this.startActivity(backIntent);
            finish();
        }
    }
}
