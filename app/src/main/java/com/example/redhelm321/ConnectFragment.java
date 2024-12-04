package com.example.redhelm321;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.redhelm321.database.DatabaseCallback;
import com.example.redhelm321.database.DatabaseManager;
import com.example.redhelm321.database.ReadCallback;
import com.example.redhelm321.profile.UserProfile;
import com.google.firebase.auth.FirebaseAuth;


public class ConnectFragment extends Fragment {

    private static final String TAG = "ConnectFragment";

    Button btn_db_debug, btn_db_read_debug;
    DatabaseManager dbManager;
    FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_connect, container, false);

        InitializeComponents(rootView);

        return rootView;
    }

    private void InitializeComponents(View rootView) {
        dbManager = DatabaseManager.getInstance();
        mAuth = FirebaseAuth.getInstance();

        btn_db_read_debug = rootView.findViewById(R.id.btn_db_read_debug);
        btn_db_read_debug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_db_read_debug_OnClick();
            }
        });


        btn_db_debug = rootView.findViewById(R.id.btn_db_debug);
        btn_db_debug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "DEBUG DEBUG pooosh", Toast.LENGTH_SHORT).show();
                btn_db_debug_OnClick();
            }
        });

    }

    private void btn_db_read_debug_OnClick() {
        String userProfilePath = "profiles/" + mAuth.getCurrentUser().getUid();

        dbManager.readData(userProfilePath, UserProfile.class, new ReadCallback<UserProfile>() {
            @Override
            public void onSuccess(UserProfile data) {
                Toast.makeText(getContext(), "WOAH: " + data.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {

            }
        });

    }

    private void btn_db_debug_OnClick() {
        String userProfilePath = "profiles/" + mAuth.getCurrentUser().getUid();

        UserProfile userProfile = new UserProfile.Builder()
                .setName("MyName")
                .setAddress("MyAddress")
                .setBirthDate("MyBDate")
                .setPhoneNumber("MyPhone")
                .setBloodType("MyBlood")
                .setUserImgLink("MyImgLink")
                .setAge(20)
                .build();

        dbManager.saveData(userProfilePath, userProfile, new DatabaseCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "SUCCESSFULLY ADDED");
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }
}