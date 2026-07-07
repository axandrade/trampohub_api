package br.com.alexandrade.trampohub_api.model;

import br.com.alexandrade.trampohub_api.enums.TipoUsuario;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "usuarios")
public class Usuario {

    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    private String password;

    private String email;

    private TipoUsuario tipo;

    private String nomeEmpresa;

    private String foto;
}
