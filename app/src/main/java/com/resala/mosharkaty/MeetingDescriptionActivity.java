package com.resala.mosharkaty;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Locale;

import static com.resala.mosharkaty.LoginActivity.userBranch;

public class MeetingDescriptionActivity extends AppCompatActivity {
    TextView type_spin;
    TextView place_spin;
    TextView reason_spin;
    DatePickerDialog picker;
    EditText eText;
    TimePickerDialog picker2;
    EditText eText2;
    TimePickerDialog picker3;
    EditText eText3;
    FirebaseDatabase database;
    int day;
    int month;
    int year;
    EditText meetingDescription_et;
    EditText meetingHead_et;
    EditText meetingCount;
    String key;
    DatabaseReference MeetingsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_description);
        database = FirebaseDatabase.getInstance();
        MeetingsRef = database.getReference("meetings").child(userBranch);

        Intent intent = getIntent();
        String typeText = intent.getStringExtra("type");
        String reasonText = intent.getStringExtra("reason");
        String countText = intent.getStringExtra("count");
        String headText = intent.getStringExtra("head");

        String dateText = intent.getStringExtra("date");
        String descriptionText = intent.getStringExtra("description");
        String locationText = intent.getStringExtra("location");
        String fromText = intent.getStringExtra("from");
        String toText = intent.getStringExtra("to");
        key = intent.getStringExtra("key");


        type_spin = findViewById(R.id.meetingTypeSpinner);
        place_spin = findViewById(R.id.meetingTypeSpinner2);
        reason_spin = findViewById(R.id.meetingTypeSpinner3);
        eText = findViewById(R.id.meetingDate_et);
        eText2 = findViewById(R.id.meetingTime_et);
        eText3 = findViewById(R.id.meetingTime_et2);
        meetingHead_et = findViewById(R.id.meetingHead_et);
        meetingCount = findViewById(R.id.meetingCount);
        meetingDescription_et = findViewById(R.id.meetingDescription_et);

        type_spin.setText(typeText);
        place_spin.setText(locationText);
        reason_spin.setText(reasonText);
        eText.setText(dateText);
        eText2.setText(fromText);
        eText3.setText(toText);
        meetingHead_et.setText(headText);
        meetingCount.setText(countText);
        meetingDescription_et.setText(descriptionText);
        final Calendar cldr = Calendar.getInstance(Locale.US);
        day = cldr.get(Calendar.DAY_OF_MONTH);
        month = cldr.get(Calendar.MONTH);
        year = cldr.get(Calendar.YEAR);
        eText.setInputType(InputType.TYPE_NULL);
        eText.setOnClickListener(
                v -> {
                    // date picker dialog
                    picker =
                            new DatePickerDialog(
                                    MeetingDescriptionActivity.this,
                                    (view, year, monthOfYear, dayOfMonth) ->
                                            eText.setText(dayOfMonth + "/" + (monthOfYear + 1)),
                                    year,
                                    month,
                                    day);
                    picker.show();
                });

        eText2.setInputType(InputType.TYPE_NULL);
        eText2.setOnClickListener(
                v -> {
                    int hour = cldr.get(Calendar.HOUR_OF_DAY);
                    int minutes = cldr.get(Calendar.MINUTE);
                    // time picker dialog
                    picker2 =
                            new TimePickerDialog(
                                    MeetingDescriptionActivity.this,
                                    (tp, sHour, sMinute) -> {
                                        int Mhour;
                                        String Mminute;
                                        String am_pm;
                                        Mhour = sHour;
                                        Mminute = String.valueOf(sMinute);
                                        if (sMinute == 0) {
                                            Mminute = "00";
                                        }
                                        if (Mhour > 12) {
                                            am_pm = "PM";
                                            Mhour = Mhour - 12;
                                        } else {
                                            am_pm = "AM";
                                        }
                                        eText2.setText(Mhour + ":" + Mminute + " " + am_pm);
                                    },
                                    hour,
                                    minutes,
                                    false);
                    picker2.show();
                });
        eText3.setInputType(InputType.TYPE_NULL);
        eText3.setOnClickListener(
                v -> {
                    int hour = cldr.get(Calendar.HOUR_OF_DAY);
                    int minutes = cldr.get(Calendar.MINUTE);
                    // time picker dialog
                    picker3 =
                            new TimePickerDialog(
                                    MeetingDescriptionActivity.this,
                                    (tp, sHour, sMinute) -> {
                                        int Mhour;
                                        String Mminute;
                                        String am_pm;
                                        Mhour = sHour;
                                        Mminute = String.valueOf(sMinute);
                                        if (sMinute == 0) {
                                            Mminute = "00";
                                        }
                                        if (Mhour > 12) {
                                            am_pm = "PM";
                                            Mhour = Mhour - 12;
                                        } else {
                                            am_pm = "AM";
                                        }
                                        eText3.setText(Mhour + ":" + Mminute + " " + am_pm);
                                    },
                                    hour,
                                    minutes,
                                    false);
                    picker3.show();
                });
    }

    private boolean validateForm() {
        boolean valid = true;
        String date = eText.getText().toString();
        String[] parts = date.split("/", 2);
        if (TextUtils.isEmpty(date)) {
            eText.setError("Required.");
            valid = false;
        } else if (Integer.parseInt(parts[1]) > month + 1
                || (Integer.parseInt(parts[1]) == month + 1 && Integer.parseInt(parts[0]) > day)) {
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
        if (TextUtils.isEmpty(head)) {
            meetingHead_et.setError("Required.");
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
            eText2.setError(null);
            eText3.setError(null);
        }
        return valid;
    }

    public void editMeeting(View view) {
        if (!validateForm()) return;
        String date = eText.getText().toString();
        String[] dateParts = date.split("/", 2);
        DatabaseReference currentEvent =
                MeetingsRef.child(String.valueOf(dateParts[1])).child(key);
        DatabaseReference dateRef = currentEvent.child("date");
        DatabaseReference headRef = currentEvent.child("head");
        DatabaseReference descriptionRef = currentEvent.child("description");
        DatabaseReference countRef = currentEvent.child("count");
        DatabaseReference fromRef = currentEvent.child("from");
        DatabaseReference toRef = currentEvent.child("to");

        headRef.setValue(meetingHead_et.getText().toString().trim());
        countRef.setValue(meetingCount.getText().toString().trim());
        descriptionRef.setValue(meetingDescription_et.getText().toString());
        dateRef.setValue(eText.getText().toString());
        fromRef.setValue(eText2.getText().toString());
        toRef.setValue(eText3.getText().toString());

        Toast.makeText(getApplicationContext(), "Meeting Edited..", Toast.LENGTH_SHORT).show();
        finish();
    }

    public void deleteMeeting(View view) {
        MeetingsRef.child(key).setValue(null);
        Toast.makeText(this, "تم الغاء الاجتماع", Toast.LENGTH_SHORT).show();
        finish();
    }
}