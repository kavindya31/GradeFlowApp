package com.example.mobileapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mobileapp.repository.GPArepo;
import com.example.mobileapp.repository.UserRepo;
import com.example.mobileapp.utils.GPACalculator;
import com.example.mobileapp.models.subject;
import com.example.mobileapp.models.semester;
import com.example.mobileapp.models.user;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class gpa extends AppCompatActivity {

    // Year / Semester selectors
    private Spinner spinnerYear;
    private Spinner spinnerSemester;

    // Subject table container
    private LinearLayout subjectTableContainer;

    // Add subject & calculate buttons
    private TextView  tvAddSubject;
    private Button    btnCalculateGpa;
    private Button    btnCalculateCgpa;

    // Top bar
    private ImageView imgTopAvatar;
    private ImageView btnNotification;
    private ImageView btnLogout;

    // Bottom nav
    private BottomNavigationView bottomNav;

    // In-memory subject list: each entry is { gradePts, credits }
    private final List<float[]> subjectEntries = new ArrayList<>();

    // Grade point values matching the dropdown order in arrays_gpa.xml
    private static final float[] GRADE_POINTS = {4.0f, 3.7f, 3.3f, 3.0f, 2.7f, 2.3f, 2.0f, 1.7f, 1.3f, 1.0f, 0.0f};
    private static final int[]   CREDIT_VALUES = {1, 2, 3, 4, 5};

    private GPArepo gpaRepo;
    private UserRepo userRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpa);

        gpaRepo = new GPArepo(this);
        userRepo = new UserRepo(this);

        initViews();
        setupYearSemesterSpinners();
        setupAddSubject();
        setupCalculateButtons();
        setupTopBarActions();
        setupBottomNavigation();
    }

    @SuppressLint("WrongViewCast")
    private void initViews() {
        spinnerYear            = findViewById(R.id.spinnerYear);
        spinnerSemester        = findViewById(R.id.spinnerSemester);
        subjectTableContainer  = findViewById(R.id.subjectTableContainer);
        tvAddSubject           = findViewById(R.id.tvAddSubject);
        btnCalculateGpa        = findViewById(R.id.btnCalculateGpa);
        btnCalculateCgpa       = findViewById(R.id.btnCalculateCgpa);

        imgTopAvatar           = findViewById(R.id.imgTopAvatar);
        btnNotification        = findViewById(R.id.btnNotification);
        btnLogout              = findViewById(R.id.btnLogout);

        bottomNav              = findViewById(R.id.bottomNav);
    }

    private void setupYearSemesterSpinners() {
        if (spinnerYear != null) {
            ArrayAdapter<CharSequence> yearAdapter = ArrayAdapter.createFromResource(
                    this, R.array.year_options, android.R.layout.simple_spinner_item);
            yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerYear.setAdapter(yearAdapter);
        }

        if (spinnerSemester != null) {
            ArrayAdapter<CharSequence> semAdapter = ArrayAdapter.createFromResource(
                    this, R.array.semester_options, android.R.layout.simple_spinner_item);
            semAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerSemester.setAdapter(semAdapter);
        }
    }

    /**
     * Dynamically inflate a new subject row and add it to the table container.
     */
    private void setupAddSubject() {
        if (tvAddSubject == null) return;
        tvAddSubject.setOnClickListener(v -> addSubjectRow("New Subject", 0, 2));
    }

    private void addSubjectRow(String name, int gradeIndex, int creditIndex) {
        if (subjectTableContainer == null) return;

        // Track entry
        subjectEntries.add(new float[]{GRADE_POINTS[gradeIndex], CREDIT_VALUES[creditIndex]});
        final int rowIndex = subjectEntries.size() - 1;

        // Build row view programmatically
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(android.view.Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        rowParams.bottomMargin = dpToPx(16);
        row.setLayoutParams(rowParams);

        // Subject name
        TextView tvName = new TextView(this);
        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 2f);
        tvName.setLayoutParams(nameParams);
        tvName.setText(name);
        tvName.setTextSize(15f);
        tvName.setTextColor(0xFF111111);

        // Grade spinner
        Spinner spGrade = new Spinner(this);
        LinearLayout.LayoutParams gradeParams = new LinearLayout.LayoutParams(0,
                dpToPx(36), 2f);
        gradeParams.setMarginEnd(dpToPx(8));
        spGrade.setLayoutParams(gradeParams);
        spGrade.setBackground(getResources().getDrawable(R.drawable.grade_dropdown_bg, null));
        ArrayAdapter<CharSequence> gradeAdapter = ArrayAdapter.createFromResource(
                this, R.array.grade_options, android.R.layout.simple_spinner_item);
        gradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGrade.setAdapter(gradeAdapter);
        spGrade.setSelection(gradeIndex);
        spGrade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> p, View v2, int pos, long id) {
                subjectEntries.get(rowIndex)[0] = GRADE_POINTS[pos];
            }
            @Override public void onNothingSelected(AdapterView<?> p) {}
        });

        // Credits spinner
        Spinner spCredits = new Spinner(this);
        LinearLayout.LayoutParams creditParams = new LinearLayout.LayoutParams(0,
                dpToPx(36), 1f);
        spCredits.setLayoutParams(creditParams);
        spCredits.setBackground(getResources().getDrawable(R.drawable.grade_dropdown_bg, null));
        ArrayAdapter<CharSequence> creditAdapter = ArrayAdapter.createFromResource(
                this, R.array.credits_options, android.R.layout.simple_spinner_item);
        creditAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCredits.setAdapter(creditAdapter);
        spCredits.setSelection(Math.max(0, creditIndex - 1)); // offset: credits_options starts at 1
        spCredits.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> p, View v2, int pos, long id) {
                subjectEntries.get(rowIndex)[1] = CREDIT_VALUES[pos];
            }
            @Override public void onNothingSelected(AdapterView<?> p) {}
        });

        row.addView(tvName);
        row.addView(spGrade);
        row.addView(spCredits);

        // Insert before the divider + Add Subject row (last 2 children)
        int insertAt = Math.max(0, subjectTableContainer.getChildCount() - 2);
        subjectTableContainer.addView(row, insertAt);
    }

    private void setupCalculateButtons() {
        if (btnCalculateGpa != null) {
            btnCalculateGpa.setOnClickListener(v -> {
                double gpaValue = calculateGpa();
                
                // Get selected year and semester names
                String selectedYear = spinnerYear.getSelectedItem() != null ? spinnerYear.getSelectedItem().toString() : "Year 1";
                String selectedSem = spinnerSemester.getSelectedItem() != null ? spinnerSemester.getSelectedItem().toString() : "Semester 1";
                
                List<subject> subjects = getSubjectListFromEntries();
                semester currentSem = new semester(selectedSem, selectedYear, subjects, gpaValue);
                
                // Add to database
                gpaRepo.addSemester(currentSem);

                // Update User GPA in UserRepo
                updateUserCumulativeGpa();

                Toast.makeText(this,
                        String.format("Semester GPA: %.2f (Saved to history)", gpaValue),
                        Toast.LENGTH_LONG).show();
            });
        }

        if (btnCalculateCgpa != null) {
            btnCalculateCgpa.setOnClickListener(v -> {
                List<semester> semesters = gpaRepo.getAllSemesters();
                if (semesters.isEmpty()) {
                    Toast.makeText(this, "No saved semester records found. Calculate GPA first.", Toast.LENGTH_LONG).show();
                    return;
                }
                
                double cgpa = GPACalculator.calculateCGPA(semesters);
                Toast.makeText(this,
                        String.format("Cumulative CGPA: %.2f (Across %d semester(s))", cgpa, semesters.size()),
                        Toast.LENGTH_LONG).show();
            });
        }
    }

    private List<subject> getSubjectListFromEntries() {
        List<subject> list = new ArrayList<>();
        for (int i = 0; i < subjectEntries.size(); i++) {
            float[] entry = subjectEntries.get(i);
            list.add(new subject("Subject " + (i + 1), "Grade", entry[0], entry[1]));
        }
        return list;
    }

    private double calculateGpa() {
        return GPACalculator.calculateGPA(getSubjectListFromEntries());
    }

    private void updateUserCumulativeGpa() {
        List<semester> semesters = gpaRepo.getAllSemesters();
        if (!semesters.isEmpty()) {
            double cgpa = GPACalculator.calculateCGPA(semesters);
            user currentUser = userRepo.getCurrentUser();
            if (currentUser != null && currentUser.getFullName() != null && !currentUser.getFullName().isEmpty()) {
                userRepo.updateUserProfile(
                        currentUser.getFullName(),
                        currentUser.getUniversity(),
                        currentUser.getDegree(),
                        currentUser.getYear(),
                        String.format("%.2f", cgpa)
                );
            }
        }
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private void setupTopBarActions() {
        if (imgTopAvatar != null) {
            imgTopAvatar.setOnClickListener(v -> {
                startActivity(new Intent(this, profile.class));
            });
        }

        if (btnNotification != null) {
            btnNotification.setOnClickListener(v -> {
                startActivity(new Intent(this, notification.class));
            });
        }

        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                userRepo.logoutUser();
                Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }
    }

    private void setupBottomNavigation() {
        bottomNav.setSelectedItemId(R.id.calculator);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.home) {
                startActivity(new Intent(this, home.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.planner) {
                startActivity(new Intent(this, planner.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.calculator) {
                // Already in GPA activity; do nothing
                return true;
            } else if (id == R.id.ai) {
                startActivity(new Intent(this, AI.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.profile) {
                startActivity(new Intent(this, profile.class));
                overridePendingTransition(0, 0);
                return true;

            }
            return false;
        });
    }

}