package br.edu.ifpb.pweb2.kawa.Athena.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
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
    private String name;
    @NotBlank(message = "Field required!")
    private String shortName;
    @OneToMany(mappedBy = "institution")
    @JsonBackReference
    private List<Semester> semesters;
    @OneToMany(mappedBy = "institution")
    private List<Student> students;
    @OneToOne
    private Semester currentSemester;
    @JsonManagedReference(value = "institution-semester")
    public List<Semester> getSemesters(){
        return this.semesters;
    }
}
