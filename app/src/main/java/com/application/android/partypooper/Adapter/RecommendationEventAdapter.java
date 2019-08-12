package com.application.android.partypooper.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    private TextView userAmount, totalAmount;

    /** Turns green when the total of items has been reached */
    private LinearLayout layout;

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
        userAmount = view.findViewById(R.id.recommendation_event_bring);
        totalAmount = view.findViewById(R.id.recommendation_event_total);
        itemView = view.findViewById(R.id.recommendation_event_item);
        layout = view.findViewById(R.id.recommendation_event_amount);


        // set the values for the view
        itemView.setText(item);
        updateUserAmount(item,userAmount);
        updateTotalAmount(item,totalAmount);
        updateLayoutDisplay(item,userAmount);

        // listeners
        addItemListener(item,amount,userAmount);
        removeItemListener(item,amount,userAmount);

        return view;
    }

    private void updateLayoutDisplay(String item, final TextView c) {
        refRecommendation.child(item).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int amount = ((Long) dataSnapshot.child("brought").getValue()).intValue();
                int total = ((Long) dataSnapshot.child("total").getValue()).intValue();

                if (amount == total) {
                    System.out.println("THEY'RE EQUAL YO LOOK " +amount + ":"+total);
                    c.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                } else if (amount < total) {
                    c.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateTotalAmount(final String item, final TextView amountView) {
        refRecommendation.child(item).child("total").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int amount = ((Long) Objects.requireNonNull(dataSnapshot.getValue())).intValue();

                amountView.setText(String.valueOf(amount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateUserAmount(final String item, final TextView amountView) {
        refBringing.child(item).addValueEventListener(new ValueEventListener() {
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
            public void onClick(View v) {
                updateUserRecommendation(item,total,amountView,false);
            }
        });
    }

    private void addItemListener(final String item, final int total, final TextView amountView) {
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserRecommendation(item,total,amountView,true);
            }
        });
    }

    private void updateUserRecommendation(final String item, final int total, final TextView amountView, final boolean add) {
        refBringing.child(item).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int amount = 0;

                if (dataSnapshot.getValue() != null) {
                    amount = ((Long) dataSnapshot.getValue()).intValue();
                }

                updateEventRecommendation(item,amount,total,amountView,add);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateEventRecommendation(final String item, final int itemAmount, final int total, final TextView amountView, final boolean add) {
        refRecommendation.child(item).child("brought").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int amount = ((Long) dataSnapshot.getValue()).intValue();

                System.out.println("Brought " + amount + " User amount " + itemAmount + " Total " + total);

                if (amount < total && add) {
                    amount++;
                    refBringing.child(item).setValue(itemAmount+1);
                    refRecommendation.child(item).child("brought").setValue(amount);
                    amountView.setText(String.valueOf(itemAmount+1));
                }

                if (amount > 0 && !add && itemAmount != 0) {
                    amount--;
                    refBringing.child(item).setValue(itemAmount-1);
                    refRecommendation.child(item).child("brought").setValue(amount);
                    amountView.setText(String.valueOf(itemAmount-1));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
