package com.resala.mosharkaty;

import android.app.DatePickerDialog;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

import static com.resala.mosharkaty.LoginActivity.allVolunteersByName;
import static com.resala.mosharkaty.LoginActivity.allVolunteersByPhone;
import static com.resala.mosharkaty.LoginActivity.userBranch;

public class AddNasheetFragment extends androidx.fragment.app.Fragment
        implements AdapterView.OnItemSelectedListener {
  View view;
  ArrayList<String> users = new ArrayList<>();
  ArrayList<String> phones = new ArrayList<>();
  Spinner users_spin;
  DatePickerDialog picker;
  EditText eText;
  TextView volunteerName_et;
  TextView editTextPhone;
  Button confirmAdd;
  FirebaseDatabase database;
  Spinner phoneSpinner;
  int day;
  int month;
  int year;
  DatabaseReference nasheetRef;

  @Override
  public View onCreateView(
          LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    view = inflater.inflate(R.layout.fragment_add_nasheet, container, false);
    database = FirebaseDatabase.getInstance();
    users_spin = view.findViewById(R.id.spinner3);
    phoneSpinner = view.findViewById(R.id.phoneSpinner2);
    editTextPhone = view.findViewById(R.id.editTextPhone2);
    volunteerName_et = view.findViewById(R.id.volInGroupTV2);
    eText = view.findViewById(R.id.nasheetDate);
    confirmAdd = view.findViewById(R.id.confirmAdd);
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
                                      eText.setText((monthOfYear + 1) + "/" + year),
                              year,
                              month,
                              day);
              picker.show();
            });
    /**
     * *******************************************************************************************************
     */
    for (Map.Entry entry : allVolunteersByName.entrySet()) {
      normalVolunteer normalVolunteer = (normalVolunteer) entry.getValue();
      phones.add(normalVolunteer.phone_text);
    }
    ArrayAdapter ac = new ArrayAdapter(getContext(), R.layout.spinner_item, phones);
    ac.setDropDownViewResource(R.layout.spinner_dropdown);
    // Setting the ArrayAdapter data on the Spinner
    phoneSpinner.setAdapter(ac);
    phoneSpinner.setSelection(0, false);
    phoneSpinner.setOnItemSelectedListener(this);

    /**
     * ************************************************************************************************************
     */
    for (Map.Entry entry : allVolunteersByName.entrySet()) {
      normalVolunteer normalVolunteer = (normalVolunteer) entry.getValue();
      users.add(normalVolunteer.Volname);
    }
    final ArrayAdapter ab = new ArrayAdapter(getContext(), R.layout.spinner_item, users);
    ab.setDropDownViewResource(R.layout.spinner_dropdown);
    // Setting the ArrayAdapter data on the Spinner
    users_spin.setAdapter(ab);
    users_spin.setSelection(0, false);
    users_spin.setOnItemSelectedListener(this);

    /**
     * ************************************************************************************************************
     */
    confirmAdd.setOnClickListener(
            v -> {
              if (!validateForm()) return;
              nasheetRef =
                      database
                              .getReference("nasheet")
                              .child(userBranch)
                              .child(volunteerName_et.getText().toString());
              DatabaseReference currentNasheet = nasheetRef.child("first_month");
              currentNasheet.setValue(eText.getText().toString());
              Toast.makeText(getContext(), "تم اضافة نشيط جديد..", Toast.LENGTH_SHORT).show();
              editTextPhone.setText("");
              volunteerName_et.setText("");
              eText.setText("");
            });

    return view;
  }

  @Override
  public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
    //    editTextPhone = view.findViewById(R.id.editTextPhone);
    //    volunteerName_et = view.findViewById(R.id.volInGroupTV);
    if (adapterView.getId() == R.id.spinner3) {
      volunteerName_et.setText(adapterView.getItemAtPosition(i).toString());
      normalVolunteer normalVolunteer =
              allVolunteersByName.get(adapterView.getItemAtPosition(i).toString());
      editTextPhone.setText(normalVolunteer.phone_text);
    } else if (adapterView.getId() == R.id.phoneSpinner2) {
      editTextPhone.setText(adapterView.getItemAtPosition(i).toString());
      normalVolunteer normalVolunteer =
              allVolunteersByPhone.get(adapterView.getItemAtPosition(i).toString());
      volunteerName_et.setText(normalVolunteer.Volname);
    }
  }

  @Override
  public void onNothingSelected(AdapterView<?> adapterView) {
  }

  private boolean validateForm() {
    String name = volunteerName_et.getText().toString().trim();
    if (TextUtils.isEmpty(name)) {
      volunteerName_et.setError("Required.");
      return false;
    } else {
      volunteerName_et.setError(null);
    }

    String date = eText.getText().toString();
    if (TextUtils.isEmpty(date)) {
      eText.setError("Required.");
      return false;
    } else {
      eText.setError(null);
    }
    return true;
  }
}
