package com.example.mobileapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.mobileapp.models.user;
import com.example.mobileapp.repository.UserRepo;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Activity class managing the advanced edit profile screen.
 * Links to the image picker for custom avatar photo updates,
 * dialog choices for year and GPA adjustments, and writes to UserRepo.
 */
public class editprofile extends AppCompatActivity {

    private EditText etFullName, etUniversity, etDegree;
    private TextView tvYear, tvEditGPA, tvChangePhoto, tvCancel;
    private ImageView imgEditAvatar, btnBack, btnCameraOverlay;
    private Button btnSaveChanges;
    private CardView cardYear, cardGPA;
    private BottomNavigationView bottomNav;

    private UserRepo userRepo;
    private user currentUser;

    private final String[] yearOptions = {
            "Freshman", "Sophomore", "Junior", "Senior", "Graduate"
    };

    private int selectedYearIndex = 2;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_editprofile);

        userRepo = new UserRepo(this);
        currentUser = userRepo.getCurrentUser();

        initViews();
        loadProfileData();
        setupImagePicker();
        setClickListeners();
        setupBottomNavigation();
        NavigationHelper.setupTopBar(this, userRepo);
    }

    private void initViews() {
        etFullName = findViewById(R.id.etFullName);
        etUniversity = findViewById(R.id.etUniversity);
        etDegree = findViewById(R.id.etDegree);

        tvYear = findViewById(R.id.tvYear);
        tvEditGPA = findViewById(R.id.tvEditGPA);
        tvChangePhoto = findViewById(R.id.tvChangePhoto);
        tvCancel = findViewById(R.id.tvCancel);

        imgEditAvatar = findViewById(R.id.imgEditAvatar);
        btnBack = findViewById(R.id.btnBack);
        btnCameraOverlay = findViewById(R.id.btnCameraOverlay);

        btnSaveChanges = findViewById(R.id.btnSaveChanges);

        cardYear = findViewById(R.id.cardYear);
        cardGPA = findViewById(R.id.cardGPA);

        bottomNav = findViewById(R.id.bottomNav);
    }

    private void loadProfileData() {
        if (currentUser != null) {
            etFullName.setText(currentUser.getFullName());
            etUniversity.setText(currentUser.getUniversity());
            etDegree.setText(currentUser.getDegree());
            tvEditGPA.setText(currentUser.getGpa() != null ? currentUser.getGpa() : "0.0");
            
            String currentYear = currentUser.getYear();
            tvYear.setText(currentYear != null ? currentYear : yearOptions[selectedYearIndex]);
            
            // Sync selectedYearIndex
            if (currentYear != null) {
                for (int i = 0; i < yearOptions.length; i++) {
                    if (yearOptions[i].equals(currentYear)) {
                        selectedYearIndex = i;
                        break;
                    }
                }
            }
        }
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null && imgEditAvatar != null) {
                            imgEditAvatar.setImageURI(uri);
                            Toast.makeText(this, "Profile picture selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void setClickListeners() {
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                startActivity(new Intent(editprofile.this, home.class));
                finish();
            });
        }

        tvCancel.setOnClickListener(v -> {
            startActivity(new Intent(editprofile.this, home.class));
            finish();
        });

        imgEditAvatar.setOnClickListener(v -> openImagePicker());
        btnCameraOverlay.setOnClickListener(v -> openImagePicker());
        tvChangePhoto.setOnClickListener(v -> openImagePicker());

        cardYear.setOnClickListener(v -> showYearDialog());
        cardGPA.setOnClickListener(v -> showGPADialog());

        btnSaveChanges.setOnClickListener(v -> handleSave());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void showYearDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Select Year")
                .setSingleChoiceItems(yearOptions, selectedYearIndex, (dialog, which) -> {
                    selectedYearIndex = which;
                    tvYear.setText(yearOptions[which]);
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showGPADialog() {
        final EditText input = new EditText(this);
        input.setText(tvEditGPA.getText().toString());

        new AlertDialog.Builder(this)
                .setTitle("Update GPA")
                .setView(input)
                .setPositiveButton("Save", (dialog, which) -> {
                    String gpa = input.getText().toString().trim();
                    if (!TextUtils.isEmpty(gpa)) {
                        tvEditGPA.setText(gpa);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void handleSave() {
        String name = etFullName.getText().toString().trim();
        String university = etUniversity.getText().toString().trim();
        String degree = etDegree.getText().toString().trim();
        String gpa = tvEditGPA.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            etFullName.setError("Required");
            return;
        }
        if (TextUtils.isEmpty(university)) {
            etUniversity.setError("Required");
            return;
        }
        if (TextUtils.isEmpty(degree)) {
            etDegree.setError("Required");
            return;
        }

        userRepo.updateUserProfile(name, university, degree, yearOptions[selectedYearIndex], gpa);
        Toast.makeText(this, "Profile Saved Successfully", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(editprofile.this, home.class);
        startActivity(intent);
        finish();
    }

    private void setupBottomNavigation() {
        NavigationHelper.setupBottomNav(this, R.id.profile);
    }
}