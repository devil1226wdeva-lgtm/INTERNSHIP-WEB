package com.student.internshiptracker.controller;

import com.student.internshiptracker.dto.ApiResponse;
import com.student.internshiptracker.dto.DashboardStatsResponse;
import com.student.internshiptracker.dto.InternshipResponse;
import com.student.internshiptracker.entity.Student;
import com.student.internshiptracker.service.AuthService;
import com.student.internshiptracker.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;
    private final AuthService authService;

    public DashboardController(DashboardService dashboardService, AuthService authService) {
        this.dashboardService = dashboardService;
        this.authService = authService;
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getStats() {
        Student current = authService.getAuthenticatedStudent();
        DashboardStatsResponse stats = dashboardService.getDashboardStats(current);
        return ResponseEntity.ok(ApiResponse.success("Dashboard stats retrieved", stats));
    }

    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<InternshipResponse>>> getRecent() {
        Student current = authService.getAuthenticatedStudent();
        List<InternshipResponse> recent = dashboardService.getRecentInternships(current);
        return ResponseEntity.ok(ApiResponse.success("Recent internships retrieved", recent));
    }
}
