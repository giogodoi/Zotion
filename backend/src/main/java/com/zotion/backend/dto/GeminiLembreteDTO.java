package com.zotion.backend.dto;

import java.time.LocalDateTime;

// a ideia é criar um "Bonus" permitindo o usuario agilizar a criação de lembretes enviando arquivos
// vou começar com esse dto que mapeia para o Jackson a resposta que virá da LLM para um formato esperado pelo Java.

public class GeminiLembreteDTO {
    private String nome;
    private String descricao;
    private LocalDateTime dataHora;
    private int prioridade;

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getDescricao() {
        return descricao;
    }
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    public LocalDateTime getDataHora() {
        return dataHora;
    }
    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }
    public int getPrioridade() {
        return prioridade;
    }
    public void setPrioridade(int prioridade) {
        this.prioridade = prioridade;
    }
}
