package br.com.alexandrade.trampohub_api.service;

import java.security.SecureRandom;
import java.time.Instant;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import br.com.alexandrade.trampohub_api.dto.LoginResponse;
import br.com.alexandrade.trampohub_api.dto.PerfilResponse;
import br.com.alexandrade.trampohub_api.exception.FieldValidationException;
import br.com.alexandrade.trampohub_api.model.Token;
import br.com.alexandrade.trampohub_api.model.TipoUsuario;
import br.com.alexandrade.trampohub_api.model.Usuario;
import br.com.alexandrade.trampohub_api.repository.TokenRepository;
import br.com.alexandrade.trampohub_api.repository.UsuarioRepository;

@Service
public class AuthService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final UsuarioRepository usuarioRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;

    public AuthService(UsuarioRepository usuarioRepository, TokenRepository tokenRepository,
                        PasswordEncoder passwordEncoder, FileStorageService fileStorageService) {
        this.usuarioRepository = usuarioRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.fileStorageService = fileStorageService;
    }

    public LoginResponse login(String username, String password) {
        Usuario usuario = usuarioRepository.findByUsername(username).orElse(null);
        if (usuario == null || !passwordEncoder.matches(password, usuario.getPassword())) {
            throw new FieldValidationException("non_field_errors",
                    "Não é possível fazer login com as credenciais fornecidas.");
        }

        tokenRepository.deleteByUsuarioId(usuario.getId());

        Token token = new Token();
        token.setKey(gerarChave());
        token.setUsuarioId(usuario.getId());
        token.setCriadoEm(Instant.now());
        tokenRepository.save(token);

        return new LoginResponse(token.getKey());
    }

    public void cadastrar(String username, String password, String tipoStr, String nomeEmpresa,
                           MultipartFile foto, String email) {
        if (!StringUtils.hasText(username)) {
            throw new FieldValidationException("username", "Este campo é obrigatório.");
        }
        if (usuarioRepository.existsByUsername(username)) {
            throw new FieldValidationException("username", "Esse nome de usuário já existe.");
        }
        if (!StringUtils.hasText(password) || password.length() < 6) {
            throw new FieldValidationException("password", "A senha deve ter ao menos 6 caracteres.");
        }

        TipoUsuario tipo;
        try {
            tipo = TipoUsuario.fromValor(tipoStr);
        } catch (IllegalArgumentException e) {
            throw new FieldValidationException("tipo", "Tipo inválido. Use 'empregador' ou 'candidato'.");
        }

        if (tipo == TipoUsuario.CANDIDATO) {
            if (foto == null || foto.isEmpty()) {
                throw new FieldValidationException("foto", "A foto é obrigatória para candidatos.");
            }
            if (!StringUtils.hasText(email)) {
                throw new FieldValidationException("email", "O e-mail é obrigatório para candidatos.");
            }
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setEmail(email);
        usuario.setTipo(tipo);
        usuario.setNomeEmpresa(nomeEmpresa);
        if (foto != null && !foto.isEmpty()) {
            usuario.setFoto(fileStorageService.salvarFotoPerfil(foto));
        }

        usuarioRepository.save(usuario);
    }

    public PerfilResponse obterPerfil(Usuario usuario) {
        return PerfilResponse.de(usuario);
    }

    public PerfilResponse editarPerfil(Usuario usuario, String username, String email, String nomeEmpresa,
                                        MultipartFile foto, String senhaAtual, String novaSenha) {
        if (StringUtils.hasText(username)) {
            boolean usadoPorOutro = usuarioRepository.findByUsername(username)
                    .filter(outro -> !outro.getId().equals(usuario.getId()))
                    .isPresent();
            if (usadoPorOutro) {
                throw new FieldValidationException("username", "Esse nome de usuário já existe.");
            }
            usuario.setUsername(username);
        }

        if (email != null) {
            if (usuario.getTipo() == TipoUsuario.CANDIDATO && !StringUtils.hasText(email)) {
                throw new FieldValidationException("email", "O e-mail é obrigatório para candidatos.");
            }
            usuario.setEmail(email);
        }

        if (StringUtils.hasText(novaSenha)) {
            if (novaSenha.length() < 6) {
                throw new FieldValidationException("nova_senha", "A senha deve ter ao menos 6 caracteres.");
            }
            if (!StringUtils.hasText(senhaAtual)) {
                throw new FieldValidationException("senha_atual", "Informe sua senha atual para trocar a senha.");
            }
            if (!passwordEncoder.matches(senhaAtual, usuario.getPassword())) {
                throw new FieldValidationException("senha_atual", "Senha atual incorreta.");
            }
            usuario.setPassword(passwordEncoder.encode(novaSenha));
        }

        if (nomeEmpresa != null) {
            usuario.setNomeEmpresa(nomeEmpresa);
        }
        if (foto != null && !foto.isEmpty()) {
            usuario.setFoto(fileStorageService.salvarFotoPerfil(foto));
        }

        usuarioRepository.save(usuario);
        return PerfilResponse.de(usuario);
    }

    private String gerarChave() {
        byte[] bytes = new byte[20];
        RANDOM.nextBytes(bytes);
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
