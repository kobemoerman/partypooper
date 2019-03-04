package com.application.android.partypooper.Activity;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.application.android.partypooper.Fragment.EventsFragment;
import com.application.android.partypooper.Fragment.FriendsFragment;
import com.application.android.partypooper.Fragment.HomeFragment;
import com.application.android.partypooper.Fragment.MenuFragment;
import com.application.android.partypooper.R;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        navigationButtonListener();

        updateFragment(new HomeFragment());

    }

    private void navigationButtonListener() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                switch (item.getItemId()) {
                    case R.id.nav_home:
                        selectedFragment = new HomeFragment();
                        break;
                    case R.id.nav_friends:
                        selectedFragment = new FriendsFragment();
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

    private void updateFragment (Fragment selected) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragement_container, selected).commit();
    }
}
