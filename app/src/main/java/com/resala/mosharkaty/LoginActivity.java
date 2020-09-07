package com.resala.mosharkaty;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.util.HashMap;

import static android.content.ContentValues.TAG;
import static com.resala.mosharkaty.NewAccount.branches;

public class LoginActivity extends AppCompatActivity {
  private FirebaseAuth mAuth;
  public static String userId;
  public static String userBranch;
  public static int branchOrder;
  public static boolean isMrkzy;
  public static boolean isAdmin;
  public static HashMap<String, normalVolunteer> allVolunteersByName = new HashMap<>();
  public static HashMap<String, normalVolunteer> allVolunteersByPhone = new HashMap<>();

  EditText email_et;
  EditText password_et;
  Admin admin;
  FirebaseDatabase database;
  ImageView logo;
  Button login_btn;
//    private ProgressDialog progress;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    // Initialize Firebase Auth
    mAuth = FirebaseAuth.getInstance();
    email_et = findViewById(R.id.editTextTextEmailAddress);
    password_et = findViewById(R.id.editTextTextPassword);
    database = FirebaseDatabase.getInstance();
    logo = findViewById(R.id.logo);

    login_btn = findViewById(R.id.login_btn);
    email_et.setOnFocusChangeListener(
            (view, hasFocus) -> {
              if (hasFocus) {
                logo.setVisibility(View.GONE);
              } else {
                logo.setVisibility(View.VISIBLE);
              }
            });
    password_et.setOnFocusChangeListener(
            (view, hasFocus) -> {
              if (hasFocus) {
                logo.setVisibility(View.GONE);
              } else {
                logo.setVisibility(View.VISIBLE);
              }
            });
//    progress = new ProgressDialog(getApplicationContext());
//    progress.setTitle("Loading");
//    progress.setMessage("لحظات معانا...");
//    progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
//    progress.show();
    DatabaseReference adminAccount = database.getReference("AdminAccount");
    adminAccount.addValueEventListener(
            new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                admin = dataSnapshot.getValue(Admin.class);
//            progress.dismiss();
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
    login_btn.setEnabled(false);
    login_btn.setBackgroundColor(
            getResources().getColor(R.color.common_google_signin_btn_text_light_disabled));
    DatabaseReference allVolsRef =
            database.getReference("1tsMZ5EwtKrBUGuLFVBvuwpU5ve0JKMsaqK1nNAONj-0").child("all");
    allVolsRef.addListenerForSingleValueEvent(
            new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //            progress = new ProgressDialog(getApplicationContext());
                //            progress.setTitle("Loading");
                //            progress.setMessage("لحظات معانا...");
                //            progress.setCancelable(false); // disable dismiss by tapping outside of
                // the dialog
                //            progress.show();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                  normalVolunteer user = snapshot.getValue(normalVolunteer.class);
                  assert user != null;
                  allVolunteersByName.put(user.Volname, user);
                  allVolunteersByPhone.put(user.phone_text, user);

                }
                isAdmin = true;
                //            progress.dismiss();
                Toast.makeText(getApplicationContext(), "اتفضل يا استاذ ادمن ", Toast.LENGTH_SHORT)
                        .show();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
              }

              @Override
              public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
              }
            });

    //        throw new RuntimeException("Test Crash"); // Force a crash
  }

  private void updateUI(FirebaseUser user) {
    if (user != null) {
      // Signed in
      userId = user.getUid();
      Toast.makeText(this, "اهلا اهلا 3>", Toast.LENGTH_SHORT).show();
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
    if (!password_et.getText().toString().trim().equalsIgnoreCase(admin.password)) return false;
    // check emails
    if (email_et.getText().toString().trim().equalsIgnoreCase(admin.mohandseen)) {
      userBranch = branches[0];
      branchOrder = 0;
      return true;
    } else if (email_et.getText().toString().trim().equalsIgnoreCase(admin.maadi)) {
      userBranch = branches[1];
      branchOrder = 1;
      return true;
    } else if (email_et.getText().toString().trim().equalsIgnoreCase(admin.faisal)) {
      userBranch = branches[2];
      branchOrder = 2;
      return true;
    } else if (email_et.getText().toString().trim().equalsIgnoreCase(admin.mnasr)) {
      userBranch = branches[3];
      branchOrder = 3;
      return true;
    } else if (email_et.getText().toString().trim().equalsIgnoreCase(admin.msrelgdida)) {
      userBranch = branches[4];
      branchOrder = 4;
      return true;
    } else if (email_et.getText().toString().trim().equalsIgnoreCase(admin.october)) {
      userBranch = branches[5];
      branchOrder = 5;
      return true;
    } else if (email_et.getText().toString().trim().equalsIgnoreCase(admin.helwan)) {
      userBranch = branches[6];
      branchOrder = 6;
      return true;
    } else if (email_et.getText().toString().trim().equalsIgnoreCase(admin.alex)) {
      userBranch = branches[7];
      branchOrder = 7;
      return true;
    } else if (email_et.getText().toString().trim().equalsIgnoreCase(admin.mokattam)) {
      userBranch = branches[8];
      branchOrder = 8;
      return true;
    } else if (email_et.getText().toString().trim().equalsIgnoreCase(admin.mrkzy)) {
      isMrkzy = true;
      userBranch = branches[9];
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
