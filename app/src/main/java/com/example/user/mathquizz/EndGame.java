package com.example.user.mathquizz;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

/**
 * Created by User on 3/5/2018.
 */

public class EndGame extends Activity implements View.OnClickListener{
    private static final String TAG = "tag";

//    private static final int AUTHENTICATE = 1;

    private TextView yourScore, bestScore;
    private Button restartBnt, shareBnt, homeBnt;
    private SharedPreferences volumePrefs, yourScorePrefs, highScorePrefs;

    Twitter twitter = TwitterFactory.getSingleton();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_end);

        yourScore = findViewById(R.id.your_text);
        bestScore = findViewById(R.id.best_text);

        restartBnt = findViewById(R.id.restart_btn);
        shareBnt = findViewById(R.id.share_btn);
        homeBnt = findViewById(R.id.home_btn);

        restartBnt.setOnClickListener(this);
        shareBnt.setOnClickListener(this);
        homeBnt.setOnClickListener(this);

        MusicManager.createSfx(this);


        volumePrefs = getSharedPreferences("volume", 0);
        if (volumePrefs.contains("volume")){
            MusicManager.setSfxVolume(volumePrefs.getInt("volume", 0), volumePrefs.getInt("volume", 0));
        }

        yourScorePrefs = getSharedPreferences(PlayGame.YOUR_SCORE_PREFS, 0);
        highScorePrefs = getSharedPreferences(PlayGame.HIGH_SCORE_PREFS, 0);

        yourScore.setText(yourScorePrefs.getString("yourScore", ""));
        if(highScorePrefs.getString("highScores", "").length() > 0) {
            bestScore.setText(((highScorePrefs.getString("highScores", "").split("\\|"))[0].split(" - "))[1]);
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.restart_btn){
            MusicManager.startUI();
            Intent restartIntent = new Intent(this, PlayGame.class);
            this.startActivity(restartIntent);
            finish();
        }
        else if(view.getId() == R.id.share_btn){
            MusicManager.startUI();
            String tweetUrl = String.format("https://twitter.com/intent/tweet?text=%s&url=%s",
                    urlEncode("I got " + yourScorePrefs.getString("yourScore", "") + " scores while playing math quiz in " +
                            "@MathQuizz. Check out on GG Play: "),
                    urlEncode("https://www.google.fi/"));
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl));


// Narrow down to official Twitter app, if available:
            List<ResolveInfo> matches = getPackageManager().queryIntentActivities(intent, 0);
            for (ResolveInfo info : matches) {
                if (info.activityInfo.packageName.toLowerCase().startsWith("com.twitter")) {
                    intent.setPackage(info.activityInfo.packageName);
                }
            }

            startActivity(intent);
        }
        else if(view.getId() == R.id.home_btn){
            MusicManager.startUI();
            Intent homeIntent = new Intent(this, MainActivity.class);
            this.startActivity(homeIntent);
            finish();
        }
    }

    public static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            Log.wtf(TAG, "UTF-8 should always be supported", e);
            throw new RuntimeException("URLEncoder.encode() failed for " + s);
        }
    }

}
