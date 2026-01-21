package com.zotion.backend.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "usuarios")
@Data
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank 
    private String nome;

    @NotBlank 
    private String sobrenome;

    @Column(unique = true)
    @Email 
    @NotBlank 
    private String email;

    @NotBlank
    @Size(min = 6)
    private String senha;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER) 
    @JoinTable(
            name = "usuarios_perfis",
            joinColumns = @JoinColumn(name = "usuario_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "perfil_id", referencedColumnName = "perfil_id")
    )
    private Set<Perfil> perfis;

    public boolean isSenhaCorreta(String senhaPura, PasswordEncoder codificadorSenha) {
        return codificadorSenha.matches(senhaPura, this.senha);
    }
}