package com.resala.mosharkaty;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import static android.content.ContentValues.TAG;

public class AdminEvents extends androidx.fragment.app.Fragment
    implements AdapterView.OnItemSelectedListener {
  private static int eventsCount;
  View view;
  String[] types = {"معارض", "كساء", "معرض عرايس", "سيشن", "اورينتيشن"}; // todo :: continue
  DatePickerDialog picker;
  EditText eText;
  Button addEvent_btn;
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
    database = FirebaseDatabase.getInstance();
    view = inflater.inflate(R.layout.admin_events_fragment, container, false);
    final DatabaseReference EventsRef = database.getReference("events");
    final DatabaseReference EventsCountRef = database.getReference("eventsCount");
    EventsCountRef.addListenerForSingleValueEvent(
        new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            eventsCount = dataSnapshot.getValue(Integer.class);
          }

          @Override
          public void onCancelled(@NonNull DatabaseError error) {
            // Failed to read value
            Log.w(TAG, "Failed to read value.", error.toException());
          }
        });

    final EditText EventName_et = view.findViewById(R.id.eventName_et);
    final EditText EventDescription_et = view.findViewById(R.id.eventDescription_et);
    final Spinner spin = (Spinner) view.findViewById(R.id.eventsTypeSpinner);
    eText = (EditText) view.findViewById(R.id.eventDate_et);
    addEvent_btn = view.findViewById(R.id.add_event_btn);

    // buttons click listener
    addEvent_btn.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            DatabaseReference currentEvent = EventsRef.child(String.valueOf(eventsCount));
            DatabaseReference dateRef = currentEvent.child("date");
            DatabaseReference typeRef = currentEvent.child("type");
            DatabaseReference descriptionRef = currentEvent.child("description");
            DatabaseReference nameRef = currentEvent.child("Eventname");

            nameRef.setValue(EventName_et.getText().toString());
            descriptionRef.setValue(EventDescription_et.getText().toString());
            dateRef.setValue(eText.getText().toString());
            typeRef.setValue(spin.getSelectedItem().toString());

            EventsCountRef.setValue(eventsCount + 1);
            Toast.makeText(getContext(), "Event Added..", Toast.LENGTH_SHORT).show();
          }
        });

    // database
    EventsCountRef.addValueEventListener(
        new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.
            eventsCount = dataSnapshot.getValue(Integer.class);
          }

          @Override
          public void onCancelled(@NonNull DatabaseError error) {
            // Failed to read value
            Log.w(TAG, "Failed to read value.", error.toException());
          }
        });

    eText.setInputType(InputType.TYPE_NULL);
    eText.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            final Calendar cldr = Calendar.getInstance();
            int day = cldr.get(Calendar.DAY_OF_MONTH);
            int month = cldr.get(Calendar.MONTH);
            int year = cldr.get(Calendar.YEAR);
            // date picker dialog
            picker =
                new DatePickerDialog(
                    getContext(),
                    new DatePickerDialog.OnDateSetListener() {
                      @Override
                      public void onDateSet(
                          DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        eText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                      }
                    },
                    year,
                    month,
                    day);
            picker.show();
          }
        });
    spin.setOnItemSelectedListener(this);
    // Creating the ArrayAdapter instance having the country list
    ArrayAdapter aa = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, types);
    aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    // Setting the ArrayAdapter data on the Spinner
    spin.setAdapter(aa);

    return view;
  }

  @Override
  public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {}

  @Override
  public void onNothingSelected(AdapterView<?> adapterView) {}
}
