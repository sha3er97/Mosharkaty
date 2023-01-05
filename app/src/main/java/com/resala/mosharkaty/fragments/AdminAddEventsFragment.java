package com.resala.mosharkaty.fragments;

import static com.resala.mosharkaty.utility.classes.UtilityClass.eventTypes;
import static com.resala.mosharkaty.utility.classes.UtilityClass.userBranch;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.resala.mosharkaty.R;
import com.resala.mosharkaty.utility.classes.Event;
import com.resala.mosharkaty.utility.classes.UtilityClass;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Locale;

public class AdminAddEventsFragment extends androidx.fragment.app.Fragment
        implements AdapterView.OnItemSelectedListener {
    View view;
    DatePickerDialog picker;
    EditText eText;
    TimePickerDialog picker2;
    EditText eText2;
    Button addEvent_btn;
    FirebaseDatabase database;
    ImageView DemoImg;
    Spinner spin;
    int day;
    int month;
    int year;
    EditText EventName_et;
    EditText EventLocation_et;
    EditText EventDescription_et;

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
        database = FirebaseDatabase.getInstance();
        view = inflater.inflate(R.layout.fragment_admin_events, container, false);
        DemoImg = view.findViewById(R.id.demoImg);

        EventName_et = view.findViewById(R.id.eventName_et);
        EventDescription_et = view.findViewById(R.id.eventDescription_et);
        EventLocation_et = view.findViewById(R.id.eventLocation_et);
        spin = view.findViewById(R.id.eventsTypeSpinner);
        eText = view.findViewById(R.id.eventDate_et);
        eText2 = view.findViewById(R.id.eventTime_et);
        addEvent_btn = view.findViewById(R.id.add_event_btn);
        if (userBranch != null) {
            final DatabaseReference EventsRef = database.getReference("events").child(userBranch);

            // buttons click listener
            addEvent_btn.setOnClickListener(
                    v -> {
                        if (!validateForm()) return;
                        DatabaseReference currentEvent =
                                // EventsRef.child(String.valueOf(System.currentTimeMillis() / 1000));
                                EventsRef.push();
                        currentEvent.setValue(
                                new Event(
                                        EventName_et.getText().toString().trim(),
                                        eText.getText().toString(),
                                        EventDescription_et.getText().toString().trim(),
                                        spin.getSelectedItem().toString(),
                                        EventLocation_et.getText().toString().trim(),
                                        eText2.getText().toString()));

                        Toast.makeText(getContext(), "Event Added..", Toast.LENGTH_SHORT).show();

                        // عشان ماجد
                        EventName_et.setText("");
                        EventDescription_et.setText("");
                        eText.setText("");
                        eText2.setText("");
                        EventLocation_et.setText("");
                    });
        }
        eText.setInputType(InputType.TYPE_NULL);
        eText.setOnClickListener(
                v -> {
                    final Calendar cldr = Calendar.getInstance(Locale.US);
                    day = cldr.get(Calendar.DAY_OF_MONTH);
                    month = cldr.get(Calendar.MONTH);
                    year = cldr.get(Calendar.YEAR);
                    // date picker dialog
                    picker =
                            new DatePickerDialog(
                                    requireContext(),
                                    (view, year, monthOfYear, dayOfMonth) ->
                                            eText.setText(UtilityClass.dateToText(dayOfMonth, monthOfYear, year)),
                                    year,
                                    month,
                                    day);
                    picker.show();
                });

        eText2.setInputType(InputType.TYPE_NULL);
        eText2.setOnClickListener(
                v -> {
                    final Calendar cldr = Calendar.getInstance(Locale.US);
                    int hour = cldr.get(Calendar.HOUR_OF_DAY);
                    int minutes = cldr.get(Calendar.MINUTE);
                    // time picker dialog
                    picker2 =
                            new TimePickerDialog(
                                    getContext(),
                                    (tp, sHour, sMinute) -> eText2.setText(UtilityClass.timeToText(sHour, sMinute)),
                                    hour,
                                    minutes,
                                    false);
                    picker2.show();
                });

        spin.setOnItemSelectedListener(this);
        // Creating the ArrayAdapter instance having the country list
        ArrayAdapter<String> aa = new ArrayAdapter<>(requireContext(), R.layout.spinner_item, eventTypes);
        aa.setDropDownViewResource(R.layout.spinner_dropdown);
        // Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);

        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String url = UtilityClass.eventsImages.get(spin.getSelectedItem().toString());
        Picasso.get().load(url).into(DemoImg);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    private boolean validateForm() {
        boolean valid = true;
        String date = eText.getText().toString();
//        String[] parts = date.split("/", 2);
        if (TextUtils.isEmpty(date)) {
            eText.setError("Required.");
            valid = false;
        }
        //        else if (Integer.parseInt(parts[1]) < month + 1
        //                || (Integer.parseInt(parts[1]) == month + 1 && Integer.parseInt(parts[0]) <
        // day)) {
        //            eText.setError("you can't choose a date in the past.");
        //            valid = false;
        //        }
        String time = eText2.getText().toString();
        if (TextUtils.isEmpty(time)) {
            eText2.setError("Required.");
            valid = false;
        }
        String loc = EventLocation_et.getText().toString();
        if (TextUtils.isEmpty(loc)) {
            EventLocation_et.setError("Required.");
            valid = false;
        }
        String description = EventDescription_et.getText().toString();
        String name = EventName_et.getText().toString();
        if (TextUtils.isEmpty(description)) {
            EventDescription_et.setError("Required.");
            valid = false;
        }
        if (TextUtils.isEmpty(name)) {
            EventName_et.setError("Required.");
            valid = false;
        }
        if (valid) {
            eText.setError(null);
            EventDescription_et.setError(null);
            EventName_et.setError(null);
            EventLocation_et.setError(null);
            eText2.setError(null);
        }
        return valid;
    }

}
