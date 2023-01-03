package com.resala.mosharkaty;

import static com.resala.mosharkaty.LoginActivity.userBranch;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Locale;

public class EventToEditActivity extends AppCompatActivity {
    FirebaseDatabase database;
    String key;
    DatabaseReference EventRef;
    DatePickerDialog picker;
    EditText eText;
    TimePickerDialog picker2;
    EditText eText2;
    int day;
    int month;
    int year;
    EditText EventName_et;
    EditText EventLocation_et;
    EditText EventDescription_et;

    Button addEvent_btn;
    Button deleteEvent_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_to_edit);
        database = FirebaseDatabase.getInstance();
        EventRef = database.getReference("events").child(userBranch);

        Intent intent = getIntent();
        String titleText = intent.getStringExtra("title");
        String dateText = intent.getStringExtra("date");
        String descriptionText = intent.getStringExtra("description");
        String locationText = intent.getStringExtra("location");
        String timeText = intent.getStringExtra("time");
        key = intent.getStringExtra("key");

        EventName_et = findViewById(R.id.eventName_et);
        EventDescription_et = findViewById(R.id.eventDescription_et);
        EventLocation_et = findViewById(R.id.eventLocation_et);
        eText = findViewById(R.id.eventDate_et);
        eText2 = findViewById(R.id.eventTime_et);
        addEvent_btn = findViewById(R.id.add_event_btn);
        deleteEvent_btn = findViewById(R.id.delete_event_btn);

        EventName_et.setText(titleText);
        EventDescription_et.setText(descriptionText);
        EventLocation_et.setText(locationText);
        eText.setText(dateText);
        eText2.setText(timeText);

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
                                    EventToEditActivity.this,
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
                    final Calendar cldr = Calendar.getInstance(Locale.US);
                    int hour = cldr.get(Calendar.HOUR_OF_DAY);
                    int minutes = cldr.get(Calendar.MINUTE);
                    // time picker dialog
                    picker2 =
                            new TimePickerDialog(
                                    EventToEditActivity.this,
                                    (tp, sHour, sMinute) -> {
                                        int Mhour;
                                        String Mminute;
                                        String am_pm;
                                        Mhour = sHour;
                                        Mminute = String.valueOf(sMinute);
                                        if (sMinute == 0) {
                                            Mminute = "00";
                                        }
                                        if (Mhour >= 12) {
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
    }

    public void editEvent(View view) {
        if (!validateForm()) return;
        DatabaseReference currentEvent = EventRef.child(key);
        DatabaseReference dateRef = currentEvent.child("date");
        DatabaseReference descriptionRef = currentEvent.child("description");
        DatabaseReference nameRef = currentEvent.child("Eventname");
        DatabaseReference locRef = currentEvent.child("location");
        DatabaseReference timeRef = currentEvent.child("time");

        nameRef.setValue(EventName_et.getText().toString().trim());
        descriptionRef.setValue(EventDescription_et.getText().toString());
        dateRef.setValue(eText.getText().toString());
        timeRef.setValue(eText2.getText().toString());
        locRef.setValue(EventLocation_et.getText().toString().trim());

        Toast.makeText(this, "تم تعديل الايفنت", Toast.LENGTH_SHORT).show();
        finish();
    }

    private boolean validateForm() {
        boolean valid = true;
        String date = eText.getText().toString();
        String[] parts = date.split("/", 2);
        if (TextUtils.isEmpty(date)) {
            eText.setError("Required.");
            valid = false;
        } else if (Integer.parseInt(parts[1]) < month + 1
                || (Integer.parseInt(parts[1]) == month + 1 && Integer.parseInt(parts[0]) < day)) {
            eText.setError("you can't choose a date in the past.");
            valid = false;
        }
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
        }
        return valid;
    }

    public void deleteEvent(View view) {
        EventRef.child(key).setValue(null);
        Toast.makeText(this, "تم الغاء الايفنت", Toast.LENGTH_SHORT).show();
        finish();
    }
}
