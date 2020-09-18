package com.resala.mosharkaty;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.resala.mosharkaty.utility.classes.Rules;

import static android.content.ContentValues.TAG;

public class SplashActivity extends AppCompatActivity {
    DatabaseReference rulesRef;
    public static Rules myRules;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        database = FirebaseDatabase.getInstance();

//        int SPLASH_DISPLAY_LENGTH = 1000; // Duration of wait
//        new Handler()
//                .postDelayed(
//                        new Runnable() {
//                            @Override
//                            public void run() {
//                                Splash.this.startActivity(new Intent(Splash.this, Starter.class));
//                                Splash.this.finish();
//                            }
//                        },
//                        SPLASH_DISPLAY_LENGTH);
        rulesRef = database.getReference("Rules");
        rulesRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        myRules = dataSnapshot.getValue(Rules.class);
                        startActivity(new Intent(SplashActivity.this, StarterActivity.class));
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Failed to read value
                        Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });
    }
}
