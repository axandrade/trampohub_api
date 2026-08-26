package br.com.alexandrade.trampohub_api.event;

import java.io.Serializable;

import br.com.alexandrade.trampohub_api.dto.CandidaturaRequest;

public class CandidaturaEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private CandidaturaRequest request;
    private String usuarioId;
    private String usuarioEmail;
    private String usuarioNome;
    private String vagaId;
    private String nomeArquivo;
    private String arquivoBase64;
    private long tamanhoArquivo;

    public CandidaturaEvent() {
    }

    public CandidaturaEvent(CandidaturaRequest request, String usuarioId,
                            String usuarioEmail, String usuarioNome) {
        this.request = request;
        this.usuarioId = usuarioId;
        this.usuarioEmail = usuarioEmail;
        this.usuarioNome = usuarioNome;
    }

    public CandidaturaEvent(String vagaId, String usuarioId, String nomeArquivo,
                            String arquivoBase64, long tamanhoArquivo) {
        this.vagaId = vagaId;
        this.usuarioId = usuarioId;
        this.nomeArquivo = nomeArquivo;
        this.arquivoBase64 = arquivoBase64;
        this.tamanhoArquivo = tamanhoArquivo;
    }

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

    public String getVagaId() {
        return vagaId;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public String getArquivoBase64() {
        return arquivoBase64;
    }

    public long getTamanhoArquivo() {
        return tamanhoArquivo;
    }
}