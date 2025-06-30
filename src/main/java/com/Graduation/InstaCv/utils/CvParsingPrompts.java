package com.Graduation.InstaCv.utils;

import org.springframework.stereotype.Component;

@Component
public class CvParsingPrompts {

    public String getCvParsingPrompt(String cvText) {
        return """
            You are an advanced CV parser designed to extract and format information into a precise JSON structure. 

            Required JSON format:
            %s

            CV TEXT:
            %s

            Parsing Instructions:
            %s
            """.formatted(getJsonStructure(), cvText, getImprovedRules());
    }

    private String getJsonStructure() {
        return """
            {
                "personalDetails": {
                    "fullName": "extract full name",
                    "email": "extract email address",
                    "phone": "extract phone number",
                    "address": "extract detailed address or location"
                },
                "educationList": [
                    {
                        "degree": "degree or qualification",
                        "school": "institution name",
                        "city": "city of institution",
                        "country": "country of institution",
                        "startDate": "YYYY-MM-DD",
                        "endDate": "YYYY-MM-DD or null if ongoing",
                        "isPresent": false,
                        "description": "additional details or accomplishments"
                    }
                ],
                "experienceList": [
                    {
                        "jobTitle": "role or position title",
                        "company": "organization name",
                        "city": "location city",
                        "country": "location country",
                        "startDate": "YYYY-MM-DD",
                        "endDate": "YYYY-MM-DD or null if current",
                        "isPresent": false,
                        "description": "responsibilities and achievements"
                    }
                ],
                "userSkills": [
                    {
                        "skill": "skill name",
                        "level": "INTERMEDIATE"
                    }
                ],
                "projects": [
                    {
                        "title": "project title",
                        "startDate": "YYYY-MM-DD or null",
                        "endDate": "YYYY-MM-DD or null",
                        "isPresent": false,
                        "description": "project details and accomplishments",
                        "skills": [
                            {
                                "skill": "technology or skill utilized"
                            }
                        ]
                    }
                ]
            }""";
    }

    private String getImprovedRules() {
        return """
            - Output a clean JSON object only, without additional text or markdown formats.
            - Use null explicitly for any missing data; do not use empty strings.
            - Skill levels must be categorized as: BEGINNER, INTERMEDIATE, ADVANCED, or EXPERT.
            - Format all dates strictly as YYYY-MM-DD (e.g., 2023-01-15); adapt when only partial date info is provided (e.g., YYYY-MM-01).
            - Set isPresent to true if the activity is ongoing (no explicit end date).
            - For unspecified end dates (like "Present"), set endDate to null and isPresent to true.
            - Separate city and country details; if only country is specified, set city to null.
            - Use projects to derive skills as distinct objects where applicable.
            - Guarantee that all list fields (arrays) are included in the output even if they are empty.
            """;
    }
}