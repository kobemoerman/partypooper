package com.application.android.partypooper.Activity;

import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.android.partypooper.Adapter.SectionsPageAdapter;
import com.application.android.partypooper.Fragment.EventCommunityFragment;
import com.application.android.partypooper.Fragment.EventInformationFragment;
import com.application.android.partypooper.Model.Event;
import com.application.android.partypooper.R;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.text.DateFormatSymbols;

public class EventActivity extends AppCompatActivity {

    private ImageView image;

    private TextView name, date, host;

    private TabLayout mTabLayout;

    private ViewPager mPager;

    private DatabaseReference mEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        initView();
        setUpViewPager();
        loadEventData();
    }

    private void initView() {
        Bundle b = getIntent().getExtras();
        String id = b.getString("id");

        mEvent = FirebaseDatabase.getInstance().getReference("Events").child(id);

        mPager = findViewById(R.id.event_view_pager);
        mTabLayout = findViewById(R.id.event_tab_layout);
        name = findViewById(R.id.event_name);
        date = findViewById(R.id.event_date_time);
        host = findViewById(R.id.event_host);
        image = findViewById(R.id.event_image);
    }

    private void loadEventData() {
        mEvent.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Event event = dataSnapshot.getValue(Event.class);

                String date_stamp = event.getDate_stamp();
                String year = date_stamp.substring(0, 4);
                int month = Integer.parseInt(date_stamp.substring(4, 6));
                String day = date_stamp.substring(6, 8);
                String hour = date_stamp.substring(8, 10);
                String min = date_stamp.substring(10, 12);
                String date_str = day+" "+getMonth(month)+" "+year+", "+hour+":"+min;

                name.setText(event.getName());
                date.setText(date_str);
                host.setText("Invited by "+event.getHost_username());
                if (event.getImageURL() != null) Glide.with(getApplicationContext()).load(event.getImageURL()).into(image);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setUpViewPager() {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new EventInformationFragment(),"Description");
        adapter.addFragment(new EventCommunityFragment(), "Community");

        mPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mPager);
    }

    /**
     * Converts a month in int form to a month in string form.
     * @param month index
     * @return corresponding string
     */
    private String getMonth(int month) {
        return new DateFormatSymbols().getMonths()[month];
    }

}
