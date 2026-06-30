package com.example.mobileapp.models;

/**
 * Model class representing a Subject/Course in GradeFlow.
 * Stores details of the course name, grade received, and credits.
 */
public class subject {
    private String subjectName;
    private String grade; // e.g. "A", "B+", "C-"
    private double gradePoints; // Numeric value of the grade, e.g. 4.0, 3.3
    private double credits; // Credit hours/units, e.g. 3.0

    // Default constructor
    public subject() {
    }

    // Complete constructor
    public subject(String subjectName, String grade, double gradePoints, double credits) {
        this.subjectName = subjectName;
        this.grade = grade;
        this.gradePoints = gradePoints;
        this.credits = credits;
    }

    // Getters and Setters
    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public double getGradePoints() {
        return gradePoints;
    }

    public void setGradePoints(double gradePoints) {
        this.gradePoints = gradePoints;
    }

    public double getCredits() {
        return credits;
    }

    public void setCredits(double credits) {
        this.credits = credits;
    }
}
