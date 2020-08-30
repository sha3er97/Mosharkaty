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
import java.util.Collections;

import static android.content.ContentValues.TAG;
import static com.resala.mosharkaty.ProfileFragment.userBranch;

public class AdminShowUsers extends AppCompatActivity {
    FirebaseDatabase database;
    UsersAdapter adapter;
    ArrayList<String> userItems = new ArrayList<>();
    ArrayList<String> codes = new ArrayList<>();
    DatabaseReference usersRef;
    DatabaseReference mosharkatTab;
    ValueEventListener mosharkatlistener;
    ValueEventListener userslistener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_show_users);
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users");
        TextView usersCount = findViewById(R.id.usersCount);
        TextView teamCount = findViewById(R.id.teamCount);
        TextView percent = findViewById(R.id.percent);
        RecyclerView recyclerView = findViewById(R.id.usersRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new UsersAdapter(userItems, getApplicationContext());
        recyclerView.setAdapter(adapter);

        userslistener =
                usersRef.addValueEventListener(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                codes.clear();
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    User user = snapshot.getValue(User.class);
                                    if (user != null && user.branch.equals(userBranch)) {
                                        codes.add(user.code);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Failed to read value
                                Log.w(TAG, "Failed to read value.", error.toException());
                            }
                        });

        DatabaseReference liveSheet =
                database.getReference("1tsMZ5EwtKrBUGuLFVBvuwpU5ve0JKMsaqK1nNAONj-0");
        mosharkatTab = liveSheet.child("month_mosharkat");
        mosharkatlistener =
                mosharkatTab.addValueEventListener(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                userItems.clear();
                                int userCounter = 0;
                                int teamCounter = 0;
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    Volunteer user = snapshot.getValue(Volunteer.class);
                                    if (user != null && !user.degree.equals("مجمد")) {
                                        teamCounter++;
                                        if (codes.contains(user.code)) {
                                            userCounter++;
                                            userItems.add(user.Volname);
                                        }
                                    }
                                }
                                Collections.sort(userItems); // alphapetical
                                adapter.notifyDataSetChanged();
                                usersCount.setText(String.valueOf(userCounter));
                                teamCount.setText(String.valueOf(teamCounter));
                                float percentage = (float) userCounter / teamCounter * 100;
                                percent.setText(Math.round(percentage) + " %");
                                if (percentage < 50) {
                                    percent.setTextColor(getResources().getColor(R.color.red));
                                } else if (percentage > 50 && percentage < 70) {
                                    percent.setTextColor(getResources().getColor(R.color.ourBlue));
                                } else {
                                    percent.setTextColor(getResources().getColor(R.color.green));
                                }
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
        if (usersRef != null && userslistener != null) {
            usersRef.removeEventListener(userslistener);
        }
        if (mosharkatTab != null && mosharkatlistener != null) {
            mosharkatTab.removeEventListener(mosharkatlistener);
        }
    }
}
