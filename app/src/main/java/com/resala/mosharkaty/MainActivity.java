package com.resala.mosharkaty;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.resala.mosharkaty.ui.main.SectionsPagerAdapter;

import static com.resala.mosharkaty.LoginActivity.isAdmin;
import static com.resala.mosharkaty.LoginActivity.userId;
import static com.resala.mosharkaty.ProfileFragment.userBranch;
import static com.resala.mosharkaty.ProfileFragment.userCode;
import static com.resala.mosharkaty.ProfileFragment.userName;

public class MainActivity extends AppCompatActivity {
  private FirebaseAuth mAuth;
  Button logOut_btn;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mAuth = FirebaseAuth.getInstance();

    SectionsPagerAdapter sectionsPagerAdapter =
            new SectionsPagerAdapter(this, getSupportFragmentManager());
    ViewPager viewPager = findViewById(R.id.view_pager);
    viewPager.setAdapter(sectionsPagerAdapter);
    TabLayout tabs = findViewById(R.id.tabs);
    logOut_btn = findViewById(R.id.logOutBtn);
    if (isAdmin) logOut_btn.setText("Exit");
    tabs.setupWithViewPager(viewPager);
    FloatingActionButton fab = findViewById(R.id.fab);

    fab.setOnClickListener(
            new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                //            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //                .setAction("Action", null)
                //                .show();
                if (isAdmin) startActivity(new Intent(getApplicationContext(), MessagesRead.class));
                else startActivity(new Intent(getApplicationContext(), MessagesWrite.class));
              }
            });
  }

  @Override
  public void onBackPressed() {
    // do nothing
    Toast.makeText(this, "can't go back .. you can log out only", Toast.LENGTH_SHORT).show();
  }

  public void logOut(View view) {
    if (!isAdmin) {
      mAuth.signOut();
      userName = getString(R.string.dummy_volunteer);
      userCode = getString(R.string.dummy_code);
      userBranch = getString(R.string.dummy_far3);
      userId = "-1";
    }
    finish();
  }
}
