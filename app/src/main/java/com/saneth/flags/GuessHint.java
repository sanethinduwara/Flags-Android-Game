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

public class GuessHint extends AppCompatActivity {

    private ArrayList<String> flags;
    private ImageView flag_img;
    private EditText guessed_char;
    private Button btn_submit;
    private TextView country_lbl, correct_ans_lbl, answer_status_lbl, timer_lbl;
    private String country_name;
    private String hidden_country_name = "";
    private int wrong_attempts = 0;
    private int flag_index = 0;
    private FlagManager flagManager;
    private CountDownTimer countDownTimer;
    private Boolean timer_on;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess_hint);

        flag_img = findViewById(R.id.flag_img);
        guessed_char = findViewById(R.id.guessed_character);
        btn_submit = findViewById(R.id.btn_submit);
        country_lbl = findViewById(R.id.country_lbl);
        correct_ans_lbl = findViewById(R.id.correct_ans_lbl);
        answer_status_lbl = findViewById(R.id.answer_status_lbl);
        timer_lbl = findViewById(R.id.timer_lbl);

        //creating a FlagManager object
        flagManager = new FlagManager(this);

        //getting the message whether the countdown timer is on or not
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.TIMER_ON);
        timer_on = Boolean.valueOf(message);

        //creating CountdownTimer object
        initializeTimer();

        flags = flagManager.getFlags();     //storing iso codes in an array list
        Collections.shuffle(flags, new Random());       //shuffling the array list which contains iso codes

        loadFlag(flag_index);       //loading image to the image view

        Log.d("country_name", country_name);
        Log.d("hidden_country_name", hidden_country_name);

        btn_submit.setOnClickListener(new View.OnClickListener() {
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

    public void loadFlag(int i) {

        String image_file_name = flags.get(i).toLowerCase();        //getting iso code of a given index and converting it to lower case
        if (image_file_name.contains("-")) {
            image_file_name = image_file_name.replace("-", "_");     //replace '-' in iso code with '_' and storing it as image_file_name (if there are any)
        }
        int img_res = getResources().getIdentifier(image_file_name, "drawable", getApplicationContext().getPackageName());      //getting image from drawables
        flag_img.setImageResource(img_res);

        if (timer_on) {
            countDownTimer.start();
        }

        country_name = flagManager.getCorrectAnswer(flags.get(i));
        hidden_country_name = country_name.replaceAll("[A-Z]", "-");

        country_lbl.setText(hidden_country_name);

    }

    public void checkGuesses() {

        String guess = guessed_char.getText().toString().toUpperCase();

        if (!guess.equals("") && country_name.contains(guess)) {
            for (int i = 0; i < country_name.length(); i++) {       //check each and every letter in country name whether the it contains the guess
                if (("" + country_name.charAt(i)).equals(guess)) {
                    hidden_country_name = hidden_country_name.substring(0, i) + country_name.charAt(i) + hidden_country_name.substring(i + 1);      //if a letter in the name matches with the guess, then replace the '-' with the correct letter
                }
            }
            if (!hidden_country_name.contains("-")) {
                answer_status_lbl.setText(R.string.correct);
                answer_status_lbl.setTextColor(Color.GREEN);
                btn_submit.setText(R.string.next);
                wrong_attempts = 0;
            }
        } else {
            wrong_attempts++;
            Toast toast = Toast.makeText(getApplicationContext(), "Attempts Left : " + (3 - wrong_attempts), Toast.LENGTH_SHORT);
            toast.show();

            if (wrong_attempts == 3) {
                answer_status_lbl.setText(R.string.wrong);
                answer_status_lbl.setTextColor(Color.RED);
                correct_ans_lbl.setText(country_name);
                correct_ans_lbl.setTextColor(Color.BLUE);
                btn_submit.setText(R.string.next);
                wrong_attempts = 0;
            }
        }

        country_lbl.setText(hidden_country_name);
        guessed_char.setText("");
    }


    public void initializeTimer() {

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

        if (btn_submit.getText().toString().equals("Next")) {
            ++flag_index;
            if (flag_index < flags.size()) {
                loadFlag(flag_index);
                answer_status_lbl.setText("");
                correct_ans_lbl.setText("");
                btn_submit.setText(R.string.submit);
            } else {
                Toast.makeText(this, "Level Completed", Toast.LENGTH_LONG).show();
            }
        } else if (btn_submit.getText().toString().equals("Submit")) {
            checkGuesses();
            if (!btn_submit.getText().toString().equals("Next")) {
                if (timer_on) {
                    countDownTimer.start();
                }
            }
        }
    }

    public void showAlert() {

        final AlertDialog.Builder myAlertBuilder = new
                AlertDialog.Builder(GuessHint.this);

        myAlertBuilder.setTitle("Quit");        //setting title for the alert
        myAlertBuilder.setMessage("Are you sure you want to exit?");        //setting message for the alert

        myAlertBuilder.setPositiveButton("Yes", new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (timer_on) {
                            countDownTimer.cancel();
                        }
                        GuessHint.super.onBackPressed();
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
