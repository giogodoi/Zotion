package com.zotion.backend.models;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

// Para o nosso projeto não usamos com tanta "Eficiência" os perfis, mas a ideia é deixar pronta a estrurutura
// em casos de furutras implementações de administração de usuários ou níveis de acesso diferentes.

@Entity
@Table(name = "perfis")
@Data
public class Perfil {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "perfil_id")
    private UUID id;

    @Column(unique = true, nullable = false)
    private String nome;
}