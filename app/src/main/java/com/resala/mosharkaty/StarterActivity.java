package com.resala.mosharkaty;

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

import java.util.HashMap;

import static com.resala.mosharkaty.NewAccountActivity.branches;
import static com.resala.mosharkaty.SplashActivity.myRules;

public class StarterActivity extends AppCompatActivity {
    public static HashMap<String, String> branchesSheets = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starter);
        try {
            fillSheets();
            checkUpdate();
        } catch (Exception ignored) {
            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void fillSheets() {
        branchesSheets.put(branches[0], "1tsMZ5EwtKrBUGuLFVBvuwpU5ve0JKMsaqK1nNAONj-0"); // مهندسين
        branchesSheets.put(branches[1], "1IzyuwMrKap0uQutEKM0qcRpL3MlBxfd2tLv9jZ1M32o"); // معادي
        branchesSheets.put(branches[2], "1twEafdr_bJgXoCmM26cQHZlPuCkYwntr8LJ5pPxreOI"); // فيصل
        branchesSheets.put(branches[3], "1x_yXMc32YxG62C_l1Bqis4Sjdy_PH2iGiAmL6w3DoTA"); // مدينة نصر
//        branchesSheets.put(branches[4], "1h8ApI25VevdkmRj_bRoYUwnbSdlGnm5lAVXpQlVte9Y"); // مصر الجديدة
        branchesSheets.put(branches[4], "1wramn0b32bE_tvW1VaT98vJ7_xDlQ22djEsRja_wzjc"); // مصر الجديدة
        branchesSheets.put(branches[5], "1h6JOptK1SRHelRIHV-iqp1S3YhjdG8hHHLi0-phVulY"); // اكتوبر
        branchesSheets.put(branches[6], "1o4MSMw7ip0BUNLkx5t0WOZrPM9SyNYsty6jJ4mtkyRo"); // حلوان
        branchesSheets.put(branches[7], "1U_F192CWif54Q9mWcYgPKM7662jCBXCU_COLPp0AaPQ"); // اسكندرية
        branchesSheets.put(branches[8], "1UrPaZyzfh7Vag_Cxb6tHkBXMKdHVThvDxya1SQYeKqI"); // المقطم
        // مركزي ملوش شيت
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
        startActivity(new Intent(this, NewAccountActivity.class));
    }

    public void loginStarter(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }
}
