package com.example.mosharkaty;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Calendar;

public class ComposeMosharkaFragment extends androidx.fragment.app.Fragment
    implements AdapterView.OnItemSelectedListener {
  String[] types = {"ولاد عم", "اجتماع", "اتصالات", "نزول الفرع", "اخري"};
  View view;
  DatePickerDialog picker;
  EditText eText;

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
    view = inflater.inflate(R.layout.compose_new_mosharka_fragment, container, false);
    eText = (EditText) view.findViewById(R.id.mosharkaDate);
    eText.setInputType(InputType.TYPE_NULL);
    eText.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            final Calendar cldr = Calendar.getInstance();
            int day = cldr.get(Calendar.DAY_OF_MONTH);
            int month = cldr.get(Calendar.MONTH);
            int year = cldr.get(Calendar.YEAR);
            // date picker dialog
            picker =
                new DatePickerDialog(
                    getContext(),
                    new DatePickerDialog.OnDateSetListener() {
                      @Override
                      public void onDateSet(
                          DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        eText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                      }
                    },
                    year,
                    month,
                    day);
            picker.show();
          }
        });
    Spinner spin = (Spinner) view.findViewById(R.id.spinner);
    spin.setOnItemSelectedListener(this);
    // Creating the ArrayAdapter instance having the country list
    ArrayAdapter aa = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, types);
    aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    // Setting the ArrayAdapter data on the Spinner
    spin.setAdapter(aa);

    return view;
  }

  @Override
  public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {}

  @Override
  public void onNothingSelected(AdapterView<?> adapterView) {}
}
