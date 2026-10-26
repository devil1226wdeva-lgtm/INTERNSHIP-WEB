package com.student.internshiptracker;

import com.student.internshiptracker.entity.ApplicationStatus;
import com.student.internshiptracker.entity.Internship;
import com.student.internshiptracker.entity.Student;
import com.student.internshiptracker.repository.InternshipRepository;
import com.student.internshiptracker.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private InternshipRepository internshipRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Student student;

    @BeforeEach
    public void setup() {
        internshipRepository.deleteAll();
        studentRepository.deleteAll();

        student = new Student("john", "john@example.com", passwordEncoder.encode("password123"));
        student.setName("John Doe");
        student = studentRepository.save(student);

        // Populate sample data matching dashboard
        internshipRepository.save(new Internship("TCS", "Java Intern", "Chennai", LocalDate.of(2026, 9, 10), ApplicationStatus.APPLIED, student));
        internshipRepository.save(new Internship("Infosys", "Web Developer Intern", "Bangalore", LocalDate.of(2026, 9, 12), ApplicationStatus.INTERVIEW, student));
        internshipRepository.save(new Internship("Zoho", "Software Developer Intern", "Chennai", LocalDate.of(2026, 9, 15), ApplicationStatus.SELECTED, student));
        internshipRepository.save(new Internship("Wipro", "Java Developer Intern", "Coimbatore", LocalDate.of(2026, 9, 18), ApplicationStatus.REJECTED, student));
    }

    @Test
    @WithUserDetails(value = "john", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("Should return accurate calculated dashboard statistics")
    public void testDashboardStats() throws Exception {
        mockMvc.perform(get("/api/dashboard/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.total", is(4)))
                .andExpect(jsonPath("$.data.applications", is(1)))
                .andExpect(jsonPath("$.data.interviews", is(1)))
                .andExpect(jsonPath("$.data.selected", is(1)))
                .andExpect(jsonPath("$.data.rejected", is(1)));
    }

    @Test
    @WithUserDetails(value = "john", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("Should return top recent internships")
    public void testRecentInternships() throws Exception {
        mockMvc.perform(get("/api/dashboard/recent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(4)));
    }
}
