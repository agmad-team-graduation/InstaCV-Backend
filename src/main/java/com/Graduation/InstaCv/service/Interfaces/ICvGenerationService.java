package com.Graduation.InstaCv.service.Interfaces;

import com.Graduation.InstaCv.data.dto.TailoredCvDto;
import com.Graduation.InstaCv.data.model.TailoredCv;

import java.util.List;

public interface ICvGenerationService {
    TailoredCv generateCv(Long userId, Long jobId);
    TailoredCv getCvById(Long cvId);
    List<TailoredCv> getCvsByUserId(Long userId);
    TailoredCv getCvByUserIdAndJobId(Long userId, Long jobId);
} 