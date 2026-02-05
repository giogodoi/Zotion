package com.zotion.backend.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity
@Table(name = "notas")
@Data
public class Nota {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String conteudo;

    private String cor; // será passado o HEX da cor pelo front-end :D

    private boolean realizada = false;

    private LocalDateTime dataCriacao = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonIgnore // se eu não colocar essa opção entramos em um loop infinito.. a nota chama o usuário, o usuário chama a nota :/ 
    private Usuario usuario;
}