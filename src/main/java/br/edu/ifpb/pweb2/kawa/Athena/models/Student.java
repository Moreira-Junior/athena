package br.edu.ifpb.pweb2.kawa.Athena.models;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotBlank(message = "Required field!")
    private String name;
    @NotBlank(message = "Required field!")
    private String registrationCode;
    @ManyToOne
    @JoinColumn(name = "id_institution")
    @ToString.Exclude
    private Institution institution;
    @OneToMany(mappedBy = "student", cascade = {CascadeType.ALL})
    @ToString.Exclude
    private List<EnrollmentStatus> enrollmentStatuses;
    @OneToOne
    @ToString.Exclude
    private EnrollmentStatus currentEnrollmentStatus;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "username")
    private User user;
}