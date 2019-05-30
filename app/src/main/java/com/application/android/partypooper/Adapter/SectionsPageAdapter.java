package com.application.android.partypooper.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class SectionsPageAdapter extends FragmentPagerAdapter {

    /** Keeps track of the fragments */
    private final List<Fragment> mFragment = new ArrayList<>();

    /** Keeps track of the names */
    private final List<String> mTitle = new ArrayList<>();

    /**
     * Adds a fragment with a corresponding title to the tab sections.
     * @param frag fragment to add
     * @param title corresponding title
     */
    public void addFragment(Fragment frag, String title) {
        mFragment.add(frag);
        mTitle.add(title);
    }

    /**
     * Constructor.
     * @param fm fragment manager
     */
    public SectionsPageAdapter(FragmentManager fm) {
        super(fm);
    }

    /**
     * Gets the title at a specific position.
     * @param position integer
     * @return title at position
     */
    @Override
    public CharSequence getPageTitle(int position) {
        return mTitle.get(position);
    }

    /**
     * Gets the size for the tab layout.
     * @return amount of fragments
     */
    @Override
    public int getCount() {
        return mFragment.size();
    }

    /**
     * Gets the fragment at a specific position.
     * @param i integer
     * @return fragment at i
     */
    @Override
    public Fragment getItem(int i) {
        return mFragment.get(i);
    }
}
