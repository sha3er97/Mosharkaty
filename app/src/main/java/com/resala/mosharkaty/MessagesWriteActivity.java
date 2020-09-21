package com.resala.mosharkaty;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Locale;

import static com.resala.mosharkaty.LoginActivity.userBranch;
import static com.resala.mosharkaty.fragments.ProfileFragment.userName;

public class MessagesWriteActivity extends AppCompatActivity {
    EditText message_et;
    Button addMessage_btn;
    FirebaseDatabase database;
    CheckBox anonymous;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_write);
        database = FirebaseDatabase.getInstance();
        anonymous = findViewById(R.id.anonymousCheckBox);
        message_et = findViewById(R.id.messageText);
        addMessage_btn = findViewById(R.id.confirmMessage);
    }

    public void sendMessage(View view) {
        DatabaseReference MessagesRef = database.getReference("messages").child(userBranch);
        String authour = anonymous.isChecked() ? "anonymous" : userName;

        String key = System.currentTimeMillis() / (1000 * 60) + "&" + authour;
        DatabaseReference currentMessage = MessagesRef.child(key);
        DatabaseReference dateRef = currentMessage.child("date");
        DatabaseReference contentRef = currentMessage.child("content");
        DatabaseReference nameRef = currentMessage.child("author");

        nameRef.setValue(authour);
        final Calendar cldr = Calendar.getInstance(Locale.US);
        dateRef.setValue(cldr.getTime().toString());
        contentRef.setValue(message_et.getText().toString());
        Toast.makeText(this, "Message Sent..", Toast.LENGTH_SHORT).show();
        finish();
    }
}
