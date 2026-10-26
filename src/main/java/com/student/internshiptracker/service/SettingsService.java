package com.student.internshiptracker.service;

import com.student.internshiptracker.dto.AccountSettingsRequest;
import com.student.internshiptracker.dto.ChangePasswordRequest;
import com.student.internshiptracker.dto.PreferencesRequest;
import com.student.internshiptracker.entity.Student;

import java.util.Map;

public interface SettingsService {

    Map<String, Object> getSettings(Student student);

    void updateAccount(AccountSettingsRequest request, Student student);

    void changePassword(ChangePasswordRequest request, Student student);

    void updatePreferences(PreferencesRequest request, Student student);
}
