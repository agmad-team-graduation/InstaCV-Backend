package com.Graduation.InstaCv.data.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobllmResponseDTO {

    @JsonProperty("job_title")
    private String jobTitle;

    @JsonProperty("company_name")
    private String companyName;

    private String summary;

    @JsonProperty("rewritten_description")
    private String rewrittenDescription;
}
