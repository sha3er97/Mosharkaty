package com.resala.mosharkaty;

import static android.content.ContentValues.TAG;
import static com.resala.mosharkaty.utility.classes.UtilityClass.branches;
import static com.resala.mosharkaty.utility.classes.UtilityClass.branchesSheets;
import static com.resala.mosharkaty.utility.classes.UtilityClass.months;
import static com.resala.mosharkaty.utility.classes.UtilityClass.userBranch;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.resala.mosharkaty.ui.adapters.UserHistoryAdapter;
import com.resala.mosharkaty.utility.classes.MosharkaItem;
import com.resala.mosharkaty.utility.classes.UserHistoryItem;
import com.resala.mosharkaty.utility.classes.Volunteer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AdminShowStatisticsActivity extends AppCompatActivity {
    FirebaseDatabase database;
    int month;
    UserHistoryAdapter adapter;
    ArrayList<UserHistoryItem> userHistoryItems = new ArrayList<>();
    DatabaseReference MosharkatRef;
    ValueEventListener mosharkatlistener;
    boolean fari2Filter;
    CheckBox filterFari2Check;
    boolean frozenFilter;
    CheckBox filterFrozenCheck;
    DatabaseReference usersRef;
    ArrayList<String> allFari2 = new ArrayList<>();
    ArrayList<String> allFrozen = new ArrayList<>();
    Spinner month_et;
    int selected_month;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_show_statistics);
        database = FirebaseDatabase.getInstance();
        RecyclerView recyclerView = findViewById(R.id.mosharkatnaRecyclerView);
        month_et = findViewById(R.id.current_month);
        ImageButton refreshBtn = findViewById(R.id.refresh_btn);
        refreshBtn.setEnabled(false); // by default
        final Calendar cldr = Calendar.getInstance(Locale.US);
        month = cldr.get(Calendar.MONTH);
        // setting spinner
        ArrayAdapter<String> aa =
                new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, months);
        aa.setDropDownViewResource(R.layout.spinner_dropdown);
        // Setting the ArrayAdapter data on the Spinner
        month_et.setAdapter(aa);
        month_et.setSelection(Math.max(month, 0));
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new UserHistoryAdapter(userHistoryItems, getApplicationContext());
        recyclerView.setAdapter(adapter);
        filterFari2Check = findViewById(R.id.Filtercheckbox);
        filterFari2Check.setOnClickListener(v -> fari2Filter = filterFari2Check.isChecked());
        filterFrozenCheck = findViewById(R.id.Filter2checkbox);
        filterFrozenCheck.setOnClickListener(v -> frozenFilter = filterFrozenCheck.isChecked());
        String branchSheetLink =
                userBranch.equals(branches[9])
                        ? branchesSheets.get(branches[0])
                        : branchesSheets.get(userBranch);
        assert branchSheetLink != null;
        DatabaseReference liveSheet = database.getReference(branchSheetLink);
        usersRef = liveSheet.child("month_mosharkat");
        usersRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        allFari2.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Volunteer user = snapshot.getValue(Volunteer.class);
                            if (user != null) {
                                if (user.degree.matches("(.*)مجمد(.*)"))
                                    allFrozen.add(user.Volname.trim()); // add all team
                                else allFari2.add(user.Volname.trim()); // add all team
                            }
                        }
                        refreshBtn.setEnabled(true);
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
    }

    public void refreshStatistics(View view) {
        selected_month = Integer.parseInt(month_et.getSelectedItem().toString());
        MosharkatRef = database.getReference("mosharkat").child(userBranch);
        mosharkatlistener =
                MosharkatRef.child(String.valueOf(selected_month))
                        .addValueEventListener(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        userHistoryItems.clear();
                                        HashMap<String, Integer> nameCounting = new HashMap<>();
                                        HashMap<String, String> nameHistory = new HashMap<>();
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            MosharkaItem mosharka = snapshot.getValue(MosharkaItem.class);
                                            String[] splittedDate;
                                            if (mosharka != null) {
                                                if (fari2Filter) {
                                                    if (frozenFilter) { // both
                                                        if (!allFari2.contains(mosharka.getVolname().trim())
                                                                && !allFrozen.contains(mosharka.getVolname().trim()))
                                                            continue;
                                                    } else { // fari2 only
                                                        if (!allFari2.contains(mosharka.getVolname().trim()))
                                                            continue;
                                                    }
                                                } else if (frozenFilter) { // frozen only
                                                    if (!allFrozen.contains(mosharka.getVolname().trim()))
                                                        continue;
                                                }
                                                splittedDate = mosharka.getMosharkaDate().split("/", 3);
                                                if (nameCounting.containsKey(mosharka.getVolname().trim())) {
                                                    // If char is present in charCountMap,
                                                    // incrementing it's count by 1
                                                    nameCounting.put(
                                                            mosharka.getVolname().trim(),
                                                            nameCounting.get(mosharka.getVolname().trim()) + 1);
                                                    nameHistory.put(
                                                            mosharka.getVolname().trim(),
                                                            nameHistory.get(mosharka.getVolname().trim())
                                                                    + ","
                                                                    + splittedDate[0]);
                                                } else {
                                                    // If char is not present in charCountMap,
                                                    // putting this char to charCountMap with 1 as it's value
                                                    nameCounting.put(mosharka.getVolname().trim(), 1);
                                                    nameHistory.put(mosharka.getVolname().trim(), splittedDate[0]);
                                                }
                                            }
                                        }
                                        for (Map.Entry<String, Integer> entry : nameCounting.entrySet()) {
                                            userHistoryItems.add(
                                                    new UserHistoryItem(
                                                            entry.getKey(),
                                                            nameHistory.get(entry.getKey()),
                                                            Integer.parseInt(entry.getValue().toString())));
                                        }
                                        Collections.sort(userHistoryItems);
                                        adapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        // Failed to read value
                                        Log.w(TAG, "Failed to read value.", error.toException());
                                    }
                                });
    }
}
