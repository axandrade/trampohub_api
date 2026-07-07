package br.com.alexandrade.trampohub_api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.com.alexandrade.trampohub_api.dto.LoginRequest;
import br.com.alexandrade.trampohub_api.dto.LoginResponse;
import br.com.alexandrade.trampohub_api.dto.MensagemResponse;
import br.com.alexandrade.trampohub_api.dto.PerfilResponse;
import br.com.alexandrade.trampohub_api.model.Usuario;
import br.com.alexandrade.trampohub_api.service.AuthService;

@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/api/token/")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authService.login(request.username(), request.password());
    }

    @PostMapping("/api/cadastro/")
    public ResponseEntity<MensagemResponse> cadastro(@RequestParam String username,
                                                       @RequestParam String password,
                                                       @RequestParam String tipo,
                                                       @RequestParam(required = false) String nomeEmpresa,
                                                       @RequestParam(required = false) MultipartFile foto,
                                                       @RequestParam(required = false) String email) {
        authService.cadastrar(username, password, tipo, nomeEmpresa, foto, email);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MensagemResponse("Usuário criado com sucesso."));
    }

    @GetMapping("/api/perfil/me/")
    public PerfilResponse meuPerfil(@AuthenticationPrincipal Usuario usuario) {
        return authService.obterPerfil(usuario);
    }

    @PatchMapping("/api/perfil/me/")
    public PerfilResponse editarPerfil(@AuthenticationPrincipal Usuario usuario,
                                        @RequestParam(required = false) String username,
                                        @RequestParam(required = false) String email,
                                        @RequestParam(required = false) String nomeEmpresa,
                                        @RequestParam(required = false) MultipartFile foto,
                                        @RequestParam(required = false) String senhaAtual,
                                        @RequestParam(required = false) String novaSenha) {
        return authService.editarPerfil(usuario, username, email, nomeEmpresa, foto, senhaAtual, novaSenha);
    }
}
