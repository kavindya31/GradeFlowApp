package com.example.mobileapp.models;

/**
 * Model class representing a User in the GradeFlow application.
 * Stores user profile, registration, and academic summary information.
 */
public class user {
    private String fullName;
    private String university;
    private String email;
    private String password;
    private String degree;
    private String year;
    private String gpa;

    // Default constructor
    public user() {
    }

    // Parametric constructor for registration
    public user(String fullName, String university, String email, String password) {
        this.fullName = fullName;
        this.university = university;
        this.email = email;
        this.password = password;
        this.degree = "";
        this.year = "";
        this.gpa = "0.0";
    }

    // Complete constructor for full profile
    public user(String fullName, String university, String email, String password, String degree, String year, String gpa) {
        this.fullName = fullName;
        this.university = university;
        this.email = email;
        this.password = password;
        this.degree = degree;
        this.year = year;
        this.gpa = gpa;
    }

    // Getters and Setters
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getGpa() {
        return gpa;
    }

    public void setGpa(String gpa) {
        this.gpa = gpa;
    }
}
