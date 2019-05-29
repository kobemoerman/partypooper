package com.application.android.partypooper.Activity;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.application.android.partypooper.Adapter.SectionsPageAdapter;
import com.application.android.partypooper.Fragment.EventCommunityFragment;
import com.application.android.partypooper.Fragment.EventInformationFragment;
import com.application.android.partypooper.R;

public class EventActivity extends AppCompatActivity {

    private TabLayout mTabLayout;

    private ViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        initView();
        setUpViewPager();
    }

    private void initView() {
        Bundle b = getIntent().getExtras();
        String id = b.getString("id");

        mPager = findViewById(R.id.event_view_pager);
        mTabLayout = findViewById(R.id.event_tab_layout);
    }

    private void setUpViewPager() {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new EventInformationFragment(),"Description");
        adapter.addFragment(new EventCommunityFragment(), "Community");

        mPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mPager);
    }
}
