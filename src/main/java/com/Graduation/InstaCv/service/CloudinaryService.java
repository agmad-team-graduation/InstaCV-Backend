package com.Graduation.InstaCv.service;

import com.Graduation.InstaCv.utils.SecurityUtils;
import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

@Service
@Slf4j
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    public Map<String, Object> uploadPhoto(MultipartFile file) {
        try {
            // Validate file
            validateFile(file);

            // Configure upload options with correct transformation syntax
            Map<String, Object> options = ObjectUtils.asMap(
                    "folder", "user-photos",
                    "public_id", "user_" + SecurityUtils.getCurrentUserDetails().getId() + "_" + System.currentTimeMillis(),
                    "overwrite", true,
                    "resource_type", "image",
                    // Correct transformation syntax
                    "transformation", new Transformation()
                            .width(500)
                            .height(500)
                            .crop("limit")
                            .quality("auto")
            );

            // Upload to Cloudinary
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), options);

            log.info("Photo uploaded successfully to Cloudinary: {}", uploadResult.get("public_id"));
            return uploadResult;

        } catch (Exception e) {
            log.error("Error uploading photo to Cloudinary", e);
            throw new RuntimeException("Failed to upload photo: " + e.getMessage());
        }
    }

    public void deletePhoto(String publicId) {
        try {
            if (publicId != null && !publicId.isEmpty()) {
                Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());

                log.info("Photo deleted from Cloudinary: {}", result);
            }
        } catch (Exception e) {
            log.error("Error deleting photo from Cloudinary", e);
            throw new RuntimeException("Failed to delete photo: " + e.getMessage());
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        // Check file size (5MB limit)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("File size exceeds 5MB limit");
        }

        // Check file type
        String contentType = file.getContentType();
        if (!isImageFile(contentType)) {
            throw new IllegalArgumentException("Only image files are allowed");
        }
    }

    private boolean isImageFile(String contentType) {
        return contentType != null && (
                contentType.equals("image/jpeg") ||
                        contentType.equals("image/png") ||
                        contentType.equals("image/gif") ||
                        contentType.equals("image/webp")
        );
    }
}

