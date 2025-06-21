package com.Graduation.InstaCv.data.model.jobMatching.skillMatching;


import com.Graduation.InstaCv.data.model.job.Job;
import com.Graduation.InstaCv.data.model.job.JobSkill;
import com.Graduation.InstaCv.data.model.profile.Profile;
import com.Graduation.InstaCv.data.model.profile.UserSkill;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;


import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "skill_matching_analyses")
public class SkillMatchingAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "job_id", nullable = false)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Job job;

    @ManyToOne(optional = false)
    @JoinColumn(name = "profile_id", nullable = false)
    @JsonIgnore
    private Profile profile;

    @OneToMany(mappedBy = "skillMatchingAnalysis", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MatchedSkill> matchedSkills;

    @OneToMany
    @JoinColumn(name = "unmatched_analysis_id")
    private List<JobSkill> unmatchedJobSkills;

    @ManyToMany
    @JoinTable(
            name = "unmatched_user_skills_map",
            joinColumns = @JoinColumn(name = "analysis_id"),
            inverseJoinColumns = @JoinColumn(name = "user_skill_id")
    )
    private List<UserSkill> unmatchedUserSkills;

    public float getMatchedSkillsPercentage() {
        if (matchedSkills == null || job.getJobSkills() == null || job.getJobSkills().isEmpty())
            return 0f;
        return (float) matchedSkills.size() / job.getHardSkills().size() * 100;
    }
}
