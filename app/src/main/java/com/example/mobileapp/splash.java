package com.example.mobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class splash extends AppCompatActivity {

    private static final int SPLASH_DURATION_MS = 3000;
    private static final int PROGRESS_UPDATE_INTERVAL = 30;

    private ProgressBar progressBar;
    private TextView tvInitializing;
    private Handler handler = new Handler(Looper.getMainLooper());
    private int currentProgress = 0;

    private final String[] loadingMessages = {
            "INITIALIZING...",
            "LOADING MODULES...",
            "SYNCING DATA...",
            "ALMOST READY..."
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide action bar on splash
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_splash);

        progressBar = findViewById(R.id.progressBar);
        tvInitializing = findViewById(R.id.tvInitializing);

        startProgressAnimation();
    }

    private void startProgressAnimation() {
        handler.post(progressRunnable);
    }

    private Runnable progressRunnable = new Runnable() {
        @Override
        public void run() {
            if (currentProgress <= 100) {
                progressBar.setProgress(currentProgress);

                // Update message at intervals
                int messageIndex = (currentProgress / 25) % loadingMessages.length;
                tvInitializing.setText(loadingMessages[messageIndex]);

                currentProgress += 2;
                handler.postDelayed(this, PROGRESS_UPDATE_INTERVAL);
            } else {
                // Navigate to Login
                navigateToLogin();
            }
        }
    };

    private void navigateToLogin() {
        Intent intent = new Intent(splash.this, login.class);
        startActivity(intent);
        // Smooth fade transition
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(progressRunnable);
    }
}