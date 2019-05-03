package com.application.android.partypooper.Adapter;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.application.android.partypooper.Model.User;
import com.application.android.partypooper.R;
import com.bumptech.glide.Glide;
import de.hdodenhof.circleimageview.CircleImageView;
import com.application.android.partypooper.Adapter.EventAdapter.onItemClickListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EventViewHolder extends RecyclerAdapter.ViewHolder {

    private List<String> members;

    /** Determines if  user is selected */
    private ImageView check;

    /** Username of the user */
    private TextView username;

    /** Profile picture of the user */
    private CircleImageView icon;

    /** Item click listener */
    private onItemClickListener mListener;

    /** Reference to the current event in Members  */
    private DatabaseReference refMembers;

    /**
     * Initialises the EventViewHolder.
     * @param itemView user_event
     * @param listener on item listener
     */
    public EventViewHolder(@NonNull View itemView, final onItemClickListener listener, DatabaseReference ref) {
        super(itemView);

        initView(listener, ref);
        itemOnClickListener(itemView);
    }

    /**
     * Initialises the items of user_event
     * @param listener on item listener
     */
    private void initView(onItemClickListener listener, DatabaseReference ref) {
        this.refMembers = ref;

        mListener = listener;

        icon = itemView.findViewById(R.id.user_event_image);
        check = itemView.findViewById(R.id.user_event_check);
        username = itemView.findViewById(R.id.user_event_name);

        members = new ArrayList<>();
    }

    /**
     * Action on the item listener.
     */
    private void itemOnClickListener(View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();

                if (position != RecyclerView.NO_POSITION) {
                    mListener.onItemClick(position);
                }
            }
        });
    }

    /**
     * Updates the button value depending on the user's friends.
     * @param id user making the query
     */
    private void isInvited(final String id) {
        refMembers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(id).exists()) {
                    check.setImageResource(R.drawable.ic_check_circle);
                } else {
                    check.setImageResource(R.drawable.ic_radio_button);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Updates the data in the recycler view
     * @param item object, associated with the item.
     */
    @Override
    public void onBind(Object item) {
        final User u = (User) item;

        username.setText(u.getUsername());
        if (u.getImgURL() != null) {
            Glide.with(icon.getContext()).load(u.getImgURL()).into(icon);
        } else {
            Glide.with(icon.getContext()).load(R.drawable.logo).into(icon);
        }

        isInvited(u.getId());
    }
}
