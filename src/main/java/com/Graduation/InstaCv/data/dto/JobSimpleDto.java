package com.Graduation.InstaCv.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobSimpleDto {
    private Long id;
    private Long profileId;
    private String title;
    private String company;
    private String description;
    private OffsetDateTime addDate;
}
