package com.resala.mosharkaty;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class EventDescription extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_event_description);
    Intent intent = getIntent();
    String titleText = intent.getStringExtra("title");
    String dateText = intent.getStringExtra("date");
    String url = intent.getStringExtra("image");
    String descriptionText = intent.getStringExtra("description");

    TextView title = findViewById(R.id.title);
    TextView date = findViewById(R.id.date);
    ImageView image = findViewById(R.id.eventImage);
    TextView description = findViewById(R.id.eventDetails);

    title.setText(titleText);
    date.setText(dateText);
    Picasso.get().load(url).into(image);
    description.setText(descriptionText);
  }
}
