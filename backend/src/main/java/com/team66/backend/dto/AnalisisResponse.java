package com.team66.backend.dto;

import java.math.BigDecimal;
import java.util.List;

public record AnalisisResponse(
        CategoriaEnergetica categoriaEnergetica,
        Double probabilidad,
        List<String> recomendaciones,
        BigDecimal costoEstimadoMensual

) {
}
