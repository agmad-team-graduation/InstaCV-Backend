package com.Graduation.InstaCv.repository;

import com.Graduation.InstaCv.data.model.RemoteOk.RemoteOkJob;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface RemoteOkJobRepository extends JpaRepository<RemoteOkJob, String> {

    /**
     * Find all job IDs to efficiently check for existing jobs
     */
    @Query("SELECT j.id FROM RemoteOkJob j")
    Set<String> findAllIds();

    /**
     * Find jobs containing a specific tag
     */
    @Query("SELECT DISTINCT j FROM RemoteOkJob j JOIN j.skills t WHERE t.skill = :tag")
    List<RemoteOkJob> findByTagName(@Param("tag") String tag);

    /**
     * Find jobs containing a specific tag (paginated)
     */
    @Query("SELECT DISTINCT j FROM RemoteOkJob j JOIN j.skills t WHERE t.skill = :tag")
    Page<RemoteOkJob> findByTagName(@Param("tag") String tag, Pageable pageable);

    /**
     * Find jobs containing a tag that matches the pattern
     */
    @Query("SELECT DISTINCT j FROM RemoteOkJob j JOIN j.skills t WHERE LOWER(t.skill) LIKE LOWER(CONCAT('%', :pattern, '%'))")
    List<RemoteOkJob> findByTagContaining(@Param("pattern") String pattern);

    /**
     * Find jobs containing a specific tag and sorted by date (recent first)
     */
    @Query("SELECT DISTINCT j FROM RemoteOkJob j JOIN j.skills t WHERE t.skill = :tag ORDER BY j.date DESC")
    List<RemoteOkJob> findByTagNameOrderByDateDesc(@Param("tag") String tag);

    /**
     * Find jobs containing any of the given tags
     */
    @Query("SELECT DISTINCT j FROM RemoteOkJob j JOIN j.skills t WHERE t.skill IN :tags")
    List<RemoteOkJob> findByTagsIn(@Param("tags") List<String> tags);

    /**
     * Find jobs for a specific company
     */
    List<RemoteOkJob> findByCompany(String company);

    /**
     * Find jobs with title containing the search term
     */
    List<RemoteOkJob> findByTitleContainingIgnoreCase(String titlePattern);

    /**
     * Find jobs with description containing the search term
     */
    List<RemoteOkJob> findByDescriptionContainingIgnoreCase(String searchTerm);

    /**
     * Find a job with the exact title and company
     */
    Optional<RemoteOkJob> findByTitleAndCompany(String title, String company);

    /**
     * Find jobs with salary in a specific range
     */
    @Query("SELECT j FROM RemoteOkJob j WHERE " +
            "(j.salaryMin IS NOT NULL AND j.salaryMin >= :minSalary) OR " +
            "(j.salaryMax IS NOT NULL AND j.salaryMax <= :maxSalary)")
    List<RemoteOkJob> findBySalaryRange(@Param("minSalary") Integer minSalary,
                                        @Param("maxSalary") Integer maxSalary);

    /**
     * Count jobs by tag
     */
    @Query("SELECT t.skill, COUNT(j) FROM RemoteOkJob j JOIN j.skills t GROUP BY t.skill ORDER BY COUNT(j) DESC")
    List<Object[]> countJobsByTag();

    /**
     * Find most recent jobs
     */
    @Query("SELECT j FROM RemoteOkJob j ORDER BY j.date DESC")
    List<RemoteOkJob> findMostRecentJobs(Pageable pageable);

    /**
     * Delete jobs older than a specific date
     */
    @Query("DELETE FROM RemoteOkJob j WHERE j.date < :date")
    void deleteJobsOlderThan(@Param("date") String date);
}