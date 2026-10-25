package br.senai.chamados;

import java.time.LocalDateTime;

public class Chamado {
    private final Integer id;
    private final String solicitante;
    private final String descricao;
    private final Prioridade prioridade;
    private final Status status;
    private final LocalDateTime dataAbertura;

    public Chamado(Integer id, String solicitante, String descricao,
                   Prioridade prioridade, Status status, LocalDateTime dataAbertura) {
        this.id = id;
        this.solicitante = solicitante;
        this.descricao = descricao;
        this.prioridade = prioridade;
        this.status = status;
        this.dataAbertura = dataAbertura;
    }

    public Integer getId() { return id; }
    public String getSolicitante() { return solicitante; }
    public String getDescricao() { return descricao; }
    public Prioridade getPrioridade() { return prioridade; }
    public Status getStatus() { return status; }
    public LocalDateTime getDataAbertura() { return dataAbertura; }
}
