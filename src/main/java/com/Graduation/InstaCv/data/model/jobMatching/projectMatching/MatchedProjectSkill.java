package com.Graduation.InstaCv.data.model.jobMatching.projectMatching;

import com.Graduation.InstaCv.data.model.job.JobSkill;
import com.Graduation.InstaCv.data.model.profile.ProjectSkill;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "matched_project_skills")
public class MatchedProjectSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "matched_project_id")
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private MatchedProject matchedProject;

    @ManyToOne
    @JoinColumn(name = "job_skill_id")
    private JobSkill jobSkill;

    @ManyToOne
    @JoinColumn(name = "project_skill_id")
    private ProjectSkill projectSkill;

    private Float similarity;
}
