package com.resala.mosharkaty;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
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

import static android.content.ContentValues.TAG;

public class AdminShowMosharkat extends androidx.fragment.app.Fragment {
  View view;
  MosharkatAdapter adapter;
  ArrayList<MosharkaItem> mosharkaItems = new ArrayList<>();
  FirebaseDatabase database;

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
    final DatabaseReference MosharkatRef = database.getReference("mosharkat");
    final DatabaseReference MosharkatCountRef = database.getReference("mosharkatPerMonthCount");

    RecyclerView recyclerView = view.findViewById(R.id.mosharkatRecyclerView);
    ImageButton refreshBtn = view.findViewById(R.id.refresh_btn);
    final EditText month_et = view.findViewById(R.id.month_et);
    final EditText day_et = view.findViewById(R.id.day_et);
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
            final int month = Integer.parseInt(month_et.getText().toString());
            final int day = Integer.parseInt(day_et.getText().toString());

            if (month < 1
                || month > 12
                || month_et.getText().toString().equals("")
                || day < 1
                || day > 31
                || day_et.getText().toString().equals("")) {
              Toast.makeText(
                      getContext(), "error : choose appropriate month and day", Toast.LENGTH_SHORT)
                  .show();
              return;
            }
            // data base
            //            MosharkatCountRef.addValueEventListener(
            //                new ValueEventListener() {
            //                  @Override
            //                  public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            //                    // This method is called once with the initial value and again
            //                    // whenever data at this location is updated.
            //                    count.setText(
            //                        String.valueOf(
            //
            // dataSnapshot.child(String.valueOf(month)).getValue(Integer.class)));
            //                  }
            //
            //                  @Override
            //                  public void onCancelled(@NonNull DatabaseError error) {
            //                    // Failed to read value
            //                    Log.w(TAG, "Failed to read value.", error.toException());
            //                  }
            //                });

            MosharkatRef.child(String.valueOf(month))
                .addValueEventListener(
                    new ValueEventListener() {
                      @Override
                      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mosharkaItems.clear();
                        int counter = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                          MosharkaItem mosharka = snapshot.getValue(MosharkaItem.class);
                          String[] splittedDate = mosharka.getMosharkaDate().split("/", 3);
                          if (Integer.parseInt(splittedDate[0]) == day) {
                            mosharkaItems.add(mosharka);
                            counter++;
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
}
