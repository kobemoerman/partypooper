package com.application.android.partypooper.Adapter;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.application.android.partypooper.Model.User;
import com.application.android.partypooper.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import de.hdodenhof.circleimageview.CircleImageView;

public class SearchViewHolder extends RecyclerAdapter.ViewHolder {

    private Button follow;
    private TextView username;
    private TextView status;
    private CircleImageView icon;

    private FirebaseUser mUser;

    public SearchViewHolder(@NonNull View itemView) {
        super(itemView);

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        username = itemView.findViewById(R.id.search_username);
        status = itemView.findViewById(R.id.statusUser);
        icon = itemView.findViewById(R.id.img_profile);
        follow = itemView.findViewById(R.id.followButton);
    }

    @Override
    public void onBind(final User u) {
        follow.setVisibility(View.VISIBLE);

        username.setText(u.getUsername());
        status.setText(u.getStatus());

        if (u.getImgURL() != null) {
            Glide.with(icon.getContext()).load(u.getImgURL()).into(icon);
        } else {
            Glide.with(icon.getContext()).load(R.drawable.logo).into(icon);
        }

        isFriends(u.getId(),follow);

        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (follow.getText().toString().equals("Add")) {
                    FirebaseDatabase.getInstance().getReference().child("Friends").child(mUser.getUid())
                            .child(u.getId()).setValue(u.getUsername());
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Friends").child(mUser.getUid())
                            .child(u.getId()).removeValue();
                }
            }
        });
    }

    private void isFriends(final String userID, final Button button) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Friends")
                .child(mUser.getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(userID).exists()) {
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
}