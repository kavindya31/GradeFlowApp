package com.example.mobileapp.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Model class representing a Semester in GradeFlow.
 * Accumulates subjects studied during the term and tracks the calculated GPA.
 */
public class semester {
    private String semesterName; // e.g. "Semester 1"
    private String year; // e.g. "Year 1", "Year 2"
    private List<subject> subjects;
    private double gpa; // Grade Point Average of the semester

    // Default constructor
    public semester() {
        this.subjects = new ArrayList<>();
    }

    // Complete constructor
    public semester(String semesterName, String year) {
        this.semesterName = semesterName;
        this.year = year;
        this.subjects = new ArrayList<>();
        this.gpa = 0.0;
    }

    public semester(String semesterName, String year, List<subject> subjects, double gpa) {
        this.semesterName = semesterName;
        this.year = year;
        this.subjects = subjects;
        this.gpa = gpa;
    }

    // Getters and Setters
    public String getSemesterName() {
        return semesterName;
    }

    public void setSemesterName(String semesterName) {
        this.semesterName = semesterName;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public List<subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<subject> subjects) {
        this.subjects = subjects;
    }

    public void addSubject(subject s) {
        if (this.subjects == null) {
            this.subjects = new ArrayList<>();
        }
        this.subjects.add(s);
    }

    public double getGpa() {
        return gpa;
    }

    public void setGpa(double gpa) {
        this.gpa = gpa;
    }
}
