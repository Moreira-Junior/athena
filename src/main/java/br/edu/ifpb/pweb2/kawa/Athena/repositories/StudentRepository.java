package br.edu.ifpb.pweb2.kawa.Athena.repositories;

import br.edu.ifpb.pweb2.kawa.Athena.models.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Page<Student> findByEnrollmentStatusesIsNull(Pageable pageable);

    Page<Student> findByUserUsername(String username, Pageable paging);

    List<Student> findByUserUsername(String username);
}
