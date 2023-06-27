package br.edu.ifpb.pweb2.kawa.Athena.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do documento não pode ser vazio")
    private String name;

    @URL
    private String url;

    @Lob
    private byte[] file;

    public Document(String name, byte[] bytes) {
        this.name = name;
        this.file = bytes;
    }
}
