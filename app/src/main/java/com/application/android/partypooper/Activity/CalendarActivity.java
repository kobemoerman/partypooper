package com.application.android.partypooper.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.application.android.partypooper.Adapter.CalendarAdapter;
import com.application.android.partypooper.Adapter.CalendarDecoration;
import com.application.android.partypooper.Adapter.Section;
import com.application.android.partypooper.Model.Event;
import com.application.android.partypooper.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarActivity extends AppCompatActivity {

    private List<Event> mEvent;

    private List<String> mMember;

    private FirebaseAuth mAuth;

    private FirebaseUser mUser;

    private RecyclerView recyclerView;

    private CalendarAdapter mAdapter;

    /**
     * On create method of the activity.
     * @param savedInstanceState this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        initView();
        queryEvents();
        itemClickListener();
    }

    /**
     * Initialises the activity view.
     */
    private void initView() {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mEvent = new ArrayList<>();
        mMember = new ArrayList<>();

        recyclerView = findViewById(R.id.calendar_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new CalendarAdapter(this,mEvent);
        recyclerView.setAdapter(mAdapter);
    }

    /**
     * Take action on the item clicked.
     */
    private void itemClickListener() {
        mAdapter.setOnItemClickListener(new CalendarAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                Event e = mAdapter.getItem(pos);
                String id = e.getTime_stamp()+"?"+e.getHost();
                onClickLaunchEvent(id);
            }
        });
    }

    /**
     * Creates a query to retrieve all events of the user.
     */
    private void queryEvents() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Invited").child(mUser.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mMember.clear();
                for (DataSnapshot snp : dataSnapshot.getChildren()) {
                    mMember.add(snp.getKey());
                }
                showEvents();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    /**
     * Displays the events with the help of the adapter.
     */
    private void showEvents() {
        Query events = FirebaseDatabase.getInstance().getReference().child("Events").orderByChild("date_stamp");
        events.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mEvent.clear();
                for (DataSnapshot snp : dataSnapshot.getChildren()) {
                    Event event = snp.getValue(Event.class);

                    for (String id : mMember) {
                        String time_stamp = event.getTime_stamp()+"?"+event.getHost();
                        String user = event.getHost();
                        if (time_stamp.equals(id) && user.equals(mUser.getUid())) {
                            mEvent.add(event);
                        }
                    }
                }

                if (!mEvent.isEmpty()) {
                    CalendarDecoration decoration =
                        new CalendarDecoration(getResources().getDimensionPixelSize(R.dimen.header),
                            getSectionCallback(mEvent));
                    recyclerView.addItemDecoration(decoration);
                }
                mAdapter.setItems(mEvent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    /**
     * Determines if an item is a header.
     * @param item list of events
     * @return header information
     */
    private Section getSectionCallback(final List<Event> item) {
        return new Section() {
            @Override
            public boolean isHeader(int position) {
                if (position == 0) {
                    return true;
                }

                String header = item.get(position).getDate_stamp().substring(0,7);
                String event = item.get(position-1).getDate_stamp().substring(0,7);

                return !header.equals(event);
            }

            @Override
            public CharSequence getSectionHeader(int position) {
                String date_stamp = item.get(position).getDate_stamp().substring(0,8);
                String year = date_stamp.substring(0,4);
                String month = getMonth(Integer.parseInt(date_stamp.substring(4,6)));
                String day = date_stamp.substring(6,8);

                return day + " " + month + " " + year;
            }
        };
    }

    /**
     * On click listener for the calendar recycler view.
     * Launches the EventActivity.
     * @param id of the event
     */
    public void onClickLaunchEvent(String id) {
        Intent eventIntent = new Intent(getApplicationContext(), EventActivity.class);
        eventIntent.putExtra("id",id);
        startActivity(eventIntent);
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
     * Listener to close the activity.
     * @param view of the activity
     */
    public void onClickBackCalendar(View view) {
        finish();
    }
}
