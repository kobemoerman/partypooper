package com.application.android.partypooper.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.android.partypooper.Model.User;
import com.application.android.partypooper.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class MenuFragment extends Fragment {

    private ImageView userImg;
    private TextView profUsername, userStatus, userFriends, userAge;
    private Button updateProfileButton, friendsButton, eventsButton, settingsButton, logOutButton;


    private FirebaseUser currentUser;
    private String userID;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        findFragmentElements(view);

        userDataBaseInfo();
        userFriendsCountInfor();

        return view;
    }

    private void userFriendsCountInfor() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Friends").child(currentUser.getUid());
        ref.addValueEventListener(new ValueEventListener() {
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

    private void userDataBaseInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (getContext() == null) {
                    return;
                }

                User user = dataSnapshot.getValue(User.class);

                assert user != null;
                profUsername.setText(user.getUsername());
                userStatus.setText(String.format("\"%s\"", user.getStatus()));
                userAge.setText(getAge(user.getAge()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

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

        int ageInt = age;

        return Integer.toString(ageInt);
    }

    private void findFragmentElements(View view) {
        userImg = view.findViewById(R.id.frag_menu_user_image);
        profUsername = view.findViewById(R.id.frag_menu_user_username);
        userStatus = view.findViewById(R.id.frag_menu_user_status);
        userFriends = view.findViewById(R.id.frag_menu_user_friends);
        userAge = view.findViewById(R.id.frag_menu_user_age);
        updateProfileButton = view.findViewById(R.id.frag_menu_update_profile);
        friendsButton = view.findViewById(R.id.frag_menu_friends);
        eventsButton = view.findViewById(R.id.frag_menu_events);
        settingsButton = view.findViewById(R.id.frag_menu_settings);
        logOutButton = view.findViewById(R.id.frag_menu_log_out);
    }
}
