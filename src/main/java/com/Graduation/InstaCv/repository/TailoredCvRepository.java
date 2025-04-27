package com.Graduation.InstaCv.repository;

import com.Graduation.InstaCv.data.model.cv.TailoredCv;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TailoredCvRepository extends JpaRepository<TailoredCv, Long> {
    List<TailoredCv> findByProfileId(Long userId);
    Optional<TailoredCv> findByIdAndProfileId(Long cvId, Long userId);

    Optional<TailoredCv> findByJobId(Long jobId);
}