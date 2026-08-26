package br.com.alexandrade.trampohub_api.service.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import br.com.alexandrade.trampohub_api.event.CandidaturaEvent;

@Service
public class CandidaturaArquivoConsumer {

    @KafkaListener(topics = "candidatura-criada", groupId = "arquivo-processors")
    public void processarArquivo(CandidaturaEvent evento) {
        try {
            System.out.println("[Kafka Consumer - Arquivo] Processando arquivo...");

            if (evento.getArquivoBase64() != null) {
                System.out.println("   Arquivo: " + evento.getNomeArquivo());
                System.out.println("   Tamanho: " + evento.getTamanhoArquivo() + " bytes");
                System.out.println("   Enviando para S3...");
                Thread.sleep(3000);
                System.out.println("   Arquivo enviado para S3!");
                System.out.println("   URL: https://s3.amazonaws.com/trampohub/" + evento.getNomeArquivo());
            }

        } catch (InterruptedException e) {
            System.err.println("Erro ao processar arquivo: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}