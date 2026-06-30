package com.example.mobileapp.utils;

import com.example.mobileapp.models.semester;
import com.example.mobileapp.models.subject;
import com.example.mobileapp.models.user;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to build structured prompts for the Gemini AI recommendation system.
 * Aggregates user profile data, GPA trends, and academic weak areas to formulate context.
 */
public class PromptBuilder {

    /**
     * Constructs a comprehensive prompt string containing user profile,
     * historical performance, and weak areas.
     *
     * @param u The active user profile
     * @param semesters Historical semester and subject records
     * @return Formatted context prompt for the AI model
     */
    public static String buildStudyRecommendationPrompt(user u, List<semester> semesters) {
        if (u == null) return "User profile not found.";

        StringBuilder sb = new StringBuilder();
        
        // 1. System Context & User Profile
        sb.append("You are GradeFlow AI, an advanced academic mentor. Analyze this student's profile:\n\n");
        sb.append("- Name: ").append(u.getFullName()).append("\n");
        sb.append("- University: ").append(u.getUniversity()).append("\n");
        sb.append("- Degree: ").append(u.getDegree()).append("\n");
        sb.append("- Current Year: ").append(u.getYear()).append("\n");
        sb.append("- Cumulative GPA: ").append(u.getGpa()).append("\n\n");

        // 2. Semester History
        sb.append("### Academic History:\n");
        if (semesters == null || semesters.isEmpty()) {
            sb.append("No historical semester data available yet.\n\n");
        } else {
            for (semester sem : semesters) {
                sb.append("- ").append(sem.getSemesterName())
                  .append(" (").append(sem.getYear()).append("): ")
                  .append("GPA ").append(String.format("%.2f", sem.getGpa())).append("\n");
                
                for (subject s : sem.getSubjects()) {
                    sb.append("  * ").append(s.getSubjectName())
                      .append(": Grade ").append(s.getGrade())
                      .append(" (").append(s.getCredits()).append(" credits)\n");
                }
            }
            sb.append("\n");
        }

        // 3. Performance Analysis Context
        List<subject> allSubjects = new ArrayList<>();
        if (semesters != null) {
            for (semester sem : semesters) {
                allSubjects.addAll(sem.getSubjects());
            }
        }

        List<subject> weakSubjects = PerformanceAnalyzer.detectWeakSubjects(allSubjects);
        String trend = PerformanceAnalyzer.analyzeGpaTrend(semesters);

        sb.append("### Performance Insights:\n");
        sb.append("- GPA Trend: ").append(trend).append("\n");
        sb.append("- Weak Areas Identified (Grade below B-/2.7):\n");
        
        if (weakSubjects.isEmpty()) {
            sb.append("  No specific weak subjects identified. Doing great!\n");
        } else {
            for (subject s : weakSubjects) {
                sb.append("  * ").append(s.getSubjectName())
                  .append(" (Grade: ").append(s.getGrade()).append(")\n");
            }
        }
        
        sb.append("\n### Instructions:\n");
        sb.append("Provide a short, structured study recommendation (3-4 sentences max). ")
          .append("Offer 1 action plan for weak subjects if any exist, suggest study hours, ")
          .append("and motivate the student based on their GPA trend.");

        return sb.toString();
    }
}
