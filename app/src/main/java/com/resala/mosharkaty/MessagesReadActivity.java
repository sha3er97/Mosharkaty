package com.resala.mosharkaty;

import static android.content.ContentValues.TAG;
import static com.resala.mosharkaty.utility.classes.UtilityClass.isAdmin;
import static com.resala.mosharkaty.utility.classes.UtilityClass.isManager;
import static com.resala.mosharkaty.utility.classes.UtilityClass.myRules;
import static com.resala.mosharkaty.utility.classes.UtilityClass.userBranch;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.resala.mosharkaty.ui.adapters.MessagesAdapter;
import com.resala.mosharkaty.utility.classes.MessageItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MessagesReadActivity extends AppCompatActivity {
    MessagesAdapter adapter;
    ArrayList<MessageItem> messageItems = new ArrayList<>();
    FirebaseDatabase database;
    ImageButton refresh_btn;
    TextView messagesTV;
    EditText pass_et;
    Button confirm_pass;
    EditText message_et;
    Button addMessage_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_read);
        database = FirebaseDatabase.getInstance();
        refresh_btn = findViewById(R.id.messages_refresh_btn);
        messagesTV = findViewById(R.id.messagesCount);
        RecyclerView recyclerView = findViewById(R.id.messagesRecyclerView);
        pass_et = findViewById(R.id.messagesPass);
        confirm_pass = findViewById(R.id.confirm_pass);
        if (isManager) {
            confirm_pass.setEnabled(false);
            confirm_pass.setBackgroundColor(
                    getResources().getColor(R.color.common_google_signin_btn_text_light_disabled));
            confirm_pass.setTextColor(getResources().getColor(R.color.new_text_black));
            confirm_pass.setText(R.string.welcome_text);
        }
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        adapter = new MessagesAdapter(messageItems, getApplicationContext());
        recyclerView.setAdapter(adapter);
    }

    public void refreshMessages(View view) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            if (connectivityManager.getActiveNetworkInfo() == null
                    || !connectivityManager.getActiveNetworkInfo().isConnected()) {
                Snackbar.make(view, "No Internet", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                return;
            }
        }
        if (!isManager) {
            Toast.makeText(
                            getApplicationContext(),
                            "illegal action : متقدرش تشوف المسدجات الا لما تدحل كلمة السر صح",
                            Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        DatabaseReference MessagesRef = database.getReference("messages").child(userBranch);

        MessagesRef.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        messageItems.clear();
                        int counter = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            MessageItem message = snapshot.getValue(MessageItem.class);
                            if (message != null) {
                                message.setKey(snapshot.getKey());

                                messageItems.add(message);
                                counter++;
                            }
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
    }

    public void checkPassword(View view) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            if (connectivityManager.getActiveNetworkInfo() == null
                    || !connectivityManager.getActiveNetworkInfo().isConnected()) {
                Snackbar.make(view, "No Internet", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                return;
            }
        }

        if (!myRules.manager_password.equalsIgnoreCase(pass_et.getText().toString().trim())) {
            Toast.makeText(getApplicationContext(), "Wrong Password", Toast.LENGTH_SHORT).show();
        } else {
            isManager = true;
            isAdmin = true;
            Toast.makeText(getApplicationContext(), "اهلا استاذ مدير", Toast.LENGTH_SHORT).show();
            confirm_pass.setEnabled(false);
            confirm_pass.setBackgroundColor(
                    getResources().getColor(R.color.common_google_signin_btn_text_light_disabled));
            confirm_pass.setTextColor(getResources().getColor(R.color.new_text_black));
            confirm_pass.setText(R.string.welcome_text);
        }
    }

    public void sendMessage(View view) {
        message_et = findViewById(R.id.messageText);
        addMessage_btn = findViewById(R.id.confirmMessage);
        if (TextUtils.isEmpty(message_et.getText().toString().trim())) {
            message_et.setError("Required.");
            return;
        } else {
            message_et.setError(null);
        }

        DatabaseReference MessagesRef = database.getReference("messages").child(userBranch).push();
        final Calendar cldr = Calendar.getInstance(Locale.US);
        MessagesRef.setValue(
                new MessageItem(
                        "admin", message_et.getText().toString().trim(), cldr.getTime().toString()));

        Toast.makeText(this, "Message Sent..", Toast.LENGTH_SHORT).show();
        message_et.setText("");//avoid repeat
    }
}
