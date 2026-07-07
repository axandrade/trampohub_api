package br.com.alexandrade.trampohub_api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/** Substitui a task Celery/RabbitMQ: sem fila externa, so registra a notificacao no log. */
@Service
public class NotificacaoService {

    private static final Logger log = LoggerFactory.getLogger(NotificacaoService.class);

    public void notificarNovaCandidatura(String vagaTitulo, String candidatoId) {
        log.info("[NOTIFICAÇÃO] Nova candidatura recebida para a vaga '{}' do candidato ID {}",
                vagaTitulo, candidatoId);
    }
}
