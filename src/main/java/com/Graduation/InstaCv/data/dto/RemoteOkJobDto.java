package com.Graduation.InstaCv.data.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RemoteOkJobDto {
    private String id;

    @JsonProperty("position")
    private String title;

    private String company;

    private List<String> tags;

    @JsonProperty("salary_min")
    private Integer salaryMin;

    @JsonProperty("salary_max")
    private Integer salaryMax;

    @JsonProperty("apply_url")
    private String applyUrl;

    // Change from Long to String to match the actual response format
    @JsonProperty("date")
    private String date;
}