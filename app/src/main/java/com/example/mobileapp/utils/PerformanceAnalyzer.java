package com.example.mobileapp.utils;

import com.example.mobileapp.models.semester;
import com.example.mobileapp.models.subject;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to analyze student performance trends and identify academic weak areas.
 */
public class PerformanceAnalyzer {

    private static final double WEAKNESS_THRESHOLD = 2.7; // Grade points below B- (2.7) are considered weak areas

    /**
     * Inspects a list of subjects to identify those where the user scored poorly.
     * @param subjects List of subjects
     * @return List of subjects identified as weak
     */
    public static List<subject> detectWeakSubjects(List<subject> subjects) {
        List<subject> weakSubjects = new ArrayList<>();
        if (subjects == null) return weakSubjects;

        for (subject s : subjects) {
            if (s.getGradePoints() < WEAKNESS_THRESHOLD) {
                weakSubjects.add(s);
            }
        }
        return weakSubjects;
    }

    /**
     * Analyzes GPA trends across a list of semesters in chronological order.
     * @param semesters List of semesters
     * @return Summary string of the GPA trend (e.g. "Improving", "Declining", "Stable", or "Insufficient Data")
     */
    public static String analyzeGpaTrend(List<semester> semesters) {
        if (semesters == null || semesters.size() < 2) {
            return "Insufficient Data (Needs at least 2 semesters)";
        }

        // Compare GPA of semesters chronologically
        double firstSemGpa = semesters.get(0).getGpa();
        double lastSemGpa = semesters.get(semesters.size() - 1).getGpa();
        double diff = lastSemGpa - firstSemGpa;

        if (diff > 0.15) {
            return "Improving (GPA increased by " + String.format("%.2f", diff) + " points)";
        } else if (diff < -0.15) {
            return "Declining (GPA decreased by " + String.format("%.2f", Math.abs(diff)) + " points)";
        } else {
            return "Stable (GPA remained consistent)";
        }
    }
}
