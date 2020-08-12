package com.resala.mosharkaty;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.ContentValues.TAG;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    public static String userId;
    public static boolean isAdmin;
    EditText email_et;
    EditText password_et;
    FirebaseDatabase database;
    final String[] adminEmail = new String[1];
    final String[] adminPass = new String[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    // Initialize Firebase Auth
    mAuth = FirebaseAuth.getInstance();
    email_et = findViewById(R.id.editTextTextEmailAddress);
    password_et = findViewById(R.id.editTextTextPassword);
    database = FirebaseDatabase.getInstance();
    DatabaseReference adminAccount = database.getReference("AdminAccount");
    adminAccount.addValueEventListener(
        new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Admin admin = dataSnapshot.getValue(Admin.class);
            if (admin != null) {
              adminEmail[0] = admin.email;
              adminPass[0] = admin.password;
              Log.d(TAG, "admin email : " + adminEmail[0]);
              Log.d(TAG, "admin pass : " + adminPass[0]);
            } else
              Toast.makeText(
                      getApplicationContext(), "error reading admin data", Toast.LENGTH_SHORT)
                  .show();
          }

          @Override
          public void onCancelled(@NonNull DatabaseError error) {
            // Failed to read value
            Log.w(TAG, "Failed to read value.", error.toException());
          }
        });
    FirebaseUser currentUser = mAuth.getCurrentUser();
    updateUI(currentUser);
  }

  private void makeAdminActions() {
      isAdmin = true;
      Toast.makeText(this, "Admin Access granted ", Toast.LENGTH_SHORT).show();
      startActivity(new Intent(getApplicationContext(), MainActivity.class));
//        throw new RuntimeException("Test Crash"); // Force a crash
  }

  private void updateUI(FirebaseUser user) {
    if (user != null) {
      // Signed in
      userId = user.getUid();
      Toast.makeText(this, "login Successful", Toast.LENGTH_SHORT).show();
      startActivity(new Intent(getApplicationContext(), MainActivity.class));
    } else {
      Toast.makeText(this, "login Failed .. try again", Toast.LENGTH_SHORT).show();
    }
  }

  private void signIn(String email, String password) {
    if (!validateForm()) {
      return;
    }

    // [START sign_in_with_email]
    mAuth
        .signInWithEmailAndPassword(email, password)
        .addOnCompleteListener(
            this,
            new OnCompleteListener<AuthResult>() {
              @Override
              public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                  // Sign in success, update UI with the signed-in user's information
                  FirebaseUser user = mAuth.getCurrentUser();
                  updateUI(user);
                } else {
                  // If sign in fails, display a message to the user.
                  Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT)
                      .show();
                  updateUI(null);
                }
              }
            });
    // [END sign_in_with_email]
  }

  private boolean validateForm() {
    boolean valid = true;

    String email = email_et.getText().toString();
    if (TextUtils.isEmpty(email)) {
      email_et.setError("Required.");
      valid = false;
    } else {
      email_et.setError(null);
    }

    String password = password_et.getText().toString();
    if (TextUtils.isEmpty(password)) {
      password_et.setError("Required.");
      valid = false;
    } else {
      password_et.setError(null);
    }

    return valid;
  }

  private boolean IsAdminDetails() {
    return (email_et.getText().toString().equalsIgnoreCase(adminEmail[0])
        && password_et.getText().toString().equalsIgnoreCase(adminPass[0]));
  }

  public void loginClick(View view) {
    if (IsAdminDetails()) makeAdminActions();
    else { // not admin
      Log.d(TAG, "user not admin ");
      Log.d(TAG, "entered email : " + email_et.getText().toString());
      Log.d(TAG, "entered pass : " + password_et.getText().toString());

      isAdmin = false;
      signIn(email_et.getText().toString(), password_et.getText().toString());
    }
  }

  public void newAccountClick(View view) {
      startActivity(new Intent(this, NewAccount.class));
  }
}
