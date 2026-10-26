package com.student.internshiptracker.service.impl;

import com.student.internshiptracker.dto.ProfileRequest;
import com.student.internshiptracker.dto.ProfileResponse;
import com.student.internshiptracker.entity.Student;
import com.student.internshiptracker.exception.DuplicateResourceException;
import com.student.internshiptracker.repository.StudentRepository;
import com.student.internshiptracker.service.ProfileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final StudentRepository studentRepository;

    public ProfileServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileResponse getProfile(Student student) {
        return ProfileResponse.fromEntity(student);
    }

    @Override
    @Transactional
    public ProfileResponse updateProfile(ProfileRequest request, Student student) {
        // If changing email, check if taken by another student
        if (!student.getEmail().equalsIgnoreCase(request.getEmail())) {
            if (studentRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateResourceException("Email '" + request.getEmail() + "' is already in use by another account");
            }
            student.setEmail(request.getEmail());
        }

        student.setName(request.getName());
        student.setStudentId(request.getStudentId());
        student.setDepartment(request.getDepartment());
        student.setYear(request.getYear());
        student.setCollege(request.getCollege());
        student.setSkills(request.getSkills());

        Student updated = studentRepository.save(student);
        return ProfileResponse.fromEntity(updated);
    }
}
