package com.application.android.partypooper.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.application.android.partypooper.Adapter.TabPageAdapter;
import com.application.android.partypooper.R;

public class CalendarTabFragment extends Fragment {

    /** Allows the user to flip left and right through pages of data. */
    private ViewPager mPager;

    /** Responsible for the tab actions */
    private TabLayout mTabLayout;

    /** Adapter to display data in the pager */
    private TabPageAdapter mAdapter;

    /**
     * On create method of the fragment.
     * @param inflater inflate any views in the fragment
     * @param container parent view that the fragment's UI should be attached to
     * @param savedInstanceState this fragment is being re-constructed from a previous saved state as given here
     * @return Return the View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar_tabs, container, false);

        initView(view);

        return view;
    }

    /**
     * Initialises the fragment view.
     * @param v fragment_calendar_tabs
     */
    private void initView(View v) {
        mPager = v.findViewById(R.id.frag_calendar_tabs_pager);
        mTabLayout = v.findViewById(R.id.frag_calendar_tabs_layout);
        mAdapter = new TabPageAdapter(getFragmentManager());

        mAdapter.addFragment(newInstance(true), "Coming");
        mAdapter.addFragment(newInstance(false), "Past");

        mPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mPager);
    }

    /**
     * Creates a new instance of CalendarFragment.
     * @param type true for coming events and false for past events
     * @return CalendarFragment
     */
    private static CalendarFragment newInstance (boolean type) {
        CalendarFragment frag = new CalendarFragment();

        Bundle args = new Bundle();
        args.putBoolean("type", type);
        frag.setArguments(args);

        return frag;
    }
}
