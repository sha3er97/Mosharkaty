package com.resala.mosharkaty;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

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
import com.resala.mosharkaty.utility.classes.Volunteer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.resala.mosharkaty.NewAccountActivity.branches;
import static com.resala.mosharkaty.StarterActivity.branchesSheets;
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

  int branchIterator;
  ArrayList<HashMap<String, String>> teamDegrees = new ArrayList<>();
  ArrayList<ArrayList<String>> allNsheet = new ArrayList<>();
  TextView[] mas2olCount = new TextView[9];
  TextView[] mshro3Count = new TextView[9];
  TextView[] nasheetCount = new TextView[9];
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
  TextView[] mas2olAverage = new TextView[9];
  TextView[] mshro3Average = new TextView[9];
  TextView[] nasheetAverage = new TextView[9];

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_admin_mrkzy_reports);
    database = FirebaseDatabase.getInstance();
    month_et = findViewById(R.id.current_month);
    day_from_et = findViewById(R.id.day_from);
    day_to_et = findViewById(R.id.day_to);
    refresh_btn = findViewById(R.id.refresh_btn);
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
    DatabaseReference usersRef = liveSheet.child("month_mosharkat");
    usersRef.addListenerForSingleValueEvent(
            new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: branchIterator = " + branchIterator);
                mas2oleenCounter[branchIterator] = 0;
                msharee3Counter[branchIterator] = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                  Volunteer user = snapshot.getValue(Volunteer.class);
                  if (user != null && !user.degree.matches("(.*)مجمد(.*)")) {
                    teamDegrees.get(branchIterator).put(user.Volname, user.degree);
                    if (user.degree.matches("(.*)مسؤول(.*)")) mas2oleenCounter[branchIterator]++;
                    else if (user.degree.matches("(.*)مشروع(.*)"))
                      msharee3Counter[branchIterator]++;
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
                              if (event.type.matches("(.*)اورينتيشن(.*)")) OrientationCounter++;
                              if (event.type.matches("(.*)سيشن(.*)")) CoursesCounter++;
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

    int mas2oleenArrived = 0;
    int msharee3Arrived = 0;
    int nasheetArrived = 0;
    for (Map.Entry<String, Integer> entry : nameCounting.entrySet()) {
      String volName = entry.getKey();

      if (teamDegrees.get(branchIterator).get(volName) != null) {
        if (teamDegrees.get(branchIterator).get(volName).matches("(.*)مسؤول(.*)")) {
          mas2oleenMosharkat8 += Math.min(8, entry.getValue());
          mas2oleenArrived++;
        } else if (teamDegrees.get(branchIterator).get(volName).matches("(.*)مشروع(.*)")) {
          msharee3Mosharkat8 += Math.min(8, entry.getValue());
          msharee3Arrived++;
        }
      } else if (allNsheet.get(branchIterator).contains(volName)) {
        nasheetMosharkat8 += Math.min(8, entry.getValue());
        nasheetArrived++;
      }
    }
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
    /* ************************************************************************************* */
    float avg1 = (float) mas2oleenMosharkat8 / mas2oleenArrived;
    float avg2 = (float) msharee3Mosharkat8 / msharee3Arrived;
    float avg3 = (float) nasheetMosharkat8 / nasheetArrived;

    mas2olAverage[branchIterator].setText(String.valueOf(Math.round(avg1 * 10) / 10.0));
    mshro3Average[branchIterator].setText(String.valueOf(Math.round(avg2 * 10) / 10.0));
    nasheetAverage[branchIterator].setText(String.valueOf(Math.round(avg3 * 10) / 10.0));
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
}
