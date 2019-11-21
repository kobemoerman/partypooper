package com.application.android.partypooper.Fragment;

import android.app.Activity;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

    /** Number of items to be displayed in the recycler view */
    private int LIMIT = 20;

    /** Reference to the Home Activity */
    private HomeActivity act;

    /** Edit Text to search users */
    private EditText searchBar;

    /** Informs the user when no users are available */
    private TextView available;

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
        recyclerScrollListener();
        usersQueryDatabase();
        hideKeyboardListener(searchBar);

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

        available = view.findViewById(R.id.frag_search_no_users);
        searchBar = view.findViewById(R.id.frag_search_search_bar);
        searchBar.requestFocus();

        mAdapter = new SearchAdapter(getContext(),new ArrayList<User>());
        recyclerView.setAdapter(mAdapter);
    }

    /**
     * Increases the value of LIMIT once the bottom of the view has been reached.
     */
    private void recyclerScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                // The user cannot scroll down anymore
                if (!recyclerView.canScrollVertically(1)) {
                    LIMIT+=20;
                }
            }
        });
    }

    /**
     * Populate the Recycler View depending on the query.
     */
    private void usersQueryDatabase() {
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) {
                    Query custom = act.getUsersCustom(s.toString());
                    displayUsers(custom);
                } else {
                    mAdapter.clear();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * Displays LIMIT users with the help of the adapter.
     * @param mQuery populates the user list
     */
    private void displayUsers (Query mQuery) {
        mQuery.limitToFirst(LIMIT).addValueEventListener(new ValueEventListener() {
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

                if (mAdapter.getItemCount() > 0) available.setVisibility(View.INVISIBLE);
                else available.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Calls @hideKeyboard when the users touches outside the edit text.
     */
    private void hideKeyboardListener(EditText editText) {
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
    }

    /**
     * Hides an open keyboard.
     * @param view of the activity
     */
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
