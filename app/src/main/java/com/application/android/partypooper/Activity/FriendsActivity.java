package com.application.android.partypooper.Activity;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.application.android.partypooper.Adapter.EventAdapter;
import com.application.android.partypooper.Adapter.FriendsAdapter;
import com.application.android.partypooper.Model.User;
import com.application.android.partypooper.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FriendsActivity extends PortraitActivity {

    /** Number of items to be displayed in the recycler view */
    private int LIMIT = 20;

    /** List of all the user's friends */
    private List<String> mFriends;

    /** Display the number of friends the user has */
    private TextView count;

    /** Search bar to customise query */
    private EditText searchBar;

    /** Recycler View to display friends */
    private RecyclerView recyclerView;

    /** Firebase authentication */
    private FirebaseAuth mAuth;

    /** Firebase current user */
    private FirebaseUser mUser;

    /** Firebase reference to all users */
    private DatabaseReference refUsers;

    /** Firebase reference to all friends of the user */
    private DatabaseReference refFriends;

    /** Adapter to display data in the recycler view */
    private FriendsAdapter mAdapter;

    /**
     * On create method of the activity.
     * @param savedInstanceState this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        initView();
        recyclerScrollListener();
        friendUsersQueryDatabase();
        hideKeyboardListener(searchBar);
    }

    /**
     * Initialises the activity view and Firebase references.
     */
    private void initView() {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        refUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        refFriends = FirebaseDatabase.getInstance().getReference().child("Friends").child(mUser.getUid());

        searchBar = findViewById(R.id.friends_search_bar);
        count = findViewById(R.id.friends_count);

        recyclerView = findViewById(R.id.friends_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mFriends = new ArrayList<>();
        mAdapter = new FriendsAdapter(this, new ArrayList<User>());
        recyclerView.setAdapter(mAdapter);
    }

    /**
     * Listener to close the activity.
     * @param view of the activity
     */
    public void onClickBackFriends(View view){
        finish();
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
    private void friendUsersQueryDatabase() {
        displayFriends(refFriends);

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence string, int start, int before, int count) {
                String s = string.toString();
                Query custom = refFriends.orderByValue().startAt(s).endAt(s+"\uf8ff");
                displayFriends(custom);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * Gets all friends of the user to be displayed.
     * @param ref query to retrieve the list of friends
     */
    private void displayFriends(Query ref) {
        ref.limitToFirst(LIMIT).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long friends = dataSnapshot.getChildrenCount();
                count.setText(String.format("Showing %s friends", String.valueOf(friends)));

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
        InputMethodManager inputMethodManager =(InputMethodManager)this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
