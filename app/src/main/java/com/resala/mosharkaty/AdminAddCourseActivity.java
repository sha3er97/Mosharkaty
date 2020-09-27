package com.resala.mosharkaty;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.resala.mosharkaty.utility.classes.Course;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class AdminAddCourseActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener {
    HashMap<String, String> coursesImages = new HashMap<>();
    String[] courseTypes = {"Media", "Computer", "ديني", "ادارة", "Soft Skills"};
    public static String[] courseColors = {
            "style 1", "style 2", "style 3", "style 4", "style 5", "style 6", "style 7", "style 8"
    };
    String[] courseLevels = {"Basic", "Medium", "Advanced"};

    TextView title;
    TextView instructor;
    TextView course_level;
    ImageView demoImage;
    FirebaseDatabase database;
    Spinner courseTypeSpinner;
    Spinner courseLevelSpinner;
    Spinner courseColorSpinner;

    EditText courseName_et;
    EditText instructor_name_et;
    EditText course_description;

    DatabaseReference CoursesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_course);
        title = findViewById(R.id.courseName);
        instructor = findViewById(R.id.instructor);
        course_level = findViewById(R.id.course_level);
        demoImage = findViewById(R.id.course_img);

        courseName_et = findViewById(R.id.courseName_et);
        courseTypeSpinner = findViewById(R.id.courseTypeSpinner);
        instructor_name_et = findViewById(R.id.instructor_name_et);
        courseLevelSpinner = findViewById(R.id.courseLevelSpinner);
        courseColorSpinner = findViewById(R.id.courseColorSpinner);
        course_description = findViewById(R.id.course_description);

        fillCoursesImages();
        database = FirebaseDatabase.getInstance();
        CoursesRef = database.getReference("courses");

        final ArrayAdapter<String> aa =
                new ArrayAdapter<>(AdminAddCourseActivity.this, R.layout.spinner_item, courseTypes);
        aa.setDropDownViewResource(R.layout.spinner_dropdown);
        // Setting the ArrayAdapter data on the Spinner
        courseTypeSpinner.setSelection(0, false);
        courseTypeSpinner.setAdapter(aa);

        final ArrayAdapter<String> ab =
                new ArrayAdapter<>(AdminAddCourseActivity.this, R.layout.spinner_item, courseLevels);
        ab.setDropDownViewResource(R.layout.spinner_dropdown);
        // Setting the ArrayAdapter data on the Spinner
        courseLevelSpinner.setSelection(0, false);
        courseLevelSpinner.setOnItemSelectedListener(this);
        courseLevelSpinner.setAdapter(ab);

        final ArrayAdapter<String> ac =
                new ArrayAdapter<>(AdminAddCourseActivity.this, R.layout.spinner_item, courseColors);
        ac.setDropDownViewResource(R.layout.spinner_dropdown);
        // Setting the ArrayAdapter data on the Spinner
        courseColorSpinner.setSelection(0, false);
        courseColorSpinner.setOnItemSelectedListener(this);
        courseColorSpinner.setAdapter(ac);

        instructor_name_et.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void afterTextChanged(Editable s) {
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        instructor.setText(s);
                    }
                });

        courseName_et.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void afterTextChanged(Editable s) {
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        title.setText(s);
                    }
                });
    }

    private void fillCoursesImages() {
        coursesImages.put(courseColors[0], "https://i.imgur.com/KziR4lT.jpeg");
        coursesImages.put(courseColors[1], "https://i.imgur.com/EVGYLud.jpeg");
        coursesImages.put(courseColors[2], "https://i.imgur.com/nopDz7z.jpeg");
        coursesImages.put(courseColors[3], "https://i.imgur.com/ssQjxN1.jpeg");
        coursesImages.put(courseColors[4], "https://i.imgur.com/g3URUe1.jpeg");
        coursesImages.put(courseColors[5], "https://i.imgur.com/T1j6SLM.jpeg");
        coursesImages.put(courseColors[6], "https://i.imgur.com/gYpp03z.jpeg");
        coursesImages.put(courseColors[7], "https://i.imgur.com/lpBuQrV.jpeg");
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String url = coursesImages.get(courseColorSpinner.getSelectedItem().toString());
        Picasso.get().load(url).into(demoImage);
        course_level.setText(courseLevelSpinner.getSelectedItem().toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    public void addCourse(View view) {
        if (!validateForm()) return;
        String courseName = courseName_et.getText().toString().trim();
        CoursesRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild(courseName)) {
                            Toast.makeText(getApplicationContext(), "اسم الكورس مكرر", Toast.LENGTH_SHORT).show();
                        } else {
                            DatabaseReference currentCourse = CoursesRef.child(courseName);
                            currentCourse.setValue(
                                    new Course(
                                            course_description.getText().toString().trim(),
                                            instructor_name_et.getText().toString().trim(),
                                            courseLevelSpinner.getSelectedItem().toString(),
                                            courseName_et.getText().toString().trim(),
                                            courseColorSpinner.getSelectedItem().toString(),
                                            courseTypeSpinner.getSelectedItem().toString()));

                            Toast.makeText(getApplicationContext(), "Course Added..", Toast.LENGTH_SHORT).show();

                            // عشان ماجد
                            courseName_et.setText("");
                            course_description.setText("");
                            instructor_name_et.setText("");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private boolean validateForm() {
        boolean valid = true;
        String name = courseName_et.getText().toString();
        if (TextUtils.isEmpty(name)) {
            courseName_et.setError("Required.");
            valid = false;
        }
        String inst = instructor_name_et.getText().toString();
        if (TextUtils.isEmpty(inst)) {
            instructor_name_et.setError("Required.");
            valid = false;
        }
        String description = course_description.getText().toString();
        if (TextUtils.isEmpty(description)) {
            course_description.setError("Required.");
            valid = false;
        }
        if (valid) {
            courseName_et.setError(null);
            instructor_name_et.setError(null);
            course_description.setError(null);
        }
        return valid;
    }
}
