package com.application.android.partypooper.Adapter;

import android.view.View;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.application.android.partypooper.Adapter.FriendsAdapter.onItemClickListener;
import com.application.android.partypooper.Model.User;
import com.application.android.partypooper.R;
import com.bumptech.glide.Glide;

import java.util.Calendar;

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

        username.setText(isUserBirthday(u));
        status.setText(u.getStatus());

        if (u.getImgURL() != null) {
            Glide.with(icon.getContext()).load(u.getImgURL()).into(icon);
        } else {
            Glide.with(icon.getContext()).load(R.drawable.default_logo).into(icon);
        }
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
