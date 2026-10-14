package com.bankcore.backend.infrastructure.messaging.consumer;

import com.bankcore.backend.domain.event.TransaccionRealizadaEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Messaging Consumer: AuditoriaTransaccionConsumer")
class AuditoriaTransaccionConsumerTest {

    private AuditoriaTransaccionConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new AuditoriaTransaccionConsumer();
    }

    @Test
    @DisplayName("DADO un evento válido con concepto normal, ENTONCES procesa la auditoría exitosamente")
    void debeProcesarAuditoriaExitosamente() {
        TransaccionRealizadaEvent evento = new TransaccionRealizadaEvent(
                "tx-100",
                "1001",
                "1002",
                new BigDecimal("150.00"),
                "TRANSFERENCIA",
                LocalDateTime.now(),
                "EXITOSA",
                "Transferencia regular"
        );

        assertThatNoException().isThrownBy(() -> consumer.procesarAuditoriaTransaccion(evento));
    }

    @Test
    @DisplayName("DADO un evento con concepto nulo, ENTONCES procesa la auditoría sin errores")
    void debeProcesarAuditoriaConConceptoNulo() {
        TransaccionRealizadaEvent evento = new TransaccionRealizadaEvent(
                "tx-101",
                "1001",
                "1002",
                new BigDecimal("100.00"),
                "TRANSFERENCIA",
                LocalDateTime.now(),
                "EXITOSA",
                null
        );

        assertThatNoException().isThrownBy(() -> consumer.procesarAuditoriaTransaccion(evento));
    }

    @Test
    @DisplayName("DADO un evento con keyword de simulación de fallo, ENTONCES lanza AmqpRejectAndDontRequeueException para DLQ")
    void debeLanzarExcepcionParaRedirigirADlq() {
        TransaccionRealizadaEvent evento = new TransaccionRealizadaEvent(
                "tx-999",
                "1001",
                "1002",
                new BigDecimal("50000.00"),
                "TRANSFERENCIA",
                LocalDateTime.now(),
                "EXITOSA",
                "Operación con " + AuditoriaTransaccionConsumer.ERROR_SIMULATION_KEYWORD
        );

        assertThatThrownBy(() -> consumer.procesarAuditoriaTransaccion(evento))
                .isInstanceOf(AmqpRejectAndDontRequeueException.class)
                .hasMessageContaining("Fallo crítico en procesamiento de auditoría");
    }
}
