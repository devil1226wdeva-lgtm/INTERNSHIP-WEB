package com.student.internshiptracker.service.impl;

import com.student.internshiptracker.dto.DashboardStatsResponse;
import com.student.internshiptracker.dto.InternshipResponse;
import com.student.internshiptracker.entity.ApplicationStatus;
import com.student.internshiptracker.entity.Internship;
import com.student.internshiptracker.entity.Student;
import com.student.internshiptracker.repository.InternshipRepository;
import com.student.internshiptracker.service.DashboardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final InternshipRepository internshipRepository;

    public DashboardServiceImpl(InternshipRepository internshipRepository) {
        this.internshipRepository = internshipRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardStatsResponse getDashboardStats(Student student) {
        Long studentId = student.getId();

        long total = internshipRepository.countByStudentId(studentId);
        long applied = internshipRepository.countByStudentIdAndStatus(studentId, ApplicationStatus.APPLIED);
        long interview = internshipRepository.countByStudentIdAndStatus(studentId, ApplicationStatus.INTERVIEW);
        long selected = internshipRepository.countByStudentIdAndStatus(studentId, ApplicationStatus.SELECTED);
        long rejected = internshipRepository.countByStudentIdAndStatus(studentId, ApplicationStatus.REJECTED);
        long completed = internshipRepository.countByStudentIdAndStatus(studentId, ApplicationStatus.COMPLETED);
        long interested = internshipRepository.countByStudentIdAndStatus(studentId, ApplicationStatus.INTERESTED);
        long assessment = internshipRepository.countByStudentIdAndStatus(studentId, ApplicationStatus.ASSESSMENT);

        DashboardStatsResponse stats = new DashboardStatsResponse(total, applied, interview, selected, rejected, completed);
        stats.setInterested(interested);
        stats.setAssessment(assessment);
        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public List<InternshipResponse> getRecentInternships(Student student) {
        List<Internship> recent = internshipRepository.findTop5ByStudentIdOrderByAppliedDateDescCreatedAtDesc(student.getId());
        return recent.stream()
                .map(InternshipResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
