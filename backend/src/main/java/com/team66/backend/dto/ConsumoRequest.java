package com.team66.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ConsumoRequest(

        @NotBlank(message = "El pais es obligatorio") String pais,

        @NotBlank(message = "La localidad es obligatoria") String localidad,

        @NotNull(message = "La temperatura ambiente es obligatoria") @JsonProperty("temperatura_ambiente") BigDecimal temperaturaAmbiente,

        @NotNull(message = "El tipo de inmueble es obligatorio") @JsonProperty("tipo_inmueble") TipoInmueble tipoInmueble,

        @NotNull(message = "El numero de habitantes es obligatorio") @Min(value = 1, message = "Debe existir al menos un habitante") @JsonProperty("numero_habitantes") Integer numeroHabitantes,

        @NotNull(message = "El consumo en kWh es obligatorio") @DecimalMin(value = "0.01", message = "El consumo debe ser mayor a 0") @JsonProperty("consumo_kwh") BigDecimal consumoKwh,

        @NotNull(message = "El uso en horario pico es obligatorio") @JsonProperty("uso_horario_pico") Boolean usoHorarioPico,

        @NotNull(message = "La cantidad de equipos es obligatoria") @Min(value = 1, message = "Debe existir al menos un equipo") @JsonProperty("cantidad_equipos") Integer cantidadEquipos,

        @NotNull(message = "La frecuencia de uso es obligatoria") @JsonProperty("frecuencia_uso") FrecuenciaUso frecuenciaUso,

        @NotNull(message = "Las horas de alto consumo son obligatorias") @Min(value = 0, message = "Las horas de alto consumo no pueden ser negativas") @Max(value = 24, message = "Las horas de alto consumo no pueden superar 24") @JsonProperty("horas_alto_consumo") Integer horasAltoConsumo

) {
}
