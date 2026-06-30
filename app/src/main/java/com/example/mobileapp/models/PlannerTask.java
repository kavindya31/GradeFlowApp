package com.example.mobileapp.models;

/**
 * Model class representing a Planner Task or academic deadline in GradeFlow.
 */
public class PlannerTask {
    private String id;
    private String title;
    private String taskType; // "Exam", "Assignment", "Mid-term"
    private String dueDate; // e.g. "Oct 12"
    private boolean isCompleted;

    public PlannerTask() {}

    public PlannerTask(String id, String title, String taskType, String dueDate, boolean isCompleted) {
        this.id = id;
        this.title = title;
        this.taskType = taskType;
        this.dueDate = dueDate;
        this.isCompleted = isCompleted;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getTaskType() { return taskType; }
    public void setTaskType(String taskType) { this.taskType = taskType; }
    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }
    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }
}
