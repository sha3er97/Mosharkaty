package com.resala.mosharkaty;

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

import static com.resala.mosharkaty.LoginActivity.userBranch;
import static com.resala.mosharkaty.ProfileFragment.userName;

public class EventDescriptionActivity extends AppCompatActivity {
    Button coming_btn;
    Button maybe_btn;
    Button not_coming_btn;
    FirebaseDatabase database;
    DatabaseReference myConfirmation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_description);
        database = FirebaseDatabase.getInstance();

        Intent intent = getIntent();
        String titleText = intent.getStringExtra("title");
        String dateText = intent.getStringExtra("date");
        String url = intent.getStringExtra("image");
        String descriptionText = intent.getStringExtra("description");
        String locationText = intent.getStringExtra("location");
        String timeText = intent.getStringExtra("time");

        TextView title = findViewById(R.id.title);
        TextView date = findViewById(R.id.dateTV);
        TextView loc = findViewById(R.id.location);
        TextView time = findViewById(R.id.timeTV);
        ImageView image = findViewById(R.id.eventImage);
        TextView description = findViewById(R.id.eventDetails);

        coming_btn = findViewById(R.id.coming);
        maybe_btn = findViewById(R.id.maybe);
        not_coming_btn = findViewById(R.id.not_coming);

        title.setText(titleText);
        date.setText(dateText);
        loc.setText(locationText);
        time.setText(timeText);
        Picasso.get().load(url).into(image);
        description.setText(descriptionText);

        assert titleText != null;
        myConfirmation = database.getReference("confirmations").child(userBranch).child(titleText).child(userName);
        myConfirmation.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild("confirm")) {
                            String old_confirm = snapshot.child("confirm").getValue(String.class);
                            assert old_confirm != null;
                            switch (old_confirm) {
                                case "جاي":
                                    comingAction();
                                    break;
                                case "احتمال":
                                    maybeAction();
                                    break;
                                case "معتذر":
                                    notComingAction();
                                    break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    public void eventComingClick(View view) {
        myConfirmation.child("confirm").setValue("جاي");
        comingAction();
        Toast.makeText(this, "شكرا لتاكيدك .. نورتنا", Toast.LENGTH_SHORT).show();
    }

    public void eventMaybeClick(View view) {
        myConfirmation.child("confirm").setValue("احتمال");
        maybeAction();
        Toast.makeText(this, "شكرا ليك .. حاول تاكد في اقرب وقت ان شاء الله", Toast.LENGTH_SHORT)
                .show();
    }

    public void eventNotComingClick(View view) {
        myConfirmation.child("confirm").setValue("معتذر");
        notComingAction();
        Toast.makeText(this, "ناسف لاعتذارك .. ربنا يقويك", Toast.LENGTH_SHORT).show();
    }

    private void comingAction() {
        coming_btn.setEnabled(false);
        maybe_btn.setEnabled(true);
        not_coming_btn.setEnabled(true);
        coming_btn.setBackgroundResource(R.drawable.btn_gradient_disabled);
        maybe_btn.setBackgroundResource(R.drawable.btn_gradient_yellow);
        not_coming_btn.setBackgroundResource(R.drawable.btn_gradient_red);
    }

    private void maybeAction() {
        coming_btn.setEnabled(true);
        maybe_btn.setEnabled(false);
        not_coming_btn.setEnabled(true);
        coming_btn.setBackgroundResource(R.drawable.btn_gradient_blue);
        maybe_btn.setBackgroundResource(R.drawable.btn_gradient_disabled);
        not_coming_btn.setBackgroundResource(R.drawable.btn_gradient_red);
    }

    private void notComingAction() {
        coming_btn.setEnabled(true);
        maybe_btn.setEnabled(true);
        not_coming_btn.setEnabled(false);
        coming_btn.setBackgroundResource(R.drawable.btn_gradient_blue);
        maybe_btn.setBackgroundResource(R.drawable.btn_gradient_yellow);
        not_coming_btn.setBackgroundResource(R.drawable.btn_gradient_disabled);
    }
}
