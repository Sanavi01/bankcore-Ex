package com.bankcore.backend.infrastructure.messaging.consumer;

import com.bankcore.backend.domain.event.TransaccionRealizadaEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatNoException;

@DisplayName("Messaging DLQ Consumer: AuditoriaDlqConsumer")
class AuditoriaDlqConsumerTest {

    @Test
    @DisplayName("DADO un mensaje recibido en la DLQ, ENTONCES procesa la alerta de seguridad sin lanzar excepciones")
    void debeProcesarMensajeMuerto() {
        AuditoriaDlqConsumer dlqConsumer = new AuditoriaDlqConsumer();

        TransaccionRealizadaEvent eventoMuerto = new TransaccionRealizadaEvent(
                "tx-failed-123",
                "1001",
                "1002",
                new BigDecimal("9999.00"),
                "TRANSFERENCIA",
                LocalDateTime.now(),
                "FALLIDA",
                "Fallo irrecuperable en auditoría"
        );

        assertThatNoException().isThrownBy(() -> dlqConsumer.procesarMensajeMuerto(eventoMuerto));
    }
}
