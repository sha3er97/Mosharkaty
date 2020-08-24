package com.resala.mosharkaty.ui.main;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.resala.mosharkaty.AdminAddGroupMosharka;
import com.resala.mosharkaty.AdminEvents;
import com.resala.mosharkaty.AdminShowMosharkat;
import com.resala.mosharkaty.AdminShowReports;
import com.resala.mosharkaty.CalendarFragment;
import com.resala.mosharkaty.ComposeMosharkaFragment;
import com.resala.mosharkaty.ProfileFragment;
import com.resala.mosharkaty.R;
import com.resala.mosharkaty.TakyeemFragment;

import static com.resala.mosharkaty.LoginActivity.isAdmin;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

  @StringRes
  private static final int[] TAB_TITLES =
          new int[]{
                  R.string.tab_text_1, R.string.tab_text_2, R.string.tab_text_3, R.string.tab_text_4
          };

  private static final int[] ADMIN_TAB_TITLES =
          new int[]{
                  R.string.admin_tab_text_1,
                  R.string.admin_tab_text_2,
                  R.string.admin_tab_text_3,
                  R.string.admin_tab_text_4
          };
  private final Context mContext;

  public SectionsPagerAdapter(Context context, FragmentManager fm) {
    super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
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
          fragment = new AdminShowReports();
          break;
        case 1:
          fragment = new AdminEvents();
          break;
        case 2:
          fragment = new AdminShowMosharkat();
          break;
        case 3:
          fragment = new AdminAddGroupMosharka();
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
        case 3:
          fragment = new TakyeemFragment();
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
    // Show 4 total pages.
    return 4;
  }
}
