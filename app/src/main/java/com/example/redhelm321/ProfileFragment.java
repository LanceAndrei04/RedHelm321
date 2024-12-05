package com.example.redhelm321;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ActionProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;
import java.util.Calendar;

public class ProfileFragment extends Fragment implements ActionProvider.VisibilityListener {
    private TextView nameTextView, emailTextView;
    private ImageView iv_profileImageView;
    private FloatingActionButton fbtn_editImageButton;
    private ListView contactListView;
    private EditText contactInputEditText;
    private ImageButton addContactButton;
    private TextInputEditText et_nameEditText, et_emailEditText, et_contactNumberEditText, et_birthdayEditText, et_bloodTypeEditText, et_addressEditText;
    private Button saveButton, logoutButton, viewContactsBtn, shareProfileBtn;
    private LinearLayout profileForm, contactListLayout;
    private ArrayList<String> contacts;
    private ArrayAdapter<String> contactAdapter;

    FirebaseAuth mAuth;
    DatabaseManager dbManager;
    String userProfilePath;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        InitializeComponent(view);
        loadProfileFromDatabase();

        contacts = new ArrayList<>();
        contactAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, contacts);
        contactListView.setAdapter(contactAdapter);

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
        viewContactsBtn = view.findViewById(R.id.viewContactsBtn);
        shareProfileBtn = view.findViewById(R.id.ShareProfileBtn);

        //Initialize linear layout
        profileForm = view.findViewById(R.id.profileForm);
        contactListLayout = view.findViewById(R.id.contactListLayout);

        //Initialize Add contact
        contactListView = view.findViewById(R.id.contactListView);
        contactInputEditText = view.findViewById(R.id.contactInputEditText);
        addContactButton = view.findViewById(R.id.addContactButton);

        // Birthday picker setup
        EditText birthdayEditText = view.findViewById(R.id.birthdayEditText);
        setupBirthdayPicker(birthdayEditText);

        // Save button click listener
        saveButton.setOnClickListener(v -> saveProfileToDatabase());
        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            getActivity().finish();
        });

        viewContactsBtn.setOnClickListener(v -> {
            change_linear_layout();
        });

        addContactButton.setOnClickListener(v -> {
            addContactToList();
        });

        // Set up share profile button click listener
        shareProfileBtn.setOnClickListener(v -> {
            showProfileShareDialog();
        });
    }


    private void addContactToList() {
        String newContact = contactInputEditText.getText().toString().trim(); // Get text from EditText
        if (!newContact.isEmpty()) {
            contacts.add(newContact); // Add new contact to list
            contactAdapter.notifyDataSetChanged(); // Refresh ListView
            contactInputEditText.setText(""); // Clear the EditText
        } else {
            Toast.makeText(getContext(), "Please enter a valid contact!", Toast.LENGTH_SHORT).show();
        }
    }

    private void change_linear_layout() {
        if (profileForm.getVisibility() == View.VISIBLE) {
            viewContactsBtn.setTextColor(getResources().getColor(R.color.white));
            viewContactsBtn.setBackgroundTintList(getResources().getColorStateList(R.color.red_700));

            profileForm.setVisibility(View.GONE);
            contactListLayout.setVisibility(View.VISIBLE);
        } else {
            viewContactsBtn.setTextColor(getResources().getColor(R.color.red_primary));
            viewContactsBtn.setBackgroundTintList(getResources().getColorStateList(R.color.white));

            contactListLayout.setVisibility(View.GONE);
            profileForm.setVisibility(View.VISIBLE);
        }
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

    private void updateProfile(UserProfile profile) {

        try {
            Log.d("DEBUG_UPDATE_PROFILE", "NATAWAG ANG UPDATE PROFILE");
            if(profile == null) {
                Log.d("DEBUG_UPDATE_PROFILE", "UserProfile is null");
                return;
            }

            if(profile.getUserImgLink() != null && !profile.getUserImgLink().isEmpty()) {
                Log.d("DEBUG_UPDATE_PROFILE", "UserProfile is null " + profile.getUserImgLink());
                UserProfile.setImageToImageView(getContext(), iv_profileImageView, profile.getUserImgLink());
            }

            nameTextView.setText(profile.getName());
            emailTextView.setText(mAuth.getCurrentUser().getEmail());

            et_nameEditText.setText(profile.getName());
            et_emailEditText.setText(mAuth.getCurrentUser().getEmail());
            et_contactNumberEditText.setText(profile.getPhoneNumber());
            et_birthdayEditText.setText(profile.getBirthDate());
            et_bloodTypeEditText.setText(profile.getBloodType());
            et_addressEditText.setText(profile.getAddress());
        } catch (Exception e) {
            Log.d("DEBUG_UPDATE_PROFILE", "FAILED ANG UPDATE PROFILE" + e.getMessage());
        }

    }

    private void showProfileShareDialog() {
        // Create and set up the dialog
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_profile_share);

        // Set dialog width to match parent with margins
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        // Initialize views
        ImageView popupProfileImage = dialog.findViewById(R.id.popupProfileImage);
        TextView popupNameText = dialog.findViewById(R.id.popupNameText);
        TextView popupEmailText = dialog.findViewById(R.id.popupEmailText);
        TextView popupAddressText = dialog.findViewById(R.id.popupAddressText);
        TextView popupContactText = dialog.findViewById(R.id.popupContactText);
        TextView popupBirthdayText = dialog.findViewById(R.id.popupBirthdayText);
        ImageView iv_qr = dialog.findViewById(R.id.iv_qr);
        ImageButton closeButton = dialog.findViewById(R.id.closeButton);
        MaterialButton shareButton = dialog.findViewById(R.id.shareButton);

        // Fetch current profile data from database
        userProfilePath = dbManager.getUserProfilePath(mAuth.getCurrentUser().getUid());
        dbManager.readData(userProfilePath, UserProfile.class, new ReadCallback<UserProfile>() {
            @Override
            public void onSuccess(UserProfile profile) {
                // Set data from database
                if (profile.getUserImgLink() != null && !profile.getUserImgLink().isEmpty()) {
                    UserProfile.setImageToImageView(requireContext(), popupProfileImage, profile.getUserImgLink());
                }
                popupNameText.setText(profile.getName());
                popupEmailText.setText(mAuth.getCurrentUser().getEmail());
                popupAddressText.setText(profile.getAddress());
                popupContactText.setText(profile.getPhoneNumber());
                popupBirthdayText.setText(profile.getBirthDate());

                String shareText  = String.format(
                                "Profile Information:\n\n" +
                                "Name: %s\n" +
                                "Email: %s\n" +
                                "Address: %s\n" +
                                "Contact: %s\n" +
                                "Birthday: %s",
                        profile.getName(),
                        profile.getEmail(),
                        profile.getAddress(),
                        profile.getPhoneNumber(),
                        profile.getBirthDate()
                );

                MultiFormatWriter writer = new MultiFormatWriter();
                try {
                    BitMatrix matrix = writer.encode(shareText, BarcodeFormat.QR_CODE, 400, 400);
                    BarcodeEncoder encoder = new BarcodeEncoder();
                    Bitmap bitmap = encoder.createBitmap(matrix);
                    iv_qr.setImageBitmap(bitmap);

                } catch (WriterException e) {
                    throw new RuntimeException(e);
                }

                // Set up share button with database data
                shareButton.setOnClickListener(v -> {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                    startActivity(Intent.createChooser(shareIntent, "Share Profile via"));
                });
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(requireContext(), "Failed to load profile data", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        // Set up close button
        closeButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    @Override
    public void onActionProviderVisibilityChanged(boolean b) {
        Toast.makeText(getContext(), "AYDIWOW", Toast.LENGTH_SHORT).show();
    }
}
