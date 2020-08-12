package com.resala.mosharkaty;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.resala.mosharkaty.ui.main.SectionsPagerAdapter;

import static com.resala.mosharkaty.LoginActivity.isAdmin;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    SectionsPagerAdapter sectionsPagerAdapter =
        new SectionsPagerAdapter(this, getSupportFragmentManager());
    ViewPager viewPager = findViewById(R.id.view_pager);
    viewPager.setAdapter(sectionsPagerAdapter);
    TabLayout tabs = findViewById(R.id.tabs);
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
}
