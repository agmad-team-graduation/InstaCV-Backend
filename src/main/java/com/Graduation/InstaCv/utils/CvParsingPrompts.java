package com.Graduation.InstaCv.utils;

import org.springframework.stereotype.Component;

@Component
public class CvParsingPrompts {

    public String getCvParsingPrompt(String cvText) {
        return """
            You are a CV parser. Extract information from this CV and return ONLY a valid JSON object.
            
            Required JSON structure:
            %s
            
            CV TEXT:
            %s
            
            Rules:
            %s
            """.formatted(getJsonStructure(), cvText, getRules());
    }

    private String getJsonStructure() {
        return """
            {
                "personalDetails": {
                    "fullName": "extract full name",
                    "email": "extract email address",
                    "phone": "extract phone number",
                    "address": "extract address or location"
                },
                "educationList": [
                    {
                        "degree": "degree or qualification name",
                        "school": "school/university name",
                        "city": "city where school is located",
                        "country": "country where school is located",
                        "startDate": "YYYY-MM-DD format",
                        "endDate": "YYYY-MM-DD format or null if ongoing",
                        "isPresent": false,
                        "description": "any additional details or achievements"
                    }
                ],
                "experienceList": [
                    {
                        "jobTitle": "position/role title",
                        "company": "company name",
                        "city": "city where job is/was located",
                        "country": "country where job is/was located",
                        "startDate": "YYYY-MM-DD format",
                        "endDate": "YYYY-MM-DD format or null if current",
                        "isPresent": false,
                        "description": "job responsibilities and achievements"
                    }
                ],
                "userSkills": [
                    {
                        "skill": "skill name",
                        "level": "EXPERT"
                    }
                ],
                "projects": [
                    {
                        "title": "project name",
                        "startDate": "YYYY-MM-DD format or null",
                        "endDate": "YYYY-MM-DD format or null",
                        "isPresent": false,
                        "description": "project description and achievements",
                        "skills": [
                            {
                                "skill": "technology or skill used"
                            }
                        ]
                    }
                ]
            }""";
    }

    private String getRules() {
        return """
            - Return ONLY the JSON object, no explanations or markdown
            - Use null for missing values, never use empty strings
            - For skill levels, use only: BEGINNER, INTERMEDIATE, ADVANCED, or EXPERT
            - Format ALL dates as YYYY-MM-DD (e.g., 2023-01-15)
            - If only year is mentioned, use YYYY-01-01
            - If only month and year, use YYYY-MM-01
            - Set isPresent to true if the position/education/project is ongoing (no end date)
            - If end date is "Present", "Current", "Now", or similar, set endDate to null and isPresent to true
            - Extract city and country separately when possible
            - If city is not mentioned but country is, set city to null
            - For projects, extract technologies used as individual skill objects
            - Ensure all arrays are present even if empty""";
    }

}