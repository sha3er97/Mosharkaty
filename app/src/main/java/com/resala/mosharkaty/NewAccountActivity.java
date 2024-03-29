package com.resala.mosharkaty;

import static com.resala.mosharkaty.utility.classes.UtilityClass.BRANCHES_COUNT;
import static com.resala.mosharkaty.utility.classes.UtilityClass.branches;
import static com.resala.mosharkaty.utility.classes.UtilityClass.userId;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.resala.mosharkaty.utility.classes.User;
import com.resala.mosharkaty.utility.classes.UtilityClass;

public class NewAccountActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener {
    private FirebaseAuth mAuth;
    EditText email_et;
    EditText password_et;
    EditText name_et;
    EditText code_et;
    FirebaseDatabase database;
    Spinner spin;
    ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account);
        mAuth = FirebaseAuth.getInstance();
        email_et = findViewById(R.id.newAccountEmail);
        password_et = findViewById(R.id.newAccountPass);
        name_et = findViewById(R.id.newAccountName);
        code_et = findViewById(R.id.newAccountCode);
        logo = findViewById(R.id.logo);

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
        name_et.setOnFocusChangeListener(
                (view, hasFocus) -> {
                    if (hasFocus) {
                        logo.setVisibility(View.GONE);
                    } else {
                        logo.setVisibility(View.VISIBLE);
                    }
                });
        code_et.setOnFocusChangeListener(
                (view, hasFocus) -> {
                    if (hasFocus) {
                        logo.setVisibility(View.GONE);
                    } else {
                        logo.setVisibility(View.VISIBLE);
                    }
                });

        database = FirebaseDatabase.getInstance();
        spin = findViewById(R.id.branchSpinner);
        spin.setOnItemSelectedListener(this);
        // Creating the ArrayAdapter instance having the country list
        ArrayAdapter<String> aa = new ArrayAdapter<>(this, R.layout.spinner_item, branches);
        aa.setDropDownViewResource(R.layout.spinner_dropdown);
        // Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);
    }

    private void createAccount(String email, String password) {
        if (!validateForm()) {
            return;
        }
        mAuth
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                        this,
                        task -> {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                //                  task.getException().printStackTrace();
                                Toast.makeText(
                                                getApplicationContext(),
                                                "Authentication failed ,maybe used email or weak password ",
                                                Toast.LENGTH_SHORT)
                                        .show();
                                updateUI(null);
                            }
                        });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            userId = user.getUid();
            DatabaseReference usersRef = database.getReference("users");
            DatabaseReference currentUser = usersRef.child(userId);
            currentUser.setValue(
                    new User(
                            spin.getSelectedItem().toString(),
                            code_et.getText().toString().trim(),
                            name_et.getText().toString().trim()));

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
        } else if (words.length < 3) {
            name_et.setError("الاسم لازم يبقي ثلاثي علي الاقل.");
            valid = false;
        } else {
            name_et.setError(null);
        }
        String code = code_et.getText().toString();
        if (TextUtils.isEmpty(code)) {
            code_et.setError("Required.");
            valid = false;
        } else if (code_et.getText().length() < UtilityClass.userCodeLength) {
            code_et.setError("incorrect code entered");
            valid = false;
        } else {
            code_et.setError(null);
        }

        String branch = spin.getSelectedItem().toString();
        TextView errorText = (TextView) spin.getSelectedView();
        if (branch.equals(branches[BRANCHES_COUNT])) {
            errorText.setError("المتطوع لازم يكون في فرع من ال9 الاساسيين فقط");
            valid = false;
        } else {
            errorText.setError(null);
        }

        return valid;
    }

    public void newAccountClick(View view) {
        if (checkConnectivity(view)) {
            createAccount(email_et.getText().toString(), password_et.getText().toString());
        }
    }

    public boolean checkConnectivity(View view) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            if (connectivityManager.getActiveNetworkInfo() == null
                    || !connectivityManager.getActiveNetworkInfo().isConnected()) {
                //          Toast.makeText(getApplicationContext(), "No Internet",
                // Toast.LENGTH_SHORT).show();
                Snackbar.make(view, "No Internet", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                return false;
            }
        }
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }
}
