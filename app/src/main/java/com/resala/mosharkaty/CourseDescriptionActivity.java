package com.resala.mosharkaty;

import static com.resala.mosharkaty.LoginActivity.userId;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class CourseDescriptionActivity extends AppCompatActivity {
    FirebaseDatabase database;
    Button enroll_btn;
    DatabaseReference EnrollmentRef;
    boolean isEnrolled;
    ValueEventListener EnrollmentListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_description);
        database = FirebaseDatabase.getInstance();

        Intent intent = getIntent();
        String titleText = intent.getStringExtra("title");
        String instructorText = intent.getStringExtra("instructor");
        String levelText = intent.getStringExtra("level");
        String descriptionText = intent.getStringExtra("description");
        String url = intent.getStringExtra("image");

        TextView title = findViewById(R.id.courseName);
        TextView instructor = findViewById(R.id.instructor);
        TextView course_level = findViewById(R.id.course_level);
        TextView courseDetails = findViewById(R.id.courseDetails);
        ImageView image = findViewById(R.id.course_img);

        title.setText(titleText);
        instructor.setText(instructorText);
        course_level.setText(levelText);
        courseDetails.setText(descriptionText);
        Picasso.get().load(url).into(image);

        enroll_btn = findViewById(R.id.enroll);

        assert titleText != null;
        EnrollmentRef = database.getReference("enrollment").child(titleText);
        EnrollmentListener =
                EnrollmentRef.addValueEventListener(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                isEnrolled = false;
                                if (snapshot.hasChild(userId)) {
                                    isEnrolled = snapshot.child(userId).getValue(Boolean.class);
                                }
                                updateUI(isEnrolled);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
    }

    private void updateUI(boolean isEnrolled) {
        if (isEnrolled) {
            enroll_btn.setBackgroundResource(R.drawable.border_only_btn);
            enroll_btn.setText("الغاء الاشتراك");
        } else {
            enroll_btn.setBackgroundResource(R.drawable.btn_gradient_blue);
            enroll_btn.setText("اشتراك");
        }
    }

    public void enrollCourse(View view) {
        EnrollmentRef.child(userId).setValue(!isEnrolled);
        Toast.makeText(
                getApplicationContext(),
                "تم تحديث اشتراكك .. روح بروفايلك عشان تقدر تتفرج علي كل كورساتك",
                Toast.LENGTH_LONG)
                .show();
    }

    public void back(View view) {
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EnrollmentRef != null && EnrollmentListener != null) {
            EnrollmentRef.removeEventListener(EnrollmentListener);
        }
    }
}
