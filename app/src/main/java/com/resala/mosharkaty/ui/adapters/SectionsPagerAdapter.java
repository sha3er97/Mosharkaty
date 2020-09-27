package com.resala.mosharkaty.ui.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.resala.mosharkaty.R;
import com.resala.mosharkaty.fragments.AdminAddEventsFragment;
import com.resala.mosharkaty.fragments.AdminAddGroupMosharkaFragment;
import com.resala.mosharkaty.fragments.AdminShowMosharkatFragment;
import com.resala.mosharkaty.fragments.AdminShowReportsFragment;
import com.resala.mosharkaty.fragments.ComposeMosharkaFragment;
import com.resala.mosharkaty.fragments.HomeFragment;
import com.resala.mosharkaty.fragments.ProfileFragment;
import com.resala.mosharkaty.fragments.TakyeemFragment;
import com.resala.mosharkaty.fragments.UserCalendarFragment;

import static com.resala.mosharkaty.LoginActivity.isAdmin;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

  @StringRes
  private static final int[] TAB_TITLES =
          new int[]{
                  R.string.tab_text_1,
                  R.string.tab_text_2,
                  R.string.home_tab,
                  R.string.tab_text_3,
                  R.string.tab_text_4
          };

  @StringRes
  private static final int[] ADMIN_TAB_TITLES =
          new int[]{
                  R.string.admin_tab_text_1,
                  R.string.admin_tab_text_2,
                  R.string.home_tab,
                  R.string.admin_tab_text_3,
                  R.string.admin_tab_text_4
          };

  private final Context mContext;

  public SectionsPagerAdapter(Context context, FragmentManager fm) {
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
    if (isAdmin) { // admin tabs
      switch (position) {
        case 0:
          fragment = new AdminShowReportsFragment();
          break;
        case 1:
          fragment = new AdminAddEventsFragment();
          break;
        case 2:
          fragment = new HomeFragment();
          break;
        case 3:
          fragment = new AdminShowMosharkatFragment();
          break;
        case 4:
          fragment = new AdminAddGroupMosharkaFragment();
          break;
      }
    } else {
      switch (position) {
        case 0:
          fragment = new ProfileFragment();
          break;
        case 1:
          fragment = new UserCalendarFragment();
          break;
        case 2:
          fragment = new HomeFragment();
          break;
        case 3:
          fragment = new ComposeMosharkaFragment();
          break;
        case 4:
          fragment = new TakyeemFragment();
          break;
      }
    }
    assert fragment != null;
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
    // Show 5 total pages.
    return 5;
  }
}
