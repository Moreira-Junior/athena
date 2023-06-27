package br.edu.ifpb.pweb2.kawa.Athena.repositories;

import br.edu.ifpb.pweb2.kawa.Athena.models.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    @Query(value = "SELECT e.document FROM EnrollmentStatus e WHERE e.id = :enrollmentStatusId")
    Document findByEnrollmentStatus( @Param("enrollmentStatusId") Long enrollmentStatusId);
}
