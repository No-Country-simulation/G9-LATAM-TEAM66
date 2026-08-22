package com.team66.backend.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AnalisisResponse(

                @JsonProperty("id_registro") Long idRegistro,

                @JsonProperty("categoria") CategoriaEnergetica categoria,

                @JsonProperty("probabilidad") Double probabilidad,

                @JsonProperty("consumo_kwh") BigDecimal consumoKwh,

                @JsonProperty("costo_estimado") BigDecimal costoEstimado,

                @JsonProperty("ahorro_estimado_mensual") BigDecimal ahorroEstimadoMensual,

                @JsonProperty("porcentaje_reduccion_estimado") BigDecimal porcentajeReduccionEstimado,

                @JsonProperty("comparacion") Comparacion comparacion,

                @JsonProperty("contexto_dataset") ContextoDataset contextoDataset,

                @JsonProperty("recomendaciones") List<String> recomendaciones

) {

        /** Como se compara este inmueble con otros del mismo tipo y categoria. */
        public record Comparacion(

                        @JsonProperty("consumo_por_habitante") BigDecimal consumoPorHabitante,

                        @JsonProperty("promedio_tipo_categoria") BigDecimal promedioTipoCategoria,

                        @JsonProperty("diferencia_porcentual") BigDecimal diferenciaPorcentual,

                        @JsonProperty("por_encima_del_promedio") Boolean porEncimaDelPromedio

        ) {
        }

        /** Que porcentaje de inmuebles comparables cae en la misma categoria. */
        public record ContextoDataset(

                        @JsonProperty("porcentaje_general") BigDecimal porcentajeGeneral,

                        @JsonProperty("porcentaje_tu_tipo_inmueble") BigDecimal porcentajeTuTipoInmueble,

                        @JsonProperty("porcentaje_tu_pais") BigDecimal porcentajeTuPais,

                        @JsonProperty("porcentaje_tu_localidad") BigDecimal porcentajeTuLocalidad,

                        @JsonProperty("distribucion_tu_tipo_inmueble") Map<String, BigDecimal> distribucionTuTipoInmueble

        ) {
        }
}
