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

import br.com.alexandrade.trampohub_api.dto.CandidaturaRequest;
import br.com.alexandrade.trampohub_api.dto.CandidaturaResponse;
import br.com.alexandrade.trampohub_api.model.Usuario;
import br.com.alexandrade.trampohub_api.service.CandidaturaService;

@RestController
public class CandidaturaController {

    private final CandidaturaService candidaturaService;

    public CandidaturaController(CandidaturaService candidaturaService) {
        this.candidaturaService = candidaturaService;
    }

    @GetMapping("/api/candidaturas/")
    public List<CandidaturaResponse> listar(@AuthenticationPrincipal Usuario usuario) {
        return candidaturaService.listar(usuario);
    }

    @GetMapping("/api/candidaturas/{id}/")
    public CandidaturaResponse obter(@PathVariable String id, @AuthenticationPrincipal Usuario usuario) {
        return candidaturaService.obter(id, usuario);
    }

    @PostMapping("/api/candidaturas/")
    public ResponseEntity<CandidaturaResponse> criar(@RequestBody CandidaturaRequest request,
                                                       @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.status(HttpStatus.CREATED).body(candidaturaService.criar(request, usuario));
    }

    @PutMapping("/api/candidaturas/{id}/")
    public CandidaturaResponse atualizar(@PathVariable String id, @RequestBody CandidaturaRequest request,
                                          @AuthenticationPrincipal Usuario usuario) {
        return candidaturaService.atualizar(id, request, usuario);
    }

    @PatchMapping("/api/candidaturas/{id}/")
    public CandidaturaResponse atualizarParcial(@PathVariable String id, @RequestBody CandidaturaRequest request,
                                                 @AuthenticationPrincipal Usuario usuario) {
        return candidaturaService.atualizar(id, request, usuario);
    }

    @DeleteMapping("/api/candidaturas/{id}/")
    public ResponseEntity<Void> remover(@PathVariable String id, @AuthenticationPrincipal Usuario usuario) {
        candidaturaService.remover(id, usuario);
        return ResponseEntity.noContent().build();
    }
}
