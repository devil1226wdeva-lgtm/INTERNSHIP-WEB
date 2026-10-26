package com.student.internshiptracker.service;

import com.student.internshiptracker.dto.InternshipRequest;
import com.student.internshiptracker.dto.InternshipResponse;
import com.student.internshiptracker.entity.Student;

import java.util.List;

public interface InternshipService {

    InternshipResponse createInternship(InternshipRequest request, Student student);

    List<InternshipResponse> getAllInternships(Student student, String search, String status, String location, String internshipType);

    InternshipResponse getInternshipById(Long id, Student student);

    InternshipResponse updateInternship(Long id, InternshipRequest request, Student student);

    InternshipResponse updateStatus(Long id, String status, Student student);

    void deleteInternship(Long id, Student student);
}
