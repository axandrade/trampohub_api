package br.com.alexandrade.trampohub_api.model;

import java.time.Instant;

import br.com.alexandrade.trampohub_api.enums.StatusCandidatura;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "candidaturas")
public class Candidatura {

    @Id
    private String id;

    private String vagaId;
    private String candidatoId;
    private StatusCandidatura status = StatusCandidatura.PENDENTE;
    private String mensagem;
    private Instant dataCandidatura = Instant.now();
}
