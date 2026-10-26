package com.student.internshiptracker.repository;

import com.student.internshiptracker.entity.ApplicationStatus;
import com.student.internshiptracker.entity.Internship;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InternshipRepository extends JpaRepository<Internship, Long> {

    List<Internship> findByStudentIdOrderByAppliedDateDesc(Long studentId);

    List<Internship> findByStudentIdOrderByCreatedAtDesc(Long studentId);

    Optional<Internship> findByIdAndStudentId(Long id, Long studentId);

    long countByStudentId(Long studentId);

    long countByStudentIdAndStatus(Long studentId, ApplicationStatus status);

    List<Internship> findTop5ByStudentIdOrderByAppliedDateDescCreatedAtDesc(Long studentId);

    @Query("SELECT i FROM Internship i WHERE i.student.id = :studentId " +
           "AND (:search IS NULL OR LOWER(i.company) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(i.role) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:status IS NULL OR i.status = :status) " +
           "AND (:location IS NULL OR LOWER(i.location) LIKE LOWER(CONCAT('%', :location, '%'))) " +
           "AND (:internshipType IS NULL OR LOWER(i.internshipType) LIKE LOWER(CONCAT('%', :internshipType, '%')))")
    List<Internship> searchAndFilter(
            @Param("studentId") Long studentId,
            @Param("search") String search,
            @Param("status") ApplicationStatus status,
            @Param("location") String location,
            @Param("internshipType") String internshipType
    );
}
