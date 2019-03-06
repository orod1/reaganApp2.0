package com.example.oscar_.reaganapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

public class assistantPrincipalOneActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assistant_principal_one);

        Button buttonBack = findViewById(R.id.btnBack);
        buttonBack.setOnClickListener(assistantPrincipalOneActivity.this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnBack:
                startActivity(new Intent(assistantPrincipalOneActivity.this, assistantPrincipalActivity.class));
                break;
        }
    }
}
