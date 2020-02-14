package com.trichain.omiinad.adapters;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.trichain.omiinad.fragments.AboutFragment;
import com.trichain.omiinad.fragments.DateFragment;
import com.trichain.omiinad.fragments.LocationFragment;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private static final String[] TAB_TITLES = new String[]{"Date", "Location","About"};
    int count= TAB_TITLES.length;
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:return new DateFragment();
            case 1:return new LocationFragment();
            case 2:return new AboutFragment();
            default:return null;
        }
        // getItem is called to instantiate the fragment for the given page.

    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return TAB_TITLES[position];
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 3;
    }
}