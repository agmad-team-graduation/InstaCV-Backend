package com.Graduation.InstaCv.controller;

import com.Graduation.InstaCv.data.dto.TailoredCvDto;
import com.Graduation.InstaCv.data.dto.request.GenerateCvRequest;
import com.Graduation.InstaCv.data.model.TailoredCv;
import com.Graduation.InstaCv.mappers.Mapper;
import com.Graduation.InstaCv.service.Interfaces.ICvGenerationService;
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
    private final Mapper<TailoredCv, TailoredCvDto> cvMapper;

    @PostMapping("/generate")
    public ResponseEntity<TailoredCvDto> generateCv(@RequestBody GenerateCvRequest request) {
        TailoredCv tailoredCv = cvGenerationService.generateCv(request.getUserId(), request.getJobId());
        return ResponseEntity.ok(cvMapper.mapTo(tailoredCv));
    }

    @GetMapping("/{cvId}")
    public ResponseEntity<TailoredCvDto> getCvById(@PathVariable Long cvId) {
        TailoredCv tailoredCv = cvGenerationService.getCvById(cvId);
        return ResponseEntity.ok(cvMapper.mapTo(tailoredCv));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TailoredCvDto>> getCvsByUserId(@PathVariable Long userId) {
        List<TailoredCv> cvs = cvGenerationService.getCvsByUserId(userId);
        List<TailoredCvDto> cvDtos = cvs.stream()
                .map(cvMapper::mapTo)
                .collect(Collectors.toList());
        return ResponseEntity.ok(cvDtos);
    }

    @GetMapping("/user/{userId}/job/{jobId}")
    public ResponseEntity<TailoredCvDto> getCvByUserIdAndJobId(
            @PathVariable Long userId,
            @PathVariable Long jobId) {
        TailoredCv tailoredCv = cvGenerationService.getCvByUserIdAndJobId(userId, jobId);
        return ResponseEntity.ok(cvMapper.mapTo(tailoredCv));
    }
} 