package com.example.mobileapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobileapp.repository.UserRepo;
import com.example.mobileapp.utils.ValidationHelper;
import com.example.mobileapp.models.user;

public class signup extends AppCompatActivity {

    private EditText etFullName;
    private EditText etUniversity;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private CheckBox cbTerms;
    private TextView tvTerms;
    private Button btnCreateAccount;
    private TextView tvLogin;
    private UserRepo userRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_signup);

        userRepo = new UserRepo(this);

        initViews();
        setupTermsText();
        setClickListeners();
    }

    private void initViews() {
        etFullName = findViewById(R.id.etFullName);
        etUniversity = findViewById(R.id.etUniversity);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        cbTerms = findViewById(R.id.cbTerms);
        tvTerms = findViewById(R.id.tvTerms);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        tvLogin = findViewById(R.id.tvLogin);
    }

    /**
     * Makes "Terms of Service" and "Privacy Policy" clickable and blue within the
     * full text.
     */
    private void setupTermsText() {
        String fullText = "I agree to the Terms of Service and Privacy Policy.";
        SpannableString spannable = new SpannableString(fullText);

        int tosStart = fullText.indexOf("Terms of Service");
        int tosEnd = tosStart + "Terms of Service".length();
        int ppStart = fullText.indexOf("Privacy Policy");
        int ppEnd = ppStart + "Privacy Policy".length();

        int blue = Color.parseColor("#1D4ED8");

        // Terms of Service span
        spannable.setSpan(new ForegroundColorSpan(blue), tosStart, tosEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Toast.makeText(signup.this, "Terms of Service", Toast.LENGTH_SHORT).show();
                // TODO: open Terms of Service URL/screen
            }
        }, tosStart, tosEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Privacy Policy span
        spannable.setSpan(new ForegroundColorSpan(blue), ppStart, ppEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Toast.makeText(signup.this, "Privacy Policy", Toast.LENGTH_SHORT).show();
                // TODO: open Privacy Policy URL/screen
            }
        }, ppStart, ppEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvTerms.setText(spannable);
        tvTerms.setMovementMethod(LinkMovementMethod.getInstance());
        tvTerms.setHighlightColor(Color.TRANSPARENT);
    }

    private void setClickListeners() {

        btnCreateAccount.setOnClickListener(v -> handleSignup());

        tvLogin.setOnClickListener(v -> {
            finish(); // Go back to Login
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });
    }

    private void handleSignup() {
        String fullName = etFullName.getText().toString().trim();
        String university = etUniversity.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPass = etConfirmPassword.getText().toString().trim();

        // --- Validation ---
        if (!ValidationHelper.validateFullName(fullName)) {
            etFullName.setError("Full name must be at least 2 characters");
            etFullName.requestFocus();
            return;
        }

        if (!ValidationHelper.validateUniversity(university)) {
            etUniversity.setError("University name must be at least 3 characters");
            etUniversity.requestFocus();
            return;
        }

        if (!ValidationHelper.validateEmail(email)) {
            etEmail.setError("Enter a valid email address");
            etEmail.requestFocus();
            return;
        }

        if (!ValidationHelper.validatePassword(password)) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPass)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return;
        }

        if (!cbTerms.isChecked()) {
            Toast.makeText(this, "Please accept the Terms of Service and Privacy Policy", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- All valid, proceed ---
        performSignup(fullName, university, email, password);
    }

    private void performSignup(String fullName, String university, String email, String password) {
        btnCreateAccount.setEnabled(false);
        btnCreateAccount.setText("Creating Account...");

        // Simulate network call — replace with Firebase/backend registration
        new android.os.Handler().postDelayed(() -> {
            btnCreateAccount.setEnabled(true);
            btnCreateAccount.setText("Create Account  →");

            // Register user in repository
            user newUser = new user(fullName, university, email, password);
            userRepo.registerUser(newUser);

            Toast.makeText(this, "Account created! Welcome to GradeFlow.", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(signup.this, home.class);
            intent.putExtra("user_name", fullName);
            intent.putExtra("user_email", email);
            intent.putExtra("university", university);
            startActivity(intent);
            finish();
        }, 2000);
    }
}