package com.application.android.partypooper.Fragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.application.android.partypooper.Activity.HomeActivity;
import com.application.android.partypooper.Adapter.CalendarAdapter;
import com.application.android.partypooper.Adapter.CalendarDecoration;
import com.application.android.partypooper.Model.Section;
import com.application.android.partypooper.Model.Event;
import com.application.android.partypooper.Notification.NotificationReceiver;
import com.application.android.partypooper.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarFragment extends Fragment implements CalendarAdapter.ItemClickListener {

    /** Number of items to be displayed inside the recycler view */
    private int LIMIT = 20;

    /** Reference to the Home Activity */
    private HomeActivity act;

    /** true for coming events, else for past events */
    private Boolean type;

    /** List of all events */
    private List<Section> mEvent;

    /** List of all events the user is a member of */
    private List<String> mMember;

    /** Firebase authentication */
    private FirebaseAuth mAuth;

    /** Firebase user */
    private FirebaseUser mUser;

    /** Recycler View to display events */
    private RecyclerView recyclerView;

    /** Adapter to display data in the recycler view */
    private CalendarAdapter mAdapter;

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
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        initView(view);
        recyclerScrollListener();
        queryEvents();

        return view;
    }

    /**
     * Initialises the fragment view.
     * @param view fragment_calendar
     */
    private void initView(View view) {
        act = (HomeActivity) getActivity();
        assert act != null;

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mEvent = new ArrayList<>();
        mMember = new ArrayList<>();

        Bundle args = getArguments();
        type = args.getBoolean("type");

        recyclerView = view.findViewById(R.id.frag_calendar_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdapter = new CalendarAdapter(getContext(),new ArrayList<Section>());
        mAdapter.setClickListener(this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.addItemDecoration(new CalendarDecoration(mAdapter));
    }

    /**
     * Take action on the item clicked.
     */
    @Override
    public void onItemClick(View view, int pos) {
        Event e = mAdapter.getItem(pos).getEvent();
        String id = e.getTime_stamp()+"?"+e.getHost();
        act.onClickLaunchEvent(id);
    }

    /**
     * Increases the value of LIMIT once the bottom of the view has been reached.
     */
    private void recyclerScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                // The user cannot scroll down anymore
                if (!recyclerView.canScrollVertically(1)) {
                    LIMIT+=20;
                }
            }
        });
    }

    /**
     * Creates a query to retrieve all events of the user.
     */
    private void queryEvents() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Invited").child(mUser.getUid());
        ref.addValueEventListener(new ValueEventListener() {
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
        Query users = FirebaseDatabase.getInstance().getReference().child("Events").orderByChild("date_stamp");
        String date_stamp = getDateStamp();

        if (type) {
             users = users.startAt(date_stamp);
        } else {
            users = users.endAt(date_stamp);
        }

        users.limitToFirst(LIMIT).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int index = 0;
                mEvent.clear();
                for (DataSnapshot snp : dataSnapshot.getChildren()) {
                    Event event = snp.getValue(Event.class);
                    String time_stamp = event.getTime_stamp()+"?"+event.getHost();

                    for (String id : mMember) {
                        if (time_stamp.equals(id)) {
                            if (isHeader(index, event)) {
                                mEvent.add(new Section(event, 0));
                                mEvent.add(new Section(event, 1));
                                index+=2;
                            } else {
                                mEvent.add(new Section(event,1));
                                index++;
                            }
                            if (type) {
                                createNotification(event.getName(), event.getDate_stamp(), index);
                            }
                        }
                    }
                }
                mAdapter.setItems(mEvent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private boolean isHeader (int position, Event event) {
        if (position == 0) {
            return true;
        }

        String header = mEvent.get(position-1).getEvent().getDate_stamp().substring(0,8);
        String item = event.getDate_stamp().substring(0,8);

        return !header.equals(item);
    }

    private void createNotification (String name, String date, int ID) {
        int year = Integer.parseInt(date.substring(0,4));
        int month = Integer.parseInt(date.substring(4,6));
        int day = Integer.parseInt(date.substring(6,8));
        int hour = Integer.parseInt(date.substring(8,10));
        int min = Integer.parseInt(date.substring(10,12));

        Calendar calendar = Calendar.getInstance();
        calendar.set(year,month,day,hour,min,0);
        calendar.add(Calendar.HOUR_OF_DAY, -4);

        AlarmManager mAlarm = (AlarmManager) act.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(getContext(),NotificationReceiver.class);
        intent.putExtra("eventName", name);
        intent.putExtra("eventDate", hour+":"+ (min<10?"0"+min:min));
        intent.putExtra("ID", ID);

        PendingIntent pending = PendingIntent.getBroadcast(getContext(), ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        mAlarm.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pending);
    }

    /**
     * Determines the current date stamp.
     * @return a string of the current date
     */
    private String getDateStamp() {
        Calendar cal = Calendar.getInstance();

        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        String y = String.valueOf(year);
        String m = String.valueOf(month);
        String d = String.valueOf(day);

        if (month < 10) m = "0"+month;
        if (day < 10) d = "0"+day;

        return y+m+d;
    }
}
