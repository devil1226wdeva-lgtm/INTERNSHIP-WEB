package com.student.internshiptracker.service.impl;

import com.student.internshiptracker.dto.AuthRequest;
import com.student.internshiptracker.dto.AuthResponse;
import com.student.internshiptracker.dto.RegisterRequest;
import com.student.internshiptracker.entity.Student;
import com.student.internshiptracker.exception.DuplicateResourceException;
import com.student.internshiptracker.exception.UnauthorizedAccessException;
import com.student.internshiptracker.repository.StudentRepository;
import com.student.internshiptracker.security.CustomUserDetails;
import com.student.internshiptracker.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthServiceImpl(StudentRepository studentRepository,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager) {
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (studentRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username '" + request.getUsername() + "' is already taken");
        }

        if (studentRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email '" + request.getEmail() + "' is already registered");
        }

        Student student = new Student();
        student.setUsername(request.getUsername());
        student.setEmail(request.getEmail());
        student.setPassword(passwordEncoder.encode(request.getPassword()));
        student.setName(request.getName() != null && !request.getName().isBlank() ? request.getName() : request.getUsername());
        student.setStudentId(request.getStudentId());
        student.setDepartment(request.getDepartment());
        student.setYear(request.getYear());
        student.setCollege(request.getCollege());

        Student saved = studentRepository.save(student);

        return new AuthResponse(
                saved.getId(),
                saved.getUsername(),
                saved.getEmail(),
                saved.getName(),
                saved.getStudentId(),
                saved.getDepartment(),
                saved.getYear(),
                saved.getCollege(),
                "Registration successful"
        );
    }

    @Override
    public AuthResponse login(AuthRequest request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        HttpSession session = httpRequest.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Student student = userDetails.getStudent();

        return new AuthResponse(
                student.getId(),
                student.getUsername(),
                student.getEmail(),
                student.getName(),
                student.getStudentId(),
                student.getDepartment(),
                student.getYear(),
                student.getCollege(),
                "Login successful"
        );
    }

    @Override
    public AuthResponse getCurrentUser(Student currentStudent) {
        return new AuthResponse(
                currentStudent.getId(),
                currentStudent.getUsername(),
                currentStudent.getEmail(),
                currentStudent.getName(),
                currentStudent.getStudentId(),
                currentStudent.getDepartment(),
                currentStudent.getYear(),
                currentStudent.getCollege(),
                "Current student details retrieved"
        );
    }

    @Override
    public Student getAuthenticatedStudent() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new UnauthorizedAccessException("User is not authenticated");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails userDetails) {
            return studentRepository.findById(userDetails.getId())
                    .orElseThrow(() -> new UnauthorizedAccessException("Authenticated user not found in database"));
        }

        throw new UnauthorizedAccessException("Invalid authentication principal");
    }
}
