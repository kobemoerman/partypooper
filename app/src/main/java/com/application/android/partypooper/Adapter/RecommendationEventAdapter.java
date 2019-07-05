package com.application.android.partypooper.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.android.partypooper.Model.Recommendation;
import com.application.android.partypooper.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class RecommendationEventAdapter extends ListViewAdapter {

    /** Displays the item Recommendation */
    private TextView itemView;

    /** Displays the amount Recommendation */
    private TextView amountView;

    /** Turns green when the total of items has been reached */
    private ImageView circle;

    /** Icons responsible to add or remove a Recommendation */
    private ImageView remove, add;

    private String event, user;

    private DatabaseReference refRecommendation;

    private DatabaseReference refBringing;

    /**
     * Base constructor.
     *  @param context  of the fragment
     * @param resource layout to be displayed inside the list view
     * @param items    list of items to be displayed
     * @param event    the id of the current event
     * @param user     the id of the current user
     */
    public RecommendationEventAdapter(Context context, int resource, ArrayList<Recommendation> items, String event, String user) {
        super(context, resource, items);
        this.event = event;
        this.user = user;

        refRecommendation = FirebaseDatabase.getInstance().getReference().child("Recommendation").child(event);
        refBringing = FirebaseDatabase.getInstance().getReference().child("Bringing").child(user).child(event);
    }

    /**
     * Get a View that displays the data at the specified position in the data set.
     *
     * @param pos The position of the item within the adapter's data set of the item
     * @param view This value may be null
     * @param parent This value must never be null
     * @return the view
     */
    @Override
    public View getView(int pos, View view, ViewGroup parent) {
        final int amount = Objects.requireNonNull(getItem(pos)).getAmount();
        final String item = Objects.requireNonNull(getItem(pos)).getItem();

        // inflate the view
        LayoutInflater inflater = LayoutInflater.from(mContext);
        view = inflater.inflate(mResource, parent,false);

        // retrieve the display items
        add = view.findViewById(R.id.recommendation_event_add);
        remove = view.findViewById(R.id.recommendation_event_remove);
        circle = view.findViewById(R.id.recommendation_event_circle);
        itemView = view.findViewById(R.id.recommendation_event_item);
        amountView = view.findViewById(R.id.recommendation_event_amount);

        // set the values for the view
        itemView.setText(item);
        updateAmountDisplay(item,amount,amountView);

        // listeners
        addItemListener(item,amount,amountView);
        removeItemListener(item,amount,amountView);
        updateCircleDisplay(item, circle);

        return view;
    }

    private void updateCircleDisplay(String item, final ImageView c) {
        refRecommendation.child(item).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int amount = ((Long) dataSnapshot.child("brought").getValue()).intValue();
                int total = ((Long) dataSnapshot.child("total").getValue()).intValue();

                if (amount == total) {
                    c.setImageResource(R.drawable.ic_circle_green);
                } else if (amount < total) {
                    c.setImageResource(R.drawable.ic_circle_grey);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateAmountDisplay(final String item, final int total, final TextView amountView) {
        refBringing.child(item).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int amount = 0;

                if (dataSnapshot.getValue() != null) {
                    amount = ((Long) dataSnapshot.getValue()).intValue();
                }

                amountView.setText(String.valueOf(amount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void removeItemListener(final String item, final int total, final TextView amountView) {
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { updateEventAmount(item,total,amountView,false);
            }
        });
    }

    private void addItemListener(final String item, final int total, final TextView amountView) {
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { updateEventAmount(item,total,amountView,true);
            }
        });
    }

    private void updateEventAmount(final String item, final int total, final TextView amountView, final boolean add) {
        refRecommendation.child(item).child("brought").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int amount = ((Long) dataSnapshot.getValue()).intValue();

                updateUserAmount(item,total-amount,amountView,add);

                if (amount < total && add) {
                    amount++;
                }

                if (amount > 0 && !add) {
                    amount--;
                }

                refRecommendation.child(item).child("brought").setValue(amount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateUserAmount(final String item, final int total, final TextView amountView, final boolean add) {
        refBringing.child(item).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int amount = 0;

                if (dataSnapshot.getValue() != null) {
                    amount = ((Long) dataSnapshot.getValue()).intValue();
                }

                if (total > 0 && add) {
                    amount++;
                }

                if (amount > 0 && !add) {
                    amount--;
                }

                refBringing.child(item).setValue(amount);
                amountView.setText(String.valueOf(amount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
