package com.example.redhelm321;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class Navigation extends AppCompatActivity {

    private FrameLayout frameLayout;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        frameLayout = findViewById(R.id.frame_layout);
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new ConnectFragment()).commit();
        }

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();

                if (id == R.id.bottom_nav_connect) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new ConnectFragment()).commit();
                } else if (id == R.id.bottom_nav_hotline) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new HotlineFragment()).commit();
                } else if (id == R.id.bottom_nav_status) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new StatusFragment()).commit();
                } else if (id == R.id.bottom_nav_profile) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new ProfileFragment()).commit();
                }
                return true;
            }
        });
    }
}