package com.resala.mosharkaty;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.resala.mosharkaty.utility.classes.EventReport;
import com.resala.mosharkaty.utility.classes.Meeting;
import com.resala.mosharkaty.utility.classes.MosharkaItem;
import com.resala.mosharkaty.utility.classes.NasheetVolunteer;
import com.resala.mosharkaty.utility.classes.normalVolunteer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import static android.content.ContentValues.TAG;
import static com.resala.mosharkaty.NewAccountActivity.branches;
import static com.resala.mosharkaty.SplashActivity.myRules;
import static com.resala.mosharkaty.StarterActivity.branchesSheets;
import static com.resala.mosharkaty.fragments.AdminShowMosharkatFragment.REQUEST;
import static com.resala.mosharkaty.fragments.AdminShowMosharkatFragment.days;
import static com.resala.mosharkaty.fragments.AdminShowMosharkatFragment.months;

public class AdminMrkzyReportsActivity extends AppCompatActivity {
    private static final int BRANCHES_COUNT = 9;
    Spinner month_et;
    Spinner day_from_et;
    Spinner day_to_et;
    int day;
    int month;
    int year;
    FirebaseDatabase database;
    int selected_month;
    int start_day;
    int end_day;

    Button refresh_btn;
    int[] mas2oleenCounter = new int[9];
    int[] msharee3Counter = new int[9];
    int[] nasheetCounter = new int[9];

    WritableWorkbook workbook;

    int branchIterator;
    ArrayList<HashMap<String, String>> teamDegrees = new ArrayList<>();
    ArrayList<ArrayList<String>> allNsheet = new ArrayList<>();
    TextView[] mas2olCount = new TextView[9];
    TextView[] mshro3Count = new TextView[9];
    TextView[] nasheetCount = new TextView[9];
    TextView[] noobsCount = new TextView[9];
    TextView[] normalCount = new TextView[9];

    TextView[] meetingsCount = new TextView[9];
    TextView[] eventsCount = new TextView[9];
    TextView[] orientationCount = new TextView[9];
    TextView[] coursesCount = new TextView[9];

    TextView[] mas2olPercent = new TextView[9];
    TextView[] mshro3Percent = new TextView[9];
    TextView[] nasheetPercent = new TextView[9];

    TextView[] mas2olMosharkat = new TextView[9];
    TextView[] mshro3Mosharkat = new TextView[9];
    TextView[] nasheetMosharkatTV = new TextView[9];
    TextView[] noobsMosharkatTV = new TextView[9];
    TextView[] normalMosharkatTV = new TextView[9];

    TextView[] mas2olAverage = new TextView[9];
    TextView[] mshro3Average = new TextView[9];
    TextView[] nasheetAverage = new TextView[9];
    TextView[] noobsAverage = new TextView[9];
    TextView[] normalAverage = new TextView[9];

    TextView[] points = new TextView[9];
    private boolean excelOut = false;
    private ProgressDialog progress;
    Button export_report_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_mrkzy_reports);
        database = FirebaseDatabase.getInstance();
        month_et = findViewById(R.id.current_month);
        day_from_et = findViewById(R.id.day_from);
        day_to_et = findViewById(R.id.day_to);
        refresh_btn = findViewById(R.id.refresh_btn);
        export_report_btn = findViewById(R.id.export_report_btn);

        getBranchesTables();
        initializeLists();
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

        for (branchIterator = 0; branchIterator < BRANCHES_COUNT; branchIterator++) {
            String branchSheetLink = branchesSheets.get(branches[branchIterator]);
            assert branchSheetLink != null;
            Log.d(TAG, "loop: branchIterator = " + branchIterator);
            getFari2Data(branchIterator, branchSheetLink);
        }
    }

    private void getFari2Data(int branchIterator, String branchSheetLink) {
        DatabaseReference liveSheet = database.getReference(branchSheetLink);
        DatabaseReference usersRef = liveSheet.child("all");
        usersRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d(TAG, "onDataChange: branchIterator = " + branchIterator);
                        mas2oleenCounter[branchIterator] = 0;
                        msharee3Counter[branchIterator] = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            normalVolunteer user = snapshot.getValue(normalVolunteer.class);
                            if (user != null) {
                                if (user.motabaa.equals("مشروع مسئول")) {
                                    teamDegrees.get(branchIterator).put(user.Volname, "مشروع");
                                    msharee3Counter[branchIterator]++;
                                } else if (user.motabaa.equals("مسئول")) {
                                    mas2oleenCounter[branchIterator]++;
                                    teamDegrees.get(branchIterator).put(user.Volname, "مسؤول");
                                } else if (user.motabaa.contains("داخل")) {
                                    teamDegrees.get(branchIterator).put(user.Volname, "داخل" + "&" + user.months_count);
                                }
                            }
                        }
                        mas2olCount[branchIterator].setText(String.valueOf(mas2oleenCounter[branchIterator]));
                        mshro3Count[branchIterator].setText(String.valueOf(msharee3Counter[branchIterator]));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Failed to read value
                        Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });
    }

    private void initializeLists() {
        for (branchIterator = 0; branchIterator < BRANCHES_COUNT; branchIterator++) {
            teamDegrees.add(new HashMap<>());
            allNsheet.add(new ArrayList<>());
        }
    }

    private void getBranchesTables() {
        // عدد مسؤول
        mas2olCount[0] = findViewById(R.id.mohMas2oolCountTV);
        mas2olCount[1] = findViewById(R.id.maadiMas2oolCountTV);
        mas2olCount[2] = findViewById(R.id.faisalMas2oolCountTV);
        mas2olCount[3] = findViewById(R.id.mnasrMas2oolCountTV);
        mas2olCount[4] = findViewById(R.id.melgdidaMas2oolCountTV);
        mas2olCount[5] = findViewById(R.id.octMas2oolCountTV);
        mas2olCount[6] = findViewById(R.id.helwanMas2oolCountTV);
        mas2olCount[7] = findViewById(R.id.alexMas2oolCountTV);
        mas2olCount[8] = findViewById(R.id.mokattamMas2oolCountTV);

        // عدد مشروع
        mshro3Count[0] = findViewById(R.id.mohMashroo3CountTV);
        mshro3Count[1] = findViewById(R.id.maadiMashroo3CountTV);
        mshro3Count[2] = findViewById(R.id.faisalMashroo3CountTV);
        mshro3Count[3] = findViewById(R.id.mnasrMashroo3CountTV);
        mshro3Count[4] = findViewById(R.id.melgdidaMashroo3CountTV);
        mshro3Count[5] = findViewById(R.id.octMashroo3CountTV);
        mshro3Count[6] = findViewById(R.id.helwanMashroo3CountTV);
        mshro3Count[7] = findViewById(R.id.alexMashroo3CountTV);
        mshro3Count[8] = findViewById(R.id.mokattamMashroo3CountTV);

        // عدد نشيط
        nasheetCount[0] = findViewById(R.id.mohNasheetCountTV);
        nasheetCount[1] = findViewById(R.id.maadiNasheetCountTV);
        nasheetCount[2] = findViewById(R.id.faisalNasheetCountTV);
        nasheetCount[3] = findViewById(R.id.mnasrNasheetCountTV);
        nasheetCount[4] = findViewById(R.id.melgdidaNasheetCountTV);
        nasheetCount[5] = findViewById(R.id.octNasheetCountTV);
        nasheetCount[6] = findViewById(R.id.helwanNasheetCountTV);
        nasheetCount[7] = findViewById(R.id.alexNasheetCountTV);
        nasheetCount[8] = findViewById(R.id.mokattamNasheetCountTV);

        // عدد جدد
        noobsCount[0] = findViewById(R.id.mohNoobsCountTV);
        noobsCount[1] = findViewById(R.id.maadiNoobsCountTV);
        noobsCount[2] = findViewById(R.id.faisalNoobsCountTV);
        noobsCount[3] = findViewById(R.id.mnasrNoobsCountTV);
        noobsCount[4] = findViewById(R.id.melgdidaNoobsCountTV);
        noobsCount[5] = findViewById(R.id.octNoobsCountTV);
        noobsCount[6] = findViewById(R.id.helwanNoobsCountTV);
        noobsCount[7] = findViewById(R.id.alexNoobsCountTV);
        noobsCount[8] = findViewById(R.id.mokattamNoobsCountTV);

        // عدد داخل المتابعة
        normalCount[0] = findViewById(R.id.mohNormalCountTV);
        normalCount[1] = findViewById(R.id.maadiNormalCountTV);
        normalCount[2] = findViewById(R.id.faisalNormalCountTV);
        normalCount[3] = findViewById(R.id.mnasrNormalCountTV);
        normalCount[4] = findViewById(R.id.melgdidaNormalCountTV);
        normalCount[5] = findViewById(R.id.octNormalCountTV);
        normalCount[6] = findViewById(R.id.helwanNormalCountTV);
        normalCount[7] = findViewById(R.id.alexNormalCountTV);
        normalCount[8] = findViewById(R.id.mokattamNormalCountTV);

        // عدد اجتماعات
        meetingsCount[0] = findViewById(R.id.mohMeetingsTV);
        meetingsCount[1] = findViewById(R.id.maadiMeetingsTV);
        meetingsCount[2] = findViewById(R.id.faisalMeetingsTV);
        meetingsCount[3] = findViewById(R.id.mnasrMeetingsTV);
        meetingsCount[4] = findViewById(R.id.melgdidaMeetingsTV);
        meetingsCount[5] = findViewById(R.id.octMeetingsTV);
        meetingsCount[6] = findViewById(R.id.helwanMeetingsTV);
        meetingsCount[7] = findViewById(R.id.alexMeetingsTV);
        meetingsCount[8] = findViewById(R.id.mokattamMeetingsTV);

        // عدد ايفنتات
        eventsCount[0] = findViewById(R.id.mohEventsTV);
        eventsCount[1] = findViewById(R.id.maadiEventsTV);
        eventsCount[2] = findViewById(R.id.faisalEventsTV);
        eventsCount[3] = findViewById(R.id.mnasrEventsTV);
        eventsCount[4] = findViewById(R.id.melgdidaEventsTV);
        eventsCount[5] = findViewById(R.id.octEventsTV);
        eventsCount[6] = findViewById(R.id.helwanEventsTV);
        eventsCount[7] = findViewById(R.id.alexEventsTV);
        eventsCount[8] = findViewById(R.id.mokattamEventsTV);

        orientationCount[0] = findViewById(R.id.mohOrientationTV);
        orientationCount[1] = findViewById(R.id.maadiOrientationTV);
        orientationCount[2] = findViewById(R.id.faisalOrientationTV);
        orientationCount[3] = findViewById(R.id.mnasrOrientationTV);
        orientationCount[4] = findViewById(R.id.melgdidaOrientationTV);
        orientationCount[5] = findViewById(R.id.octOrientationTV);
        orientationCount[6] = findViewById(R.id.helwanOrientationTV);
        orientationCount[7] = findViewById(R.id.alexOrientationTV);
        orientationCount[8] = findViewById(R.id.mokattamOrientationTV);

        coursesCount[0] = findViewById(R.id.mohSessionsTV);
        coursesCount[1] = findViewById(R.id.maadiSessionsTV);
        coursesCount[2] = findViewById(R.id.faisalSessionsTV);
        coursesCount[3] = findViewById(R.id.mnasrSessionsTV);
        coursesCount[4] = findViewById(R.id.melgdidaSessionsTV);
        coursesCount[5] = findViewById(R.id.octSessionsTV);
        coursesCount[6] = findViewById(R.id.helwanSessionsTV);
        coursesCount[7] = findViewById(R.id.alexSessionsTV);
        coursesCount[8] = findViewById(R.id.mokattamSessionsTV);

        // نسبة مشاركات
        mas2olPercent[0] = findViewById(R.id.mohMas2oolPercentTV);
        mas2olPercent[1] = findViewById(R.id.maadiMas2oolPercentTV);
        mas2olPercent[2] = findViewById(R.id.faisalMas2oolPercentTV);
        mas2olPercent[3] = findViewById(R.id.mnasrMas2oolPercentTV);
        mas2olPercent[4] = findViewById(R.id.melgdidaMas2oolPercentTV);
        mas2olPercent[5] = findViewById(R.id.octMas2oolPercentTV);
        mas2olPercent[6] = findViewById(R.id.helwanMas2oolPercentTV);
        mas2olPercent[7] = findViewById(R.id.alexMas2oolPercentTV);
        mas2olPercent[8] = findViewById(R.id.mokattamMas2oolPercentTV);

        mshro3Percent[0] = findViewById(R.id.mohMashroo3PercentTV);
        mshro3Percent[1] = findViewById(R.id.maadiMashroo3PercentTV);
        mshro3Percent[2] = findViewById(R.id.faisalMashroo3PercentTV);
        mshro3Percent[3] = findViewById(R.id.mnasrMashroo3PercentTV);
        mshro3Percent[4] = findViewById(R.id.melgdidaMashroo3PercentTV);
        mshro3Percent[5] = findViewById(R.id.octMashroo3PercentTV);
        mshro3Percent[6] = findViewById(R.id.helwanMashroo3PercentTV);
        mshro3Percent[7] = findViewById(R.id.alexMashroo3PercentTV);
        mshro3Percent[8] = findViewById(R.id.mokattamMashroo3PercentTV);

        nasheetPercent[0] = findViewById(R.id.mohNasheetPercentTV);
        nasheetPercent[1] = findViewById(R.id.maadiNasheetPercentTV);
        nasheetPercent[2] = findViewById(R.id.faisalNasheetPercentTV);
        nasheetPercent[3] = findViewById(R.id.mnasrNasheetPercentTV);
        nasheetPercent[4] = findViewById(R.id.melgdidaNasheetPercentTV);
        nasheetPercent[5] = findViewById(R.id.octNasheetPercentTV);
        nasheetPercent[6] = findViewById(R.id.helwanNasheetPercentTV);
        nasheetPercent[7] = findViewById(R.id.alexNasheetPercentTV);
        nasheetPercent[8] = findViewById(R.id.mokattamNasheetPercentTV);

        // عدد مشاركات
        mas2olMosharkat[0] = findViewById(R.id.mohMas2oolMosharkatTV);
        mas2olMosharkat[1] = findViewById(R.id.maadiMas2oolMosharkatTV);
        mas2olMosharkat[2] = findViewById(R.id.faisalMas2oolMosharkatTV);
        mas2olMosharkat[3] = findViewById(R.id.mnasrMas2oolMosharkatTV);
        mas2olMosharkat[4] = findViewById(R.id.melgdidaMas2oolMosharkatTV);
        mas2olMosharkat[5] = findViewById(R.id.octMas2oolMosharkatTV);
        mas2olMosharkat[6] = findViewById(R.id.helwanMas2oolMosharkatTV);
        mas2olMosharkat[7] = findViewById(R.id.alexMas2oolMosharkatTV);
        mas2olMosharkat[8] = findViewById(R.id.mokattamMas2oolMosharkatTV);

        mshro3Mosharkat[0] = findViewById(R.id.mohMashroo3MosharkatTV);
        mshro3Mosharkat[1] = findViewById(R.id.maadiMashroo3MosharkatTV);
        mshro3Mosharkat[2] = findViewById(R.id.faisalMashroo3MosharkatTV);
        mshro3Mosharkat[3] = findViewById(R.id.mnasrMashroo3MosharkatTV);
        mshro3Mosharkat[4] = findViewById(R.id.melgdidaMashroo3MosharkatTV);
        mshro3Mosharkat[5] = findViewById(R.id.octMashroo3MosharkatTV);
        mshro3Mosharkat[6] = findViewById(R.id.helwanMashroo3MosharkatTV);
        mshro3Mosharkat[7] = findViewById(R.id.alexMashroo3MosharkatTV);
        mshro3Mosharkat[8] = findViewById(R.id.mokattamMashroo3MosharkatTV);

        nasheetMosharkatTV[0] = findViewById(R.id.mohNasheetMosharkatTV);
        nasheetMosharkatTV[1] = findViewById(R.id.maadiNasheetMosharkatTV);
        nasheetMosharkatTV[2] = findViewById(R.id.faisalNasheetMosharkatTV);
        nasheetMosharkatTV[3] = findViewById(R.id.mnasrNasheetMosharkatTV);
        nasheetMosharkatTV[4] = findViewById(R.id.melgdidaNasheetMosharkatTV);
        nasheetMosharkatTV[5] = findViewById(R.id.octNasheetMosharkatTV);
        nasheetMosharkatTV[6] = findViewById(R.id.helwanNasheetMosharkatTV);
        nasheetMosharkatTV[7] = findViewById(R.id.alexNasheetMosharkatTV);
        nasheetMosharkatTV[8] = findViewById(R.id.mokattamNasheetMosharkatTV);

        noobsMosharkatTV[0] = findViewById(R.id.mohNoobsMosharkatTV);
        noobsMosharkatTV[1] = findViewById(R.id.maadiNoobsMosharkatTV);
        noobsMosharkatTV[2] = findViewById(R.id.faisalNoobsMosharkatTV);
        noobsMosharkatTV[3] = findViewById(R.id.mnasrNoobsMosharkatTV);
        noobsMosharkatTV[4] = findViewById(R.id.melgdidaNoobsMosharkatTV);
        noobsMosharkatTV[5] = findViewById(R.id.octNoobsMosharkatTV);
        noobsMosharkatTV[6] = findViewById(R.id.helwanNoobsMosharkatTV);
        noobsMosharkatTV[7] = findViewById(R.id.alexNoobsMosharkatTV);
        noobsMosharkatTV[8] = findViewById(R.id.mokattamNoobsMosharkatTV);

        normalMosharkatTV[0] = findViewById(R.id.mohNormalMosharkatTV);
        normalMosharkatTV[1] = findViewById(R.id.maadiNormalMosharkatTV);
        normalMosharkatTV[2] = findViewById(R.id.faisalNormalMosharkatTV);
        normalMosharkatTV[3] = findViewById(R.id.mnasrNormalMosharkatTV);
        normalMosharkatTV[4] = findViewById(R.id.melgdidaNormalMosharkatTV);
        normalMosharkatTV[5] = findViewById(R.id.octNormalMosharkatTV);
        normalMosharkatTV[6] = findViewById(R.id.helwanNormalMosharkatTV);
        normalMosharkatTV[7] = findViewById(R.id.alexNormalMosharkatTV);
        normalMosharkatTV[8] = findViewById(R.id.mokattamNormalMosharkatTV);

        // متوسط مشاركات
        mas2olAverage[0] = findViewById(R.id.mohMas2oolAvgTV);
        mas2olAverage[1] = findViewById(R.id.maadiMas2oolAvgTV);
        mas2olAverage[2] = findViewById(R.id.faisalMas2oolAvgTV);
        mas2olAverage[3] = findViewById(R.id.mnasrMas2oolAvgTV);
        mas2olAverage[4] = findViewById(R.id.melgdidaMas2oolAvgTV);
        mas2olAverage[5] = findViewById(R.id.octMas2oolAvgTV);
        mas2olAverage[6] = findViewById(R.id.helwanMas2oolAvgTV);
        mas2olAverage[7] = findViewById(R.id.alexMas2oolAvgTV);
        mas2olAverage[8] = findViewById(R.id.mokattamMas2oolAvgTV);

        mshro3Average[0] = findViewById(R.id.mohMashroo3AvgTV);
        mshro3Average[1] = findViewById(R.id.maadiMashroo3AvgTV);
        mshro3Average[2] = findViewById(R.id.faisalMashroo3AvgTV);
        mshro3Average[3] = findViewById(R.id.mnasrMashroo3AvgTV);
        mshro3Average[4] = findViewById(R.id.melgdidaMashroo3AvgTV);
        mshro3Average[5] = findViewById(R.id.octMashroo3AvgTV);
        mshro3Average[6] = findViewById(R.id.helwanMashroo3AvgTV);
        mshro3Average[7] = findViewById(R.id.alexMashroo3AvgTV);
        mshro3Average[8] = findViewById(R.id.mokattamMashroo3AvgTV);

        nasheetAverage[0] = findViewById(R.id.mohNasheetAvgTV);
        nasheetAverage[1] = findViewById(R.id.maadiNasheetAvgTV);
        nasheetAverage[2] = findViewById(R.id.faisalNasheetAvgTV);
        nasheetAverage[3] = findViewById(R.id.mnasrNasheetAvgTV);
        nasheetAverage[4] = findViewById(R.id.melgdidaNasheetAvgTV);
        nasheetAverage[5] = findViewById(R.id.octNasheetAvgTV);
        nasheetAverage[6] = findViewById(R.id.helwanNasheetAvgTV);
        nasheetAverage[7] = findViewById(R.id.alexNasheetAvgTV);
        nasheetAverage[8] = findViewById(R.id.mokattamNasheetAvgTV);

        noobsAverage[0] = findViewById(R.id.mohNoobsAvgTV);
        noobsAverage[1] = findViewById(R.id.maadiNoobsAvgTV);
        noobsAverage[2] = findViewById(R.id.faisalNoobsAvgTV);
        noobsAverage[3] = findViewById(R.id.mnasrNoobsAvgTV);
        noobsAverage[4] = findViewById(R.id.melgdidaNoobsAvgTV);
        noobsAverage[5] = findViewById(R.id.octNoobsAvgTV);
        noobsAverage[6] = findViewById(R.id.helwanNoobsAvgTV);
        noobsAverage[7] = findViewById(R.id.alexNoobsAvgTV);
        noobsAverage[8] = findViewById(R.id.mokattamNoobsAvgTV);

        normalAverage[0] = findViewById(R.id.mohNormalAvgTV);
        normalAverage[1] = findViewById(R.id.maadiNormalAvgTV);
        normalAverage[2] = findViewById(R.id.faisalNormalAvgTV);
        normalAverage[3] = findViewById(R.id.mnasrNormalAvgTV);
        normalAverage[4] = findViewById(R.id.melgdidaNormalAvgTV);
        normalAverage[5] = findViewById(R.id.octNormalAvgTV);
        normalAverage[6] = findViewById(R.id.helwanNormalAvgTV);
        normalAverage[7] = findViewById(R.id.alexNormalAvgTV);
        normalAverage[8] = findViewById(R.id.mokattamNormalAvgTV);

        //النقاط
        points[0] = findViewById(R.id.mohPoints);
        points[1] = findViewById(R.id.maadiPoints);
        points[2] = findViewById(R.id.faisalPoints);
        points[3] = findViewById(R.id.mnasrPoints);
        points[4] = findViewById(R.id.melgdidaPoints);
        points[5] = findViewById(R.id.octPoints);
        points[6] = findViewById(R.id.helwanPoints);
        points[7] = findViewById(R.id.alexPoints);
        points[8] = findViewById(R.id.mokattamPoints);
    }

    public void refreshReport(View view) {
        selected_month = Integer.parseInt(month_et.getSelectedItem().toString());
        start_day = Integer.parseInt(day_from_et.getSelectedItem().toString());
        end_day = Integer.parseInt(day_to_et.getSelectedItem().toString());
        for (branchIterator = 0; branchIterator < BRANCHES_COUNT; branchIterator++) {
            getNashitData(branchIterator);

            updateMeetings(branchIterator);
            updateMosharkat(branchIterator);
            updateEvents(branchIterator);
        }
    }

    private void updateEvents(int branchIterator) {
        DatabaseReference EventsRef = database.getReference("reports").child(branches[branchIterator]);
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
                                eventsCount[branchIterator].setText(String.valueOf(EventsCounter));
                                orientationCount[branchIterator].setText(String.valueOf(OrientationCounter));
                                coursesCount[branchIterator].setText(String.valueOf(CoursesCounter));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Failed to read value
                                Log.w(TAG, "Failed to read value.", error.toException());
                            }
                        });
    }

    private void updateMosharkat(int branchIterator) {
        DatabaseReference MosharkatRef =
                database.getReference("mosharkat").child(branches[branchIterator]);
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
                                divideMosharkatByDegree(nameCounting, branchIterator);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Failed to read value
                                Log.w(TAG, "Failed to read value.", error.toException());
                            }
                        });
    }

    @SuppressLint("SetTextI18n")
    private void divideMosharkatByDegree(HashMap<String, Integer> nameCounting, int branchIterator) {
        int mas2oleenMosharkat8 = 0;
        int msharee3Mosharkat8 = 0;
        int nasheetMosharkat8 = 0;
        int noobsMosharkat8 = 0;
        int da5el_2_Mosharkat = 0;
        int normalMosharkat8 = 0;
        int da5el_aboveMosharkat = 0;
        int otherMosharkat = 0;

        int mas2oleenArrived = 0;
        int msharee3Arrived = 0;
        int nasheetArrived = 0;
        int noobsArrived = 0;
        int normalArrived = 0;


        for (Map.Entry<String, Integer> entry : nameCounting.entrySet()) {
            String volName = entry.getKey();

            if (teamDegrees.get(branchIterator).containsKey(volName)) {
                if (teamDegrees.get(branchIterator).get(volName).equals("مسؤول")) {
                    mas2oleenMosharkat8 += Math.min(8, entry.getValue());
                    mas2oleenArrived++;
                } else if (teamDegrees.get(branchIterator).get(volName).equals("مشروع")) {
                    msharee3Mosharkat8 += Math.min(8, entry.getValue());
                    msharee3Arrived++;
                } else if (teamDegrees.get(branchIterator).get(volName).contains("داخل")) {
                    String[] splittedDegree = teamDegrees.get(branchIterator).get(volName).split("&", 2);

                    switch (Integer.parseInt(splittedDegree[1]) - (selected_month - (month + 1))) {
                        case 0:
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

                    if (Integer.parseInt(splittedDegree[1]) - (selected_month - (month + 1)) > 0 && allNsheet.get(branchIterator).contains(volName)) {
                        nasheetMosharkat8 += Math.min(8, entry.getValue());
                        nasheetArrived++;
                    } else {
                        normalMosharkat8 += Math.min(8, entry.getValue());
                        normalArrived++;
                    }
                }
            } else {
                otherMosharkat += Math.min(8, entry.getValue());
            }
        }
        noobsCount[branchIterator].setText(String.valueOf(noobsArrived));
        normalCount[branchIterator].setText(String.valueOf(normalArrived));
        /* ************************************************************************************ */
        int pointsCalculated =
                msharee3Mosharkat8 * myRules.mashroo3_points + myRules.mas2ool_points * mas2oleenMosharkat8;
        int otherPointsCalculated = otherMosharkat + noobsMosharkat8 + da5el_2_Mosharkat * 2 + da5el_aboveMosharkat * 3;
        points[branchIterator].setText(String.valueOf(pointsCalculated + otherPointsCalculated));
        /* ************************************************************************************* */
        float percent1 = (float) mas2oleenArrived / mas2oleenCounter[branchIterator] * 100;
        float percent2 = (float) msharee3Arrived / msharee3Counter[branchIterator] * 100;
        float percent3 = (float) nasheetArrived / nasheetCounter[branchIterator] * 100;

        mas2olPercent[branchIterator].setText(Math.round(percent1 * 10) / 10.0 + " %");
        mshro3Percent[branchIterator].setText(Math.round(percent2 * 10) / 10.0 + " %");
        nasheetPercent[branchIterator].setText(Math.round(percent3 * 10) / 10.0 + " %");
        /* ************************************************************************************* */
        mas2olMosharkat[branchIterator].setText(String.valueOf(mas2oleenMosharkat8));
        mshro3Mosharkat[branchIterator].setText(String.valueOf(msharee3Mosharkat8));
        nasheetMosharkatTV[branchIterator].setText(String.valueOf(nasheetMosharkat8));
        noobsMosharkatTV[branchIterator].setText(String.valueOf(noobsMosharkat8));
        normalMosharkatTV[branchIterator].setText(String.valueOf(normalMosharkat8));
        /* ************************************************************************************* */
        float avg1 = (float) mas2oleenMosharkat8 / mas2oleenArrived;
        float avg2 = (float) msharee3Mosharkat8 / msharee3Arrived;
        float avg3 = (float) nasheetMosharkat8 / nasheetArrived;
        float avg4 = (float) noobsMosharkat8 / noobsArrived;
        float avg5 = (float) normalMosharkat8 / normalArrived;

        mas2olAverage[branchIterator].setText(String.valueOf(Math.round(avg1 * 10) / 10.0));
        mshro3Average[branchIterator].setText(String.valueOf(Math.round(avg2 * 10) / 10.0));
        nasheetAverage[branchIterator].setText(String.valueOf(Math.round(avg3 * 10) / 10.0));
        noobsAverage[branchIterator].setText(String.valueOf(Math.round(avg4 * 10) / 10.0));
        normalAverage[branchIterator].setText(String.valueOf(Math.round(avg5 * 10) / 10.0));

    }

    private void updateMeetings(int branchIterator) {
        DatabaseReference MeetingsRef =
                database.getReference("meetings").child(branches[branchIterator]);
        MeetingsRef.child(String.valueOf(selected_month))
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
                                meetingsCount[branchIterator].setText(String.valueOf(meetingsCounter));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Failed to read value
                                Log.w(TAG, "Failed to read value.", error.toException());
                            }
                        });
    }

    private void getNashitData(int branchIterator) {
        DatabaseReference nasheetRef = database.getReference("nasheet").child(branches[branchIterator]);
        nasheetRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        allNsheet.get(branchIterator).clear();
                        nasheetCounter[branchIterator] = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            NasheetVolunteer nasheetVolunteer = snapshot.getValue(NasheetVolunteer.class);
                            assert nasheetVolunteer != null;
                            String[] parts = nasheetVolunteer.first_month.split("/", 2);
                            int months =
                                    (year - Integer.parseInt(parts[1])) * 12
                                            + (selected_month - Integer.parseInt(parts[0]));
                            if (months > 0) {
                                allNsheet.get(branchIterator).add(snapshot.getKey());
                                nasheetCounter[branchIterator]++;
                            }
                        }
                        nasheetCount[branchIterator].setText(String.valueOf(nasheetCounter[branchIterator]));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Failed to read value
                        Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });
    }

    public void exportExcelClick(View view) {
        excelOut = true;
        selected_month = Integer.parseInt(month_et.getSelectedItem().toString());

        if (Build.VERSION.SDK_INT >= 23) {
            String[] PERMISSIONS = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (!hasPermissions(PERMISSIONS)) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST);
            } else { // permession already granted
                exportExcel(month_et.getSelectedItem().toString());
            }
        } else { // api below 23
            exportExcel(month_et.getSelectedItem().toString());
        }
    }

    private void exportExcel(String month) {
        String root =
                Environment.getExternalStorageDirectory().getAbsolutePath() + "/Mosharkaty/تقارير";
        File dir = new File(root);
        dir.mkdirs();
        String Fnamexls = ("/تقرير_مجمع_شهر_" + month + ".xls");
        WorkbookSettings wbSettings = new WorkbookSettings();
        try {
            File file = new File(dir, Fnamexls);
            workbook = Workbook.createWorkbook(file, wbSettings);
            WritableSheet sheet1 = workbook.createSheet("درجات التطوع", 0);
            WritableSheet sheet2 = workbook.createSheet("اجتماعات", 1);
            WritableSheet sheet3 = workbook.createSheet("ايفنتات و اخري", 2);

            progress = new ProgressDialog(AdminMrkzyReportsActivity.this);
            progress.setTitle("Loading");
            progress.setMessage("لحظات معانا...");
            progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
            progress.show();

            for (branchIterator = 0; branchIterator < BRANCHES_COUNT; branchIterator++) {
                printMeetings(branchIterator, sheet2);
                printEvents(branchIterator, sheet3);
                printMosharkat(branchIterator, sheet1);
            }
            for (int i = 1; i <= 22; i += 7) {
                int print_end_day = i + 6;//7-->14-->21-->28
                if (end_day == 28) end_day = 31; //ensure end of month
                for (branchIterator = 0; branchIterator < BRANCHES_COUNT; branchIterator++) {
                    printWeeklyMosharkat(branchIterator, i, print_end_day, sheet1);
                }
            }
            printMeetingsHeaders(sheet2);
            printEventsHeaders(sheet3);
            printMosharkatHeaders(sheet1);
            Toast.makeText(getApplicationContext(), "تم حفظ الفايل في\n " + root + Fnamexls, Toast.LENGTH_SHORT)
                    .show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printEventsHeaders(WritableSheet sheet) {
        Label label0 = new Label(1, 0, "الكورسات");
        Label label1 = new Label(2, 0, "الاورينتيشن");
        Label label2 = new Label(3, 0, "الورش");
        Label label3 = new Label(4, 0, "ايفنتات اخري");
        Label label4 = new Label(5, 0, "الاجمالي");

        try {
            sheet.addCell(label4);
            sheet.addCell(label3);
            sheet.addCell(label2);
            sheet.addCell(label1);
            sheet.addCell(label0);
            for (branchIterator = 0; branchIterator < BRANCHES_COUNT; branchIterator++) {
                Label label00 = new Label(0, branchIterator + 1, branches[branchIterator]);
                sheet.addCell(label00);
            }
        } catch (RowsExceededException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        }
    }

    private void printEvents(int branchIterator, WritableSheet sheet) {
        DatabaseReference EventsRef = database.getReference("reports").child(branches[branchIterator]);
        EventsRef.child(String.valueOf(selected_month))
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                int EventsCounter = 0;
                                int OrientationCounter = 0;
                                int CoursesCounter = 0;
                                int warshaCounter = 0;
                                int otherCounter = 0;
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    EventReport event = snapshot.getValue(EventReport.class);
                                    if (event != null) {
                                        EventsCounter++;
                                        if (event.type.matches("(.*)اورينتيشن(.*)"))
                                            OrientationCounter++;
                                        else if (event.type.matches("(.*)سيشن(.*)"))
                                            CoursesCounter++;
                                        else if (event.type.matches("(.*)ورشة(.*)"))
                                            warshaCounter++;
                                        else otherCounter++;
                                    }
                                }
                                Label label0 = new Label(1, branchIterator + 1, String.valueOf(CoursesCounter));
                                Label label1 = new Label(2, branchIterator + 1, String.valueOf(OrientationCounter));
                                Label label2 = new Label(3, branchIterator + 1, String.valueOf(warshaCounter));
                                Label label3 = new Label(4, branchIterator + 1, String.valueOf(otherCounter));
                                Label label4 = new Label(5, branchIterator + 1, String.valueOf(EventsCounter));
                                try {
                                    sheet.addCell(label4);
                                    sheet.addCell(label3);
                                    sheet.addCell(label2);
                                    sheet.addCell(label1);
                                    sheet.addCell(label0);
                                } catch (RowsExceededException e) {
                                    e.printStackTrace();
                                } catch (WriteException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Failed to read value
                                Log.w(TAG, "Failed to read value.", error.toException());
                            }
                        });
    }

    private void printMosharkatHeaders(WritableSheet sheet) {
        Label label0 = new Label(1, 0, "عدد المسؤولين");
        Label label1 = new Label(2, 0, "حضور الاسبوع الاول");
        Label label2 = new Label(3, 0, "حضور الاسبوع التاني");
        Label label3 = new Label(4, 0, "حضور الاسبوع التالت");
        Label label4 = new Label(5, 0, "حضور الاسبوع الرابع");
        Label label5 = new Label(6, 0, "عدد مشاركات مسؤول المحقق");
        Label label6 = new Label(7, 0, "متوسط مشاركات مسؤول المحقق");
        Label label7 = new Label(8, 0, "عدد المشاريع");
        Label label8 = new Label(9, 0, "حضور الاسبوع الاول");
        Label label9 = new Label(10, 0, "حضور الاسبوع التاني");
        Label label10 = new Label(11, 0, "حضور الاسبوع التالت");
        Label label11 = new Label(12, 0, "حضور الاسبوع الرابع");
        Label label12 = new Label(13, 0, "عدد مشاركات مشروع المحقق");
        Label label13 = new Label(14, 0, "متوسط مشاركات مشروع المحقق");
        Label label14 = new Label(15, 0, "محقق عدد داخل المتابعة");
        Label label15 = new Label(16, 0, "محقق مشاركات داخل المتابعة");
        Label label16 = new Label(17, 0, "متوسط داخل المتابعة");
        Label label17 = new Label(18, 0, "محقق الجدد");
        Label label18 = new Label(19, 0, "اجمالي النقاط");

        try {
            sheet.addCell(label18);
            sheet.addCell(label17);
            sheet.addCell(label16);
            sheet.addCell(label15);
            sheet.addCell(label14);
            sheet.addCell(label13);
            sheet.addCell(label12);
            sheet.addCell(label11);
            sheet.addCell(label10);
            sheet.addCell(label9);
            sheet.addCell(label8);
            sheet.addCell(label7);
            sheet.addCell(label6);
            sheet.addCell(label5);
            sheet.addCell(label4);
            sheet.addCell(label3);
            sheet.addCell(label2);
            sheet.addCell(label1);
            sheet.addCell(label0);
            for (branchIterator = 0; branchIterator < BRANCHES_COUNT; branchIterator++) {
                Label label00 = new Label(0, branchIterator + 1, branches[branchIterator]);
                sheet.addCell(label00);
            }
        } catch (RowsExceededException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        }
    }

    private void printMosharkat(int branchIterator, WritableSheet sheet) {
        DatabaseReference MosharkatRef =
                database.getReference("mosharkat").child(branches[branchIterator]);
        MosharkatRef.child(String.valueOf(selected_month))
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                HashMap<String, Integer> nameCounting = new HashMap<>();
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    MosharkaItem mosharka = snapshot.getValue(MosharkaItem.class);
                                    if (mosharka != null) {
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
                                print_divideMosharkatByDegree(nameCounting, branchIterator, sheet);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Failed to read value
                                Log.w(TAG, "Failed to read value.", error.toException());
                            }
                        });
    }

    private void print_divideMosharkatByDegree(HashMap<String, Integer> nameCounting, int branchIterator, WritableSheet sheet) {
        int mas2oleenMosharkat8 = 0;
        int msharee3Mosharkat8 = 0;
        int nasheetMosharkat8 = 0;
        int noobsMosharkat8 = 0;
        int da5el_2_Mosharkat = 0;
        int normalMosharkat8 = 0;
        int da5el_aboveMosharkat = 0;
        int otherMosharkat = 0;

        int mas2oleenArrived = 0;
        int msharee3Arrived = 0;
        int nasheetArrived = 0;
        int noobsArrived = 0;
        int normalArrived = 0;


        for (Map.Entry<String, Integer> entry : nameCounting.entrySet()) {
            String volName = entry.getKey();

            if (teamDegrees.get(branchIterator).containsKey(volName)) {
                if (teamDegrees.get(branchIterator).get(volName).equals("مسؤول")) {
                    mas2oleenMosharkat8 += Math.min(8, entry.getValue());
                    mas2oleenArrived++;
                } else if (teamDegrees.get(branchIterator).get(volName).equals("مشروع")) {
                    msharee3Mosharkat8 += Math.min(8, entry.getValue());
                    msharee3Arrived++;
                } else if (teamDegrees.get(branchIterator).get(volName).contains("داخل")) {
                    String[] splittedDegree = teamDegrees.get(branchIterator).get(volName).split("&", 2);

                    switch (Integer.parseInt(splittedDegree[1]) - (selected_month - (month + 1))) {
                        case 0:
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

                    if (Integer.parseInt(splittedDegree[1]) - (selected_month - (month + 1)) > 0 && allNsheet.get(branchIterator).contains(volName)) {
                        nasheetMosharkat8 += Math.min(8, entry.getValue());
                        nasheetArrived++;
                    } else {
                        normalMosharkat8 += Math.min(8, entry.getValue());
                        normalArrived++;
                    }
                }
            } else {
                otherMosharkat += Math.min(8, entry.getValue());
            }
        }
        Label label0 = new Label(18, branchIterator + 1, String.valueOf(noobsArrived));
        Label label1 = new Label(15, branchIterator + 1, String.valueOf(normalArrived + nasheetArrived));
        /* ************************************************************************************ */
        int pointsCalculated =
                msharee3Mosharkat8 * myRules.mashroo3_points + myRules.mas2ool_points * mas2oleenMosharkat8;
        int otherPointsCalculated = otherMosharkat + noobsMosharkat8 + da5el_2_Mosharkat * 2 + da5el_aboveMosharkat * 3;
        Label label2 = new Label(19, branchIterator + 1, String.valueOf(pointsCalculated + otherPointsCalculated));
        /* ************************************************************************************* */
        Label label3 = new Label(1, branchIterator + 1, String.valueOf(mas2oleenCounter[branchIterator]));
        Label label4 = new Label(8, branchIterator + 1, String.valueOf(msharee3Counter[branchIterator]));
        /* ************************************************************************************* */
        Label label5 = new Label(6, branchIterator + 1, String.valueOf(mas2oleenMosharkat8));
        Label label6 = new Label(13, branchIterator + 1, String.valueOf(msharee3Mosharkat8));
        Label label7 = new Label(16, branchIterator + 1, String.valueOf(nasheetMosharkat8 + normalMosharkat8));

        /* ************************************************************************************* */
        float avg1 = (float) mas2oleenMosharkat8 / mas2oleenArrived;
        float avg2 = (float) msharee3Mosharkat8 / msharee3Arrived;
        float avg3 = (float) (nasheetMosharkat8 + normalMosharkat8) / (nasheetArrived + normalArrived);

        Label label8 = new Label(7, branchIterator + 1, String.valueOf(Math.round(avg1 * 10) / 10.0));
        Label label9 = new Label(14, branchIterator + 1, String.valueOf(Math.round(avg2 * 10) / 10.0));
        Label label10 = new Label(17, branchIterator + 1, String.valueOf(Math.round(avg3 * 10) / 10.0));
        try {
            sheet.addCell(label10);
            sheet.addCell(label9);
            sheet.addCell(label8);
            sheet.addCell(label7);
            sheet.addCell(label6);
            sheet.addCell(label5);
            sheet.addCell(label4);
            sheet.addCell(label3);
            sheet.addCell(label2);
            sheet.addCell(label1);
            sheet.addCell(label0);
        } catch (RowsExceededException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        }
    }

    private void printWeeklyMosharkat(int branchIterator, int print_start_day, int print_end_day, WritableSheet sheet) {
        DatabaseReference MosharkatRef =
                database.getReference("mosharkat").child(branches[branchIterator]);
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
                                        if (Integer.parseInt(splittedDate[0]) >= print_start_day
                                                && Integer.parseInt(splittedDate[0]) <= print_end_day) {
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
                                print_divideWeeklyMosharkatByDegree(nameCounting, branchIterator, sheet, getweekNum(print_start_day));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Failed to read value
                                Log.w(TAG, "Failed to read value.", error.toException());
                            }
                        });
    }

    private int getweekNum(int print_start_day) {
        switch (print_start_day) {
            case 1:
                return 1;
            case 8:
                return 2;
            case 15:
                return 3;
            case 22:
                return 4;
        }
        return 0;
    }

    private void print_divideWeeklyMosharkatByDegree(HashMap<String, Integer> nameCounting, int branchIterator, WritableSheet sheet, int weekNum) {
        if (weekNum == 4 && branchIterator == BRANCHES_COUNT - 1) progress.dismiss();
        Log.d(TAG, "print_divideWeeklyMosharkatByDegree: weeknum = " + weekNum + " , branchIterator = " + branchIterator);
        int mas2oleenMosharkat8 = 0;
        int msharee3Mosharkat8 = 0;
        int nasheetMosharkat8 = 0;
        int noobsMosharkat8 = 0;
        int da5el_2_Mosharkat = 0;
        int normalMosharkat8 = 0;
        int da5el_aboveMosharkat = 0;
        int otherMosharkat = 0;

        int mas2oleenArrived = 0;
        int msharee3Arrived = 0;
        int nasheetArrived = 0;
        int noobsArrived = 0;
        int normalArrived = 0;


        for (Map.Entry<String, Integer> entry : nameCounting.entrySet()) {
            String volName = entry.getKey();

            if (teamDegrees.get(branchIterator).containsKey(volName)) {
                if (teamDegrees.get(branchIterator).get(volName).equals("مسؤول")) {
                    mas2oleenMosharkat8 += Math.min(8, entry.getValue());
                    mas2oleenArrived++;
                } else if (teamDegrees.get(branchIterator).get(volName).equals("مشروع")) {
                    msharee3Mosharkat8 += Math.min(8, entry.getValue());
                    msharee3Arrived++;
                } else if (teamDegrees.get(branchIterator).get(volName).contains("داخل")) {
                    String[] splittedDegree = teamDegrees.get(branchIterator).get(volName).split("&", 2);

                    switch (Integer.parseInt(splittedDegree[1]) - (selected_month - (month + 1))) {
                        case 0:
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

                    if (Integer.parseInt(splittedDegree[1]) - (selected_month - (month + 1)) > 0 && allNsheet.get(branchIterator).contains(volName)) {
                        nasheetMosharkat8 += Math.min(8, entry.getValue());
                        nasheetArrived++;
                    } else {
                        normalMosharkat8 += Math.min(8, entry.getValue());
                        normalArrived++;
                    }
                }
            } else {
                otherMosharkat += Math.min(8, entry.getValue());
            }
        }
        /* ************************************************************************************* */
        Label label0 = new Label(1 + weekNum, branchIterator + 1, String.valueOf(mas2oleenArrived));
        Label label1 = new Label(8 + weekNum, branchIterator + 1, String.valueOf(msharee3Arrived));
        try {
            sheet.addCell(label1);
            sheet.addCell(label0);
        } catch (RowsExceededException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        }
    }

    private void printMeetingsHeaders(WritableSheet sheet) {
        Label label0 = new Label(1, 0, "اجتماعات هيد الفرع");
        Label label1 = new Label(2, 0, "اجتماعات hr");
        Label label2 = new Label(3, 0, "اجتماعات المتابعة");
        Label label3 = new Label(4, 0, "اجتماعات الاتصالات");
        Label label4 = new Label(5, 0, "اجتماعات الفرق");
        Label label5 = new Label(6, 0, "اجتماعات نفسك في ايه");
        Label label6 = new Label(7, 0, "اجتماعات المعارض");
        Label label7 = new Label(8, 0, "اجتماعات اخري");
        Label label8 = new Label(9, 0, "اجمالي الاجتماعات");

        try {
            sheet.addCell(label8);
            sheet.addCell(label7);
            sheet.addCell(label6);
            sheet.addCell(label5);
            sheet.addCell(label4);
            sheet.addCell(label3);
            sheet.addCell(label2);
            sheet.addCell(label1);
            sheet.addCell(label0);
            for (branchIterator = 0; branchIterator < BRANCHES_COUNT; branchIterator++) {
                Label label00 = new Label(0, branchIterator + 1, branches[branchIterator]);
                sheet.addCell(label00);
            }
        } catch (RowsExceededException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        }
    }

    private void printMeetings(int branchIterator, WritableSheet sheet) {
        DatabaseReference MeetingsRef =
                database.getReference("meetings").child(branches[branchIterator]);
        MeetingsRef.child(String.valueOf(selected_month))
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                int headMeetings = 0;
                                int hrMeetings = 0;
                                int motabaaMeetings = 0;
                                int etisalatMeetings = 0;
                                int fera2Meetings = 0;
                                int nfskMeetings = 0;
                                int m3aredMeetings = 0;
                                int otherMeetings = 0;
                                int meetingsCounter = 0;
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    Meeting meet = snapshot.getValue(Meeting.class);
                                    if (meet != null) {
                                        meetingsCounter++;

                                        if (meet.type.contains("فرق")) fera2Meetings++;
                                        else if (meet.type.contains("معارض")) m3aredMeetings++;
                                        else if (meet.type.contains("نفسك")) nfskMeetings++;
                                        else if (meet.type.contains("hr")) hrMeetings++;
                                        else if (meet.type.contains("متابعة")) motabaaMeetings++;
                                        else if (meet.type.contains("نشيط")) motabaaMeetings++;
                                        else if (meet.type.contains("اتصالات")) etisalatMeetings++;
                                        else if (meet.type.contains("فريق")) headMeetings++;
                                        else if (meet.type.contains("مسؤولين")) headMeetings++;
                                        else otherMeetings++;
                                    }
                                }
                                Label label0 = new Label(1, branchIterator + 1, String.valueOf(headMeetings));
                                Label label1 = new Label(2, branchIterator + 1, String.valueOf(hrMeetings));
                                Label label2 = new Label(3, branchIterator + 1, String.valueOf(motabaaMeetings));
                                Label label3 = new Label(4, branchIterator + 1, String.valueOf(etisalatMeetings));
                                Label label4 = new Label(5, branchIterator + 1, String.valueOf(fera2Meetings));
                                Label label5 = new Label(6, branchIterator + 1, String.valueOf(nfskMeetings));
                                Label label6 = new Label(7, branchIterator + 1, String.valueOf(m3aredMeetings));
                                Label label7 = new Label(8, branchIterator + 1, String.valueOf(otherMeetings));
                                Label label8 = new Label(9, branchIterator + 1, String.valueOf(meetingsCounter));
                                try {
                                    sheet.addCell(label8);
                                    sheet.addCell(label7);
                                    sheet.addCell(label6);
                                    sheet.addCell(label5);
                                    sheet.addCell(label4);
                                    sheet.addCell(label3);
                                    sheet.addCell(label2);
                                    sheet.addCell(label1);
                                    sheet.addCell(label0);
                                } catch (RowsExceededException e) {
                                    e.printStackTrace();
                                } catch (WriteException e) {
                                    e.printStackTrace();
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
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    exportExcel(month_et.getSelectedItem().toString());

                } else {
                    Toast.makeText(
                            getApplicationContext(),
                            "The app was not allowed to write in your storage",
                            Toast.LENGTH_LONG)
                            .show();
                }
            }
        }
    }

    private boolean hasPermissions(String[] permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && getApplicationContext() != null
                && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), permission)
                        != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (excelOut) {
            try {
                workbook.write();
                Log.i(TAG, "printMeetings: workbook write");

            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                workbook.close();
                Log.i(TAG, "printMeetings: final workbook close");

            } catch (WriteException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}
