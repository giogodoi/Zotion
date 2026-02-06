package com.zotion.backend.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "lembretes")
@Data
public class Lembrete {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    @NotBlank
    private String nome;

    @NotBlank
    private String descricao;

    @Column(nullable = false)
    @NotNull(message = "A data e hora do lembrete não pode ser nula")
    private LocalDateTime dataHora;

    private boolean realizada = false;

    @Column(nullable = false)
    @Min(1) @Max(3) 
    private int prioridade;  

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
}