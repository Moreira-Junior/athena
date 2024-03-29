package br.edu.ifpb.pweb2.kawa.Athena.models;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Semester {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Size(min = 4, message = "Size must be at least 4.")
    @NotBlank(message = "Required field")
    private String description;
    @NotNull(message = "Required field")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startsAt;
    @NotNull(message = "Required field")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endsAt;
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
    @JoinColumn(name = "id_institution")
    private Institution institution;

    @JsonManagedReference(value = "institution-semester")
    public Institution getInstitution(){
        return this.institution;
    }
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String startsAtFormatted = startsAt.format(formatter);
        String endsAtFormatted = endsAt.format(formatter);
        return "Starts at " + startsAtFormatted + " ends at " + endsAtFormatted;
    }
}