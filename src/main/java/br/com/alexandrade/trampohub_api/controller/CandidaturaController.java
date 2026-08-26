package br.com.alexandrade.trampohub_api.controller;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

import br.com.alexandrade.trampohub_api.event.CandidaturaEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import br.com.alexandrade.trampohub_api.dto.CandidaturaRequest;
import br.com.alexandrade.trampohub_api.dto.CandidaturaResponse;
import br.com.alexandrade.trampohub_api.model.Usuario;
import br.com.alexandrade.trampohub_api.service.CandidaturaService;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class CandidaturaController {

    private final CandidaturaService candidaturaService;
    private final RabbitTemplate rabbitTemplate;

    private final KafkaTemplate<String, CandidaturaEvent> kafkaTemplate;  // ← Declare

    public CandidaturaController(CandidaturaService candidaturaService,
                                 RabbitTemplate rabbitTemplate,
                                 KafkaTemplate<String, CandidaturaEvent> kafkaTemplate) {
        this.candidaturaService = candidaturaService;
        this.rabbitTemplate = rabbitTemplate;
        this.kafkaTemplate = kafkaTemplate;
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
    public ResponseEntity<?> criar(@RequestBody CandidaturaRequest request,
                                   @AuthenticationPrincipal Usuario usuario) {

        CandidaturaEvent evento = new CandidaturaEvent(
                request,
                usuario.getId(),
                usuario.getEmail(),
                usuario.getUsername()
        );

        rabbitTemplate.convertAndSend(
                "candidatura-exchange",
                "candidatura.criada",
                evento
        );

        return ResponseEntity.accepted()
                .body(new Object() {
                    public String mensagem = "Candidatura recebida! Processaremos em breve.";
                });
    }

    @PostMapping("/api/candidaturas/com-curriculo")
    public ResponseEntity<?> candidatarComCurriculo(
            @RequestParam("vagaId") String vagaId,
            @RequestParam("file") MultipartFile curriculo,
            @AuthenticationPrincipal Usuario usuario) {

        try {
            if (curriculo.getSize() > 10 * 1024 * 1024) {  // 10MB
                return ResponseEntity.badRequest().body("Arquivo muito grande!");
            }

            byte[] fileBytes = curriculo.getInputStream().readAllBytes();
            String base64 = Base64.getEncoder().encodeToString(fileBytes);

            CandidaturaEvent evento = new CandidaturaEvent(
                    vagaId,
                    usuario.getId(),
                    curriculo.getOriginalFilename(),
                    base64,
                    curriculo.getSize()
            );

            kafkaTemplate.send("candidatura-criada", evento);

            return ResponseEntity.accepted()
                    .body("Candidatura recebida! Processando currículo...");

        } catch (IOException e) {
            System.err.println("Erro ao processar arquivo: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao processar arquivo!");
        }
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
