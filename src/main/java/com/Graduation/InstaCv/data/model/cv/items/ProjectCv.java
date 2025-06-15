package com.Graduation.InstaCv.data.model.cv.items;

import com.Graduation.InstaCv.data.model.cv.skills.ProjectSkillCv;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "cv_projects")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectCv implements CvItem {
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

    @Column(name = "is_hidden")
    @Builder.Default
    private boolean isHidden = false;

    @Column(name = "description", length = 4000)
    private String description;

    @OneToMany(mappedBy = "projectCv", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectSkillCv> skills;

    @Column(name = "order_index")
    private Integer orderIndex;
}
