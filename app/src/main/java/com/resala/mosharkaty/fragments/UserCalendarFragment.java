package com.resala.mosharkaty.fragments;

import static android.content.ContentValues.TAG;
import static com.resala.mosharkaty.utility.classes.UtilityClass.BRANCHES_COUNT;
import static com.resala.mosharkaty.utility.classes.UtilityClass.branches;
import static com.resala.mosharkaty.utility.classes.UtilityClass.userBranch;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.resala.mosharkaty.R;
import com.resala.mosharkaty.ui.adapters.EventsAdapter;
import com.resala.mosharkaty.utility.classes.Event;
import com.resala.mosharkaty.utility.classes.EventItem;
import com.resala.mosharkaty.utility.classes.UtilityClass;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

public class UserCalendarFragment extends androidx.fragment.app.Fragment {
    View view;
    HashMap<String, String> eventsImages = new HashMap<>();
    EventsAdapter adapter;
    ArrayList<EventItem> eventItems = new ArrayList<>();
    FirebaseDatabase database;
    ValueEventListener Eventslistener;
    DatabaseReference EventsRef;

    /**
     * Called to have the fragment instantiate its user interface view. This is optional, and
     * non-graphical fragments can return null. This will be called between {@link #onCreate(Bundle)}
     * and {@link #onActivityCreated(Bundle)}.
     *
     * <p>It is recommended to <strong>only</strong> inflate the layout in this method and move logic
     * that operates on the returned View to {@link #onViewCreated(View, Bundle)}.
     *
     * <p>If you return a View from here, you will later be called in {@link #onDestroyView} when the
     * view is being released.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the
     *                           fragment,
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached
     *                           to. The fragment should not add the view itself, but this can be used to generate the
     *                           LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     *                           saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_calendar, container, false);
        database = FirebaseDatabase.getInstance();
        EventsRef = database.getReference("events");
        final TextView dateTV = view.findViewById(R.id.todayDate);
        final TextView errorTV = view.findViewById(R.id.eventsErrorTV);

        RecyclerView recyclerView = view.findViewById(R.id.eventsRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EventsAdapter(eventItems, getContext());
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
                                int year = cldr.get(Calendar.YEAR);
                                dateTV.setText(UtilityClass.dateToText(day, month - 1, year));
                                if (userBranch != null && dataSnapshot.hasChild(userBranch)) {
                                    for (DataSnapshot snapshot : dataSnapshot.child(userBranch).getChildren()) {
                                        Event event = snapshot.getValue(Event.class);
                                        if (event != null) {
                                            String[] splittedDate = event.date.split("/", 3);
                                            if (Integer.parseInt(splittedDate[0]) >= day
                                                    && Integer.parseInt(splittedDate[1]) == month) {
                                                String url = eventsImages.get(event.type);
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
                                // الايفنتات المركزية
                                if (dataSnapshot.hasChild(branches[BRANCHES_COUNT])) {
                                    for (DataSnapshot snapshot : dataSnapshot.child(branches[BRANCHES_COUNT]).getChildren()) {
                                        Event event = snapshot.getValue(Event.class);
                                        if (event != null) {
                                            String[] splittedDate = event.date.split("/", 2);
                                            if (Integer.parseInt(splittedDate[0]) >= day
                                                    && Integer.parseInt(splittedDate[1]) == month) {
                                                String url = eventsImages.get(event.type);
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
                                Collections.sort(eventItems);
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
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventsRef != null && Eventslistener != null) {
            EventsRef.removeEventListener(Eventslistener);
        }
    }
}
