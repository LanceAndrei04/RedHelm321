package com.example.redhelm321;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class Navigation extends AppCompatActivity {

    private FrameLayout frameLayout;
    private BottomNavigationView bottomNavigationView;

    private Fragment connectFragment = new ConnectFragment();
    private Fragment hotlineFragment = new HotlineFragment();
    private Fragment statusFragment = new StatusFragment();
    private Fragment profileFragment = new ProfileFragment();

    private Fragment activeFragment = connectFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        frameLayout = findViewById(R.id.frame_layout);
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        initializeFragments();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                handleNavigation(menuItem.getItemId());
                return true;
            }
        });
    }

    private void initializeFragments() {
        addFragmentToManager(profileFragment, "PROFILE");
        addFragmentToManager(statusFragment, "STATUS");
        addFragmentToManager(hotlineFragment, "HOTLINE");
        addFragmentToManager(connectFragment, "CONNECT", false);
    }

    private void addFragmentToManager(Fragment fragment, String tag) {
        addFragmentToManager(fragment, tag, true);
    }

    private void addFragmentToManager(Fragment fragment, String tag, boolean hide) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction()
                .add(R.id.frame_layout, fragment, tag);

        if (hide) {
            transaction.hide(fragment);
        }

        transaction.commit();
    }

    private void handleNavigation(int itemId) {
        if (itemId == R.id.bottom_nav_connect) {
            switchFragment(connectFragment);
        } else if (itemId == R.id.bottom_nav_hotline) {
            switchFragment(hotlineFragment);
        } else if (itemId == R.id.bottom_nav_status) {
            switchFragment(statusFragment);
        } else if (itemId == R.id.bottom_nav_profile) {
            switchFragment(profileFragment);
        }
    }

    private void switchFragment(Fragment targetFragment) {
        if (activeFragment != targetFragment) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.hide(activeFragment);
            transaction.show(targetFragment);
            transaction.commit();

            activeFragment = targetFragment;
        }
    }
}
