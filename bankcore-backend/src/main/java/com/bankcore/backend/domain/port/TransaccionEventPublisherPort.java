package com.bankcore.backend.domain.port;

import com.bankcore.backend.domain.event.TransaccionRealizadaEvent;

/**
 * Puerto de Salida del Dominio para Publicación de Eventos (DIP / Clean Architecture).
 * Desacopla la lógica de negocio de los detalles de mensajería (RabbitMQ / Kafka).
 */
public interface TransaccionEventPublisherPort {

    /**
     * Publica el evento de transacción realizada en el broker de mensajería.
     *
     * @param evento Evento de dominio inmutable con los detalles de la transferencia.
     */
    void publicarTransaccionExitosa(TransaccionRealizadaEvent evento);
}
