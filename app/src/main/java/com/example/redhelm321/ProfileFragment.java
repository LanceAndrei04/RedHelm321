package com.example.redhelm321;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.service.controls.actions.FloatAction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

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

public class ProfileFragment extends Fragment {
    private TextView nameTextView, emailTextView, contactTextView;
    private ImageView iv_profileImageView;
    private FloatingActionButton fbtn_editImageButton;
    private TextInputEditText et_nameEditText, et_emailEditText, et_contactNumberEditText, et_birthdayEditText, et_bloodTypeEditText, et_addressEditText;
    private Button saveButton;;

    FirebaseAuth mAuth;
    DatabaseManager dbManager;
    String userProfilePath;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        InitiliazeComponent(view);
        loadProfileFromDatabase();

        return view;
    }

    private void loadProfileFromDatabase() {
        userProfilePath = dbManager.getUserProfilePath();
        dbManager.readData(userProfilePath, UserProfile.class, new ReadCallback<UserProfile>() {
            @Override
            public void onSuccess(UserProfile data) {
                updateProfile(data);
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }

    private void InitiliazeComponent(View view) {
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

        // Birthday picker setup
        EditText birthdayEditText = view.findViewById(R.id.birthdayEditText);
        setupBirthdayPicker(birthdayEditText);

        // Save button click listener
        saveButton.setOnClickListener(v -> saveProfileToDatabase());
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
        if(userProfile == null) {
            Log.d("DEBUG_PROFILE", "UserProfile is null");
            return;
        }

        String name = mAuth.getCurrentUser().getDisplayName();
        String email = mAuth.getCurrentUser().getEmail();
        String contact = userProfile.getPhoneNumber();

        // Validate inputs
        if (name.isEmpty() || email.isEmpty() || contact.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update profile header section

        UserProfile.setImageToImageView(getContext(), iv_profileImageView, String.valueOf(mAuth.getCurrentUser().getPhotoUrl()));

        nameTextView.setText(name);
        emailTextView.setText(email);
        contactTextView.setText(contact);

        et_nameEditText.setText(name);
        et_emailEditText.setText(email);
        et_contactNumberEditText.setText(contact);
        et_bloodTypeEditText.setText(userProfile.getBloodType());
        et_addressEditText.setText(userProfile.getAddress());
        et_birthdayEditText.setText(userProfile.getBirthDate());


//        Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
    }



}
