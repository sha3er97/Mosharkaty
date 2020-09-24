package com.resala.mosharkaty.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.resala.mosharkaty.R;
import com.resala.mosharkaty.ui.adapters.EventsReportsAdapter;
import com.resala.mosharkaty.utility.classes.EventReport;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

import static android.content.ContentValues.TAG;
import static com.resala.mosharkaty.LoginActivity.userBranch;
import static com.resala.mosharkaty.fragments.AdminShowMosharkatFragment.months;

public class ShowEventsReportsFragment extends Fragment {
    View view;
    EventsReportsAdapter adapter;
    ArrayList<EventReport> eventReports = new ArrayList<>();
    FirebaseDatabase database;
    int month;
    Spinner month_et;
    DatabaseReference ReportsRef;
    ValueEventListener Reportslistener;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_show_event_reports, container, false);
        database = FirebaseDatabase.getInstance();
        RecyclerView recyclerView = view.findViewById(R.id.reportsRecyclerView);
        month_et = view.findViewById(R.id.month_et);

        final Calendar cldr = Calendar.getInstance(Locale.US);
        month = cldr.get(Calendar.MONTH);
        ArrayAdapter<String> aa =
                new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, months);
        aa.setDropDownViewResource(R.layout.spinner_dropdown);
        // Setting the ArrayAdapter data on the Spinner
        month_et.setAdapter(aa);
        month_et.setSelection(Math.max(month, 0));

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EventsReportsAdapter(eventReports, getContext());
        recyclerView.setAdapter(adapter);

        ImageButton refreshBtn = view.findViewById(R.id.refresh_btn);
        refreshBtn.setOnClickListener(
                v -> {
                    ReportsRef = database.getReference("reports").child(userBranch);
                    Reportslistener =
                            ReportsRef.child(month_et.getSelectedItem().toString())
                                    .addValueEventListener(
                                            new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    eventReports.clear();
                                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                        EventReport eventReport = snapshot.getValue(EventReport.class);
                                                        if (eventReport != null) {
                                                            eventReport.setKey(snapshot.getKey());
                                                            eventReports.add(eventReport);

                                                        } else {
                                                            Toast.makeText(
                                                                    getContext(), "something went wrong", Toast.LENGTH_SHORT)
                                                                    .show();
                                                        }
                                                    }
                                                    Collections.sort(eventReports);
                                                    adapter.notifyDataSetChanged();
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    // Failed to read value
                                                    Log.w(TAG, "Failed to read value.", error.toException());
                                                }
                                            });
                });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (ReportsRef != null && Reportslistener != null) {
            ReportsRef.removeEventListener(Reportslistener);
        }
    }
}
