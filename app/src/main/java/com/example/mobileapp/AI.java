package com.example.mobileapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.mobileapp.repository.GPArepo;
import com.example.mobileapp.repository.UserRepo;
import com.example.mobileapp.utils.PromptBuilder;
import com.example.mobileapp.ai.gemini;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AI extends AppCompatActivity {

    private ScrollView    chatScrollView;
    private LinearLayout  chatContainer;
    private EditText      etMessage;
    private TextView      btnSend;
    private TextView      btnMic;
    private ImageView     imgTopAvatar;
    private ImageView     btnNotification;
    private ImageView     btnLogout;
    private BottomNavigationView bottomNav;
    
    private UserRepo userRepo;
    private GPArepo gpaRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        
        setContentView(R.layout.activity_ai);

        userRepo = new UserRepo(this);
        gpaRepo = new GPArepo(this);

        initViews();
        setupSendButton();
        setupMicButton();
        setupTopBarActions();
        setupBottomNavigation();
    }

    @SuppressLint("WrongViewCast")
    private void initViews() {
        chatScrollView = findViewById(R.id.chatScrollView);
        chatContainer  = findViewById(R.id.chatContainer);   // add this ID to your XML's inner LinearLayout
        etMessage      = findViewById(R.id.etMessage);
        btnSend        = findViewById(R.id.btnSend);
        btnMic         = findViewById(R.id.btnMic);
        imgTopAvatar   = findViewById(R.id.imgTopAvatar);
        btnNotification = findViewById(R.id.btnNotification);
        btnLogout      = findViewById(R.id.btnLogout);
        bottomNav      = findViewById(R.id.bottomNav);
    }

    private void setupSendButton() {
        if (btnSend == null) return;
        btnSend.setOnClickListener(v -> sendMessage());
    }

    private void setupMicButton() {
        if (btnMic == null) return;
        btnMic.setOnClickListener(v ->
                Toast.makeText(this, "Voice input coming soon", Toast.LENGTH_SHORT).show());
    }

    private void sendMessage() {
        if (etMessage == null) return;
        String text = etMessage.getText().toString().trim();
        if (TextUtils.isEmpty(text)) return;

        // Add user bubble
        addUserMessage(text);
        etMessage.setText("");

        // Simulate AI response with prompt builder
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Generate structured prompt (useful for print logs / future API connections)
            String promptContext = PromptBuilder.buildStudyRecommendationPrompt(
                    userRepo.getCurrentUser(),
                    gpaRepo.getAllSemesters()
            );
            android.util.Log.d("GradeFlowAI", "Generated Prompt context:\n" + promptContext);

            // Generate simulated response based on user profile and history
            String response = gemini.generateRecommendation(
                    userRepo.getCurrentUser(),
                    gpaRepo.getAllSemesters(),
                    text
            );
            
            addAiMessage(response);
        }, 800);
    }

    private void addUserMessage(String text) {
        if (chatContainer == null) return;

        // Outer row (right-aligned)
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        rowParams.bottomMargin = dpToPx(16);
        row.setLayoutParams(rowParams);

        // Bubble card
        CardView card = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        card.setLayoutParams(cardParams);
        card.setCardBackgroundColor(0xFF1A4FCC);
        card.setRadius(dpToPx(18));
        card.setCardElevation(dpToPx(2));

        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextColor(0xFFFFFFFF);
        tv.setTextSize(15f);
        tv.setPadding(dpToPx(14), dpToPx(12), dpToPx(14), dpToPx(12));
        tv.setLineSpacing(0, 1.4f);
        card.addView(tv);

        // User avatar
        TextView avatar = new TextView(this);
        LinearLayout.LayoutParams avatarParams = new LinearLayout.LayoutParams(dpToPx(40), dpToPx(40));
        avatarParams.setMarginStart(dpToPx(10));
        avatar.setLayoutParams(avatarParams);
        avatar.setBackground(getResources().getDrawable(R.drawable.user_avatar_bg, null));
        avatar.setText("👤");
        avatar.setTextSize(18f);
        avatar.setGravity(Gravity.CENTER);

        row.addView(card);
        row.addView(avatar);

        chatContainer.addView(row);
        scrollToBottom();
    }

    private void addAiMessage(String text) {
        if (chatContainer == null) return;

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.TOP);
        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        rowParams.bottomMargin = dpToPx(16);
        row.setLayoutParams(rowParams);

        // AI avatar
        TextView avatar = new TextView(this);
        LinearLayout.LayoutParams avatarParams = new LinearLayout.LayoutParams(dpToPx(44), dpToPx(44));
        avatarParams.setMarginEnd(dpToPx(10));
        avatarParams.topMargin = dpToPx(2);
        avatar.setLayoutParams(avatarParams);
        avatar.setBackground(getResources().getDrawable(R.drawable.ai_avatar_bg, null));
        avatar.setText("✦");
        avatar.setTextSize(20f);
        avatar.setTextColor(0xFFFFFFFF);
        avatar.setGravity(Gravity.CENTER);

        // Bubble card
        CardView card = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        card.setLayoutParams(cardParams);
        card.setCardBackgroundColor(0xFFFFFFFF);
        card.setRadius(dpToPx(18));
        card.setCardElevation(dpToPx(2));

        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextColor(0xFF111111);
        tv.setTextSize(15f);
        tv.setPadding(dpToPx(14), dpToPx(12), dpToPx(14), dpToPx(12));
        tv.setLineSpacing(0, 1.4f);
        card.addView(tv);

        // Right spacer
        View spacer = new View(this);
        spacer.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(44), 1));

        row.addView(avatar);
        row.addView(card);
        row.addView(spacer);

        chatContainer.addView(row);
        scrollToBottom();
    }

    private void scrollToBottom() {
        if (chatScrollView == null) return;
        chatScrollView.post(() -> chatScrollView.fullScroll(View.FOCUS_DOWN));
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
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
        bottomNav.setSelectedItemId(R.id.ai);
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
                startActivity(new Intent(this, gpa.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.ai) {
                // Already in AI activity; do nothing
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