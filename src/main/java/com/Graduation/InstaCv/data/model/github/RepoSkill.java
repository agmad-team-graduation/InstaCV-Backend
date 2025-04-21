package com.Graduation.InstaCv.data.model.github;

import com.Graduation.InstaCv.data.model.BaseSkill;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;


@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@SuperBuilder
@Table(name = "github_repo_skills")
public class RepoSkill extends BaseSkill {
    @ManyToOne(optional = false)
    @JoinColumn(name = "github_repository_id")
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    GithubRepository githubRepository;
}
