package com.example.redhelm321;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ActionProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import android.window.SplashScreen;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.redhelm321.database.DatabaseCallback;
import com.example.redhelm321.database.DatabaseManager;
import com.example.redhelm321.database.ReadCallback;
import com.example.redhelm321.profile.UserProfile;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;

public class ProfileFragment extends Fragment implements ActionProvider.VisibilityListener {
    private TextView nameTextView, emailTextView, contactTextView;
    private ImageView iv_profileImageView;
    private FloatingActionButton fbtn_editImageButton;
    private TextInputEditText et_nameEditText, et_emailEditText, et_contactNumberEditText, et_birthdayEditText, et_bloodTypeEditText, et_addressEditText;
    private Button saveButton, logoutButton;;

    FirebaseAuth mAuth;
    DatabaseManager dbManager;
    String userProfilePath;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        InitializeComponent(view);
        loadProfileFromDatabase();

        return view;
    }

    public void loadProfileFromDatabase() {
        userProfilePath = dbManager.getUserProfilePath(mAuth.getCurrentUser().getUid());
        dbManager.readData(userProfilePath, UserProfile.class, new ReadCallback<UserProfile>() {
            @Override
            public void onSuccess(UserProfile data) {
                updateProfile(data);
            }

            @Override
            public void onFailure(Exception e) {
                Log.d("DEBUG_UPDATE_PROFILE", "FAILED ANG UPDATE PROFILE" + e.getMessage());
            }
        });
    }

    private void InitializeComponent(View view) {
        mAuth = FirebaseAuth.getInstance();
        dbManager = DatabaseManager.getInstance(mAuth.getCurrentUser().getUid());


        iv_profileImageView = view.findViewById(R.id.profileImageView);
        fbtn_editImageButton = view.findViewById(R.id.editImageButton);
        fbtn_editImageButton.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Edit button clicked", Toast.LENGTH_SHORT).show();
        });

        // Initialize TextViews from header section
        nameTextView = view.findViewById(R.id.nameTextView);
        emailTextView = view.findViewById(R.id.emailTextView);
        contactTextView = view.findViewById(R.id.contactTextView);

        // Initialize EditTexts from input fields
        et_nameEditText = view.findViewById(R.id.nameEditText);
        et_emailEditText = view.findViewById(R.id.emailEditText);
        et_contactNumberEditText = view.findViewById(R.id.contactNumberEditText);

        et_birthdayEditText = view.findViewById(R.id.birthdayEditText);
        et_bloodTypeEditText = view.findViewById(R.id.bloodTypeEditText);
        et_addressEditText = view.findViewById(R.id.addressEditText);

        // Initialize Save Button
        saveButton = view.findViewById(R.id.saveButton);
        logoutButton = view.findViewById(R.id.loginButton);

        // Birthday picker setup
        EditText birthdayEditText = view.findViewById(R.id.birthdayEditText);
        setupBirthdayPicker(birthdayEditText);

        // Save button click listener
        saveButton.setOnClickListener(v -> saveProfileToDatabase());
        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            getActivity().finish();
        });
    }

    private void saveProfileToDatabase() {
        String name = et_nameEditText.getText().toString();
        String contact = et_contactNumberEditText.getText().toString();
        String birthday = et_birthdayEditText.getText().toString();
        String bloodType = et_bloodTypeEditText.getText().toString();
        String address = et_addressEditText.getText().toString();

        UserProfile newProfile = new UserProfile.Builder()
                .setName(name)
                .setPhoneNumber(contact)
                .setBirthDate(birthday)
                .setBloodType(bloodType)
                .setAddress(address)
                .build();

        dbManager.saveData(userProfilePath, newProfile, new DatabaseCallback() {
            @Override
            public void onSuccess() {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "Profile updated!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {

            }
        });


    }

    private void setupBirthdayPicker(EditText birthdayEditText) {
        birthdayEditText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                    (view1, selectedYear, selectedMonth, selectedDay) -> {
                        String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        birthdayEditText.setText(date);
                    },
                    year, month, day);

            datePickerDialog.show();
        });
    }

    private void updateProfile(UserProfile userProfile) {

        try {
            Log.d("DEBUG_UPDATE_PROFILE", "NATAWAG ANG UPDATE PROFILE");
            if(userProfile == null) {
                Log.d("DEBUG_UPDATE_PROFILE", "UserProfile is null");
                return;
            }

            if(userProfile.getUserImgLink() != null && !userProfile.getUserImgLink().isEmpty()) {
                Log.d("DEBUG_UPDATE_PROFILE", "UserProfile is null " + userProfile.getUserImgLink());
                UserProfile.setImageToImageView(getContext(), iv_profileImageView, userProfile.getUserImgLink());
            }

            nameTextView.setText(userProfile.getName());
            emailTextView.setText(userProfile.getEmail());
            contactTextView.setText(userProfile.getPhoneNumber());

            et_emailEditText.setText(userProfile.getEmail());
            et_nameEditText.setText(userProfile.getName());
            et_contactNumberEditText.setText(userProfile.getPhoneNumber());
            et_bloodTypeEditText.setText(userProfile.getBloodType());
            et_addressEditText.setText(userProfile.getAddress());
            et_birthdayEditText.setText(userProfile.getBirthDate());
        } catch (Exception e) {
            Log.d("DEBUG_UPDATE_PROFILE", "FAILED ANG UPDATE PROFILE" + e.getMessage());
        }

    }


    @Override
    public void onActionProviderVisibilityChanged(boolean b) {
        Toast.makeText(getContext(), "AYDIWOW", Toast.LENGTH_SHORT).show();
    }
}
