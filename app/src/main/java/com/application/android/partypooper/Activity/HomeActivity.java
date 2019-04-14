package com.application.android.partypooper.Activity;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.application.android.partypooper.Fragment.EventsFragment;
import com.application.android.partypooper.Fragment.SearchFragment;
import com.application.android.partypooper.Fragment.HomeFragment;
import com.application.android.partypooper.Fragment.MenuFragment;
import com.application.android.partypooper.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class HomeActivity extends AppCompatActivity {

    /** Firebase authentication */
    private FirebaseAuth mAuth;

    /** Firebase current user */
    private FirebaseUser mUser;

    /** Firebase reference to user info */
    private DatabaseReference refUserInfo;

    /** Firebase reference to user friends */
    private DatabaseReference refFriendsCount;

    /**
     * Sets the initial fragment to HomeFragment
     */
    @Override
    protected void onStart() {
        super.onStart();
        updateFragment(new HomeFragment());
    }
    /**
     * On create method of the activity.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initFirebase();
        navigationButtonListener();
    }
    /**
     * Initialises the Firebase information.
     */
    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        refFriendsCount = FirebaseDatabase.getInstance().getReference("Friends").child(mUser.getUid());
        refUserInfo = FirebaseDatabase.getInstance().getReference("Users").child(mUser.getUid());
    }

    /**
     * Allows the user to navigate between fragments on the HomeActivity.
     */
    private void navigationButtonListener() {
        BottomNavigationView bottomNav = findViewById(R.id.home_bottom_nav);

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                switch (item.getItemId()) {
                    case R.id.nav_home:
                        selectedFragment = new HomeFragment();
                        break;
                    case R.id.nav_search:
                        selectedFragment = new SearchFragment();
                        break;
                    case R.id.nav_calendar:
                        selectedFragment = new EventsFragment();
                        break;
                    case R.id.nav_menu:
                        selectedFragment = new MenuFragment();
                        break;
                }
                updateFragment(selectedFragment);
                return true;
            }
        });
    }

    /**
     * Replace the current fragment with the parameter.
     * @param selected fragment to be displayed
     */
    private void updateFragment (Fragment selected) {
        getSupportFragmentManager().beginTransaction().replace(R.id.home_fragment_container, selected).commit();
    }
}
