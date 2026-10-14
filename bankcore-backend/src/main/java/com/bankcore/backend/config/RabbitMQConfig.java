package com.bankcore.backend.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de Infraestructura de Mensajería Asíncrona (Spring AMQP / RabbitMQ).
 * 
 * Topología Bancaria Implementada:
 * 1. Topic Exchange: 'bank.events' (Enrutamiento flexible por routing key).
 * 2. Cola Principal: 'transacciones.audit.queue' con Dead-Lettering habilitado.
 * 3. Dead Letter Exchange (DLX): 'bank.dlx'.
 * 4. Dead Letter Queue (DLQ): 'transacciones.audit.dlq' para aislamiento de mensajes fallidos.
 * 5. Serialización JSON nativa mediante Jackson2JsonMessageConverter.
 */
@Configuration
public class RabbitMQConfig {

    // Constantes de Topología de Mensajería
    public static final String EXCHANGE_BANK_EVENTS = "bank.events";
    public static final String QUEUE_AUDIT = "transacciones.audit.queue";
    public static final String ROUTING_KEY_TRANSACCION_EXITOSA = "transaccion.exitosa";
    public static final String ROUTING_KEY_TRANSACCION_WILDCARD = "transaccion.*";

    // Constantes de Dead Letter Queue (DLQ / Resiliencia)
    public static final String EXCHANGE_BANK_DLX = "bank.dlx";
    public static final String QUEUE_AUDIT_DLQ = "transacciones.audit.dlq";
    public static final String ROUTING_KEY_DLQ = "transacciones.audit.dlq";

    /**
     * Topic Exchange principal para eventos de dominio del banco.
     */
    @Bean
    public TopicExchange bankEventsExchange() {
        return new TopicExchange(EXCHANGE_BANK_EVENTS, true, false);
    }

    /**
     * Dead Letter Exchange (Direct) para redirigir mensajes que agotan reintentos o son rechazados.
     */
    @Bean
    public DirectExchange bankDeadLetterExchange() {
        return new DirectExchange(EXCHANGE_BANK_DLX, true, false);
    }

    /**
     * Cola de auditoría transaccional.
     * Configurada con argumentos x-dead-letter para resiliencia bancaria.
     */
    @Bean
    public Queue auditQueue() {
        return QueueBuilder.durable(QUEUE_AUDIT)
                .withArgument("x-dead-letter-exchange", EXCHANGE_BANK_DLX)
                .withArgument("x-dead-letter-routing-key", ROUTING_KEY_DLQ)
                .build();
    }

    /**
     * Cola Dead Letter (DLQ) para almacenar mensajes no procesables sin pérdida de datos.
     */
    @Bean
    public Queue auditDeadLetterQueue() {
        return QueueBuilder.durable(QUEUE_AUDIT_DLQ).build();
    }

    /**
     * Binding: Conecta la cola de auditoría al exchange principal para cualquier evento 'transaccion.*'.
     */
    @Bean
    public Binding auditBinding(Queue auditQueue, TopicExchange bankEventsExchange) {
        return BindingBuilder.bind(auditQueue)
                .to(bankEventsExchange)
                .with(ROUTING_KEY_TRANSACCION_WILDCARD);
    }

    /**
     * Binding: Conecta la cola DLQ al Dead Letter Exchange.
     */
    @Bean
    public Binding auditDlqBinding(Queue auditDeadLetterQueue, DirectExchange bankDeadLetterExchange) {
        return BindingBuilder.bind(auditDeadLetterQueue)
                .to(bankDeadLetterExchange)
                .with(ROUTING_KEY_DLQ);
    }

    /**
     * Convertidor de mensajes a formato JSON usando Jackson.
     * Garantiza serialización interoperable de Records y DTOs en el broker.
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * Plantilla de RabbitMQ configurada con el serializador JSON para publicación de eventos.
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter);
        return template;
    }
}
