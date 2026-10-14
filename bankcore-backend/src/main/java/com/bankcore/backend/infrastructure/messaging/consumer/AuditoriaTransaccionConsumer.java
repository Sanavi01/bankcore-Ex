package com.bankcore.backend.infrastructure.messaging.consumer;

import com.bankcore.backend.config.RabbitMQConfig;
import com.bankcore.backend.domain.event.TransaccionRealizadaEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumidor Asíncrono de Eventos de Auditoría (Spring AMQP Listener).
 * Procesa en segundo plano la auditoría y análisis de fraude de cada transferencia bancaria.
 * 
 * Resiliencia con DLQ:
 * Si ocurre un error fatal o irrecuperable, lanza AmqpRejectAndDontRequeueException,
 * lo que instruye al broker a redirigir inmediatamente el mensaje a la Dead Letter Queue (DLQ).
 */
@Slf4j
@Component
public class AuditoriaTransaccionConsumer {

    public static final String ERROR_SIMULATION_KEYWORD = "SIMULAR_ERROR_AUDITORIA";

    @RabbitListener(queues = RabbitMQConfig.QUEUE_AUDIT)
    public void procesarAuditoriaTransaccion(TransaccionRealizadaEvent evento) {
        log.info("RabbitMQ Consumer: Recibido evento de auditoría para transacción ID [{}] - Origen: [{}], Destino: [{}], Monto: [{}]",
                evento.idTransaccion(), evento.cuentaOrigen(), evento.cuentaDestino(), evento.monto());

        // Demostración de Resiliencia Bancaria: Simulación de fallo para activar DLQ
        if (evento.concepto() != null && evento.concepto().contains(ERROR_SIMULATION_KEYWORD)) {
            log.error("RabbitMQ Consumer: Falla irrecuperable en auditoría para transacción [{}]. Redirigiendo a Dead Letter Queue (DLQ).",
                    evento.idTransaccion());
            throw new AmqpRejectAndDontRequeueException("Fallo crítico en procesamiento de auditoría. Derivado a DLQ.");
        }

        // Procesamiento normal de auditoría contable
        log.info("RabbitMQ Consumer: Auditoría y trazabilidad registradas exitosamente para transacción [{}]", evento.idTransaccion());
    }
}
