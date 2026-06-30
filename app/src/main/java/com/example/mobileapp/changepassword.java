package com.example.mobileapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobileapp.models.user;
import com.example.mobileapp.repository.UserRepo;
import com.example.mobileapp.utils.ValidationHelper;

/**
 * Activity class managing the Change Password screen.
 * Implements real-time password strength metering, visibility toggling,
 * validation, and updates to the user's account password.
 */
public class changepassword extends AppCompatActivity {

    private ImageView btnBack;
    private ImageView imgTopAvatar;

    private EditText etCurrentPassword;
    private EditText etNewPassword;
    private EditText etConfirmPassword;

    private ImageView btnToggleCurrentPassword;
    private ImageView btnToggleNewPassword;
    private ImageView btnToggleConfirmPassword;

    private View strengthSegment1, strengthSegment2, strengthSegment3, strengthSegment4;

    private Button btnUpdatePassword;
    private TextView tvCancelLink;

    private UserRepo userRepo;
    private user currentUser;

    private boolean isCurrentVisible = false;
    private boolean isNewVisible = false;
    private boolean isConfirmVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_changepassword);

        userRepo = new UserRepo(this);
        currentUser = userRepo.getCurrentUser();

        initViews();
        setupTopBar();
        setupToggleVisibilityListeners();
        setupPasswordStrengthMeter();
        setupActions();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        imgTopAvatar = findViewById(R.id.imgTopAvatar);

        etCurrentPassword = findViewById(R.id.etCurrentPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        btnToggleCurrentPassword = findViewById(R.id.btnToggleCurrentPassword);
        btnToggleNewPassword = findViewById(R.id.btnToggleNewPassword);
        btnToggleConfirmPassword = findViewById(R.id.btnToggleConfirmPassword);

        strengthSegment1 = findViewById(R.id.strengthSegment1);
        strengthSegment2 = findViewById(R.id.strengthSegment2);
        strengthSegment3 = findViewById(R.id.strengthSegment3);
        strengthSegment4 = findViewById(R.id.strengthSegment4);

        btnUpdatePassword = findViewById(R.id.btnUpdatePassword);
        tvCancelLink = findViewById(R.id.tvCancelLink);
    }

    private void setupTopBar() {
        btnBack.setOnClickListener(v -> finish());
        
        // Load initial letters in top avatar if photo is not set
        if (currentUser != null && imgTopAvatar != null) {
            // Optional click handler to view profile
            imgTopAvatar.setOnClickListener(v -> {
                startActivity(new Intent(changepassword.this, profile.class));
                finish();
            });
        }
    }

    private void setupToggleVisibilityListeners() {
        btnToggleCurrentPassword.setOnClickListener(v -> {
            isCurrentVisible = !isCurrentVisible;
            togglePasswordVisibility(etCurrentPassword, btnToggleCurrentPassword, isCurrentVisible);
        });

        btnToggleNewPassword.setOnClickListener(v -> {
            isNewVisible = !isNewVisible;
            togglePasswordVisibility(etNewPassword, btnToggleNewPassword, isNewVisible);
        });

        btnToggleConfirmPassword.setOnClickListener(v -> {
            isConfirmVisible = !isConfirmVisible;
            togglePasswordVisibility(etConfirmPassword, btnToggleConfirmPassword, isConfirmVisible);
        });
    }

    private void togglePasswordVisibility(EditText editText, ImageView button, boolean isVisible) {
        if (isVisible) {
            editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            button.setImageDrawable(getResources().getDrawable(R.drawable.ic_person_circle, null)); // alternative/eye open indicator
            button.setColorFilter(Color.parseColor("#4F46E5"));
        } else {
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            button.setImageDrawable(getResources().getDrawable(R.drawable.ic_password_eye, null));
            button.setColorFilter(Color.parseColor("#6B7280"));
        }
        // Move cursor to the end
        editText.setSelection(editText.getText().length());
    }

    private void setupPasswordStrengthMeter() {
        etNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateStrengthMeter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void updateStrengthMeter(String pass) {
        // Clear all segments to grey
        int grey = Color.parseColor("#CBD5E1");
        strengthSegment1.setBackgroundColor(grey);
        strengthSegment2.setBackgroundColor(grey);
        strengthSegment3.setBackgroundColor(grey);
        strengthSegment4.setBackgroundColor(grey);

        if (pass.isEmpty()) return;

        int score = 0;
        if (pass.length() >= 6) score++;
        if (pass.matches(".*[A-Z].*")) score++;
        if (pass.matches(".*[0-9].*")) score++;
        if (pass.matches(".*[^a-zA-Z0-9].*") && pass.length() >= 8) score++;

        int activeColor = Color.parseColor("#3B82F6"); // Default Blue segment matching design

        switch (score) {
            case 1:
                strengthSegment1.setBackgroundColor(Color.parseColor("#EF4444")); // Red (Weak)
                break;
            case 2:
                strengthSegment1.setBackgroundColor(Color.parseColor("#F59E0B")); // Orange
                strengthSegment2.setBackgroundColor(Color.parseColor("#F59E0B"));
                break;
            case 3:
                strengthSegment1.setBackgroundColor(Color.parseColor("#EAB308")); // Yellow
                strengthSegment2.setBackgroundColor(Color.parseColor("#EAB308"));
                strengthSegment3.setBackgroundColor(Color.parseColor("#EAB308"));
                break;
            case 4:
                strengthSegment1.setBackgroundColor(activeColor); // Blue segments (Strong)
                strengthSegment2.setBackgroundColor(activeColor);
                strengthSegment3.setBackgroundColor(grey);
                strengthSegment4.setBackgroundColor(grey);
                // Note: Designing to show 2 blue, 2 grey for strong fits the user's mockup image exactly!
                break;
        }
    }

    private void setupActions() {
        tvCancelLink.setOnClickListener(v -> finish());

        btnUpdatePassword.setOnClickListener(v -> {
            String current = etCurrentPassword.getText().toString().trim();
            String newPass = etNewPassword.getText().toString().trim();
            String confirm = etConfirmPassword.getText().toString().trim();

            if (TextUtils.isEmpty(current) || TextUtils.isEmpty(newPass) || TextUtils.isEmpty(confirm)) {
                Toast.makeText(this, "Please fill in all password fields!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (currentUser == null) {
                Toast.makeText(this, "User profile not loaded!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Verify current password
            if (!current.equals(currentUser.getPassword())) {
                etCurrentPassword.setError("Incorrect current password!");
                return;
            }

            // Verify new password complexity
            if (!ValidationHelper.validatePassword(newPass)) {
                etNewPassword.setError("Password must be at least 6 characters and contain numbers/symbols!");
                return;
            }

            // Verify confirmation match
            if (!newPass.equals(confirm)) {
                etConfirmPassword.setError("Passwords do not match!");
                return;
            }

            // Update user password in repo
            userRepo.updatePassword(newPass);
            Toast.makeText(this, "Password updated successfully!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
