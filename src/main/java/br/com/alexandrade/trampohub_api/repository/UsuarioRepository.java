package br.com.alexandrade.trampohub_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.alexandrade.trampohub_api.model.Usuario;

public interface UsuarioRepository extends MongoRepository<Usuario, String> {

    Optional<Usuario> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByCnpj(String cnpj);

    List<Usuario> findByIdNotAndUsername(String id, String username);
}
