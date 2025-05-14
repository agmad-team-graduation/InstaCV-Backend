package com.Graduation.InstaCv.repository;

import com.Graduation.InstaCv.data.model.job.Job;
import feign.Param;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    @Query("SELECT r.remoteId FROM RemoteJobData r")
    Set<String> findAllRemoteIds();

    @Query("SELECT j FROM Job j JOIN j.remoteJobData r WHERE j.profile IS NULL AND r.date >= :fromDate")
    List<Job> findRecentRemoteJobs(@Param("fromDate") OffsetDateTime fromDate);

    @Query("SELECT a.profile.id FROM SkillMatchingAnalysis a WHERE a.job.id = :jobId")
    Set<Long> findProfileIdsAnalyzedForJob(@Param("jobId") Long jobId);

    @Query("SELECT j FROM Job j WHERE j.remoteJobData IS NOT NULL")
    Page<Job> findAllRemoteJobs(Pageable pageable);
}
