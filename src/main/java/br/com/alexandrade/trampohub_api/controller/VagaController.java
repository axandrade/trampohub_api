package br.com.alexandrade.trampohub_api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.alexandrade.trampohub_api.dto.VagaRequest;
import br.com.alexandrade.trampohub_api.dto.VagaResponse;
import br.com.alexandrade.trampohub_api.model.Usuario;
import br.com.alexandrade.trampohub_api.service.VagaService;

@RestController
public class VagaController {

    private final VagaService vagaService;

    public VagaController(VagaService vagaService) {
        this.vagaService = vagaService;
    }

    @GetMapping("/api/vagas/")
    public List<VagaResponse> listar(@AuthenticationPrincipal Usuario usuario) {
        return vagaService.listar(usuario);
    }

    @GetMapping("/api/vagas/{id}/")
    public VagaResponse obter(@PathVariable String id, @AuthenticationPrincipal Usuario usuario) {
        return vagaService.obter(id, usuario);
    }

    @PostMapping("/api/vagas/")
    public ResponseEntity<VagaResponse> criar(@RequestBody VagaRequest request,
                                               @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vagaService.criar(request, usuario));
    }

    @PutMapping("/api/vagas/{id}/")
    public VagaResponse atualizar(@PathVariable String id, @RequestBody VagaRequest request,
                                   @AuthenticationPrincipal Usuario usuario) {
        return vagaService.atualizarCompleto(id, request, usuario);
    }

    @PatchMapping("/api/vagas/{id}/")
    public VagaResponse atualizarParcial(@PathVariable String id, @RequestBody VagaRequest request,
                                          @AuthenticationPrincipal Usuario usuario) {
        return vagaService.atualizarParcial(id, request, usuario);
    }

    @DeleteMapping("/api/vagas/{id}/")
    public ResponseEntity<Void> remover(@PathVariable String id, @AuthenticationPrincipal Usuario usuario) {
        vagaService.remover(id, usuario);
        return ResponseEntity.noContent().build();
    }
}
