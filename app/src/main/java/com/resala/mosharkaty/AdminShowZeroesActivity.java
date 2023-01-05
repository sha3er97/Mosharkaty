package com.resala.mosharkaty;

import static android.content.ContentValues.TAG;
import static com.resala.mosharkaty.fragments.HomeFragment.globalFari2Names;
import static com.resala.mosharkaty.utility.classes.UtilityClass.userBranch;

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
import com.resala.mosharkaty.ui.adapters.UsersAdapter;
import com.resala.mosharkaty.utility.classes.MosharkaItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AdminShowZeroesActivity extends AppCompatActivity {
    FirebaseDatabase database;
    UsersAdapter adapter;
    ArrayList<String> notattended = new ArrayList<>();
    int month;
    DatabaseReference MosharkatRef;
    TextView zeroTV;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_show_zeroes);
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

        notattended.addAll(globalFari2Names);
        checkCompleted();
    }

    private void checkCompleted() {
        MosharkatRef = database.getReference("mosharkat").child(userBranch);
        MosharkatRef.child(String.valueOf(month))
                .addListenerForSingleValueEvent(
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
}
