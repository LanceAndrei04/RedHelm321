package com.example.redhelm321;

import android.os.Build;
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
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class StatusFragment extends Fragment {
    private static final int MAX_DISPLAY_FRIEND = 6;
    private ShapeableImageView profileImageView,
            iv_friendImageView1, iv_friendImageView2, iv_friendImageView3, iv_friendImageView4, iv_friendImageView5;
    private MaterialButton markSafeButton, needHelpButton, sendReportButton;
    private TextInputLayout reportInputLayout;
    private TextInputEditText reportEditText;

    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseManager dbManager;
    UserProfile currentUserProfile;
    String userProfilePath;
    View rootView;

    HashMap<String, ShapeableImageView> friendProfileIds;

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
        userProfilePath = dbManager.getUserProfilePath(mAuth.getCurrentUser().getUid());

        friendProfileIds = new HashMap<>();
        InitializeFriendList(view);

//        friendProfileIds.put("eHr9sW6e6hOJo2ooJUoX5wIUYvR2", view.findViewById(R.id.friendImageView4));
//        friendProfileIds.put("y93MjFxb5ghxeJiPu1cL1t1uMk83", view.findViewById(R.id.friendImageView5));

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

    private void InitializeFriendList(View view) {
//        friendProfileIds.put("SXi005pZ8vZZILr0wHKRZ14DZ5D2", view.findViewById(R.id.friendImageView1));
//        friendProfileIds.put("XPIPPiuONcafZdZoBNuKSunJ8J73", view.findViewById(R.id.friendImageView2));
//        friendProfileIds.put("y93MjFxb5ghxeJiPu1cL1t1uMk83", view.findViewById(R.id.friendImageView3));
        String userProfilePath = dbManager.getUserProfilePath(mAuth.getCurrentUser().getUid());
        ArrayList<ShapeableImageView> iv_friendImageViews = new ArrayList<>();
        iv_friendImageViews.add(view.findViewById(R.id.friendImageView1));
        iv_friendImageViews.add(view.findViewById(R.id.friendImageView2));
        iv_friendImageViews.add(view.findViewById(R.id.friendImageView3));
        iv_friendImageViews.add(view.findViewById(R.id.friendImageView4));
        iv_friendImageViews.add(view.findViewById(R.id.friendImageView5));


        dbManager.readData(userProfilePath, UserProfile.class, new ReadCallback<UserProfile>() {
            @Override
            public void onSuccess(UserProfile profile) {
                ArrayList<String> friendIdList = profile.getFriendIDList();
                if (friendIdList != null) {
                    friendIdList.removeIf(String::isEmpty); // Remove all empty strings
                }

                for (int i = 0; i < MAX_DISPLAY_FRIEND; i++) {
                    if (i >= friendIdList.size()) break;
                    if (friendIdList.get(i).isEmpty()) continue;
                    friendProfileIds.put(friendIdList.get(i), (ShapeableImageView) iv_friendImageViews.get(i));
                }
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }

    private void database_onDataChange(DataSnapshot snapshot) {
        InitializeFriendList(rootView);
        for(String profileId : friendProfileIds.keySet()) {
            UserProfile friendProfile = snapshot.child(profileId).getValue(UserProfile.class);

            String friendCurrentStatus = friendProfile.getStatus() != null ? friendProfile.getStatus() : "Safe";;
            ShapeableImageView iv_friendImageView = Objects.requireNonNull(friendProfileIds.get(profileId));

            setImageClickListener(iv_friendImageView, friendProfile.getName(), friendCurrentStatus, friendProfile.getLatestTimeStatusUpdate());

            changeStatusBorder(
                    iv_friendImageView,
                    friendCurrentStatus
            );

            String userProfilePic = friendProfile.getUserImgLink() != null ? friendProfile.getUserImgLink() : UserProfile.DEFAULT_PROFILE_PIC;
            UserProfile.setImageToImageView(getContext(), iv_friendImageView, userProfilePic);

            Log.d("DEBUG_BORDERCHANGE", friendProfile.getName() + ": " + friendProfile.getStatus() + "||" + friendCurrentStatus );
        }
    }


    private void loadProfileFromDatabase() {
        dbManager.readData(userProfilePath, UserProfile.class, new ReadCallback<UserProfile>() {
            @Override
            public void onSuccess(UserProfile data) {
                currentUserProfile = data;

                String currentStatus = currentUserProfile.getStatus() != null ? currentUserProfile.getStatus() : "Safe";
                changeStatusBorder(profileImageView, currentStatus);
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

    private void markSafeButton_OnClick(View v) {
        reportInputLayout.setVisibility(View.GONE);
        sendReportButton.setVisibility(View.GONE);

        String status = "Safe";
        updateUserStatusUpdtateTimeDB();
        updateUserStatusDB(status);
        updateUserReportDB("");
        changeStatusBorder(profileImageView, status);
    }

    private void needHelpButton_OnClick(View v) {
        changeStatusBorder(profileImageView, "Need Help");
        updateUserStatusDB("Not Safe");
        updateUserStatusUpdtateTimeDB();
        reportInputLayout.setVisibility(View.VISIBLE);
        sendReportButton.setVisibility(View.VISIBLE);

        dbManager.readData(dbManager.getUserProfilePath(mAuth.getCurrentUser().getUid()), UserProfile.class, new ReadCallback<UserProfile>() {
            @Override
            public void onSuccess(UserProfile data) {
                reportEditText.setText(data.getReport());
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }

    private void sendReportButton_OnClick(View v) {
        String report = reportEditText.getText().toString().trim();
        if (!report.isEmpty()) {
            reportInputLayout.setVisibility(View.GONE);
            sendReportButton.setVisibility(View.GONE);

            updateUserReportDB(report);
            updateUserStatusUpdtateTimeDB();

        } else {
            Toast.makeText(requireContext(), "Please enter report details.", Toast.LENGTH_SHORT).show();
        }
    }

    private void changeStatusBorder(ShapeableImageView imgView, String status) {
        int colorResId = status.equals("Safe") ? R.color.green : R.color.red_primary;

        imgView.setStrokeColor(
                getResources().getColorStateList(
                        colorResId,
                        requireContext().getTheme()
                )
        );
    }


    private void updateUserStatusDB(String status) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userStatusPath = dbManager.getUserProfilePath(currentUser.getUid()) + "/status";
        dbManager.saveData(userStatusPath, status, new DatabaseCallback() {
            @Override
            public void onSuccess() {
                dbManager.readData(dbManager.getUserProfilePath(currentUser.getUid()), UserProfile.class, new ReadCallback<UserProfile>() {
                    @Override
                    public void onSuccess(UserProfile data) {
                        setImageClickListener(profileImageView, data.getName(), data.getStatus(), data.getLatestTimeStatusUpdate());
                    }

                    @Override
                    public void onFailure(Exception e) {

                    }
                });
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "Status Updated",
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
    private void updateUserStatusUpdtateTimeDB() {
        String userStatusPath = dbManager.getUserProfilePath(mAuth.getCurrentUser().getUid()) + "/latestTimeStatusUpdate";


        LocalDateTime now = null;
        String formattedDate = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            now = LocalDateTime.now();
            formattedDate = DateTimeFormatter.ofPattern("hh:mm a | yyyy-MM-dd").format(now);
        }
        dbManager.saveData(userStatusPath, formattedDate, new DatabaseCallback() {
            @Override
            public void onSuccess() {
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
    private void updateUserReportDB(String report) {
        String userStatusPath = dbManager.getUserProfilePath(mAuth.getCurrentUser().getUid()) + "/report";
        dbManager.saveData(userStatusPath, report, new DatabaseCallback() {
            @Override
            public void onSuccess() {
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