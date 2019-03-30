package com.application.android.partypooper.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.application.android.partypooper.Model.User;
import com.application.android.partypooper.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUsers;

    private FirebaseUser firebaseUser;

    public UserAdapter(Context mContext, List<User> mUsers) {
        this.mContext = mContext;
        this.mUsers = mUsers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, viewGroup,false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final User user = mUsers.get(i);

        viewHolder.followBtn.setVisibility(View.VISIBLE);
        viewHolder.username.setText(user.getUsername());
        viewHolder.status.setText(user.getStatus());
        if (user.getImgURL() != null) Glide.with(viewHolder.icon.getContext()).load(user.getImgURL()).into(viewHolder.icon);

        isFriends(user.getId(), viewHolder.followBtn);

        viewHolder.followBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolder.followBtn.getText().toString().equals("Add")) {
                    FirebaseDatabase.getInstance().getReference().child("Friends").child(firebaseUser.getUid())
                            .child(user.getId()).setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Friends").child(firebaseUser.getUid())
                            .child(user.getId()).removeValue();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    private void isFriends(final String userid, final Button button) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Friends")
                .child(firebaseUser.getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(userid).exists()) {
                    button.setText("Friends");
                    button.setBackgroundResource(R.drawable.border_light);
                    button.setTextColor(Color.BLACK);
                } else {
                    button.setText("Add");
                    button.setBackgroundResource(R.drawable.button_background_green);
                    button.setTextColor(Color.WHITE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username;
        public TextView status;
        public CircleImageView icon;
        public Button followBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.usernameUser);
            status = itemView.findViewById(R.id.statusUser);
            icon = itemView.findViewById(R.id.img_profile);
            followBtn = itemView.findViewById(R.id.followButton);
        }
    }
}
