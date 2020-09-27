package com.resala.mosharkaty;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
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
import com.resala.mosharkaty.ui.adapters.SessionsAdapter;
import com.resala.mosharkaty.utility.classes.Session;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdminAddSessionActivity extends AppCompatActivity {
  EditText sessionName_et;
  EditText link_et;
  EditText session_description;

  DatabaseReference SessionsRef;
  DatabaseReference CoursesRef;

  FirebaseDatabase database;
  String titleText;
  DatabaseReference EnrollmentRef;
  DatabaseReference progressRef;
  SessionsAdapter adapter;
  ArrayList<Session> sessionItems = new ArrayList<>();
  ValueEventListener SessionsListener;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_admin_add_session);
    Intent intent = getIntent();
    titleText = intent.getStringExtra("title");

    sessionName_et = findViewById(R.id.sessionName_et);
    link_et = findViewById(R.id.link_et);
    session_description = findViewById(R.id.session_description);

    database = FirebaseDatabase.getInstance();
    assert titleText != null;
    SessionsRef = database.getReference("sessions").child(titleText);
    EnrollmentRef = database.getReference("enrollment").child(titleText);
    progressRef = database.getReference("progress").child(titleText);
    CoursesRef = database.getReference("courses").child(titleText);

    RecyclerView recyclerView = findViewById(R.id.sessionsRecyclerView);
    recyclerView.setHasFixedSize(true);
    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    adapter = new SessionsAdapter(sessionItems, getApplicationContext());
    recyclerView.setAdapter(adapter);

    SessionsListener =
            SessionsRef.addValueEventListener(
                    new ValueEventListener() {
                      @Override
                      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        sessionItems.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                          Session session = snapshot.getValue(Session.class);
                          if (session != null) {
                            session.setFinished(false);
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

  private String extractVideoId(String ytUrl) {
    String vId = null;
    Pattern pattern =
            Pattern.compile(
                    "^https?://.*(?:youtu.be/|v/|u/\\w/|embed/|watch?v=)([^#&?]*).*$",
                    Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(ytUrl);
    if (matcher.matches()) {
      vId = matcher.group(1);
    }
    return vId;
  }

  public void addSession(View view) {
    if (!validateForm()) return;
    String sessionName = sessionName_et.getText().toString().trim();
    SessionsRef.addListenerForSingleValueEvent(
            new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(sessionName)) {
                  Toast.makeText(getApplicationContext(), "اسم السيشن مكرر", Toast.LENGTH_SHORT).show();
                } else {
                  DatabaseReference currentSession = SessionsRef.child(sessionName);
                  currentSession.setValue(
                          new Session(
                                  link_et.getText().toString().trim(),
                                  session_description.getText().toString().trim()));

                  Toast.makeText(getApplicationContext(), "Session Added..", Toast.LENGTH_SHORT).show();

                  // عشان ماجد
                  sessionName_et.setText("");
                  link_et.setText("");
                  session_description.setText("");
                }
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
  }

  private boolean validateForm() {
    boolean valid = true;
    String name = sessionName_et.getText().toString();
    if (TextUtils.isEmpty(name)) {
      sessionName_et.setError("Required.");
      valid = false;
    }
    String link = link_et.getText().toString();
    if (TextUtils.isEmpty(link)) {
      link_et.setError("Required.");
      valid = false;
    }
    if (extractVideoId(link) == null) {
      link_et.setError("invalid link.");
      valid = false;
    }
    String description = session_description.getText().toString();
    if (TextUtils.isEmpty(description)) {
      session_description.setError("Required.");
      valid = false;
    }
    if (valid) {
      sessionName_et.setError(null);
      link_et.setError(null);
      session_description.setError(null);
    }
    return valid;
  }
}
