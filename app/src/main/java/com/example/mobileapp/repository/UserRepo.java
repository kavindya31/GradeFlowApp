package com.example.mobileapp.repository;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.mobileapp.models.user;

/**
 * Repository class for User Authentication and Profile data management.
 * Currently uses SharedPreferences as a local database simulation,
 * facilitating clean integration with Firebase in the future.
 */
public class UserRepo {

    private static final String PREF_NAME = "GradeFlowUserPrefs";
    private static final String KEY_LOGGED_IN = "isLoggedIn";
    private static final String KEY_EMAIL = "userEmail";
    private static final String KEY_NAME = "userName";
    private static final String KEY_UNIVERSITY = "userUniversity";
    private static final String KEY_DEGREE = "userDegree";
    private static final String KEY_YEAR = "userYear";
    private static final String KEY_GPA = "userGPA";
    private static final String KEY_PASSWORD = "userPassword";

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    // Constructor requiring Context for SharedPreferences
    public UserRepo(Context context) {
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.editor = prefs.edit();
    }

    /**
     * Attempts to log in a user by checking credentials.
     * @param email User email
     * @param password User password
     * @return true if credentials match local record, false otherwise.
     */
    public boolean loginUser(String email, String password) {
        String savedEmail = prefs.getString(KEY_EMAIL, "");
        String savedPassword = prefs.getString(KEY_PASSWORD, "");

        if (email.equalsIgnoreCase(savedEmail) && password.equals(savedPassword)) {
            editor.putBoolean(KEY_LOGGED_IN, true);
            editor.apply();
            return true;
        }
        return false;
    }

    /**
     * Registers a new user. Saves credentials and profile details.
     * @param u The user object to register.
     * @return true if signup is successful.
     */
    public boolean registerUser(user u) {
        editor.putString(KEY_NAME, u.getFullName());
        editor.putString(KEY_UNIVERSITY, u.getUniversity());
        editor.putString(KEY_EMAIL, u.getEmail());
        editor.putString(KEY_PASSWORD, u.getPassword());
        editor.putString(KEY_DEGREE, u.getDegree());
        editor.putString(KEY_YEAR, u.getYear());
        editor.putString(KEY_GPA, u.getGpa());
        editor.putBoolean(KEY_LOGGED_IN, true);
        editor.apply();
        return true;
    }

    /**
     * Saves / updates user profile fields.
     * @param name Full name
     * @param university University name
     * @param degree Degree program
     * @param year Academic year
     * @param gpa Cumulative GPA
     */
    public void updateUserProfile(String name, String university, String degree, String year, String gpa) {
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_UNIVERSITY, university);
        editor.putString(KEY_DEGREE, degree);
        editor.putString(KEY_YEAR, year);
        editor.putString(KEY_GPA, gpa);
        editor.apply();
    }

    /**
     * Updates the user's login password.
     * @param newPassword The new password to save.
     */
    public void updatePassword(String newPassword) {
        editor.putString(KEY_PASSWORD, newPassword);
        editor.apply();
    }

    /**
     * Retrieves the currently active user profile.
     * @return user object filled with cached data.
     */
    public user getCurrentUser() {
        String name = prefs.getString(KEY_NAME, "");
        String university = prefs.getString(KEY_UNIVERSITY, "");
        String email = prefs.getString(KEY_EMAIL, "");
        String degree = prefs.getString(KEY_DEGREE, "");
        String year = prefs.getString(KEY_YEAR, "");
        String gpa = prefs.getString(KEY_GPA, "0.0");
        String password = prefs.getString(KEY_PASSWORD, "");

        return new user(name, university, email, password, degree, year, gpa);
    }

    /**
     * Checks if a user is currently logged in.
     * @return true if logged in, false otherwise.
     */
    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_LOGGED_IN, false);
    }

    /**
     * Logs out the current user and clears active session flags.
     */
    public void logoutUser() {
        editor.putBoolean(KEY_LOGGED_IN, false);
        editor.apply();
    }
}
