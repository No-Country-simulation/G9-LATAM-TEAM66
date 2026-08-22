package com.team66.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record MlPrediccionRequest(

        String pais,

        String localidad,

        @JsonProperty("tipo_inmueble") String tipoInmueble,

        @JsonProperty("numero_habitantes") Integer numeroHabitantes,

        @JsonProperty("cantidad_equipos") Integer cantidadEquipos,

        @JsonProperty("frecuencia_uso") String frecuenciaUso,

        @JsonProperty("horas_alto_consumo") Integer horasAltoConsumo,

        @JsonProperty("uso_horario_pico") Boolean usoHorarioPico,

        @JsonProperty("temperatura_ambiente") BigDecimal temperaturaAmbiente,

        @JsonProperty("consumo_kwh") BigDecimal consumoKwh

) {
}
