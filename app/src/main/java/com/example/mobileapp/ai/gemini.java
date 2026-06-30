package com.example.mobileapp.ai;

import com.example.mobileapp.models.semester;
import com.example.mobileapp.models.subject;
import com.example.mobileapp.models.user;
import com.example.mobileapp.utils.PerformanceAnalyzer;
import java.util.ArrayList;
import java.util.List;

/**
 * Placeholder and local simulator for the Gemini AI model.
 * Performs local query parsing against the student's profile context
 * to generate relevant academic study recommendations.
 */
public class gemini {

    /**
     * Generates a smart academic study advice response based on user query and prompt context.
     *
     * @param u The active user profile
     * @param semesters Historical semesters list
     * @param userQuery The chat message sent by the user
     * @return Simulated academic recommendation response
     */
    public static String generateRecommendation(user u, List<semester> semesters, String userQuery) {
        if (u == null) {
            return "Hello! I am your GradeFlow AI assistant. Please complete your registration/profile to get started.";
        }

        String query = userQuery.toLowerCase().trim();

        // Detect weak subjects
        List<subject> allSubjects = new ArrayList<>();
        if (semesters != null) {
            for (semester sem : semesters) {
                allSubjects.addAll(sem.getSubjects());
            }
        }
        List<subject> weak = PerformanceAnalyzer.detectWeakSubjects(allSubjects);
        String trend = PerformanceAnalyzer.analyzeGpaTrend(semesters);

        // 1. Check if user is asking about weak subjects
        if (query.contains("weak") || query.contains("fail") || query.contains("struggle") || query.contains("poor")) {
            if (weak.isEmpty()) {
                return "Fantastic news, " + u.getFullName() + "! You don't have any weak subjects (grades below B-) in your history. You are in excellent academic standing!";
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("Hi ").append(u.getFullName()).append(", I detected the following weak subjects (grades below B-):\n");
                for (subject s : weak) {
                    sb.append("- ").append(s.getSubjectName()).append(" (Grade: ").append(s.getGrade()).append(")\n");
                }
                sb.append("\nI suggest starting with a structured study routine. Focus on these subjects first for at least 2 hours daily.");
                return sb.toString();
            }
        }

        // 2. Check if user is asking about GPA or progress
        if (query.contains("gpa") || query.contains("cgpa") || query.contains("grade") || query.contains("score")) {
            double gpaVal = 0.0;
            try {
                gpaVal = Double.parseDouble(u.getGpa());
            } catch (NumberFormatException ignored) {}

            if (gpaVal >= 3.5) {
                return "Your current Cumulative GPA is " + u.getGpa() + " (" + trend + "). You are doing exceptionally well at " + u.getUniversity() + "! Continue your active recall study methods to keep it up.";
            } else if (gpaVal >= 2.0) {
                return "Your current Cumulative GPA is " + u.getGpa() + " (" + trend + "). There is room for growth! I recommend prioritizing subjects with higher credits to maximize your score quickly.";
            } else {
                return "Your current Cumulative GPA is " + u.getGpa() + ". I recommend scheduling a daily study session in your planner and using active recall to boost your grades.";
            }
        }

        // 3. Check if user asks for study plan / advice
        if (query.contains("plan") || query.contains("study") || query.contains("schedule") || query.contains("recommend")) {
            if (!weak.isEmpty()) {
                return "Based on your performance, here is your study plan:\n" +
                       "1. Prioritize " + weak.get(0).getSubjectName() + " (Focus on core concepts).\n" +
                       "2. Devote 45 minutes to active practice problems daily.\n" +
                       "3. Track your midterm progress using our Study Planner.";
            } else {
                return "Here is your study plan:\n" +
                       "1. Maintain your steady review cycle.\n" +
                       "2. Dedicate 30 minutes daily to previewing upcoming lecture content.\n" +
                       "3. Do practice quizzes regularly to consolidate your understanding.";
            }
        }

        // 4. Greetings
        if (query.contains("hello") || query.contains("hi") || query.contains("hey")) {
            return "Hello " + u.getFullName() + "! I'm your GradeFlow AI assistant. Ask me about your weak subjects, study plans, or GPA trends!";
        }

        // Default response
        return "I've analyzed your academic records. You are enrolled at " + u.getUniversity() + 
               " with a GPA of " + u.getGpa() + " (" + trend + "). " +
               (weak.isEmpty() ? "All your subjects are in good standing." : "I suggest focusing on your weak areas: " + weak.get(0).getSubjectName() + ".") +
               " Ask me specifically about your 'weak subjects' or for a 'study plan'.";
    }
}
