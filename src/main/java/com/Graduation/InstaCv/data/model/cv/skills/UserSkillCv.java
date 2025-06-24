package com.Graduation.InstaCv.data.model.cv.skills;

import com.Graduation.InstaCv.data.enums.SkillLevel;
import com.Graduation.InstaCv.data.model.cv.items.CvItem;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tailored_cv_skills")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSkillCv implements CvItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String skill;

    @Enumerated(EnumType.STRING)
    private SkillLevel level;

    @Column(name = "is_hidden")
    @Builder.Default
    private boolean isHidden = false;

    @Column(name = "order_index")
    private Integer orderIndex;
}