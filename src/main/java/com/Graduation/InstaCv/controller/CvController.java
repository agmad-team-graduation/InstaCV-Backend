package com.Graduation.InstaCv.controller;

import com.Graduation.InstaCv.data.dto.TailoredCvDto;
import com.Graduation.InstaCv.data.dto.request.GenerateCvRequest;
import com.Graduation.InstaCv.data.model.cv.TailoredCv;
import com.Graduation.InstaCv.data.model.job.Job;
import com.Graduation.InstaCv.mappers.ContextAwareMapper;
import com.Graduation.InstaCv.service.Interfaces.ICvGenerationService;
import com.Graduation.InstaCv.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/cv")
public class CvController {
    private final ICvGenerationService cvGenerationService;
    private final ContextAwareMapper<TailoredCv, TailoredCvDto, Job> cvMapper;

    @PostMapping("/generate")
    public ResponseEntity<TailoredCvDto> generateCv(@RequestBody GenerateCvRequest request) {
        TailoredCv tailoredCv = cvGenerationService.generateCv(SecurityUtils.getCurrentUserDetails().getId(),
                request.getJobId());
        return ResponseEntity.ok(cvMapper.mapTo(tailoredCv));
    }

    @GetMapping("/{cvId}")
    public ResponseEntity<TailoredCvDto> getCvById(@PathVariable Long cvId) {
        TailoredCv tailoredCv = cvGenerationService.getCvByIdAndUserId(cvId, SecurityUtils.getCurrentUserDetails().getId());
        return ResponseEntity.ok(cvMapper.mapTo(tailoredCv));
    }

    @GetMapping("/user")
    public ResponseEntity<List<TailoredCvDto>> getCvsByUserId() {
        List<TailoredCv> cvs = cvGenerationService.getCvsByUserId(SecurityUtils.getCurrentUserDetails().getId());
        List<TailoredCvDto> cvDtos = cvs.stream()
                .map(cvMapper::mapTo)
                .collect(Collectors.toList());
        return ResponseEntity.ok(cvDtos);
    }

    @GetMapping("/job/{jobId}")
    public ResponseEntity<TailoredCvDto> getCvByUserIdAndJobId(@PathVariable Long jobId) {
        TailoredCv tailoredCv = cvGenerationService.getCvByJobIdAndUserId(SecurityUtils.getCurrentUserDetails().getId(), jobId);
        return ResponseEntity.ok(cvMapper.mapTo(tailoredCv));
    }
}