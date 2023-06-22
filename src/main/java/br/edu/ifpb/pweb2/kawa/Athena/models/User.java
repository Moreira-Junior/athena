package br.edu.ifpb.pweb2.kawa.Athena.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @OneToMany(mappedBy = "username", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Authority> authorities;
    @Id
    private String username;
    private String password;
    private Boolean enabled = true;
}

