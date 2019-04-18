package com.application.android.partypooper.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.application.android.partypooper.Activity.CreateEventActivity;
import com.application.android.partypooper.Activity.EventActivity;
import com.application.android.partypooper.Adapter.EventAdapter;
import com.application.android.partypooper.Model.User;
import com.application.android.partypooper.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class InviteFriendsFragment extends Fragment {

	private EditText searchBar;
	private Button back, next;
		
	private FirebaseUser firebaseUser;
	private RecyclerView recyclerView;
	private EventAdapter mAdapter;
	private List<User> mUsers;
	private List<String> mFriends;

  	@Override
  	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	View view = inflater.inflate(R.layout.fragment_invite_friends, container, false);

    	initView(view);

		friendsQueryDatabase();

    	navigationListener();

    	return view;
  	}

    private void initView(View view) {
    	firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    	recyclerView = view.findViewById(R.id.frag_invite_recycler);
    	recyclerView.setHasFixedSize(true);
    	recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    	searchBar = view.findViewById(R.id.frag_invite_search_bar);

    	mUsers = new ArrayList<>();
    	mFriends = new ArrayList<>();
    	mAdapter = new EventAdapter(getContext(),mUsers);
    	recyclerView.setAdapter(mAdapter);

    	mAdapter.setOnItemClickListener(new EventAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                //TODO:get user id from click
            }
        });

    	back = view.findViewById(R.id.frag_invite_back);
    	next = view.findViewById(R.id.frag_invite_next);
  	}

	private void friendsQueryDatabase() {
		Query ref = FirebaseDatabase.getInstance().getReference("Friends").child(firebaseUser.getUid());
		displayFriends(ref);

		searchBar.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				Query custom = FirebaseDatabase.getInstance().getReference("Friends").child(firebaseUser.getUid()).orderByValue()
						.startAt(s.toString().toLowerCase()).endAt(s.toString().toLowerCase() + "\uf8ff");
				displayFriends(custom);
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	private void displayFriends(Query ref) {
		ref.addListenerForSingleValueEvent(new ValueEventListener() {
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

	private void showUsers() {
  		final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
  		Query users = ref.orderByChild("username");
  		users.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				mAdapter.clear();
				for (DataSnapshot snp : dataSnapshot.getChildren()) {
					User user = snp.getValue(User.class);

					for (String id : mFriends) {
						if (user.getId().equals(id)) {
							mAdapter.add(user);
							HashMap event = new HashMap();
							String eventID = ((CreateEventActivity)getActivity()).getTimeStamp() + "?" + firebaseUser.getUid();
							event.put(eventID,false);
							ref.child(user.getId()).child("events").updateChildren(event);
						}
					}
				}
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {

			}
		});
	}

    private void openCreateEventFragment(Fragment frag) {
        Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().replace(
                R.id.event_fragment_container, frag).commit();
    }

    private void navigationListener() {
  	    back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCreateEventFragment(new EventInformationFragment());
            }
        });
  	    next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homeIntent = new Intent(getActivity().getApplicationContext(), EventActivity.class);
                startActivity(homeIntent);
                getActivity().finish();
            }
        });
    }
}
