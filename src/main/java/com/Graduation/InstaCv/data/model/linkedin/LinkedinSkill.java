package com.Graduation.InstaCv.data.model.linkedin;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkedinSkill {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    @ManyToOne
    @JoinColumn(name = "profile_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private LinkedinProfile profile;
}
