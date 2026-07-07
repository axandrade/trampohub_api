package br.com.alexandrade.trampohub_api.dto;

import br.com.alexandrade.trampohub_api.model.StatusCandidatura;

public record CandidaturaRequest(String vaga, String mensagem, StatusCandidatura status) {
}
