package com.example.redhelm321.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.redhelm321.MainActivity;
import com.example.redhelm321.R;
import com.example.redhelm321.database.DatabaseCallback;
import com.example.redhelm321.database.DatabaseManager;
import com.example.redhelm321.database.ReadCallback;
import com.example.redhelm321.profile.UserProfile;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    GoogleSignInClient googleSignInClient ;

    private DatabaseReference usersRef;
    private DatabaseReference FBDB_profilesRef;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    DatabaseManager dbManager;

    MaterialButton btn_loginButton, btn_google_signIn_button;
    EditText et_email_input, et_password_input;
    private String userProfilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        InitializeComponents();
        InitializeAuthentication();
    }

    private void HandleUserAuthentication() {
        if (mAuth.getCurrentUser() != null) {
            Toast.makeText(this, "You are already logged in.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void InitializeComponents() {
        et_email_input = findViewById(R.id.et_email_input);
        et_password_input = findViewById(R.id.et_password_input);
        btn_google_signIn_button = findViewById(R.id.btn_google_signin_button);
        btn_google_signIn_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_google_signIn_button_OnClick(view);
            }
        });

        btn_loginButton = findViewById(R.id.btn_login_button);
        btn_loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_login_button_OnClick(v);
            }
        });
    }
    private void InitializeAuthentication() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
        googleSignInClient.signOut();

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        usersRef = firebaseDatabase.getReference("users");
        FBDB_profilesRef = firebaseDatabase.getReference("profiles");

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        int resultCode = result.getResultCode();
                        Intent data = result.getData();


                        if(resultCode == RESULT_OK) {
                            handleSigningInWithGoogle(data);
                        }
                        else {
                            Toast.makeText(
                                    LoginActivity.this,
                                    "Google Sign In Cancelled",
                                    Toast.LENGTH_SHORT
                            ).show();
//                            pb_login.setVisibility(View.INVISIBLE);
                        }


                    }
                }
        );
    }

    private void handleSigningInWithGoogle(Intent data) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

        try {
            GoogleSignInAccount account = task.getResult(Exception.class);

            if(account != null) {

                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        Toast.makeText(LoginActivity.this, "Successfully signed in with Google!", Toast.LENGTH_SHORT).show();
                        FirebaseUser user = mAuth.getCurrentUser();

                        dbManager = DatabaseManager.getInstance(user.getUid());
                        userProfilePath = dbManager.getUserProfilePath(user.getUid());

                        dbManager.readData(userProfilePath, UserProfile.class, new ReadCallback<UserProfile>() {
                            @Override
                            public void onSuccess(UserProfile data) {
                                openMainScreen();
                            }

                            @Override
                            public void onFailure(Exception e) {
                                ArrayList<String> initFriends = new ArrayList<>();
                                initFriends.add("");
                                UserProfile newUserProfile = new UserProfile.Builder()
                                        .setName(user.getDisplayName())
                                        .setPhoneNumber(user.getPhoneNumber())
                                        .setEmail(user.getEmail())
                                        .setStatus("Safe")
                                        .setFriendIDList(initFriends)
                                        .setUserImgLink(String.valueOf(user.getPhotoUrl()))
                                        .build();

                                dbManager.saveData(userProfilePath, newUserProfile, new DatabaseCallback() {
                                    @Override
                                    public void onSuccess() {
                                        openMainScreen();
                                    }

                                    @Override
                                    public void onFailure(Exception e) {

                                    }
                                });
                            }
                        });


                    }
                });
            }

        }

        catch (Exception e) {
//            Log.e(TAG, e.toString());
            Toast.makeText(this, "An error happened while getting google account!", Toast.LENGTH_SHORT).show();
        }

    }
    private void openMainScreen() {
        finish();
    }

    private void btn_google_signIn_button_OnClick(View view) {
        Intent openGoogleSignIn = googleSignInClient.getSignInIntent();
        activityResultLauncher.launch(openGoogleSignIn);
    }
    private void btn_login_button_OnClick(View v) {
        final String email = et_email_input.getText().toString().trim();
        final  String password = et_password_input.getText().toString().trim();

        FormValidation.FormValidationResult loginFormResult = FormValidation.isLoginFormValid(
                email,
                password
        );

        String message = "";

        switch (loginFormResult) {
            case INVALID_EMAIL:
                message = FormValidation.WarningMessage.INVALID_EMAIL_WARNING.getMessage();
                break;
            case INVALID_PASSWORD:
                message = FormValidation.WarningMessage.INVALID_PASSWORD_WARNING.getMessage();
                break;
            case INPUT_NULL:
                message = FormValidation.WarningMessage.INPUT_NULL_WARNING.getMessage();
                break;
        }

        if(!TextUtils.isEmpty(message)) {
            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT)
                    .show();
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();

                            dbManager.readData(userProfilePath, UserProfile.class, new ReadCallback<UserProfile>() {
                                @Override
                                public void onSuccess(UserProfile data) {
                                    openMainScreen();
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    ArrayList<String> initFriends = new ArrayList<>();
                                    initFriends.add("");
                                    UserProfile newUserProfile = new UserProfile.Builder()
                                            .setName(user.getDisplayName())
                                            .setPhoneNumber(user.getPhoneNumber())
                                            .setEmail(user.getEmail())
                                            .setStatus("Safe")
                                            .setFriendIDList(initFriends)
                                            .setUserImgLink(String.valueOf(user.getPhotoUrl()))
                                            .build();

                                    dbManager.saveData(userProfilePath, newUserProfile, new DatabaseCallback() {
                                        @Override
                                        public void onSuccess() {
                                            openMainScreen();
                                        }

                                        @Override
                                        public void onFailure(Exception e) {

                                        }
                                    });
                                }
                            });

                        } else {
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}
