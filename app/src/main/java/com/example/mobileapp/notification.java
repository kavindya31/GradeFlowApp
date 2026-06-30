package com.example.mobileapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
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
import androidx.cardview.widget.CardView;

import com.example.mobileapp.models.NotificationItem;
import com.example.mobileapp.repository.notifyRepo;
import com.example.mobileapp.repository.UserRepo;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity class managing the Notifications Center screen.
 * Implements local notification loading, marking read status, and multi-criteria spinner filtering.
 */
public class notification extends AppCompatActivity {

    private LinearLayout notificationContainer;
    private Button btnMarkAllRead;
    private Spinner spinnerDateRange;
    private Spinner spinnerStatusType;
    private ImageView imgTopAvatar;
    private ImageView btnNotification;
    private ImageView btnLogout;
    private BottomNavigationView bottomNav;

    private notifyRepo notificationRepository;
    private UserRepo userRepo;
    private List<NotificationItem> currentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        
        setContentView(R.layout.activity_notification);

        notificationRepository = new notifyRepo(this);
        userRepo = new UserRepo(this);

        initViews();
        setupMarkAllRead();
        setupSpinners();
        loadAndDisplayNotifications();
        setupTopBarActions();
        setupBottomNavigation();
    }

    private void initViews() {
        notificationContainer = findViewById(R.id.notificationContainer);
        btnMarkAllRead         = findViewById(R.id.btnMarkAllRead);
        spinnerDateRange       = findViewById(R.id.spinnerDateRange);
        spinnerStatusType      = findViewById(R.id.spinnerStatusType);
        imgTopAvatar           = findViewById(R.id.imgTopAvatar);
        btnNotification        = findViewById(R.id.btnNotification);
        btnLogout              = findViewById(R.id.btnLogout);
        bottomNav              = findViewById(R.id.bottomNav);
    }

    private void setupMarkAllRead() {
        if (btnMarkAllRead == null) return;
        btnMarkAllRead.setOnClickListener(v -> {
            notificationRepository.markAllAsRead();
            Toast.makeText(this, "All notifications marked as read", Toast.LENGTH_SHORT).show();
            loadAndDisplayNotifications();
        });
    }

    private void setupSpinners() {
        if (spinnerDateRange != null) {
            spinnerDateRange.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    loadAndDisplayNotifications();
                }
                @Override public void onNothingSelected(AdapterView<?> parent) {}
            });
        }

        if (spinnerStatusType != null) {
            spinnerStatusType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    loadAndDisplayNotifications();
                }
                @Override public void onNothingSelected(AdapterView<?> parent) {}
            });
        }
    }

    /**
     * Filters notifications based on spinner selections and dynamically generates CardViews.
     */
    private void loadAndDisplayNotifications() {
        if (notificationContainer == null) return;

        List<NotificationItem> all = notificationRepository.getAllNotifications();
        currentList.clear();

        String dateFilter = spinnerDateRange != null ? spinnerDateRange.getSelectedItem().toString() : "All time";
        String statusFilter = spinnerStatusType != null ? spinnerStatusType.getSelectedItem().toString() : "All";

        for (NotificationItem item : all) {
            // Apply status filter
            if ("Unread".equals(statusFilter) && item.isRead()) continue;
            if ("Read".equals(statusFilter) && !item.isRead()) continue;

            // Apply date range filter (simulation)
            if ("Today".equals(dateFilter) && !item.getTimeString().contains("hour") && !item.getTimeString().contains("minute")) {
                continue;
            }
            if ("Yesterday".equals(dateFilter) && !item.getTimeString().equals("Yesterday")) {
                continue;
            }

            currentList.add(item);
        }

        // Clean dynamically added children (everything starting from index 4: after Title, Subtitle, Mark All Read Button, and Filter Row)
        while (notificationContainer.getChildCount() > 4) {
            notificationContainer.removeViewAt(4);
        }

        if (currentList.isEmpty()) {
            // Render an Empty Placeholder Card
            CardView emptyCard = createPlaceholderCard("No notifications found matching these filters.");
            notificationContainer.addView(emptyCard);
            return;
        }

        // Build list cards programmatically
        for (NotificationItem item : currentList) {
            CardView card = createNotificationCard(item);
            notificationContainer.addView(card);
        }
    }

    private CardView createNotificationCard(NotificationItem item) {
        CardView card = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.bottomMargin = dpToPx(12);
        card.setLayoutParams(cardParams);
        card.setRadius(dpToPx(18));
        card.setCardElevation(dpToPx(1));
        card.setCardBackgroundColor(Color.WHITE);

        LinearLayout cardLayout = new LinearLayout(this);
        cardLayout.setOrientation(LinearLayout.HORIZONTAL);
        cardLayout.setGravity(Gravity.CENTER_VERTICAL);
        card.addView(cardLayout);

        // 1. Left border color depending on type
        View colorBar = new View(this);
        int color;
        switch (item.getType()) {
            case "Alert":
                color = Color.parseColor("#E53935"); // Red
                break;
            case "Grade":
                color = Color.parseColor("#1A52D4"); // Blue
                break;
            case "Reminder":
            default:
                color = Color.parseColor("#7B3FF8"); // Purple
                break;
        }
        LinearLayout.LayoutParams barParams = new LinearLayout.LayoutParams(dpToPx(6), LinearLayout.LayoutParams.MATCH_PARENT);
        colorBar.setLayoutParams(barParams);
        colorBar.setBackgroundColor(color);
        cardLayout.addView(colorBar);

        // 2. Info content layout
        LinearLayout infoLayout = new LinearLayout(this);
        infoLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams infoParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        infoParams.setMargins(dpToPx(14), dpToPx(14), dpToPx(14), dpToPx(14));
        infoLayout.setLayoutParams(infoParams);

        TextView tvTitle = new TextView(this);
        tvTitle.setText(item.getTitle());
        tvTitle.setTextSize(15f);
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        tvTitle.setTextColor(Color.parseColor("#111111"));
        infoLayout.addView(tvTitle);

        TextView tvDesc = new TextView(this);
        tvDesc.setText(item.getDescription());
        tvDesc.setTextSize(13f);
        tvDesc.setTextColor(Color.parseColor("#666666"));
        LinearLayout.LayoutParams descParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        descParams.topMargin = dpToPx(4);
        tvDesc.setLayoutParams(descParams);
        infoLayout.addView(tvDesc);

        TextView tvTime = new TextView(this);
        tvTime.setText(item.getTimeString());
        tvTime.setTextSize(11f);
        tvTime.setTextColor(Color.parseColor("#888888"));
        LinearLayout.LayoutParams timeParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        timeParams.topMargin = dpToPx(6);
        tvTime.setLayoutParams(timeParams);
        infoLayout.addView(tvTime);

        cardLayout.addView(infoLayout);

        // 3. Unread indicator dot
        if (!item.isRead()) {
            View dot = new View(this);
            LinearLayout.LayoutParams dotParams = new LinearLayout.LayoutParams(dpToPx(10), dpToPx(10));
            dotParams.setMargins(0, 0, dpToPx(16), 0);
            dot.setLayoutParams(dotParams);
            
            GradientDrawable shape = new GradientDrawable();
            shape.setShape(GradientDrawable.OVAL);
            shape.setColor(Color.parseColor("#1A52D4"));
            dot.setBackground(shape);
            
            cardLayout.addView(dot);
        }

        // Click to mark single item read
        card.setOnClickListener(v -> {
            if (!item.isRead()) {
                item.setRead(true);
                notificationRepository.saveNotifications(notificationRepository.getAllNotifications());
                loadAndDisplayNotifications();
                Toast.makeText(this, "Notification marked as read", Toast.LENGTH_SHORT).show();
            }
        });

        return card;
    }

    private CardView createPlaceholderCard(String text) {
        CardView card = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.bottomMargin = dpToPx(12);
        card.setLayoutParams(cardParams);
        card.setRadius(dpToPx(18));
        card.setCardElevation(dpToPx(1));
        card.setCardBackgroundColor(Color.WHITE);

        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(14f);
        tv.setTextColor(Color.parseColor("#888888"));
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(dpToPx(24), dpToPx(24), dpToPx(24), dpToPx(24));
        card.addView(tv);

        return card;
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

        // Notification icon - already on this page, no action needed
        // but keep the binding for consistency

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
        bottomNav.setSelectedItemId(R.id.home); // Under 'home' top bar trigger
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