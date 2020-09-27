package com.resala.mosharkaty;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.resala.mosharkaty.utility.classes.Session;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdminEditSessionActivity extends AppCompatActivity {
    DatabaseReference SessionsRef;
    DatabaseReference CoursesRef;

    FirebaseDatabase database;
    String titleText;
    String session_num;
    String description;
    String link;

    DatabaseReference EnrollmentRef;
    DatabaseReference progressRef;

    TextView sessionName_et;
    EditText link_et;
    EditText session_description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_session);
        Intent intent = getIntent();
        titleText = intent.getStringExtra("course_name");
        session_num = intent.getStringExtra("session_num");
        description = intent.getStringExtra("description");
        link = intent.getStringExtra("link");

        database = FirebaseDatabase.getInstance();
        assert titleText != null;
        SessionsRef = database.getReference("sessions").child(titleText);
        EnrollmentRef = database.getReference("enrollment").child(titleText);
        progressRef = database.getReference("progress").child(titleText);
        CoursesRef = database.getReference("courses").child(titleText);

        sessionName_et = findViewById(R.id.sessionName_et);
        link_et = findViewById(R.id.link_et);
        session_description = findViewById(R.id.session_description);

        sessionName_et.setText(session_num);
        session_description.setText(description);
        link_et.setText(link);
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

    private boolean validateForm() {
        boolean valid = true;
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

    public void deleteCourse(View view) {
        CoursesRef.setValue(null);
        SessionsRef.setValue(null);
        progressRef.setValue(null);
        EnrollmentRef.setValue(null);

        Toast.makeText(getApplicationContext(), "Course deleted..", Toast.LENGTH_SHORT).show();
        finish();
    }

    public void editSession(View view) {
        if (!validateForm()) return;
        DatabaseReference currentSession = SessionsRef.child(session_num);

        currentSession.setValue(
                new Session(
                        link_et.getText().toString().trim(), session_description.getText().toString().trim()));

        Toast.makeText(getApplicationContext(), "Session Edited..", Toast.LENGTH_SHORT).show();
    }

    public void deleteSession(View view) {
        SessionsRef.child(session_num).setValue(null);
        progressRef.child(session_num).setValue(null);

        Toast.makeText(getApplicationContext(), "Session deleted..", Toast.LENGTH_SHORT).show();
        finish();
    }
}
