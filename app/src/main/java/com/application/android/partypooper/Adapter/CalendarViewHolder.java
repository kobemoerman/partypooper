package com.application.android.partypooper.Adapter;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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

    /**
     * Initialises the SearchViewHolder.
     * @param itemView item_search
     */
    public CalendarViewHolder(@NonNull View itemView) {
        super(itemView);

        initView();
    }

    /**
     * Initialises the items of item_calendar
     */
    private void initView() {
        image = itemView.findViewById(R.id.item_calendar_image);
        name = itemView.findViewById(R.id.item_calendar_name);
        host = itemView.findViewById(R.id.item_calendar_host);
        time = itemView.findViewById(R.id.item_calendar_time);
        next = itemView.findViewById(R.id.item_calendar_next);
    }

    /**
     * Opens the selected event.
     * @param id to the event
     */
    private void openEventListener(String id) {
        //TODO: open event activity.
    }

    @Override
    public void onBind(Object item) {
        final Event t = (Event) item;

        name.setText(t.getName());
        host.setText(t.getHost() + " has invited you");
        time.setText(t.getTime());

        if (t.getImageURL() != null) {
            Glide.with(image.getContext()).load(t.getImageURL()).into(image);
        } else {
            Glide.with(image.getContext()).load(R.drawable.logo).into(image);
        }

        openEventListener(t.getTime_stamp());
    }
}
