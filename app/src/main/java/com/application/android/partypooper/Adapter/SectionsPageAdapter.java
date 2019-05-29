package com.application.android.partypooper.Adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class SectionsPageAdapter extends FragmentPagerAdapter {

    /** Keeps track of the fragments */
    private final ArrayList<Fragment> mFragment = new ArrayList<>();

    /** Keeps track of the names */
    private final ArrayList<String> mTitle = new ArrayList<>();

    public void addFragment(Fragment frag, String title) {
        mFragment.add(frag);
        mTitle.add(title);
    }

    public SectionsPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitle.get(position);
    }

    @Override
    public int getCount() {
        return mTitle.size();
    }

    @Override
    public Fragment getItem(int i) {
        return mFragment.get(i);
    }
}
