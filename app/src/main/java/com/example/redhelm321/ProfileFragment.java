package com.example.redhelm321;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

public class ProfileFragment extends Fragment {
    private TextView nameTextView, emailTextView, contactTextView;
    private TextInputEditText nameEditText, emailEditText, contactNumberEditText;
    private Button saveButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize TextViews from header section
        nameTextView = view.findViewById(R.id.nameTextView);
        emailTextView = view.findViewById(R.id.emailTextView);
        contactTextView = view.findViewById(R.id.contactTextView);

        // Initialize EditTexts from input fields
        nameEditText = view.findViewById(R.id.nameEditText);
        emailEditText = view.findViewById(R.id.emailEditText);
        contactNumberEditText = view.findViewById(R.id.contactNumberEditText);

        // Initialize Save Button
        saveButton = view.findViewById(R.id.saveButton);

        // Birthday picker setup
        EditText birthdayEditText = view.findViewById(R.id.birthdayEditText);
        setupBirthdayPicker(birthdayEditText);

        // Save button click listener
        saveButton.setOnClickListener(v -> updateProfile());

        return view;
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

    private void updateProfile() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String contact = contactNumberEditText.getText().toString().trim();

        // Validate inputs
        if (name.isEmpty() || email.isEmpty() || contact.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update profile header section
        nameTextView.setText(name);
        emailTextView.setText(email);
        contactTextView.setText(contact);

        Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
    }
}
