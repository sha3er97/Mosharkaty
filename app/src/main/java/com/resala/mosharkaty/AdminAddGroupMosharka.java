package com.resala.mosharkaty;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import static android.content.ContentValues.TAG;
import static com.resala.mosharkaty.ProfileFragment.userBranch;

public class AdminAddGroupMosharka extends androidx.fragment.app.Fragment
    implements AdapterView.OnItemSelectedListener {
  public static String[] types = {
          "استكشاف",
          "ولاد عم",
          "اجتماع",
          "اتصالات",
          "نزول الفرع",
          "اخري / بيت",
          "شيت",
          "معرض / قافلة",
          "كرنفال",
          "سيشن / اورينتيشن",
          "دعايا"
  };
  View view;
  ArrayList<String> users = new ArrayList<>();
  DatePickerDialog picker;
    EditText eText;
    EditText editTextPhone;
    Button addMosharka_btn;
    Spinner spin;
    Spinner users_spin;
    AutoCompleteTextView volunteerName_et;
    FirebaseDatabase database;
    int day;
    int month;
    int year;
    DatabaseReference usersRef;
    ValueEventListener userslistener;

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
        view = inflater.inflate(R.layout.admin_add_group_mosharka, container, false);
        database = FirebaseDatabase.getInstance();
        final DatabaseReference MosharkatRef = database.getReference("mosharkat").child(userBranch);
        final DatabaseReference ClosingRef = database.getReference("closings").child(userBranch);

        eText = view.findViewById(R.id.mosharkaDate);
        editTextPhone = view.findViewById(R.id.editTextPhone);
        addMosharka_btn = view.findViewById(R.id.confirmMosharka);
        spin = view.findViewById(R.id.spinner);
        users_spin = view.findViewById(R.id.spinner2);
        volunteerName_et = view.findViewById(R.id.volInGroupTV);
        final Calendar cldr = Calendar.getInstance();
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
                                            eText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year),
                                    year,
                                    month,
                                    day);
                    picker.show();
                });
        // Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(getContext(), R.layout.spinner_item, types);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);
        /**
         * ************************************************************************************************************
         */
        DatabaseReference liveSheet =
                database.getReference("1tsMZ5EwtKrBUGuLFVBvuwpU5ve0JKMsaqK1nNAONj-0");
        usersRef = liveSheet.child("month_mosharkat");
        users_spin.setOnItemSelectedListener(this);
        final ArrayAdapter ab = new ArrayAdapter(getContext(), R.layout.spinner_item, users);
        ab.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Setting the ArrayAdapter data on the Spinner
        users_spin.setAdapter(ab);
        userslistener =
                usersRef.addValueEventListener(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                users.clear();
                                users.add(" ");
                                users_spin.setSelection(0);

                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    Volunteer user = snapshot.getValue(Volunteer.class);
                                    users.add(user.Volname);
                                }
                                Collections.sort(users); // alphapetical
                                ab.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Failed to read value
                                Log.w(TAG, "Failed to read value.", error.toException());
                            }
                        });
        /**
         * ************************************************************************************************************
         */
        // buttons click listener
        addMosharka_btn.setOnClickListener(
                v -> {
                    if (!validateForm()) return;
                    String date = eText.getText().toString();
                    String[] dateParts = date.split("/", 3);
                    String key =
                            System.currentTimeMillis() / (1000 * 60)
                                    + "&"
                                    + dateParts[0]
                                    + "&"
                                    + volunteerName_et.getText().toString().trim();
                    DatabaseReference currentMosharka =
                            MosharkatRef.child(String.valueOf(dateParts[1])).child(key);
                    DatabaseReference dateRef = currentMosharka.child("mosharkaDate");
                    DatabaseReference typeRef = currentMosharka.child("mosharkaType");
                    DatabaseReference nameRef = currentMosharka.child("volname");

                    String mobile = " " + editTextPhone.getText().toString().trim();
                    nameRef.setValue(volunteerName_et.getText().toString().trim().concat(mobile).trim());
                    dateRef.setValue(eText.getText().toString());
                    typeRef.setValue(spin.getSelectedItem().toString());

                    ClosingRef.child(String.valueOf(dateParts[1]))
                            .child(String.valueOf(dateParts[0]))
                            .setValue(0);
                    Toast.makeText(getContext(), "تم اضافة مشاركة جديدة..", Toast.LENGTH_SHORT).show();
                    editTextPhone.setText("");
                });

        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
    volunteerName_et.setText(adapterView.getItemAtPosition(i).toString());
        editTextPhone.setText("");
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    private boolean validateForm() {
        String date = eText.getText().toString();
        String[] parts = date.split("/", 3);
        if (TextUtils.isEmpty(date)) {
            eText.setError("Required.");
            return false;
        } else if (Integer.parseInt(parts[2]) > year
                || Integer.parseInt(parts[1]) > month + 1
                || (Integer.parseInt(parts[1]) == month + 1 && Integer.parseInt(parts[0]) > day)) {
            eText.setError("you can't choose a date in the future.");
            return false;
        }

        String name = volunteerName_et.getText().toString().trim();
        String[] words = name.split(" ", 5);

        if (TextUtils.isEmpty(name)) {
            volunteerName_et.setError("Required.");
            return false;
        }
        if (words.length < 2) {
            volunteerName_et.setError("الاسم لازم يبقي ثنائي علي الاقل.");
            return false;
        } else {
            volunteerName_et.setError(null);
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (usersRef != null && userslistener != null) {
            usersRef.removeEventListener(userslistener);
        }
    }
}
