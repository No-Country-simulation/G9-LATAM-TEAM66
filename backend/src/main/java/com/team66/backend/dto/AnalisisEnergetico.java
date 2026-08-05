package com.team66.backend.dto;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AnalisisEnergetico(

    @NotNull(message = "La fecha de registro es obligatoria")
    LocalDate fechaRegistro,

    @NotNull(message = "El tipo de inmueble es obligatorio")
    TipoInmueble tipoInmueble,

    @NotNull(message = "El consumo en kWh es obligatorio")
    @DecimalMin(value = "0.01", message = "El consumo debe ser mayor a 0")
    BigDecimal consumoKwh,

    @NotNull(message = "El uso en horario pico es obligatorio")
    Boolean usoHorarioPico,

    @NotNull(message = "La cantidad de equipos es obligatoria")
    @Min(value = 1, message = "Debe existir al menos un equipo")
    Integer cantidadEquipos,

    @NotNull(message = "La frecuencia de uso es obligatoria")
    FrecuenciaUso frecuenciaUso,

    @NotNull(message = "Las horas de alto consumo son obligatorias")
    @Min(value = 0, message = "Las horas de alto consumo no pueden ser negativas")
    @Max(value = 24, message = "Las horas de alto consumo no pueden superar 24")
    Integer horasAltoConsumo

) {
}