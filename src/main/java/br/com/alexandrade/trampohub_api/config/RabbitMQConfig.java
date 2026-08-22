package br.com.alexandrade.trampohub_api.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // ========== RABBIT ADMIN ==========

    /**
     * RabbitAdmin é responsável por declarar exchanges, queues e bindings
     */
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    // ========== MESSAGE CONVERTER ==========

    /**
     * JSON em vez de serialização Java nativa: legível no painel do RabbitMQ
     * e não exige que todo o grafo de objetos implemente Serializable.
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    // ========== EXCHANGES ==========

    @Bean
    public DirectExchange candidaturaExchange() {
        return new DirectExchange("candidatura-exchange", true, false);
    }

    // ========== QUEUES ==========

    @Bean
    public Queue candidaturaCriadaQueue() {
        return new Queue("candidatura-criada-queue", true);
    }

    @Bean
    public Queue candidaturaEmailQueue() {
        return new Queue("candidatura-email-queue", true);
    }

    @Bean
    public Queue candidaturaNotificacaoQueue() {
        return new Queue("candidatura-notificacao-queue", true);
    }

    // ========== BINDINGS ==========

    @Bean
    public Binding bindingCriar(Queue candidaturaCriadaQueue,
                                DirectExchange candidaturaExchange) {
        return BindingBuilder.bind(candidaturaCriadaQueue)
                .to(candidaturaExchange)
                .with("candidatura.criada");
    }

    @Bean
    public Binding bindingEmail(Queue candidaturaEmailQueue,
                                DirectExchange candidaturaExchange) {
        return BindingBuilder.bind(candidaturaEmailQueue)
                .to(candidaturaExchange)
                .with("candidatura.criada");
    }

    @Bean
    public Binding bindingNotificacao(Queue candidaturaNotificacaoQueue,
                                      DirectExchange candidaturaExchange) {
        return BindingBuilder.bind(candidaturaNotificacaoQueue)
                .to(candidaturaExchange)
                .with("candidatura.criada");
    }
}