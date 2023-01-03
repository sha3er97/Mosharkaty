package com.resala.mosharkaty;

import static com.resala.mosharkaty.LoginActivity.isAdmin;
import static com.resala.mosharkaty.LoginActivity.isMrkzy;
import static com.resala.mosharkaty.LoginActivity.userBranch;
import static com.resala.mosharkaty.LoginActivity.userId;
import static com.resala.mosharkaty.MessagesReadActivity.isManager;
import static com.resala.mosharkaty.fragments.HomeFragment.userCode;
import static com.resala.mosharkaty.fragments.HomeFragment.userName;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.resala.mosharkaty.ui.adapters.SectionsPagerAdapter;

public class MainActivity extends AppCompatActivity {
  private FirebaseAuth mAuth;
  Button logOut_btn;
  public TabLayout.Tab tab0;
  public TabLayout.Tab tab1;
  public TabLayout.Tab tab2;
  public TabLayout.Tab tab3;
  public TabLayout.Tab tab4;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mAuth = FirebaseAuth.getInstance();

      SectionsPagerAdapter sectionsPagerAdapter =
              new SectionsPagerAdapter(this, getSupportFragmentManager());
      ViewPager viewPager = findViewById(R.id.view_pager);
    viewPager.setAdapter(sectionsPagerAdapter);
    viewPager.setCurrentItem(2); // home page
    TabLayout tabs = findViewById(R.id.tabs);
    logOut_btn = findViewById(R.id.logOutBtn);
    tabs.setupWithViewPager(viewPager);
    tab0 = tabs.getTabAt(0);
    tab1 = tabs.getTabAt(1);
    tab2 = tabs.getTabAt(2);
    tab3 = tabs.getTabAt(3);
    tab4 = tabs.getTabAt(4);

    if (!isAdmin) {
      assert tab0 != null;
      tab0.setCustomView(R.layout.tab_item);
      TextView textTab0 = tab0.getCustomView().findViewById(R.id.text1);
      textTab0.setText(getString(R.string.tab_text_1));
      ImageView imageViewTab0 = tab0.getCustomView().findViewById(R.id.icon);
      imageViewTab0.setImageResource(R.drawable.account);
      //      tab0.setIcon(R.drawable.account);
      assert tab1 != null;
      tab1.setCustomView(R.layout.tab_item);
      TextView textTab1 = tab1.getCustomView().findViewById(R.id.text1);
      textTab1.setText(getString(R.string.tab_text_2));
      ImageView imageViewTab1 = tab1.getCustomView().findViewById(R.id.icon);
      imageViewTab1.setImageResource(R.drawable.calendar_white);
      //      tab1.setIcon(R.drawable.calendar_white);
      assert tab2 != null;
      tab2.setCustomView(R.layout.tab_item);
      TextView textTab2 = tab2.getCustomView().findViewById(R.id.text1);
      textTab2.setText(getString(R.string.home_tab));
      ImageView imageViewTab2 = tab2.getCustomView().findViewById(R.id.icon);
      imageViewTab2.setImageResource(R.drawable.home);
      //      tab1.setIcon(R.drawable.calendar_white);
      assert tab3 != null;
      tab3.setCustomView(R.layout.tab_item);
      TextView textTab3 = tab3.getCustomView().findViewById(R.id.text1);
      textTab3.setText(getString(R.string.tab_text_3));
      ImageView imageViewTab3 = tab3.getCustomView().findViewById(R.id.icon);
      imageViewTab3.setImageResource(R.drawable.tick);
      //      tab2.setIcon(R.drawable.tick);
      assert tab4 != null;
      tab4.setCustomView(R.layout.tab_item);
      TextView textTab4 = tab4.getCustomView().findViewById(R.id.text1);
      textTab4.setText(getString(R.string.tab_text_4));
      ImageView imageViewTab4 = tab4.getCustomView().findViewById(R.id.icon);
      imageViewTab4.setImageResource(R.drawable.trend);
      //      tab3.setIcon(R.drawable.trend);
    } else {
      assert tab0 != null;
      tab0.setCustomView(R.layout.tab_item);
      TextView textTab0 = tab0.getCustomView().findViewById(R.id.text1);
      textTab0.setText(getString(R.string.admin_tab_text_1));
      ImageView imageViewTab0 = tab0.getCustomView().findViewById(R.id.icon);
      imageViewTab0.setImageResource(R.drawable.settings);
      //      tab0.setIcon(R.drawable.account);
      assert tab1 != null;
      tab1.setCustomView(R.layout.tab_item);
      TextView textTab1 = tab1.getCustomView().findViewById(R.id.text1);
      textTab1.setText(getString(R.string.admin_tab_text_2));
      ImageView imageViewTab1 = tab1.getCustomView().findViewById(R.id.icon);
      imageViewTab1.setImageResource(R.drawable.calendar_white);
      //      tab1.setIcon(R.drawable.calendar_white);
      assert tab2 != null;
      tab2.setCustomView(R.layout.tab_item);
      TextView textTab2 = tab2.getCustomView().findViewById(R.id.text1);
      textTab2.setText(getString(R.string.home_tab));
      ImageView imageViewTab2 = tab2.getCustomView().findViewById(R.id.icon);
      imageViewTab2.setImageResource(R.drawable.home);
      //      tab1.setIcon(R.drawable.calendar_white);
      assert tab3 != null;
      tab3.setCustomView(R.layout.tab_item);
      TextView textTab3 = tab3.getCustomView().findViewById(R.id.text1);
      textTab3.setText(getString(R.string.admin_tab_text_3));
      ImageView imageViewTab3 = tab3.getCustomView().findViewById(R.id.icon);
      imageViewTab3.setImageResource(R.drawable.tick);
      //      tab2.setIcon(R.drawable.tick);
      assert tab4 != null;
      tab4.setCustomView(R.layout.tab_item);
      TextView textTab4 = tab4.getCustomView().findViewById(R.id.text1);
      textTab4.setText(getString(R.string.admin_tab_text_4));
      ImageView imageViewTab4 = tab4.getCustomView().findViewById(R.id.icon);
      imageViewTab4.setImageResource(R.drawable.add);
      //      tab3.setIcon(R.drawable.trend);
    }
    FloatingActionButton fab = findViewById(R.id.fab);

      fab.setOnClickListener(
              view -> {
                  if (isAdmin)
                      startActivity(new Intent(getApplicationContext(), MessagesReadActivity.class));
                  else
                      startActivity(new Intent(getApplicationContext(), MessagesWriteActivity.class));
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
    } else {
      isAdmin = false;
      isManager = false;
      isMrkzy = false;
    }
    finish();
  }
}
