package com.bankcore.backend.infrastructure.messaging.publisher;

import com.bankcore.backend.config.RabbitMQConfig;
import com.bankcore.backend.domain.event.TransaccionRealizadaEvent;
import com.bankcore.backend.domain.port.TransaccionEventPublisherPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * Adaptador Secundario de Infraestructura para Publicación de Eventos en RabbitMQ.
 * Implementa el puerto del dominio TransaccionEventPublisherPort.
 * 
 * Resiliencia Financiera:
 * Si el broker de mensajería experimenta una falla transitoria de red, captura la excepción
 * para evitar que la transacción bancaria (ya confirmada en PostgreSQL) sea revertida
 * de forma destructiva, dejando traza para conciliación o outbox.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitTransaccionEventPublisher implements TransaccionEventPublisherPort {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publicarTransaccionExitosa(TransaccionRealizadaEvent evento) {
        log.info("RabbitMQ Publisher: Publicando evento [TransaccionRealizadaEvent] ID [{}] al Exchange [{}] con RoutingKey [{}]",
                evento.idTransaccion(), RabbitMQConfig.EXCHANGE_BANK_EVENTS, RabbitMQConfig.ROUTING_KEY_TRANSACCION_EXITOSA);

        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_BANK_EVENTS,
                    RabbitMQConfig.ROUTING_KEY_TRANSACCION_EXITOSA,
                    evento
            );
        } catch (AmqpException e) {
            log.error("RabbitMQ Publisher: Falla de conexión con el broker al publicar evento ID [{}]: {}",
                    evento.idTransaccion(), e.getMessage());
        }
    }
}
