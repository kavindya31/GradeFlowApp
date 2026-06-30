package com.example.mobileapp.utils;

import com.example.mobileapp.models.subject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Utility class to manage and prioritize subjects for studying.
 * Prioritizes subjects by sorting them such that subjects with lower grades
 * and higher credits get higher priority.
 */
public class SubjectPriorityManager {

    /**
     * Data wrapper to store a subject along with its calculated priority level and reason.
     */
    public static class PrioritizedSubject {
        private final subject sub;
        private final String priorityLevel; // "Very High", "High", "Medium", "Low"
        private final String reason;

        public PrioritizedSubject(subject sub, String priorityLevel, String reason) {
            this.sub = sub;
            this.priorityLevel = priorityLevel;
            this.reason = reason;
        }

        public subject getSubject() {
            return sub;
        }

        public String getPriorityLevel() {
            return priorityLevel;
        }

        public String getReason() {
            return reason;
        }
    }

    /**
     * Prioritizes subjects. Sorting criteria:
     * 1. Lower grade points first (weaker subjects need more focus).
     * 2. If grade points are equal, higher credits first (more weight on CGPA).
     *
     * @param subjects List of raw subjects
     * @return List of prioritized subjects sorted in recommended study order
     */
    public static List<PrioritizedSubject> prioritizeSubjects(List<subject> subjects) {
        List<PrioritizedSubject> prioritizedList = new ArrayList<>();
        if (subjects == null || subjects.isEmpty()) {
            return prioritizedList;
        }

        // Create a copy to sort
        List<subject> sortedSubjects = new ArrayList<>(subjects);
        Collections.sort(sortedSubjects, new Comparator<subject>() {
            @Override
            public int compare(subject s1, subject s2) {
                // Ascending order of grade points (lowest grade first)
                int gradeCompare = Double.compare(s1.getGradePoints(), s2.getGradePoints());
                if (gradeCompare != 0) {
                    return gradeCompare;
                }
                // Descending order of credits if grades are equal (highest credit first)
                return Double.compare(s2.getCredits(), s1.getCredits());
            }
        });

        // Map to PrioritizedSubject with levels and reasons
        for (int i = 0; i < sortedSubjects.size(); i++) {
            subject s = sortedSubjects.get(i);
            String priorityLevel;
            String reason;

            double pts = s.getGradePoints();
            if (pts < 2.0) {
                priorityLevel = "Very High";
                reason = "Failing/Critical Grade";
            } else if (pts < 2.7) {
                priorityLevel = "High";
                reason = "Below B- (Needs Improvement)";
            } else if (pts < 3.3) {
                priorityLevel = "Medium";
                reason = "Average Grade";
            } else {
                priorityLevel = "Low";
                reason = "Good Standing";
            }

            prioritizedList.add(new PrioritizedSubject(s, priorityLevel, reason));
        }

        return prioritizedList;
    }
}
