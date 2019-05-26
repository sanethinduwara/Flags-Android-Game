package com.saneth.flags;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class AdvancedLevel extends AppCompatActivity {

    private ImageView flag_img_1, flag_img_2, flag_img_3;
    private EditText guess_1, guess_2, guess_3;
    private TextView status_lbl, score_lbl, timer_lbl;
    private Button btn_submit;
    private ArrayList<String> flags, country_name;
    private ArrayList<EditText> guess;
    private int score=0;
    private int flag_index = 0;
    private int failed_attempts = 0;
    private CountDownTimer countDownTimer;
    private FlagManager flagManager;
    private boolean timer_on;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_level);

        flag_img_1 = findViewById(R.id.flag_img_1);
        flag_img_2 = findViewById(R.id.flag_img_2);
        flag_img_3 = findViewById(R.id.flag_img_3);
        btn_submit = findViewById(R.id.btn_submit);
        status_lbl = findViewById(R.id.answer_status_lbl);
        guess_1 = findViewById(R.id.guess_1);
        guess_2 = findViewById(R.id.guess_2);
        guess_3 = findViewById(R.id.guess_3);
        score_lbl = findViewById(R.id.score_lbl);
        timer_lbl = findViewById(R.id.timer_lbl);

        flagManager = new FlagManager(this);

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.TIMER_ON);
        timer_on = Boolean.valueOf(message);

        //creating the CountdownTimer
        initializeTimer();

        score_lbl.setText("Score :" + score);

        guess = new ArrayList<>();

        guess.add(guess_1);
        guess.add(guess_2);
        guess.add(guess_3);

        flags = flagManager.getFlags();     //storing iso codes in an array list
        Collections.shuffle(flags, new Random());       //shuffling the array list which contains iso codes


        loadFlag(flag_index);
        if (timer_on) {
            countDownTimer.start();
        }

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timer_on) {
                    countDownTimer.cancel();
                }
                if (flag_index<(flags.size()-4)){
                    submit();
                }else {
                    Toast.makeText(getApplicationContext(),"Advanced Level Completed",Toast.LENGTH_LONG).show();
                }

            }
        });


    }

    @Override
    public void onBackPressed() {
        showAlert();        //show exit confirmation alert on back pressed
    }


    public void loadFlag(int i){

        int img_res[] = new int[3];
        country_name = new ArrayList<String>();
        for (int j=0; j<3;j++){

            String image_file_name = flags.get(i).toLowerCase();        //getting iso code of a given index and converting it to lower case
            if (image_file_name.contains("-")){
                image_file_name = image_file_name.replace("-","_");     //replace '-' in iso code with '_' and storing it as image_file_name (if there are any)
            }
            img_res[j] = getResources().getIdentifier(image_file_name, "drawable", getApplicationContext().getPackageName());       //getting image from drawables
            country_name.add(flagManager.getCorrectAnswer(flags.get(i)));
            i++;
        }

        flag_img_1.setImageResource(img_res[0]);
        flag_img_2.setImageResource(img_res[1]);
        flag_img_3.setImageResource(img_res[2]);
    }

    public void checkAnswers(ArrayList<EditText> guess){
        int correct_answers = 0;
        String user_answers[] = new String[3];

        for (int i=0;i<3;i++) {     //checking given 3 answers
            String user_answer = guess.get(i).getText().toString().toUpperCase();   //getting text from the EditText and converting it to uppercase
            String trimmed_answer = user_answer.trim();         //removing spaces in beginning and end of the string
            user_answers[i] = trimmed_answer;

            if (country_name.get(i).equals(trimmed_answer)) {
                guess.get(i).setTextColor(Color.DKGRAY);
                guess.get(i).setEnabled(false);
                correct_answers++;

            }else {
                guess.get(i).setTextColor(Color.RED);
            }
        }

        if (correct_answers==3){
            status_lbl.setText(R.string.correct);
            status_lbl.setTextColor(Color.GREEN);
            btn_submit.setText(R.string.next);
            failed_attempts = 0;
        }else {
            failed_attempts++;
            Toast.makeText(this,"Attempts Left : "+(3-failed_attempts),Toast.LENGTH_SHORT).show();
            if (failed_attempts<3) {
                if (timer_on) {
                    countDownTimer.start();
                }

            }else if (failed_attempts==3){

                for (int i=0;i<3;i++) {
                    if (!user_answers[i].equals(country_name.get(i))){
                        guess.get(i).setText(country_name.get(i));
                        guess.get(i).setTextColor(Color.BLUE);
                    }
                }
                status_lbl.setText(R.string.wrong);
                status_lbl.setTextColor(Color.RED);
                btn_submit.setText(R.string.next);
                failed_attempts=0;
            }
        }

        score = correct_answers;
        score_lbl.setText("Score : " + score);


    }

    public void submit(){

        if (timer_on) {
            countDownTimer.cancel();
        }

        if (btn_submit.getText().toString().equals("Submit")) {
            String guessed_values[] = new String[3];
            for (int i=0;i<3;i++){
                guessed_values[i] = guess.get(i).getText().toString();
            }

                checkAnswers(guess);

        } else if (btn_submit.getText().toString().equals("Next")) {
            flag_index+=3;
            loadFlag(flag_index);
            if (timer_on) {
                countDownTimer.start();
            }
            btn_submit.setText(R.string.submit);
            status_lbl.setText("");
            score = 0;
            failed_attempts = 0;
            score_lbl.setText("Score : "+score);

            for (int i=0;i<3;i++){
                guess.get(i).setEnabled(true);
                guess.get(i).setText("");
                guess.get(i).setTextColor(Color.BLACK);

            }
        }

        Log.d("failed_attempts", String.valueOf(failed_attempts));
    }

    public void initializeTimer(){

        countDownTimer = new CountDownTimer(10000, 1000) {      //creating countdownTimer with 1s intervals for 10s

            public void onTick(long millisUntilFinished) {
                timer_lbl.setText("Time Left : " + millisUntilFinished / 1000);     //display remaining time in seconds
            }

            public void onFinish() {
                submit();       //submitting the page when the countdown completed
            }
        };
    }

    public void showAlert() {

        final AlertDialog.Builder myAlertBuilder = new
                AlertDialog.Builder(AdvancedLevel.this);

        myAlertBuilder.setTitle("Quit");        //setting title for the alert
        myAlertBuilder.setMessage("Are you sure you want to exit?");        //setting message for the alert

        myAlertBuilder.setPositiveButton("Yes", new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (timer_on) {
                            countDownTimer.cancel();
                        }
                        AdvancedLevel.super.onBackPressed();
                    }
                });
        myAlertBuilder.setNegativeButton("No", new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        myAlertBuilder.show();      //showing the alert

    }

}
