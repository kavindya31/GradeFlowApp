package com.example.mobileapp;

import com.example.mobileapp.models.PlannerTask;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mobileapp.repository.plannerRepo;
import com.example.mobileapp.repository.UserRepo;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Activity class managing the Study Planner screen.
 * Displays calendar events, dynamic deadlines, and allows adding new academic study tasks.
 */
public class planner extends AppCompatActivity {

    private TextView tvMonthYear;
    private TextView tabToday;
    private TextView tabThisWeek;
    private TextView tabThisMonth;

    private TextView chipExam;
    private TextView chipAssignment;
    private TextView chipMidterm;

    private CheckBox checkDeadline1;
    private CheckBox checkDeadline2;
    private CheckBox checkDeadline3;

    private FloatingActionButton fabAdd;
    private ImageView imgTopAvatar;
    private ImageView btnNotification;
    private ImageView btnLogout;
    private BottomNavigationView bottomNav;

    private String selectedTab = "Today";
    
    private plannerRepo plannerRepository;
    private UserRepo userRepo;
    private List<PlannerTask> activeTasks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        
        setContentView(R.layout.activity_planner);

        plannerRepository = new plannerRepo(this);
        userRepo = new UserRepo(this);

        initViews();
        setupTabs();
        loadAndDisplayTasks();
        setupQuickAddChips();
        setupFab();
        setupTopBarActions();
        setupBottomNavigation();
    }

    private void initViews() {
        tabToday       = findViewById(R.id.tabToday);
        tabThisWeek    = findViewById(R.id.tabThisWeek);
        tabThisMonth   = findViewById(R.id.tabThisMonth);

        chipExam       = findViewById(R.id.chipExam);
        chipAssignment = findViewById(R.id.chipAssignment);
        chipMidterm    = findViewById(R.id.chipMidterm);

        checkDeadline1 = findViewById(R.id.checkDeadline1);
        checkDeadline2 = findViewById(R.id.checkDeadline2);
        checkDeadline3 = findViewById(R.id.checkDeadline3);

        fabAdd         = findViewById(R.id.fabAdd);
        imgTopAvatar   = findViewById(R.id.imgTopAvatar);
        btnNotification = findViewById(R.id.btnNotification);
        btnLogout      = findViewById(R.id.btnLogout);
        bottomNav      = findViewById(R.id.bottomNav);
    }

    private void setupTabs() {
        if (tabToday != null) {
            tabToday.setOnClickListener(v -> selectTab("Today"));
        }
        if (tabThisWeek != null) {
            tabThisWeek.setOnClickListener(v -> selectTab("This Week"));
        }
        if (tabThisMonth != null) {
            tabThisMonth.setOnClickListener(v -> selectTab("This Month"));
        }
    }

    private void selectTab(String tab) {
        selectedTab = tab;
        loadAndDisplayTasks();
        Toast.makeText(this, "Showing: " + tab, Toast.LENGTH_SHORT).show();
    }

    /**
     * Loads tasks from the database and binds them to the three deadline checkboxes in the UI.
     */
    private void loadAndDisplayTasks() {
        List<PlannerTask> allTasks = plannerRepository.getAllTasks();
        activeTasks.clear();

        // Simple filtering based on selected tab (simulation)
        for (PlannerTask t : allTasks) {
            if ("Today".equals(selectedTab) && !t.isCompleted()) {
                activeTasks.add(t);
            } else if ("This Week".equals(selectedTab)) {
                // Shows all uncompleted tasks
                if (!t.isCompleted()) activeTasks.add(t);
            } else {
                // Show all tasks for "This Month"
                activeTasks.add(t);
            }
        }

        bindTaskToCheckbox(checkDeadline1, 0);
        bindTaskToCheckbox(checkDeadline2, 1);
        bindTaskToCheckbox(checkDeadline3, 2);
    }

    private void bindTaskToCheckbox(CheckBox checkBox, int index) {
        if (checkBox == null) return;

        if (index < activeTasks.size()) {
            PlannerTask task = activeTasks.get(index);
            checkBox.setVisibility(View.VISIBLE);
            
            // Format label e.g. "Adv. Calculus Mid-Exam (Oct 12)"
            checkBox.setText(task.getTitle() + " (" + task.getDueDate() + ")");
            
            // Temporarily clear listener to prevent loop
            checkBox.setOnCheckedChangeListener(null);
            checkBox.setChecked(task.isCompleted());
            
            checkBox.setOnCheckedChangeListener((btn, isChecked) -> {
                task.setCompleted(isChecked);
                plannerRepository.addOrUpdateTask(task);
                
                String msg = isChecked ? 
                        task.getTitle() + " marked done" : 
                        task.getTitle() + " unmarked";
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                
                // Refresh list to update counts/completion state
                loadAndDisplayTasks();
            });
        } else {
            // Hide checkboxes if there are no tasks for this slot
            checkBox.setVisibility(View.GONE);
        }
    }

    private void setupQuickAddChips() {
        if (chipExam != null) {
            chipExam.setOnClickListener(v -> quickAddTask("New Exam Prep", "Exam", "Tomorrow"));
        }
        if (chipAssignment != null) {
            chipAssignment.setOnClickListener(v -> quickAddTask("New Assignment Task", "Assignment", "Oct 18"));
        }
        if (chipMidterm != null) {
            chipMidterm.setOnClickListener(v -> quickAddTask("New Mid-term Exam", "Mid-term", "Oct 24"));
        }
    }

    private void quickAddTask(String title, String type, String dueDate) {
        String id = UUID.randomUUID().toString();
        PlannerTask newTask = new PlannerTask(id, title, type, dueDate, false);
        plannerRepository.addOrUpdateTask(newTask);
        
        loadAndDisplayTasks();
        Toast.makeText(this, type + " added quickly!", Toast.LENGTH_SHORT).show();
    }

    private void setupFab() {
        if (fabAdd != null) {
            fabAdd.setOnClickListener(v -> showAddTaskDialog());
        }
    }

    /**
     * Opens a programmatic AlertDialog form for adding a new planner task.
     */
    private void showAddTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Study Task");

        // Layout container
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 20);

        // Title Input
        final EditText etTitle = new EditText(this);
        etTitle.setHint("Task Name (e.g. Physics Lab Report)");
        layout.addView(etTitle);

        // Type Spinner
        final Spinner spType = new Spinner(this);
        String[] types = {"Assignment", "Exam", "Mid-term"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spType.setAdapter(adapter);
        layout.addView(spType);

        // Due Date Input
        final EditText etDate = new EditText(this);
        etDate.setHint("Due Date (e.g. Oct 28)");
        layout.addView(etDate);

        builder.setView(layout);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String title = etTitle.getText().toString().trim();
            String type = spType.getSelectedItem().toString();
            String date = etDate.getText().toString().trim();

            if (title.isEmpty() || date.isEmpty()) {
                Toast.makeText(this, "Fields cannot be empty!", Toast.LENGTH_SHORT).show();
                return;
            }

            String id = UUID.randomUUID().toString();
            PlannerTask newTask = new PlannerTask(id, title, type, date, false);
            plannerRepository.addOrUpdateTask(newTask);

            loadAndDisplayTasks();
            Toast.makeText(this, "Task saved successfully!", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
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
        bottomNav.setSelectedItemId(R.id.planner);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.home) {
                startActivity(new Intent(this, home.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.planner) {
                // already here
                return true;
            } else if (id == R.id.calculator) {
                startActivity(new Intent(this, gpa.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.ai) {
                startActivity(new Intent(this, AI.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.profile) {
                startActivity(new Intent(this, profile.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });
    }
}