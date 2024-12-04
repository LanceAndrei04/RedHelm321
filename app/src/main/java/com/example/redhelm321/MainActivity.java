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
    GoogleSignInClient googleSignInClient ;

    private DatabaseReference usersRef;
    private DatabaseReference FBDB_profilesRef;
    private ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        InitializeAuth();
        HandleUserAuthentication();
    }

    private void HandleUserAuthentication() {
        if(mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Please log in first.", Toast.LENGTH_SHORT).show();
            openLoginPage();
        }
    }

    private void openLoginPage() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }


    private void InitializeAuth() {
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        usersRef = firebaseDatabase.getReference("users");
        FBDB_profilesRef = firebaseDatabase.getReference("profiles");
    }


}