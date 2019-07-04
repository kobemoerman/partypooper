package com.application.android.partypooper.Adapter;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.android.partypooper.Adapter.CalendarAdapter.onItemClickListener;
import com.application.android.partypooper.Model.Event;
import com.application.android.partypooper.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CalendarViewHolder extends RecyclerAdapter.ViewHolder {

    /** Image used for the event */
    private ImageView image;

    /** Opens the selected event */
    private ImageView next;

    /** Information about the event */
    private TextView name, host, time;

    /** Firebase authentication */
    private FirebaseAuth mAuth;

    /** Firebase user */
    private FirebaseUser mUser;

    /** Item click listener */
    private onItemClickListener mListener;

    /**
     * Initialises the SearchViewHolder.
     * @param itemView item_search
     */
    public CalendarViewHolder(@NonNull View itemView, final onItemClickListener listener) {
        super(itemView);

        initView(listener);
        itemOnClickListener(itemView);
    }

    /**
     * Initialises the items of item_calendar
     * @param listener on item listener
     */
    private void initView(onItemClickListener listener) {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mListener = listener;

        image = itemView.findViewById(R.id.item_calendar_image);
        name = itemView.findViewById(R.id.item_calendar_name);
        host = itemView.findViewById(R.id.item_calendar_host);
        time = itemView.findViewById(R.id.item_calendar_time);
        next = itemView.findViewById(R.id.item_calendar_next);
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
     * Updates the data in the recycler view
     * @param item object, associated with the item.
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void onBind(Object item) {
        String owner;
        final Event e = (Event) item;

        name.setText(e.getName());

        if (e.getHost().equals(mUser.getUid())) owner = "by You";
        else owner = "by " + e.getHost_username();

        host.setText(owner);
        time.setText(e.getTime());

        if (e.getImageURL() != null) {
            Glide.with(image.getContext()).load(e.getImageURL()).into(image);
        } else {
            Glide.with(image.getContext()).load(R.drawable.logo).into(image);
        }
    }
}
