package com.example.mosharkaty;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
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

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class MessagesRead extends AppCompatActivity {
  MessagesAdapter adapter;
  ArrayList<MessageItem> messageItems = new ArrayList<>();
  FirebaseDatabase database;
  ImageButton refresh_btn;
  TextView messagesTV;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_messages_read);
    database = FirebaseDatabase.getInstance();
    refresh_btn = findViewById(R.id.messages_refresh_btn);
    messagesTV = findViewById(R.id.messagesCount);
    RecyclerView recyclerView = findViewById(R.id.messagesRecyclerView);

    recyclerView.setHasFixedSize(true);
    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    adapter = new MessagesAdapter(messageItems, getApplicationContext());
    recyclerView.setAdapter(adapter);
  }

  public void refreshMessages(View view) {
    DatabaseReference MessagesRef = database.getReference("messages");
    DatabaseReference MessagesCountRef = database.getReference("messagesCount");

    MessagesRef.addValueEventListener(
        new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {
            messageItems.clear();
            int counter = 0;
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
              MessageItem message = snapshot.getValue(MessageItem.class);
              messageItems.add(message);
              Toast.makeText(getApplicationContext(), "messages updated", Toast.LENGTH_SHORT)
                  .show();
              counter++;
            }
            adapter.notifyDataSetChanged();
            messagesTV.setText(String.valueOf(counter));
          }

          @Override
          public void onCancelled(@NonNull DatabaseError error) {
            // Failed to read value
            Log.w(TAG, "Failed to read value.", error.toException());
          }
        });

    //    MessagesCountRef.addValueEventListener(
    //        new ValueEventListener() {
    //          @Override
    //          public void onDataChange(DataSnapshot dataSnapshot) {
    //            messagesTV.setText(String.valueOf(dataSnapshot.getValue(Integer.class)));
    //          }
    //
    //          @Override
    //          public void onCancelled(@NonNull DatabaseError error) {
    //            // Failed to read value
    //            Log.w(TAG, "Failed to read value.", error.toException());
    //          }
    //        });
  }
}
