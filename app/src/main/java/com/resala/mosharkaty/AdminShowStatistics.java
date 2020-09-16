package com.resala.mosharkaty;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.resala.mosharkaty.LoginActivity.userBranch;

public class AdminShowStatistics extends AppCompatActivity {
  FirebaseDatabase database;
  int month;
  UserHistoryAdapter adapter;
  ArrayList<UserHistoryItem> userHistoryItems = new ArrayList<>();
  DatabaseReference MosharkatRef;
  ValueEventListener mosharkatlistener;
  boolean fari2Filter;
  CheckBox filterFari2Check;
  DatabaseReference usersRef;
  ArrayList<String> allFari2 = new ArrayList<>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.admin_show_statistics);
    database = FirebaseDatabase.getInstance();
    RecyclerView recyclerView = findViewById(R.id.mosharkatnaRecyclerView);
    TextView current_month = findViewById(R.id.current_month);
    ImageButton refreshBtn = findViewById(R.id.refresh_btn);
    refreshBtn.setEnabled(false); // by default
    final Calendar cldr = Calendar.getInstance(Locale.US);
    month = cldr.get(Calendar.MONTH) + 1;
    current_month.setText(String.valueOf(month));
    recyclerView.setHasFixedSize(true);
    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    adapter = new UserHistoryAdapter(userHistoryItems, getApplicationContext());
    recyclerView.setAdapter(adapter);
    filterFari2Check = findViewById(R.id.Filtercheckbox);
    filterFari2Check.setOnClickListener(v -> fari2Filter = filterFari2Check.isChecked());
    DatabaseReference liveSheet =
            database.getReference("1tsMZ5EwtKrBUGuLFVBvuwpU5ve0JKMsaqK1nNAONj-0");
    usersRef = liveSheet.child("month_mosharkat");
    usersRef.addListenerForSingleValueEvent(
            new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allFari2.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                  Volunteer user = snapshot.getValue(Volunteer.class);
                  if (user != null && !user.degree.matches("(.*)مجمد(.*)"))
                    allFari2.add(user.Volname.trim()); // add all team
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
    MosharkatRef = database.getReference("mosharkat").child(userBranch);
    mosharkatlistener =
            MosharkatRef.child(String.valueOf(month))
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
                                      if (fari2Filter && !allFari2.contains(mosharka.getVolname().trim()))
                                          continue;
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
                                for (Map.Entry entry : nameCounting.entrySet()) {
                                  userHistoryItems.add(
                                          new UserHistoryItem(
                                                  entry.getKey().toString(),
                                                  nameHistory.get(entry.getKey().toString()),
                                                  Integer.parseInt(entry.getValue().toString())));
                                  //                      System.out.println(entry.getKey() + " " +
                                  // entry.getValue());
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
