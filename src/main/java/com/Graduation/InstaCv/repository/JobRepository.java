package com.Graduation.InstaCv.repository;

import com.Graduation.InstaCv.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, Long> {
}
