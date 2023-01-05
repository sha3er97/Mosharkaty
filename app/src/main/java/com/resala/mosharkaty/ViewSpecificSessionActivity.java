package com.resala.mosharkaty;

import static android.content.ContentValues.TAG;
import static com.resala.mosharkaty.fragments.HomeFragment.userOfficialName;
import static com.resala.mosharkaty.utility.classes.UtilityClass.userBranch;
import static com.resala.mosharkaty.utility.classes.UtilityClass.userId;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.resala.mosharkaty.utility.classes.MosharkaItem;
import com.resala.mosharkaty.utility.classes.PlayerConfig;

import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ViewSpecificSessionActivity extends YouTubeBaseActivity {

    YouTubePlayerView youTubePlayerView;
    YouTubePlayer.OnInitializedListener onInitializedListener;
    Button mosharka_btn;
    Button markFinished_btn;
    FirebaseDatabase database;
    int day;
    int month;
    int year;
    DatabaseReference appMosharkatRef;
    DatabaseReference progressRef;
    TextView descriptionTV;
    TextView linkTV;
    TextView session_num;
    TextView course_name;
    String course_nameText;
    String session_numText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_specific_session);
        youTubePlayerView = findViewById(R.id.youTubePlayerView);
        mosharka_btn = findViewById(R.id.mosharka_btn);
        markFinished_btn = findViewById(R.id.markFinished_btn);

        descriptionTV = findViewById(R.id.descriptionTV);
        linkTV = findViewById(R.id.linkTV);
        course_name = findViewById(R.id.session_title);
        session_num = findViewById(R.id.session_num);

        database = FirebaseDatabase.getInstance();

        Intent intent = getIntent();
        String linkText = intent.getStringExtra("link");
        String descriptionText = intent.getStringExtra("description");
        session_numText = intent.getStringExtra("session_num");
        course_nameText = intent.getStringExtra("course_name");

        linkTV.setText(linkText);
        descriptionTV.setText(descriptionText);
        session_num.setText(session_numText);
        course_name.setText(course_nameText);

        final Calendar cldr = Calendar.getInstance(Locale.US);
        day = cldr.get(Calendar.DAY_OF_MONTH);
        month = cldr.get(Calendar.MONTH) + 1;
        year = cldr.get(Calendar.YEAR);

        onInitializedListener =
                new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(
                            YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                        String id = extractVideoId(linkText);
                        if (id != null) youTubePlayer.loadVideo(id);
                        //                        youTubePlayer.loadVideo("IuPxQP0lg78");
                    }

                    @Override
                    public void onInitializationFailure(
                            YouTubePlayer.Provider provider,
                            YouTubeInitializationResult youTubeInitializationResult) {
                    }
                };
        youTubePlayerView.initialize(PlayerConfig.API_KEY, onInitializedListener);
        mosharka_btn.setEnabled(false);
        mosharka_btn.setBackgroundColor(
                getResources().getColor(R.color.common_google_signin_btn_text_light_disabled));
    }

    public void back(View view) {
        finish();
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

    public void finishVideo(View view) {
        progressRef =
                database
                        .getReference("progress")
                        .child(course_nameText)
                        .child(session_numText)
                        .child(userId);
        progressRef.setValue(true);

        mosharka_btn.setEnabled(true);
        mosharka_btn.setBackgroundResource(R.drawable.btn_gradient_blue);
        markFinished_btn.setEnabled(false);
        markFinished_btn.setBackgroundColor(
                getResources().getColor(R.color.common_google_signin_btn_text_light_disabled));
        Toast.makeText(getApplicationContext(), "عاش يا بطل", Toast.LENGTH_SHORT).show();
    }

    public void AddCourseMosharka(View view) {
        final DatabaseReference ClosingRef = database.getReference("closings").child(userBranch);
        appMosharkatRef = database.getReference("mosharkat").child(userBranch);
        String date = day + "/" + month + "/" + year;
        appMosharkatRef
                .child(String.valueOf(month))
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                boolean duplicate = false;
                                mosharka_btn.setEnabled(false);
                                mosharka_btn.setBackgroundColor(
                                        getResources().getColor(R.color.common_google_signin_btn_text_light_disabled));
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    MosharkaItem mosharka = snapshot.getValue(MosharkaItem.class);
                                    if (mosharka != null) {
                                        if (userOfficialName.equals(mosharka.getVolname().trim())
                                                && date.equals(mosharka.getMosharkaDate())) {
                                            duplicate = true;
                                            break;
                                        }
                                    }
                                }
                                if (!duplicate) {
                                    String key =
                                            System.currentTimeMillis() / (1000 * 60) + "&" + day + "&" + userOfficialName;
                                    DatabaseReference currentMosharka =
                                            appMosharkatRef.child(String.valueOf(month)).child(key);
                                    DatabaseReference dateRef = currentMosharka.child("mosharkaDate");
                                    DatabaseReference typeRef = currentMosharka.child("mosharkaType");
                                    DatabaseReference nameRef = currentMosharka.child("volname");

                                    nameRef.setValue(userOfficialName);
                                    dateRef.setValue(date);
                                    typeRef.setValue("سيشن / اورينتيشن");

                                    ClosingRef.child(String.valueOf(month)).child(String.valueOf(day)).setValue(0);
                                    Toast.makeText(
                                                    getApplicationContext(), "تم اضافة مشاركة جديدة..", Toast.LENGTH_SHORT)
                                            .show();
                                    mosharka_btn.setEnabled(false);
                                    mosharka_btn.setBackgroundColor(
                                            getResources()
                                                    .getColor(R.color.common_google_signin_btn_text_light_disabled));
                                    mosharka_btn.setText("تم تسجيل مشاركة لليوم");
                                } else {
                                    Toast.makeText(
                                                    getApplicationContext(),
                                                    "عذرا .. المشاركة مكررة في اليوم دا",
                                                    Toast.LENGTH_SHORT)
                                            .show();
                                    mosharka_btn.setEnabled(true);
                                    mosharka_btn.setBackgroundResource(R.drawable.btn_gradient_blue);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Failed to read value
                                Log.w(TAG, "Failed to read value.", error.toException());
                            }
                        });
    }
}
