package com.resala.mosharkaty.fragments;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.resala.mosharkaty.ShowMosharkatyActivity;
import com.resala.mosharkaty.utility.classes.MosharkaItem;

import java.util.Calendar;
import java.util.Locale;

import static android.content.ContentValues.TAG;
import static com.resala.mosharkaty.LoginActivity.userBranch;
import static com.resala.mosharkaty.fragments.AdminAddGroupMosharkaFragment.mosharkaTypes;
import static com.resala.mosharkaty.fragments.HomeFragment.userOfficialName;

public class ComposeMosharkaFragment extends androidx.fragment.app.Fragment
        implements AdapterView.OnItemSelectedListener {
    View view;
    DatePickerDialog picker;
    EditText eText;
    ImageButton addMosharka_btn;
    ImageButton showMosharkaty_btn;
    Spinner spin;
    FirebaseDatabase database;
    int day;
    int month;
    int year;
    DatabaseReference appMosharkatRef;
    DatabaseReference branchRef;
    ValueEventListener branchlistener;

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
        view = inflater.inflate(R.layout.fragment_compose_new_mosharka, container, false);
        database = FirebaseDatabase.getInstance();

        eText = view.findViewById(R.id.mosharkaDate);
        addMosharka_btn = view.findViewById(R.id.confirmMosharka);
        showMosharkaty_btn = view.findViewById(R.id.showMosharkaty_btn);
        final TextView newMosharkaTV = view.findViewById(R.id.newMosharkaTV);
        spin = view.findViewById(R.id.spinner);
        final Calendar cldr = Calendar.getInstance(Locale.US);
        day = cldr.get(Calendar.DAY_OF_MONTH);
        month = cldr.get(Calendar.MONTH);
        year = cldr.get(Calendar.YEAR);

        eText.setInputType(InputType.TYPE_NULL);
        eText.setOnClickListener(
                v -> {
                    addMosharka_btn.setEnabled(true);
                    addMosharka_btn.setBackgroundResource(R.drawable.blue_btn);
                    newMosharkaTV.setText(R.string.new_mosharka_btn_txt);
                    // date picker dialog
                    picker =
                            new DatePickerDialog(
                                    getContext(),
                                    (view, year, monthOfYear, dayOfMonth) -> {
                                        eText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                    },
                                    year,
                                    month,
                                    day);
                    picker.show();
                });
        spin.setOnItemSelectedListener(this);
        // Creating the ArrayAdapter instance having the country list
        ArrayAdapter<String> aa =
                new ArrayAdapter<>(getContext(), R.layout.spinner_item, mosharkaTypes);
        aa.setDropDownViewResource(R.layout.spinner_dropdown);
        // Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);

        // buttons click listener
        showMosharkaty_btn.setOnClickListener(
                v -> startActivity(new Intent(getActivity(), ShowMosharkatyActivity.class)));

        addMosharka_btn.setOnClickListener(
                v -> {
                    if (!validateForm()) return;
                    if (userBranch != null) {
                        final DatabaseReference ClosingRef =
                                database.getReference("closings").child(userBranch);
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
                                                        if (userOfficialName.equals(mosharka.getVolname().trim())
                                                                && mosharka.getMosharkaType().matches("(.*)بيت(.*)"))
                                                            isHome = true;
                                                        if (userOfficialName.equals(mosharka.getVolname().trim())
                                                                && eText.getText().toString().equals(mosharka.getMosharkaDate())
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
                                                                    + userOfficialName;

                                                    DatabaseReference currentMosharka =
                                                            appMosharkatRef.child(String.valueOf(dateParts[1])).child(key);

                                                    currentMosharka.setValue(
                                                            new MosharkaItem(
                                                                    userOfficialName,
                                                                    eText.getText().toString(),
                                                                    spin.getSelectedItem().toString()));

                                                    ClosingRef.child(String.valueOf(dateParts[1]))
                                                            .child(String.valueOf(dateParts[0]))
                                                            .setValue(0);
                                                    Toast.makeText(
                                                            getContext(), "تم اضافة مشاركة جديدة..", Toast.LENGTH_SHORT)
                                                            .show();
                                                    addMosharka_btn.setEnabled(false);
                                                    addMosharka_btn.setBackgroundColor(
                                                            getResources()
                                                                    .getColor(R.color.common_google_signin_btn_text_light_disabled));
                                                    newMosharkaTV.setText("تم تسجيل مشاركة لليوم");
                                                } else {
                                                    Toast.makeText(
                                                            getContext(),
                                                            "عذرا .. المشاركة مكررة في اليوم دا او في مشاركة بيت سابقة مسجلة",
                                                            Toast.LENGTH_SHORT)
                                                            .show();
                                                    addMosharka_btn.setEnabled(true);
                                                    addMosharka_btn.setBackgroundResource(R.drawable.blue_btn);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                // Failed to read value
                                                Log.w(TAG, "Failed to read value.", error.toException());
                                            }
                                        });
                    }
                });
        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
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
        } else if (userOfficialName.equals(" ")) {
            Toast.makeText(
                    getContext(), "لا يمكن تسجيل المشاركة الان يرجي مراجعة الكود", Toast.LENGTH_SHORT)
                    .show();
            return false;
        } else {
            eText.setError(null);
            return true;
        }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (branchRef != null && branchlistener != null) {
      branchRef.removeEventListener(branchlistener);
    }
  }
}
