package br.edu.ifpb.pweb2.kawa.Athena.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Institution {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotBlank(message = "Field required!")
    @Size(min = 3, message = "Size must be at least 3.")
    private String name;
    @NotBlank(message = "Field required!")
    @Size(min = 3, message = "Size must be at least 3.")
    private String shortName;
    @OneToMany(mappedBy = "institution", cascade = {CascadeType.ALL})
    @JsonBackReference
    private List<Semester> semesters;
    @OneToMany(mappedBy = "institution")
    private List<Student> students;

    @OneToOne
    private Semester currentSemester;

    @JsonManagedReference(value = "institution-semester")
    public List<Semester> getSemesters() {
        return this.semesters;
    }

    public void detachStudents(){
        this.students.forEach(student -> student.setInstitution(null));
        this.students.clear();
    }

    @Override
    public String toString() {
        return name;
    }
}
