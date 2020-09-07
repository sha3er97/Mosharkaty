package com.resala.mosharkaty;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

import static android.content.ContentValues.TAG;
import static com.resala.mosharkaty.LoginActivity.userBranch;

public class AdminShowZeroes extends AppCompatActivity {
    FirebaseDatabase database;
    UsersAdapter adapter;
    ArrayList<String> notattended = new ArrayList<>();
    int month;
    DatabaseReference usersRef;
    DatabaseReference MosharkatRef;
    ValueEventListener mosharkatlistener;
    TextView zeroTV;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_show_zeroes);
        database = FirebaseDatabase.getInstance();
        RecyclerView recyclerView = findViewById(R.id.zeroesRecyclerView);
        TextView current_month = findViewById(R.id.month_et);
        zeroTV = findViewById(R.id.zeroTV);

        final Calendar cldr = Calendar.getInstance(Locale.US);
        month = cldr.get(Calendar.MONTH) + 1;
        current_month.setText(String.valueOf(month));
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new UsersAdapter(notattended, getApplicationContext());
        recyclerView.setAdapter(adapter);

        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("لحظات معانا...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        DatabaseReference liveSheet =
                database.getReference("1tsMZ5EwtKrBUGuLFVBvuwpU5ve0JKMsaqK1nNAONj-0");
        usersRef = liveSheet.child("month_mosharkat");
        usersRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        notattended.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Volunteer user = snapshot.getValue(Volunteer.class);
                            if (user != null && !user.degree.matches("(.*)مجمد(.*)"))
                                notattended.add(user.Volname); // add all team
                        }
                        Collections.sort(notattended); // alphabetical
                        checkCompleted();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Failed to read value
                        Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });
    }

    private void checkCompleted() {
        MosharkatRef = database.getReference("mosharkat").child(userBranch);
        mosharkatlistener =
                MosharkatRef.child(String.valueOf(month))
                        .addValueEventListener(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            MosharkaItem mosharka = snapshot.getValue(MosharkaItem.class);
                                            if (mosharka != null) {
                                                notattended.remove(mosharka.getVolname());
                                            }
                                        }
                                        adapter.notifyDataSetChanged();
                                        zeroTV.setText(String.valueOf(notattended.size()));
                                        // To dismiss the dialog
                                        progress.dismiss();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        // Failed to read value
                                        Log.w(TAG, "Failed to read value.", error.toException());
                                    }
                                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (MosharkatRef != null && mosharkatlistener != null) {
            MosharkatRef.removeEventListener(mosharkatlistener);
        }
    }
}
