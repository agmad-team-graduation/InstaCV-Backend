package com.Graduation.InstaCv.data.model.profile;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile {
    @Embedded
    private PersonalDetails personalDetails;
    @ElementCollection
    @CollectionTable(name = "user_education", joinColumns = @JoinColumn(name = "user_id"))
    private List<Education> educationList;
    @ElementCollection
    @CollectionTable(name = "user_experience", joinColumns = @JoinColumn(name = "user_id"))
    private List<Experience> experienceList;
    @ElementCollection
    @CollectionTable(name = "user_skills", joinColumns = @JoinColumn(name = "user_id"))
    private List<Skill> skills;
    @ElementCollection
    @CollectionTable(name = "user_projects", joinColumns = @JoinColumn(name = "user_id"))
    private List<Project> projects;
}
