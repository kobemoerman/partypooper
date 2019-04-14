package com.application.android.partypooper.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.application.android.partypooper.Activity.HomeActivity;
import com.application.android.partypooper.Adapter.SearchUserAdapter;
import com.application.android.partypooper.Model.User;
import com.application.android.partypooper.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    /** List of all users to display */
    private List<User> userList;

    /** Reference to the Home Activity */
    private HomeActivity act;

    /** Edit Text to search users */
    private EditText searchBar;

    /** Recycler View to display users */
    private RecyclerView recyclerView;

    /** Query to all users in the database */
    private Query qUsers;

    /** Firebase current user */
    private FirebaseUser mUser;

    /** Adapter to display data in the recycler view */
    private SearchUserAdapter mAdapter;

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
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        initView(view);
        friendsQueryDatabase();

        return view;
    }

    /**
     * Initialises the fragment view.
     * @param view fragment_search
     */
    private void initView(View view) {
        act = (HomeActivity) getActivity();
        mUser = act.getmUser();
        qUsers = act.getqUsers();

        recyclerView = view.findViewById(R.id.frag_search_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        searchBar = view.findViewById(R.id.frag_search_search_bar);

        userList = new ArrayList<>();
        mAdapter = new SearchUserAdapter(getContext(), userList);
        recyclerView.setAdapter(mAdapter);
    }

    /**
     * Displays the users depending on the query.
     */
    private void friendsQueryDatabase() {
        displayUsers(qUsers);

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Query qCustom = act.getUsersCustom(s.toString());
                displayUsers(qCustom);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    /**
     * Displays the users with the help of the adapter.
     * @param mQuery populates the user list
     */
    private void displayUsers (Query mQuery) {
        mQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snp : dataSnapshot.getChildren()) {
                    User user = snp.getValue(User.class);

                    assert user != null;
                    if (!user.getId().equals(mUser.getUid())) {
                        userList.add(user);
                    }
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
