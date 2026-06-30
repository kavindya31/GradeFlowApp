package com.example.mobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mobileapp.models.semester;
import com.example.mobileapp.models.subject;
import com.example.mobileapp.models.user;
import com.example.mobileapp.repository.GPArepo;
import com.example.mobileapp.repository.UserRepo;
import com.example.mobileapp.utils.GPACalculator;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

/**
 * Activity class managing the student's profile summary view screen.
 * Displays academic stats (cumulative CGPA, completed credits), daily motivation quote,
 * top-bar navigation actions, and links to edit profile, change password, or sign out.
 */
public class profile extends AppCompatActivity {

    private ImageView imgTopAvatar;
    private ImageView btnNotification;
    private ImageView btnLogout;

    private ImageView imgProfileAvatar;
    private TextView tvProfileName;
    private TextView tvProfileUniversity;
    private TextView tvProfileDepartment;
    private TextView tvProfileDegree;

    private TextView tvProfileGPA;
    private TextView tvProfileCredits;

    private LinearLayout btnEditProfileLink;
    private LinearLayout rowChangePassword;
    private LinearLayout rowSignOut;

    private BottomNavigationView bottomNav;

    private UserRepo userRepo;
    private GPArepo gpaRepo;
    private user currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_profile);

        userRepo = new UserRepo(this);
        gpaRepo = new GPArepo(this);
        currentUser = userRepo.getCurrentUser();

        initViews();
        loadProfileData();
        setupTopBarActions();
        setupClickListeners();
        setupBottomNavigation();
    }

    private void initViews() {
        imgTopAvatar = findViewById(R.id.imgTopAvatar);
        btnNotification = findViewById(R.id.btnNotification);
        btnLogout = findViewById(R.id.btnLogout);

        imgProfileAvatar = findViewById(R.id.imgProfileAvatar);
        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileUniversity = findViewById(R.id.tvProfileUniversity);
        tvProfileDepartment = findViewById(R.id.tvProfileDepartment);
        tvProfileDegree = findViewById(R.id.tvProfileDegree);

        tvProfileGPA = findViewById(R.id.tvProfileGPA);
        tvProfileCredits = findViewById(R.id.tvProfileCredits);

        btnEditProfileLink = findViewById(R.id.btnEditProfileLink);
        rowChangePassword = findViewById(R.id.rowChangePassword);
        rowSignOut = findViewById(R.id.rowSignOut);

        bottomNav = findViewById(R.id.bottomNav);
    }

    private void loadProfileData() {
        if (currentUser == null) return;

        // Populate details
        tvProfileName.setText(currentUser.getFullName());
        tvProfileUniversity.setText(currentUser.getUniversity());
        tvProfileDegree.setText(currentUser.getDegree());
        
        // Use degree field or a default string for department
        tvProfileDepartment.setText("Faculty of " + (currentUser.getDegree().contains("Computer") ? "Engineering & IT" : "Science"));

        // Calculate dynamic Stats from semesters history
        List<semester> semesters = gpaRepo.getAllSemesters();
        double cgpa = 0.0;
        double totalCredits = 0.0;

        if (!semesters.isEmpty()) {
            cgpa = GPACalculator.calculateCGPA(semesters);
            for (semester sem : semesters) {
                for (subject s : sem.getSubjects()) {
                    totalCredits += s.getCredits();
                }
            }
        } else {
            try {
                cgpa = Double.parseDouble(currentUser.getGpa());
            } catch (NumberFormatException ignored) {}
        }

        tvProfileGPA.setText(String.format("%.2f", cgpa));
        tvProfileCredits.setText(String.format("%.0f", totalCredits));
    }

    private void setupTopBarActions() {
        if (btnNotification != null) {
            btnNotification.setOnClickListener(v -> {
                startActivity(new Intent(profile.this, notification.class));
            });
        }

        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> performSignOut());
        }
    }

    private void setupClickListeners() {
        // Edit Profile Button -> redirects to editprofile.java
        btnEditProfileLink.setOnClickListener(v -> {
            startActivity(new Intent(profile.this, editprofile.class));
        });

        // Change Password Row -> redirects to changepassword.java
        rowChangePassword.setOnClickListener(v -> {
            startActivity(new Intent(profile.this, changepassword.class));
        });

        // Sign Out Row -> triggers user signout
        rowSignOut.setOnClickListener(v -> performSignOut());
    }

    private void performSignOut() {
        userRepo.logoutUser();
        Toast.makeText(profile.this, "Signed out successfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(profile.this, login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setupBottomNavigation() {
        bottomNav.setSelectedItemId(R.id.profile);
        bottomNav.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.home) {
                    startActivity(new Intent(profile.this, home.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                } else if (id == R.id.planner) {
                    startActivity(new Intent(profile.this, planner.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                } else if (id == R.id.calculator) {
                    startActivity(new Intent(profile.this, gpa.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                } else if (id == R.id.ai) {
                    startActivity(new Intent(profile.this, AI.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                } else if (id == R.id.profile) {
                    return true;
                }
                return false;
            }
        });
    }
}