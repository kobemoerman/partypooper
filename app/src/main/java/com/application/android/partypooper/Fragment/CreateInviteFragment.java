package com.application.android.partypooper.Fragment;

import android.app.Activity;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.application.android.partypooper.Activity.CreateEventActivity;
import com.application.android.partypooper.Adapter.EventAdapter;
import com.application.android.partypooper.Model.User;
import com.application.android.partypooper.R;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class CreateInviteFragment extends Fragment {

	/** Number of items to be displayed in the recycler view */
	private int LIMIT = 20;

	/** TAG reference of this fragment */
	private final static String TAG = "fragment/CreateInvite";

	/** Reference to the CreateEvent Activity */
	private CreateEventActivity act;

	/** List of all the user's friends */
	private List<String> mFriends;

	/** Search bar to customise query */
	private EditText searchBar;

	/** Buttons to navigate back and forth between fragments */
	private Button back, next;

	/** Firebase reference to all members of the event */
	private DatabaseReference mMembers;

	/** Firebase reference to all friends of the user */
	private DatabaseReference refFriends;

	/** Firebase reference to all users */
	private DatabaseReference refUsers;

	/** Recycler View to display friends */
	private RecyclerView recyclerView;

	/** Adapter to display data in the recycler view */
	private EventAdapter mAdapter;

	/**
	 * On create method of the fragment.
	 * @param inflater inflate any views in the fragment
	 * @param container parent view that the fragment's UI should be attached to
	 * @param savedInstanceState this fragment is being re-constructed from a previous saved state as given here
	 * @return Return the View for the fragment's UI, or null.
	 */
	@Override
  	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	View view = inflater.inflate(R.layout.fragment_create_invite, container, false);

    	initView(view);
    	recyclerScrollListener();
        inviteUsersQueryDatabase();
        itemClickListener();

        navigationListener(back, new CreateInformationFragment(),"fragment/CreateInformation");
        navigationListener(next, new CreateRecommendationFragment(), "fragment/CreateInformation");

        hideKeyboardListener(searchBar);

    	return view;
  	}

    /**
	 * Initialises the fragment view, date and time.
	 * @param view fragment_create_invite
	 */
    private void initView(View view) {
		act = (CreateEventActivity) getActivity();

		assert act != null;

		refUsers = act.getRefUsers();
		refFriends = act.getRefFriends();
		mMembers = act.getmMembers();

		back = view.findViewById(R.id.frag_create_invite_back);
		next = view.findViewById(R.id.frag_create_invite_next);
		searchBar = view.findViewById(R.id.frag_create_search_bar);

		recyclerView = view.findViewById(R.id.frag_create_recycler);
		recyclerView.setHasFixedSize(true);
		recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    	mFriends = new ArrayList<>();
    	mAdapter = new EventAdapter(getContext(), new ArrayList<User>(), mMembers);
    	recyclerView.setAdapter(mAdapter);
  	}

	/**
	 * Take action on the item clicked.
	 */
	private void itemClickListener() {
      mAdapter.setOnItemClickListener(new EventAdapter.onItemClickListener() {
        @Override
        public void onItemClick(int pos) {
          User u = mAdapter.getItem(pos);
          setUserDatabaseReference(u);
        }
      });
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
	 * Update the data in the Members DataBaseReference.
	 * @param u user to be updated
	 */
	private void setUserDatabaseReference(final User u) {
      mMembers.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
          if (dataSnapshot.child(u.getId()).exists()) {
            mMembers.child(u.getId()).removeValue();
          } else {
            mMembers.child(u.getId()).setValue(false);
          }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
      });
    }

	/**
	 * Populate the Recycler View depending on the query.
	 */
    private void inviteUsersQueryDatabase() {
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
		InputMethodManager inputMethodManager =(InputMethodManager)getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	/**
	 * Listener to navigate to the previous fragment or the next one.
	 * @param b button to listen
	 * @param frag fragment to switch
	 * @param TAG reference of the fragment
	 */
	private void navigationListener (Button b, final Fragment frag, final String TAG) {
  	    b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	act.updateFragment(frag,TAG);
            }
        });
    }
}
