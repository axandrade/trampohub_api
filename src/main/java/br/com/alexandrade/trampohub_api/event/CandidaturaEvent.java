package br.com.alexandrade.trampohub_api.event;

import java.io.Serializable;

import br.com.alexandrade.trampohub_api.dto.CandidaturaRequest;

public class CandidaturaEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private CandidaturaRequest request;
    private String usuarioId;      // ← MUDE PARA ID
    private String usuarioEmail;   // ← MUDE PARA EMAIL
    private String usuarioNome;    // ← MUDE PARA NOME

    public CandidaturaEvent() {
    }

    public CandidaturaEvent(CandidaturaRequest request, String usuarioId, String usuarioEmail, String usuarioNome) {
        this.request = request;
        this.usuarioId = usuarioId;
        this.usuarioEmail = usuarioEmail;
        this.usuarioNome = usuarioNome;
    }

    // Getters
    public CandidaturaRequest getRequest() {
        return request;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public String getUsuarioEmail() {
        return usuarioEmail;
    }

    public String getUsuarioNome() {
        return usuarioNome;
    }
}