package com.Graduation.InstaCv.repository;

import com.Graduation.InstaCv.data.model.github.RepoSkill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GithubSkillRepository extends JpaRepository<RepoSkill, Long> {
    Optional<RepoSkill> findBySkill(String name);
}
