package com.Graduation.InstaCv.data.model.cv.sections;

import com.Graduation.InstaCv.data.model.cv.items.EducationCv;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Table(name = "education_sections")
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class EducationSection extends CvSection {
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    private List<EducationCv> items = List.of();
} 