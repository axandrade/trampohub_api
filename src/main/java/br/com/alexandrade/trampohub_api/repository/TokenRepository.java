package br.com.alexandrade.trampohub_api.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.alexandrade.trampohub_api.model.Token;

public interface TokenRepository extends MongoRepository<Token, String> {

    Optional<Token> findByKey(String key);

    void deleteByUsuarioId(String usuarioId);
}
