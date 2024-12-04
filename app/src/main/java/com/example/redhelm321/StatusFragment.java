package com.example.redhelm321;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.redhelm321.database.DatabaseCallback;
import com.example.redhelm321.database.DatabaseManager;
import com.example.redhelm321.database.ReadCallback;
import com.example.redhelm321.profile.UserProfile;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class StatusFragment extends Fragment {
    private static final int MAX_DISPLAY_FRIEND = 5;
    private ImageView profileImageView, iv_friendImageView1, iv_friendImageView2, iv_friendImageView3, iv_friendImageView4, iv_friendImageView5;
    private MaterialButton markSafeButton, needHelpButton, sendReportButton;
    private TextInputLayout reportInputLayout;
    private TextInputEditText reportEditText;

    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseManager dbManager;
    UserProfile currentUserProfile;
    String userProfilePath;
    View rootView;

    HashMap<String, ImageView> friendProfileIds;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_status, container, false);

        InitializeComponent(rootView);
        loadProfileFromDatabase();

        return rootView;
    }

    private void InitializeComponent(View view) {
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference().child("profiles").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(getContext(), "DATABASE CHANGED", Toast.LENGTH_SHORT).show();
                    database_onDataChange(snapshot);
                }
                else {
                    Log.d("DATABASE_CHANGE", "No snapshot available");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        dbManager = DatabaseManager.getInstance(mAuth.getCurrentUser().getUid());
        userProfilePath = dbManager.getUserProfilePath();

        

        friendProfileIds = new HashMap<>();
        friendProfileIds.put("9wgMiJMBDJUhKIYExHdqLxsPbOO2", view.findViewById(R.id.friendImageView1));
        friendProfileIds.put("VmHdqFzAbfY7GFT1r94bL1kjGfZ2", view.findViewById(R.id.friendImageView2));
        friendProfileIds.put("ivmfZhoOnvhYYc8nQj6DtU0qLfm2", view.findViewById(R.id.friendImageView3));
        friendProfileIds.put("eHr9sW6e6hOJo2ooJUoX5wIUYvR2", view.findViewById(R.id.friendImageView4));
        friendProfileIds.put("y93MjFxb5ghxeJiPu1cL1t1uMk83", view.findViewById(R.id.friendImageView5));

        // Initialize views
        profileImageView = view.findViewById(R.id.profileImageView);
        markSafeButton = view.findViewById(R.id.markSafeButton);
        needHelpButton = view.findViewById(R.id.needHelpButton);
        reportInputLayout = view.findViewById(R.id.reportInputLayout);
        reportEditText = view.findViewById(R.id.reportEditText);
        sendReportButton = view.findViewById(R.id.sendReportButton);

        // Set click listeners for status buttons
        markSafeButton.setOnClickListener(v -> {
            markSafeButton_OnClick(v);
        });

        needHelpButton.setOnClickListener(v -> {
            needHelpButton_OnClick(v);
        });
        
        // Set click listener for send button
        sendReportButton.setOnClickListener(v -> {
            sendReportButton_OnClick(v);
        });
    }

    private void loadProfileFromDatabase() {
        dbManager.readData(userProfilePath, UserProfile.class, new ReadCallback<UserProfile>() {
            @Override
            public void onSuccess(UserProfile data) {
                currentUserProfile = data;

                String currentStatus = currentUserProfile.getStatus() != null ? currentUserProfile.getStatus() : "Safe";
                String latestTimeStatusUpdate = currentUserProfile.getLatestTimeStatusUpdate() != null ? currentUserProfile.getLatestTimeStatusUpdate() : "N/A";
                setImageClickListener(profileImageView, currentUserProfile.getName(), currentStatus, latestTimeStatusUpdate);

                updateProfileUI(data);
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }

    private void updateProfileUI(UserProfile userProfile) {
        String profileImgUrl =
                mAuth.getCurrentUser().getPhotoUrl() != null ?
                        mAuth.getCurrentUser().getPhotoUrl().toString() :
                        UserProfile.DEFAULT_PROFILE_PIC;
        UserProfile.setImageToImageView(getContext(), profileImageView, profileImgUrl);
    }

    private void database_onDataChange(DataSnapshot snapshot) {
        for(String profileId : friendProfileIds.keySet()) {
            UserProfile friendProfile = snapshot.child(profileId).getValue(UserProfile.class);


            String friendCurrentStatus = friendProfile.getStatus() != null ? friendProfile.getStatus() : "Safe";;

            changeStatusBorder(
                    Objects.requireNonNull(friendProfileIds.get(profileId)),
                    friendCurrentStatus
            );

            Log.d("DEBUG_BORDERCHANGE", friendProfile.getStatus() + "");
        }
    }

    private void needHelpButton_OnClick(View v) {
        changeStatusBorder(profileImageView, "Need Help");
        reportInputLayout.setVisibility(View.VISIBLE);
        sendReportButton.setVisibility(View.VISIBLE);
    }

    private void changeStatusBorder(ImageView imgView, String status) {
        int statusBorder = status.contains("Safe") ? R.drawable.circle_border_green : R.drawable.circle_border_red;

        imgView.setBackground(
                getResources().getDrawable(
                        statusBorder,
                        requireContext().getTheme()));
    }

    private void markSafeButton_OnClick(View v) {
        changeStatusBorder(profileImageView, "Safe");
        reportInputLayout.setVisibility(View.GONE);
        sendReportButton.setVisibility(View.GONE);

        String userStatusPath = dbManager.getUserProfilePath() + "/status";
        dbManager.saveData(userStatusPath, "Safe", new DatabaseCallback() {
            @Override
            public void onSuccess() {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "You've been marked safe",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "Failed to mark you safe",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }
        });
    }

    private void sendReportButton_OnClick(View v) {
        String report = reportEditText.getText().toString().trim();
        if (!report.isEmpty()) {
            Toast.makeText(requireContext(), "Report sent: " + report, Toast.LENGTH_SHORT).show();
            reportInputLayout.setVisibility(View.GONE);
            sendReportButton.setVisibility(View.GONE);

            String userStatusPath = dbManager.getUserProfilePath() + "/status";
            dbManager.saveData(userStatusPath, report, new DatabaseCallback() {
                @Override
                public void onSuccess() {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "Report sent",
                                            Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
                }

                @Override
                public void onFailure(Exception e) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "Failed to send report",
                                            Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
                }
            });

        } else {
            Toast.makeText(requireContext(), "Please enter report details.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setImageClickListener(ImageView imageView, String name, String status, String lastUpdate, String... extraDetails) {
        imageView.setOnClickListener(v -> {
            View popupView = LayoutInflater.from(requireContext()).inflate(R.layout.popup_info, null);
            PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            TextView nameTextView = popupView.findViewById(R.id.popupName);
            TextView statusTextView = popupView.findViewById(R.id.popupStatus);
            TextView reportTextView = popupView.findViewById(R.id.popupReport);
            TextView locationTextView = popupView.findViewById(R.id.popupLocation);
            TextView lastUpdateTextView = popupView.findViewById(R.id.popupLastUpdate);

            nameTextView.setText("Name: " + name);
            statusTextView.setText("Status: " + status);
            lastUpdateTextView.setText("Last Update: " + lastUpdate);

            if (extraDetails.length > 0) {
                reportTextView.setText("Report: " + extraDetails[0]);
                reportTextView.setVisibility(View.VISIBLE);
            }

            if (extraDetails.length > 1) {
                locationTextView.setText("Location: " + extraDetails[1]);
                locationTextView.setVisibility(View.VISIBLE);
            }

            popupWindow.setFocusable(true);
            popupWindow.showAtLocation(imageView, Gravity.CENTER, 0, 0);
        });
    }
}