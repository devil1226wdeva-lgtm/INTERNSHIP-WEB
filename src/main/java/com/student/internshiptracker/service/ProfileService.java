package com.student.internshiptracker.service;

import com.student.internshiptracker.dto.ProfileRequest;
import com.student.internshiptracker.dto.ProfileResponse;
import com.student.internshiptracker.entity.Student;

public interface ProfileService {

    ProfileResponse getProfile(Student student);

    ProfileResponse updateProfile(ProfileRequest request, Student student);
}
