package com.student.internshiptracker.service.impl;

import com.student.internshiptracker.dto.AccountSettingsRequest;
import com.student.internshiptracker.dto.ChangePasswordRequest;
import com.student.internshiptracker.dto.PreferencesRequest;
import com.student.internshiptracker.entity.Student;
import com.student.internshiptracker.exception.BadRequestException;
import com.student.internshiptracker.exception.DuplicateResourceException;
import com.student.internshiptracker.repository.StudentRepository;
import com.student.internshiptracker.service.SettingsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class SettingsServiceImpl implements SettingsService {

    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    public SettingsServiceImpl(StudentRepository studentRepository, PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getSettings(Student student) {
        Map<String, Object> settings = new HashMap<>();
        settings.put("username", student.getUsername());
        settings.put("email", student.getEmail());
        settings.put("theme", student.getTheme() != null ? student.getTheme() : "light");
        settings.put("emailUpdates", student.getEmailUpdates());
        settings.put("applicationReminders", student.getApplicationReminders());
        return settings;
    }

    @Override
    @Transactional
    public void updateAccount(AccountSettingsRequest request, Student student) {
        if (!student.getUsername().equalsIgnoreCase(request.getUsername())) {
            if (studentRepository.existsByUsername(request.getUsername())) {
                throw new DuplicateResourceException("Username '" + request.getUsername() + "' is already taken");
            }
            student.setUsername(request.getUsername());
        }

        if (!student.getEmail().equalsIgnoreCase(request.getEmail())) {
            if (studentRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateResourceException("Email '" + request.getEmail() + "' is already in use");
            }
            student.setEmail(request.getEmail());
        }

        studentRepository.save(student);
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request, Student student) {
        if (!passwordEncoder.matches(request.getCurrentPassword(), student.getPassword())) {
            throw new BadRequestException("Current password does not match");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("New password and confirmation password do not match");
        }

        if (passwordEncoder.matches(request.getNewPassword(), student.getPassword())) {
            throw new BadRequestException("New password cannot be the same as your old password");
        }

        student.setPassword(passwordEncoder.encode(request.getNewPassword()));
        studentRepository.save(student);
    }

    @Override
    @Transactional
    public void updatePreferences(PreferencesRequest request, Student student) {
        if (request.getTheme() != null && !request.getTheme().isBlank()) {
            student.setTheme(request.getTheme().toLowerCase());
        }
        if (request.getEmailUpdates() != null) {
            student.setEmailUpdates(request.getEmailUpdates());
        }
        if (request.getApplicationReminders() != null) {
            student.setApplicationReminders(request.getApplicationReminders());
        }

        studentRepository.save(student);
    }
}
