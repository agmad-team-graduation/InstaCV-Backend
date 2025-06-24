package com.Graduation.InstaCv.repository;

import com.Graduation.InstaCv.data.model.cv.TailoredCv;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TailoredCvRepository extends JpaRepository<TailoredCv, Long> {
    List<TailoredCv> findByProfileId(Long userId);
    Optional<TailoredCv> findByJobIdAndProfileId(Long jobId, Long userId);
    Optional<TailoredCv> findByIdAndProfileId(Long cvId, Long userId);
    @Modifying
    @Query("UPDATE TailoredCv t SET t.cvTitle = :title WHERE t.id = :id")
    void updateCvTitle(@Param("id") Long id, @Param("title") String title);

    @Query("SELECT COUNT(t) FROM TailoredCv t WHERE t.profile.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(t) FROM TailoredCv t WHERE t.profile.user.id = :userId AND t.createdAt >= :fromDate")
    long countCvsCreatedAfterByUserId(@Param("userId") Long userId, @Param("fromDate") LocalDateTime fromDate);
}