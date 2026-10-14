package com.bankcore.backend.service.usecase;

import com.bankcore.backend.domain.TipoMovimiento;
import com.bankcore.backend.domain.model.Cuenta;
import com.bankcore.backend.domain.model.Transaccion;
import com.bankcore.backend.domain.event.TransaccionRealizadaEvent;
import com.bankcore.backend.domain.port.CuentaRepositoryPort;
import com.bankcore.backend.domain.port.TransaccionEventPublisherPort;
import com.bankcore.backend.domain.port.TransaccionRepositoryPort;
import com.bankcore.backend.dto.TransferenciaRequestDto;
import com.bankcore.backend.dto.TransferenciaResponseDto;
import com.bankcore.backend.service.IdempotenciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Caso de Uso Atómico (Patrón de Comportamiento: Command / Interactor).
 * Responsabilidad Única (SRP): Orquestar una transferencia bancaria atómica.
 */
@Service
@RequiredArgsConstructor
public class RealizarTransferenciaUseCase {

    private final CuentaRepositoryPort cuentaRepositoryPort;
    private final TransaccionRepositoryPort transaccionRepositoryPort;
    private final IdempotenciaService idempotenciaService;
    private final TransaccionEventPublisherPort transaccionEventPublisherPort;

    @Transactional(rollbackFor = Exception.class)
    public TransferenciaResponseDto ejecutar(TransferenciaRequestDto request) {
        return ejecutar(null, request);
    }

    @Transactional(rollbackFor = Exception.class)
    public TransferenciaResponseDto ejecutar(String idempotencyKey, TransferenciaRequestDto request) {
        
        // 1. Verificación de Idempotencia: Si ya fue procesada, devolvemos el comprobante previo sin tocar saldos
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            Optional<TransferenciaResponseDto> respuestaPrevia = idempotenciaService
                    .obtenerRespuestaPrevia(idempotencyKey, TransferenciaResponseDto.class);
            if (respuestaPrevia.isPresent()) {
                return respuestaPrevia.get();
            }
        }

        // 2. Regla de Negocio: No transferirse a la misma cuenta
        if (request.cuentaOrigen().equals(request.cuentaDestino())) {
            throw new IllegalArgumentException("La cuenta de origen y destino no pueden ser iguales");
        }

        // 3. Búsqueda mediante el Puerto del Dominio (DIP)
        Cuenta origen = cuentaRepositoryPort.buscarPorNumeroCuenta(request.cuentaOrigen())
                .orElseThrow(() -> new IllegalArgumentException("Cuenta de origen no encontrada: " + request.cuentaOrigen()));

        Cuenta destino = cuentaRepositoryPort.buscarPorNumeroCuenta(request.cuentaDestino())
                .orElseThrow(() -> new IllegalArgumentException("Cuenta de destino no encontrada: " + request.cuentaDestino()));

        // 4. Ejecución de lógica rica en el Dominio Puro
        origen.debitar(request.monto());
        destino.acreditar(request.monto());

        // 5. Persistencia atómica de ambas cuentas actualizadas
        cuentaRepositoryPort.guardar(origen);
        cuentaRepositoryPort.guardar(destino);

        // 6. Asiento contable inmutable en el Libro Mayor (General Ledger)
        String concepto = (request.concepto() != null && !request.concepto().isBlank())
                ? request.concepto()
                : "Transferencia interbancaria";

        Transaccion movimientoDebito = new Transaccion(
                null,
                origen.getId(),
                request.monto(),
                TipoMovimiento.TRANSFERENCIA_SALIENTE,
                "Transferencia a " + request.cuentaDestino() + ": " + concepto,
                LocalDateTime.now(),
                "EXITOSA"
        );

        Transaccion movimientoCredito = new Transaccion(
                null,
                destino.getId(),
                request.monto(),
                TipoMovimiento.TRANSFERENCIA_ENTRANTE,
                "Transferencia de " + request.cuentaOrigen() + ": " + concepto,
                LocalDateTime.now(),
                "EXITOSA"
        );

        transaccionRepositoryPort.guardar(movimientoDebito);
        transaccionRepositoryPort.guardar(movimientoCredito);

        // 7. Emisión de comprobante inmutable
        TransferenciaResponseDto respuesta = new TransferenciaResponseDto(
                UUID.randomUUID().toString(),
                request.cuentaOrigen(),
                request.cuentaDestino(),
                request.monto(),
                "EXITOSA",
                LocalDateTime.now(),
                "Transferencia realizada con éxito"
        );

        // 7. Registro de idempotencia para futuras peticiones repetidas con la misma clave
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            idempotenciaService.registrarRespuesta(idempotencyKey, "TRANSFERENCIA", 200, respuesta);
        }

        // 8. Publicación Asíncrona del Evento de Dominio (EDA / RabbitMQ)
        TransaccionRealizadaEvent evento = new TransaccionRealizadaEvent(
                respuesta.idTransaccion(),
                request.cuentaOrigen(),
                request.cuentaDestino(),
                request.monto(),
                "TRANSFERENCIA",
                respuesta.fechaHora(),
                "EXITOSA",
                concepto
        );
        transaccionEventPublisherPort.publicarTransaccionExitosa(evento);

        return respuesta;
    }
}
