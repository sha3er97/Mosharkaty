package com.resala.mosharkaty;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.resala.mosharkaty.utility.classes.MessageItem;

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
        if (TextUtils.isEmpty(message_et.getText().toString().trim())) {
            message_et.setError("Required.");
            return;
        } else {
            message_et.setError(null);
        }
        DatabaseReference MessagesRef = database.getReference("messages").child(userBranch).push();
        String authour = anonymous.isChecked() ? "anonymous" : userName;

        //        String key = System.currentTimeMillis() / (1000 * 60) + "&" + authour;
        final Calendar cldr = Calendar.getInstance(Locale.US);

        MessagesRef.setValue(
                new MessageItem(
                        authour, message_et.getText().toString().trim(), cldr.getTime().toString()));

        Toast.makeText(this, "Message Sent..", Toast.LENGTH_SHORT).show();
        //    finish();
    }
}
