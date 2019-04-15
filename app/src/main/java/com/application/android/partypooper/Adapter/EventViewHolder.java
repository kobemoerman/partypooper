package com.application.android.partypooper.Adapter;

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

public class EventViewHolder extends RecyclerAdapter.ViewHolder {

    private ImageView check;
    private TextView username;
    private CircleImageView icon;

    private int mPosition;
    private onItemClickListener mListener;

    /**
     * Initialises the EventViewHolder.
     * @param itemView user_event
     * @param listener on item listener
     */
    public EventViewHolder(@NonNull View itemView, final onItemClickListener listener) {
        super(itemView);

        initView(listener);
        itemOnClickListener();
    }

    /**
     * Initialises the items of user_event
     * @param listener on item listener
     */
    private void initView(onItemClickListener listener) {
        mListener = listener;
        mPosition = getAdapterPosition();

        icon = itemView.findViewById(R.id.user_event_image);
        check = itemView.findViewById(R.id.user_event_check);
        username = itemView.findViewById(R.id.user_event_name);
    }

    /**
     * Action on the item listener.
     */
    private void itemOnClickListener() {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPosition != RecyclerView.NO_POSITION) {
                    check.setBackgroundResource(R.drawable.ic_check_circle);
                    mListener.onItemClick(mPosition);
                }
            }
        });
    }

    /**
     * Updates the data in the recycler view
     * @param item object, associated with the item.
     */
    @Override
    public void onBind(Object item) {
        User u = (User) item;

        username.setText(u.getUsername());
        if (u.getImgURL() != null) {
            Glide.with(icon.getContext()).load(u.getImgURL()).into(icon);
        } else {
            Glide.with(icon.getContext()).load(R.drawable.logo).into(icon);
        }
    }
}
