package com.bankcore.backend.service;

import com.bankcore.backend.domain.EstadoCuenta;
import com.bankcore.backend.domain.model.Cuenta;
import com.bankcore.backend.domain.model.Tarjeta;
import com.bankcore.backend.domain.port.CuentaRepositoryPort;
import com.bankcore.backend.domain.port.TarjetaRepositoryPort;
import com.bankcore.backend.dto.CrearTarjetaRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Servicio de Aplicación / Fachada para el Subdominio de Tarjetas (GoF Façade Pattern).
 * Coordina las operaciones de emisión, ciclo de vida y seguridad de tarjetas de crédito/débito.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TarjetaService {

    private final TarjetaRepositoryPort tarjetaRepositoryPort;
    private final CuentaRepositoryPort cuentaRepositoryPort;
    private final SecureRandom random = new SecureRandom();

    @Transactional(readOnly = true)
    public Tarjeta obtenerPorId(Long id) {
        return tarjetaRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Tarjeta no encontrada con ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Tarjeta> obtenerPorCuentaId(Long cuentaId) {
        return tarjetaRepositoryPort.buscarPorCuentaId(cuentaId);
    }

    /**
     * Emite una nueva tarjeta vinculada a una cuenta bancaria activa.
     */
    @Transactional
    public Tarjeta emitirTarjeta(CrearTarjetaRequestDto request) {
        Cuenta cuenta = cuentaRepositoryPort.buscarPorId(request.cuentaId())
                .orElseThrow(() -> new IllegalArgumentException("No existe la cuenta con ID: " + request.cuentaId()));

        if (cuenta.getEstado() != EstadoCuenta.ACTIVA) {
            throw new IllegalStateException("No se pueden emitir tarjetas asociadas a una cuenta inactiva o bloqueada");
        }

        String numeroTarjeta = generarNumeroTarjeta(request.franquicia());
        String fechaVencimiento = calcularFechaVencimiento();
        String cvv = generarCvv();

        Tarjeta nuevaTarjeta = Tarjeta.emitir(
                numeroTarjeta,
                request.franquicia().toUpperCase(),
                fechaVencimiento,
                cvv,
                request.cupoTotal(),
                cuenta.getId()
        );

        log.info("Emisión de Tarjeta: Creada tarjeta [{}] para cuenta ID [{}] con cupo [{}]",
                nuevaTarjeta.getFranquicia(), cuenta.getId(), nuevaTarjeta.getCupoTotal());

        return tarjetaRepositoryPort.guardar(nuevaTarjeta);
    }

    /**
     * Bloquea una tarjeta bancaria por motivos de seguridad o prevención de fraude.
     */
    @Transactional
    public Tarjeta bloquearTarjeta(Long id) {
        Tarjeta tarjeta = obtenerPorId(id);

        log.info("Seguridad Bancaria: Ejecutando bloqueo preventivo de tarjeta ID [{}] con franquicia [{}]",
                id, tarjeta.getFranquicia());

        tarjeta.bloquear();

        return tarjetaRepositoryPort.guardar(tarjeta);
    }

    /**
     * Reactiva o desbloquea una tarjeta bancaria bloqueada previamente.
     */
    @Transactional
    public Tarjeta desbloquearTarjeta(Long id) {
        Tarjeta tarjeta = obtenerPorId(id);

        log.info("Seguridad Bancaria: Reactivando tarjeta ID [{}] con franquicia [{}]",
                id, tarjeta.getFranquicia());

        tarjeta.desbloquear();

        return tarjetaRepositoryPort.guardar(tarjeta);
    }

    // ====================================================================
    // MÉTODOS AUXILIARES DETERMINISTAS DE GENERACIÓN BANCARIA
    // ====================================================================

    private String generarNumeroTarjeta(String franquicia) {
        String prefijo = switch (franquicia.toUpperCase()) {
            case "VISA" -> "4532";
            case "MASTERCARD" -> "5412";
            case "AMEX" -> "3712";
            default -> "4000";
        };

        long sufijo = 100000000000L + (long)(random.nextDouble() * 899999999999L);
        return prefijo + String.valueOf(sufijo).substring(0, 12);
    }

    private String calcularFechaVencimiento() {
        LocalDate futuro = LocalDate.now().plusYears(5);
        return futuro.format(DateTimeFormatter.ofPattern("MM/yy"));
    }

    private String generarCvv() {
        return String.format("%03d", random.nextInt(900) + 100);
    }
}
