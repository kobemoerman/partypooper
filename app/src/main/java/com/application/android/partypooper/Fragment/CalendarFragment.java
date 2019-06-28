package com.application.android.partypooper.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.application.android.partypooper.Activity.HomeActivity;
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
import java.util.List;

public class CalendarFragment extends Fragment {

    /** Reference to the Home Activity */
    private HomeActivity act;

    private List<Event> mEvent;

    private List<String> mMember;

    private FirebaseAuth mAuth;

    private FirebaseUser mUser;

    private RecyclerView recyclerView;

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
        queryEvents();
        itemClickListener();

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

        recyclerView = view.findViewById(R.id.frag_calendar_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdapter = new CalendarAdapter(getContext(),mEvent);
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
                act.onClickLaunchEvent(id);
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
        Query users = FirebaseDatabase.getInstance().getReference().child("Events").orderByChild("date_stamp");
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mEvent.clear();
                for (DataSnapshot snp : dataSnapshot.getChildren()) {
                    Event event = snp.getValue(Event.class);

                    for (String id : mMember) {
                        String time_stamp = event.getTime_stamp()+"?"+event.getHost();
                        if (time_stamp.equals(id)) {
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
     * Converts a month in int form to a month in string form.
     * @param month index
     * @return corresponding string
     */
    private String getMonth(int month) {
        return new DateFormatSymbols().getMonths()[month];
    }

}
