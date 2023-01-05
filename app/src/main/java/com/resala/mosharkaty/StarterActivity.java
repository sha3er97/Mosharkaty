package com.resala.mosharkaty;

import static com.resala.mosharkaty.utility.classes.UtilityClass.myRules;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.resala.mosharkaty.utility.classes.UtilityClass;


public class StarterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starter);
        try {
            checkUpdate();
        } catch (Exception ignored) {
            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            finish();
        }
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
                                + " "
                                + getString(R.string.youAreNotUpdatedMessage1));
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setPositiveButton(
                        R.string.update,
                        (dialog, id) -> {
                            startActivity(
                                    new Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse(UtilityClass.playStoreLink
                                            )));
                            dialog.cancel();
                        });
                alertDialogBuilder.show();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void newAccStarter(View view) {
        startActivity(new Intent(this, NewAccountActivity.class));
    }

    public void loginStarter(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }
}
