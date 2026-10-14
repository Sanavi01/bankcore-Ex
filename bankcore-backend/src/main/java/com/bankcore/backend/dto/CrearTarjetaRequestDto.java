package com.bankcore.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * Contrato inmutable (Record) para solicitud de emisión y vinculación de nueva tarjeta bancaria.
 */
@Schema(description = "Solicitud de emisión y vinculación de tarjeta a una cuenta bancaria")
public record CrearTarjetaRequestDto(
    @Schema(description = "ID de la cuenta bancaria propietaria", example = "1")
    @NotNull(message = "El ID de la cuenta es obligatorio")
    Long cuentaId,

    @Schema(description = "Franquicia de la tarjeta", example = "VISA", allowableValues = {"VISA", "MASTERCARD", "AMEX"})
    @NotBlank(message = "La franquicia es obligatoria")
    @Pattern(regexp = "^(VISA|MASTERCARD|AMEX)$", message = "La franquicia debe ser VISA, MASTERCARD o AMEX")
    String franquicia,

    @Schema(description = "Cupo total otorgado a la tarjeta", example = "15000.00")
    @NotNull(message = "El cupo total es obligatorio")
    @Positive(message = "El cupo total debe ser estrictamente mayor a cero")
    BigDecimal cupoTotal
) {}
