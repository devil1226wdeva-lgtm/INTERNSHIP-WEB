package com.student.internshiptracker.controller;

import com.student.internshiptracker.dto.ApiResponse;
import com.student.internshiptracker.dto.InternshipRequest;
import com.student.internshiptracker.dto.InternshipResponse;
import com.student.internshiptracker.entity.Student;
import com.student.internshiptracker.service.AuthService;
import com.student.internshiptracker.service.InternshipService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/internships")
public class InternshipController {

    private final InternshipService internshipService;
    private final AuthService authService;

    public InternshipController(InternshipService internshipService, AuthService authService) {
        this.internshipService = internshipService;
        this.authService = authService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<InternshipResponse>> createInternship(@Valid @RequestBody InternshipRequest request) {
        Student current = authService.getAuthenticatedStudent();
        InternshipResponse response = internshipService.createInternship(request, current);
        return new ResponseEntity<>(ApiResponse.success("Internship added successfully", response), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<InternshipResponse>>> getAllInternships(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String internshipType) {
        Student current = authService.getAuthenticatedStudent();
        List<InternshipResponse> list = internshipService.getAllInternships(current, search, status, location, internshipType);
        return ResponseEntity.ok(ApiResponse.success("Internships retrieved successfully", list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InternshipResponse>> getInternshipById(@PathVariable Long id) {
        Student current = authService.getAuthenticatedStudent();
        InternshipResponse response = internshipService.getInternshipById(id, current);
        return ResponseEntity.ok(ApiResponse.success("Internship details retrieved", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<InternshipResponse>> updateInternship(
            @PathVariable Long id,
            @Valid @RequestBody InternshipRequest request) {
        Student current = authService.getAuthenticatedStudent();
        InternshipResponse response = internshipService.updateInternship(id, request, current);
        return ResponseEntity.ok(ApiResponse.success("Internship updated successfully", response));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<InternshipResponse>> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> statusBody) {
        Student current = authService.getAuthenticatedStudent();
        String status = statusBody.get("status");
        InternshipResponse response = internshipService.updateStatus(id, status, current);
        return ResponseEntity.ok(ApiResponse.success("Status updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteInternship(@PathVariable Long id) {
        Student current = authService.getAuthenticatedStudent();
        internshipService.deleteInternship(id, current);
        return ResponseEntity.ok(ApiResponse.success("Internship deleted successfully"));
    }
}
