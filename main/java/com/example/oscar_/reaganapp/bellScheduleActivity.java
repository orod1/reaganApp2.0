package com.example.oscar_.reaganapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class bellScheduleActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bell_schedule);


        Button buttonBack = (Button) findViewById(R.id.btnBack);
        buttonBack.setOnClickListener(bellScheduleActivity.this);
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){

            case R.id.btnBack:

                startActivity(new Intent(bellScheduleActivity.this, MainActivity.class));

                break;

        }

    }
}
