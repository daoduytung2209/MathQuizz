package com.example.user.mathquizz;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by User on 29/4/2018.
 */

public class PlayGame extends Activity implements View.OnClickListener {

    private final int ADD_OPERATOR = 0, SUBTRACT_OPERATOR = 1, MULTIPLY_OPERATOR = 2, DIVIDE_OPERATOR = 3;

    public static final String HIGH_SCORE_PREFS = "ArithmeticFile1";
    public static final String YOUR_SCORE_PREFS = "ArithmeticFile2";

    private int level = 0, answer = 0, wrongAnswer = 0, operator = 0, operand1 = 0, operand2 = 0, randQuestion, i = 5;
    private int chances = 3;

    private boolean hasWrongAnswer = false;
    private String[] operators = {"+", "-", "x", "/"};
    private Random random;
    private SharedPreferences highScorePrefs, yourScorePrefs, volumePrefs;
    private ScoreDatabase scoreDatabase;

    private ShakeListener mShaker;

    private int[][] levelMin = {
            {1, 11, 21},
            {1, 5, 10},
            {2, 5, 10},
            {2, 3, 5}};
    private int[][] levelMax = {
            {10, 25, 50},
            {10, 20, 30},
            {5, 10, 15},
            {10, 50, 100}};

    private TextView question, scoreText, bestText, firstChance, secondChance, thirdChance;
    private ImageButton tick, cross;
    private ProgressBar timerBar;
    private ObjectAnimator animation;


//    private ImageView response;
//    private Button btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btn0, enterBtn, clearBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_playgame);
        highScorePrefs = getSharedPreferences(HIGH_SCORE_PREFS, 0);
        yourScorePrefs = getSharedPreferences(YOUR_SCORE_PREFS, 0);

        question =  findViewById(R.id.question);
        scoreText = findViewById(R.id.score_text);
        bestText = findViewById(R.id.best_text);
        firstChance = findViewById(R.id.first_chance);
        secondChance = findViewById(R.id.second_chance);
        thirdChance = findViewById(R.id.third_chance);

        tick = findViewById(R.id.tick);
        cross = findViewById(R.id.cross);

        scoreDatabase = new ScoreDatabase(this);

        MusicManager.createSfx(this);

        tick.setOnClickListener(this);
        cross.setOnClickListener(this);

        clearYourScores();

        mShaker = new ShakeListener(this);
        mShaker.setOnShakeListener(new ShakeListener.OnShakeListener () {
            public void onShake()
            {
                if(chances > 0) {
                    MusicManager.startChange();
                    chances--;
                    if(chances == 2){
                        firstChance.setCompoundDrawables(null,null,null,null);
                    }
                    else if (chances == 1){
                        secondChance.setCompoundDrawables(null,null,null,null);
                    }
                    else if(chances == 0){
                        thirdChance.setCompoundDrawables(null,null,null,null);
                    }
                    animation.start();
                    updateBestScore();
                    chooseQuestion();
                }
            }
        });

        if(highScorePrefs.getString("highScores", "").length() > 0) {
            bestText.setText(((highScorePrefs.getString("highScores", "").split("\\|"))[0].split(" - "))[1]);
        }

        volumePrefs = getSharedPreferences("volume", 0);
        if (volumePrefs.contains("volume")){
            MusicManager.setSfxVolume(volumePrefs.getInt("volume", 0), volumePrefs.getInt("volume", 0));
        }

        timerBar = findViewById(R.id.timerBar);
        timerBar.setScaleY(3f);
        animation = ObjectAnimator.ofInt(timerBar, "progress", 100, 0);
        animation.setDuration(3000);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) { }

            @Override
            public void onAnimationEnd(Animator animator) {
                if(!hasWrongAnswer) {
                    MusicManager.startWrong();
                    hasWrongAnswer = true;
                    setYourScore();
                    setHighScore();
                    Intent endIntent = new Intent(getBaseContext(), EndGame.class);
                    startActivity(endIntent);
                    finish();
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) { }
        });
        animation.start();

//        bestText.setText("" + scoreStrings.get(-1).getScoreNum());
//        answerTxt = findViewById(R.id.answer);
//        response =  findViewById(R.id.response);
//        scoreTxt =  findViewById(R.id.score);

//        btn1 = findViewById(R.id.btn1);
//        btn2 = findViewById(R.id.btn2);
//        btn3 = findViewById(R.id.btn3);
//        btn4 = findViewById(R.id.btn4);
//        btn5 = findViewById(R.id.btn5);
//        btn6 = findViewById(R.id.btn6);
//        btn7 = findViewById(R.id.btn7);
//        btn8 = findViewById(R.id.btn8);
//        btn9 = findViewById(R.id.btn9);
//        btn0 = findViewById(R.id.btn0);
//        enterBtn = findViewById(R.id.enter);
//        clearBtn = findViewById(R.id.clear);

//        response.setVisibility(View.INVISIBLE);
//
//        btn1.setOnClickListener(this);
//        btn2.setOnClickListener(this);
//        btn3.setOnClickListener(this);
//        btn4.setOnClickListener(this);
//        btn5.setOnClickListener(this);
//        btn6.setOnClickListener(this);
//        btn7.setOnClickListener(this);
//        btn8.setOnClickListener(this);
//        btn9.setOnClickListener(this);
//        btn0.setOnClickListener(this);
//        enterBtn.setOnClickListener(this);
//        clearBtn.setOnClickListener(this);

        if(savedInstanceState!=null){
            level=savedInstanceState.getInt("level");
            int exScore = savedInstanceState.getInt("score");
//            scoreText.setText("Score: "+exScore);
            scoreText.setText("" + exScore);

        }
        else{
            Bundle extras = getIntent().getExtras();
            if(extras !=null)
            {
                int passedLevel = extras.getInt("level", -1);
                if(passedLevel>=0) level = passedLevel;
            }
        }

        random = new Random();
        chooseQuestion();
    }

    protected void onDestroy(){
        if(!hasWrongAnswer) {
            setHighScore();
        }
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        int exScore = getScore();
        savedInstanceState.putInt("score", exScore);
        savedInstanceState.putInt("level", level);
        super.onSaveInstanceState(savedInstanceState);
    }

    private void chooseQuestion(){
//        answerTxt.setText("= ?");
        randQuestion = random.nextInt(2);

        operator = random.nextInt(operators.length);

        operand1 = getOperand();
        operand2 = getOperand();

        if(operator == SUBTRACT_OPERATOR){
            while(operand2 > operand1){
                operand1 = getOperand();
                operand2 = getOperand();
            }
        }
        else if(operator==DIVIDE_OPERATOR){
            while((((double)operand1/(double)operand2)%1 > 0) || (operand1==operand2))
            {
                operand1 = getOperand();
                operand2 = getOperand();
            }
        }

        switch(operator)
        {
            case ADD_OPERATOR:
                answer = operand1 + operand2;
                break;
            case SUBTRACT_OPERATOR:
                answer = operand1 - operand2;
                break;
            case MULTIPLY_OPERATOR:
                answer = operand1 * operand2;
                break;
            case DIVIDE_OPERATOR:
                answer = operand1 / operand2;
                break;
            default:
                break;
        }
        if (randQuestion == 0) {
            wrongAnswer = getWrongAnswer();
            question.setText(operand1 + " " + operators[operator] + " " + operand2 + "\n= " + wrongAnswer);
        }
        else if (randQuestion == 1){
            question.setText(operand1 + " " + operators[operator] + " " + operand2 + "\n= " + answer);
        }
    }

    private int getOperand(){
        return random.nextInt(levelMax[operator][level] - levelMin[operator][level] + 1)
                + levelMin[operator][level];
    }

    @Override
    public void onClick(View view) {
//        if(view.getId()==R.id.enter){
//            String answerContent = answerTxt.getText().toString();
//            if(!answerContent.endsWith("?"))
//            {
//                int enteredAnswer = Integer.parseInt(answerContent.substring(2));
//                int exScore = getScore();
//                if(enteredAnswer == answer){
//                    scoreTxt.setText("Score: "+(exScore+1));
//                    response.setImageResource(R.drawable.tick);
//                    response.setVisibility(View.VISIBLE);
//                }
//                else{
//                    setHighScore();
//                    scoreTxt.setText("Score: 0");
//                    response.setImageResource(R.drawable.cross);
//                    response.setVisibility(View.VISIBLE);
//                }
//                chooseQuestion();
//            }
//
//        }
//        else if(view.getId()==R.id.clear){
//            answerTxt.setText("= ?");
//        }
//        else {
//            response.setVisibility(View.INVISIBLE);
//            int enteredNum = Integer.parseInt(view.getTag().toString());
//
//            if(answerTxt.getText().toString().endsWith("?"))
//                answerTxt.setText("= "+enteredNum);
//            else
//                answerTxt.append(""+enteredNum);
//        }
        int exScore = getScore();
        if(view.getId() == R.id.tick){
            if(randQuestion == 0){
                MusicManager.startWrong();
                hasWrongAnswer = true;
                setYourScore();
                setHighScore();
                Intent endIntent = new Intent(this, EndGame.class);
                startActivity(endIntent);
                finish();
            }
            else if(randQuestion == 1){
                MusicManager.startCorrect();
                scoreText.setText("" + (exScore+1));
                animation.start();
                updateBestScore();
                chooseQuestion();
            }
        }
        else if (view.getId() == R.id.cross){
            if(randQuestion == 0){
                MusicManager.startCorrect();
                scoreText.setText("" + (exScore+1));
                animation.start();
                updateBestScore();
                chooseQuestion();
            }
            else if(randQuestion == 1){
                MusicManager.startWrong();
                hasWrongAnswer = true;
                setYourScore();
                setHighScore();
                Intent endIntent = new Intent(this, EndGame.class);
                startActivity(endIntent);
                finish();
            }
        }
    }

    private int getScore(){
        String scoreStr = scoreText.getText().toString();
//        return Integer.parseInt(scoreStr.substring(scoreStr.lastIndexOf(" ")+1));
        return Integer.parseInt(scoreStr);
    }

    private void setHighScore(){
        List<Score> scoreStrings = new ArrayList<Score>();;
        scoreDatabase.deleteData();
        SharedPreferences.Editor scoreEdit = highScorePrefs.edit();
        DateFormat dateForm = new SimpleDateFormat("dd MMMM yyyy");
        String dateOutput = dateForm.format(new Date());
        String scores = highScorePrefs.getString("highScores", "");
        System.out.println("Length: " + scores.length());

        int exScore = getScore();
        if(exScore > 0){
//            SharedPreferences.Editor scoreEdit = highScorePrefs.edit();
//            DateFormat dateForm = new SimpleDateFormat("dd MMMM yyyy");
//            String dateOutput = dateForm.format(new Date());
//            String scores = highScorePrefs.getString("highScores", "");
//            System.out.println("Length: " + scores.length());
            if(scores.length()>0){
                //we have existing scores
                String[] exScores = scores.split("\\|");

                for(String eSc : exScores){
                    String[] parts = eSc.split(" - ");
                    scoreStrings.add(new Score(parts[0], Integer.parseInt(parts[1])));
                }

                Score newScore = new Score(dateOutput, exScore);
                scoreStrings.add(newScore);
                Collections.sort(scoreStrings);

                StringBuilder scoreBuild = new StringBuilder("");
                for(int s = 0; s < scoreStrings.size(); s++){
                    if(s >= 10) break;//only want ten
                    if(s > 0) scoreBuild.append("|");//pipe separate the score strings
                    scoreBuild.append(scoreStrings.get(s).getScoreText());
                    addData(scoreStrings.get(s).scoreDate, scoreStrings.get(s).scoreNum);
                }
                //write to prefs
                scoreEdit.putString("highScores", scoreBuild.toString());
                scoreEdit.commit();
            }
            else{
                addData(dateOutput, exScore);
                scoreEdit.putString("highScores", ""+dateOutput+" - "+exScore);
                scoreEdit.commit();
            }
        }
        else {
            if(scores.length() > 0) {
                String[] exScores = scores.split("\\|");

                for (String eSc : exScores) {
                    String[] parts = eSc.split(" - ");
                    scoreStrings.add(new Score(parts[0], Integer.parseInt(parts[1])));
                }

                for (int s = 0; s < scoreStrings.size(); s++) {
                    addData(scoreStrings.get(s).scoreDate, scoreStrings.get(s).scoreNum);
                }
            }
        }
    }

    public void addData(String date, int score){
        boolean insertData = scoreDatabase.addData(date, score);

        if(insertData){
            System.out.println("Success");
        }
        else {
            System.out.println("Something wrong");
        }
    }

    public void setYourScore(){
        SharedPreferences.Editor scoreEdit = yourScorePrefs.edit();
        scoreEdit.putString("yourScore", scoreText.getText().toString());
        scoreEdit.commit();
    }

    public int getWrongAnswer(){
        int randOperator = random.nextInt(2);
        if (randOperator == 0) {
            wrongAnswer = answer + (random.nextInt(10) + 1);
        }
        else {
            wrongAnswer = answer - (random.nextInt(10) + 1);
            while (wrongAnswer < 0){
                wrongAnswer = answer - (random.nextInt(10) + 1);
            }
        }
        return wrongAnswer;
    }

    public void updateBestScore(){
        if(Integer.parseInt(scoreText.getText().toString()) > Integer.parseInt(bestText.getText().toString()) ){
            bestText.setText(scoreText.getText());
        }
    }

    public void clearYourScores(){
        SharedPreferences.Editor scoreEdit = yourScorePrefs.edit();
        scoreEdit.clear();
        scoreEdit.commit();
    }
}
