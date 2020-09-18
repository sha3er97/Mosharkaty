package com.resala.mosharkaty.ui.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.resala.mosharkaty.R;
import com.resala.mosharkaty.fragments.AddMeetingFragment;
import com.resala.mosharkaty.fragments.ShowMeetingFragment;

public class MeetingsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES =
            new int[]{R.string.meeting_tab_text_1, R.string.meeting_tab_text_2};

    private final Context mContext;

    public MeetingsPagerAdapter(Context context, FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mContext = context;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        //        return PlaceholderFragment.newInstance(position + 1);
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new AddMeetingFragment();
                break;
            case 1:
                fragment = new ShowMeetingFragment();
                break;
        }
        assert fragment != null;
        return fragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 2;
    }
}
