package com.application.android.partypooper.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.application.android.partypooper.Activity.EventActivity;
import com.application.android.partypooper.Model.Event;
import com.application.android.partypooper.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class EventInformationFragment extends Fragment {

    private TextView desc;

    private LinearLayout accept, decline;

    private EventActivity act;

    private DatabaseReference mEvent;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_info,container,false);

        initView(view);
        loadEventData();
        acceptOnClickListener();
        declineOnClickListener();

        return view;
    }

    private void initView(View view) {
        act = (EventActivity) getActivity();

        mEvent = act.getmEvent();

        desc = view.findViewById(R.id.frag_event_info_desc);
        accept = view.findViewById(R.id.frag_event_info_accept);
        decline = view.findViewById(R.id.frag_event_info_decline);
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
                ImageView icon = v.findViewById(R.id.frag_event_info_accept_image);
                System.out.println("Replace icon");
            }
        });
    }
}
