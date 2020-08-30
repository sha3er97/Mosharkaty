package com.resala.mosharkaty;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.ContentValues.TAG;

public class Starter extends AppCompatActivity {
    DatabaseReference rulesRef;
    public static Rules myRules;
    FirebaseDatabase database;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starter);
        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("لحظات معانا...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
        database = FirebaseDatabase.getInstance();
        rulesRef = database.getReference("Rules");
        rulesRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        myRules = dataSnapshot.getValue(Rules.class);
                        // To dismiss the dialog
                        progress.dismiss();
                        checkUpdate();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Failed to read value
                        Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });
    }

    private void checkUpdate() {
        TextView versionTV = findViewById(R.id.version);
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            int verCode = pInfo.versionCode;
            versionTV.setText("version : " + version + " (" + verCode + ")");
            if (verCode < myRules.last_important_update) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

                alertDialogBuilder.setTitle(getString(R.string.youAreNotUpdatedTitle));
                alertDialogBuilder.setMessage(
                        getString(R.string.youAreNotUpdatedMessage)
                                + " "
                                + myRules.last_important_update
                                + getString(R.string.youAreNotUpdatedMessage1));
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setPositiveButton(
                        R.string.update,
                        (dialog, id) -> {
                            startActivity(
                                    new Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse(
                                                    "https://play.google.com/store/apps/details?id=com.resala.mosharkaty")));
                            dialog.cancel();
                        });
                alertDialogBuilder.show();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void newAccStarter(View view) {
        startActivity(new Intent(this, NewAccount.class));
    }

    public void loginStarter(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }
}
