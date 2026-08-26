package br.com.alexandrade.trampohub_api.service.consumer;

import br.com.alexandrade.trampohub_api.service.CandidaturaService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import br.com.alexandrade.trampohub_api.dto.CandidaturaRequest;
import br.com.alexandrade.trampohub_api.event.CandidaturaEvent;
import br.com.alexandrade.trampohub_api.model.Usuario;

@Service
public class CandidaturaKafkaConsumer {

    private final CandidaturaService candidaturaService;

    public CandidaturaKafkaConsumer(CandidaturaService candidaturaService) {
        this.candidaturaService = candidaturaService;
    }

    @KafkaListener(topics = "candidatura-criada", groupId = "candidatura-processors")
    public void salvarCandidatura(CandidaturaEvent evento) {
        try {
            System.out.println("[Kafka Consumer - Persistência] Salvando candidatura...");
            Thread.sleep(2000);

            // Se é candidatura simples (do RabbitMQ antigo)
            if (evento.getRequest() != null) {
                CandidaturaRequest request = evento.getRequest();

                Usuario usuario = new Usuario();
                usuario.setId(evento.getUsuarioId());
                usuario.setEmail(evento.getUsuarioEmail());
                usuario.setUsername(evento.getUsuarioNome());

                candidaturaService.criar(request, usuario);
                System.out.println("Candidatura simples salva com sucesso!");
            }
            // Se é candidatura com arquivo (do Kafka novo)
            else if (evento.getVagaId() != null) {
                System.out.println("Candidatura com arquivo recebida!");
                System.out.println("Vaga ID: " + evento.getVagaId());
                System.out.println("Arquivo: " + evento.getNomeArquivo());
                System.out.println("Tamanho: " + evento.getTamanhoArquivo() + " bytes");
                System.out.println("Candidatura com arquivo processada!");
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Interrompido: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erro ao salvar: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}