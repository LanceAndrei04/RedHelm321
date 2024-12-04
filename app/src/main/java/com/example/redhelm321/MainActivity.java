package com.example.redhelm321;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.redhelm321.authentication.LoginActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private FrameLayout frameLayout;
    private BottomNavigationView bottomNavigationView;

    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;

    private DatabaseReference usersRef;
    private DatabaseReference FBDB_profilesRef;
    private ActivityResultLauncher<Intent> activityResultLauncher;

    FragmentManager fragmentManager;
    private Fragment connectFragment;
    private Fragment hotlineFragment;
    private Fragment statusFragment;
    private Fragment profileFragment;

    private Fragment activeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        frameLayout = findViewById(R.id.frame_layout);
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        InitializeAuth();
        HandleUserAuthentication();


        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                handleNavigation(menuItem.getItemId());
                return true;
            }
        });

    }

        @Override
    protected void onResume() {
        super.onResume();
        if(mAuth.getCurrentUser() != null) {
            connectFragment = new ConnectFragment();
            hotlineFragment = new HotlineFragment();
            statusFragment = new StatusFragment();
            profileFragment = new ProfileFragment();
            activeFragment = connectFragment;
            initializeFragments();
        }
    }

    private void initializeFragments() {
        addFragmentToManager(profileFragment, "PROFILE", true);
        addFragmentToManager(statusFragment, "STATUS", true);
        addFragmentToManager(hotlineFragment, "HOTLINE", true);
        addFragmentToManager(connectFragment, "CONNECT", true);
    }

    private void addFragmentToManager(Fragment fragment, String tag) {
        addFragmentToManager(fragment, tag, true);
    }

    private void addFragmentToManager(Fragment fragment, String tag, boolean hide) {
        fragmentManager = getSupportFragmentManager();
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



    private void HandleUserAuthentication() {

        if(mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Please log in first.",
                    Toast.LENGTH_SHORT).show();
            openLoginPage();
        }   else {
            Toast.makeText(this,
                    "Welcome back! " + mAuth.getCurrentUser().getDisplayName(),
                    Toast.LENGTH_SHORT).show();

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

    private void InitializeAuth() {
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        usersRef = firebaseDatabase.getReference("users");
        FBDB_profilesRef = firebaseDatabase.getReference("profiles");
    }

    private void openLoginPage() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }





}