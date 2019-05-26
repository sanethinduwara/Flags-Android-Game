package com.saneth.flags;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class GuessCountry extends AppCompatActivity {

    private ImageView flag_img;
    private Spinner spinner;
    private Button btn_submit;
    private TextView answer_status, correct_answer, timer_lbl;
    private ArrayList<String> flags;
    private int flag_index = 0;
    private CountDownTimer countDownTimer;
    private FlagManager flagManager;
    boolean timer_on;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess_country);

        //creating a FlagManager object
        flagManager = new FlagManager(this);

        //getting the message whether the countdown timer is on or not
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.TIMER_ON);
        timer_on = Boolean.valueOf(message);

        Log.d("timer_on",message);

        flag_img = findViewById(R.id.flag_img);
        btn_submit = findViewById(R.id.btn_submit);
        answer_status = findViewById(R.id.answer_status_lbl);
        correct_answer = findViewById(R.id.correct_answer_lbl);
        timer_lbl = findViewById(R.id.timer_lbl);

        //creating the CountdownTimer
        initializeTimer();

        ArrayList<String> items= flagManager.getCountries();    //storing country names in an array list
        spinner=(Spinner)findViewById(R.id.spinner);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,
                R.layout.spinner_layout,R.id.txt,items);
        spinner.setAdapter(adapter);

        flags = flagManager.getFlags();     //storing iso codes in an array list
        Collections.shuffle(flags, new Random());       //shuffling the array list which contains iso codes

        loadImage(flag_index);      //loading image to the image view

        if (timer_on) {
            countDownTimer.start();     //if countdown timer switch is on, then start the countdownTimer
        }



        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timer_on) {
                    countDownTimer.cancel();       //cancel the running timer
                }
                submit();
            }
        });



    }

    @Override
    public void onBackPressed() {
        showAlert();        //show exit confirmation alert on back pressed
    }



    public boolean isCorrect(String flag){

        boolean correct;

        String user_answer = spinner.getSelectedItem().toString().toUpperCase();        //getting the selected item name from the spinner
        String correct_answer = flagManager.getCorrectAnswer(flag);         //getting the correct name of the flag loaded in the image view

        correct = user_answer.equals(correct_answer);
        return correct;
    }

    private void loadImage(int i){
        String image_file_name = flags.get(i).toLowerCase();        //getting iso code of a given index and converting it to lower case
        if (image_file_name.contains("-")){
            image_file_name = image_file_name.replace("-","_");     //replace '-' in iso code with '_' and storing it as image_file_name (if there are any)
        }
        int img_res = getResources().getIdentifier(image_file_name, "drawable", getApplicationContext().getPackageName());      //getting image from drawables
        flag_img.setImageResource(img_res);
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

    private void submit() {

        if (flag_index<flags.size()) {

            if (btn_submit.getText().toString().equals("Submit")) {
                boolean correct = isCorrect(flags.get(flag_index).toUpperCase());       //check whether the user ic correct or not
                if (correct) {
                    answer_status.setText(R.string.correct);
                    answer_status.setTextColor(Color.GREEN);
                } else {
                    answer_status.setText(R.string.wrong);
                    answer_status.setTextColor(Color.RED);
                    correct_answer.setText(flagManager.getCorrectAnswer(flags.get(flag_index).toUpperCase()));      //displaying the correct answer in uppercase
                    correct_answer.setTextColor(Color.BLUE);
                }
                btn_submit.setText(R.string.next);
            }else if (btn_submit.getText().toString().equals("Next")) {
                ++flag_index;
                loadImage(flag_index);

                if (timer_on) {
                    countDownTimer.start();
                }

                answer_status.setText("");
                correct_answer.setText("");

                btn_submit.setText(R.string.submit);
            }
        }
    }

    public void showAlert() {

        final AlertDialog.Builder myAlertBuilder = new
                AlertDialog.Builder(GuessCountry.this);

        myAlertBuilder.setTitle("Quit");        //setting title for the alert
        myAlertBuilder.setMessage("Are you sure you want to exit?");        //setting message for the alert

        myAlertBuilder.setPositiveButton("Yes", new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        GuessCountry.super.onBackPressed();
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
