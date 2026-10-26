package com.student.internshiptracker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.student.internshiptracker.dto.AuthRequest;
import com.student.internshiptracker.dto.RegisterRequest;
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
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        studentRepository.deleteAll();
    }

    @Test
    @DisplayName("Should successfully register a new student")
    public void testSuccessfulRegistration() throws Exception {
        RegisterRequest req = new RegisterRequest("student1", "student1@example.com", "password123", "Student One");
        req.setStudentId("ST1001");
        req.setDepartment("Computer Science");
        req.setYear("3rd Year");
        req.setCollege("ABC College");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.username", is("student1")))
                .andExpect(jsonPath("$.data.email", is("student1@example.com")));
    }

    @Test
    @DisplayName("Should reject duplicate username or email during registration")
    public void testDuplicateRegistration() throws Exception {
        Student existing = new Student("student1", "student1@example.com", passwordEncoder.encode("password123"));
        studentRepository.save(existing);

        // Attempt duplicate username
        RegisterRequest dupUsername = new RegisterRequest("student1", "other@example.com", "password123", "Other Student");
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dupUsername)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("already taken")));

        // Attempt duplicate email
        RegisterRequest dupEmail = new RegisterRequest("student2", "student1@example.com", "password123", "Other Student");
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dupEmail)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("already registered")));
    }

    @Test
    @DisplayName("Should validate required registration fields")
    public void testRegistrationValidationFailure() throws Exception {
        RegisterRequest invalidReq = new RegisterRequest("", "invalid-email", "123", "");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.validationErrors", notNullValue()));
    }

    @Test
    @DisplayName("Should authenticate valid student and reject invalid credentials")
    public void testLoginValidation() throws Exception {
        Student student = new Student("testuser", "testuser@example.com", passwordEncoder.encode("secretPass123"));
        studentRepository.save(student);

        // Valid login
        AuthRequest validReq = new AuthRequest("testuser", "secretPass123");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.username", is("testuser")));

        // Valid login using email
        AuthRequest emailLoginReq = new AuthRequest("testuser@example.com", "secretPass123");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailLoginReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.username", is("testuser")));

        // Invalid password
        AuthRequest badPassReq = new AuthRequest("testuser", "wrongPassword");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badPassReq)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success", is(false)));
    }

    @Test
    @DisplayName("Should protect /api/auth/me against unauthenticated access")
    public void testMeEndpointUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
    }
}
