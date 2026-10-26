package com.student.internshiptracker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.student.internshiptracker.dto.AccountSettingsRequest;
import com.student.internshiptracker.dto.ChangePasswordRequest;
import com.student.internshiptracker.dto.PreferencesRequest;
import com.student.internshiptracker.dto.ProfileRequest;
import com.student.internshiptracker.entity.Student;
import com.student.internshiptracker.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProfileAndSettingsTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private Student student;

    @BeforeEach
    public void setup() {
        studentRepository.deleteAll();

        student = new Student("devan", "devan@example.com", passwordEncoder.encode("oldPassword123"));
        student.setName("Devan");
        student.setStudentId("122601");
        student.setDepartment("Computer Science");
        student.setYear("3rd Year");
        student.setCollege("College of Engineering");
        student.setSkills("Java, Spring Boot, MySQL");
        student = studentRepository.save(student);
    }

    @Test
    @WithUserDetails(value = "devan", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("Should retrieve and update student profile")
    public void testProfileOperations() throws Exception {
        // Retrieve profile
        mockMvc.perform(get("/api/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.name", is("Devan")))
                .andExpect(jsonPath("$.data.studentId", is("122601")));

        // Update profile
        ProfileRequest updateReq = new ProfileRequest();
        updateReq.setName("Devan S");
        updateReq.setStudentId("122601");
        updateReq.setDepartment("Information Technology");
        updateReq.setYear("4th Year");
        updateReq.setEmail("devan.updated@example.com");
        updateReq.setCollege("Engineering College");
        updateReq.setSkills("Java, Spring Boot, React, MySQL");

        mockMvc.perform(put("/api/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.name", is("Devan S")))
                .andExpect(jsonPath("$.data.department", is("Information Technology")));
    }

    @Test
    @WithUserDetails(value = "devan", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("Should update account settings, password, and preferences")
    public void testSettingsOperations() throws Exception {
        // 1. Update preferences
        PreferencesRequest prefReq = new PreferencesRequest("dark", true, true);
        mockMvc.perform(put("/api/settings/preferences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(prefReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));

        // 2. Change password successfully
        ChangePasswordRequest pwReq = new ChangePasswordRequest("oldPassword123", "newPassword456", "newPassword456");
        mockMvc.perform(put("/api/settings/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pwReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));

        Student updated = studentRepository.findById(student.getId()).orElseThrow();
        assertTrue(passwordEncoder.matches("newPassword456", updated.getPassword()));

        // 3. Reject password change with incorrect current password
        ChangePasswordRequest badPwReq = new ChangePasswordRequest("wrongCurrent", "anotherPassword789", "anotherPassword789");
        mockMvc.perform(put("/api/settings/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badPwReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)));
    }
}
