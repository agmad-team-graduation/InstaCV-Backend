package com.Graduation.InstaCv.data.model.cv.sections;

import com.Graduation.InstaCv.data.model.cv.items.ProjectCv;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "project_sections")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectSection extends CvSection {
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    private List<ProjectCv> items = List.of();
}