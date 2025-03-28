package com.Graduation.InstaCv.data.model;

import com.Graduation.InstaCv.data.model.profile.Education;
import com.Graduation.InstaCv.data.model.profile.Experience;
import com.Graduation.InstaCv.data.model.profile.PersonalDetails;
import com.Graduation.InstaCv.data.model.profile.Skill;
import com.Graduation.InstaCv.data.model.profile.Project;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tailored_cvs")
public class TailoredCv {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id")
    private Long userId;
    
    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job job;
    
    @Embedded
    private PersonalDetails personalDetails;
    
    @ElementCollection
    @CollectionTable(name = "cv_education", joinColumns = @JoinColumn(name = "cv_id"))
    private List<Education> education;
    
    @ElementCollection
    @CollectionTable(name = "cv_experience", joinColumns = @JoinColumn(name = "cv_id"))
    private List<Experience> experience;
    
    @ElementCollection
    @CollectionTable(name = "cv_skills", joinColumns = @JoinColumn(name = "cv_id"))
    private List<Skill> skills;
    
    @ElementCollection
    @CollectionTable(name = "cv_projects", joinColumns = @JoinColumn(name = "cv_id"))
    private List<Project> projects;
    
    private String summary;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "match_score")
    private Integer matchScore;
} 