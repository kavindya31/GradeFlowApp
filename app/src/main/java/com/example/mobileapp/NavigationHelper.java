package com.example.mobileapp;

import android.app.Activity;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mobileapp.repository.UserRepo;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Centralized helper class for setting up common top bar and bottom navigation
 * across all activities. Eliminates duplicated navigation code.
 */
public class NavigationHelper {

    /**
     * Sets up the common top bar actions (avatar -> profile, notification bell, logout).
     */
    public static void setupTopBar(Activity activity, UserRepo userRepo) {
        ImageView imgTopAvatar = activity.findViewById(R.id.imgTopAvatar);
        ImageView btnNotification = activity.findViewById(R.id.btnNotification);
        ImageView btnLogout = activity.findViewById(R.id.btnLogout);

        if (imgTopAvatar != null) {
            imgTopAvatar.setOnClickListener(v -> {
                activity.startActivity(new Intent(activity, profile.class));
            });
        }

        if (btnNotification != null) {
            btnNotification.setOnClickListener(v -> {
                activity.startActivity(new Intent(activity, notification.class));
            });
        }

        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                userRepo.logoutUser();
                Toast.makeText(activity, "Logged out successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(activity, login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                activity.startActivity(intent);
                activity.finish();
            });
        }
    }

    /**
     * Sets up the bottom navigation bar with proper item selection and activity transitions.
     * @param activity The current activity
     * @param selectedItemId The menu item ID that should be highlighted (e.g., R.id.home)
     */
    public static void setupBottomNav(Activity activity, int selectedItemId) {
        BottomNavigationView bottomNav = activity.findViewById(R.id.bottomNav);
        if (bottomNav == null) return;

        bottomNav.setSelectedItemId(selectedItemId);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == selectedItemId) {
                // Already on this page
                return true;
            }

            Class<?> targetClass = null;

            if (id == R.id.home) {
                targetClass = home.class;
            } else if (id == R.id.planner) {
                targetClass = planner.class;
            } else if (id == R.id.calculator) {
                targetClass = gpa.class;
            } else if (id == R.id.ai) {
                targetClass = AI.class;
            } else if (id == R.id.profile) {
                targetClass = profile.class;
            }

            if (targetClass != null) {
                activity.startActivity(new Intent(activity, targetClass));
                activity.overridePendingTransition(0, 0);
                activity.finish();
                return true;
            }

            return false;
        });
    }
}
