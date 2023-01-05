package com.resala.mosharkaty;

import static android.content.ContentValues.TAG;
import static com.resala.mosharkaty.utility.classes.UtilityClass.userBranch;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.resala.mosharkaty.ui.adapters.EventsAdapter;
import com.resala.mosharkaty.utility.classes.Event;
import com.resala.mosharkaty.utility.classes.EventItem;
import com.resala.mosharkaty.utility.classes.UtilityClass;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AdminEditEventActivity extends AppCompatActivity {
    EventsAdapter adapter;
    ArrayList<EventItem> eventItems = new ArrayList<>();
    FirebaseDatabase database;
    ValueEventListener Eventslistener;
    DatabaseReference EventsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_event);
        database = FirebaseDatabase.getInstance();
        EventsRef = database.getReference("events");
        final TextView errorTV = findViewById(R.id.eventsErrorTV);

        RecyclerView recyclerView = findViewById(R.id.eventsRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new EventsAdapter(eventItems, getApplicationContext());
        recyclerView.setAdapter(adapter);

        Eventslistener =
                EventsRef.addValueEventListener(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                eventItems.clear();
                                errorTV.setVisibility(View.INVISIBLE);
                                boolean eventFound = false;
                                final Calendar cldr = Calendar.getInstance(Locale.US);
                                int day = cldr.get(Calendar.DAY_OF_MONTH);
                                int month = cldr.get(Calendar.MONTH) + 1;
                                if (userBranch != null && dataSnapshot.hasChild(userBranch)) {
                                    for (DataSnapshot snapshot : dataSnapshot.child(userBranch).getChildren()) {
                                        Event event = snapshot.getValue(Event.class);
                                        if (event != null) {
                                            String[] splittedDate = event.date.split("/", 2);
                                            if ((Integer.parseInt(splittedDate[0]) >= day
                                                    && Integer.parseInt(splittedDate[1]) == month)
                                                    || Integer.parseInt(splittedDate[1]) > month) {
                                                String url = UtilityClass.eventsImages.get(event.type);
                                                eventItems.add(
                                                        new EventItem(
                                                                event.Eventname,
                                                                event.date,
                                                                url,
                                                                event.description,
                                                                event.location,
                                                                event.time,
                                                                snapshot.getKey()));
                                                eventFound = true;
                                            }
                                        }
                                    }
                                }
                                if (!eventFound) {
                                    Log.i(TAG, "onDataChange: no events found");
                                    errorTV.setVisibility(View.VISIBLE);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventsRef != null && Eventslistener != null) {
            EventsRef.removeEventListener(Eventslistener);
        }
    }
}
