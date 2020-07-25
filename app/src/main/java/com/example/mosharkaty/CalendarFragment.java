package com.example.mosharkaty;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CalendarFragment extends androidx.fragment.app.Fragment {
  View view;
  EventsAdapter adapter;
  ArrayList<EventItem> eventItems = new ArrayList<>();
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
    view = inflater.inflate(R.layout.calendar_fragment, container, false);
    RecyclerView recyclerView = view.findViewById(R.id.eventsRecyclerView);
    // recyclerView.setHasFixedSize(true);
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    adapter = new EventsAdapter(eventItems, getContext());
    recyclerView.setAdapter(adapter);

    // TODO :: get events from data base
    for (int i = 0; i <= 3; i++) {
      String title = "ايفنت "+(i+1);
      String day = (i*10+1)+"/"+(i+5);
      String url = "https://i.imgur.com/SYkEfz7.jpg";
      eventItems.add(new EventItem(title, day, url));
    }
    adapter.notifyDataSetChanged();
    return view;
  }
}
