package com.Graduation.InstaCv.repository;

import com.Graduation.InstaCv.data.model.linkedin.LinkedinProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LinkedinProfileRepository extends JpaRepository<LinkedinProfile, String> {
}