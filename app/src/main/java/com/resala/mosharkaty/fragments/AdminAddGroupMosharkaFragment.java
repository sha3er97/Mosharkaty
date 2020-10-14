package com.resala.mosharkaty.fragments;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.resala.mosharkaty.R;
import com.resala.mosharkaty.utility.classes.CustomSearchableSpinner;
import com.resala.mosharkaty.utility.classes.MosharkaItem;
import com.resala.mosharkaty.utility.classes.NasheetVolunteer;
import com.resala.mosharkaty.utility.classes.Volunteer;
import com.resala.mosharkaty.utility.classes.normalVolunteer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.resala.mosharkaty.LoginActivity.allVolunteersByName;
import static com.resala.mosharkaty.LoginActivity.allVolunteersByPhone;
import static com.resala.mosharkaty.LoginActivity.userBranch;
import static com.resala.mosharkaty.NewAccountActivity.branches;
import static com.resala.mosharkaty.StarterActivity.branchesSheets;

public class AdminAddGroupMosharkaFragment extends androidx.fragment.app.Fragment
        implements AdapterView.OnItemSelectedListener {
    public static String[] mosharkaTypes = {
            "استكشاف",
            "ولاد عم",
            "اجتماع",
            "اتصالات",
            "شيت",
            "معرض / قافلة",
            "كرنفال",
            "نقل",
            "فرز",
            "سيشن / اورينتيشن",
            "دعايا",
            "اوتينج",
            "كامب",
            "نزول الفرع",
            "اخري / بيت"
    };
  View view;
  ArrayList<String> users = new ArrayList<>();
  ArrayList<String> phones = new ArrayList<>();
  ArrayList<String> allNsheet = new ArrayList<>();
  ArrayList<String> allFari2 = new ArrayList<>();

  DatePickerDialog picker;
  EditText eText;
  Button addMosharka_btn;
  Spinner spin;
  CustomSearchableSpinner users_spin;
  CustomSearchableSpinner phoneSpinner;
  Spinner nasheetSpinner;
  Spinner fari2Spinner;

  TextView volunteerName_et;
  TextView editTextPhone;
  FirebaseDatabase database;
  int day;
  int month;
  int year;
  DatabaseReference usersRef;
  DatabaseReference appMosharkatRef;
  ValueEventListener userslistener;
  DatabaseReference nasheetRef;
  ValueEventListener nasheetlistener;
  DatabaseReference fari2Ref;
  ValueEventListener fari2listener;

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
   * @param inflater The LayoutInflater object that can be used to inflate any views in the
   *     fragment,
   * @param container If non-null, this is the parent view that the fragment's UI should be attached
   *     to. The fragment should not add the view itself, but this can be used to generate the
   *     LayoutParams of the view.
   * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
   *     saved state as given here.
   * @return Return the View for the fragment's UI, or null.
   */
  @Nullable
  @Override
  public View onCreateView(
          @NonNull LayoutInflater inflater,
          @Nullable ViewGroup container,
          @Nullable Bundle savedInstanceState) {
      view = inflater.inflate(R.layout.fragment_admin_add_group_mosharka, container, false);
      database = FirebaseDatabase.getInstance();
      final DatabaseReference MosharkatRef = database.getReference("mosharkat").child(userBranch);
      final DatabaseReference ClosingRef = database.getReference("closings").child(userBranch);

      eText = view.findViewById(R.id.mosharkaDate);
      addMosharka_btn = view.findViewById(R.id.confirmMosharka);
      spin = view.findViewById(R.id.spinner);
      users_spin = view.findViewById(R.id.spinner2);
      phoneSpinner = view.findViewById(R.id.phoneSpinner);
    editTextPhone = view.findViewById(R.id.editTextPhone);
    volunteerName_et = view.findViewById(R.id.volInGroupTV);
    nasheetSpinner = view.findViewById(R.id.nasheetSpinner);
    fari2Spinner = view.findViewById(R.id.fari2Spinner);

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
                                          eText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year),
                                  year,
                                  month,
                                  day);
                  picker.show();
              });

      ArrayAdapter<String> aa =
              new ArrayAdapter<>(getContext(), R.layout.spinner_item, mosharkaTypes);
    aa.setDropDownViewResource(R.layout.spinner_dropdown);
    // Setting the ArrayAdapter data on the Spinner
    spin.setAdapter(aa);
    /**
     * ***********************************************************************************************************************************
     */
    phones.clear();
    for (Map.Entry<String, normalVolunteer> entry : allVolunteersByPhone.entrySet()) {
      //      normalVolunteer normalVolunteer = (normalVolunteer) entry.getValue();
      //      phones.add(normalVolunteer.phone_text);
      phones.add(entry.getKey());
    }
    ArrayAdapter<String> ac = new ArrayAdapter<>(getContext(), R.layout.spinner_item, phones);
    ac.setDropDownViewResource(R.layout.spinner_dropdown);
    // Setting the ArrayAdapter data on the Spinner
    phoneSpinner.setAdapter(ac);
    phoneSpinner.setSelection(0, false);
    phoneSpinner.setOnItemSelectedListener(this);

    /**
     * ************************************************************************************************************
     */
    users.clear();
    for (Map.Entry<String, normalVolunteer> entry : allVolunteersByName.entrySet()) {
      //      normalVolunteer normalVolunteer = (normalVolunteer) entry.getValue();
      //      users.add(normalVolunteer.Volname);
      users.add(entry.getKey());
    }
    final ArrayAdapter<String> ab = new ArrayAdapter<>(getContext(), R.layout.spinner_item, users);
    ab.setDropDownViewResource(R.layout.spinner_dropdown);
    // Setting the ArrayAdapter data on the Spinner
    users_spin.setAdapter(ab);
    users_spin.setSelection(0, false);
    users_spin.setOnItemSelectedListener(this);

    /**
     * ************************************************************************************************************
     */
      final ArrayAdapter<String> ad =
              new ArrayAdapter<>(getContext(), R.layout.spinner_item, allNsheet);
    ad.setDropDownViewResource(R.layout.spinner_dropdown);
    // Setting the ArrayAdapter data on the Spinner
    nasheetSpinner.setAdapter(ad);
    nasheetSpinner.setSelection(0, false);
    nasheetSpinner.setOnItemSelectedListener(this);

    nasheetRef = database.getReference("nasheet").child(userBranch);
      nasheetlistener =
              nasheetRef.addValueEventListener(
                      new ValueEventListener() {
                          @Override
                          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                              allNsheet.clear();
                              for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                  NasheetVolunteer nasheetVolunteer = snapshot.getValue(NasheetVolunteer.class);
                                  assert nasheetVolunteer != null;
                                  allNsheet.add(snapshot.getKey().trim());
                              }
                              Collections.sort(allNsheet); // alphapetical
                              ad.notifyDataSetChanged();
                          }

                          @Override
                          public void onCancelled(@NonNull DatabaseError error) {
                              // Failed to read value
                              Log.w(TAG, "Failed to read value.", error.toException());
                          }
                      });
    /**
     * *************************************************************************************************************
     */
      String branchSheetLink =
              userBranch.equals(branches[9])
                      ? branchesSheets.get(branches[0])
                      : branchesSheets.get(userBranch);
    assert branchSheetLink != null;
    DatabaseReference liveSheet = database.getReference(branchSheetLink);
    fari2Ref = liveSheet.child("month_mosharkat");
      final ArrayAdapter<String> ae =
              new ArrayAdapter<>(getContext(), R.layout.spinner_item, allFari2);
    ae.setDropDownViewResource(R.layout.spinner_dropdown);
    fari2Spinner.setSelection(0, false);
    fari2Spinner.setOnItemSelectedListener(this);
    fari2Spinner.setAdapter(ae);

      fari2listener =
              fari2Ref.addValueEventListener(
                      new ValueEventListener() {
                          @Override
                          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                              allFari2.clear();

                              for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                  Volunteer user = snapshot.getValue(Volunteer.class);
                                  assert user != null;
                                  allFari2.add(user.Volname.trim());
                              }
                              Collections.sort(allFari2); // alphapetical
                              ae.notifyDataSetChanged();
                          }

                          @Override
                          public void onCancelled(@NonNull DatabaseError error) {
                              // Failed to read value
                              Log.w(TAG, "Failed to read value.", error.toException());
                          }
                      });
    /**
     * *************************************************************************************************************
     */
    // buttons click listener
    addMosharka_btn.setOnClickListener(
            v -> {
                if (!validateForm()) return;

                appMosharkatRef = database.getReference("mosharkat").child(userBranch);
                String date = eText.getText().toString();
                String[] dateParts = date.split("/", 3);
                appMosharkatRef
                        .child(dateParts[1])
                        .addListenerForSingleValueEvent(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        boolean duplicate = false;
                                        boolean isHome = false;
                                        addMosharka_btn.setEnabled(false);
                                        addMosharka_btn.setBackgroundColor(
                                                getResources()
                                                        .getColor(R.color.common_google_signin_btn_text_light_disabled));
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            MosharkaItem mosharka = snapshot.getValue(MosharkaItem.class);
                                            if (mosharka != null) {
                                                if (volunteerName_et
                                                        .getText()
                                                        .toString()
                                                        .equals(mosharka.getVolname().trim())
                                                        && mosharka.getMosharkaType().matches("(.*)بيت(.*)"))
                                                    isHome = true;
                                                if (volunteerName_et
                                                        .getText()
                                                        .toString()
                                                        .equals(mosharka.getVolname().trim())
                                                        && (eText.getText().toString().equals(mosharka.getMosharkaDate()))
                                                        || (spin.getSelectedItem().toString().matches("(.*)بيت(.*)")
                                                        && isHome)) {
                                                    duplicate = true;
                                                    break;
                                                }
                                            }
                                        }
                                        if (!duplicate) {
                                            String key =
                                                    System.currentTimeMillis() / (1000 * 60)
                                                            + "&"
                                                            + dateParts[0]
                                                            + "&"
                                                            + volunteerName_et.getText().toString().trim();
                                            DatabaseReference currentMosharka =
                                                    MosharkatRef.child(String.valueOf(dateParts[1])).child(key);
                                            currentMosharka.setValue(
                                                    new MosharkaItem(
                                                            volunteerName_et.getText().toString().trim(),
                                                            eText.getText().toString(),
                                                            spin.getSelectedItem().toString()));

                                            ClosingRef.child(String.valueOf(dateParts[1]))
                                                    .child(String.valueOf(dateParts[0]))
                                                    .setValue(0);
                                            Toast.makeText(getContext(), "تم اضافة مشاركة جديدة..", Toast.LENGTH_SHORT)
                                                    .show();
                                            editTextPhone.setText("");
                                            volunteerName_et.setText("");
                                        } else {
                                            Toast.makeText(
                                                    getContext(),
                                                    "عذرا .. المشاركة مكررة في اليوم دا او في مشاركة بيت سابقة مسجلة",
                                                    Toast.LENGTH_SHORT)
                                                    .show();
                                        }
                                        addMosharka_btn.setEnabled(true);
                                        addMosharka_btn.setBackgroundResource(R.drawable.blue_btn);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        // Failed to read value
                                        Log.w(TAG, "Failed to read value.", error.toException());
                                    }
                                });
            });

    return view;
  }

  @Override
  public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
    //    editTextPhone = view.findViewById(R.id.editTextPhone);
    //    volunteerName_et = view.findViewById(R.id.volInGroupTV);
      if (adapterView.getId() == R.id.spinner2
              || adapterView.getId() == R.id.nasheetSpinner
              || adapterView.getId() == R.id.fari2Spinner) {
          volunteerName_et.setText(adapterView.getItemAtPosition(i).toString().trim());
          try {
              normalVolunteer normalVolunteer =
                      allVolunteersByName.get(adapterView.getItemAtPosition(i).toString().trim());
              assert normalVolunteer != null;
              editTextPhone.setText(normalVolunteer.phone_text);
          } catch (NullPointerException e) {
              volunteerName_et.setText("");
              editTextPhone.setText("الاسم غير موجود في الشيت حاليا");
          }
    } else if (adapterView.getId() == R.id.phoneSpinner) {
      editTextPhone.setText(adapterView.getItemAtPosition(i).toString());
          normalVolunteer normalVolunteer =
                  allVolunteersByPhone.get(adapterView.getItemAtPosition(i).toString().trim());
      assert normalVolunteer != null;
      volunteerName_et.setText(normalVolunteer.Volname.trim());
    }
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
    } else {
        eText.setError(null);
    }

    String name = volunteerName_et.getText().toString().trim();
    if (TextUtils.isEmpty(name)) {
      volunteerName_et.setError("Required.");
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
    if (nasheetRef != null && nasheetlistener != null) {
      nasheetRef.removeEventListener(nasheetlistener);
    }
    if (fari2Ref != null && fari2listener != null) {
      fari2Ref.removeEventListener(fari2listener);
    }
  }
}
