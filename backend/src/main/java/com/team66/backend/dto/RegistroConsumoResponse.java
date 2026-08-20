package com.team66.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RegistroConsumoResponse(
    Long id,

    @JsonProperty("fecha_registro") LocalDate fechaRegistro,

    String pais,

    String localidad,

    @JsonProperty("temperatura_ambiente") Integer temperaturaAmbiente,

    @JsonProperty("tipo_inmueble") TipoInmueble tipoInmueble,

    @JsonProperty("numero_habitantes") Integer numeroHabitantes,

    @JsonProperty("cantidad_equipos") Integer cantidadEquipos,

    @JsonProperty("frecuencia_uso") FrecuenciaUso frecuenciaUso,

    @JsonProperty("horas_alto_consumo") Integer horasAltoConsumo,

    @JsonProperty("uso_horario_pico") Boolean usoHorarioPico,

    @JsonProperty("consumo_kwh") BigDecimal consumoKwh,

    CategoriaEnergetica categoria,

    @JsonProperty("costo_estimado") BigDecimal costoEstimado) {

}
