package com.resala.mosharkaty;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.resala.mosharkaty.ui.main.MeetingsPagerAdapter;

public class AdminMeetingsActivity extends AppCompatActivity {
    public TabLayout.Tab tab0;
    public TabLayout.Tab tab1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meetings);
        MeetingsPagerAdapter sectionsPagerAdapter = new MeetingsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        tab0 = tabs.getTabAt(0);
        tab1 = tabs.getTabAt(1);

        assert tab0 != null;
        tab0.setCustomView(R.layout.tab_item);
        TextView textTab0 = tab0.getCustomView().findViewById(R.id.text1);
        textTab0.setText(getString(R.string.meeting_tab_text_1));
        ImageView imageViewTab0 = tab0.getCustomView().findViewById(R.id.icon);
        imageViewTab0.setImageResource(R.drawable.add);
        //      tab0.setIcon(R.drawable.account);
        assert tab1 != null;
        tab1.setCustomView(R.layout.tab_item);
        TextView textTab1 = tab1.getCustomView().findViewById(R.id.text1);
        textTab1.setText(getString(R.string.meeting_tab_text_2));
        ImageView imageViewTab1 = tab1.getCustomView().findViewById(R.id.icon);
        imageViewTab1.setImageResource(R.drawable.meeting);
    }
}