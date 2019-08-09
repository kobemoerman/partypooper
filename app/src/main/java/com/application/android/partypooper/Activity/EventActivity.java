package com.application.android.partypooper.Activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.application.android.partypooper.Adapter.HeightWrappingViewPager;
import com.application.android.partypooper.Adapter.TabPageAdapter;
import com.application.android.partypooper.Fragment.EventRecommendationFragment;
import com.application.android.partypooper.Fragment.EventInformationFragment;
import com.application.android.partypooper.Model.Event;
import com.application.android.partypooper.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormatSymbols;
import java.util.Objects;

public class EventActivity extends AppCompatActivity {

    /** ID of the event */
    private String ID;

    /** All information relevant to the event */
    private Event event;

    /** Event banner image */
    private ImageView image;

    /** TextView to display event data */
    private TextView name, start_date, end_date, host;

    /** These buttons are only visible by the event owner */
    private Button edit, invite;

    /** Responsible for the tab layout */
    private TabLayout mTabLayout;

    /** Wraps the height of the Pager to the highest child */
    private HeightWrappingViewPager mPager;

    /** Firebase authentication */
    private FirebaseAuth mAuth;

    /** Firebase user */
    private FirebaseUser mUser;

    /** Reference to the current event in Events */
    private DatabaseReference mEvent;

    @Override
    protected void onStart() {
        super.onStart();

        mUser = mAuth.getCurrentUser();

        if (!mUser.getUid().equals(ID.substring(ID.lastIndexOf("?") + 1))) {
            edit.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
            invite.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        }
    }

    /**
     * On create method of the activity.
     * @param savedInstanceState this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        initView();
        setUpViewPager();
        loadEventData();
    }

    /**
     * Initialises the activity view.
     */
    private void initView() {
        Bundle b = getIntent().getExtras();
        ID = Objects.requireNonNull(b).getString("id");

        mAuth = FirebaseAuth.getInstance();

        mEvent = FirebaseDatabase.getInstance().getReference("Events").child(Objects.requireNonNull(ID));

        mPager = findViewById(R.id.event_view_pager);
        mTabLayout = findViewById(R.id.event_tab_layout);
        name = findViewById(R.id.event_name);
        start_date = findViewById(R.id.event_start_date);
        end_date = findViewById(R.id.event_end_date);
        host = findViewById(R.id.event_host);
        image = findViewById(R.id.event_image);
        edit = findViewById(R.id.event_edit);
        invite = findViewById(R.id.event_invite);
    }

    /**
     * Populate the layout with the event data.
     */
    private void loadEventData() {
        mEvent.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                event = dataSnapshot.getValue(Event.class);
                int month;
                String year, day, hour, min, date;

                String sdate = Objects.requireNonNull(event).getStart_date();
                String stime = Objects.requireNonNull(event).getStart_time();

                // date of the form dd/mm/yyyy
                day = sdate.substring(0,2);
                month = Integer.parseInt(sdate.substring(3,5));
                year = sdate.substring(6,10);

                // time of the form hh:mm
                hour = stime.substring(0,2);
                min = stime.substring(3,5);

                // date of the form "dd mm yyyy, hh:mm"
                date = day+" "+getMonth(month)+", "+hour+":"+min;
                start_date.setText(date);

                String edate = Objects.requireNonNull(event).getEnd_date();
                String etime = Objects.requireNonNull(event).getEnd_time();

                // date of the form dd/mm/yyyy
                day = edate.substring(0,2);
                month = Integer.parseInt(edate.substring(3,5));
                year = edate.substring(6,10);

                // time of the form hh:mm
                hour = etime.substring(0,2);
                min = etime.substring(3,5);

                // date of the form "to dd mm yyyy, hh:mm"
                date = "to " + day+" "+getMonth(month)+", "+hour+":"+min;
                end_date.setText(date);

                if (mUser.getUid().equals(event.getHost())) {
                    host.setText(String.format("Hosting event", event.getHost_username()));
                } else {
                    host.setText(String.format("Invited by %s", event.getHost_username()));
                }

                name.setText(event.getName());

                if (event.getImageURL() != null) Glide.with(getApplicationContext()).load(event.getImageURL()).into(image);
                else Glide.with(getApplicationContext()).load(R.drawable.default_banner).into(image);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     *  Initialises the Tab layout
     */
    private void setUpViewPager() {
        TabPageAdapter adapter = new TabPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new EventInformationFragment(),"General");
        adapter.addFragment(new EventRecommendationFragment(), "To bring");

        mPager.setAdapter(adapter);
        mPager.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
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

    /**
     * Firebase reference to the event in Events.
     * @return mEvent
     */
    public DatabaseReference getmEvent() {
        return mEvent;
    }


    /**
     * Listener to close the activity.
     * @param view of the activity
     */
    public void onClickBackEvent(View view) {
        finish();
    }

    /**
     * Listener to open Google Maps directions
     * @param view fragment_event_information
     */
    public void onClickDirectionsEvent(View view) {
        String location = getLocationUri();

        Uri uri = Uri.parse("google.navigation:q=" +location);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
        mapIntent.setPackage("com.google.android.apps.maps");
        try {
            startActivity(mapIntent);
        } catch(ActivityNotFoundException ex) {
            try {
                Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.valueOf(uri)));
                startActivity(unrestrictedIntent);
            } catch(ActivityNotFoundException innerEx) {
                Toast.makeText(this, "Please install a maps application", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Adapts the event address to correspond to google navigation.
     * @return All " " are replaced with "+", all "," are replaced with "%2C"
     */
    private String getLocationUri() {
        String loc = event.getNumber()+"+"+event.getStreet()+"+"+event.getCity();

        return loc.replaceAll(",","%2C");
    }

    /**
     * Listener to share the application
     * @param view fragment_event_information
     */
    public void onClickShareEvent(View view) {
        showMessage("You shared PartyPooper!");
    }

    /**
     * Displays a toast on the screen.
     * @param s text to display
     */
    private void showMessage(String s) {
        Toast.makeText(this,s, Toast.LENGTH_LONG).show();
    }

    /**
     * Returns the event ID.
     * @return ID
     */
    public String getID() {
        return ID;
    }

    public void onClickEventEdit(View view) {
        Intent activity = new Intent(getApplicationContext(), EditEventActivity.class);
        activity.putExtra("id",event.getTime_stamp()+"?"+event.getHost());
        startActivity(activity);
    }

    public void onClickEventInvite(View view) {
        Intent activity = new Intent(getApplicationContext(), InviteEventActivity.class);
        activity.putExtra("id",event.getTime_stamp()+"?"+event.getHost());
        startActivity(activity);
    }
}
