package com.resala.mosharkaty;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.resala.mosharkaty.utility.classes.EventReport;
import com.resala.mosharkaty.utility.classes.Meeting;
import com.resala.mosharkaty.utility.classes.MosharkaItem;
import com.resala.mosharkaty.utility.classes.NasheetVolunteer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.resala.mosharkaty.LoginActivity.userBranch;
import static com.resala.mosharkaty.SplashActivity.myRules;
import static com.resala.mosharkaty.fragments.AdminShowMosharkatFragment.days;
import static com.resala.mosharkaty.fragments.AdminShowMosharkatFragment.months;
import static com.resala.mosharkaty.fragments.HomeFragment.teamDegrees;

public class AdminWeeklyReportActivity extends AppCompatActivity {
    Spinner month_et;
    Spinner day_from_et;
    Spinner day_to_et;
    int day;
    int month;
    int year;
    FirebaseDatabase database;
    DatabaseReference MosharkatRef;
    DatabaseReference EventsRef;
    DatabaseReference MeetingsRef;
    DatabaseReference nasheetRef;
    int selected_month;
    int start_day;
    int end_day;
    ArrayList<String> allNsheet = new ArrayList<>();
    int nasheetCounter;
    int mas2oleenCounter;
    int msharee3Counter;
    int mas2oleenMosharkat;
    int msharee3Mosharkat;
    int nasheetMosharkat;
    int normalMosharkat;
    int noobsMosharkat;
    int noobsMosharkat8;
    int mas2oleenMosharkat8;
    int msharee3Mosharkat8;
    int nasheetMosharkat8;
    int normalMosharkat8;

    int da5el_2_Mosharkat;
    int da5el_aboveMosharkat;
    int otherMosharkat;

    int mas2oleenArrived;
    int msharee3Arrived;
    int nasheetArrived;
    int normalArrived;
    int noobsArrived;

    Button refresh_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_weekly_report);
        database = FirebaseDatabase.getInstance();

        TableLayout eventsTable = findViewById(R.id.eventsTable);
        eventsTable.setVisibility(myRules.show_dummy_report ? View.VISIBLE : View.GONE);
        month_et = findViewById(R.id.current_month);
        day_from_et = findViewById(R.id.day_from);
        day_to_et = findViewById(R.id.day_to);
        refresh_btn = findViewById(R.id.refresh_btn);
        refresh_btn.setEnabled(false);
        refresh_btn.setBackgroundColor(
                getResources().getColor(R.color.common_google_signin_btn_text_light_disabled));
        final Calendar cldr = Calendar.getInstance(Locale.US);
        day = cldr.get(Calendar.DAY_OF_MONTH);
        month = cldr.get(Calendar.MONTH);
        year = cldr.get(Calendar.YEAR);

        // setting spinner
        ArrayAdapter<String> aa =
                new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, months);
        aa.setDropDownViewResource(R.layout.spinner_dropdown);
        // Setting the ArrayAdapter data on the Spinner
        month_et.setAdapter(aa);
        month_et.setSelection(Math.max(month, 0));

        ArrayAdapter<String> ab =
                new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, days);
        ab.setDropDownViewResource(R.layout.spinner_dropdown);
        // Setting the ArrayAdapter data on the Spinner
        day_to_et.setAdapter(ab);
        day_to_et.setSelection(Math.max(day - 1, 0));

        ArrayAdapter<String> ac =
                new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, days);
        ac.setDropDownViewResource(R.layout.spinner_dropdown);
        // Setting the ArrayAdapter data on the Spinner
        day_from_et.setAdapter(ac);
        day_from_et.setSelection(0);
        if (userBranch == null) {
            Toast.makeText(getApplicationContext(), "an error occurred .. try again", Toast.LENGTH_SHORT)
                    .show();
            finish();
        } else {
            refresh_btn.setEnabled(true);
            refresh_btn.setBackgroundResource(R.drawable.btn_gradient_blue);

            mas2oleenCounter = 0;
            msharee3Counter = 0;
            for (Map.Entry<String, String> entry : teamDegrees.entrySet()) {
                String degree = teamDegrees.get(entry.getKey());
                assert degree != null;
                if (degree.equals("مسؤول")) mas2oleenCounter++;
                else if (degree.equals("مشروع")) msharee3Counter++;
            }
            TextView mas2olCount = findViewById(R.id.mas2olCount);
            mas2olCount.setText(String.valueOf(mas2oleenCounter));
            TextView mshro3Count = findViewById(R.id.mshro3Count);
            mshro3Count.setText(String.valueOf(msharee3Counter));
        }
    }

    public void refreshReport(View view) {
        selected_month = Integer.parseInt(month_et.getSelectedItem().toString());
        start_day = Integer.parseInt(day_from_et.getSelectedItem().toString());
        end_day = Integer.parseInt(day_to_et.getSelectedItem().toString());

        getNashitData(); //and mosharkat
        updateMeetings();
        updateEvents();
    }

    private void getNashitData() {
        nasheetRef = database.getReference("nasheet").child(userBranch);
        nasheetRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        allNsheet.clear();
                        nasheetCounter = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            NasheetVolunteer nasheetVolunteer = snapshot.getValue(NasheetVolunteer.class);
                            assert nasheetVolunteer != null;
                            String[] parts = nasheetVolunteer.first_month.split("/", 2);
                            int months =
                                    (year - Integer.parseInt(parts[1])) * 12
                                            + (selected_month - Integer.parseInt(parts[0]));
                            if (months > 0) {
                                allNsheet.add(snapshot.getKey());
                                nasheetCounter++;
                            }
                        }
                        TextView nasheetCount = findViewById(R.id.nasheetCount);
                        nasheetCount.setText(String.valueOf(nasheetCounter));
                        updateMosharkat();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Failed to read value
                        Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });
    }

    private void updateMosharkat() {
        MosharkatRef = database.getReference("mosharkat").child(userBranch);
        MosharkatRef.child(String.valueOf(selected_month))
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                HashMap<String, Integer> nameCounting = new HashMap<>();
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    MosharkaItem mosharka = snapshot.getValue(MosharkaItem.class);
                                    String[] splittedDate;
                                    if (mosharka != null) {
                                        splittedDate = mosharka.getMosharkaDate().split("/", 3);
                                        if (Integer.parseInt(splittedDate[0]) >= start_day
                                                && Integer.parseInt(splittedDate[0]) <= end_day) {
                                            // get all mosharkat for everyone firstly
                                            if (nameCounting.containsKey(mosharka.getVolname().trim())) {
                                                // If char is present in charCountMap,
                                                // incrementing it's count by 1
                                                nameCounting.put(
                                                        mosharka.getVolname().trim(),
                                                        nameCounting.get(mosharka.getVolname().trim()) + 1);
                                            } else {
                                                // If char is not present in charCountMap,
                                                // putting this char to charCountMap with 1 as it's value
                                                nameCounting.put(mosharka.getVolname().trim(), 1);
                                            }
                                        }
                                    }
                                }
                                divideMosharkatByDegree(nameCounting);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Failed to read value
                                Log.w(TAG, "Failed to read value.", error.toException());
                            }
                        });
    }

    private void divideMosharkatByDegree(HashMap<String, Integer> nameCounting) {
        mas2oleenMosharkat8 = 0;
        msharee3Mosharkat8 = 0;
        nasheetMosharkat8 = 0;
        normalMosharkat8 = 0;
        noobsMosharkat8 = 0;

        mas2oleenMosharkat = 0;
        msharee3Mosharkat = 0;
        nasheetMosharkat = 0;
        normalMosharkat = 0;
        noobsMosharkat = 0;

        mas2oleenArrived = 0;
        msharee3Arrived = 0;
        nasheetArrived = 0;
        normalArrived = 0;
        noobsArrived = 0;

        da5el_2_Mosharkat = 0;
        da5el_aboveMosharkat = 0;
        otherMosharkat = 0;

        for (Map.Entry<String, Integer> entry : nameCounting.entrySet()) {
            String volName = entry.getKey();

            if (teamDegrees.containsKey(volName)) {
                if (teamDegrees.get(volName).equals("مسؤول")) {
                    mas2oleenMosharkat += entry.getValue();
                    mas2oleenMosharkat8 += Math.min(8, entry.getValue());
                    mas2oleenArrived++;
                } else if (teamDegrees.get(volName).equals("مشروع")) {
                    msharee3Mosharkat += entry.getValue();
                    msharee3Mosharkat8 += Math.min(8, entry.getValue());
                    msharee3Arrived++;
                } else if (teamDegrees.get(volName).contains("داخل")) {
                    String[] splittedDegree = teamDegrees.get(volName).split("&", 2);
                    int tempMonth = myRules.prev_months_allowed ? selected_month - (month + 1) : 0;

                    switch (Integer.parseInt(splittedDegree[1]) - tempMonth) {
                        case 0:
                            noobsMosharkat += entry.getValue();
                            noobsMosharkat8 += Math.min(8, entry.getValue());
                            noobsArrived++;
                            break;
                        case 1:
                            da5el_2_Mosharkat += Math.min(8, entry.getValue());
                            break;
                        default:
                            if (Integer.parseInt(splittedDegree[1]) >= 8 && myRules.ignoreAbove)
                                break;
                            da5el_aboveMosharkat += Math.min(8, entry.getValue());
                            break;
                    }

                    if (Integer.parseInt(splittedDegree[1]) - tempMonth > 0 && allNsheet.contains(volName)) {
                        nasheetMosharkat += entry.getValue();
                        nasheetMosharkat8 += Math.min(8, entry.getValue());
                        nasheetArrived++;
                    } else {
                        normalMosharkat += entry.getValue();
                        normalMosharkat8 += Math.min(8, entry.getValue());
                        normalArrived++;
                    }
                }
            } else { //خارج متابعة
                otherMosharkat += Math.min(8, entry.getValue());
            }
        }
        updatePoints();
        // write in table
        TextView mas2olAttended = findViewById(R.id.mas2olAttended);
        TextView mshro3Attended = findViewById(R.id.mshro3Attended);
        TextView nasheetAttended = findViewById(R.id.nasheetAttended);
        TextView normalAttended = findViewById(R.id.normalAttended);
        TextView noobsAttended = findViewById(R.id.noobsAttended);

        mas2olAttended.setText(String.valueOf(mas2oleenArrived));
        mshro3Attended.setText(String.valueOf(msharee3Arrived));
        nasheetAttended.setText(String.valueOf(nasheetArrived));
        normalAttended.setText(String.valueOf(normalArrived));
        noobsAttended.setText(String.valueOf(noobsArrived));
        /** ************************************************************************************* */
        TextView mas2olPercent = findViewById(R.id.mas2olPercent);
        TextView mshro3Percent = findViewById(R.id.mshro3Percent);
        TextView nasheetPercent = findViewById(R.id.nasheetPercent);

        float percent1 = (float) mas2oleenArrived / mas2oleenCounter * 100;
        float percent2 = (float) msharee3Arrived / msharee3Counter * 100;
        float percent3 = (float) nasheetArrived / nasheetCounter * 100;

        mas2olPercent.setText(Math.round(percent1 * 10) / 10.0 + " %");
        mshro3Percent.setText(Math.round(percent2 * 10) / 10.0 + " %");
        nasheetPercent.setText(Math.round(percent3 * 10) / 10.0 + " %");
        /** ************************************************************************************* */
        TextView mas2olMosharkat = findViewById(R.id.mas2olMosharkat);
        TextView mshro3Mosharkat = findViewById(R.id.mshro3Mosharkat);
        TextView nasheetMosharkatTV = findViewById(R.id.nasheetMosharkat);
        TextView normalMosharkatTV = findViewById(R.id.normalMosharkat);
        TextView noobsMosharkatTV = findViewById(R.id.noobsMosharkat);

        mas2olMosharkat.setText(String.valueOf(mas2oleenMosharkat));
        mshro3Mosharkat.setText(String.valueOf(msharee3Mosharkat));
        nasheetMosharkatTV.setText(String.valueOf(nasheetMosharkat));
        normalMosharkatTV.setText(String.valueOf(normalMosharkat));
        noobsMosharkatTV.setText(String.valueOf(noobsMosharkat));
        /** ************************************************************************************* */
        TextView mas2olAverage = findViewById(R.id.mas2olAverage);
        TextView mshro3Average = findViewById(R.id.mshro3Average);
        TextView nasheetAverage = findViewById(R.id.nasheetAverage);
        TextView normalAverage = findViewById(R.id.normalAverage);
        TextView noobsAverage = findViewById(R.id.noobsAverage);

        float avg1 = (float) mas2oleenMosharkat / mas2oleenArrived;
        float avg2 = (float) msharee3Mosharkat / msharee3Arrived;
        float avg3 = (float) nasheetMosharkat / nasheetArrived;
        float avg4 = (float) normalMosharkat / normalArrived;
        float avg5 = (float) noobsMosharkat / noobsArrived;

        mas2olAverage.setText(String.valueOf(Math.round(avg1 * 10) / 10.0));
        mshro3Average.setText(String.valueOf(Math.round(avg2 * 10) / 10.0));
        nasheetAverage.setText(String.valueOf(Math.round(avg3 * 10) / 10.0));
        normalAverage.setText(String.valueOf(Math.round(avg4 * 10) / 10.0));
        noobsAverage.setText(String.valueOf(Math.round(avg5 * 10) / 10.0));

        /** ************************************************************************************* */
        TextView mas2olAverage8 = findViewById(R.id.mas2olAverage8);
        TextView mshro3Average8 = findViewById(R.id.mshro3Average8);
        TextView nasheetAverage8 = findViewById(R.id.nasheetAverage8);
        TextView normalAverage8 = findViewById(R.id.normalAverage8);
        TextView noobsAverage8 = findViewById(R.id.noobsAverage8);

        float avg81 = (float) mas2oleenMosharkat8 / mas2oleenArrived;
        float avg82 = (float) msharee3Mosharkat8 / msharee3Arrived;
        float avg83 = (float) nasheetMosharkat8 / nasheetArrived;
        float avg84 = (float) normalMosharkat8 / normalArrived;
        float avg85 = (float) noobsMosharkat8 / noobsArrived;

        mas2olAverage8.setText(String.valueOf(Math.round(avg81 * 10) / 10.0));
        mshro3Average8.setText(String.valueOf(Math.round(avg82 * 10) / 10.0));
        nasheetAverage8.setText(String.valueOf(Math.round(avg83 * 10) / 10.0));
        normalAverage8.setText(String.valueOf(Math.round(avg84 * 10) / 10.0));
        noobsAverage8.setText(String.valueOf(Math.round(avg85 * 10) / 10.0));
    }

    private void updatePoints() {
        TextView points = findViewById(R.id.points);
        int pointsCalculated =
                msharee3Mosharkat8 * myRules.mashroo3_points + myRules.mas2ool_points * mas2oleenMosharkat8;
        int otherPointsCalculated = otherMosharkat + noobsMosharkat8 + da5el_2_Mosharkat * 2 + da5el_aboveMosharkat * 3;
        points.setText(String.valueOf(pointsCalculated + otherPointsCalculated));
    }

    private void updateEvents() {
        TextView eventsCount = findViewById(R.id.eventsCount);
        TextView orientationCount = findViewById(R.id.orientationCount);
        TextView coursesCount = findViewById(R.id.coursesCount);
        EventsRef = database.getReference("reports").child(userBranch);
        EventsRef.child(String.valueOf(selected_month))
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                int EventsCounter = 0;
                                int OrientationCounter = 0;
                                int CoursesCounter = 0;
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    EventReport event = snapshot.getValue(EventReport.class);
                                    if (event != null) {
                                        String[] splittedDate = event.date.split("/", 2);
                                        if (Integer.parseInt(splittedDate[0]) >= start_day
                                                && Integer.parseInt(splittedDate[0]) <= end_day) {
                                            if (event.type.matches("(.*)اورينتيشن(.*)"))
                                                OrientationCounter++;
                                            else if (event.type.matches("(.*)سيشن(.*)"))
                                                CoursesCounter++;
                                            else EventsCounter++;
                                        }
                                    }
                                }
                                eventsCount.setText(String.valueOf(EventsCounter));
                                orientationCount.setText(String.valueOf(OrientationCounter));
                                coursesCount.setText(String.valueOf(CoursesCounter));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Failed to read value
                                Log.w(TAG, "Failed to read value.", error.toException());
                            }
                        });
    }

    private void updateMeetings() {
        MeetingsRef = database.getReference("meetings").child(userBranch);
        TextView meetingsCount = findViewById(R.id.meetingsCount);
        MeetingsRef.child(month_et.getSelectedItem().toString())
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                int meetingsCounter = 0;
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    Meeting meet = snapshot.getValue(Meeting.class);
                                    if (meet != null) {
                                        String[] splittedDate = meet.date.split("/", 2);
                                        if (Integer.parseInt(splittedDate[0]) >= start_day
                                                && Integer.parseInt(splittedDate[0]) <= end_day) {
                                            meetingsCounter++;
                                        }
                                    }
                                }
                                meetingsCount.setText(String.valueOf(meetingsCounter));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Failed to read value
                                Log.w(TAG, "Failed to read value.", error.toException());
                            }
                        });
    }
}
