package com.Graduation.InstaCv.service.Interfaces;

import com.Graduation.InstaCv.data.model.cv.TailoredCv;
import com.Graduation.InstaCv.data.dto.TailoredCvDto;

import java.util.List;

public interface ICvGenerationService {
    TailoredCv generateCv(Long userId, Long jobId);

    TailoredCv getCvByIdAndUserId(Long cvId, Long userId);

    List<TailoredCv> getCvsByUserId(Long userId);

    TailoredCv getCvByJobIdAndUserId(Long userId, Long jobId);

    TailoredCv updateCv(Long cvId, Long userId, TailoredCvDto tailoredCvDto);
}