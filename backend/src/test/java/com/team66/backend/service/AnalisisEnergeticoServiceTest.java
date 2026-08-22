package com.team66.backend.service;

import com.team66.backend.client.MlServiceClient;
import com.team66.backend.dto.AnalisisResponse;
import com.team66.backend.dto.CategoriaEnergetica;
import com.team66.backend.dto.ConsumoRequest;
import com.team66.backend.dto.FrecuenciaUso;
import com.team66.backend.dto.MlPrediccionRequest;
import com.team66.backend.dto.MlPrediccionResponse;
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
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AnalisisEnergeticoServiceTest {

    @Mock
    private RegistroConsumoRepository repository;

    @Mock
    private MlServiceClient mlServiceClient;

    private AnalisisEnergeticoService service;

    @BeforeEach
    void setUp() {
        service = new AnalisisEnergeticoService(repository, mlServiceClient);

        lenient().when(repository.save(any(RegistroConsumo.class)))
                .thenAnswer(invocation -> {
                    RegistroConsumo entidad = invocation.getArgument(0);
                    entidad.setId(1L);
                    return entidad;
                });

        lenient().when(mlServiceClient.predecir(any(MlPrediccionRequest.class)))
                .thenReturn(prediccionSimulada(CategoriaEnergetica.Moderado, 0.87));
    }

    private MlPrediccionResponse prediccionSimulada(CategoriaEnergetica categoria, double probabilidad) {
        return new MlPrediccionResponse(
                categoria,
                probabilidad,
                new MlPrediccionResponse.EstimacionFinanciera(
                        new BigDecimal("240.38"),
                        new BigDecimal("50.00"),
                        new BigDecimal("20.5")),
                new MlPrediccionResponse.Comparacion(
                        new BigDecimal("80.13"),
                        new BigDecimal("76.82"),
                        new BigDecimal("4.3"),
                        true),
                new MlPrediccionResponse.ContextoDataset(
                        new BigDecimal("33.0"),
                        new BigDecimal("34.18"),
                        new BigDecimal("33.5"),
                        new BigDecimal("32.0"),
                        Map.of("Eficiente", new BigDecimal("31.87"),
                               "Moderado", new BigDecimal("34.18"),
                               "Ineficiente", new BigDecimal("33.96"))),
                List.of("Recomendacion de prueba"));
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

    @Test
    void analizarConsumo_usaLaCategoriaQueDevuelveElModelo() {
        AnalisisResponse response = service.analizarConsumo(requestValido());

        assertThat(response.categoria()).isEqualTo(CategoriaEnergetica.Moderado);
        assertThat(response.probabilidad()).isEqualTo(0.87);
        assertThat(response.recomendaciones()).containsExactly("Recomendacion de prueba");
    }

    @Test
    void analizarConsumo_incluyeLaComparacionContraElPromedioDeSuCategoria() {
        AnalisisResponse response = service.analizarConsumo(requestValido());

        assertThat(response.comparacion()).isNotNull();
        assertThat(response.comparacion().consumoPorHabitante()).isEqualByComparingTo("80.13");
        assertThat(response.comparacion().promedioTipoCategoria()).isEqualByComparingTo("76.82");
        assertThat(response.comparacion().diferenciaPorcentual()).isEqualByComparingTo("4.3");
        assertThat(response.comparacion().porEncimaDelPromedio()).isTrue();
    }

    @Test
    void analizarConsumo_enviaAlModeloLosDatosDelRequest() {
        ConsumoRequest request = requestValido();

        service.analizarConsumo(request);

        ArgumentCaptor<MlPrediccionRequest> captor = ArgumentCaptor.forClass(MlPrediccionRequest.class);
        verify(mlServiceClient).predecir(captor.capture());

        MlPrediccionRequest enviado = captor.getValue();
        assertThat(enviado.pais()).isEqualTo(request.pais());
        assertThat(enviado.localidad()).isEqualTo(request.localidad());
        assertThat(enviado.tipoInmueble()).isEqualTo(request.tipoInmueble().name());
        assertThat(enviado.numeroHabitantes()).isEqualTo(request.numeroHabitantes());
        assertThat(enviado.frecuenciaUso()).isEqualTo(request.frecuenciaUso().name());
        assertThat(enviado.consumoKwh()).isEqualByComparingTo(request.consumoKwh());
    }

    @Test
    void analizarConsumo_persisteLaCategoriaYElCostoDelModelo() {
        ConsumoRequest request = requestValido();

        AnalisisResponse response = service.analizarConsumo(request);

        ArgumentCaptor<RegistroConsumo> captor = ArgumentCaptor.forClass(RegistroConsumo.class);
        verify(repository).save(captor.capture());

        RegistroConsumo guardado = captor.getValue();
        assertThat(guardado.getCategoria()).isEqualTo(CategoriaEnergetica.Moderado);
        assertThat(guardado.getCostoEstimado()).isEqualByComparingTo("240.38");
        assertThat(guardado.getPais()).isEqualTo(request.pais());
        assertThat(guardado.getNumeroHabitantes()).isEqualTo(request.numeroHabitantes());
        assertThat(guardado.getConsumoKwh()).isEqualByComparingTo(request.consumoKwh());
        assertThat(response.idRegistro()).isEqualTo(1L);
    }

    @Test
    void temperaturaDecimal_seRedondeaAlEnteroQueEsperaLaBaseDeDatos() {
        ConsumoRequest request = new ConsumoRequest(
                "Perú", "Lima", new BigDecimal("22.6"), TipoInmueble.Departamento, 2,
                new BigDecimal("150.00"), false, 3, FrecuenciaUso.Baja, 2);

        service.analizarConsumo(request);

        ArgumentCaptor<RegistroConsumo> captor = ArgumentCaptor.forClass(RegistroConsumo.class);
        verify(repository).save(captor.capture());

        assertThat(captor.getValue().getTemperaturaAmbiente()).isEqualTo(23);
    }

    @Test
    void frecuenciaUsoNula_seEnviaComoMediaPorDefecto() {
        ConsumoRequest request = new ConsumoRequest(
                "México", "Mérida", new BigDecimal("28.0"), TipoInmueble.Local, 5,
                new BigDecimal("80.00"), false, 2, null, 1);

        service.analizarConsumo(request);

        ArgumentCaptor<MlPrediccionRequest> captor = ArgumentCaptor.forClass(MlPrediccionRequest.class);
        verify(mlServiceClient).predecir(captor.capture());

        assertThat(captor.getValue().frecuenciaUso()).isEqualTo(FrecuenciaUso.Media.name());
    }
}
