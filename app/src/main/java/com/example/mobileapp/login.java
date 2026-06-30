package com.example.mobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.mobileapp.repository.UserRepo;
import com.example.mobileapp.utils.ValidationHelper;

public class login extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;
    private CardView btnGoogle;
    private TextView tvForgotPassword;
    private TextView tvSignUp;
    private UserRepo userRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_login);

        userRepo = new UserRepo(this);

        initViews();
        setClickListeners();
    }

    private void initViews() {
        etEmail         = findViewById(R.id.etEmail);
        etPassword      = findViewById(R.id.etPassword);
        btnLogin        = findViewById(R.id.btnLogin);
        btnGoogle       = findViewById(R.id.btnGoogle);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvSignUp        = findViewById(R.id.tvSignUp);
    }

    private void setClickListeners() {

        btnLogin.setOnClickListener(v -> handleLogin());

        btnGoogle.setOnClickListener(v -> handleGoogleLogin());

        tvForgotPassword.setOnClickListener(v -> {
            Toast.makeText(this, "Password reset email sent!", Toast.LENGTH_SHORT).show();
            // TODO: Implement forgot password flow
        });

        tvSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(login.this, signup.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });
    }

    private void handleLogin() {
        String email    = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid university email");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return;
        }

        // TODO: Replace with real authentication (Firebase / your backend)
        performLogin(email, password);
    }

    private void performLogin(String email, String password) {
        btnLogin.setEnabled(false);
        btnLogin.setText("Signing in...");

        // Simulate network call — replace with real API call
        new android.os.Handler().postDelayed(() -> {
            btnLogin.setEnabled(true);
            btnLogin.setText("Login");

            // On success navigate to Dashboard
            Intent intent = new Intent(login.this, gpa.class);
            intent.putExtra("user_email", email);
            startActivity(intent);
            finish();
        }, 1500);
    }

    private void handleGoogleLogin() {
        // TODO: Integrate Google Sign-In SDK
        // GoogleSignInOptions gso = new GoogleSignInOptions.Builder(...)
        Toast.makeText(this, "Google Sign-In coming soon!", Toast.LENGTH_SHORT).show();
    }
}