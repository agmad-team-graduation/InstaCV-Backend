package com.Graduation.InstaCv.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScrapedJobSimpleDto {
    private Long id;
    private String title;
    private String company;
    private String description;
    private String applyUrl;
    private String date;
}
