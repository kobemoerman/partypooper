package com.application.android.partypooper.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.application.android.partypooper.Activity.EventActivity;
import com.application.android.partypooper.Adapter.RecommendationEventAdapter;
import com.application.android.partypooper.Model.Recommendation;
import com.application.android.partypooper.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.FormatFlagsConversionMismatchException;
import java.util.Map;

public class EventRecommendationFragment extends Fragment {

    /** Reference to the Event Activity */
    private EventActivity act;

    /** Unique ID of the event */
    private String eventID;

    /** List view to display the recommendations */
    private ListView list;

    /** Displays information text when there are no recommendations */
    private TextView empty;

    /** Adapter to display items in the list view */
    private RecommendationEventAdapter mAdapter;

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
        View view = inflater.inflate(R.layout.fragment_event_recommendation,container,false);

        initView(view);

        queryRecommendationList();

        return view;
    }

    /**
     * Initialises the fragment view.
     * @param view fragment_event_recommendation
     */
    private void initView(View view) {
        act = (EventActivity) getActivity();

        assert act != null;

        eventID = act.getID();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        list = view.findViewById(R.id.frag_event_recommendation_list_view);
        empty = view.findViewById(R.id.frag_event_recommendation_text);

        mAdapter = new RecommendationEventAdapter(getContext(), R.layout.item_recommendation_event, new ArrayList<Recommendation>(), eventID, mUser.getUid());
        list.setAdapter(mAdapter);
    }

    /**
     * Starts a query to retrieve all event recommendations.
     */
    private void queryRecommendationList() {
        final DatabaseReference refRecom = FirebaseDatabase.getInstance().getReference().child("Recommendation").child(eventID);
        final DatabaseReference refBring = FirebaseDatabase.getInstance().getReference().child("Bringing").child(mUser.getUid()).child(eventID);

        refRecom.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                final String item = dataSnapshot.getKey();
                Map<String, Object> amount = (Map<String, Object>) dataSnapshot.getValue();

                final int total = ((Long) amount.get("total")).intValue();

                refBring.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int brought = 0;

                        if (dataSnapshot.child(item).exists()) {
                            brought = ((Long) dataSnapshot.child(item).getValue()).intValue();
                        } else {
                            refBring.child(item).setValue(brought);
                        }

                        mAdapter.add(new Recommendation(brought,total,item));
                        isRecommendationsEmpty();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                final String item = dataSnapshot.getKey();
                mAdapter.removeItem(item);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Display information text if the adapter list is empty
     */
    private void isRecommendationsEmpty() {
        if (mAdapter.isEmpty()) {
            empty.setText("You do not have to bring anything.");
        }
    }
}
