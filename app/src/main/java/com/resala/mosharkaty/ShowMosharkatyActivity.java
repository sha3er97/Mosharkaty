package com.resala.mosharkaty;

import static android.content.ContentValues.TAG;
import static com.resala.mosharkaty.fragments.HomeFragment.userName;
import static com.resala.mosharkaty.fragments.HomeFragment.userOfficialName;
import static com.resala.mosharkaty.utility.classes.UtilityClass.userBranch;

import android.os.Bundle;
import android.util.Log;
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
import com.resala.mosharkaty.ui.adapters.MosharkatAdapter;
import com.resala.mosharkaty.utility.classes.MosharkaItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

public class ShowMosharkatyActivity extends AppCompatActivity {
  int month;
  MosharkatAdapter adapter;
  ArrayList<MosharkaItem> mosharkaItems = new ArrayList<>();
  FirebaseDatabase database;
  ValueEventListener Mosharkatlistener;
  DatabaseReference MosharkatRef;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_show_mosharkaty);
    database = FirebaseDatabase.getInstance();
    MosharkatRef = database.getReference("mosharkat").child(userBranch);
    RecyclerView recyclerView = findViewById(R.id.mosharkatyRecyclerView);
    TextView current_month = findViewById(R.id.current_month);
    final TextView count = findViewById(R.id.mosharkatyMonthCount);

    final Calendar cldr = Calendar.getInstance(Locale.US);
    month = cldr.get(Calendar.MONTH) + 1;
    current_month.setText(String.valueOf(month));

    recyclerView.setHasFixedSize(true);
    LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
    mLayoutManager.setReverseLayout(true);
    mLayoutManager.setStackFromEnd(true);
    recyclerView.setLayoutManager(mLayoutManager);
    adapter = new MosharkatAdapter(mosharkaItems, getApplicationContext());
    recyclerView.setAdapter(adapter);

    Mosharkatlistener =
        MosharkatRef.child(String.valueOf(month))
            .addValueEventListener(
                new ValueEventListener() {
                  @Override
                  public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mosharkaItems.clear();
                    int counter = 0;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                      MosharkaItem mosharka = snapshot.getValue(MosharkaItem.class);
                      if (mosharka != null) {
                        if (mosharka.getVolname().trim().equals(userName.trim())
                            || mosharka.getVolname().trim().equals(userOfficialName.trim())) {
                          mosharka.setKey(snapshot.getKey());
                          mosharkaItems.add(mosharka);
                          counter++;
                        }
                      } else {
                        Toast.makeText(
                                getApplicationContext(), "something went wrong", Toast.LENGTH_SHORT)
                            .show();
                      }
                    }
                    Collections.sort(mosharkaItems);
                    adapter.notifyDataSetChanged();
                    count.setText(String.valueOf(counter));
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
    if (MosharkatRef != null && Mosharkatlistener != null) {
      MosharkatRef.removeEventListener(Mosharkatlistener);
    }
  }
}
