package br.com.alexandrade.trampohub_api.dto;

import java.math.BigDecimal;
import java.time.Instant;

import br.com.alexandrade.trampohub_api.model.Modalidade;
import br.com.alexandrade.trampohub_api.model.TipoContrato;
import br.com.alexandrade.trampohub_api.model.Vaga;

public record VagaResponse(String id, String titulo, String descricao, String empresa, String localizacao,
                            BigDecimal salario, TipoContrato tipoContrato, Modalidade modalidade,
                            String empregadorId, Instant dataInicio, Instant dataFim, String status,
                            Instant criadoEm) {

    public static VagaResponse de(Vaga vaga) {
        String status = vaga.isExpirada() ? "Expirada" : "Aberta";
        return new VagaResponse(vaga.getId(), vaga.getTitulo(), vaga.getDescricao(), vaga.getEmpresa(),
                vaga.getLocalizacao(), vaga.getSalario(), vaga.getTipoContrato(), vaga.getModalidade(),
                vaga.getEmpregadorId(), vaga.getDataInicio(), vaga.getDataFim(), status, vaga.getCriadoEm());
    }
}
