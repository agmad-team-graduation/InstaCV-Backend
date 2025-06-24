package com.Graduation.InstaCv.data.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPhotoResponse {
    private Long photoId;
    private Long userId;
    private String photoUrl;
    private String format;
    private Long size;
    private Integer width;
    private Integer height;
    private Date uploadedAt;
}
