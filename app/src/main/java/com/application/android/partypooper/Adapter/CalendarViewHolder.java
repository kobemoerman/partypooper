package com.application.android.partypooper.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.android.partypooper.Adapter.CalendarAdapter.onItemClickListener;
import com.application.android.partypooper.Model.Event;
import com.application.android.partypooper.R;
import com.bumptech.glide.Glide;

public class CalendarViewHolder extends RecyclerAdapter.ViewHolder {

    /** Image used for the event */
    private ImageView image;

    /** Opens the selected event */
    private ImageView next;

    /** Information about the event */
    private TextView name, host, time;

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
     * Opens the selected event.
     * @param time_stamp of the event
     * @param host of the event
     */
    private void openEventListener(String time_stamp, String host) {
        //TODO: open event activity.
    }

    @Override
    public void onBind(Object item) {
        final Event t = (Event) item;

        name.setText(t.getName());
        host.setText(t.getHost_username() + " has invited you");
        time.setText(t.getTime());

        if (t.getImageURL() != null) {
            Glide.with(image.getContext()).load(t.getImageURL()).into(image);
        } else {
            Glide.with(image.getContext()).load(R.drawable.logo).into(image);
        }

        openEventListener(t.getTime_stamp(),t.getHost());
    }
}
