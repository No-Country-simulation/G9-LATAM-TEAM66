package com.team66.backend.domain.analisis;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record DTOAnalisisEnergetico(

    @NotNull
    @Positive
    @DecimalMax(value = "100000")
    BigDecimal consumoKwh,

    @NotNull
    Boolean usoHorarioPico,

    @NotNull
    @Min(1)
    @Max(100)
    Integer cantidadEquipos,

    @NotNull
    TipoInmueble tipoInmueble,

    @NotNull
    @Min(0)
    @Max(24)
    Integer horasAltoConsumo
) {}

