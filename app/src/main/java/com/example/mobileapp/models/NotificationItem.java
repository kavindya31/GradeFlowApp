package com.example.mobileapp.models;

/**
 * Model class representing a Notification Item in GradeFlow.
 * Captures notification details, timestamps, types, and read/unread status.
 */
public class NotificationItem {
    private String id;
    private String title;
    private String description;
    private String timeString; // e.g. "2 hours ago" or "Oct 15"
    private String type; // "Alert", "Grade", "Reminder"
    private boolean isRead;

    // Default constructor
    public NotificationItem() {
    }

    // Complete constructor
    public NotificationItem(String id, String title, String description, String timeString, String type, boolean isRead) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.timeString = timeString;
        this.type = type;
        this.isRead = isRead;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTimeString() {
        return timeString;
    }

    public void setTimeString(String timeString) {
        this.timeString = timeString;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}
