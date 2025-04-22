package com.Graduation.InstaCv.data.model.cv.skills;

import com.Graduation.InstaCv.data.enums.SkillLevel;
import com.Graduation.InstaCv.data.model.cv.TailoredCv;
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

    private String skill;

    @Enumerated(EnumType.STRING)
    private SkillLevel level;

    @ManyToOne
    @JoinColumn(name = "tailored_cv_id")
    @ToString.Exclude
    @JsonIgnore
    private TailoredCv cv;
}