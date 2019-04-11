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

import com.application.android.partypooper.Adapter.SearchUserAdapter;
import com.application.android.partypooper.Model.User;
import com.application.android.partypooper.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private EditText searchBar;

    private FirebaseUser firebaseUser;
    private RecyclerView recyclerView;
    private SearchUserAdapter searchUserAdapter;
    private List<User> mUsers;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        recyclerView = view.findViewById(R.id.frag_search_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        searchBar = view.findViewById(R.id.frag_search_search_bar);

        mUsers = new ArrayList<>();
        searchUserAdapter = new SearchUserAdapter(getContext(),mUsers);
        recyclerView.setAdapter(searchUserAdapter);

        friendsQueryDatabase();

        return view;
    }

    private void friendsQueryDatabase() {
        displayUsers(searchQuery(false,""));

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                displayUsers(searchQuery(true,s.toString().toLowerCase()));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void displayUsers (Query mQuery) {
        mQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snp : dataSnapshot.getChildren()) {
                    User user = snp.getValue(User.class);

                    assert user != null;
                    if (!user.getId().equals(firebaseUser.getUid())) {
                        mUsers.add(user);
                    }
                }
                searchUserAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private Query searchQuery (Boolean onTextChange, String s) {
        if (onTextChange) {
            return FirebaseDatabase.getInstance().getReference("Users").orderByChild("username")
                    .startAt(s).endAt(s + "\uf8ff");
        } else {
            return FirebaseDatabase.getInstance().getReference("Users").orderByChild("username");
        }
    }
}
