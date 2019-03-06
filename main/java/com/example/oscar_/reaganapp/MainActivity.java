package com.example.oscar_.reaganapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonBS = findViewById(R.id.btnBS);
        buttonBS.setOnClickListener(MainActivity.this);

        ImageButton buttonAbout = findViewById(R.id.btnAbout);
        buttonAbout.setOnClickListener(MainActivity.this);

        Button buttonMap = findViewById(R.id.btnMap);
        buttonMap.setOnClickListener(MainActivity.this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.btnBS:
                startActivity(new Intent(MainActivity.this, bellScheduleActivity.class));
                break;

            case R.id.btnAbout:
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                break;

            case R.id.btnMap:
                startActivity(new Intent(MainActivity.this, mapActivity.class));
                break;
        }
    }
}
