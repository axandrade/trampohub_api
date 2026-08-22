package br.com.alexandrade.trampohub_api.service.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import br.com.alexandrade.trampohub_api.event.CandidaturaEvent;

@Service
public class CandidaturaEmailConsumer {

    @RabbitListener(queues = "candidatura-email-queue")
    public void enviarEmail(CandidaturaEvent evento) {
        try {
            System.out.println("[Consumer 2] Enviando email de confirmação...");

            String emailUsuario = evento.getUsuarioEmail();
            String nomeUsuario = evento.getUsuarioNome();

            // TODO: Implementar lógica de envio de email
            System.out.println("   Email enviado para: "+ nomeUsuario + " <" + emailUsuario + ">");
            System.out.println("Email enviado com sucesso!");
        } catch (Exception e) {
            System.err.println("Erro ao enviar email: " + e.getMessage());
        }
    }
}