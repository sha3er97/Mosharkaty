package com.resala.mosharkaty;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.resala.mosharkaty.NewAccount.branches;
import static com.resala.mosharkaty.ProfileFragment.userBranch;

public class AdminShowStatistics extends AppCompatActivity {
    FirebaseDatabase database;
    int month;
    UserHistoryAdapter adapter;
    ArrayList<UserHistoryItem> userHistoryItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_show_statistics);
        database = FirebaseDatabase.getInstance();
        userBranch = branches[0]; // todo:: add a way for admin to configure his branch
        RecyclerView recyclerView = findViewById(R.id.mosharkatnaRecyclerView);
        TextView current_month = findViewById(R.id.current_month);
        final Calendar cldr = Calendar.getInstance();
        month = cldr.get(Calendar.MONTH) + 1;
        current_month.setText(String.valueOf(month));
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new UserHistoryAdapter(userHistoryItems, getApplicationContext());
        recyclerView.setAdapter(adapter);
        final DatabaseReference MosharkatRef = database.getReference("mosharkat").child(userBranch);
        MosharkatRef.child(String.valueOf(month))
                .addValueEventListener(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                userHistoryItems.clear();
                                HashMap<String, Integer> nameCounting = new HashMap<String, Integer>();
                                HashMap<String, String> nameHistory = new HashMap<String, String>();
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    MosharkaItem mosharka = snapshot.getValue(MosharkaItem.class);
                                    String[] splittedDate;
                                    if (mosharka != null) {
                                        splittedDate = mosharka.getMosharkaDate().split("/", 3);
                                        if (nameCounting.containsKey(mosharka.getVolname().trim())) {
                                            // If char is present in charCountMap,
                                            // incrementing it's count by 1
                                            nameCounting.put(
                                                    mosharka.getVolname().trim(), nameCounting.get(mosharka.getVolname().trim()) + 1);
                                            nameHistory.put(
                                                    mosharka.getVolname().trim(),
                                                    nameHistory.get(mosharka.getVolname().trim()) + "," + splittedDate[0]);
                                        } else {
                                            // If char is not present in charCountMap,
                                            // putting this char to charCountMap with 1 as it's value
                                            nameCounting.put(mosharka.getVolname().trim(), 1);
                                            nameHistory.put(mosharka.getVolname().trim(), splittedDate[0]);
                                        }
                                    }
                                }
                                for (Map.Entry entry : nameCounting.entrySet()) {
                                    userHistoryItems.add(
                                            new UserHistoryItem(
                                                    entry.getKey().toString(),
                                                    nameHistory.get(entry.getKey().toString()),
                                                    Integer.parseInt(entry.getValue().toString())));
                                    //                      System.out.println(entry.getKey() + " " +
                                    // entry.getValue());
                                }
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