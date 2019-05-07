package com.application.android.partypooper.Adapter;

import android.content.Context;
import android.view.ViewGroup;
import com.application.android.partypooper.Model.User;
import com.application.android.partypooper.R;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class EventAdapter extends RecyclerAdapter<User, EventViewHolder> {

    private onItemClickListener mListener;

    private final DatabaseReference refMembers;

    /**
     * Base constructor.
     * Allocate adapter-related objects here if needed.
     *
     * @param context Context needed to retrieve LayoutInflater
     */
    public EventAdapter(Context context, List<User> mUsers, DatabaseReference ref) {
        super(context);
        this.refMembers = ref;
        setItems(mUsers);
    }

    public interface onItemClickListener {
        /**
         * On click listener.
         * @param pos item position from the recycler view
         */
        void onItemClick(int pos);
    }

    /**
     * Update the listener from the EventInviteFragment class.
     * @param listener item position
     */
    public void setOnItemClickListener (onItemClickListener listener) {
        mListener = listener;
    }

    /**
     * To be implemented in as specific adapter
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new EventViewHolder(inflate(R.layout.item_event,parent),mListener,refMembers);
    }
}
