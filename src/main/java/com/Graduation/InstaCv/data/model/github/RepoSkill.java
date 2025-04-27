package com.Graduation.InstaCv.data.model.github;

import com.Graduation.InstaCv.data.model.BaseSkill;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;


@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@SuperBuilder
@Table(name = "github_repo_skills")
public class RepoSkill extends BaseSkill {
}
