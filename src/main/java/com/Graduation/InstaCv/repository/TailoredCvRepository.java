package com.Graduation.InstaCv.repository;

import com.Graduation.InstaCv.data.model.TailoredCv;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TailoredCvRepository extends JpaRepository<TailoredCv, Long> {
    List<TailoredCv> findByUserId(Long userId);
    Optional<TailoredCv> findByUserIdAndJobId(Long userId, Long jobId);
} 