package com.resala.mosharkaty;

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
import com.resala.mosharkaty.ui.adapters.ExploreCoursesAdapter;
import com.resala.mosharkaty.utility.classes.Course;

import java.util.ArrayList;

public class AdminViewCoursesActivity extends AppCompatActivity {
    ExploreCoursesAdapter adapter;
    ArrayList<Course> courseItems = new ArrayList<>();
    FirebaseDatabase database;
    ValueEventListener CoursesListener;
    DatabaseReference CoursesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_courses);
        database = FirebaseDatabase.getInstance();
        CoursesRef = database.getReference("courses");
        RecyclerView recyclerView = findViewById(R.id.coursesRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new ExploreCoursesAdapter(courseItems, getApplicationContext());
        recyclerView.setAdapter(adapter);
        CoursesListener =
                CoursesRef.addValueEventListener(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                courseItems.clear();
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    Course course = snapshot.getValue(Course.class);
                                    if (course != null) {
                                        courseItems.add(course);
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
    public void onDestroy() {
        super.onDestroy();
        if (CoursesRef != null && CoursesListener != null) {
            CoursesRef.removeEventListener(CoursesListener);
        }
    }

    public void back(View view) {
        finish();
    }
}
