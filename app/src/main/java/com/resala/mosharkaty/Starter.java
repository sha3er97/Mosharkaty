package com.resala.mosharkaty;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class Starter extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starter);
    }

    public void newAccStarter(View view) {
        startActivity(new Intent(this, NewAccount.class));
    }

    public void loginStarter(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }
}