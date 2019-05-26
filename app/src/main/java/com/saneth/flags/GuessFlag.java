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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class GuessFlag extends AppCompatActivity implements View.OnClickListener {

    private ImageView flag_img_1, flag_img_2, flag_img_3;
    private Button submit_btn;
    private ArrayList<String> flags;
    private String clicked_country;
    private TextView country_lbl, status_lbl;
    private int flag_index = 0;
    private FlagManager flagManager;
    private CountDownTimer countDownTimer;
    private TextView timer_lbl;
    private Boolean timer_on;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess_flag);

        flag_img_1 = findViewById(R.id.flag_img_1);
        flag_img_2 = findViewById(R.id.flag_img_2);
        flag_img_3 = findViewById(R.id.flag_img_3);
        submit_btn = findViewById(R.id.btn_submit);
        country_lbl = findViewById(R.id.country_lbl);
        status_lbl = findViewById(R.id.answer_status_lbl);
        timer_lbl = findViewById(R.id.timer_lbl);

        flag_img_1.setOnClickListener(this);
        flag_img_2.setOnClickListener(this);
        flag_img_3.setOnClickListener(this);
        submit_btn.setOnClickListener(this);

        flagManager = new FlagManager(this);

        //creating the CountdownTimer
        initializeTimer();

        //getting the message whether the countdown timer is on or not
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.TIMER_ON);
        timer_on = Boolean.valueOf(message);

        flags = flagManager.getFlags();         //storing iso codes in an array list
        Collections.shuffle(flags, new Random());       //shuffling the array list which contains iso codes

        loadFlag(flag_index);    //loading image to the image view
        if (timer_on) {
            countDownTimer.start();
        }

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();

            }
        });
    }

    @Override
    public void onBackPressed() {
        showAlert();        //show exit confirmation alert on back pressed
    }

    public void loadFlag(int i){

        int img_res[] = new int[3];
        ArrayList<String> country_name = new ArrayList<String>();
        for (int j=0; j<3;j++){

            String image_file_name = flags.get(i).toLowerCase();        //getting iso code of a given index and converting it to lower case
            if (image_file_name.contains("-")){
                image_file_name = image_file_name.replace("-","_");     //replace '-' in iso code with '_' and storing it as image_file_name (if there are any)
            }

            img_res[j] = getResources().getIdentifier(image_file_name, "drawable", getApplicationContext().getPackageName());       //getting image from drawables
            country_name.add(flagManager.getCorrectAnswer(flags.get(i)));
            i++;
        }
        Collections.shuffle(country_name, new Random());

        flag_img_1.setImageResource(img_res[0]);
        flag_img_2.setImageResource(img_res[1]);
        flag_img_3.setImageResource(img_res[2]);
        country_lbl.setText(country_name.get(0));
    }

    @Override
    public void onClick(View v) {

        clicked_country="";

        switch (v.getId()){
            case R.id.flag_img_1:
                clicked_country= flagManager.getCorrectAnswer(flags.get(flag_index));
                break;
            case R.id.flag_img_2:
                clicked_country= flagManager.getCorrectAnswer(flags.get(flag_index+1));
                break;
            case R.id.flag_img_3:
                clicked_country= flagManager.getCorrectAnswer(flags.get(flag_index+2));
                break;
        }
        checkAnswer();
    }

    public void checkAnswer(){
        if (clicked_country.equals(country_lbl.getText().toString())){
            status_lbl.setText(R.string.correct);
            status_lbl.setTextColor(Color.GREEN);
        }else {
            status_lbl.setText(R.string.wrong);
            status_lbl.setTextColor(Color.RED);
        }
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

        if (timer_on) {
            countDownTimer.cancel();
        }

        if (flag_index<(flags.size()-4)){
            flag_index+=3;
            loadFlag(flag_index);
            if (timer_on) {
                countDownTimer.start();
            }
            status_lbl.setText("");
        }else {
            Toast.makeText(getApplicationContext(),"Flag Guessing Completed",Toast.LENGTH_LONG).show();
        }
    }

    public void showAlert() {

        final AlertDialog.Builder myAlertBuilder = new
                AlertDialog.Builder(GuessFlag.this);

        myAlertBuilder.setTitle("Quit");        //setting title for the alert
        myAlertBuilder.setMessage("Are you sure you want to exit?");        //setting message for the alert

        myAlertBuilder.setPositiveButton("Yes", new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        GuessFlag.super.onBackPressed();
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
