package com.resala.mosharkaty;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class AdminCoursesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_courses);
    }

    public void AddSession(View view) {
        startActivity(new Intent(this, AdminViewCoursesActivity.class));
        finish();
    }

    public void AddCourse(View view) {
        startActivity(new Intent(this, AdminAddCourseActivity.class));
        finish();
    }
}
