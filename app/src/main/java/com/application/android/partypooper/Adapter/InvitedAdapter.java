package com.application.android.partypooper.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.application.android.partypooper.Model.User;
import com.application.android.partypooper.R;

import java.util.List;

public class InvitedAdapter extends RecyclerAdapter<User, InvitedViewHolder> {
    /**
     * Base constructor.
     *
     * @param context Context needed to retrieve LayoutInflater
     */
    public InvitedAdapter(Context context, List<User> mUsers) {
        super(context);
        setItems(mUsers);
    }

    @NonNull
    @Override
    public InvitedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new InvitedViewHolder(inflate(R.layout.item_invited, parent));
    }
}
