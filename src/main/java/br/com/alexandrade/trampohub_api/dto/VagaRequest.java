package br.com.alexandrade.trampohub_api.dto;

import java.math.BigDecimal;
import java.time.Instant;

import br.com.alexandrade.trampohub_api.model.Modalidade;
import br.com.alexandrade.trampohub_api.model.TipoContrato;

public record VagaRequest(String titulo, String descricao, String empresa, String localizacao,
                           BigDecimal salario, TipoContrato tipoContrato, Modalidade modalidade,
                           Instant dataInicio, Instant dataFim) {
}
