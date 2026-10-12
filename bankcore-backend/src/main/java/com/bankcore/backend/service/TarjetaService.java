package com.bankcore.backend.service;

import com.bankcore.backend.domain.model.Tarjeta;
import com.bankcore.backend.domain.port.TarjetaRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio de Aplicación / Fachada para el Subdominio de Tarjetas (GoF Façade Pattern).
 * Coordina las operaciones de ciclo de vida y seguridad de tarjetas de crédito/débito.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TarjetaService {

    private final TarjetaRepositoryPort tarjetaRepositoryPort;

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
     * Bloquea una tarjeta bancaria por motivos de seguridad o prevención de fraude.
     * Ejecuta el método de negocio puro en la entidad Tarjeta y persiste el cambio atómicamente.
     */
    @Transactional
    public Tarjeta bloquearTarjeta(Long id) {
        Tarjeta tarjeta = obtenerPorId(id);

        log.info("Seguridad Bancaria: Ejecutando bloqueo de tarjeta ID [{}] con franquicia [{}]",
                id, tarjeta.getFranquicia());

        tarjeta.bloquear();

        return tarjetaRepositoryPort.guardar(tarjeta);
    }
}
