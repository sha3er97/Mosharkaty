package com.resala.mosharkaty;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.resala.mosharkaty.ui.adapters.NasheetPagerAdapter;

public class NasheetActivity extends AppCompatActivity {
    public TabLayout.Tab tab0;
    public TabLayout.Tab tab1;
    public TabLayout.Tab tab2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nasheet);
        NasheetPagerAdapter sectionsPagerAdapter = new NasheetPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        tab0 = tabs.getTabAt(0);
        tab1 = tabs.getTabAt(1);
        tab2 = tabs.getTabAt(2);

        assert tab0 != null;
        tab0.setCustomView(R.layout.tab_item);
        TextView textTab0 = tab0.getCustomView().findViewById(R.id.text1);
        textTab0.setText(getString(R.string.nasheet_tab_text_1));
        ImageView imageViewTab0 = tab0.getCustomView().findViewById(R.id.icon);
        imageViewTab0.setImageResource(R.drawable.person_add);
        //      tab0.setIcon(R.drawable.account);
        assert tab1 != null;
        tab1.setCustomView(R.layout.tab_item);
        TextView textTab1 = tab1.getCustomView().findViewById(R.id.text1);
        textTab1.setText(getString(R.string.nasheet_tab_text_2));
        ImageView imageViewTab1 = tab1.getCustomView().findViewById(R.id.icon);
        imageViewTab1.setImageResource(R.drawable.filter);
        assert tab2 != null;
        tab2.setCustomView(R.layout.tab_item);
        TextView textTab2 = tab2.getCustomView().findViewById(R.id.text1);
        textTab2.setText(getString(R.string.nasheet_tab_text_3));
        ImageView imageViewTab2 = tab2.getCustomView().findViewById(R.id.icon);
        imageViewTab2.setImageResource(R.drawable.trend);
    }
}