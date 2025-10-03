package com.atsoptimizer.jobanalyzer.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class KeywordExtractionService {

    // Common technical skills
    private static final Set<String> TECHNICAL_SKILLS = new HashSet<>(Arrays.asList(
            "java", "python", "javascript", "typescript", "kotlin", "swift", "c++", "c#", "go", "rust",
            "react", "angular", "vue", "spring", "spring boot", "django", "flask", "node.js", "express",
            "sql", "nosql", "postgresql", "mysql", "mongodb", "redis", "elasticsearch",
            "aws", "azure", "gcp", "docker", "kubernetes", "jenkins", "git", "ci/cd",
            "rest api", "graphql", "microservices", "agile", "scrum", "devops",
            "machine learning", "ai", "data science", "tensorflow", "pytorch",
            "html", "css", "sass", "webpack", "babel", "npm", "yarn"
    ));

    // Experience level indicators
    private static final Map<String, String> EXPERIENCE_LEVELS = new HashMap<>() {{
        put("junior|entry|graduate|0-2 years", "Entry");
        put("mid|intermediate|2-5 years|3-5 years", "Mid");
        put("senior|lead|principal|5\\+ years|6\\+ years", "Senior");
        put("staff|architect|distinguished", "Staff");
    }};

    // Education level indicators
    private static final Map<String, String> EDUCATION_LEVELS = new HashMap<>() {{
        put("bachelor|bs|ba|b\\.s\\.|b\\.a\\.", "Bachelor's");
        put("master|ms|ma|m\\.s\\.|m\\.a\\.|mba", "Master's");
        put("phd|doctorate|ph\\.d\\.", "PhD");
        put("associate|aa|as|a\\.a\\.|a\\.s\\.", "Associate");
    }};

    /**
     * Extract keywords from job description
     */
    public List<String> extractKeywords(String text) {
        if (text == null || text.isEmpty()) {
            return Collections.emptyList();
        }

        String lowerText = text.toLowerCase();
        Set<String> foundKeywords = new HashSet<>();

        // Extract technical skills
        for (String skill : TECHNICAL_SKILLS) {
            if (lowerText.contains(skill.toLowerCase())) {
                foundKeywords.add(skill);
            }
        }

        // Extract years of experience mentions
        Pattern yearsPattern = Pattern.compile("(\\d+)\\+?\\s*years?");
        Matcher matcher = yearsPattern.matcher(lowerText);
        while (matcher.find()) {
            foundKeywords.add(matcher.group() + " experience");
        }

        // Remove duplicates and sort
        return foundKeywords.stream()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Extract required skills (skills mentioned with "required", "must have", etc.)
     */
    public List<String> extractRequiredSkills(String text) {
        if (text == null || text.isEmpty()) {
            return Collections.emptyList();
        }

        String lowerText = text.toLowerCase();
        Set<String> requiredSkills = new HashSet<>();

        // Look for required/must-have sections
        String[] requiredIndicators = {
                "required", "must have", "must-have", "mandatory", "essential"
        };

        for (String indicator : requiredIndicators) {
            int index = lowerText.indexOf(indicator);
            if (index != -1) {
                // Extract text around the indicator (next 500 chars)
                int endIndex = Math.min(index + 500, lowerText.length());
                String section = lowerText.substring(index, endIndex);

                // Find technical skills in this section
                for (String skill : TECHNICAL_SKILLS) {
                    if (section.contains(skill.toLowerCase())) {
                        requiredSkills.add(skill);
                    }
                }
            }
        }

        return new ArrayList<>(requiredSkills);
    }

    /**
     * Extract preferred skills (skills mentioned with "preferred", "nice to have", etc.)
     */
    public List<String> extractPreferredSkills(String text) {
        if (text == null || text.isEmpty()) {
            return Collections.emptyList();
        }

        String lowerText = text.toLowerCase();
        Set<String> preferredSkills = new HashSet<>();

        String[] preferredIndicators = {
                "preferred", "nice to have", "nice-to-have", "bonus", "plus", "desired"
        };

        for (String indicator : preferredIndicators) {
            int index = lowerText.indexOf(indicator);
            if (index != -1) {
                int endIndex = Math.min(index + 500, lowerText.length());
                String section = lowerText.substring(index, endIndex);

                for (String skill : TECHNICAL_SKILLS) {
                    if (section.contains(skill.toLowerCase())) {
                        preferredSkills.add(skill);
                    }
                }
            }
        }

        return new ArrayList<>(preferredSkills);
    }

    /**
     * Detect experience level from job description
     */
    public String detectExperienceLevel(String text) {
        if (text == null || text.isEmpty()) {
            return "Not Specified";
        }

        String lowerText = text.toLowerCase();

        for (Map.Entry<String, String> entry : EXPERIENCE_LEVELS.entrySet()) {
            Pattern pattern = Pattern.compile(entry.getKey());
            if (pattern.matcher(lowerText).find()) {
                return entry.getValue();
            }
        }

        return "Not Specified";
    }

    /**
     * Detect education level from job description
     */
    public String detectEducationLevel(String text) {
        if (text == null || text.isEmpty()) {
            return "Not Specified";
        }

        String lowerText = text.toLowerCase();

        for (Map.Entry<String, String> entry : EDUCATION_LEVELS.entrySet()) {
            Pattern pattern = Pattern.compile(entry.getKey());
            if (pattern.matcher(lowerText).find()) {
                return entry.getValue();
            }
        }

        return "Not Specified";
    }

    /**
     * Calculate keyword frequency for ranking purposes
     */
    public Map<String, Integer> getKeywordFrequency(String text) {
        Map<String, Integer> frequency = new HashMap<>();
        String lowerText = text.toLowerCase();

        for (String skill : TECHNICAL_SKILLS) {
            int count = countOccurrences(lowerText, skill.toLowerCase());
            if (count > 0) {
                frequency.put(skill, count);
            }
        }

        return frequency.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    private int countOccurrences(String text, String keyword) {
        int count = 0;
        int index = 0;

        while ((index = text.indexOf(keyword, index)) != -1) {
            count++;
            index += keyword.length();
        }

        return count;
    }
}