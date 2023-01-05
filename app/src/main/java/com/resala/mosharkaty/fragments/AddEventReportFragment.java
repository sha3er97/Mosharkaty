package com.resala.mosharkaty.fragments;

import static com.resala.mosharkaty.utility.classes.UtilityClass.userBranch;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
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
import com.resala.mosharkaty.utility.classes.EventReport;

import java.util.Calendar;
import java.util.Locale;

public class AddEventReportFragment extends Fragment {
    View view;
    public static String[] reportsTypes = {
            "دار ايواء",
            "مستشفي",
            "معرض داخل الفرع",
            "معرض خارج الفرع",
            "ايفنت فرز",
            "ايفنت ولاد عم",
            "كورس / سيشن",
            "كرنفال",
            "ورشة اتصالات",
            "اوتينج",
            "عزومة",
            "كامب",
            "نقلة",
            "اورينتيشن"
    };
    Spinner type_spin;
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
    Button add_report_btn;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_event_report, container, false);
        database = FirebaseDatabase.getInstance();
        type_spin = view.findViewById(R.id.reportTypeSpinner);
        eText = view.findViewById(R.id.reportDate_et);
        reportHead_et = view.findViewById(R.id.reportHead_et);
        reportCount = view.findViewById(R.id.reportCount);
        place_et = view.findViewById(R.id.place_et);
        money_et = view.findViewById(R.id.money_et);
        reportDescription_et = view.findViewById(R.id.reportDescription_et);
        add_report_btn = view.findViewById(R.id.add_report_btn);

        if (userBranch != null) {

            final DatabaseReference ReportsRef = database.getReference("reports").child(userBranch);

            add_report_btn.setOnClickListener(
                    v -> {
                        if (!validateForm()) return;
                        String date = eText.getText().toString();
                        String[] dateParts = date.split("/", 2);
                        DatabaseReference currentEvent = ReportsRef.child(String.valueOf(dateParts[1])).push();
                        // .child(String.valueOf(System.currentTimeMillis() / 1000));
                        currentEvent.setValue(
                                new EventReport(
                                        reportCount.getText().toString().trim(),
                                        eText.getText().toString(),
                                        reportDescription_et.getText().toString(),
                                        reportHead_et.getText().toString().trim(),
                                        place_et.getText().toString().trim(),
                                        money_et.getText().toString().trim(),
                                        type_spin.getSelectedItem().toString()));

                        Toast.makeText(getContext(), "Report Added..", Toast.LENGTH_SHORT).show();

                        // عشان ماجد
                        reportHead_et.setText("");
                        reportCount.setText("");
                        reportDescription_et.setText("");
                        eText.setText("");
                        place_et.setText("");
                        money_et.setText("");
                    });
        }
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
                                    getContext(),
                                    (view, year, monthOfYear, dayOfMonth) ->
                                            eText.setText(dayOfMonth + "/" + (monthOfYear + 1)),
                                    year,
                                    month,
                                    day);
                    picker.show();
                });
        ArrayAdapter<String> ab = new ArrayAdapter<>(getContext(), R.layout.spinner_item, reportsTypes);
        ab.setDropDownViewResource(R.layout.spinner_dropdown);
        // Setting the ArrayAdapter data on the Spinner
        type_spin.setAdapter(ab);
        return view;
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
