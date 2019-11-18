package com.application.android.partypooper.Adapter;

import android.support.annotation.NonNull;
import android.view.View;

import com.application.android.partypooper.Model.User;
import com.application.android.partypooper.R;
import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;

public class InvitedViewHolder extends RecyclerAdapter.ViewHolder {

    /** Profile picture of the user */
    private CircleImageView icon;

    /**
     * Initialises the EventViewHolder.
     * @param itemView item_friends
     */InvitedViewHolder(@NonNull View itemView) {
        super(itemView);

        initView();
    }

    /**
     * Initialises the items of item_event
     */
    private void initView() {
        icon = itemView.findViewById(R.id.icon_invited_icon);
    }

    @Override
    public void onBind(Object item) {
        final User u = (User) item;

        if (u.getImgURL() != null) {
            Glide.with(icon.getContext()).load(u.getImgURL()).into(icon);
        } else {
            Glide.with(icon.getContext()).load(R.drawable.default_logo).into(icon);
        }
    }
}
