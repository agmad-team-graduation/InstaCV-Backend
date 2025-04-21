package com.Graduation.InstaCv.repository;

import com.Graduation.InstaCv.data.model.github.GithubProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GithubProfileRepository extends JpaRepository<GithubProfile, Long> {
    Optional<GithubProfile> findByUsername(String username);
}
