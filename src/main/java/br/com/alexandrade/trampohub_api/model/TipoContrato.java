package br.com.alexandrade.trampohub_api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TipoContrato {
    CLT("CLT"),
    PJ("PJ"),
    FREELANCE("Freelance"),
    ESTAGIO("Estágio");

    private final String valor;

    TipoContrato(String valor) {
        this.valor = valor;
    }

    @JsonValue
    public String getValor() {
        return valor;
    }

    @JsonCreator
    public static TipoContrato fromValor(String valor) {
        for (TipoContrato tipo : values()) {
            if (tipo.valor.equalsIgnoreCase(valor)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo de contrato inválido: " + valor);
    }
}
