package com.example.mobileapp.utils;

import com.example.mobileapp.models.semester;
import com.example.mobileapp.models.subject;
import java.util.List;

/**
 * Utility class to calculate GPA and CGPA values based on subjects and semesters.
 * Includes helper methods for conversion between letter grades and grade points.
 */
public class GPACalculator {

    /**
     * Converts a letter grade to its equivalent grade points.
     * Standard US/4.0 scale mapping:
     * A/A+ -> 4.0, A- -> 3.7, B+ -> 3.3, B -> 3.0, B- -> 2.7,
     * C+ -> 2.3, C -> 2.0, C- -> 1.7, D+ -> 1.3, D -> 1.0, F -> 0.0
     *
     * @param grade Letter grade string
     * @return Grade points value
     */
    public static double convertGradeToPoints(String grade) {
        if (grade == null) return 0.0;
        String cleanGrade = grade.trim().toUpperCase();

        // If it starts with a letter but has details, extract the base grade
        if (cleanGrade.contains("(")) {
            // E.g. "A (4.0)" -> "A"
            cleanGrade = cleanGrade.split("\\(")[0].trim();
        }

        switch (cleanGrade) {
            case "A+":
            case "A":
                return 4.0;
            case "A-":
                return 3.7;
            case "B+":
                return 3.3;
            case "B":
                return 3.0;
            case "B-":
                return 2.7;
            case "C+":
                return 2.3;
            case "C":
                return 2.0;
            case "C-":
                return 1.7;
            case "D+":
                return 1.3;
            case "D":
                return 1.0;
            case "F":
            default:
                // Check if it's already a numeric string
                try {
                    double numericVal = Double.parseDouble(cleanGrade);
                    if (numericVal >= 0 && numericVal <= 4.0) {
                        return numericVal;
                    } else if (numericVal > 4.0 && numericVal <= 100) {
                        // Map percentage to 4.0 scale
                        return mapPercentageToGPA(numericVal);
                    }
                } catch (NumberFormatException ignored) {}
                return 0.0;
        }
    }

    private static double mapPercentageToGPA(double score) {
        if (score >= 90) return 4.0;
        if (score >= 85) return 3.7;
        if (score >= 80) return 3.3;
        if (score >= 75) return 3.0;
        if (score >= 70) return 2.7;
        if (score >= 65) return 2.3;
        if (score >= 60) return 2.0;
        if (score >= 55) return 1.7;
        if (score >= 50) return 1.0;
        return 0.0;
    }

    /**
     * Calculates GPA for a single semester.
     * GPA = Σ(gradePoints × credits) / Σ(credits)
     *
     * @param subjects List of subjects in the semester
     * @return Calculated GPA value
     */
    public static double calculateGPA(List<subject> subjects) {
        if (subjects == null || subjects.isEmpty()) {
            return 0.0;
        }

        double totalGradePointsTimesCredits = 0.0;
        double totalCredits = 0.0;

        for (subject s : subjects) {
            totalGradePointsTimesCredits += s.getGradePoints() * s.getCredits();
            totalCredits += s.getCredits();
        }

        return totalCredits > 0 ? (totalGradePointsTimesCredits / totalCredits) : 0.0;
    }

    /**
     * Calculates Cumulative GPA (CGPA) across multiple semesters.
     * CGPA = Σ(Semester GPA × Semester Credits) / Σ(Semester Credits)
     * Falls back to standard average if semester credits are not tracked.
     *
     * @param semesters List of semesters
     * @return Calculated CGPA value
     */
    public static double calculateCGPA(List<semester> semesters) {
        if (semesters == null || semesters.isEmpty()) {
            return 0.0;
        }

        double totalWeightedGpa = 0.0;
        double totalCredits = 0.0;

        for (semester sem : semesters) {
            double semCredits = 0.0;
            for (subject s : sem.getSubjects()) {
                semCredits += s.getCredits();
            }

            if (semCredits > 0) {
                totalWeightedGpa += sem.getGpa() * semCredits;
                totalCredits += semCredits;
            } else {
                // If credits are 0, count this semester with a default weight of 15 credits
                totalWeightedGpa += sem.getGpa() * 15.0;
                totalCredits += 15.0;
            }
        }

        return totalCredits > 0 ? (totalWeightedGpa / totalCredits) : 0.0;
    }
}
