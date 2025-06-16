package com.Graduation.InstaCv.controller;

import com.Graduation.InstaCv.data.dto.response.ApiResponse;
import com.Graduation.InstaCv.data.dto.response.PhotoUploadResponse;
import com.Graduation.InstaCv.data.dto.response.UserPhotoResponse;
import com.Graduation.InstaCv.data.model.UserPhoto;
import com.Graduation.InstaCv.service.UserService;
import com.Graduation.InstaCv.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.Map;

@RestController
@RequestMapping("/api/users/photo")
@RequiredArgsConstructor
public class UserPhotoController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<PhotoUploadResponse> uploadPhoto(@RequestParam("photo") MultipartFile file) {
        UserPhoto userPhoto = userService.uploadUserPhoto(file);

        PhotoUploadResponse response = PhotoUploadResponse.builder()
                .success(true)
                .message("Photo uploaded successfully")
                .photoId(userPhoto.getId())
                .photoUrl(userPhoto.getPhotoUrl())
                .uploadedAt(userPhoto.getUploadedAt())
                .size(userPhoto.getPhotoSize())
                .width(userPhoto.getWidth())
                .height(userPhoto.getHeight())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<UserPhotoResponse> getUserPhoto() {
        Long userId = SecurityUtils.getCurrentUserDetails().getId();
        UserPhoto userPhoto = userService.getUserPhoto();

        UserPhotoResponse response = UserPhotoResponse.builder()
                .photoId(userPhoto.getId())
                .userId(userId)
                .photoUrl(userPhoto.getPhotoUrl())
                .format(userPhoto.getPhotoFormat())
                .size(userPhoto.getPhotoSize())
                .width(userPhoto.getWidth())
                .height(userPhoto.getHeight())
                .uploadedAt(userPhoto.getUploadedAt())
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse> deletePhoto() {
        userService.deleteUserPhoto();
        return ResponseEntity.ok(ApiResponse.success("Photo deleted successfully"));
    }

    @GetMapping("/exists")
    public ResponseEntity<Map<String, Boolean>> checkPhotoExists() {
        boolean exists = userService.hasPhoto();
        return ResponseEntity.ok(Map.of("exists", exists));
    }
}
