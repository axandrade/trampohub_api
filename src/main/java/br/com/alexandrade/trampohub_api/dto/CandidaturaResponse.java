package br.com.alexandrade.trampohub_api.dto;

import java.time.Instant;

import br.com.alexandrade.trampohub_api.enums.StatusCandidatura;
import br.com.alexandrade.trampohub_api.model.Candidatura;

public record CandidaturaResponse(String id, String vaga, VagaResponse vagaDetalhes, String candidatoId,
                                   StatusCandidatura status, String mensagem, Instant dataCandidatura) {

    public static CandidaturaResponse de(Candidatura candidatura, VagaResponse vagaDetalhes) {
        return new CandidaturaResponse(candidatura.getId(), candidatura.getVagaId(), vagaDetalhes,
                candidatura.getCandidatoId(), candidatura.getStatus(), candidatura.getMensagem(),
                candidatura.getDataCandidatura());
    }
}
