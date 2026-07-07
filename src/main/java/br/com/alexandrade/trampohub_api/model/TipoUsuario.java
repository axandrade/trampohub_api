package br.com.alexandrade.trampohub_api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TipoUsuario {
    EMPREGADOR("empregador"),
    CANDIDATO("candidato");

    private final String valor;

    TipoUsuario(String valor) {
        this.valor = valor;
    }

    @JsonValue
    public String getValor() {
        return valor;
    }

    @JsonCreator
    public static TipoUsuario fromValor(String valor) {
        for (TipoUsuario tipo : values()) {
            if (tipo.valor.equalsIgnoreCase(valor)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo de usuário inválido: " + valor);
    }
}
