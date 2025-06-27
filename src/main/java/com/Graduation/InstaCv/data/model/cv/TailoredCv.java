package com.Graduation.InstaCv.data.model.cv;

import com.Graduation.InstaCv.data.model.cv.sections.*;
import com.Graduation.InstaCv.data.model.job.Job;
import com.Graduation.InstaCv.data.model.profile.PersonalDetails;
import com.Graduation.InstaCv.data.model.profile.Profile;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tailored_cvs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TailoredCv {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cvTitle;

    @ManyToOne
    @JoinColumn(name = "profile_id", referencedColumnName = "id")
    private Profile profile;

    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job job;

    @Embedded
    private PersonalDetails personalDetails;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private SummarySection summarySection;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private EducationSection educationSection;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private ExperienceSection experienceSection;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private SkillSection skillSection;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private ProjectSection projectSection;

    @Embedded
    private CvSettings cvSettings;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
