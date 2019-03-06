package com.example.oscar_.reaganapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class assistantPrincipalActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assistant_principal);


        Button buttonBack = findViewById(R.id.btnBack);
        buttonBack.setOnClickListener(assistantPrincipalActivity.this);

        Button buttonAPOne = findViewById(R.id.btnAPOne);
        buttonAPOne.setOnClickListener(assistantPrincipalActivity.this);

        Button buttonAPTwo = findViewById(R.id.btnAPTwo);
        buttonAPTwo.setOnClickListener(assistantPrincipalActivity.this);

        Button buttonAPThree = findViewById(R.id.btnAPThree);
        buttonAPThree.setOnClickListener(assistantPrincipalActivity.this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.btnBack:
                startActivity(new Intent(assistantPrincipalActivity.this, AboutActivity.class));
                break;

            case R.id.btnAPOne:
                startActivity(new Intent(assistantPrincipalActivity.this, assistantPrincipalOneActivity.class));
                break;

            case R.id.btnAPTwo:
                startActivity(new Intent(assistantPrincipalActivity.this, assistantPrincipalTwoActivity.class));
                break;

            case R.id.btnAPThree:
                startActivity(new Intent(assistantPrincipalActivity.this, assistantPrincipalThreeActivity.class));
                break;
        }
    }
}
