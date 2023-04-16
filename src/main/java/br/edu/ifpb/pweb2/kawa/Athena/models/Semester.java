package br.edu.ifpb.pweb2.kawa.Athena.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
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
    @NotNull(message = "Required field")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate startAt;
    @NotNull(message = "Required field")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Future(message = "Date should be in the future!")
    private LocalDate endsAt;
    @NotNull(message = "Required field")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_institution")
    private Institution institution;

    @JsonManagedReference(value = "institution-semester")
    public Institution getInstitution(){
        return this.institution;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String startsAtFormatted = startAt.format(formatter);
        String endsAtFormatted = endsAt.format(formatter);
        return "Starts at " + startsAtFormatted + " ends at " + endsAtFormatted;
    }
}
