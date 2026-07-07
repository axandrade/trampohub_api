package br.com.alexandrade.trampohub_api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum StatusCandidatura {
    PENDENTE("Pendente"),
    EM_ANALISE("Em analise"),
    APROVADO("Aprovado"),
    REJEITADO("Rejeitado");

    private final String valor;

    StatusCandidatura(String valor) {
        this.valor = valor;
    }

    @JsonValue
    public String getValor() {
        return valor;
    }

    @JsonCreator
    public static StatusCandidatura fromValor(String valor) {
        for (StatusCandidatura status : values()) {
            if (status.valor.equalsIgnoreCase(valor)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Status de candidatura inválido: " + valor);
    }
}
