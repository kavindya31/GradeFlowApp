package com.example.mobileapp.repository;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.mobileapp.models.semester;
import com.example.mobileapp.models.subject;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository class for managing Semester records and GPA history.
 * Simulates a local database via SharedPreferences using custom string serialization.
 */
public class GPArepo {

    private static final String PREF_NAME = "GradeFlowGPAPrefs";
    private static final String KEY_SEMESTERS_DATA = "semesters_raw_data";

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public GPArepo(Context context) {
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.editor = prefs.edit();
    }

    /**
     * Retrieves all semester records from storage.
     * Parses custom serialized format:
     * SemesterName|Year|GPA;SubjectName,Grade,GradePoints,Credits;...::SemesterName|Year|GPA;...
     */
    public List<semester> getAllSemesters() {
        List<semester> list = new ArrayList<>();
        String raw = prefs.getString(KEY_SEMESTERS_DATA, "");
        if (raw == null || raw.isEmpty()) {
            return list;
        }

        String[] semestersSplit = raw.split("::");
        for (String semRaw : semestersSplit) {
            if (semRaw.trim().isEmpty()) continue;
            
            String[] parts = semRaw.split(";");
            if (parts.length < 1) continue;

            // Parse semester details
            String[] semDetails = parts[0].split("\\|");
            if (semDetails.length < 3) continue;

            String name = semDetails[0];
            String year = semDetails[1];
            double gpa = 0.0;
            try {
                gpa = Double.parseDouble(semDetails[2]);
            } catch (NumberFormatException ignored) {}

            semester sem = new semester(name, year);
            sem.setGpa(gpa);

            // Parse subjects if any
            for (int i = 1; i < parts.length; i++) {
                String[] subDetails = parts[i].split(",");
                if (subDetails.length < 4) continue;

                String subName = subDetails[0];
                String subGrade = subDetails[1];
                double subPts = 0.0;
                double subCredits = 0.0;
                try {
                    subPts = Double.parseDouble(subDetails[2]);
                    subCredits = Double.parseDouble(subDetails[3]);
                } catch (NumberFormatException ignored) {}

                subject s = new subject(subName, subGrade, subPts, subCredits);
                sem.addSubject(s);
            }
            list.add(sem);
        }
        return list;
    }

    /**
     * Saves a list of semesters to storage.
     */
    public void saveSemesters(List<semester> list) {
        if (list == null || list.isEmpty()) {
            editor.putString(KEY_SEMESTERS_DATA, "");
            editor.apply();
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            semester sem = list.get(i);
            // Format: SemesterName|Year|GPA
            sb.append(sem.getSemesterName()).append("|")
              .append(sem.getYear()).append("|")
              .append(sem.getGpa());

            // Append subjects
            for (subject s : sem.getSubjects()) {
                // Format: ;SubjectName,Grade,GradePoints,Credits
                sb.append(";").append(s.getSubjectName()).append(",")
                  .append(s.getGrade()).append(",")
                  .append(s.getGradePoints()).append(",")
                  .append(s.getCredits());
            }

            if (i < list.size() - 1) {
                sb.append("::");
            }
        }

        editor.putString(KEY_SEMESTERS_DATA, sb.toString());
        editor.apply();
    }

    /**
     * Saves a single semester record to the list.
     */
    public void addSemester(semester sem) {
        List<semester> currentList = getAllSemesters();
        
        // Update if already exists, else add new
        int existingIndex = -1;
        for (int i = 0; i < currentList.size(); i++) {
            semester s = currentList.get(i);
            if (s.getSemesterName().equals(sem.getSemesterName()) && s.getYear().equals(sem.getYear())) {
                existingIndex = i;
                break;
            }
        }

        if (existingIndex >= 0) {
            currentList.set(existingIndex, sem);
        } else {
            currentList.add(sem);
        }

        saveSemesters(currentList);
    }

    /**
     * Clears all saved semester data.
     */
    public void clearHistory() {
        editor.remove(KEY_SEMESTERS_DATA);
        editor.apply();
    }
}
