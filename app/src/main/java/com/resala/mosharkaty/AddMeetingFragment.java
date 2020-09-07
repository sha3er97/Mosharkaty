package com.resala.mosharkaty;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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

import java.util.Calendar;
import java.util.Locale;

import static com.resala.mosharkaty.LoginActivity.userBranch;
import static com.resala.mosharkaty.NewAccount.branches;

public class AddMeetingFragment extends Fragment {
    View view;
    public static String[] meetingsTypesNormal = {
            "اجتماع لجنة فرق",
            "اجتماع لجنة معارض/قوافل",
            "اجتماع لجنة دور ايواء",
            "اجتماع لجنة ولاد عم",
            "اجتماع لجنة نفسك في ايه",
            "اجتماع لجنة hr",
            "اجتماع لجنة متابعة",
            "اجتماع لجنة اتصالات",
            "اجتماع لجنة نقل",
            "اجتماع لجان مجمع",
            "اجتماع ادارة الفرع",
            "اجتماع مسؤولين",
            "اجتماع فريق عمل"
    };
    public static String[] meetingsTypesMarkzy = {
            "اجتماع مركزية فرق",
            "اجتماع مركزية معارض/قوافل",
            "اجتماع مركزية دور ايواء",
            "اجتماع مركزية ولاد عم",
            "اجتماع مركزية نفسك في ايه",
            "اجتماع مركزية hr",
            "اجتماع مركزية متابعة",
            "اجتماع مركزية اتصالات",
            "اجتماع لجنة دعايا",
            "اجتماع لجان مجمع",
            "اجتماع ادارة فنية",
            "اجتماع مسؤولين النشاط",
            "اجتماع فريق عمل النشاط"
    };
    public static String[] meetingsReason = {
            "متابعة شغل الشهر", "تقييم الشغل", "حل مشكلة", "تواصل/ترفيه", "تخطيط"
    };
    public static String[] meetingsPlace = {"zoom النشاط", "الفرع", "اونلاين اخر"};
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
        meetingCount = view.findViewById(R.id.meetingCount);
        meetingDescription_et = view.findViewById(R.id.meetingDescription_et);

        if (userBranch != null) {

            final DatabaseReference MeetingsRef = database.getReference("meetings").child(userBranch);

            addMeeting_btn.setOnClickListener(
                    v -> {
                        if (!validateForm()) return;
                        String date = eText.getText().toString();
                        String[] dateParts = date.split("/", 2);
                        DatabaseReference currentEvent =
                                MeetingsRef.child(String.valueOf(dateParts[1]))
                                        .child(String.valueOf(System.currentTimeMillis() / 1000));
                        DatabaseReference dateRef = currentEvent.child("date");
                        DatabaseReference typeRef = currentEvent.child("type");
                        DatabaseReference headRef = currentEvent.child("head");
                        DatabaseReference descriptionRef = currentEvent.child("description");
                        DatabaseReference countRef = currentEvent.child("count");
                        DatabaseReference reasonRef = currentEvent.child("reason");
                        DatabaseReference locRef = currentEvent.child("location");
                        DatabaseReference fromRef = currentEvent.child("from");
                        DatabaseReference toRef = currentEvent.child("to");

                        headRef.setValue(meetingHead_et.getText().toString().trim());
                        countRef.setValue(meetingCount.getText().toString().trim());
                        descriptionRef.setValue(meetingDescription_et.getText().toString());
                        dateRef.setValue(eText.getText().toString());
                        fromRef.setValue(eText2.getText().toString());
                        toRef.setValue(eText3.getText().toString());
                        typeRef.setValue(type_spin.getSelectedItem().toString());
                        locRef.setValue(place_spin.getSelectedItem().toString());
                        reasonRef.setValue(reason_spin.getSelectedItem().toString());

                        Toast.makeText(getContext(), "Meeting Added..", Toast.LENGTH_SHORT).show();

                        // عشان ماجد
                        meetingHead_et.setText("");
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
                                    getContext(),
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
                                    getContext(),
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
                    final Calendar cldr = Calendar.getInstance(Locale.US);
                    int hour = cldr.get(Calendar.HOUR_OF_DAY);
                    int minutes = cldr.get(Calendar.MINUTE);
                    // time picker dialog
                    picker3 =
                            new TimePickerDialog(
                                    getContext(),
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

        ArrayAdapter aa;
        if (userBranch.equals(branches[9]))
            aa = new ArrayAdapter(getContext(), R.layout.spinner_item, meetingsTypesMarkzy);
        else aa = new ArrayAdapter(getContext(), R.layout.spinner_item, meetingsTypesNormal);

        aa.setDropDownViewResource(R.layout.spinner_dropdown);
        // Setting the ArrayAdapter data on the Spinner
        type_spin.setAdapter(aa);

        ArrayAdapter ab = new ArrayAdapter(getContext(), R.layout.spinner_item, meetingsReason);
        ab.setDropDownViewResource(R.layout.spinner_dropdown);
        // Setting the ArrayAdapter data on the Spinner
        reason_spin.setAdapter(ab);

        ArrayAdapter ac = new ArrayAdapter(getContext(), R.layout.spinner_item, meetingsPlace);
        ac.setDropDownViewResource(R.layout.spinner_dropdown);
        // Setting the ArrayAdapter data on the Spinner
        place_spin.setAdapter(ac);
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
}
