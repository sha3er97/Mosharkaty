package com.resala.mosharkaty;

import static com.resala.mosharkaty.utility.classes.UtilityClass.userBranch;

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
import com.resala.mosharkaty.utility.classes.EventReport;
import com.resala.mosharkaty.utility.classes.UtilityClass;

import java.util.Calendar;
import java.util.Locale;

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
    String typeText;
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
        typeText = intent.getStringExtra("type");
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
                                            eText.setText(UtilityClass.dateToText(dayOfMonth, monthOfYear, year)),
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
        currentEvent.setValue(
                new EventReport(
                        reportCount.getText().toString().trim(),
                        eText.getText().toString(),
                        reportDescription_et.getText().toString(),
                        reportHead_et.getText().toString().trim(),
                        place_et.getText().toString().trim(),
                        money_et.getText().toString().trim(),
                        typeText));

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
        if (TextUtils.isEmpty(date)) {
            eText.setError("Required.");
            valid = false;
        } else if (UtilityClass.checkFutureDate(date, year, month, day)) {
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
