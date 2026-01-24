package com.zotion.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

// a ideia é criar um "Bonus" permitindo o usuario agilizar a criação de lembretes enviando arquivos
// vou começar com esse dto que mapeia para o Jackson a resposta que virá da LLM para um formato esperado pelo Java.

public class GeminiLembreteDTO {
    private String nome;
    private String descricao;
    private LocalDateTime dataHora;
    private Long prioridade;
}
