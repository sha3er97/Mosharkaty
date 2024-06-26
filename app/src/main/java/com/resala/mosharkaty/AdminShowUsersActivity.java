package com.resala.mosharkaty;

import static android.content.ContentValues.TAG;
import static com.resala.mosharkaty.utility.classes.UtilityClass.BRANCHES_COUNT;
import static com.resala.mosharkaty.utility.classes.UtilityClass.branches;
import static com.resala.mosharkaty.utility.classes.UtilityClass.branchesSheets;
import static com.resala.mosharkaty.utility.classes.UtilityClass.myRules;
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
import com.resala.mosharkaty.utility.classes.User;
import com.resala.mosharkaty.utility.classes.UtilityClass;
import com.resala.mosharkaty.utility.classes.Volunteer;

import java.util.ArrayList;
import java.util.Collections;

public class AdminShowUsersActivity extends AppCompatActivity {
    FirebaseDatabase database;
    UsersAdapter adapter;
    ArrayList<String> userItems = new ArrayList<>();
    ArrayList<String> codes = new ArrayList<>();
    DatabaseReference usersRef;
    DatabaseReference mosharkatTab;
    ValueEventListener mosharkatlistener;
    ValueEventListener userslistener;
    TextView usersCount;
    TextView teamCount;
    TextView percent;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_show_users);
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users");
        usersCount = findViewById(R.id.usersCount);
        teamCount = findViewById(R.id.teamCount);
        percent = findViewById(R.id.percent);
        RecyclerView recyclerView = findViewById(R.id.usersRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new UsersAdapter(userItems, getApplicationContext());
        recyclerView.setAdapter(adapter);
        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("لحظات معانا...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
        userslistener =
                usersRef.addValueEventListener(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                codes.clear();
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    User user = snapshot.getValue(User.class);
                                    if (user != null && user.branch.equals(userBranch)) {
                                        codes.add(user.code.toLowerCase());
                                    }
                                }
                                checkForBranch();
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

    private void checkForBranch() {
        String branchSheetLink =
                userBranch.equals(branches[BRANCHES_COUNT])
                        ? branchesSheets.get(branches[0])
                        : branchesSheets.get(userBranch);
        assert branchSheetLink != null;
        DatabaseReference liveSheet = !myRules.useOnlineSheets ? database.getReference(branchSheetLink) : database.getReference("sheets").child(userBranch);
//        DatabaseReference liveSheet = database.getReference(branchSheetLink);
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
                                    if (user != null && !user.degree.matches("(.*)مجمد(.*)")) {
                                        teamCounter++;
                                        if (codes.contains(user.code.toLowerCase())) {
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
                                percent.setText(UtilityClass.getPercentString(percentage));
                                if (percentage < 50) {
                                    percent.setTextColor(getResources().getColor(R.color.red));
                                } else if (percentage > 50 && percentage < 70) {
                                    percent.setTextColor(getResources().getColor(R.color.ourBlue));
                                } else {
                                    percent.setTextColor(getResources().getColor(R.color.green));
                                }
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
