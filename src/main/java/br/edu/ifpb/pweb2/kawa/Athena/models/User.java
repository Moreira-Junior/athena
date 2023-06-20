package br.edu.ifpb.pweb2.kawa.Athena.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @OneToMany(mappedBy = "username")
    private List<Authority> authorities;

    @Id
    private String username;
    private String password;
    private Boolean enabled;
}

