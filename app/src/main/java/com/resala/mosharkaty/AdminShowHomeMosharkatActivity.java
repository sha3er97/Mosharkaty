package com.resala.mosharkaty;

import static android.content.ContentValues.TAG;
import static com.resala.mosharkaty.fragments.HomeFragment.globalFari2Names;
import static com.resala.mosharkaty.utility.classes.UtilityClass.userBranch;

import android.app.ProgressDialog;
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
import com.resala.mosharkaty.ui.adapters.UsersAdapter;
import com.resala.mosharkaty.utility.classes.MosharkaItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

public class AdminShowHomeMosharkatActivity extends AppCompatActivity {
    FirebaseDatabase database;
    UsersAdapter adapter;
    ArrayList<String> completedUsers = new ArrayList<>();
    UsersAdapter adapter2;
    ArrayList<String> notCompletedUsers = new ArrayList<>();
    int month;
    DatabaseReference MosharkatRef;
    TextView completedCount, notCompletedCount;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_show_home);
        database = FirebaseDatabase.getInstance();
        RecyclerView recyclerView = findViewById(R.id.homeRecyclerView); // عمل
        RecyclerView recyclerView2 = findViewById(R.id.homeRecyclerView2); // معملش
        TextView current_month = findViewById(R.id.month_et);
        completedCount = findViewById(R.id.completedTV);
        notCompletedCount = findViewById(R.id.notCompletedTV);

        final Calendar cldr = Calendar.getInstance(Locale.US);
        month = cldr.get(Calendar.MONTH) + 1;
        current_month.setText(String.valueOf(month));
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView2.setHasFixedSize(true);
        recyclerView2.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new UsersAdapter(completedUsers, getApplicationContext());
        recyclerView.setAdapter(adapter);
        adapter2 = new UsersAdapter(notCompletedUsers, getApplicationContext());
        recyclerView2.setAdapter(adapter2);

        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("لحظات معانا...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        notCompletedUsers.addAll(globalFari2Names); // add all team
        checkCompleted();
    }

    private void checkCompleted() {
        MosharkatRef = database.getReference("mosharkat").child(userBranch);
        MosharkatRef.child(String.valueOf(month))
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                completedUsers.clear();
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    MosharkaItem mosharka = snapshot.getValue(MosharkaItem.class);
                                    if (mosharka != null) {
                                        if (mosharka.getMosharkaType().matches("(.*)بيت(.*)")) {
                                            notCompletedUsers.remove(mosharka.getVolname());
                                            completedUsers.add(mosharka.getVolname());
                                        }
                                    }
                                }
                                Collections.sort(completedUsers);
                                adapter.notifyDataSetChanged();
                                adapter2.notifyDataSetChanged();
                                completedCount.setText(String.valueOf(completedUsers.size()));
                                notCompletedCount.setText(String.valueOf(notCompletedUsers.size()));
                                // To dismiss the dialog
                                progress.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Failed to read value
                                Log.w(TAG, "Failed to read value.", error.toException());
                            }
                        });
    }
}
