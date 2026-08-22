package com.team66.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Respuesta del microservicio de ML (ml-service) en POST /predecir.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record MlPrediccionResponse(

        CategoriaEnergetica categoria,

        Double probabilidad,

        @JsonProperty("estimacion_financiera") EstimacionFinanciera estimacionFinanciera,

        Comparacion comparacion,

        @JsonProperty("contexto_dataset") ContextoDataset contextoDataset,

        List<String> recomendaciones

) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record EstimacionFinanciera(

            @JsonProperty("costo_actual_mensual") BigDecimal costoActualMensual,

            @JsonProperty("ahorro_monetario_mensual") BigDecimal ahorroMonetarioMensual,

            @JsonProperty("porcentaje_reduccion_estimado") BigDecimal porcentajeReduccionEstimado

    ) {
    }

    /** Comparacion contra el promedio de inmuebles del mismo tipo y categoria. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Comparacion(

            @JsonProperty("consumo_por_habitante") BigDecimal consumoPorHabitante,

            @JsonProperty("promedio_tipo_categoria") BigDecimal promedioTipoCategoria,

            @JsonProperty("diferencia_porcentual") BigDecimal diferenciaPorcentual,

            @JsonProperty("por_encima_del_promedio") Boolean porEncimaDelPromedio

    ) {
    }

    /** Ubicacion del inmueble dentro del dataset (% de inmuebles en su misma categoria). */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ContextoDataset(

            @JsonProperty("porcentaje_general") BigDecimal porcentajeGeneral,

            @JsonProperty("porcentaje_tu_tipo_inmueble") BigDecimal porcentajeTuTipoInmueble,

            @JsonProperty("porcentaje_tu_pais") BigDecimal porcentajeTuPais,

            @JsonProperty("porcentaje_tu_localidad") BigDecimal porcentajeTuLocalidad,

            @JsonProperty("distribucion_tu_tipo_inmueble") Map<String, BigDecimal> distribucionTuTipoInmueble

    ) {
    }
}
