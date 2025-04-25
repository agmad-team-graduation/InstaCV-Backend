package com.Graduation.InstaCv.data.dto.response.linkedin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkedinEducationResponse {
    private String schoolName;
    private String degreeName;
    private LocalDate startDate;
    private LocalDate endDate;
}
