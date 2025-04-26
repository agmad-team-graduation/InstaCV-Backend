package com.Graduation.InstaCv.repository;

import com.Graduation.InstaCv.data.model.job.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findJobsByProfileId(Long profileId);
}
