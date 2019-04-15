package com.application.android.partypooper.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.android.partypooper.Activity.HomeActivity;
import com.application.android.partypooper.Model.User;
import com.application.android.partypooper.R;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class MenuFragment extends Fragment {

    /** Reference to the Home Activity */
    private HomeActivity act;

    /** Image view for the user profile picture */
    private ImageView userImage;

    /** Text view items containing user info */
    private TextView userUsername, userStatus, userFriends, userAge;

    /** Text view items on click listeners */
    private TextView bFriends, bEvents, bSettings, bLogOut;

    /** Firebase reference to user info */
    private DatabaseReference refUserInfo;

    /** Firebase reference to user friends */
    private DatabaseReference refFriendsCount;

    /**
     * On create method of the fragment.
     * @param inflater inflate any views in the fragment
     * @param container parent view that the fragment's UI should be attached to
     * @param savedInstanceState this fragment is being re-constructed from a previous saved state as given here
     * @return Return the View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        initView(view);

        userDataBaseInfo();
        userFriendsCountInfo();

        return view;
    }

    /**
     * Initialises the fragment view.
     * @param view fragment_menu
     */
    private void initView(View view) {
        act = (HomeActivity) getActivity();
        assert act != null;
        refFriendsCount = act.getRefUserFriends();
        refUserInfo = act.getRefUserInfo();

        userImage = view.findViewById(R.id.frag_menu_user_image);
        userUsername = view.findViewById(R.id.frag_menu_user_username);
        userStatus = view.findViewById(R.id.frag_menu_user_status);
        userFriends = view.findViewById(R.id.frag_menu_user_friends);
        userAge = view.findViewById(R.id.frag_menu_user_age);
        bFriends = view.findViewById(R.id.frag_menu_friends);
        bEvents = view.findViewById(R.id.frag_menu_events);
        bSettings = view.findViewById(R.id.frag_menu_settings);
        bLogOut = view.findViewById(R.id.frag_menu_log_out);
    }

    /**
     * Updates userFriends view to the user friend count
     */
    private void userFriendsCountInfo() {
        refFriendsCount.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long friends = dataSnapshot.getChildrenCount();
                userFriends.setText(String.valueOf(friends));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Updates userUsername, userStatus, userAge to the user data
     */
    private void userDataBaseInfo() {
        refUserInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                userUsername.setText(user.getUsername());
                userStatus.setText(String.format("\"%s\"", user.getStatus()));
                userAge.setText(getAge(user.getAge()));
                if (user.getImgURL() != null) Glide.with(userImage.getContext()).load(user.getImgURL()).into(userImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Returns the int age from a given string date
     * @param str (string) date
     * @return (int) age
     */
    private String getAge(String str){
        String date[] = str.split("/");

        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        int year = Integer.parseInt(date[2]);
        int month = Integer.parseInt(date[1]);
        int day = Integer.parseInt(date[0]);

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        return Integer.toString(age);
    }
}
