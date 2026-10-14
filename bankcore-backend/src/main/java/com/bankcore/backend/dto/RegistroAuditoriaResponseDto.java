package com.bankcore.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Contrato inmutable para consulta de registros de auditoría y cumplimiento AML.
 */
@Schema(description = "Registro de auditoría forense procesado asíncronamente vía RabbitMQ")
public record RegistroAuditoriaResponseDto(
    @Schema(description = "Identificador secuencial del registro de auditoría", example = "101")
    Long id,

    @Schema(description = "Identificador único de la transacción bancaria", example = "TX-789a-45b2")
    String idTransaccion,

    @Schema(description = "Número de cuenta que originó la transacción", example = "001-ORIGEN")
    String cuentaOrigen,

    @Schema(description = "Número de cuenta que recibió los fondos", example = "002-DESTINO")
    String cuentaDestino,

    @Schema(description = "Monto monetario de la operación", example = "250.00")
    BigDecimal monto,

    @Schema(description = "Canal de origen de la transacción", example = "CORE_BANKING")
    String canal,

    @Schema(description = "Clasificación de riesgo de lavado de activos (AML)", example = "BAJO_RIESGO")
    String clasificacionRiesgo,

    @Schema(description = "Fecha y hora en que ocurrió la transacción en el core")
    LocalDateTime fechaEvento,

    @Schema(description = "Fecha y hora en que RabbitMQ persistió la auditoría")
    LocalDateTime fechaAuditoria
) {}
