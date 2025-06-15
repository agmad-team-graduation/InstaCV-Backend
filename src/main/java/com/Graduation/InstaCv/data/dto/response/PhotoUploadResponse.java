package com.Graduation.InstaCv.data.dto.response;
import lombok.*;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhotoUploadResponse {
    private boolean success;
    private String message;
    private Long photoId;
    private String photoUrl;
    private Date uploadedAt;
    private Long size;
    private Integer width;
    private Integer height;
}
