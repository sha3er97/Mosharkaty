package com.resala.mosharkaty;

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
import android.widget.DatePicker;
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

import java.util.Calendar;

import static android.content.ContentValues.TAG;
import static com.resala.mosharkaty.AdminAddGroupMosharka.types;
import static com.resala.mosharkaty.ProfileFragment.userBranch;
import static com.resala.mosharkaty.ProfileFragment.userName;

public class ComposeMosharkaFragment extends androidx.fragment.app.Fragment
        implements AdapterView.OnItemSelectedListener {
    View view;
    DatePickerDialog picker;
    EditText eText;
    private static int mosharkatCount;
    ImageButton addMosharka_btn;
    ImageButton showMosharkaty_btn;
    Spinner spin;
    TextView mosharkatCounterTV;
    FirebaseDatabase database;
    int day;
    int month;
    int year;

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
        view = inflater.inflate(R.layout.compose_new_mosharka_fragment, container, false);
        database = FirebaseDatabase.getInstance();
        final int[] monthSelected = {-1};

        final DatabaseReference MosharkatRef = database.getReference("mosharkat").child(userBranch);
        final DatabaseReference MosharkatCountRef = database.getReference("mosharkatPerMonthCount");

        eText = view.findViewById(R.id.mosharkaDate);
        addMosharka_btn = view.findViewById(R.id.confirmMosharka);
        showMosharkaty_btn = view.findViewById(R.id.showMosharkaty_btn);
        final TextView newMosharkaTV = view.findViewById(R.id.newMosharkaTV);
        spin = view.findViewById(R.id.spinner);
        mosharkatCounterTV = view.findViewById(R.id.mosharkatMonthCount2);
        final Calendar cldr = Calendar.getInstance();
        day = cldr.get(Calendar.DAY_OF_MONTH);
        month = cldr.get(Calendar.MONTH);
        year = cldr.get(Calendar.YEAR);
        eText.setInputType(InputType.TYPE_NULL);
        eText.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addMosharka_btn.setEnabled(true);
                        addMosharka_btn.setBackgroundColor(getResources().getColor(R.color.lighterBlue));
                        newMosharkaTV.setText("سجل المشاركة");
                        // date picker dialog
                        picker =
                                new DatePickerDialog(
                                        getContext(),
                                        new DatePickerDialog.OnDateSetListener() {
                                            @Override
                                            public void onDateSet(
                                                    DatePicker view, int year, final int monthOfYear, int dayOfMonth) {
                                                eText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                                monthSelected[0] = monthOfYear + 1;
                                                mosharkatCounterTV.setText(String.valueOf(mosharkatCount));
                                                // database
                                                MosharkatCountRef.addValueEventListener(
                                                        new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                // This method is called once with the initial value and again
                                                                // whenever data at this location is updated.
                                                                mosharkatCount =
                                                                        dataSnapshot
                                                                                .child(String.valueOf(monthOfYear + 1))
                                                                                .getValue(Integer.class);
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {
                                                                // Failed to read value
                                                                Log.w(TAG, "Failed to read value.", error.toException());
                                                            }
                                                        });
                                            }
                                        },
                                        year,
                                        month,
                                        day);
                        picker.show();
                    }
                });
        spin.setOnItemSelectedListener(this);
        // Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, types);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);

        // buttons click listener
        showMosharkaty_btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getActivity(), ShowMosharkaty.class));
                    }
                });
        addMosharka_btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!validateForm()) return;
                        DatabaseReference currentMosharka =
                                MosharkatRef.child(String.valueOf(monthSelected[0]))
                                        .child(String.valueOf(mosharkatCount));
                        DatabaseReference dateRef = currentMosharka.child("mosharkaDate");
                        DatabaseReference typeRef = currentMosharka.child("mosharkaType");
                        DatabaseReference nameRef = currentMosharka.child("volname");

                        nameRef.setValue(userName);
                        dateRef.setValue(eText.getText().toString());
                        typeRef.setValue(spin.getSelectedItem().toString());

                        MosharkatCountRef.child(String.valueOf(monthSelected[0])).setValue(mosharkatCount + 1);
                        Toast.makeText(getContext(), "تم اضافة مشاركة جديدة..", Toast.LENGTH_SHORT).show();
                        addMosharka_btn.setEnabled(false);
                        addMosharka_btn.setBackgroundColor(
                                getResources().getColor(R.color.common_google_signin_btn_text_light_disabled));
                        newMosharkaTV.setText("تم تسجيل مشاركة لليوم");
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
        } else {
            eText.setError(null);
            return true;
        }
    }
}
