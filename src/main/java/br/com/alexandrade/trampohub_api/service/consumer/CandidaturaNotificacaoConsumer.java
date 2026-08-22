package br.com.alexandrade.trampohub_api.service.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import br.com.alexandrade.trampohub_api.event.CandidaturaEvent;
import br.com.alexandrade.trampohub_api.service.NotificacaoService;

@Service
public class CandidaturaNotificacaoConsumer {

    private final NotificacaoService notificacaoService;

    public CandidaturaNotificacaoConsumer(NotificacaoService notificacaoService) {
        this.notificacaoService = notificacaoService;
    }

    @RabbitListener(queues = "candidatura-notificacao-queue")
    public void notificarEmpresa(CandidaturaEvent evento) {
        try {
            System.out.println("[Consumer 3] Notificando empresa...");

            String usuarioId = evento.getUsuarioId();
            // TODO: Implementar notificação para empresa

            System.out.println("Empresa notificada com sucesso!");
        } catch (Exception e) {
            System.err.println("Erro ao notificar: " + e.getMessage());
        }
    }
}