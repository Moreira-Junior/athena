package br.edu.ifpb.pweb2.kawa.Athena.repositories;

import br.edu.ifpb.pweb2.kawa.Athena.models.EnrollmentStatus;
import br.edu.ifpb.pweb2.kawa.Athena.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EnrollmentStatusRepository extends JpaRepository<EnrollmentStatus, Long> {

    List<EnrollmentStatus> findBySemesterId(Long id);
}
