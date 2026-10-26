package com.student.internshiptracker.dto;

import com.student.internshiptracker.entity.Student;

public class ProfileResponse {

    private Long id;
    private String username;
    private String name;
    private String studentId;
    private String department;
    private String year;
    private String email;
    private String college;
    private String skills;

    public ProfileResponse() {
    }

    public static ProfileResponse fromEntity(Student student) {
        ProfileResponse res = new ProfileResponse();
        res.setId(student.getId());
        res.setUsername(student.getUsername());
        res.setName(student.getName());
        res.setStudentId(student.getStudentId());
        res.setDepartment(student.getDepartment());
        res.setYear(student.getYear());
        res.setEmail(student.getEmail());
        res.setCollege(student.getCollege());
        res.setSkills(student.getSkills());
        return res;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }
}
