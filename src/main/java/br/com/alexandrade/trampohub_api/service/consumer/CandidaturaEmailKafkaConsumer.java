package br.com.alexandrade.trampohub_api.service.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import br.com.alexandrade.trampohub_api.event.CandidaturaEvent;

@Service
public class CandidaturaEmailKafkaConsumer {

    @KafkaListener(topics = "candidatura-criada", groupId = "email-processors")
    public void enviarEmail(CandidaturaEvent evento) {
        try {
            System.out.println("[Kafka Consumer - Email] Enviando email de confirmação...");

            String emailUsuario = evento.getUsuarioEmail();
            String nomeUsuario = evento.getUsuarioNome();

            if (emailUsuario != null) {
                System.out.println("   Email para: " + nomeUsuario + " <" + emailUsuario + ">");
                Thread.sleep(2000);
                System.out.println("   Email enviado com sucesso!");
            }

        } catch (InterruptedException e) {
            System.err.println("Erro ao enviar email: " + e.getMessage());
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.err.println("Erro ao enviar email: " + e.getMessage());
        }
    }
}