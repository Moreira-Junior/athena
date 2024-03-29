package br.edu.ifpb.pweb2.kawa.Athena.models;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class EnrollmentStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate receivingDate = LocalDate.now();
    @NotBlank(message = "Field required!")
    @Size(min = 3, message = "Size must be at least 3.")
    private String observation;
    @ManyToOne
    @JoinColumn(name = "id_semester")
    private Semester semester;
    @ManyToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "id_student")
    private Student student;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_document")
    private Document document;

    public void detachObjects(){
        student.setCurrentEnrollmentStatus(null);
        setSemester(null);
    }

    @Override
    public String toString(){
        return observation;
    }
}