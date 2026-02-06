package com.zotion.backend.models;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

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