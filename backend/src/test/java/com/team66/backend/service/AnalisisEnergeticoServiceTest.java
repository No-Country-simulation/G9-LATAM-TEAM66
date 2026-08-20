package com.team66.backend.service;

import com.team66.backend.dto.AnalisisResponse;
import com.team66.backend.dto.CategoriaEnergetica;
import com.team66.backend.dto.ConsumoRequest;
import com.team66.backend.dto.FrecuenciaUso;
import com.team66.backend.dto.TipoInmueble;
import com.team66.backend.model.RegistroConsumo;
import com.team66.backend.repository.RegistroConsumoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AnalisisEnergeticoServiceTest {

    @Mock
    private RegistroConsumoRepository repository;

    private AnalisisEnergeticoService service;

    @BeforeEach
    void setUp() {
        service = new AnalisisEnergeticoService(repository);
        lenient().when(repository.save(any(RegistroConsumo.class)))
                .thenAnswer(invocation -> {
                    RegistroConsumo entidad = invocation.getArgument(0);
                    entidad.setId(1L);
                    return entidad;
                });
    }

    private ConsumoRequest requestConConsumo(BigDecimal consumoKwh, Integer horasAltoConsumo) {
        return new ConsumoRequest(
                TipoInmueble.Casa,
                consumoKwh,
                true,
                5,
                FrecuenciaUso.Media,
                horasAltoConsumo
        );
    }

    @Test
    void consumoAlto_clasificaComoIneficiente() {
        AnalisisResponse response = service.analizarConsumo(requestConConsumo(new BigDecimal("450"), 3));

        assertThat(response.categoria()).isEqualTo(CategoriaEnergetica.Ineficiente);
        assertThat(response.recomendaciones()).isNotEmpty();
        assertThat(response.consumoKwh()).isEqualByComparingTo(new BigDecimal("450"));
    }

    @Test
    void horasAltoConsumoSuperaOcho_clasificaComoIneficienteAunqueConsumoSeaBajo() {
        AnalisisResponse response = service.analizarConsumo(requestConConsumo(new BigDecimal("100"), 9));

        assertThat(response.categoria()).isEqualTo(CategoriaEnergetica.Ineficiente);
    }

    @Test
    void consumoMedio_clasificaComoModerado() {
        AnalisisResponse response = service.analizarConsumo(requestConConsumo(new BigDecimal("300"), 4));

        assertThat(response.categoria()).isEqualTo(CategoriaEnergetica.Moderado);
    }

    @Test
    void consumoBajo_clasificaComoEficiente() {
        AnalisisResponse response = service.analizarConsumo(requestConConsumo(new BigDecimal("120"), 2));

        assertThat(response.categoria()).isEqualTo(CategoriaEnergetica.Eficiente);
    }

    @Test
    void costoEstimado_seCalculaATarifaReferencia() {
        AnalisisResponse response = service.analizarConsumo(requestConConsumo(new BigDecimal("200"), 2));

        BigDecimal costoEsperado = new BigDecimal("200")
                .multiply(AnalisisEnergeticoService.TARIFA_REFERENCIA)
                .setScale(2, RoundingMode.HALF_UP);
        assertThat(response.costoEstimado()).isEqualByComparingTo(costoEsperado);
    }

    @Test
    void analizarConsumo_persisteLaEntidadConLosDatosDelRequest() {
        ConsumoRequest request = requestConConsumo(new BigDecimal("150"), 1);

        AnalisisResponse response = service.analizarConsumo(request);

        ArgumentCaptor<RegistroConsumo> captor = ArgumentCaptor.forClass(RegistroConsumo.class);
        verify(repository).save(captor.capture());

        RegistroConsumo guardado = captor.getValue();
        assertThat(guardado.getTipoInmueble()).isEqualTo(request.tipoInmueble());
        assertThat(guardado.getConsumoKwh()).isEqualByComparingTo(request.consumoKwh());
        assertThat(guardado.getUsoHorarioPico()).isEqualTo(request.usoHorarioPico());
        assertThat(guardado.getCantidadEquipos()).isEqualTo(request.cantidadEquipos());
        assertThat(guardado.getFrecuenciaUso()).isEqualTo(request.frecuenciaUso());
        assertThat(guardado.getHorasAltoConsumo()).isEqualTo(request.horasAltoConsumo());
        assertThat(guardado.getCostoEstimado()).isNotNull();
        assertThat(response.idRegistro()).isEqualTo(1L);
    }

    @Test
    void frecuenciaUsoNula_seGuardaComoMediaPorDefecto() {
        ConsumoRequest request = new ConsumoRequest(
                TipoInmueble.Local, new BigDecimal("80"), false, 2, null, 1);

        service.analizarConsumo(request);

        ArgumentCaptor<RegistroConsumo> captor = ArgumentCaptor.forClass(RegistroConsumo.class);
        verify(repository).save(captor.capture());

        assertThat(captor.getValue().getFrecuenciaUso()).isEqualTo(FrecuenciaUso.Media);
    }
}
