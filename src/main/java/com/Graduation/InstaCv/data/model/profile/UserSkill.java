package com.Graduation.InstaCv.data.model.profile;

import com.Graduation.InstaCv.data.enums.SkillLevel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user_skills")
public class UserSkill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "profile_id")
    @ToString.Exclude
    @JsonIgnore
    private Profile profile;

    @Column(nullable = false, name = "user_skill")
    private String skill;

    @Enumerated(EnumType.STRING)
    private SkillLevel level;
}