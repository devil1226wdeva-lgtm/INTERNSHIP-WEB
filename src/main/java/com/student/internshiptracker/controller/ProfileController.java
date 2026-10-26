package com.student.internshiptracker.controller;

import com.student.internshiptracker.dto.ApiResponse;
import com.student.internshiptracker.dto.ProfileRequest;
import com.student.internshiptracker.dto.ProfileResponse;
import com.student.internshiptracker.entity.Student;
import com.student.internshiptracker.service.AuthService;
import com.student.internshiptracker.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;
    private final AuthService authService;

    public ProfileController(ProfileService profileService, AuthService authService) {
        this.profileService = profileService;
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<ProfileResponse>> getProfile() {
        Student current = authService.getAuthenticatedStudent();
        ProfileResponse profile = profileService.getProfile(current);
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", profile));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<ProfileResponse>> updateProfile(@Valid @RequestBody ProfileRequest request) {
        Student current = authService.getAuthenticatedStudent();
        ProfileResponse updated = profileService.updateProfile(request, current);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", updated));
    }
}
