package com.resala.mosharkaty;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import static android.content.ContentValues.TAG;
import static com.resala.mosharkaty.NewAccount.branches;
import static com.resala.mosharkaty.ProfileFragment.userBranch;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    public static String userId;
    public static int branchOrder;
    public static boolean isMrkzy;
    public static boolean isAdmin;
    EditText email_et;
    EditText password_et;
    Admin admin;
    FirebaseDatabase database;

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
                        admin = dataSnapshot.getValue(Admin.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Failed to read value
                        Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });
        FirebaseUser currentUser = mAuth.getCurrentUser();
        FirebaseInstanceId.getInstance()
                .getInstanceId()
                .addOnCompleteListener(
                        task -> {
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "getInstanceId failed", task.getException());
                                return;
                            }

                            // Get new Instance ID token
                            String token = task.getResult().getToken();

                            // Log
                            Log.i("getInstanceId", token);
                        });
        updateUI(currentUser);
    }

  private void makeAdminActions() {
      isAdmin = true;
      Toast.makeText(this, "اتفضل يا استاذ ادمن ", Toast.LENGTH_SHORT).show();
      startActivity(new Intent(getApplicationContext(), MainActivity.class));
      //        throw new RuntimeException("Test Crash"); // Force a crash
  }

  private void updateUI(FirebaseUser user) {
    if (user != null) {
        // Signed in
        userId = user.getUid();
        Toast.makeText(this, "نورت مصر 3>", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    } else {
        Toast.makeText(this, "مش فاكرك ياض :/", Toast.LENGTH_SHORT).show();
    }
  }

  private void signIn(String email, String password) {
    if (!validateForm()) {
      return;
    }
    mAuth
        .signInWithEmailAndPassword(email, password)
        .addOnCompleteListener(
                this,
                task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        updateUI(null);
                    }
                });
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
      if (!password_et.getText().toString().equalsIgnoreCase(admin.password)) return false;
      // check emails
      if (email_et.getText().toString().equalsIgnoreCase(admin.mohandseen)) {
          userBranch = branches[0];
          branchOrder = 0;
          return true;
      } else if (email_et.getText().toString().equalsIgnoreCase(admin.maadi)) {
          userBranch = branches[1];
          branchOrder = 1;
          return true;
      } else if (email_et.getText().toString().equalsIgnoreCase(admin.faisal)) {
          userBranch = branches[2];
          branchOrder = 2;
          return true;
      } else if (email_et.getText().toString().equalsIgnoreCase(admin.mnasr)) {
          userBranch = branches[3];
          branchOrder = 3;
          return true;
      } else if (email_et.getText().toString().equalsIgnoreCase(admin.msrelgdida)) {
          userBranch = branches[4];
          branchOrder = 4;
          return true;
      } else if (email_et.getText().toString().equalsIgnoreCase(admin.october)) {
          userBranch = branches[5];
          branchOrder = 5;
          return true;
      } else if (email_et.getText().toString().equalsIgnoreCase(admin.helwan)) {
          userBranch = branches[6];
          branchOrder = 6;
          return true;
      } else if (email_et.getText().toString().equalsIgnoreCase(admin.alex)) {
          userBranch = branches[7];
          branchOrder = 7;
          return true;
      } else if (email_et.getText().toString().equalsIgnoreCase(admin.mokattam)) {
          userBranch = branches[8];
          branchOrder = 8;
          return true;
      } else if (email_et.getText().toString().equalsIgnoreCase(admin.mrkzy)) {
          isMrkzy = true;
          userBranch = branches[0];
          branchOrder = 0;
          return true;
      } else return false;
      //    return (email_et.getText().toString().equalsIgnoreCase(admin.email)
      //        && password_et.getText().toString().equalsIgnoreCase(admin.password));
  }

  public void loginClick(View view) {
      ConnectivityManager connectivityManager =
              (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

      if (connectivityManager != null) {
          if (connectivityManager.getActiveNetworkInfo() == null
                  || !connectivityManager.getActiveNetworkInfo().isConnected()) {
              //          Toast.makeText(getApplicationContext(), "No Internet",
              // Toast.LENGTH_SHORT).show();
              Snackbar.make(view, "No Internet", Snackbar.LENGTH_LONG).setAction("Action", null).show();
              return;
          }
      }

      if (IsAdminDetails()) makeAdminActions();
      else { // not admin
          Log.d(TAG, "user not admin ");
          Log.d(TAG, "entered email : " + email_et.getText().toString());
          Log.d(TAG, "entered pass : " + password_et.getText().toString());

          isAdmin = false;
          signIn(email_et.getText().toString(), password_et.getText().toString());
      }
  }

  public void resetPassword(View view) {
      String email = email_et.getText().toString();
      if (TextUtils.isEmpty(email)) {
          email_et.setError("must enter a valid email");
          return;
      }
      mAuth
              .sendPasswordResetEmail(email_et.getText().toString())
              .addOnCompleteListener(
                      task -> {
                          if (task.isSuccessful()) {
                              Toast.makeText(
                                      getApplicationContext(),
                                      "check your email for reset password link",
                                      Toast.LENGTH_SHORT)
                                      .show();
                              Log.d(TAG, "Email sent.");
                          } else {
                              Toast.makeText(
                                      getApplicationContext(), "couldn't reset password", Toast.LENGTH_SHORT)
                                      .show();
                          }
                      });
  }
}
