package com.Graduation.InstaCv.data.model.cv.sections;

import com.Graduation.InstaCv.data.model.cv.skills.UserSkillCv;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "skill_sections")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SkillSection extends CvSection {
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    private List<UserSkillCv> items = List.of();
} 