package com.example.mobileapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.mobileapp.models.semester;
import com.example.mobileapp.models.subject;
import com.example.mobileapp.repository.GPArepo;
import com.example.mobileapp.repository.UserRepo;
import com.example.mobileapp.utils.SubjectPriorityManager;
import com.example.mobileapp.utils.SubjectPriorityManager.PrioritizedSubject;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity class managing the Subject Prioritization screen.
 * Leverages the SubjectPriorityManager to sort the student's courses dynamically,
 * generating a visual recommended study priority list.
 */
public class prioritice extends AppCompatActivity {

    private LinearLayout priorityContainer;
    private BottomNavigationView bottomNav;
    
    private ImageView imgTopAvatar;
    private ImageView btnNotification;
    private ImageView btnLogout;
    
    private GPArepo gpaRepo;
    private UserRepo userRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        
        // Corrected layout: loads subject prioritization layout activity_prioritice.xml
        setContentView(R.layout.activity_prioritice);

        gpaRepo = new GPArepo(this);
        userRepo = new UserRepo(this);

        initViews();
        setupTopBarActions();
        displayPrioritizedSubjects();
        setupBottomNavigation();
    }

    private void initViews() {
        priorityContainer = findViewById(R.id.priorityContainer);
        bottomNav = findViewById(R.id.bottomNav);
        
        imgTopAvatar = findViewById(R.id.imgTopAvatar);
        btnNotification = findViewById(R.id.btnNotification);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void setupTopBarActions() {
        if (imgTopAvatar != null) {
            imgTopAvatar.setOnClickListener(v -> {
                startActivity(new Intent(prioritice.this, profile.class));
            });
        }

        if (btnNotification != null) {
            btnNotification.setOnClickListener(v -> {
                startActivity(new Intent(prioritice.this, notification.class));
            });
        }

        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                userRepo.logoutUser();
                Toast.makeText(prioritice.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(prioritice.this, login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }
    }

    /**
     * Loads saved subjects from GPA history and dynamically displays their prioritization.
     * Falls back to static XML mockups if no semesters/subjects are present yet.
     */
    private void displayPrioritizedSubjects() {
        if (priorityContainer == null) return;

        List<semester> semesters = gpaRepo.getAllSemesters();
        List<subject> allSubjects = new ArrayList<>();
        
        for (semester sem : semesters) {
            allSubjects.addAll(sem.getSubjects());
        }

        if (allSubjects.isEmpty()) {
            // No real data yet: keep the static placeholder cards defined in activity_prioritice.xml
            return;
        }

        // Run priority algorithm
        List<PrioritizedSubject> prioritizedList = SubjectPriorityManager.prioritizeSubjects(allSubjects);

        // Remove the static placeholder cards (child indices 2 to 5)
        // Keep Child 0 (Title), Child 1 (Subtitle), and Child 6 (Bottom Tip Banner)
        // Since indices shift as we remove, we can remove the middle cards in a loop
        int staticCardsCount = 4;
        for (int i = 0; i < staticCardsCount; i++) {
            if (priorityContainer.getChildCount() > 3) {
                priorityContainer.removeViewAt(2);
            }
        }

        // Insert new prioritized subject cards dynamically
        int indexToInsert = 2;
        for (int i = 0; i < prioritizedList.size(); i++) {
            PrioritizedSubject ps = prioritizedList.get(i);
            CardView card = createPriorityCard(ps, i + 1);
            priorityContainer.addView(card, indexToInsert++);
        }
    }

    /**
     * Helper to dynamically generate a prioritized subject card matching activity_prioritice.xml styles.
     */
    private CardView createPriorityCard(PrioritizedSubject ps, int rank) {
        CardView card = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.bottomMargin = dpToPx(14);
        card.setLayoutParams(cardParams);
        card.setRadius(dpToPx(18));
        card.setCardElevation(dpToPx(3));
        card.setCardBackgroundColor(Color.WHITE);

        LinearLayout horizontalLayout = new LinearLayout(this);
        horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
        horizontalLayout.setGravity(Gravity.CENTER_VERTICAL);
        horizontalLayout.setPadding(dpToPx(18), dpToPx(18), dpToPx(18), dpToPx(18));
        card.addView(horizontalLayout);

        // Style parameters depending on priority level
        int colorRes;
        int backgroundDrawable;
        switch (ps.getPriorityLevel()) {
            case "Very High":
                colorRes = Color.parseColor("#E53935"); // Red
                backgroundDrawable = R.drawable.circle_red_light_bg;
                break;
            case "High":
                colorRes = Color.parseColor("#F57C00"); // Orange
                backgroundDrawable = R.drawable.circle_orange_light_bg;
                break;
            case "Medium":
                colorRes = Color.parseColor("#F9A825"); // Yellow
                backgroundDrawable = R.drawable.circle_yellow_light_bg;
                break;
            case "Low":
            default:
                colorRes = Color.parseColor("#1A52D4"); // Blue
                backgroundDrawable = R.drawable.circle_blue_light_bg;
                break;
        }

        // 1. Rank Badge
        TextView tvBadge = new TextView(this);
        LinearLayout.LayoutParams badgeParams = new LinearLayout.LayoutParams(dpToPx(44), dpToPx(44));
        tvBadge.setLayoutParams(badgeParams);
        tvBadge.setText(String.valueOf(rank));
        tvBadge.setTextSize(18f);
        tvBadge.setTypeface(null, android.graphics.Typeface.BOLD);
        tvBadge.setTextColor(colorRes);
        tvBadge.setGravity(Gravity.CENTER);
        tvBadge.setBackgroundResource(backgroundDrawable);
        horizontalLayout.addView(tvBadge);

        // 2. Subject Info Layout
        LinearLayout infoLayout = new LinearLayout(this);
        infoLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams infoParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        infoParams.setMargins(dpToPx(14), 0, 0, 0);
        infoLayout.setLayoutParams(infoParams);

        TextView tvSubjectName = new TextView(this);
        tvSubjectName.setText(ps.getSubject().getSubjectName());
        tvSubjectName.setTextSize(16f);
        tvSubjectName.setTypeface(null, android.graphics.Typeface.BOLD);
        tvSubjectName.setTextColor(Color.parseColor("#111111"));
        infoLayout.addView(tvSubjectName);

        TextView tvPriority = new TextView(this);
        tvPriority.setText("Priority: " + ps.getPriorityLevel());
        tvPriority.setTextSize(13f);
        tvPriority.setTypeface(null, android.graphics.Typeface.BOLD);
        tvPriority.setTextColor(colorRes);
        LinearLayout.LayoutParams priorityParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        priorityParams.topMargin = dpToPx(4);
        tvPriority.setLayoutParams(priorityParams);
        infoLayout.addView(tvPriority);
        
        horizontalLayout.addView(infoLayout);

        // 3. Reason Layout
        LinearLayout reasonLayout = new LinearLayout(this);
        reasonLayout.setOrientation(LinearLayout.VERTICAL);
        reasonLayout.setGravity(Gravity.END);
        LinearLayout.LayoutParams reasonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        reasonLayout.setLayoutParams(reasonParams);

        TextView tvReasonLabel = new TextView(this);
        tvReasonLabel.setText("Reason");
        tvReasonLabel.setTextSize(12f);
        tvReasonLabel.setTextColor(Color.parseColor("#AAAAAA"));
        reasonLayout.addView(tvReasonLabel);

        TextView tvReasonVal = new TextView(this);
        tvReasonVal.setText(ps.getReason());
        tvReasonVal.setTextSize(13f);
        tvReasonVal.setTypeface(null, android.graphics.Typeface.BOLD);
        tvReasonVal.setTextColor(colorRes);
        tvReasonVal.setGravity(Gravity.END);
        LinearLayout.LayoutParams valParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        valParams.topMargin = dpToPx(2);
        tvReasonVal.setLayoutParams(valParams);
        reasonLayout.addView(tvReasonVal);

        horizontalLayout.addView(reasonLayout);

        return card;
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private void setupBottomNavigation() {
        bottomNav.setSelectedItemId(R.id.home); // Under 'home' tab
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.home) {
                startActivity(new Intent(this, home.class));
                overridePendingTransition(0, 0);
                finish();
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