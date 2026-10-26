package com.student.internshiptracker.controller;

import com.student.internshiptracker.dto.AccountSettingsRequest;
import com.student.internshiptracker.dto.ApiResponse;
import com.student.internshiptracker.dto.ChangePasswordRequest;
import com.student.internshiptracker.dto.PreferencesRequest;
import com.student.internshiptracker.entity.Student;
import com.student.internshiptracker.service.AuthService;
import com.student.internshiptracker.service.SettingsService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/settings")
public class SettingsController {

    private final SettingsService settingsService;
    private final AuthService authService;

    public SettingsController(SettingsService settingsService, AuthService authService) {
        this.settingsService = settingsService;
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSettings() {
        Student current = authService.getAuthenticatedStudent();
        Map<String, Object> settings = settingsService.getSettings(current);
        return ResponseEntity.ok(ApiResponse.success("Settings retrieved", settings));
    }

    @PutMapping("/account")
    public ResponseEntity<ApiResponse<Void>> updateAccount(@Valid @RequestBody AccountSettingsRequest request) {
        Student current = authService.getAuthenticatedStudent();
        settingsService.updateAccount(request, current);
        return ResponseEntity.ok(ApiResponse.success("Account settings updated successfully"));
    }

    @PutMapping("/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        Student current = authService.getAuthenticatedStudent();
        settingsService.changePassword(request, current);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully"));
    }

    @PutMapping("/preferences")
    public ResponseEntity<ApiResponse<Void>> updatePreferences(@RequestBody PreferencesRequest request) {
        Student current = authService.getAuthenticatedStudent();
        settingsService.updatePreferences(request, current);
        return ResponseEntity.ok(ApiResponse.success("Preferences updated successfully"));
    }
}
