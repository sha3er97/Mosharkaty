package com.resala.mosharkaty;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.resala.mosharkaty.ui.adapters.ClosingAdapter;
import com.resala.mosharkaty.utility.classes.ClosingItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import static android.content.ContentValues.TAG;
import static com.resala.mosharkaty.LoginActivity.userBranch;
import static com.resala.mosharkaty.fragments.AdminShowMosharkatFragment.months;

public class AdminShowClosingsActivity extends AppCompatActivity {
    ClosingAdapter adapter;
    ArrayList<ClosingItem> closingItems = new ArrayList<>();
    ClosingAdapter adapter2;
    ArrayList<ClosingItem> closingItems2 = new ArrayList<>();
    FirebaseDatabase database;
    int month;
    Spinner month_et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_show_closings);
        database = FirebaseDatabase.getInstance();
        RecyclerView recyclerView = findViewById(R.id.closingsRecyclerView);
        RecyclerView recyclerView2 = findViewById(R.id.closingsRecyclerView2);
        month_et = findViewById(R.id.month_et);
        final Calendar cldr = Calendar.getInstance(Locale.US);
        month = cldr.get(Calendar.MONTH);
        ArrayAdapter<String> aa =
                new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, months);
        aa.setDropDownViewResource(R.layout.spinner_dropdown);
        // Setting the ArrayAdapter data on the Spinner
        month_et.setAdapter(aa);
        month_et.setSelection(Math.max(month, 0));
        recyclerView.setHasFixedSize(true);
        //    LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        //    mLayoutManager.setReverseLayout(true);
        //    mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new ClosingAdapter(closingItems, getApplicationContext());
        recyclerView.setAdapter(adapter);
        /** ************************ */
        recyclerView2.setHasFixedSize(true);
        //    LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        //    mLayoutManager.setReverseLayout(true);
        //    mLayoutManager.setStackFromEnd(true);
        recyclerView2.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter2 = new ClosingAdapter(closingItems2, getApplicationContext());
        recyclerView2.setAdapter(adapter2);
    }

    public void refreshClosing(View view) {
        final int month = Integer.parseInt(month_et.getSelectedItem().toString());
        final DatabaseReference ClosingRef = database.getReference("closings").child(userBranch);
        ClosingRef.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(String.valueOf(month))) {
                            ClosingRef.child(String.valueOf(month))
                                    .addValueEventListener(
                                            new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    closingItems.clear();
                                                    closingItems2.clear();
                                                    for (int i = 1; i < 32; i++) {
                                                        if (dataSnapshot.hasChild(String.valueOf(i))) {
                                                            int isClosed =
                                                                    dataSnapshot.child(String.valueOf(i)).getValue(Integer.class);
                                                            if (i % 2 != 0)
                                                                closingItems.add(new ClosingItem(String.valueOf(i), isClosed));
                                                            else
                                                                closingItems2.add(new ClosingItem(String.valueOf(i), isClosed));

                                                        } else {
                                                            if (i % 2 != 0)
                                                                closingItems.add(new ClosingItem(String.valueOf(i), 0));
                                                            else
                                                                closingItems2.add(new ClosingItem(String.valueOf(i), 0));
                                                        }
                                                    }
                                                    adapter.notifyDataSetChanged();
                                                    adapter2.notifyDataSetChanged();
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    // Failed to read value
                                                    Log.w(TAG, "Failed to read value.", error.toException());
                                                }
                                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Failed to read value
                        Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });
    }
}
