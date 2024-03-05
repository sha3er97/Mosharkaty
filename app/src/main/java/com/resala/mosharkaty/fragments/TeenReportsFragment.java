package com.resala.mosharkaty.fragments;

import static android.content.ContentValues.TAG;
import static com.resala.mosharkaty.utility.classes.UtilityClass.userBranch;

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
import com.resala.mosharkaty.R;
import com.resala.mosharkaty.utility.classes.MosharkaItem;
import com.resala.mosharkaty.utility.classes.NasheetVolunteer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TeenReportsFragment extends androidx.fragment.app.Fragment {
    View view;
    FirebaseDatabase database;
    int month;
    int year;
    ArrayList<String> allTeens = new ArrayList<>();
    ArrayList<String> allNoobs = new ArrayList<>();
    DatabaseReference teenRef;
    ValueEventListener teenlistener;
    DatabaseReference MosharkatRef;
    ValueEventListener mosharkatlistener;
    HashMap<Integer, Integer> teenIntervals = new HashMap<>();

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_teen_reports, container, false);
        database = FirebaseDatabase.getInstance();
        final Calendar cldr = Calendar.getInstance(Locale.US);
        TextView current_month = view.findViewById(R.id.current_month);
        month = cldr.get(Calendar.MONTH) + 1;
        year = cldr.get(Calendar.YEAR);
        current_month.setText(String.valueOf(month));
        //******************************************************************************************/
        teenIntervals.put(0, 0); // هذا الشهر الاول
        teenIntervals.put(1, 0); // هذا الشهر الثاني
        teenIntervals.put(2, 0); // هذا الشهر الثالث
        teenIntervals.put(3, 0); // هذا الشهر الرابع
        teenIntervals.put(4, 0); // 4 فاكثر
        //******************************************************************************************/
        teenRef = database.getReference("teens").child(userBranch);
        teenlistener =
                teenRef.addValueEventListener(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                allTeens.clear();
                                allNoobs.clear();
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    NasheetVolunteer teenVolunteer = snapshot.getValue(NasheetVolunteer.class);
                                    assert teenVolunteer != null;
                                    String[] parts = teenVolunteer.first_month.split("/", 2);
                                    Log.d(TAG, parts[0] + "/" + parts[1]);
                                    int months =
                                            (year - Integer.parseInt(parts[1])) * 12
                                                    + (month - Integer.parseInt(parts[0]));
                                    if (months == 0) {
                                        teenIntervals.put(months, teenIntervals.get(months) + 1);
                                        allNoobs.add(snapshot.getKey());
                                    } else {
                                        allTeens.add(snapshot.getKey());
                                        if (months >= 4)
                                            teenIntervals.put(4, teenIntervals.get(4) + 1);
                                        else
                                            teenIntervals.put(months, teenIntervals.get(months) + 1);
                                    }
                                    //                  if (parts[0].equals(String.valueOf(month))
                                    //                      && parts[1].equals(String.valueOf(year)))
                                    // allNoobs.add(snapshot.getKey());
                                    //                  else allTeens.add(snapshot.getKey());
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
        TextView teenCount = view.findViewById(R.id.teenCount);
        TextView noobsCount = view.findViewById(R.id.noobsCount);

        TextView teenAttended = view.findViewById(R.id.teenAttended);
        TextView noobsAttended = view.findViewById(R.id.noobsAttended);

        TextView teenPercentTV = view.findViewById(R.id.teenPercent);
        TextView noobsPercentTV = view.findViewById(R.id.noobsPercent);

        TextView teenMosharkat = view.findViewById(R.id.teenMosharkat);
        TextView noobsMosharkat = view.findViewById(R.id.noobsMosharkat);

        TextView teenAverage1 = view.findViewById(R.id.teenAverage1);
        TextView noobsAverage1 = view.findViewById(R.id.noobsAverage1);

        TextView months1 = view.findViewById(R.id.months1);
        TextView months2 = view.findViewById(R.id.months2);
        TextView months3 = view.findViewById(R.id.months3);
        TextView months4 = view.findViewById(R.id.months4);
        TextView months5 = view.findViewById(R.id.months5);

        teenCount.setText(String.valueOf(allTeens.size()));
        noobsCount.setText(String.valueOf(allNoobs.size()));

        months1.setText(String.valueOf(teenIntervals.get(0)));
        months2.setText(String.valueOf(teenIntervals.get(1)));
        months3.setText(String.valueOf(teenIntervals.get(2)));
        months4.setText(String.valueOf(teenIntervals.get(3)));
        months5.setText(String.valueOf(teenIntervals.get(4)));

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
                                                if (!allTeens.contains(mosharka.getVolname())
                                                        && !allNoobs.contains(mosharka.getVolname()))
                                                    continue;
                                                if (allTeens.contains(mosharka.getVolname())) {
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
                                        teenAttended.setText(String.valueOf(nsheetAttendedCount));
                                        noobsAttended.setText(String.valueOf(noobsAttendedCount));
                                        float nsheetPercent = (float) nsheetAttendedCount / allTeens.size() * 100;
                                        float noobsPercent = (float) noobsAttendedCount / allNoobs.size() * 100;
                                        teenPercentTV.setText(Math.round(nsheetPercent * 10) / 10.0 + " %");
                                        noobsPercentTV.setText(Math.round(noobsPercent * 10) / 10.0 + " %");
                                        for (Map.Entry<String, Integer> entry : nsheetCounting.entrySet()) {
                                            nsheetMosharkatCount += entry.getValue();
                                        }
                                        for (Map.Entry<String, Integer> entry : noobCounting.entrySet()) {
                                            noobsMosharkatCount += entry.getValue();
                                        }
                                        teenMosharkat.setText(String.valueOf(nsheetMosharkatCount));
                                        noobsMosharkat.setText(String.valueOf(noobsMosharkatCount));
                                        float nsheetAverageReal = (float) nsheetMosharkatCount / nsheetAttendedCount;
                                        float noobsAverageReal = (float) noobsMosharkatCount / noobsAttendedCount;
                                        teenAverage1.setText(
                                                String.valueOf(Math.round(nsheetAverageReal * 10) / 10.0));
                                        noobsAverage1.setText(String.valueOf(Math.round(noobsAverageReal * 10) / 10.0));
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
        if (teenRef != null && teenlistener != null) {
            teenRef.removeEventListener(teenlistener);
        }
    }
}
