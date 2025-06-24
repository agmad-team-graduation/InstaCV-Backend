package com.Graduation.InstaCv.data.model.cv.sections;

import com.Graduation.InstaCv.data.model.cv.items.ExperienceCv;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Table(name = "experience_sections")
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ExperienceSection extends CvSection {
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    @Builder.Default
    private List<ExperienceCv> items = List.of();
} 