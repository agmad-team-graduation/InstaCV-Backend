package com.Graduation.InstaCv.repository;

import com.Graduation.InstaCv.data.model.job.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, Long> {
}
