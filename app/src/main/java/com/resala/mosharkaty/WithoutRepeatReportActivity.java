package com.resala.mosharkaty;

import static android.content.ContentValues.TAG;
import static com.resala.mosharkaty.utility.classes.UtilityClass.branches;
import static com.resala.mosharkaty.utility.classes.UtilityClass.days;
import static com.resala.mosharkaty.utility.classes.UtilityClass.isBetween;
import static com.resala.mosharkaty.utility.classes.UtilityClass.months;
import static com.resala.mosharkaty.utility.classes.UtilityClass.mosharkaTypes;
import static com.resala.mosharkaty.utility.classes.UtilityClass.myRules;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.resala.mosharkaty.ui.adapters.Top5Adapter;
import com.resala.mosharkaty.utility.classes.MosharkaItem;
import com.resala.mosharkaty.utility.classes.Top5Item;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class WithoutRepeatReportActivity extends AppCompatActivity {
    Spinner month_from;
    Spinner month_to;

    Spinner day_from_et;
    Spinner day_to_et;
    Spinner branchSpinner;
    Spinner typeSpinner;
    int day;
    int month;
    int year;
    FirebaseDatabase database;
    DatabaseReference MosharkatRef;

    int start_month;
    int end_month;
    int start_day;
    int end_day;

    Button refresh_btn;
    ArrayList<Top5Item> top5Items3 = new ArrayList<>();
    Top5Adapter adapter3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_without_repeat_report);
        database = FirebaseDatabase.getInstance();
        month_from = findViewById(R.id.month_from);
        month_to = findViewById(R.id.month_to);
        day_from_et = findViewById(R.id.day_from);
        day_to_et = findViewById(R.id.day_to);
        branchSpinner = findViewById(R.id.branchSpinner);
        typeSpinner = findViewById(R.id.typeSpinner);

        refresh_btn = findViewById(R.id.refresh_btn);

        final Calendar cldr = Calendar.getInstance(Locale.US);
        day = cldr.get(Calendar.DAY_OF_MONTH);
        month = cldr.get(Calendar.MONTH);
        year = cldr.get(Calendar.YEAR);

        // setting spinner
        ArrayAdapter<String> aa =
                new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, months);
        aa.setDropDownViewResource(R.layout.spinner_dropdown);
        // Setting the ArrayAdapter data on the Spinner
        month_from.setAdapter(aa);
        month_from.setSelection(Math.max(month, 0));
        month_to.setAdapter(aa);
        month_to.setSelection(Math.max(month, 0));

        ArrayAdapter<String> ab =
                new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, days);
        ab.setDropDownViewResource(R.layout.spinner_dropdown);
        // Setting the ArrayAdapter data on the Spinner
        day_from_et.setAdapter(ab);
        day_from_et.setSelection(Math.max(day - 1, 0));
        day_to_et.setAdapter(ab);
        day_to_et.setSelection(Math.max(day - 1, 0));

        ArrayAdapter<String> ac =
                new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, mosharkaTypes);
        ac.setDropDownViewResource(R.layout.spinner_dropdown);
        // Setting the ArrayAdapter data on the Spinner
        typeSpinner.setAdapter(ac);

        ArrayAdapter<String> ad =
                new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, branches);
        ad.setDropDownViewResource(R.layout.spinner_dropdown);
        // Setting the ArrayAdapter data on the Spinner
        branchSpinner.setAdapter(ad);

        RecyclerView recyclerView3 = findViewById(R.id.top5TypesRecyclerView);
        recyclerView3.setHasFixedSize(true);
        recyclerView3.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter3 = new Top5Adapter(top5Items3, getApplicationContext());
        recyclerView3.setAdapter(adapter3);
    }

    public void refreshReport(View view) {
        start_month = Integer.parseInt(month_from.getSelectedItem().toString());
        end_month = Integer.parseInt(month_to.getSelectedItem().toString());
        if (end_month < start_month) {
            Toast.makeText(getApplicationContext(), "التقرير لا يمكن ان يشمل اكثر من سنة", Toast.LENGTH_SHORT).show();
            return;
        }
        start_day = Integer.parseInt(day_from_et.getSelectedItem().toString());
        end_day = Integer.parseInt(day_to_et.getSelectedItem().toString());
        MosharkatRef = database.getReference("mosharkat").child(branchSpinner.getSelectedItem().toString());
        MosharkatRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        HashMap<String, Integer> nameCounting = new HashMap<>();
                        ArrayList<String> days_of_mosharka = new ArrayList<>();
                        int allVols = 0;
                        for (int m = start_month; m <= end_month; m++) {
                            for (DataSnapshot snapshot : dataSnapshot.child(String.valueOf(m)).getChildren()) {
                                MosharkaItem mosharka = snapshot.getValue(MosharkaItem.class);
                                String[] splittedDate;
                                if (mosharka != null) {
                                    splittedDate = mosharka.getMosharkaDate().split("/", 3);
                                    if (isBetween(m, start_month, end_month, Integer.parseInt(splittedDate[0]), start_day, end_day)
                                            && mosharka.getMosharkaType().equals(typeSpinner.getSelectedItem().toString())) {
                                        if (!days_of_mosharka.contains(mosharka.getMosharkaDate()))
                                            days_of_mosharka.add(mosharka.getMosharkaDate());

                                        allVols++;

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
                            //divideMosharkatByDegree(nameCounting);
                            TextView noRepeatCount = findViewById(R.id.noRepeatCount);
                            TextView withRepeatCount = findViewById(R.id.withRepeatCount);
                            TextView eventsCount = findViewById(R.id.eventsCount);

                            noRepeatCount.setText(String.valueOf(nameCounting.size()));
                            withRepeatCount.setText(String.valueOf(allVols));
                            eventsCount.setText(String.valueOf(days_of_mosharka.size()));

                            ArrayList<Top5Item> top5ItemsArray = new ArrayList<>();
                            int maxVol = 0;
                            for (Map.Entry<String, Integer> entry : nameCounting.entrySet()) {
                                if (entry.getValue() > maxVol) maxVol = entry.getValue();
                                top5ItemsArray.add(
                                        new Top5Item(
                                                entry.getKey(),
                                                entry.getValue(),
                                                100,
                                                false));
                            }
                            Collections.sort(top5ItemsArray);
                            top5Items3.clear();
                            int listsize = 0;
                            for (int i = 0; i < top5ItemsArray.size(); i++) {
                                if (top5ItemsArray.get(i).total >= maxVol - 1) {
                                    listsize++;
                                    top5Items3.add(top5ItemsArray.get(i));
                                }
                                if (listsize >= myRules.top_types_count) break;
                            }
                            adapter3.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Failed to read value
                        Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });

    }

}