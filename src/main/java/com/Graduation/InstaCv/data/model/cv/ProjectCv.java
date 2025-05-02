package com.Graduation.InstaCv.data.model.cv;

import com.Graduation.InstaCv.data.model.cv.skills.ProjectSkillCv;
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

    @ManyToOne
    @JoinColumn(name = "section_id")
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ProjectSection section;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;
    
    @Column(name = "order_index")
    private int orderIndex;

    @Column(name = "is_present")
    private boolean isPresent;

    private String description;

    @OneToMany(mappedBy = "projectCv", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectSkillCv> skills;
}
