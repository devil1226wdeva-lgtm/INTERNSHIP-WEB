package com.student.internshiptracker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.student.internshiptracker.dto.InternshipRequest;
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
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class InternshipControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private InternshipRepository internshipRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private Student student1;
    private Student student2;

    @BeforeEach
    public void setup() {
        internshipRepository.deleteAll();
        studentRepository.deleteAll();

        student1 = new Student("alice", "alice@example.com", passwordEncoder.encode("password123"));
        student1.setName("Alice");
        student1 = studentRepository.save(student1);

        student2 = new Student("bob", "bob@example.com", passwordEncoder.encode("password123"));
        student2.setName("Bob");
        student2 = studentRepository.save(student2);
    }

    @Test
    @WithUserDetails(value = "alice", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("Should create an internship for logged in student")
    public void testCreateInternship() throws Exception {
        InternshipRequest req = new InternshipRequest("Google", "SWE Intern", "Bangalore", LocalDate.of(2026, 9, 15), "Applied");
        req.setDescription("Software Engineering Internship");

        mockMvc.perform(post("/api/internships")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.company", is("Google")))
                .andExpect(jsonPath("$.data.role", is("SWE Intern")))
                .andExpect(jsonPath("$.data.status", is("Applied")));
    }

    @Test
    @WithUserDetails(value = "alice", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("Should retrieve only the internships belonging to the logged in student")
    public void testGetOnlyOwnInternships() throws Exception {
        Internship i1 = new Internship("TCS", "Java Intern", "Chennai", LocalDate.now(), ApplicationStatus.APPLIED, student1);
        internshipRepository.save(i1);

        Internship i2 = new Internship("Zoho", "Software Intern", "Chennai", LocalDate.now(), ApplicationStatus.INTERVIEW, student2);
        internshipRepository.save(i2);

        mockMvc.perform(get("/api/internships"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].company", is("TCS")));
    }

    @Test
    @WithUserDetails(value = "bob", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("Should reject access when a student tries to view another student's internship")
    public void testPreventAccessToOtherStudentInternship() throws Exception {
        Internship aliceInternship = new Internship("Microsoft", "Dev Intern", "Hyderabad", LocalDate.now(), ApplicationStatus.APPLIED, student1);
        aliceInternship = internshipRepository.save(aliceInternship);

        // Bob tries to access Alice's internship
        mockMvc.perform(get("/api/internships/" + aliceInternship.getId()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success", is(false)));
    }

    @Test
    @WithUserDetails(value = "alice", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("Should update an existing internship for owner")
    public void testUpdateInternship() throws Exception {
        Internship internship = new Internship("Infosys", "Web Intern", "Bangalore", LocalDate.now(), ApplicationStatus.APPLIED, student1);
        internship = internshipRepository.save(internship);

        InternshipRequest updateReq = new InternshipRequest("Infosys", "Senior Web Intern", "Bangalore", LocalDate.now(), "Interview");
        updateReq.setDescription("Interview scheduled for next Monday");

        mockMvc.perform(put("/api/internships/" + internship.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.role", is("Senior Web Intern")))
                .andExpect(jsonPath("$.data.status", is("Interview")));
    }

    @Test
    @WithUserDetails(value = "alice", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("Should update internship status via PATCH")
    public void testUpdateStatus() throws Exception {
        Internship internship = new Internship("Wipro", "Java Intern", "Coimbatore", LocalDate.now(), ApplicationStatus.APPLIED, student1);
        internship = internshipRepository.save(internship);

        mockMvc.perform(patch("/api/internships/" + internship.getId() + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("status", "Selected"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.status", is("Selected")));
    }

    @Test
    @WithUserDetails(value = "alice", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("Should delete own internship")
    public void testDeleteInternship() throws Exception {
        Internship internship = new Internship("TCS", "Tester Intern", "Chennai", LocalDate.now(), ApplicationStatus.APPLIED, student1);
        internship = internshipRepository.save(internship);

        mockMvc.perform(delete("/api/internships/" + internship.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));

        assertFalse(internshipRepository.findById(internship.getId()).isPresent());
    }

    @Test
    @WithUserDetails(value = "bob", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("Should prevent deleting another student's internship")
    public void testPreventDeletingOtherStudentInternship() throws Exception {
        Internship aliceInternship = new Internship("Amazon", "SDE Intern", "Bangalore", LocalDate.now(), ApplicationStatus.APPLIED, student1);
        aliceInternship = internshipRepository.save(aliceInternship);

        mockMvc.perform(delete("/api/internships/" + aliceInternship.getId()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success", is(false)));
    }
}
