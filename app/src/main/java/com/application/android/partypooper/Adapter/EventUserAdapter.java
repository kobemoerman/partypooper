package com.application.android.partypooper.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.android.partypooper.Model.User;
import com.application.android.partypooper.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class EventUserAdapter extends RecyclerView.Adapter<EventUserAdapter.EventViewHolder> {
		
	private Context mContext;
	private List<User> mUsers;
		
	private FirebaseUser firebaseUser;

	private onItemClickListener mListener;
		
	public EventUserAdapter(Context mContext, List<User> mUsers) {
		this.mContext = mContext;
		this.mUsers = mUsers;
	}

	public interface onItemClickListener {
	    void onItemClick(int pos);
    }

    public void setOnItemClickListener (onItemClickListener listener) {
        mListener = listener;
    }
		
	@NonNull
	@Override
	public EventViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
		View view = LayoutInflater.from(mContext).inflate(R.layout.user_event, viewGroup, false);

		firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

		return new EventViewHolder(view, mListener);
	}

	@Override
	public void onBindViewHolder(@NonNull EventViewHolder eventViewHolder, int i) {

		final User user = mUsers.get(i);

		eventViewHolder.username.setText(user.getUsername());
		if (user.getImgURL() != null) {
			Glide.with(eventViewHolder.icon.getContext()).load(user.getImgURL()).into(eventViewHolder.icon);
		} else {
			Glide.with(eventViewHolder.icon.getContext()).load(R.drawable.logo).into(eventViewHolder.icon);
		}

	}

	@Override
	public int getItemCount() {
		return mUsers.size();
	}

	public class EventViewHolder extends RecyclerView.ViewHolder {
				
		public TextView username;
		public ImageView check;
		public CircleImageView icon;
				
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
	}
}