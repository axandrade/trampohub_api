package br.com.alexandrade.trampohub_api.config;

import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
@Profile("!test")
public class RabbitAdminInitializer {

    private final RabbitAdmin rabbitAdmin;

    public RabbitAdminInitializer(RabbitAdmin rabbitAdmin) {
        this.rabbitAdmin = rabbitAdmin;
    }

    @PostConstruct
    public void initializeRabbitMQ() {
        System.out.println("Inicializando RabbitMQ...");
        rabbitAdmin.purgeQueue("candidatura-criada-queue");
        System.out.println("RabbitMQ inicializado com sucesso!");
    }
}