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
                TipoInmueble.Casa,
                new BigDecimal("320.50"),
                true,
                8,
                FrecuenciaUso.Alta,
                6
        );
    }

    @Test
    void analizar_devuelveOkConLaRespuestaDelService() {
        AnalisisResponse respuestaSimulada = new AnalisisResponse(
                1L,
                CategoriaEnergetica.Moderado,
                new BigDecimal("320.50"),
                new BigDecimal("240.38"),
                List.of("Desconectar aparatos en modo de espera (consumo vampiro)")
        );
        when(service.analizarConsumo(any(ConsumoRequest.class))).thenReturn(respuestaSimulada);

        ResponseEntity<AnalisisResponse> response = controller.analizar(requestValido());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(respuestaSimulada);
    }

    @Test
    void analizar_delegaElRequestTalCualAlService() {
        ConsumoRequest request = requestValido();
        when(service.analizarConsumo(any(ConsumoRequest.class))).thenReturn(
                new AnalisisResponse(1L, CategoriaEnergetica.Eficiente, request.consumoKwh(),
                        BigDecimal.ONE, List.of()));

        controller.analizar(request);

        verify(service).analizarConsumo(request);
    }
}
