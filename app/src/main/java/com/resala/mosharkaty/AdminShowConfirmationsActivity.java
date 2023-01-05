package com.resala.mosharkaty;

import static android.content.ContentValues.TAG;
import static com.resala.mosharkaty.utility.classes.UtilityClass.branches;
import static com.resala.mosharkaty.utility.classes.UtilityClass.myRules;
import static com.resala.mosharkaty.utility.classes.UtilityClass.userBranch;

import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import com.resala.mosharkaty.utility.classes.Event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AdminShowConfirmationsActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener {
    FirebaseDatabase database;
    Spinner eventSpinner;
    ArrayList<String> events = new ArrayList<>();
    ArrayList<String> filtered_confirmations = new ArrayList<>();
    HashMap<String, String> allConfirmations = new HashMap<>();
    int month;
    int day;
    UsersAdapter adapter;
    DatabaseReference EventsRef;
    DatabaseReference confirmationsRef;
    TextView comingCount;
    TextView maybeCount;
    TextView notComingCount;
    TextView totalComing;
    TextView totalConfirmations;
    TextView percent;
    ProgressBar attendanceBar;
    ValueEventListener EventsListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_show_confirmations);
        database = FirebaseDatabase.getInstance();
        EventsRef = database.getReference("events");

        eventSpinner = findViewById(R.id.eventSpinner);
        comingCount = findViewById(R.id.coming);
        maybeCount = findViewById(R.id.maybe);
        notComingCount = findViewById(R.id.not_coming);
        totalComing = findViewById(R.id.totalComing);
        totalConfirmations = findViewById(R.id.totalConfirmations);
        percent = findViewById(R.id.percent);
        attendanceBar = findViewById(R.id.determinateBar);

        final Calendar cldr = Calendar.getInstance(Locale.US);
        month = cldr.get(Calendar.MONTH) + 1;
        day = cldr.get(Calendar.DAY_OF_MONTH);
        // recycler view
        RecyclerView recyclerView = findViewById(R.id.confirmationsRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new UsersAdapter(filtered_confirmations, getApplicationContext());
        recyclerView.setAdapter(adapter);

        // spinner
        events.add(" ");
        final ArrayAdapter<String> ab =
                new ArrayAdapter<>(AdminShowConfirmationsActivity.this, R.layout.spinner_item, events);
        ab.setDropDownViewResource(R.layout.spinner_dropdown);
        // Setting the ArrayAdapter data on the Spinner
        eventSpinner.setSelection(0, false);
        eventSpinner.setOnItemSelectedListener(this);
        eventSpinner.setAdapter(ab);
        EventsListener =
                EventsRef.addValueEventListener(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                events.clear();
                                if (userBranch != null && dataSnapshot.hasChild(userBranch)) {
                                    for (DataSnapshot snapshot : dataSnapshot.child(userBranch).getChildren()) {
                                        Event event = snapshot.getValue(Event.class);
                                        if (event != null) {
                                            String[] splittedDate = event.date.split("/", 2);
                                            if (Integer.parseInt(splittedDate[1]) == month) {
                                                events.add(event.Eventname);
                                            }
                                        }
                                    }
                                }
                                if (dataSnapshot.hasChild(branches[9])) {
                                    for (DataSnapshot snapshot : dataSnapshot.child(branches[9]).getChildren()) {
                                        Event event = snapshot.getValue(Event.class);
                                        if (event != null) {
                                            String[] splittedDate = event.date.split("/", 2);
                                            if (Integer.parseInt(splittedDate[1]) == month) {
                                                events.add(event.Eventname);
                                            }
                                        }
                                    }
                                    ab.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Failed to read value
                                Log.w(TAG, "Failed to read value.", error.toException());
                            }
                        });
    }

    public void filterComing(View view) {
        filtered_confirmations.clear();
        for (Map.Entry<String, String> entry : allConfirmations.entrySet()) {
            if (entry.getValue().equals("جاي")) filtered_confirmations.add(entry.getKey());
        }
        adapter.notifyDataSetChanged();
    }

    public void filterMaybe(View view) {
        filtered_confirmations.clear();
        for (Map.Entry<String, String> entry : allConfirmations.entrySet()) {
            if (entry.getValue().equals("احتمال")) filtered_confirmations.add(entry.getKey());
        }
        adapter.notifyDataSetChanged();
    }

    public void filterNotComing(View view) {
        filtered_confirmations.clear();
        for (Map.Entry<String, String> entry : allConfirmations.entrySet()) {
            if (entry.getValue().equals("معتذر")) filtered_confirmations.add(entry.getKey());
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        filtered_confirmations.clear(); // in case not found
        adapter.notifyDataSetChanged();
        getAllConfirmations(eventSpinner.getSelectedItem().toString());
    }

    private void getAllConfirmations(String eventName) {
        confirmationsRef = database.getReference("confirmations").child(userBranch).child(eventName);
        confirmationsRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int numComing = 0;
                        int numNotComing = 0;
                        int numMaybe = 0;
                        allConfirmations.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (snapshot.hasChild("confirm")) {
                                String confirm = snapshot.child("confirm").getValue(String.class);
                                assert confirm != null;
                                switch (confirm) {
                                    case "جاي":
                                        numComing++;
                                        break;
                                    case "احتمال":
                                        numMaybe++;
                                        break;
                                    case "معتذر":
                                        numNotComing++;
                                        break;
                                }
                                allConfirmations.put(snapshot.getKey(), confirm);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        comingCount.setText(String.valueOf(numComing));
                        totalComing.setText(String.valueOf(numComing));
                        notComingCount.setText(String.valueOf(numNotComing));
                        maybeCount.setText(String.valueOf(numMaybe));
                        int total = numComing + numMaybe + numNotComing;
                        totalConfirmations.setText(String.valueOf(total));
                        float percentage = (float) numComing / total * 100;
                        float maybepercentage = (float) numMaybe / total * 100;

                        attendanceBar.setProgress(Math.round(percentage));
                        attendanceBar.setSecondaryProgress(Math.round(percentage + maybepercentage));
                        percent.setText(Math.round(percentage) + " %");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            attendanceBar.setProgressTintList(
                                    ColorStateList.valueOf(getResources().getColor(R.color.green)));
                            attendanceBar.setSecondaryProgressTintList(
                                    ColorStateList.valueOf(getResources().getColor(R.color.new_yellow_light)));
                        }
                        if (percentage < myRules.attendance_bad) {
                            percent.setTextColor(getResources().getColor(R.color.red));
                        } else if (percentage < myRules.attendance_medium) {
                            percent.setTextColor(getResources().getColor(R.color.ourBlue));
                        } else { // bigger than both
                            percent.setTextColor(getResources().getColor(R.color.green));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventsRef != null && EventsListener != null) {
            EventsRef.removeEventListener(EventsListener);
        }
    }
}
