package com.Graduation.InstaCv.repository;

import com.Graduation.InstaCv.data.model.job.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findJobsByProfileId(Long profileId);
    Optional<Job> findJobByIdAndProfileId(Long jobId, Long profileId);
    boolean existsByIdAndProfileId(Long jobId, Long profileId);
    List<Job> findAllByRemoteJobDataIsNotNull();
}
