package com.example.redhelm321;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.redhelm321.authentication.LoginActivity;
import com.example.redhelm321.connect_nearby.ConnectNearbyFragment;
import com.example.redhelm321.connect_nearby.WIFI_P2P_SharedData;
import com.example.redhelm321.connect_nearby.WiFiDirectBroadcastReceiver;
import com.example.redhelm321.profile.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    public static final int IP_PORT = 1307;
    private FrameLayout frameLayout;
    private BottomNavigationView bottomNavigationView;

    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;

    private DatabaseReference usersRef;
    private DatabaseReference FBDB_profilesRef;

    private FragmentManager fragmentManager;
    private Fragment connectFragment;
    private Fragment hotlineFragment;
    private Fragment statusFragment;
    private Fragment profileFragment;

    private Fragment activeFragment;

    WifiManager wifiManager;
    WifiP2pManager wifiP2pManager;
    WifiP2pManager.Channel wifiP2pChannel;
    BroadcastReceiver broadcastReceiver;
    IntentFilter intentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        frameLayout = findViewById(R.id.frame_layout);
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        InitializeAuth();
        HandleUserAuthentication();

        // Initialize WiFi Direct
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WIFI_P2P_SharedData.setWifiManager(wifiManager);

        wifiP2pManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        wifiP2pChannel = wifiP2pManager.initialize(this, getMainLooper(), null);
        broadcastReceiver = new WiFiDirectBroadcastReceiver(wifiP2pManager, wifiP2pChannel, this);
        WIFI_P2P_SharedData.setWifiP2pManager(wifiP2pManager);
        WIFI_P2P_SharedData.setWifiP2pChannel(wifiP2pChannel);
        WIFI_P2P_SharedData.setBroadcastReceiver(broadcastReceiver);

        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        WIFI_P2P_SharedData.setIntentFilter(intentFilter);

        // Bottom Navigation Listener
        bottomNavigationView.setOnItemSelectedListener(menuItem -> {
            handleNavigation(menuItem.getItemId());
            return true;
        });

        // Initialize fragments
        initializeFragments();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAuth.getCurrentUser() != null) {
            Toast.makeText(this,
                    "Welcome back! " + mAuth.getCurrentUser().getDisplayName(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeFragments() {
        fragmentManager = getSupportFragmentManager();

        // Initialize fragments
        connectFragment = new ConnectNearbyFragment();
        hotlineFragment = new HotlineFragment();
        statusFragment = new StatusFragment();
        profileFragment = new ProfileFragment();

        // Add all fragments, hide them except the default active one
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.frame_layout, connectFragment, "CONNECT").hide(connectFragment);
        transaction.add(R.id.frame_layout, hotlineFragment, "HOTLINE").hide(hotlineFragment);
        transaction.add(R.id.frame_layout, statusFragment, "STATUS").hide(statusFragment);
        transaction.add(R.id.frame_layout, profileFragment, "PROFILE").hide(profileFragment);

        // Show the initial fragment (ConnectNearbyFragment)
        activeFragment = connectFragment;
        transaction.show(activeFragment);
        transaction.commit();
    }

    private void handleNavigation(int itemId) {
        Fragment targetFragment = null;

        if (itemId == R.id.bottom_nav_connect) {
            targetFragment = connectFragment;
        } else if (itemId == R.id.bottom_nav_hotline) {
            targetFragment = hotlineFragment;
        } else if (itemId == R.id.bottom_nav_status) {
            targetFragment = statusFragment;
        } else if (itemId == R.id.bottom_nav_profile) {
            targetFragment = profileFragment;
        }

        if (targetFragment != null && targetFragment != activeFragment) {
            switchFragment(targetFragment);
        }
    }


    private void switchFragment(Fragment targetFragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.hide(activeFragment).show(targetFragment).commit();
        activeFragment = targetFragment;
    }

    private void HandleUserAuthentication() {
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Please log in first.", Toast.LENGTH_SHORT).show();
            openLoginPage();
        }
    }

    private void InitializeAuth() {
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        usersRef = firebaseDatabase.getReference("users");
        FBDB_profilesRef = firebaseDatabase.getReference("profiles");
    }

    private void openLoginPage() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish(); // Prevent user from returning to the main activity without logging in
    }
}
