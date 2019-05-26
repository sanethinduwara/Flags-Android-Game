package com.saneth.flags;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TIMER_ON = "countdown.status";
    boolean timerOn = false;    //variable to store whether the countdown timer is on or not

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn_guess_country = findViewById(R.id.btn_guess_country);
        btn_guess_country.setOnClickListener(this);

        Button btn_guess_hints = findViewById(R.id.btn_guess_hints);
        btn_guess_hints.setOnClickListener(this);

        Button btn_guess_flags = findViewById(R.id.btn_guess_flag);
        btn_guess_flags.setOnClickListener(this);

        Button btn_advanced_level = findViewById(R.id.btn_advanced_level);
        btn_advanced_level.setOnClickListener(this);

        Switch timer_switch = findViewById(R.id.timer_switch);
        timer_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                //set timerOn to true if the switch is on or set it to false if the switch is off
                timerOn = isChecked;
            }
        });

    }

    @Override
    public void onBackPressed() {
        showAlert();        //
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btn_guess_country:
                Intent intent_1 = new Intent(getApplicationContext(),GuessCountry.class);
                intent_1.putExtra(TIMER_ON,String.valueOf(timerOn));
                startActivity(intent_1);

                break;
            case R.id.btn_guess_hints:
                Intent intent_2 = new Intent(getApplicationContext(),GuessHint.class);
                intent_2.putExtra(TIMER_ON,String.valueOf(timerOn));
                startActivity(intent_2);
                break;
            case R.id.btn_guess_flag:
                Intent intent_3 = new Intent(getApplicationContext(),GuessFlag.class);
                intent_3.putExtra(TIMER_ON,String.valueOf(timerOn));
                startActivity(intent_3);
                break;
            case R.id.btn_advanced_level:
                Intent intent_4 = new Intent(getApplicationContext(),AdvancedLevel.class);
                intent_4.putExtra(TIMER_ON,String.valueOf(timerOn));
                startActivity(intent_4);
                break;
        }

    }

    public void showAlert() {

        final AlertDialog.Builder myAlertBuilder = new
                AlertDialog.Builder(MainActivity.this);

        myAlertBuilder.setTitle("Quit");
        myAlertBuilder.setMessage("Are you sure you want to exit from the app?");

        myAlertBuilder.setPositiveButton("Yes", new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.super.onBackPressed();
                    }
                });
        myAlertBuilder.setNegativeButton("No", new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        myAlertBuilder.show();

    }
}
