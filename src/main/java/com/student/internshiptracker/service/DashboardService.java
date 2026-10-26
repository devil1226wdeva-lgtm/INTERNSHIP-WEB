package com.student.internshiptracker.service;

import com.student.internshiptracker.dto.DashboardStatsResponse;
import com.student.internshiptracker.dto.InternshipResponse;
import com.student.internshiptracker.entity.Student;

import java.util.List;

public interface DashboardService {

    DashboardStatsResponse getDashboardStats(Student student);

    List<InternshipResponse> getRecentInternships(Student student);
}
