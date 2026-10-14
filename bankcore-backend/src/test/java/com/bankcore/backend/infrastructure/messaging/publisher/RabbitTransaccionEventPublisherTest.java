package com.bankcore.backend.infrastructure.messaging.publisher;

import com.bankcore.backend.config.RabbitMQConfig;
import com.bankcore.backend.domain.event.TransaccionRealizadaEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpConnectException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Messaging Publisher: RabbitTransaccionEventPublisher")
class RabbitTransaccionEventPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private RabbitTransaccionEventPublisher publisher;

    @Test
    @DisplayName("DADO un evento de transacción realizada, ENTONCES publica al topic exchange correcto con su routing key")
    void debePublicarEventoCorrectamente() {
        TransaccionRealizadaEvent evento = new TransaccionRealizadaEvent(
                "tx-123",
                "1001",
                "1002",
                new BigDecimal("250.00"),
                "TRANSFERENCIA",
                LocalDateTime.now(),
                "EXITOSA",
                "Pago de servicios"
        );

        publisher.publicarTransaccionExitosa(evento);

        verify(rabbitTemplate, times(1)).convertAndSend(
                eq(RabbitMQConfig.EXCHANGE_BANK_EVENTS),
                eq(RabbitMQConfig.ROUTING_KEY_TRANSACCION_EXITOSA),
                eq(evento)
        );
    }

    @Test
    @DisplayName("DADO un error de conexión con el broker AMQP, ENTONCES captura la excepción y no propaga error")
    void debeCapturarExcepcionDeConexionAmqp() {
        TransaccionRealizadaEvent evento = new TransaccionRealizadaEvent(
                "tx-500",
                "1001",
                "1002",
                new BigDecimal("100.00"),
                "TRANSFERENCIA",
                LocalDateTime.now(),
                "EXITOSA",
                "Prueba de resiliencia"
        );

        doThrow(new AmqpConnectException(new Exception("Broker caído")))
                .when(rabbitTemplate)
                .convertAndSend(anyString(), anyString(), any(Object.class));

        assertThatNoException().isThrownBy(() -> publisher.publicarTransaccionExitosa(evento));
    }
}
