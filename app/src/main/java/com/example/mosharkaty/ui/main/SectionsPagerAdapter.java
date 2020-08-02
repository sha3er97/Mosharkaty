package com.example.mosharkaty.ui.main;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.mosharkaty.AdminAddGroupMosharka;
import com.example.mosharkaty.AdminEvents;
import com.example.mosharkaty.AdminShowMosharkat;
import com.example.mosharkaty.CalendarFragment;
import com.example.mosharkaty.ComposeMosharkaFragment;
import com.example.mosharkaty.ProfileFragment;
import com.example.mosharkaty.R;

import static com.example.mosharkaty.LoginActivity.isAdmin;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

  @StringRes
  private static final int[] TAB_TITLES =
      new int[] {R.string.tab_text_1, R.string.tab_text_2, R.string.tab_text_3};

  private static final int[] ADMIN_TAB_TITLES =
      new int[] {R.string.admin_tab_text_1, R.string.admin_tab_text_2, R.string.admin_tab_text_3};
  private final Context mContext;

  public SectionsPagerAdapter(Context context, FragmentManager fm) {
    super(fm);
    mContext = context;
  }

  @Override
  public Fragment getItem(int position) {
    // getItem is called to instantiate the fragment for the given page.
    // Return a PlaceholderFragment (defined as a static inner class below).
    //        return PlaceholderFragment.newInstance(position + 1);
    Fragment fragment = null;
    if (isAdmin) { // admin tabs
      switch (position) {
        case 0:
          fragment = new AdminAddGroupMosharka();
          break;
        case 1:
          fragment = new AdminEvents();
          break;
        case 2:
          fragment = new AdminShowMosharkat();
          break;
      }
    } else {
      switch (position) {
        case 0:
          fragment = new ProfileFragment();
          break;
        case 1:
          fragment = new CalendarFragment();
          break;
        case 2:
          fragment = new ComposeMosharkaFragment();
          break;
      }
    }
    return fragment;
  }

  @Nullable
  @Override
  public CharSequence getPageTitle(int position) {
    if (isAdmin) return mContext.getResources().getString(ADMIN_TAB_TITLES[position]);
    return mContext.getResources().getString(TAB_TITLES[position]);
  }

  @Override
  public int getCount() {
    // Show 3 total pages.
    return 3;
  }
}
