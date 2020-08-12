package com.resala.mosharkaty;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.resala.mosharkaty.LoginActivity.userId;

public class NewAccount extends AppCompatActivity {
  private FirebaseAuth mAuth;
  EditText email_et;
  EditText password_et;
  EditText name_et;
  EditText code_et;
  FirebaseDatabase database;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_new_account);
    mAuth = FirebaseAuth.getInstance();
    email_et = findViewById(R.id.newAccountEmail);
    password_et = findViewById(R.id.newAccountPass);
    name_et = findViewById(R.id.newAccountName);
    code_et = findViewById(R.id.newAccountCode);

    database = FirebaseDatabase.getInstance();
  }

  private void createAccount(String email, String password) {
    if (!validateForm()) {
      return;
    }
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
                          //                  task.getException().printStackTrace();
                          Toast.makeText(
                                  getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT)
                                  .show();
                          updateUI(null);
                        }
                      }
                    });
  }

  private void updateUI(FirebaseUser user) {
    if (user != null) {
      userId = user.getUid();
      DatabaseReference usersRef = database.getReference("users");
      DatabaseReference currentUser = usersRef.child(userId);
      DatabaseReference nameRef = currentUser.child("name");
      DatabaseReference codeRef = currentUser.child("code");

      nameRef.setValue(name_et.getText().toString());
      codeRef.setValue(code_et.getText().toString());
      Toast.makeText(this, "Account created Successfully..", Toast.LENGTH_SHORT).show();
      startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
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

    String name = name_et.getText().toString();
    String[] words = name.split(" ", 5);
    if (TextUtils.isEmpty(name)) {
      name_et.setError("Required.");
      valid = false;
    }
    if (words.length < 3) {
      name_et.setError("الاسم لازم يبقي ثلاثي علي الاقل.");
      valid = false;
    } else {
      name_et.setError(null);
    }

    String code = code_et.getText().toString();
    if (TextUtils.isEmpty(code)) {
      code_et.setError("Required.");
      valid = false;
    } else {
      code_et.setError(null);
    }

    return valid;
  }

  public void newAccountClick(View view) {
    createAccount(email_et.getText().toString(), password_et.getText().toString());
  }
}
