package com.team66.backend.controller;

import com.team66.backend.dto.AnalisisResponse;
import com.team66.backend.dto.CategoriaEnergetica;
import com.team66.backend.dto.ConsumoRequest;
import com.team66.backend.dto.FrecuenciaUso;
import com.team66.backend.dto.TipoInmueble;
import com.team66.backend.service.AnalisisEnergeticoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalisisEnergeticoControllerTest {

    @Mock
    private AnalisisEnergeticoService service;

    private AnalisisEnergeticoController controller;

    @BeforeEach
    void setUp() {
        controller = new AnalisisEnergeticoController(service);
    }

    private ConsumoRequest requestValido() {
        return new ConsumoRequest(
                "Colombia",
                "Bogotá",
                new BigDecimal("18.5"),
                TipoInmueble.Casa,
                4,
                new BigDecimal("320.50"),
                true,
                8,
                FrecuenciaUso.Alta,
                6);
    }

    private AnalisisResponse respuestaSimulada() {
        return new AnalisisResponse(
                1L,
                CategoriaEnergetica.Moderado,
                0.87,
                new BigDecimal("320.50"),
                new BigDecimal("240.38"),
                new BigDecimal("50.00"),
                new BigDecimal("20.5"),
                new AnalisisResponse.Comparacion(
                        new BigDecimal("80.13"),
                        new BigDecimal("76.82"),
                        new BigDecimal("4.3"),
                        true),
                new AnalisisResponse.ContextoDataset(
                        new BigDecimal("33.0"),
                        new BigDecimal("34.18"),
                        new BigDecimal("33.5"),
                        new BigDecimal("32.0"),
                        Map.of("Moderado", new BigDecimal("34.18"))),
                List.of("Desconectar aparatos en modo de espera (consumo vampiro)"));
    }

    @Test
    void analizar_devuelveOkConLaRespuestaDelService() {
        AnalisisResponse esperada = respuestaSimulada();
        when(service.analizarConsumo(any(ConsumoRequest.class))).thenReturn(esperada);

        ResponseEntity<AnalisisResponse> response = controller.analizar(requestValido());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(esperada);
    }

    @Test
    void analizar_delegaElRequestTalCualAlService() {
        ConsumoRequest request = requestValido();
        when(service.analizarConsumo(any(ConsumoRequest.class))).thenReturn(respuestaSimulada());

        controller.analizar(request);

        verify(service).analizarConsumo(request);
    }
}
