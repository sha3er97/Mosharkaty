package com.resala.mosharkaty;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

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
import static com.resala.mosharkaty.LoginActivity.userBranch;

public class NasheetReportsFragment extends androidx.fragment.app.Fragment {
  View view;
  FirebaseDatabase database;
  int month;
  int year;
  ArrayList<String> allNsheet = new ArrayList<>();
  ArrayList<String> allNoobs = new ArrayList<>();
  DatabaseReference nasheetRef;
  ValueEventListener nasheetlistener;
  DatabaseReference MosharkatRef;
  ValueEventListener mosharkatlistener;

  @Override
  public View onCreateView(
          LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    view = inflater.inflate(R.layout.fragment_nasheet_reports, container, false);
    database = FirebaseDatabase.getInstance();
    final Calendar cldr = Calendar.getInstance(Locale.US);
    TextView current_month = view.findViewById(R.id.current_month);
    month = cldr.get(Calendar.MONTH) + 1;
    year = cldr.get(Calendar.YEAR);
    current_month.setText(String.valueOf(month));
    nasheetRef = database.getReference("nasheet").child(userBranch);
    nasheetlistener =
            nasheetRef.addValueEventListener(
                    new ValueEventListener() {
                      @Override
                      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        allNsheet.clear();
                        allNoobs.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                          NasheetVolunteer nasheetVolunteer = snapshot.getValue(NasheetVolunteer.class);
                          assert nasheetVolunteer != null;
                          String[] parts = nasheetVolunteer.first_month.split("/", 2);

                          if (parts[0].equals(String.valueOf(month))
                                  && parts[1].equals(String.valueOf(year)))
                            allNoobs.add(snapshot.getKey());
                          else allNsheet.add(snapshot.getKey());
                        }
                        updateReport();
                      }

                      @Override
                      public void onCancelled(@NonNull DatabaseError error) {
                        // Failed to read value
                        Log.w(TAG, "Failed to read value.", error.toException());
                      }
                    });
    return view;
  }

  private void updateReport() {
    TextView nasheetCount = view.findViewById(R.id.nasheetCount);
    TextView noobsCount = view.findViewById(R.id.noobsCount);

    TextView nasheetAttended = view.findViewById(R.id.nasheetAttended);
    TextView noobsAttended = view.findViewById(R.id.noobsAttended);

    TextView nasheetPercentTV = view.findViewById(R.id.nasheetPercent);
    TextView noobsPercentTV = view.findViewById(R.id.noobsPercent);

    TextView nasheetMosharkat = view.findViewById(R.id.nasheetMosharkat);
    TextView noobsMosharkat = view.findViewById(R.id.noobsMosharkat);

    TextView nasheetAverage1 = view.findViewById(R.id.nasheetAverage1);
    TextView noobsAverage1 = view.findViewById(R.id.noobsAverage1);

    nasheetCount.setText(String.valueOf(allNsheet.size()));
    noobsCount.setText(String.valueOf(allNoobs.size()));
    MosharkatRef = database.getReference("mosharkat").child(userBranch);
    mosharkatlistener =
            MosharkatRef.child(String.valueOf(month))
                    .addValueEventListener(
                            new ValueEventListener() {
                              @Override
                              public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                HashMap<String, Integer> nsheetCounting = new HashMap<>();
                                HashMap<String, Integer> noobCounting = new HashMap<>();
                                int nsheetAttendedCount = 0, noobsAttendedCount = 0;
                                int nsheetMosharkatCount = 0, noobsMosharkatCount = 0;
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                  MosharkaItem mosharka = snapshot.getValue(MosharkaItem.class);
                                  if (mosharka != null) {
                                    if (!allNsheet.contains(mosharka.getVolname())
                                            && !allNoobs.contains(mosharka.getVolname())) continue;
                                    if (allNsheet.contains(mosharka.getVolname())) {
                                      if (nsheetCounting.containsKey(mosharka.getVolname().trim())) {
                                        // If char is present in charCountMap,
                                        // incrementing it's count by 1
                                        nsheetCounting.put(
                                                mosharka.getVolname().trim(),
                                                Math.min(8, nsheetCounting.get(mosharka.getVolname().trim()) + 1));
                                      } else {
                                        // If char is not present in charCountMap,
                                        // putting this char to charCountMap with 1 as it's value
                                        nsheetCounting.put(mosharka.getVolname().trim(), 1);
                                        nsheetAttendedCount++;
                                      }
                                    } else {
                                      if (noobCounting.containsKey(mosharka.getVolname().trim())) {
                                        // If char is present in charCountMap,
                                        // incrementing it's count by 1
                                        noobCounting.put(
                                                mosharka.getVolname().trim(),
                                                Math.min(8, noobCounting.get(mosharka.getVolname().trim()) + 1));
                                      } else {
                                        // If char is not present in charCountMap,
                                        // putting this char to charCountMap with 1 as it's value
                                        noobCounting.put(mosharka.getVolname().trim(), 1);
                                        noobsAttendedCount++;
                                      }
                                    }
                                  }
                                }
                                nasheetAttended.setText(String.valueOf(nsheetAttendedCount));
                                noobsAttended.setText(String.valueOf(noobsAttendedCount));
                                float nsheetPercent = (float) nsheetAttendedCount / allNsheet.size() * 100;
                                float noobsPercent = (float) noobsAttendedCount / allNoobs.size() * 100;
                                nasheetPercentTV.setText(Math.round(nsheetPercent * 10) / 10 + " %");
                                noobsPercentTV.setText(Math.round(noobsPercent * 10) / 10 + " %");
                                for (Map.Entry entry : nsheetCounting.entrySet()) {
                                  nsheetMosharkatCount += (int) entry.getValue();
                                }
                                for (Map.Entry entry : noobCounting.entrySet()) {
                                  noobsMosharkatCount += (int) entry.getValue();
                                }
                                nasheetMosharkat.setText(String.valueOf(nsheetMosharkatCount));
                                noobsMosharkat.setText(String.valueOf(noobsMosharkatCount));
                                float nsheetAverageReal = (float) nsheetMosharkatCount / nsheetAttendedCount;
                                float noobsAverageReal = (float) noobsMosharkatCount / noobsAttendedCount;
                                nasheetAverage1.setText(
                                        String.valueOf(Math.round(nsheetAverageReal * 10) / 10));
                                noobsAverage1.setText(String.valueOf(Math.round(noobsAverageReal * 10) / 10));
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
    if (MosharkatRef != null && mosharkatlistener != null) {
      MosharkatRef.removeEventListener(mosharkatlistener);
    }
    if (nasheetRef != null && nasheetlistener != null) {
      nasheetRef.removeEventListener(nasheetlistener);
    }
  }
}
