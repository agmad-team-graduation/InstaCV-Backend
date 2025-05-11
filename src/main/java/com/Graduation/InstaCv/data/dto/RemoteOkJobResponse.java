package com.Graduation.InstaCv.data.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RemoteOkJobResponse {
    private String id;
    @JsonProperty("position")
    private String title;
    private String company;
    private List<String> tags;
    @JsonProperty("apply_url")
    private String applyUrl;
    private String description;
    private String htmlDescription;
    private OffsetDateTime date;
}