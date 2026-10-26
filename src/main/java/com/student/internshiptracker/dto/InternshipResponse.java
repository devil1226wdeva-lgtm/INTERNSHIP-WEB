package com.student.internshiptracker.dto;

import com.student.internshiptracker.entity.Internship;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class InternshipResponse {

    private Long id;
    private String company;
    private String role;
    private String location;
    private LocalDate appliedDate;
    private String status;
    private String description;
    private String internshipType;
    private LocalDate startDate;
    private LocalDate endDate;
    private String websiteUrl;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public InternshipResponse() {
    }

    public static InternshipResponse fromEntity(Internship internship) {
        InternshipResponse res = new InternshipResponse();
        res.setId(internship.getId());
        res.setCompany(internship.getCompany());
        res.setRole(internship.getRole());
        res.setLocation(internship.getLocation());
        res.setAppliedDate(internship.getAppliedDate());
        res.setStatus(internship.getStatus().getDisplayName());
        res.setDescription(internship.getDescription());
        res.setInternshipType(internship.getInternshipType());
        res.setStartDate(internship.getStartDate());
        res.setEndDate(internship.getEndDate());
        res.setWebsiteUrl(internship.getWebsiteUrl());
        res.setNotes(internship.getNotes());
        res.setCreatedAt(internship.getCreatedAt());
        res.setUpdatedAt(internship.getUpdatedAt());
        return res;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDate getAppliedDate() {
        return appliedDate;
    }

    public void setAppliedDate(LocalDate appliedDate) {
        this.appliedDate = appliedDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInternshipType() {
        return internshipType;
    }

    public void setInternshipType(String internshipType) {
        this.internshipType = internshipType;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
