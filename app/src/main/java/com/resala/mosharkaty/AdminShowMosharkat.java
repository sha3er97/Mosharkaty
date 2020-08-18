package com.resala.mosharkaty;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

import static android.content.ContentValues.TAG;
import static com.resala.mosharkaty.NewAccount.branches;
import static com.resala.mosharkaty.ProfileFragment.userBranch;

public class AdminShowMosharkat extends androidx.fragment.app.Fragment
        implements AdapterView.OnItemSelectedListener {
  View view;
  MosharkatAdapter adapter;
  ArrayList<MosharkaItem> mosharkaItems = new ArrayList<>();
  FirebaseDatabase database;
  public static String[] months = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
  public static String[] days = {
          "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17",
          "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"
  };
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
    view = inflater.inflate(R.layout.admin_show_mosharkat, container, false);
    database = FirebaseDatabase.getInstance();
    userBranch = branches[0]; // todo:: add a way for admin to configure his branch
    final DatabaseReference MosharkatRef = database.getReference("mosharkat").child(userBranch);

    RecyclerView recyclerView = view.findViewById(R.id.mosharkatRecyclerView);
    ImageButton refreshBtn = view.findViewById(R.id.refresh_btn);
    final Spinner month_et = view.findViewById(R.id.month_et);
    final Spinner day_et = view.findViewById(R.id.day_et);
    final Calendar cldr = Calendar.getInstance();
    day = cldr.get(Calendar.DAY_OF_MONTH);
    month = cldr.get(Calendar.MONTH);
    year = cldr.get(Calendar.YEAR);

    // setting spinner
    month_et.setOnItemSelectedListener(this);
    ArrayAdapter aa = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, months);
    aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    // Setting the ArrayAdapter data on the Spinner
    month_et.setAdapter(aa);
    month_et.setSelection(Math.max(month, 0));
    day_et.setOnItemSelectedListener(this);
    ArrayAdapter ab = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, days);
    ab.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    // Setting the ArrayAdapter data on the Spinner
    day_et.setAdapter(ab);
    day_et.setSelection(Math.max(day - 1, 0));

    final TextView count = view.findViewById(R.id.mosharkatMonthCount);

    recyclerView.setHasFixedSize(true);
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    adapter = new MosharkatAdapter(mosharkaItems, getContext());
    recyclerView.setAdapter(adapter);

    // button listener
    refreshBtn.setOnClickListener(
            new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                final int month = Integer.parseInt(month_et.getSelectedItem().toString());
                final int day = Integer.parseInt(day_et.getSelectedItem().toString());

                //            if (month < 1
                //                || month > 12
                //                || month_et.getText().toString().equals("")
                //                || day < 1
                //                || day > 31
                //                || day_et.getText().toString().equals("")) {
                //              Toast.makeText(
                //                      getContext(), "error : choose appropriate month and day",
                // Toast.LENGTH_SHORT)
                //                  .show();
                //              return;
                //            }

                MosharkatRef.child(String.valueOf(month))
                        .addValueEventListener(
                                new ValueEventListener() {
                                  @Override
                                  public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    mosharkaItems.clear();
                                    int counter = 0;
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                      MosharkaItem mosharka = snapshot.getValue(MosharkaItem.class);
                                      String[] splittedDate;
                                      if (mosharka != null) {
                                        splittedDate = mosharka.getMosharkaDate().split("/", 3);
                                        if (Integer.parseInt(splittedDate[0]) == day) {
                                          mosharkaItems.add(mosharka);
                                          counter++;
                                        }
                                      } else {
                                        Toast.makeText(getContext(), "something went wrong", Toast.LENGTH_SHORT)
                                                .show();
                                      }
                                    }
                                    adapter.notifyDataSetChanged();
                                    count.setText(String.valueOf(counter));
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
}
