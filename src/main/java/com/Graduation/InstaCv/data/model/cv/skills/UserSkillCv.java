package com.Graduation.InstaCv.data.model.cv.skills;

import com.Graduation.InstaCv.data.enums.SkillLevel;
import com.Graduation.InstaCv.data.model.cv.SkillSection;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tailored_cv_skills")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSkillCv {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "section_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private SkillSection section;

    private String skill;

    @Column(name = "order_index")
    private int orderIndex;

    @Enumerated(EnumType.STRING)
    private SkillLevel level;
}