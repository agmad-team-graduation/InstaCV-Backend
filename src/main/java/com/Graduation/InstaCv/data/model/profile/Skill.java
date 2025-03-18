package com.Graduation.InstaCv.data.model.profile;

import com.Graduation.InstaCv.data.enums.SkillLevel;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Skill {
    @Column(nullable = false)
    private String skill;
    private SkillLevel level;
}
