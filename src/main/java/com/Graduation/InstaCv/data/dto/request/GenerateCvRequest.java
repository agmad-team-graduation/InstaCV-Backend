package com.Graduation.InstaCv.data.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerateCvRequest {
    private Long userId;
    private Long jobId;
} 