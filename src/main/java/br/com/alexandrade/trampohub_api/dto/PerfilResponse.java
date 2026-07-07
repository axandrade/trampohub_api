package br.com.alexandrade.trampohub_api.dto;

import br.com.alexandrade.trampohub_api.enums.TipoUsuario;
import br.com.alexandrade.trampohub_api.model.Usuario;

public record PerfilResponse(TipoUsuario tipo, String nomeEmpresa, String foto, String username, String email) {

    public static PerfilResponse de(Usuario usuario) {
        return new PerfilResponse(usuario.getTipo(), usuario.getNomeEmpresa(), usuario.getFoto(),
                usuario.getUsername(), usuario.getEmail());
    }
}
