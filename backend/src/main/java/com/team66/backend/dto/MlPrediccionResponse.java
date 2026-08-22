package com.team66.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MlPrediccionResponse(

        CategoriaEnergetica categoria,

        Double probabilidad,

        @JsonProperty("estimacion_financiera") EstimacionFinanciera estimacionFinanciera,

        List<String> recomendaciones

) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record EstimacionFinanciera(

            @JsonProperty("costo_actual_mensual") BigDecimal costoActualMensual,

            @JsonProperty("ahorro_monetario_mensual") BigDecimal ahorroMonetarioMensual,

            @JsonProperty("porcentaje_reduccion_estimado") BigDecimal porcentajeReduccionEstimado

    ) {
    }
}
