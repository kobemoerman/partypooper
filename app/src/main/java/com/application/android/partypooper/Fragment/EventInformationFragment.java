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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.application.android.partypooper.Activity.EventActivity;
import com.application.android.partypooper.Adapter.InvitedAdapter;
import com.application.android.partypooper.Model.Event;
import com.application.android.partypooper.Model.User;
import com.application.android.partypooper.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EventInformationFragment extends Fragment {

    /** Recycler View to display invited users */
    private RecyclerView recyclerView;

    private TextView desc, location, invited;

    private LinearLayout accept, decline;

    /** Adapter to display data in the recycler view */
    private InvitedAdapter mAdapter;

    private String mEventID;

    /** List of all events */
    private List<User> mUser;

    private List<String> mMember;

    private EventActivity act;

    private DatabaseReference mEvent;

    /**
     * On create method of the fragment.
     * @param inflater inflate any views in the fragment
     * @param container parent view that the fragment's UI should be attached to
     * @param savedInstanceState this fragment is being re-constructed from a previous saved state as given here
     * @return Return the View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_information,container,false);

        initView(view);
        queryInvitedUsers();
        loadEventData();
        acceptOnClickListener();
        declineOnClickListener();

        return view;
    }

    private void initView(View view) {
        act = (EventActivity) getActivity();

        mEvent = act.getmEvent();
        mEventID = act.getID();

        mUser = new ArrayList<>();
        mMember = new ArrayList<>();

        desc = view.findViewById(R.id.frag_event_info_desc);
        invited = view.findViewById(R.id.frag_event_info_invited);
        location = view.findViewById(R.id.frag_event_info_location);
        accept = view.findViewById(R.id.frag_event_info_accept);
        decline = view.findViewById(R.id.frag_event_info_decline);

        recyclerView = view.findViewById(R.id.frag_event_info_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        mAdapter = new InvitedAdapter(getContext(), mUser);
        recyclerView.setAdapter(mAdapter);
    }

    /**
     * Populate the layout with the event data.
     */
    private void loadEventData() {
        mEvent.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Event event = dataSnapshot.getValue(Event.class);

                desc.setText(event.getDescription());
                location.setText(event.getLocation());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Creates a query to retrieve all users invited to the event.
     */
    private void queryInvitedUsers() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Members").child(mEventID);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mMember.clear();

                for (DataSnapshot snp : dataSnapshot.getChildren()) {
                    mMember.add(snp.getKey());
                }

                invited.setText(String.format("%d people have been invited", mMember.size()));
                showUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showUsers() {
        DatabaseReference users = FirebaseDatabase.getInstance().getReference("Users");
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mAdapter.clear();

                for (DataSnapshot snp : dataSnapshot.getChildren()) {
                    User user = snp.getValue(User.class);

                    for (String id : mMember) {
                        if (user.getId().equals(id)) {
                            mAdapter.add(user);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void declineOnClickListener() {
        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    private void acceptOnClickListener() {
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }
}
