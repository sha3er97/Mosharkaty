package com.resala.mosharkaty;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class Starter extends AppCompatActivity {

  //  FirebaseRemoteConfig mFirebaseRemoteConfig;
  //  boolean debug = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_starter);
    //    TextView welcomeTV = findViewById(R.id.welcomeTV);
    //    mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
    //    mFirebaseRemoteConfig
    //        .fetchAndActivate()
    //        .addOnCompleteListener(
    //            this,
    //            task -> {
    //              if (task.isSuccessful()) {
    //                welcomeTV.setText(mFirebaseRemoteConfig.getString("test"));
    //
    //                //                debug = mFirebaseRemoteConfig.getBoolean("debug");
    ////                Toast.makeText(
    ////                        getApplicationContext(),
    ////                        "welcome_message = " +
    // mFirebaseRemoteConfig.getValue("welcome_message").toString(),
    ////                        Toast.LENGTH_SHORT)
    ////                    .show();
    //
    //              } else {
    //                Toast.makeText(getApplicationContext(), "something went wrong",
    // Toast.LENGTH_SHORT)
    //                    .show();
    //              }
    //            });
  }

  public void newAccStarter(View view) {
    startActivity(new Intent(this, NewAccount.class));
  }

  public void loginStarter(View view) {
    startActivity(new Intent(this, LoginActivity.class));
  }
}
