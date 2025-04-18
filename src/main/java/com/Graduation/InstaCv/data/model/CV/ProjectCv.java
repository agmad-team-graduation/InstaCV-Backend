package com.Graduation.InstaCv.data.model.CV;

import com.Graduation.InstaCv.data.model.CV.skills.ProjectSkillCv;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "cv_projects")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectCv {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "is_present")
    private boolean isPresent;

    private String description;

    @ManyToOne
    @JoinColumn(name = "tailored_cv_id")
    @ToString.Exclude
    @JsonIgnore
    private TailoredCv cv;

    @OneToMany(mappedBy = "projectCv", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectSkillCv> skills;
}
