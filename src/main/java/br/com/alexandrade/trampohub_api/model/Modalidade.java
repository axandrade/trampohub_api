package br.com.alexandrade.trampohub_api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Modalidade {
    PRESENCIAL("Presencial"),
    REMOTO("Remoto"),
    HIBRIDO("Híbrido");

    private final String valor;

    Modalidade(String valor) {
        this.valor = valor;
    }

    @JsonValue
    public String getValor() {
        return valor;
    }

    @JsonCreator
    public static Modalidade fromValor(String valor) {
        for (Modalidade modalidade : values()) {
            if (modalidade.valor.equalsIgnoreCase(valor)) {
                return modalidade;
            }
        }
        throw new IllegalArgumentException("Modalidade inválida: " + valor);
    }
}
