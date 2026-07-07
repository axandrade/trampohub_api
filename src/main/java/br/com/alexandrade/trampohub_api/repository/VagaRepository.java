package br.com.alexandrade.trampohub_api.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.alexandrade.trampohub_api.model.Vaga;

public interface VagaRepository extends MongoRepository<Vaga, String> {

    List<Vaga> findByEmpregadorId(String empregadorId);

    List<Vaga> findByEmpregadorIdIn(List<String> empregadorIds);
}
