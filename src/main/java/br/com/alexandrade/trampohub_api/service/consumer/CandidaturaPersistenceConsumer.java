package br.com.alexandrade.trampohub_api.service.consumer;

import br.com.alexandrade.trampohub_api.service.CandidaturaService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import br.com.alexandrade.trampohub_api.dto.CandidaturaRequest;
import br.com.alexandrade.trampohub_api.event.CandidaturaEvent;
import br.com.alexandrade.trampohub_api.model.Usuario;

@Service
public class CandidaturaPersistenceConsumer {

    private final CandidaturaService candidaturaService;

    public CandidaturaPersistenceConsumer(CandidaturaService candidaturaService) {
        this.candidaturaService = candidaturaService;
    }

    @RabbitListener(queues = "candidatura-criada-queue")
    public void salvarCandidatura(CandidaturaEvent evento) {
        try {
            System.out.println("[Consumer 1] Salvando candidatura...");
            Thread.sleep(5000);

            CandidaturaRequest request = evento.getRequest();

            // CRIA um novo Usuario com os dados do evento
            Usuario usuario = new Usuario();
            usuario.setId(evento.getUsuarioId());
            usuario.setEmail(evento.getUsuarioEmail());
            usuario.setUsername(evento.getUsuarioNome());

            // Reutiliza o método criar do CandidaturaService
            candidaturaService.criar(request, usuario);

            System.out.println("Candidatura salva com sucesso!");
        } catch (Exception e) {
            System.err.println("Erro ao salvar: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}