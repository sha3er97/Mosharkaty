package com.resala.mosharkaty.fragments;

import static com.resala.mosharkaty.utility.classes.UtilityClass.BRANCHES_COUNT;
import static com.resala.mosharkaty.utility.classes.UtilityClass.branches;
import static com.resala.mosharkaty.utility.classes.UtilityClass.meetingsPlace;
import static com.resala.mosharkaty.utility.classes.UtilityClass.meetingsReason;
import static com.resala.mosharkaty.utility.classes.UtilityClass.meetingsTypesMarkzy;
import static com.resala.mosharkaty.utility.classes.UtilityClass.meetingsTypesNormal;
import static com.resala.mosharkaty.utility.classes.UtilityClass.userBranch;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.resala.mosharkaty.R;
import com.resala.mosharkaty.utility.classes.Meeting;
import com.resala.mosharkaty.utility.classes.UtilityClass;

import java.util.Calendar;
import java.util.Locale;

public class AddMeetingFragment extends Fragment {
    View view;
    Spinner type_spin;
    Spinner place_spin;
    Spinner reason_spin;
    DatePickerDialog picker;
    EditText eText;
    TimePickerDialog picker2;
    EditText eText2;
    TimePickerDialog picker3;
    EditText eText3;
    Button addMeeting_btn;
    FirebaseDatabase database;
    int day;
    int month;
    int year;
    EditText meetingDescription_et;
    EditText meetingHead_et;
    EditText meetingHead_et2;

    EditText meetingCount;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_meeting, container, false);
        database = FirebaseDatabase.getInstance();

        type_spin = view.findViewById(R.id.meetingTypeSpinner);
        place_spin = view.findViewById(R.id.meetingTypeSpinner2);
        reason_spin = view.findViewById(R.id.meetingTypeSpinner3);

        eText = view.findViewById(R.id.meetingDate_et);
        eText2 = view.findViewById(R.id.meetingTime_et);
        eText3 = view.findViewById(R.id.meetingTime_et2);
        addMeeting_btn = view.findViewById(R.id.add_meeting_btn);

        meetingHead_et = view.findViewById(R.id.meetingHead_et);
        meetingHead_et2 = view.findViewById(R.id.meetingHead_et2);

        meetingCount = view.findViewById(R.id.meetingCount);
        meetingDescription_et = view.findViewById(R.id.meetingDescription_et);

        if (userBranch != null) {

            final DatabaseReference MeetingsRef = database.getReference("meetings").child(userBranch);

            addMeeting_btn.setOnClickListener(
                    v -> {
                        if (!validateForm()) return;
                        String date = eText.getText().toString();
                        String[] dateParts = date.split("/", 3);
                        DatabaseReference currentEvent = MeetingsRef.child(String.valueOf(dateParts[1])).push();
                        // .child(String.valueOf(System.currentTimeMillis() / 1000));
                        currentEvent.setValue(
                                new Meeting(
                                        meetingCount.getText().toString().trim(),
                                        eText.getText().toString(),
                                        meetingDescription_et.getText().toString(),
                                        eText2.getText().toString(),
                                        meetingHead_et.getText().toString().trim(),
                                        place_spin.getSelectedItem().toString(),
                                        reason_spin.getSelectedItem().toString(),
                                        eText3.getText().toString(),
                                        type_spin.getSelectedItem().toString(),
                                        meetingHead_et2.getText().toString().trim()));

                        Toast.makeText(getContext(), "تم اضافة الاجتماع ..", Toast.LENGTH_SHORT).show();

                        // avoid replication
                        meetingHead_et.setText("");
                        meetingHead_et2.setText("");
                        meetingCount.setText("");
                        meetingDescription_et.setText("");
                        eText.setText("");
                        eText2.setText("");
                        eText3.setText("");
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
        eText3.setInputType(InputType.TYPE_NULL);
        eText3.setOnClickListener(
                v -> {
                    final Calendar cldr = Calendar.getInstance(Locale.US);
                    int hour = cldr.get(Calendar.HOUR_OF_DAY);
                    int minutes = cldr.get(Calendar.MINUTE);
                    // time picker dialog
                    picker3 =
                            new TimePickerDialog(
                                    getContext(),
                                    (tp, sHour, sMinute) -> eText3.setText(UtilityClass.timeToText(sHour, sMinute)),
                                    hour,
                                    minutes,
                                    false);
                    picker3.show();
                });

        ArrayAdapter<String> aa;
        Log.d("D", "debug ::" + userBranch);
        if (userBranch.equals(branches[BRANCHES_COUNT]))
            aa = new ArrayAdapter<>(requireContext(), R.layout.spinner_item, meetingsTypesMarkzy);
        else aa = new ArrayAdapter<>(requireContext(), R.layout.spinner_item, meetingsTypesNormal);

        aa.setDropDownViewResource(R.layout.spinner_dropdown);
        // Setting the ArrayAdapter data on the Spinner
        type_spin.setAdapter(aa);

        ArrayAdapter<String> ab =
                new ArrayAdapter<>(requireContext(), R.layout.spinner_item, meetingsReason);
        ab.setDropDownViewResource(R.layout.spinner_dropdown);
        // Setting the ArrayAdapter data on the Spinner
        reason_spin.setAdapter(ab);

        ArrayAdapter<String> ac =
                new ArrayAdapter<>(requireContext(), R.layout.spinner_item, meetingsPlace);
        ac.setDropDownViewResource(R.layout.spinner_dropdown);
        // Setting the ArrayAdapter data on the Spinner
        place_spin.setAdapter(ac);
        return view;
    }

    private boolean validateForm() {
        boolean valid = true;
        String date = eText.getText().toString();
        if (TextUtils.isEmpty(date)) {
            eText.setError("Required.");
            valid = false;
        } else if (UtilityClass.checkFutureDate(date, year, month, day)) {
            eText.setError("you can't choose a date in the future.");
            valid = false;
        }
        String time = eText2.getText().toString();
        if (TextUtils.isEmpty(time)) {
            eText2.setError("Required.");
            valid = false;
        }

        String time2 = eText3.getText().toString();
        if (TextUtils.isEmpty(time2)) {
            eText3.setError("Required.");
            valid = false;
        }

        String head = meetingHead_et.getText().toString();
        String[] words = head.split(" ", 5);
        if (TextUtils.isEmpty(head)) {
            meetingHead_et.setError("Required.");
            valid = false;
        } else if (words.length < 2) {
            meetingHead_et.setError("الاسم لازم يبقي ثنائي علي الاقل.");
            valid = false;
        }

        String head2 = meetingHead_et2.getText().toString();
        if (TextUtils.isEmpty(head2)) {
            meetingHead_et2.setError("Required.");
            valid = false;
        }

        String description = meetingDescription_et.getText().toString();
        String count = meetingCount.getText().toString();
        if (TextUtils.isEmpty(description)) {
            meetingDescription_et.setError("Required.");
            valid = false;
        }
        if (TextUtils.isEmpty(count)) {
            meetingCount.setError("Required.");
            valid = false;
        }
        if (valid) {
            eText.setError(null);
            meetingDescription_et.setError(null);
            meetingCount.setError(null);
            meetingHead_et.setError(null);
            meetingHead_et2.setError(null);
            eText2.setError(null);
            eText3.setError(null);
        }
        return valid;
    }
}
