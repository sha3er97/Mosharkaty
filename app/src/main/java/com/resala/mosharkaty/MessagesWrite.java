package com.resala.mosharkaty;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import static android.content.ContentValues.TAG;
import static com.resala.mosharkaty.ProfileFragment.userName;

public class MessagesWrite extends AppCompatActivity {
  EditText message_et;
  Button addMessage_btn;
  FirebaseDatabase database;
  private static int messagesCount;
  CheckBox anonymous;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_messages_write);
      database = FirebaseDatabase.getInstance();
      anonymous = findViewById(R.id.anonymousCheckBox);
      message_et = findViewById(R.id.messageText);
      addMessage_btn = findViewById(R.id.confirmMessage);

      DatabaseReference MessagesCountRef = database.getReference("messagesCount");
      MessagesCountRef.addValueEventListener(
              new ValueEventListener() {
                  @Override
                  public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                      messagesCount = dataSnapshot.getValue(Integer.class);
                  }

                  @Override
                  public void onCancelled(@NonNull DatabaseError error) {
                      // Failed to read value
                      Log.w(TAG, "Failed to read value.", error.toException());
                  }
              });
  }

    public void sendMessage(View view) {
        DatabaseReference MessagesRef = database.getReference("messages");
        DatabaseReference MessagesCountRef = database.getReference("messagesCount");

        DatabaseReference currentMessage = MessagesRef.child(String.valueOf(messagesCount));
        DatabaseReference dateRef = currentMessage.child("date");
        DatabaseReference contentRef = currentMessage.child("content");
        DatabaseReference nameRef = currentMessage.child("author");

        String authour = anonymous.isChecked() ? "anonymous" : userName;
        nameRef.setValue(authour);
        final Calendar cldr = Calendar.getInstance();
        dateRef.setValue(cldr.getTime().toString());
    contentRef.setValue(message_et.getText().toString());
    MessagesCountRef.setValue(messagesCount + 1);
    Toast.makeText(this, "Message Sent..", Toast.LENGTH_SHORT).show();
  }
}
