package com.resala.mosharkaty;

import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.resala.mosharkaty.utility.classes.Takyeem;
import com.resala.mosharkaty.utility.classes.Volunteer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

import static android.content.ContentValues.TAG;
import static com.resala.mosharkaty.LoginActivity.userBranch;
import static com.resala.mosharkaty.MessagesReadActivity.isManager;
import static com.resala.mosharkaty.NewAccountActivity.branches;
import static com.resala.mosharkaty.SplashActivity.myRules;
import static com.resala.mosharkaty.StarterActivity.branchesSheets;

public class AdminShowTakyeemActivity extends AppCompatActivity {
    Spinner fari2Spinner;
    DatabaseReference fari2Ref;
    FirebaseDatabase database;
    ArrayList<String> allFari2 = new ArrayList<>();
    int big_total;
    int last_month;
    int the_month_before;
    int this_month;
    ValueEventListener Takyeemlistener;
    DatabaseReference takyeemTab;
    int thisMonthName;
    Calendar cldr;
    boolean takyeemFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_show_takyeem);
        fari2Spinner = findViewById(R.id.takyeemVolName);
        database = FirebaseDatabase.getInstance();

        String branchSheetLink =
                userBranch.equals(branches[9])
                        ? branchesSheets.get(branches[0])
                        : branchesSheets.get(userBranch);
        assert branchSheetLink != null;
        DatabaseReference liveSheet = database.getReference(branchSheetLink);
        fari2Ref = liveSheet.child("month_mosharkat");
        final ArrayAdapter<String> ae =
                new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, allFari2);
        ae.setDropDownViewResource(R.layout.spinner_dropdown);
        fari2Spinner.setAdapter(ae);
        fari2Ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        allFari2.clear();

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Volunteer user = snapshot.getValue(Volunteer.class);
                            assert user != null;
                            allFari2.add(user.Volname.trim());
                        }
                        Collections.sort(allFari2); // alphabetical
                        ae.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Failed to read value
                        Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });
    }

    public void refreshTakyeem(View view) {
        final TextView communication = findViewById(R.id.communication);
        final TextView commitment = findViewById(R.id.commitment);
        final TextView problem_solving = findViewById(R.id.problem_solving);
        final TextView quality = findViewById(R.id.quality);
        final TextView creativity = findViewById(R.id.creativity);

        final TextView cooperation = findViewById(R.id.cooperation);
        final TextView ekhtlat = findViewById(R.id.ekhtlat);
        final TextView respect = findViewById(R.id.respect);
        final TextView humble = findViewById(R.id.humble);
        final TextView loyalty = findViewById(R.id.loyalty);
        final TextView execuses = findViewById(R.id.execues);

        final TextView totalTechTV = findViewById(R.id.totalTechTV);
        final TextView totalPersonalTV = findViewById(R.id.totalPersonalTV);
        final TextView head_bonus = findViewById(R.id.bonusHead);

        final TextView extra_comment = findViewById(R.id.extraComment);

        final TextView thisNameTV = findViewById(R.id.thisNameTV);
        final TextView LastNameTV = findViewById(R.id.LastNameTV);
        final TextView beforeLastNameTV = findViewById(R.id.beforeLastNameTV);
        final TextView thisCountTV = findViewById(R.id.thisCountTV);
        final TextView LastCountTV = findViewById(R.id.LastCountTV);
        final TextView beforeLastCountTV = findViewById(R.id.beforeLastCountTV);
        if (!isManager) {
            Toast.makeText(
                    getApplicationContext(),
                    "illegal action : متقدرش تشوف التقييمات الا لما تدحل كلمة السر الاضافية صح",
                    Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        DatabaseReference liveSheet =
                database.getReference(
                        "1VuTdZ3el0o94Y9wqec0y90yxVG4Ko7PHTtrOm2EViOk"); // TODO :: شيت تقييمات لكل فرع
        takyeemTab = liveSheet.child("takyeem");
        Takyeemlistener =
                takyeemTab.addValueEventListener(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    Takyeem takyeem = snapshot.getValue(Takyeem.class);
                                    assert takyeem != null;
                                    if (takyeem.Volname.equalsIgnoreCase(fari2Spinner.getSelectedItem().toString())) {
                                        communication.setText(String.valueOf(takyeem.communication));
                                        commitment.setText(String.valueOf(takyeem.commitment));
                                        problem_solving.setText(String.valueOf(takyeem.problem_solving));
                                        quality.setText(String.valueOf(takyeem.quality));
                                        creativity.setText(String.valueOf(takyeem.creativity));
                                        cooperation.setText(String.valueOf(takyeem.cooperation));
                                        ekhtlat.setText(String.valueOf(takyeem.ekhtlat));
                                        respect.setText(String.valueOf(takyeem.respect));
                                        humble.setText(String.valueOf(takyeem.humble));
                                        loyalty.setText(String.valueOf(takyeem.loyalty));
                                        execuses.setText(String.valueOf(takyeem.execuses));
                                        totalTechTV.setText(String.valueOf(takyeem.total_technical));
                                        totalPersonalTV.setText(String.valueOf(takyeem.total_personal));
                                        head_bonus.setText(String.valueOf(takyeem.head_bonus));
                                        extra_comment.setText(String.valueOf(takyeem.extra_comment));
                                        big_total = takyeem.big_total;
                                        last_month = takyeem.last_month;
                                        the_month_before = takyeem.the_month_before;
                                        this_month = takyeem.this_month;

                                        cldr = Calendar.getInstance(Locale.US);
                                        thisMonthName = cldr.get(Calendar.MONTH); // تقييم الشهر اللي فات
                                        int lastMonthName = thisMonthName - 1;
                                        int beforeLastMonthName = thisMonthName - 2;
                                        // special cases correction
                                        switch (thisMonthName) {
                                            case 2:
                                                beforeLastMonthName = 12;
                                                break;
                                            case 1:
                                                beforeLastMonthName = 11;
                                                lastMonthName = 12;
                                                break;
                                        }
                                        thisNameTV.setText(String.valueOf(thisMonthName));
                                        LastNameTV.setText(String.valueOf(lastMonthName));
                                        beforeLastNameTV.setText(String.valueOf(beforeLastMonthName));
                                        thisCountTV.setText(String.valueOf(this_month));
                                        LastCountTV.setText(String.valueOf(last_month));
                                        beforeLastCountTV.setText(String.valueOf(the_month_before));
                                        takyeemFound = true;
                                        break;
                                    }
                                    if (!takyeemFound) {
                                        extra_comment.setText("التقييم غير موجود في الشيت");
                                        extra_comment.setTextColor(getResources().getColor(R.color.red));
                                        extra_comment.setTextSize(TypedValue.COMPLEX_UNIT_SP, myRules.big_font);
                                    } else {
                                        extra_comment.setTextColor(getResources().getColor(R.color.new_text_blue));
                                        extra_comment.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Failed to read value
                                Log.w(TAG, "Failed to read value.", error.toException());
                            }
                        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (takyeemTab != null && Takyeemlistener != null) {
            takyeemTab.removeEventListener(Takyeemlistener);
        }
    }
}
