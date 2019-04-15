package com.application.android.partypooper.Adapter;

import android.content.Context;
import android.view.ViewGroup;
import com.application.android.partypooper.Model.User;
import com.application.android.partypooper.R;

import java.util.List;

public class SearchAdapter extends RecyclerAdapter<User, SearchViewHolder> {

    /**
     * Base constructor.
     * Allocate adapter-related objects here if needed.
     *
     * @param context Context needed to retrieve LayoutInflater
     */
    public SearchAdapter(Context context, List<User> mUsers) {
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
    public SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SearchViewHolder(inflate(R.layout.user_search,parent));
    }
}
