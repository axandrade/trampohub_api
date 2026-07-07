package br.com.alexandrade.trampohub_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.alexandrade.trampohub_api.model.Candidatura;

public interface CandidaturaRepository extends MongoRepository<Candidatura, String> {

    List<Candidatura> findByCandidatoId(String candidatoId);

    List<Candidatura> findByVagaIdIn(List<String> vagaIds);

    Optional<Candidatura> findByVagaIdAndCandidatoId(String vagaId, String candidatoId);
}
