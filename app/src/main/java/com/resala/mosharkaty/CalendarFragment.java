package com.resala.mosharkaty;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import static android.content.ContentValues.TAG;

public class CalendarFragment extends androidx.fragment.app.Fragment {
  View view;
  HashMap<String, String> eventsImages = new HashMap<>();
  EventsAdapter adapter;
  ArrayList<EventItem> eventItems = new ArrayList<>();
  FirebaseDatabase database;

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
   * @param inflater The LayoutInflater object that can be used to inflate any views in the
   *     fragment,
   * @param container If non-null, this is the parent view that the fragment's UI should be attached
   *     to. The fragment should not add the view itself, but this can be used to generate the
   *     LayoutParams of the view.
   * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
   *     saved state as given here.
   * @return Return the View for the fragment's UI, or null.
   */
  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    fillEventsImages();
    view = inflater.inflate(R.layout.calendar_fragment, container, false);
    database = FirebaseDatabase.getInstance();
    final DatabaseReference EventsRef = database.getReference("events");
    final TextView dateTV = view.findViewById(R.id.todayDate);
    RecyclerView recyclerView = view.findViewById(R.id.eventsRecyclerView);
    recyclerView.setHasFixedSize(true);
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    adapter = new EventsAdapter(eventItems, getContext());
    recyclerView.setAdapter(adapter);

    EventsRef.addValueEventListener(
        new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {
            eventItems.clear();
            final Calendar cldr = Calendar.getInstance();
            int day = cldr.get(Calendar.DAY_OF_MONTH);
            int month = cldr.get(Calendar.MONTH) + 1;
            int year = cldr.get(Calendar.YEAR);
            dateTV.setText(day + "/" + month + "/" + year);
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
              Event event = snapshot.getValue(Event.class);
              String[] splittedDate = event.date.split("/", 3);
              if (Integer.parseInt(splittedDate[0]) >= day
                  && Integer.parseInt(splittedDate[1]) == month
                  && Integer.parseInt(splittedDate[2]) == year) {
                String url = eventsImages.get(event.type);
                eventItems.add(new EventItem(event.Eventname, event.date, url, event.description));
                Toast.makeText(getContext(), "events updated", Toast.LENGTH_SHORT).show();
              }
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

  private void fillEventsImages() {     // todo :: continue
    eventsImages.put("معارض", "https://i.imgur.com/cmxhBZJ.jpg");
    eventsImages.put("كساء", "https://i.imgur.com/HROESKW.jpg");
    eventsImages.put("معرض عرايس", "https://i.imgur.com/fPo06rN.jpg");
    eventsImages.put("سيشن", "https://i.imgur.com/SMp2S6b.jpg");
    eventsImages.put("اورينتيشن", "https://i.imgur.com/GV3chTd.jpg");
  }
}
