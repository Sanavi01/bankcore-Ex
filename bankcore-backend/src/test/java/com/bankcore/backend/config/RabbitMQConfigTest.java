package com.bankcore.backend.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DisplayName("Configuración: RabbitMQConfig Topology")
class RabbitMQConfigTest {

    private final RabbitMQConfig config = new RabbitMQConfig();

    @Test
    @DisplayName("DADO RabbitMQConfig, ENTONCES crea TopicExchange, DLX, Colas y Bindings con los nombres correctos")
    void debeConfigurarTopologiaCorrectamente() {
        TopicExchange topicExchange = config.bankEventsExchange();
        assertThat(topicExchange.getName()).isEqualTo(RabbitMQConfig.EXCHANGE_BANK_EVENTS);
        assertThat(topicExchange.isDurable()).isTrue();

        DirectExchange dlx = config.bankDeadLetterExchange();
        assertThat(dlx.getName()).isEqualTo(RabbitMQConfig.EXCHANGE_BANK_DLX);
        assertThat(dlx.isDurable()).isTrue();

        Queue auditQueue = config.auditQueue();
        assertThat(auditQueue.getName()).isEqualTo(RabbitMQConfig.QUEUE_AUDIT);
        assertThat(auditQueue.getArguments().get("x-dead-letter-exchange")).isEqualTo(RabbitMQConfig.EXCHANGE_BANK_DLX);
        assertThat(auditQueue.getArguments().get("x-dead-letter-routing-key")).isEqualTo(RabbitMQConfig.ROUTING_KEY_DLQ);

        Queue dlq = config.auditDeadLetterQueue();
        assertThat(dlq.getName()).isEqualTo(RabbitMQConfig.QUEUE_AUDIT_DLQ);

        Binding auditBinding = config.auditBinding(auditQueue, topicExchange);
        assertThat(auditBinding.getExchange()).isEqualTo(RabbitMQConfig.EXCHANGE_BANK_EVENTS);
        assertThat(auditBinding.getRoutingKey()).isEqualTo(RabbitMQConfig.ROUTING_KEY_TRANSACCION_WILDCARD);

        Binding dlqBinding = config.auditDlqBinding(dlq, dlx);
        assertThat(dlqBinding.getExchange()).isEqualTo(RabbitMQConfig.EXCHANGE_BANK_DLX);
        assertThat(dlqBinding.getRoutingKey()).isEqualTo(RabbitMQConfig.ROUTING_KEY_DLQ);

        assertThat(config.jsonMessageConverter()).isInstanceOf(Jackson2JsonMessageConverter.class);

        ConnectionFactory mockFactory = mock(ConnectionFactory.class);
        assertThat(config.rabbitTemplate(mockFactory, config.jsonMessageConverter())).isNotNull();
    }
}
