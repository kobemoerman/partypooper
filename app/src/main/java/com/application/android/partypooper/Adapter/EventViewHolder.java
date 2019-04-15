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

    private TextView username;
    private ImageView check;
    private CircleImageView icon;

    private onItemClickListener mListener;

    public EventViewHolder(@NonNull View itemView, final onItemClickListener listner) {
        super(itemView);
        username = itemView.findViewById(R.id.user_event_name);
        icon = itemView.findViewById(R.id.user_event_image);
        check = itemView.findViewById(R.id.user_event_check);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    check.setBackgroundResource(R.drawable.ic_check_circle);
                    listner.onItemClick(position);
                }
            }
        });
    }

    @Override
    public void onBind(User u) {
        username.setText(u.getUsername());
        if (u.getImgURL() != null) {
            Glide.with(icon.getContext()).load(u.getImgURL()).into(icon);
        } else {
            Glide.with(icon.getContext()).load(R.drawable.logo).into(icon);
        }
    }
}
