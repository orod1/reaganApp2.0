package com.example.oscar_.reaganapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);




        Button buttonBack = findViewById(R.id.btnBack);
        buttonBack.setOnClickListener(AboutActivity.this);

        Button buttonAP = findViewById(R.id.btnAP);
        buttonAP.setOnClickListener(AboutActivity.this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.btnBack:
                startActivity(new Intent(AboutActivity.this, MainActivity.class));
                break;

            case R.id.btnAP:
                startActivity(new Intent(AboutActivity.this, assistantPrincipalActivity.class));
                break;

        }
    }
}
