package com.resala.mosharkaty;

import android.app.DatePickerDialog;
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

public class EventReportDescriptionActivity extends AppCompatActivity {
    DatePickerDialog picker;
    EditText eText;
    FirebaseDatabase database;
    int day;
    int month;
    int year;
    EditText reportCount;
    EditText place_et;
    EditText reportHead_et;
    EditText money_et;
    EditText reportDescription_et;
    TextView type_spin;
    DatabaseReference ReportsRef;

    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_report_description);
        database = FirebaseDatabase.getInstance();
        type_spin = findViewById(R.id.reportTypeSpinner);
        eText = findViewById(R.id.reportDate_et);
        reportHead_et = findViewById(R.id.reportHead_et);
        reportCount = findViewById(R.id.reportCount);
        place_et = findViewById(R.id.place_et);
        money_et = findViewById(R.id.money_et);
        reportDescription_et = findViewById(R.id.reportDescription_et);

        ReportsRef = database.getReference("reports").child(userBranch);

        Intent intent = getIntent();
        String typeText = intent.getStringExtra("type");
        String moneyText = intent.getStringExtra("money");
        String countText = intent.getStringExtra("count");
        String headText = intent.getStringExtra("head");

        String dateText = intent.getStringExtra("date");
        String descriptionText = intent.getStringExtra("description");
        String locationText = intent.getStringExtra("location");
        key = intent.getStringExtra("key");

        type_spin.setText(typeText);
        place_et.setText(locationText);
        money_et.setText(moneyText);
        eText.setText(dateText);
        reportHead_et.setText(headText);
        reportCount.setText(countText);
        reportDescription_et.setText(descriptionText);

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
                                    getApplicationContext(),
                                    (view, year, monthOfYear, dayOfMonth) ->
                                            eText.setText(dayOfMonth + "/" + (monthOfYear + 1)),
                                    year,
                                    month,
                                    day);
                    picker.show();
                });
    }

    public void editReport(View view) {
        if (!validateForm()) return;
        String date = eText.getText().toString();
        String[] dateParts = date.split("/", 2);
        DatabaseReference currentEvent = ReportsRef.child(String.valueOf(dateParts[1])).child(key);
        DatabaseReference dateRef = currentEvent.child("date");
        DatabaseReference headRef = currentEvent.child("head");
        DatabaseReference descriptionRef = currentEvent.child("description");
        DatabaseReference countRef = currentEvent.child("count");
        DatabaseReference moneyRef = currentEvent.child("money");
        DatabaseReference locRef = currentEvent.child("location");

        headRef.setValue(reportHead_et.getText().toString().trim());
        countRef.setValue(reportCount.getText().toString().trim());
        descriptionRef.setValue(reportDescription_et.getText().toString());
        dateRef.setValue(eText.getText().toString());
        locRef.setValue(place_et.getText().toString().trim());
        moneyRef.setValue(money_et.getText().toString().trim());

        Toast.makeText(getApplicationContext(), "Report Edited..", Toast.LENGTH_SHORT).show();
        finish();
    }

    public void deleteReport(View view) {
        String date = eText.getText().toString();
        String[] dateParts = date.split("/", 2);
        ReportsRef.child(String.valueOf(dateParts[1])).child(key).setValue(null);
        Toast.makeText(this, "تم الغاء الحدث", Toast.LENGTH_SHORT).show();
        finish();
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
        String money = money_et.getText().toString();
        if (TextUtils.isEmpty(money)) {
            money_et.setError("Required.");
            valid = false;
        }
        String place = place_et.getText().toString();
        if (TextUtils.isEmpty(place)) {
            place_et.setError("Required.");
            valid = false;
        }
        String description = reportDescription_et.getText().toString();
        if (TextUtils.isEmpty(description)) {
            reportDescription_et.setError("Required.");
            valid = false;
        }
        String count = reportCount.getText().toString();
        if (TextUtils.isEmpty(count)) {
            reportCount.setError("Required.");
            valid = false;
        }
        String head = reportHead_et.getText().toString();
        if (TextUtils.isEmpty(head)) {
            reportHead_et.setError("Required.");
            valid = false;
        }
        if (valid) {
            eText.setError(null);
            place_et.setError(null);
            money_et.setError(null);
            reportDescription_et.setError(null);
            reportCount.setError(null);
            reportHead_et.setError(null);
        }
        return valid;
    }
}
