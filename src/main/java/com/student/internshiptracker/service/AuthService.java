package com.student.internshiptracker.service;

import com.student.internshiptracker.dto.AuthRequest;
import com.student.internshiptracker.dto.AuthResponse;
import com.student.internshiptracker.dto.RegisterRequest;
import com.student.internshiptracker.entity.Student;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(AuthRequest request, HttpServletRequest httpRequest, HttpServletResponse httpResponse);

    AuthResponse getCurrentUser(Student currentStudent);

    Student getAuthenticatedStudent();
}
