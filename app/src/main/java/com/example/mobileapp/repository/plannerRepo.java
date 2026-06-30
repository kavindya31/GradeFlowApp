package com.example.mobileapp.repository;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.mobileapp.models.PlannerTask;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository class for managing Study Planner tasks and deadlines.
 * Simulates a database using SharedPreferences and custom string serialization.
 */
public class plannerRepo {

    private static final String PREF_NAME = "GradeFlowPlannerPrefs";
    private static final String KEY_TASKS_DATA = "planner_tasks_raw_data";

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public plannerRepo(Context context) {
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.editor = prefs.edit();

        // Pre-populate with default items if empty
        if (!prefs.contains(KEY_TASKS_DATA)) {
            prePopulateDefaults();
        }
    }

    private void prePopulateDefaults() {
        List<PlannerTask> defaults = new ArrayList<>();
        defaults.add(new PlannerTask("1", "Adv. Calculus Mid-Exam", "Mid-term", "Oct 12", false));
        defaults.add(new PlannerTask("2", "History Presentation", "Assignment", "Oct 15", false));
        defaults.add(new PlannerTask("3", "AI Ethics Research Paper", "Assignment", "Oct 20", false));
        saveTasks(defaults);
    }

    /**
     * Retrieves all study tasks from storage.
     * Custom serialization: id|title|taskType|dueDate|isCompleted::...
     */
    public List<PlannerTask> getAllTasks() {
        List<PlannerTask> list = new ArrayList<>();
        String raw = prefs.getString(KEY_TASKS_DATA, "");
        if (raw == null || raw.isEmpty()) {
            return list;
        }

        String[] tasksSplit = raw.split("::");
        for (String taskRaw : tasksSplit) {
            if (taskRaw.trim().isEmpty()) continue;

            String[] parts = taskRaw.split("\\|");
            if (parts.length < 5) continue;

            String id = parts[0];
            String title = parts[1];
            String type = parts[2];
            String date = parts[3];
            boolean completed = Boolean.parseBoolean(parts[4]);

            list.add(new PlannerTask(id, title, type, date, completed));
        }
        return list;
    }

    /**
     * Saves a list of planner tasks to storage.
     */
    public void saveTasks(List<PlannerTask> list) {
        if (list == null || list.isEmpty()) {
            editor.putString(KEY_TASKS_DATA, "");
            editor.apply();
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            PlannerTask t = list.get(i);
            sb.append(t.getId()).append("|")
              .append(t.getTitle()).append("|")
              .append(t.getTaskType()).append("|")
              .append(t.getDueDate()).append("|")
              .append(t.isCompleted());

            if (i < list.size() - 1) {
                sb.append("::");
            }
        }

        editor.putString(KEY_TASKS_DATA, sb.toString());
        editor.apply();
    }

    /**
     * Saves a single task (either inserts new or updates existing).
     */
    public void addOrUpdateTask(PlannerTask task) {
        List<PlannerTask> list = getAllTasks();
        int existingIndex = -1;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(task.getId())) {
                existingIndex = i;
                break;
            }
        }

        if (existingIndex >= 0) {
            list.set(existingIndex, task);
        } else {
            list.add(task);
        }

        saveTasks(list);
    }

    /**
     * Deletes a task by ID.
     */
    public void deleteTask(String id) {
        List<PlannerTask> list = getAllTasks();
        int targetIndex = -1;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(id)) {
                targetIndex = i;
                break;
            }
        }

        if (targetIndex >= 0) {
            list.remove(targetIndex);
            saveTasks(list);
        }
    }
}
