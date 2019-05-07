package com.application.android.partypooper.Adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.application.android.partypooper.Model.Event;
import com.application.android.partypooper.R;

import java.util.List;

public class CalendarAdapter extends RecyclerAdapter<Event, CalendarViewHolder> {

    /**
     * Base constructor.
     * Allocate adapter-related objects here if needed.
     *
     * @param context Context needed to retrieve LayoutInflater
     */
    public CalendarAdapter(Context context, List<Event> mUsers) {
        super(context);
        setItems(mUsers);
    }

    /**
     * To be implemented in as specific adapter
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    @Override
    public CalendarViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CalendarViewHolder(inflate(R.layout.item_calendar,parent));
    }
}
