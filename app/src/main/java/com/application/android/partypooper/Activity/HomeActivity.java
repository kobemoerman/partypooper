package com.application.android.partypooper.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import android.view.View;
import android.widget.Toast;

import com.application.android.partypooper.Fragment.CalendarTabFragment;
import com.application.android.partypooper.Fragment.SearchDefaultFragment;
import com.application.android.partypooper.Fragment.SearchFragment;
import com.application.android.partypooper.Fragment.HomeFragment;
import com.application.android.partypooper.Fragment.MenuFragment;
import com.application.android.partypooper.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class HomeActivity extends PortraitActivity {

    /** TAG of the current fragment */
    private String TAG = "home_frag";

    /** Query of all users */
    private Query qUsers;

    /** Firebase authentication */
    private FirebaseAuth mAuth;

    /** Firebase current user */
    private FirebaseUser mUser;

    /** Firebase reference to user info */
    private DatabaseReference refUserInfo;

    /** Firebase reference to user friends */
    private DatabaseReference refUserFriends;

    /**
     * On start method of the activity
     */
    @Override
    protected void onStart() {
        super.onStart();
        initFirebase();
    }

    /**
     * On create method of the activity.
     * @param savedInstanceState this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        navigationButtonListener();
        updateFragment(new HomeFragment());
    }

    /**
     * Initialises the Firebase information.
     */
    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        refUserFriends = FirebaseDatabase.getInstance().getReference("Friends").child(mUser.getUid());
        refUserInfo = FirebaseDatabase.getInstance().getReference("Users").child(mUser.getUid());

        qUsers = FirebaseDatabase.getInstance().getReference("Users").orderByChild("username");
    }

    /**
     * Allows the user to navigate between fragments on the HomeActivity.
     */
    private void navigationButtonListener() {
        BottomNavigationView bottomNav = findViewById(R.id.home_bottom_nav);

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        if (!TAG.equals("home_frag")) {
                            updateFragment(new HomeFragment());
                            TAG = "home_frag";
                        }
                        break;
                    case R.id.nav_search:
                        if (!TAG.equals("search_frag")) {
                            updateFragment(new SearchDefaultFragment());
                            TAG = "search_frag";
                        }
                        break;
                    case R.id.nav_calendar:
                        if (!TAG.equals("calendar_frag")) {
                            updateFragment( new CalendarTabFragment());
                            TAG = "calendar_frag";
                        }
                        break;
                    case R.id.nav_menu:
                        if (!TAG.equals("menu_frag")) {
                            updateFragment(new MenuFragment());
                            TAG = "menu_frag";
                        }
                        break;
                }
                return true;
            }
        });
    }

    /**
     * On click listener for the log out text view.
     * Launches the LoginActivity and kills the current one.
     * @param view view of this activity
     */
    public void onClickLogOutMenu(View view) {
        try {
            mAuth.signOut();
        } catch (Exception e) {
            e.printStackTrace();
        }
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    /**
     * On click listener for the edit profile button.
     * Launches the EditProfileActivity.
     * @param view view of this activity
     */
    public void onClickEditProfileMenu(View view) {
        startActivity(new Intent(this, EditProfileActivity.class));
    }

    /**
     * On click listener for the my friends section.
     * Launches the FriendsActivity.
     * @param view view of this activity
     */
    public void onClickFriendsMenu(View view) {
        startActivity(new Intent(this, FriendsActivity.class));
    }

    /**
     * On click listener for the my calendar section.
     * Launches the CalendarActivity.
     * @param view view of this activity
     */
    public void onClickEventMenu(View view) {
        startActivity(new Intent(this, CalendarActivity.class));
    }

    public void onClickSettingsMenu(View view) {
        showMessage("Settings not yet implemented.");
    }

    /**
     * On click listener for the create event button.
     * Launches the CreateEventActivity.
     * @param view view of this activity
     */
    public void onClickCreateEventHome(View view) {
        startActivity(new Intent(this, CreateEventActivity.class));
    }

    /**
     * On click listener for the calendar recycler view.
     * Launches the EventActivity.
     * @param id of the event
     */
    public void onClickLaunchEvent(String id) {
        Intent eventIntent = new Intent(getApplicationContext(), EventActivity.class);
        eventIntent.putExtra("id",id);
        startActivity(eventIntent);
    }

    /**
     * Displays a toast on the screen.
     * @param s text to display
     */
    public void showMessage(String s) {
        Toast.makeText(this,s, Toast.LENGTH_LONG).show();
    }

    /**
     * Replace the current fragment with the one to be displayed.
     * @param selected fragment to be displayed
     */
    public void updateFragment(Fragment selected) {
        getSupportFragmentManager().beginTransaction().replace(R.id.home_fragment_container, selected).commit();
    }

    /**
     * The Firebase authentication of the user.
     * @return mAuth
     */
    public FirebaseAuth getmAuth() {
        return mAuth;
    }

    /**
     * The current user.
     * @return mUser
     */
    public FirebaseUser getmUser() {
        return mUser;
    }

    /**
     * Reference to current user data.
     * @return refUserInfo
     */
    public DatabaseReference getRefUserInfo() {
        return refUserInfo;
    }

    /**
     * Reference to get friend count of user.
     * @return refUserFriends
     */
    public DatabaseReference getRefUserFriends() {
        return refUserFriends;
    }

    /**
     * Query of all users in the Firebase.
     * @return
     */
    public Query getqUsers() {
        return qUsers;
    }

    /**
     * Query of all users in a string interval
     * @return
     */
    public Query getUsersCustom(String s) {
        return qUsers.startAt(s).endAt(s+"\uf8ff");
    }
}
