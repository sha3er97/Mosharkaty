package com.resala.mosharkaty;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.resala.mosharkaty.ui.adapters.SignaturesAdapter;
import com.resala.mosharkaty.utility.classes.Sig;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import static android.content.ContentValues.TAG;
import static com.resala.mosharkaty.LoginActivity.userBranch;

public class AdminShowSignatureActivity extends AppCompatActivity {
    SignaturesAdapter adapter;
    ArrayList<Sig> signatureitems = new ArrayList<>();
    FirebaseDatabase database;
    int month;
    DatabaseReference SignaturesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_show_signature);
        database = FirebaseDatabase.getInstance();
        RecyclerView recyclerView = findViewById(R.id.signaturesRecyclerView);
        TextView current_month = findViewById(R.id.current_month);
        final Calendar cldr = Calendar.getInstance(Locale.US);
        month = cldr.get(Calendar.MONTH);
        current_month.setText(String.valueOf(month));
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new SignaturesAdapter(signatureitems, getApplicationContext());
        recyclerView.setAdapter(adapter);
        SignaturesRef = database.getReference("signatures").child(userBranch);
        SignaturesRef.child(String.valueOf(month))
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                signatureitems.clear();
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    Sig signature = snapshot.getValue(Sig.class);
                                    if (signature != null) {
                                        signatureitems.add(signature);
                                    } else {
                                        Toast.makeText(
                                                getApplicationContext(), "something went wrong", Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Failed to read value
                                Log.w(TAG, "Failed to read value.", error.toException());
                            }
                        });
    }
}
