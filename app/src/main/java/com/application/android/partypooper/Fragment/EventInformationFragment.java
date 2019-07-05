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
import android.widget.ImageView;
import android.widget.TextView;

import com.application.android.partypooper.Activity.EventActivity;
import com.application.android.partypooper.Adapter.InvitedAdapter;
import com.application.android.partypooper.Model.Event;
import com.application.android.partypooper.Model.User;
import com.application.android.partypooper.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EventInformationFragment extends Fragment {

    /** Reference to the Event Activity */
    private EventActivity act;

    /** ID reference to the event */
    private String mEventID;

    /** Stores all user IDs that have been invited to the event */
    private List<String> invited;

    /** Views for the user to accept or decline the invitation */
    private ImageView accept, decline;

    /** Views to display information about the event */
    private TextView desc, location, amount;

    /** Recycler View to display invited users */
    private RecyclerView recyclerView;

    /** Adapter to display data in the recycler view */
    private InvitedAdapter mAdapter;

    /** Reference to the event */
    private DatabaseReference mEvent;

    /** Reference to the user invited events */
    private DatabaseReference mInvited;

    /** Reference to the members of the event */
    private DatabaseReference mMembers;

    /** Firebase authentication */
    private FirebaseAuth mAuth;

    /** Firebase user */
    private FirebaseUser mUser;

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
        isUserComing();
        queryInvitedUsers();
        loadEventData();
        acceptOnClickListener();
        declineOnClickListener();

        return view;
    }

    /**
     * Initialises the fragment view.
     * @param view fragment_event_information
     */
    private void initView(View view) {
        act = (EventActivity) getActivity();

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mEvent = act.getmEvent();
        mEventID = act.getID();

        mInvited = FirebaseDatabase.getInstance().getReference().child("Invited").child(mUser.getUid());
        mMembers = FirebaseDatabase.getInstance().getReference().child("Members").child(mEventID);

        invited = new ArrayList<>();

        desc = view.findViewById(R.id.frag_event_info_desc);
        amount = view.findViewById(R.id.frag_event_info_invited);
        location = view.findViewById(R.id.frag_event_info_location);
        accept = view.findViewById(R.id.frag_event_info_accept_image);
        decline = view.findViewById(R.id.frag_event_info_decline_image);

        recyclerView = view.findViewById(R.id.frag_event_info_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        mAdapter = new InvitedAdapter(getContext(), new ArrayList<User>());
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
        mMembers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                invited.clear();

                for (DataSnapshot snp : dataSnapshot.getChildren()) {
                    invited.add(snp.getKey());
                }

                amount.setText(String.format("%d people have been invited", invited.size()));
                showUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Displays information of all the users invited to the event.
     */
    private void showUsers() {
        DatabaseReference users = FirebaseDatabase.getInstance().getReference("Users");
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mAdapter.clear();

                for (DataSnapshot snp : dataSnapshot.getChildren()) {
                    User user = snp.getValue(User.class);

                    for (String id : invited) {
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

    /**
     * Updates the value of the user inside the event invited reference.
     * Sets the image view for accept.
     */
    private void isUserComing() {
        mInvited.child(mEventID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) return;

                boolean coming = (boolean) dataSnapshot.getValue();

                if (coming) {
                    accept.setImageResource(R.drawable.ic_check_circle);
                } else {
                    accept.setImageResource(R.drawable.ic_check_circle_grey);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Listener to remove event references for the user.
     * Closes the activity.
     */
    private void declineOnClickListener() {
        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInvited.child(mEventID).getRef().removeValue();
                mMembers.child(mUser.getUid()).getRef().removeValue();
                act.finish();
            }
        });
    }

    /**
     * Listener to update the user inside the event invited reference.
     */
    private void acceptOnClickListener() {
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { mInvited.child(mEventID).setValue(true);
            }
        });
    }
}
