package com.student.internshiptracker.service.impl;

import com.student.internshiptracker.dto.InternshipRequest;
import com.student.internshiptracker.dto.InternshipResponse;
import com.student.internshiptracker.entity.ApplicationStatus;
import com.student.internshiptracker.entity.Internship;
import com.student.internshiptracker.entity.Student;
import com.student.internshiptracker.exception.BadRequestException;
import com.student.internshiptracker.exception.ResourceNotFoundException;
import com.student.internshiptracker.exception.UnauthorizedAccessException;
import com.student.internshiptracker.repository.InternshipRepository;
import com.student.internshiptracker.service.InternshipService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InternshipServiceImpl implements InternshipService {

    private final InternshipRepository internshipRepository;

    public InternshipServiceImpl(InternshipRepository internshipRepository) {
        this.internshipRepository = internshipRepository;
    }

    @Override
    @Transactional
    public InternshipResponse createInternship(InternshipRequest request, Student student) {
        Internship internship = new Internship();
        internship.setCompany(request.getCompany());
        internship.setRole(request.getRole());
        internship.setLocation(request.getLocation());
        internship.setAppliedDate(request.getAppliedDate());
        internship.setStatus(ApplicationStatus.fromString(request.getStatus()));
        internship.setDescription(request.getDescription());
        internship.setInternshipType(request.getInternshipType());
        internship.setStartDate(request.getStartDate());
        internship.setEndDate(request.getEndDate());
        internship.setWebsiteUrl(request.getWebsiteUrl());
        internship.setNotes(request.getNotes());
        internship.setStudent(student);

        Internship saved = internshipRepository.save(internship);
        return InternshipResponse.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InternshipResponse> getAllInternships(Student student, String search, String status, String location, String internshipType) {
        ApplicationStatus appStatus = null;
        if (status != null && !status.isBlank() && !"ALL".equalsIgnoreCase(status)) {
            appStatus = ApplicationStatus.fromString(status);
        }

        String searchParam = (search != null && !search.isBlank()) ? search.trim() : null;
        String locationParam = (location != null && !location.isBlank()) ? location.trim() : null;
        String typeParam = (internshipType != null && !internshipType.isBlank()) ? internshipType.trim() : null;

        List<Internship> list;
        if (searchParam == null && appStatus == null && locationParam == null && typeParam == null) {
            list = internshipRepository.findByStudentIdOrderByAppliedDateDesc(student.getId());
        } else {
            list = internshipRepository.searchAndFilter(student.getId(), searchParam, appStatus, locationParam, typeParam);
        }

        return list.stream()
                .map(InternshipResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public InternshipResponse getInternshipById(Long id, Student student) {
        Internship internship = internshipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Internship not found with ID: " + id));

        if (!internship.getStudent().getId().equals(student.getId())) {
            throw new UnauthorizedAccessException("You are not authorized to view this internship");
        }

        return InternshipResponse.fromEntity(internship);
    }

    @Override
    @Transactional
    public InternshipResponse updateInternship(Long id, InternshipRequest request, Student student) {
        Internship internship = internshipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Internship not found with ID: " + id));

        if (!internship.getStudent().getId().equals(student.getId())) {
            throw new UnauthorizedAccessException("You are not authorized to edit this internship");
        }

        internship.setCompany(request.getCompany());
        internship.setRole(request.getRole());
        internship.setLocation(request.getLocation());
        internship.setAppliedDate(request.getAppliedDate());
        internship.setStatus(ApplicationStatus.fromString(request.getStatus()));
        internship.setDescription(request.getDescription());
        internship.setInternshipType(request.getInternshipType());
        internship.setStartDate(request.getStartDate());
        internship.setEndDate(request.getEndDate());
        internship.setWebsiteUrl(request.getWebsiteUrl());
        internship.setNotes(request.getNotes());

        Internship updated = internshipRepository.save(internship);
        return InternshipResponse.fromEntity(updated);
    }

    @Override
    @Transactional
    public InternshipResponse updateStatus(Long id, String status, Student student) {
        if (status == null || status.isBlank()) {
            throw new BadRequestException("Status cannot be empty");
        }

        Internship internship = internshipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Internship not found with ID: " + id));

        if (!internship.getStudent().getId().equals(student.getId())) {
            throw new UnauthorizedAccessException("You are not authorized to update this internship");
        }

        internship.setStatus(ApplicationStatus.fromString(status));
        Internship updated = internshipRepository.save(internship);
        return InternshipResponse.fromEntity(updated);
    }

    @Override
    @Transactional
    public void deleteInternship(Long id, Student student) {
        Internship internship = internshipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Internship not found with ID: " + id));

        if (!internship.getStudent().getId().equals(student.getId())) {
            throw new UnauthorizedAccessException("You are not authorized to delete this internship");
        }

        internshipRepository.delete(internship);
    }
}
