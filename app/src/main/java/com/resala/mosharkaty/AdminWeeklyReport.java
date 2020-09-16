package com.resala.mosharkaty;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.resala.mosharkaty.AdminShowMosharkat.days;
import static com.resala.mosharkaty.AdminShowMosharkat.months;
import static com.resala.mosharkaty.LoginActivity.userBranch;

public class AdminWeeklyReport extends AppCompatActivity {
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
    DatabaseReference usersRef;
    int selected_month;
    int start_day;
    int end_day;
    ArrayList<String> allNsheet = new ArrayList<>();
    HashMap<String, String> teamDegrees = new HashMap<>();
    int nasheetCounter;
    int mas2oleenCounter;
    int msharee3Counter;
    int mas2oleenMosharkat;
    int msharee3Mosharkat;
    int nasheetMosharkat;
    int normalMosharkat;
    int mas2oleenArrived;
    int msharee3Arrived;
    int nasheetArrived;
    int normalArrived;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_weekly_report);
        database = FirebaseDatabase.getInstance();

        month_et = findViewById(R.id.current_month);
        day_from_et = findViewById(R.id.day_from);
        day_to_et = findViewById(R.id.day_to);
    final Calendar cldr = Calendar.getInstance(Locale.US);
    day = cldr.get(Calendar.DAY_OF_MONTH);
    month = cldr.get(Calendar.MONTH);
    year = cldr.get(Calendar.YEAR);

    // setting spinner
        ArrayAdapter aa =
                new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, months);
    aa.setDropDownViewResource(R.layout.spinner_dropdown);
    // Setting the ArrayAdapter data on the Spinner
    month_et.setAdapter(aa);
    month_et.setSelection(Math.max(month, 0));

        ArrayAdapter ab =
                new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, days);
    ab.setDropDownViewResource(R.layout.spinner_dropdown);
    // Setting the ArrayAdapter data on the Spinner
    day_to_et.setAdapter(ab);
    day_to_et.setSelection(Math.max(day - 1, 0));

        ArrayAdapter ac =
                new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, days);
    ac.setDropDownViewResource(R.layout.spinner_dropdown);
    // Setting the ArrayAdapter data on the Spinner
    day_from_et.setAdapter(ac);
    day_from_et.setSelection(0);

    nasheetRef = database.getReference("nasheet").child(userBranch);
    nasheetRef.addListenerForSingleValueEvent(
            new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    allNsheet.clear();
                    nasheetCounter = 0;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        allNsheet.add(snapshot.getKey());
                        nasheetCounter++;
                    }
                    TextView nasheetCount = findViewById(R.id.nasheetCount);
                    nasheetCount.setText(String.valueOf(nasheetCounter));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });

        DatabaseReference liveSheet =
                database.getReference("1tsMZ5EwtKrBUGuLFVBvuwpU5ve0JKMsaqK1nNAONj-0");
    usersRef = liveSheet.child("month_mosharkat");
    usersRef.addListenerForSingleValueEvent(
            new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mas2oleenCounter = 0;
                    msharee3Counter = 0;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Volunteer user = snapshot.getValue(Volunteer.class);
                        if (user != null && !user.degree.matches("(.*)مجمد(.*)")) {
                            teamDegrees.put(user.Volname, user.degree);
                            if (user.degree.matches("(.*)مسؤول(.*)")) mas2oleenCounter++;
                            else if (user.degree.matches("(.*)مشروع(.*)")) msharee3Counter++;
                        }
                    }
                    TextView mas2olCount = findViewById(R.id.mas2olCount);
                    mas2olCount.setText(String.valueOf(mas2oleenCounter));
                    TextView mshro3Count = findViewById(R.id.mshro3Count);
                    mshro3Count.setText(String.valueOf(msharee3Counter));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });
  }

  public void refreshReport(View view) {
    selected_month = Integer.parseInt(month_et.getSelectedItem().toString());
    start_day = Integer.parseInt(day_from_et.getSelectedItem().toString());
    end_day = Integer.parseInt(day_to_et.getSelectedItem().toString());
    updateMeetings();
    updateEvents();
    updateMosharkat();
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
      mas2oleenMosharkat = 0;
      msharee3Mosharkat = 0;
      nasheetMosharkat = 0;
      normalMosharkat = 0;
      mas2oleenArrived = 0;
      msharee3Arrived = 0;
      nasheetArrived = 0;
      normalArrived = 0;
      for (Map.Entry entry : nameCounting.entrySet()) {
          String volName = entry.getKey().toString();

          if (teamDegrees.get(volName) != null) {
              if (teamDegrees.get(volName).matches("(.*)مسؤول(.*)")) {
                  mas2oleenMosharkat += (Integer) entry.getValue();
                  mas2oleenArrived++;
              } else if (teamDegrees.get(volName).matches("(.*)مشروع(.*)")) {
                  msharee3Mosharkat += (Integer) entry.getValue();
                  msharee3Arrived++;
        }
      } else if (allNsheet.contains(volName)) {
        nasheetMosharkat += (Integer) entry.getValue();
        nasheetArrived++;
      } else {
        normalMosharkat += (Integer) entry.getValue();
        normalArrived++;
      }
    }
    // write in table
    TextView mas2olAttended = findViewById(R.id.mas2olAttended);
    TextView mshro3Attended = findViewById(R.id.mshro3Attended);
    TextView nasheetAttended = findViewById(R.id.nasheetAttended);
    TextView normalAttended = findViewById(R.id.normalAttended);

    mas2olAttended.setText(String.valueOf(mas2oleenArrived));
    mshro3Attended.setText(String.valueOf(msharee3Arrived));
    nasheetAttended.setText(String.valueOf(nasheetArrived));
    normalAttended.setText(String.valueOf(normalArrived));
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

    mas2olMosharkat.setText(String.valueOf(mas2oleenMosharkat));
    mshro3Mosharkat.setText(String.valueOf(msharee3Mosharkat));
    nasheetMosharkatTV.setText(String.valueOf(nasheetMosharkat));
    normalMosharkatTV.setText(String.valueOf(normalMosharkat));
    /** ************************************************************************************* */
    TextView mas2olAverage = findViewById(R.id.mas2olAverage);
    TextView mshro3Average = findViewById(R.id.mshro3Average);
    TextView nasheetAverage = findViewById(R.id.nasheetAverage);
    TextView normalAverage = findViewById(R.id.normalAverage);

    float avg1 = (float) mas2oleenMosharkat / mas2oleenArrived;
    float avg2 = (float) msharee3Mosharkat / msharee3Arrived;
    float avg3 = (float) nasheetMosharkat / nasheetArrived;
    float avg4 = (float) normalMosharkat / normalArrived;

    mas2olAverage.setText(String.valueOf(Math.round(avg1 * 10) / 10.0));
    mshro3Average.setText(String.valueOf(Math.round(avg2 * 10) / 10.0));
    nasheetAverage.setText(String.valueOf(Math.round(avg3 * 10) / 10.0));
    normalAverage.setText(String.valueOf(Math.round(avg4 * 10) / 10.0));
  }

  private void updateEvents() {
    TextView eventsCount = findViewById(R.id.eventsCount);
      TextView orientationCount = findViewById(R.id.orientationCount);
      TextView coursesCount = findViewById(R.id.coursesCount);
    EventsRef = database.getReference("events").child(userBranch);
    EventsRef.addValueEventListener(
            new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int EventsCounter = 0;
                    int OrientationCounter = 0;
                    int CoursesCounter = 0;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Event event = snapshot.getValue(Event.class);
                        if (event != null) {
                            String[] splittedDate = event.date.split("/", 2);
                            if (Integer.parseInt(splittedDate[0]) >= start_day
                                    && Integer.parseInt(splittedDate[0]) <= end_day
                                    && Integer.parseInt(splittedDate[1]) == selected_month) {
                                if (event.type.matches("(.*)اورينتيشن(.*)")) OrientationCounter++;
                                if (event.type.matches("(.*)سيشن(.*)")) CoursesCounter++;
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
                                              && Integer.parseInt(splittedDate[0]) <= end_day
                                              && Integer.parseInt(splittedDate[1]) == selected_month) {
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
