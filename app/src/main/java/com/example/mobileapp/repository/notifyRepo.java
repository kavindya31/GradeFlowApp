package com.example.mobileapp.repository;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.mobileapp.models.NotificationItem;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository class for managing academic notifications.
 * Simulates a local DB utilizing SharedPreferences and custom CSV-like serialization.
 */
public class notifyRepo {

    private static final String PREF_NAME = "GradeFlowNotifyPrefs";
    private static final String KEY_NOTIFICATIONS_DATA = "notifications_raw_data";

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public notifyRepo(Context context) {
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.editor = prefs.edit();

        // Pre-populate with default items if empty
        if (!prefs.contains(KEY_NOTIFICATIONS_DATA)) {
            prePopulateDefaults();
        }
    }

    private void prePopulateDefaults() {
        List<NotificationItem> defaults = new ArrayList<>();
        defaults.add(new NotificationItem("1", "Calculus Midterm Tomorrow", "You have a Calculus Midterm scheduled for tomorrow morning. Review weak subjects now!", "2 hours ago", "Alert", false));
        defaults.add(new NotificationItem("2", "GPA Report Recalculated", "Your CGPA has been recalculated to 3.88. View details in the GPA dashboard.", "5 hours ago", "Grade", false));
        defaults.add(new NotificationItem("3", "Study Recommendation Generated", "GradeFlow AI has generated new suggestions based on your recent performance trend.", "Yesterday", "Reminder", false));
        defaults.add(new NotificationItem("4", "Data Structures Homework", "Homework assignment 'Binary Search Trees' is due in 3 days. Track progress in planner.", "2 days ago", "Reminder", true));
        saveNotifications(defaults);
    }

    /**
     * Retrieves all notifications from storage.
     */
    public List<NotificationItem> getAllNotifications() {
        List<NotificationItem> list = new ArrayList<>();
        String raw = prefs.getString(KEY_NOTIFICATIONS_DATA, "");
        if (raw == null || raw.isEmpty()) {
            return list;
        }

        String[] split = raw.split("::");
        for (String rawItem : split) {
            if (rawItem.trim().isEmpty()) continue;

            String[] parts = rawItem.split("\\|");
            if (parts.length < 6) continue;

            String id = parts[0];
            String title = parts[1];
            String desc = parts[2];
            String time = parts[3];
            String type = parts[4];
            boolean isRead = Boolean.parseBoolean(parts[5]);

            list.add(new NotificationItem(id, title, desc, time, type, isRead));
        }
        return list;
    }

    /**
     * Saves a list of notifications to storage.
     */
    public void saveNotifications(List<NotificationItem> list) {
        if (list == null || list.isEmpty()) {
            editor.putString(KEY_NOTIFICATIONS_DATA, "");
            editor.apply();
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            NotificationItem item = list.get(i);
            sb.append(item.getId()).append("|")
              .append(item.getTitle()).append("|")
              .append(item.getDescription()).append("|")
              .append(item.getTimeString()).append("|")
              .append(item.getType()).append("|")
              .append(item.isRead());

            if (i < list.size() - 1) {
                sb.append("::");
            }
        }

        editor.putString(KEY_NOTIFICATIONS_DATA, sb.toString());
        editor.apply();
    }

    /**
     * Marks all notifications as read.
     */
    public void markAllAsRead() {
        List<NotificationItem> list = getAllNotifications();
        for (NotificationItem item : list) {
            item.setRead(true);
        }
        saveNotifications(list);
    }

    /**
     * Inserts a new notification.
     */
    public void addNotification(NotificationItem item) {
        List<NotificationItem> list = getAllNotifications();
        list.add(0, item); // Add newest first
        saveNotifications(list);
    }
}
