package com.application.android.partypooper.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.application.android.partypooper.Activity.HomeActivity;
import com.application.android.partypooper.Adapter.InvitedAdapter;
import com.application.android.partypooper.Model.User;
import com.application.android.partypooper.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchDefaultFragment extends Fragment {

    /** Reference to the Home Activity */
    private HomeActivity act;

    /** List of all the user's friends */
    private List<String> mFriends;

    /** Edit Text to search users */
    private EditText searchBar;

    /** Image view for the user profile picture and logo */
    private ImageView userImage;

    /** Share the application */
    private LinearLayout shareLayout;

    /** Recycler View to display friends */
    private RecyclerView recyclerView;

    /** Adapter to display data in the recycler view */
    private InvitedAdapter mAdapter;

    /** Firebase current user */
    private FirebaseUser mUser;

    /** Firebase reference to all users */
    private DatabaseReference refUsers;

    /** Firebase reference to user info */
    private DatabaseReference refUserInfo;

    /** Firebase reference to all friends of the user */
    private DatabaseReference refFriends;

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
        View view = inflater.inflate(R.layout.fragment_search_default, container, false);

        initView(view);
        displayFriends();
        updateUserData();
        searchBarListener();
        shareLayoutListener();

        return view;
    }

    /**
     * Initialises the fragment view.
     * @param view fragment_search
     */
    private void initView(View view) {
        act = (HomeActivity) getActivity();
        assert act != null;
        mUser = act.getmUser();
        refUserInfo = act.getRefUserInfo();

        refUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        refFriends = FirebaseDatabase.getInstance().getReference().child("Friends").child(mUser.getUid());

        userImage = view.findViewById(R.id.frag_search_default_user_image);
        searchBar = view.findViewById(R.id.frag_search_default_bar);
        shareLayout = view.findViewById(R.id.frag_search_default_share);

        recyclerView = view.findViewById(R.id.frag_search_default_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        mFriends = new ArrayList<>();
        mAdapter = new InvitedAdapter(getContext(), new ArrayList<User>());
        recyclerView.setAdapter(mAdapter);
    }

    private void searchBarListener() {
        searchBar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    act.updateFragment(new SearchFragment());
                }
            }
        });
    }

    private void shareLayoutListener() {
        shareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "PartyPooper Application");
                String message = "\nLet me recommend you this application *Your app link* \n\n";

                i.putExtra(Intent.EXTRA_TEXT, message);
                startActivity(Intent.createChooser(i, "Share with:"));
            }
        });
    }

    private void updateUserData() {
        refUserInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                if (user.getImgURL() != null) Glide.with(userImage.getContext()).load(user.getImgURL()).into(userImage);
                else Glide.with(userImage.getContext()).load(R.drawable.default_logo).into(userImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Gets all friends of the user to be displayed.
     */
    private void displayFriends() {
        refFriends.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mFriends.clear();
                for (DataSnapshot snp : dataSnapshot.getChildren()) {
                    mFriends.add(snp.getKey());
                }
                showUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    /**
     * Displays the friends with the help of the adapter.
     */
    private void showUsers() {
        Query users = refUsers.orderByChild("username");
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mAdapter.clear();
                for (DataSnapshot snp : dataSnapshot.getChildren()) {
                    User user = snp.getValue(User.class);

                    for (String id : mFriends) {
                        if (user.getId().equals(id)) {
                            mAdapter.add(user);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
