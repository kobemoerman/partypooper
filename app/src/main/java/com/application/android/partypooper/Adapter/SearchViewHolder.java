package com.application.android.partypooper.Adapter;

import android.annotation.SuppressLint;
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

import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchViewHolder extends RecyclerAdapter.ViewHolder {

    /** Adds or removes selected user from Friends node */
    private Button follow;

    /** Username of the user */
    private TextView username;

    /** Status of the user */
    private TextView status;

    /** Profile picture of the user */
    private CircleImageView icon;

    /** Firebase user */
    private FirebaseUser mUser;

    /** Firebase reference to user friends */
    private DatabaseReference refFriends;

    /**
     * Initialises the SearchViewHolder.
     * @param itemView item_search
     */
    public SearchViewHolder(@NonNull View itemView) {
        super(itemView);

        initView();
    }

    /**
     * Initialises the items of item_search and Firebase.
     */
    private void initView() {
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        refFriends = FirebaseDatabase.getInstance().getReference().child("Friends").child(mUser.getUid());

        icon = itemView.findViewById(R.id.item_search_image);
        status = itemView.findViewById(R.id.item_search_status);
        follow = itemView.findViewById(R.id.item_search_button);
        username = itemView.findViewById(R.id.item_search_name);

        follow.setVisibility(View.VISIBLE);
    }

    /**
     * Adds or removes user to friends database list.
     * @param id user to add/remove
     */
    private void followItemListener(final String id, final User user) {
        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (follow.getText().toString().equals("Add")) {
                refFriends.child(id).setValue(user.getUsername());
            } else {
                refFriends.child(id).removeValue();
            }
            }
        });
    }

    /**
     * Updates the button value depending on the user's friends.
     * @param id user in question
     */
    private void isFriends(final String id) {
        refFriends.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(id).exists()) {
                    follow.setText("Friends");
                    follow.setBackgroundResource(R.drawable.border_light);
                    follow.setTextColor(Color.BLACK);
                } else {
                    follow.setText("Add");
                    follow.setBackgroundResource(R.drawable.button_background_green);
                    follow.setTextColor(Color.WHITE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Updates the data in the recycler view
     * @param item object, associated with the item.
     */
    @Override
    public void onBind(Object item) {
        final User u = (User) item;

        username.setText(isUserBirthday(u));
        status.setText(u.getStatus());

        if (u.getImgURL() != null) {
            Glide.with(icon.getContext()).load(u.getImgURL()).into(icon);
        } else {
            Glide.with(icon.getContext()).load(R.drawable.logo).into(icon);
        }

        isFriends(u.getId());
        followItemListener(u.getId(),u);
    }

    /**
     * Compares the date with the user's birth day.
     *
     * @param u user to compare with
     * @return username to display
     */
    public String isUserBirthday(User u) {
        Calendar cal = Calendar.getInstance();
        int m = cal.get(Calendar.MONTH)+1;
        int d = cal.get(Calendar.DAY_OF_MONTH);

        String day = String.valueOf(d);
        String month = String.valueOf(m);

        if (d < 10) day = "0"+d;
        if (m < 10) month = "0"+m;

        if (u.getAge().substring(0,5).equals(day+"/"+month)) {
            return u.getUsername()+" "+getEmojiByUnicode(0x1F382);
        } else {
            return u.getUsername();
        }
    }

    /**
     * Transforms a unicode into a string.
     *
     * @param unicode to be displayed
     * @return Emoji corresponding to the unicode
     */
    private String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }
}