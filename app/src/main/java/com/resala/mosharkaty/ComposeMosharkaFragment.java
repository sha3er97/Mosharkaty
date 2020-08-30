package com.resala.mosharkaty;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

import static com.resala.mosharkaty.AdminAddGroupMosharka.types;
import static com.resala.mosharkaty.ProfileFragment.userBranch;
import static com.resala.mosharkaty.ProfileFragment.userOfficialName;

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
        if (userBranch != null) {
            final DatabaseReference MosharkatRef = database.getReference("mosharkat").child(userBranch);
            final DatabaseReference ClosingRef = database.getReference("closings").child(userBranch);

            eText = view.findViewById(R.id.mosharkaDate);
            addMosharka_btn = view.findViewById(R.id.confirmMosharka);
            showMosharkaty_btn = view.findViewById(R.id.showMosharkaty_btn);
            final TextView newMosharkaTV = view.findViewById(R.id.newMosharkaTV);
            spin = view.findViewById(R.id.spinner);
            final Calendar cldr = Calendar.getInstance();
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
            ArrayAdapter aa = new ArrayAdapter(getContext(), R.layout.spinner_item, types);
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Setting the ArrayAdapter data on the Spinner
            spin.setAdapter(aa);

            // buttons click listener
            showMosharkaty_btn.setOnClickListener(
                    v -> startActivity(new Intent(getActivity(), ShowMosharkaty.class)));
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
                                        + userOfficialName;
                        DatabaseReference currentMosharka =
                                MosharkatRef.child(String.valueOf(dateParts[1])).child(key);
                        DatabaseReference dateRef = currentMosharka.child("mosharkaDate");
                        DatabaseReference typeRef = currentMosharka.child("mosharkaType");
                        DatabaseReference nameRef = currentMosharka.child("volname");

                        nameRef.setValue(userOfficialName);
                        dateRef.setValue(eText.getText().toString());
                        typeRef.setValue(spin.getSelectedItem().toString());

                        ClosingRef.child(String.valueOf(dateParts[1]))
                                .child(String.valueOf(dateParts[0]))
                                .setValue(0);
                        Toast.makeText(getContext(), "تم اضافة مشاركة جديدة..", Toast.LENGTH_SHORT).show();
                        addMosharka_btn.setEnabled(false);
                        addMosharka_btn.setBackgroundColor(
                                getResources().getColor(R.color.common_google_signin_btn_text_light_disabled));
                        newMosharkaTV.setText("تم تسجيل مشاركة لليوم");
                    });
        }
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
}
