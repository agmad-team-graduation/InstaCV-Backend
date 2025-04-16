package com.Graduation.InstaCv.data.model.profile;

import com.Graduation.InstaCv.data.enums.SkillLevel;
import com.Graduation.InstaCv.data.model.BaseSkill;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "user_skills")
public class UserSkill extends BaseSkill {

    @ManyToOne(optional = false)
    @JoinColumn(name = "profile_id")
    @ToString.Exclude
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    private Profile profile;

    @Enumerated(EnumType.STRING)
    private SkillLevel level;
}