package com.resala.mosharkaty;

import static com.resala.mosharkaty.utility.classes.UtilityClass.userId;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.resala.mosharkaty.ui.adapters.SessionsAdapter;
import com.resala.mosharkaty.utility.classes.Session;

import java.util.ArrayList;

public class ViewSessionsActivity extends AppCompatActivity {
    SessionsAdapter adapter;
    ArrayList<Session> sessionItems = new ArrayList<>();
    ArrayList<String> finishedSessions = new ArrayList<>();
    FirebaseDatabase database;
    DatabaseReference SessionsRef;
    DatabaseReference progressRef;
    ValueEventListener progressListener;
    ValueEventListener SessionsListener;

    String titleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_sessions);

        Intent intent = getIntent();
        titleText = intent.getStringExtra("title");

        database = FirebaseDatabase.getInstance();
        SessionsRef = database.getReference("sessions").child(titleText);
        progressRef = database.getReference("progress").child(titleText);

        RecyclerView recyclerView = findViewById(R.id.sessionsRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new SessionsAdapter(sessionItems, getApplicationContext());
        recyclerView.setAdapter(adapter);

        progressListener =
                progressRef.addValueEventListener(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                finishedSessions.clear();
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    if (snapshot.hasChild(userId)) {
                                        finishedSessions.add(snapshot.getKey());

                                    }
                                }
                                SessionsListener =
                                        SessionsRef.addValueEventListener(
                                                new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        sessionItems.clear();
                                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                            Session session = snapshot.getValue(Session.class);
                                                            if (session != null) {
                                                                session.setFinished(
                                                                        finishedSessions.contains(snapshot.getKey()));

                                                                session.setParentCourse(titleText);
                                                                session.setSession_num(snapshot.getKey());
                                                                sessionItems.add(session);
                                                            }
                                                        }
                                                        adapter.notifyDataSetChanged();
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                    }
                                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (SessionsRef != null && SessionsListener != null) {
            SessionsRef.removeEventListener(SessionsListener);
        }
        if (progressRef != null && progressListener != null) {
            progressRef.removeEventListener(progressListener);
        }
    }

    public void back(View view) {
        finish();
    }
}
