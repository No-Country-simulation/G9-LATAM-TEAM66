package com.team66.backend.dto;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AnalisisResponse(

        @JsonProperty("id_registro")
        Long idRegistro,

        @JsonProperty("categoria")
        CategoriaEnergetica categoria,

        @JsonProperty("consumo_kwh")
        BigDecimal consumoKwh,

        @JsonProperty("costo_estimado")
        BigDecimal costoEstimado,

        @JsonProperty("diagnostico")
        String diagnostico,

        @JsonProperty("recomendaciones")
        List<String> recomendaciones

) {
}
