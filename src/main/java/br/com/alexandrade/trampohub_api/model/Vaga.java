package br.com.alexandrade.trampohub_api.model;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "vagas")
public class Vaga {

    @Id
    private String id;

    private String titulo;
    private String descricao;
    private String empresa;
    private String localizacao;
    private BigDecimal salario;
    private TipoContrato tipoContrato;
    private Modalidade modalidade;
    private String empregadorId;
    private Instant dataInicio;
    private Instant dataFim;
    private Instant criadoEm = Instant.now();

    public boolean isExpirada() {
        return dataFim != null && dataFim.isBefore(Instant.now());
    }
}
