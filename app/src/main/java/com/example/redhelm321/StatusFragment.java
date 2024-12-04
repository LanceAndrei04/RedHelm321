package com.example.redhelm321;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class StatusFragment extends Fragment {
    private ImageView profileImageView, contactImageView;
    private MaterialButton markSafeButton, needHelpButton, sendReportButton;
    private TextInputLayout reportInputLayout;
    private TextInputEditText reportEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_status, container, false);

        // Initialize views
        profileImageView = view.findViewById(R.id.profileImageView);
        contactImageView = view.findViewById(R.id.contactImageView);
        markSafeButton = view.findViewById(R.id.markSafeButton);
        needHelpButton = view.findViewById(R.id.needHelpButton);
        reportInputLayout = view.findViewById(R.id.reportInputLayout);
        reportEditText = view.findViewById(R.id.reportEditText);
        sendReportButton = view.findViewById(R.id.sendReportButton);

        // Set click listeners for status buttons
        markSafeButton.setOnClickListener(v -> {
            profileImageView.setBackground(getResources().getDrawable(R.drawable.circle_border_green, requireContext().getTheme()));
            reportInputLayout.setVisibility(View.GONE);
            sendReportButton.setVisibility(View.GONE);
        });

        needHelpButton.setOnClickListener(v -> {
            profileImageView.setBackground(getResources().getDrawable(R.drawable.circle_border_red, requireContext().getTheme()));
            reportInputLayout.setVisibility(View.VISIBLE);
            sendReportButton.setVisibility(View.VISIBLE);
        });

        // Set click listeners for profile images
        setImageClickListener(profileImageView, "Red Helm", "Safe", "Yesterday");
        setImageClickListener(contactImageView, "BSU ALANGILAN", "Need Help", "Today", "Report detail", "Location detail");

        // Set click listener for send button
        sendReportButton.setOnClickListener(v -> {
            String report = reportEditText.getText().toString().trim();
            if (!report.isEmpty()) {
                Toast.makeText(requireContext(), "Report sent: " + report, Toast.LENGTH_SHORT).show();
                reportInputLayout.setVisibility(View.GONE);
                sendReportButton.setVisibility(View.GONE);
                // Update the report detail in the popup
                setImageClickListener(profileImageView, "Red Helm", "Need Help", "Today", report, "Location detail");
            } else {
                Toast.makeText(requireContext(), "Please enter report details.", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
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