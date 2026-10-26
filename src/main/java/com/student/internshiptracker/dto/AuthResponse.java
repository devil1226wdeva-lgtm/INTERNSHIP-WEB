package com.student.internshiptracker.dto;

public class AuthResponse {

    private Long id;
    private String username;
    private String email;
    private String name;
    private String studentId;
    private String department;
    private String year;
    private String college;
    private String message;

    public AuthResponse() {
    }

    public AuthResponse(Long id, String username, String email, String name, String studentId, String department, String year, String college, String message) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.name = name;
        this.studentId = studentId;
        this.department = department;
        this.year = year;
        this.college = college;
        this.message = message;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
