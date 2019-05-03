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
import com.application.android.partypooper.Adapter.SearchAdapter;
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
    private SearchAdapter mAdapter;

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
        usersQueryDatabase();

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
        qUsers = act.getqUsers();

        recyclerView = view.findViewById(R.id.frag_search_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        searchBar = view.findViewById(R.id.frag_search_search_bar);

        mAdapter = new SearchAdapter(getContext(),new ArrayList<User>());
        recyclerView.setAdapter(mAdapter);
    }

    /**
     * Populate the Recycler View depending on the query.
     */
    private void usersQueryDatabase() {
        displayUsers(qUsers);

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Query custom = act.getUsersCustom(s.toString());
                displayUsers(custom);
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
                mAdapter.clear();
                for (DataSnapshot snp : dataSnapshot.getChildren()) {
                    User user = snp.getValue(User.class);

                    assert user != null;
                    if (!user.getId().equals(mUser.getUid())) {
                        mAdapter.add(user);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
