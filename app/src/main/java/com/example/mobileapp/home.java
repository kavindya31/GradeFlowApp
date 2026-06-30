package com.example.mobileapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.mobileapp.models.semester;
import com.example.mobileapp.models.subject;
import com.example.mobileapp.models.user;
import com.example.mobileapp.repository.GPArepo;
import com.example.mobileapp.repository.UserRepo;
import com.example.mobileapp.utils.GPACalculator;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

/**
 * Main dashboard / home activity for the GradeFlow application.
 * Manages stats displays (cumulative CGPA, credit progress) and quick action navigation.
 */
public class home extends AppCompatActivity {

    private CardView cardGpaCalculator;
    private CardView cardStudyPlanner;
    private CardView cardAiAssistant;
    private CardView cardSubjectPriority;
    
    private TextView tvCgpaValue;
    private TextView tvCreditsProgress;
    private TextView tvCreditsPercent;
    private ProgressBar pbCreditsProgress;
    
    private ImageView imgTopAvatar;
    private ImageView btnNotification;
    private ImageView btnLogout;
    
    private BottomNavigationView bottomNav;
    
    private UserRepo userRepo;
    private GPArepo gpaRepo;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        
        // Corrected layout: loads dashboard layout activity_home.xml
        setContentView(R.layout.activity_home);

        userRepo = new UserRepo(this);
        gpaRepo = new GPArepo(this);

        initViews();
        displayDashboardStats();
        setupQuickActions();
        setupTopBarActions();
        setupBottomNavigation();
    }

    private void initViews() {
        cardGpaCalculator   = findViewById(R.id.cardGpaCalculator);
        cardStudyPlanner    = findViewById(R.id.cardStudyPlanner);
        cardAiAssistant     = findViewById(R.id.cardAiAssistant);
        cardSubjectPriority  = findViewById(R.id.cardSubjectPriority);
        
        tvCgpaValue         = findViewById(R.id.tvCgpaValue);
        tvCreditsProgress   = findViewById(R.id.tvCreditsProgress);
        tvCreditsPercent    = findViewById(R.id.tvCreditsPercent);
        pbCreditsProgress   = findViewById(R.id.pbCreditsProgress);
        
        imgTopAvatar        = findViewById(R.id.imgTopAvatar);
        btnNotification     = findViewById(R.id.btnNotification);
        btnLogout           = findViewById(R.id.btnLogout);
        
        bottomNav           = findViewById(R.id.bottomNav);
    }

    /**
     * Dynamically calculates and displays user academic statistics.
     */
    private void displayDashboardStats() {
        List<semester> semesters = gpaRepo.getAllSemesters();
        user currentUser = userRepo.getCurrentUser();

        // Calculate CGPA dynamically
        double cgpa = 0.0;
        double totalCreditsEarned = 0.0;
        
        if (!semesters.isEmpty()) {
            cgpa = GPACalculator.calculateCGPA(semesters);
            
            // Calculate total credits accumulated
            for (semester sem : semesters) {
                for (subject s : sem.getSubjects()) {
                    totalCreditsEarned += s.getCredits();
                }
            }
        } else if (currentUser != null && currentUser.getGpa() != null) {
            try {
                cgpa = Double.parseDouble(currentUser.getGpa());
            } catch (NumberFormatException ignored) {}
        }

        // Set CGPA text
        if (tvCgpaValue != null) {
            tvCgpaValue.setText(String.format("%.2f", cgpa));
        }

        // Set Credit Progress text & progress bar
        int targetCredits = 120; // standard graduation target
        if (tvCreditsProgress != null) {
            tvCreditsProgress.setText(String.format("%.0f / %d", totalCreditsEarned, targetCredits));
        }

        if (pbCreditsProgress != null) {
            pbCreditsProgress.setMax(targetCredits);
            pbCreditsProgress.setProgress((int) totalCreditsEarned);
        }

        if (tvCreditsPercent != null) {
            double percent = (totalCreditsEarned / targetCredits) * 100;
            tvCreditsPercent.setText(String.format("%.0f%% complete", percent));
        }
    }

    private void setupQuickActions() {
        cardGpaCalculator.setOnClickListener(v -> {
            startActivity(new Intent(home.this, gpa.class));
        });

        cardStudyPlanner.setOnClickListener(v -> {
            startActivity(new Intent(home.this, planner.class));
        });

        cardAiAssistant.setOnClickListener(v -> {
            startActivity(new Intent(home.this, AI.class));
        });

        cardSubjectPriority.setOnClickListener(v -> {
            startActivity(new Intent(home.this, prioritice.class));
        });
    }

    private void setupTopBarActions() {
        if (imgTopAvatar != null) {
            imgTopAvatar.setOnClickListener(v -> {
                startActivity(new Intent(home.this, profile.class));
            });
        }

        if (btnNotification != null) {
            btnNotification.setOnClickListener(v -> {
                startActivity(new Intent(home.this, notification.class));
            });
        }

        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                userRepo.logoutUser();
                Toast.makeText(home.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(home.this, login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }
    }

    private void setupBottomNavigation() {
        bottomNav.setSelectedItemId(R.id.home);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.home) {
                // already here
                return true;
            } else if (id == R.id.planner) {
                startActivity(new Intent(this, planner.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.calculator) {
                startActivity(new Intent(this, gpa.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.ai) {
                startActivity(new Intent(this, AI.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.profile) {
                startActivity(new Intent(this, profile.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });
    }
}