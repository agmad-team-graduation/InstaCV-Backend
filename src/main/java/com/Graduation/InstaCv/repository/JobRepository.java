package com.Graduation.InstaCv.repository;

import com.Graduation.InstaCv.data.model.job.Job;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findJobsByProfileId(Long profileId);

    Optional<Job> findJobByIdAndProfileId(Long jobId, Long profileId);

    Optional<Job> findJobByIdAndProfileIsNull(Long jobId);

    boolean existsByIdAndProfileId(Long jobId, Long profileId);

    @Query("SELECT j FROM Job j JOIN j.remoteJobData r WHERE j.profile is null ORDER BY r.date DESC")
    List<Job> findAllRemoteJobsSortedByDateDesc();

    @Query("SELECT j FROM Job j JOIN j.skillMatchingAnalyses a WHERE j.profile IS NULL AND a.profile.id = :profileId")
    List<Job> findAnalyzedScrapedJobsByProfileId(@Param("profileId") Long profileId);

    @Query("SELECT COUNT(a) > 0 FROM Job j JOIN j.skillMatchingAnalyses a WHERE j.id = :jobId AND a.profile.id = :profileId")
    boolean existsJobSkillMatchingAnalysis(@Param("jobId") Long jobId, @Param("profileId") Long profileId);

    @Query("SELECT COUNT(a) > 0 FROM Job j JOIN j.projectMatchingAnalyses a WHERE j.id = :jobId AND a.profile.id = :profileId")
    boolean existsJobProjectMatchingAnalysis(@Param("jobId") Long jobId, @Param("profileId") Long profileId);
}
