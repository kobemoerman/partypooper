package com.application.android.partypooper.Adapter;

import android.view.View;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.application.android.partypooper.Adapter.FriendsAdapter.onItemClickListener;
import com.application.android.partypooper.Model.User;
import com.application.android.partypooper.R;
import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsViewHolder extends RecyclerAdapter.ViewHolder {

    /** Username and status of the user */
    private TextView username, status;

    /** Profile picture of the user */
    private CircleImageView icon;

    /** Item click listener */
    private onItemClickListener mListener;

    /**
     * Initialises the EventViewHolder.
     * @param itemView item_friends
     * @param listener on item listener
     */
    FriendsViewHolder(@NonNull View itemView, final onItemClickListener listener) {
        super(itemView);

        initView(listener);
        itemOnClickListener(itemView);
    }

    /**
     * Initialises the items of item_event
     * @param listener on item listener
     */
    private void initView(onItemClickListener listener) {
        mListener = listener;

        icon = itemView.findViewById(R.id.item_friends_image);
        username = itemView.findViewById(R.id.item_friends_name);
        status = itemView.findViewById(R.id.item_friends_status);
    }

    /**
     * Action on the item listener.
     */
    private void itemOnClickListener(View itemView) {
    }

    /**
     * Updates the data in the recycler view
     * @param item object, associated with the item.
     */
    @Override
    public void onBind(Object item) {
        final User u = (User) item;

        username.setText(u.getUsername());
        status.setText(u.getStatus());

        if (u.getImgURL() != null) {
            Glide.with(icon.getContext()).load(u.getImgURL()).into(icon);
        } else {
            Glide.with(icon.getContext()).load(R.drawable.logo).into(icon);
        }
    }
}
