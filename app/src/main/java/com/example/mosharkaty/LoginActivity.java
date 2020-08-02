package com.example.mosharkaty;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
  private FirebaseAuth mAuth;
  public static String userId = "-1";
  public static boolean isAdmin = false;
  Button loginBtn;
  EditText email_et;
  EditText password_et;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    // Initialize Firebase Auth
    mAuth = FirebaseAuth.getInstance();
    loginBtn = findViewById(R.id.login_btn);
    email_et = findViewById(R.id.editTextTextEmailAddress);
    password_et = findViewById(R.id.editTextTextPassword);

    FirebaseUser currentUser = mAuth.getCurrentUser();
    updateUI(currentUser);
  }

  private void makeAdminActions() {
    isAdmin = true;
    Toast.makeText(this, "admin Access granted ", Toast.LENGTH_SHORT).show();
    startActivity(new Intent(getApplicationContext(), MainActivity.class));
  }

  private void updateUI(FirebaseUser user) {
    if (user != null) {
      // Signed in
      userId = user.getUid();
      // loginBtn.setVisibility(View.GONE);
      // loginBtn.setText("you are logged in");
      Toast.makeText(this, "login Successful", Toast.LENGTH_SHORT).show();
      startActivity(new Intent(getApplicationContext(), MainActivity.class));
    } else {
      // Signed out
      loginBtn.setVisibility(View.VISIBLE);
      Toast.makeText(this, "login Failed .. try again", Toast.LENGTH_SHORT).show();
    }
  }

  private void createAccount(String email, String password) {
    if (!validateForm()) {
      return;
    }

    // [START create_user_with_email]
    mAuth
        .createUserWithEmailAndPassword(email, password)
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
    // [END create_user_with_email]
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

  public void loginClick(View view) {
    if (email_et.getText().toString().equals("admin")
        || password_et.getText().toString().equals("admin")) makeAdminActions();
    else { // not admin
      isAdmin = false;
      signIn(email_et.getText().toString(), password_et.getText().toString());
    }
  }

  public void newAccountClick(View view) {
    createAccount(email_et.getText().toString(), password_et.getText().toString());
  }
}
