package com.team66.backend.service;

import com.team66.backend.client.MlServiceClient;
import com.team66.backend.dto.AnalisisResponse;
import com.team66.backend.dto.CategoriaEnergetica;
import com.team66.backend.dto.ConsumoRequest;
import com.team66.backend.dto.FrecuenciaUso;
import com.team66.backend.dto.MlPrediccionRequest;
import com.team66.backend.dto.MlPrediccionResponse;
import com.team66.backend.model.RegistroConsumo;
import com.team66.backend.repository.RegistroConsumoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Me falta documentar. Héctor.
 */
@Service
public class AnalisisEnergeticoService {

    private final RegistroConsumoRepository repository;
    private final MlServiceClient mlServiceClient;

    public AnalisisEnergeticoService(RegistroConsumoRepository repository, MlServiceClient mlServiceClient) {
        this.repository = repository;
        this.mlServiceClient = mlServiceClient;
    }

    public AnalisisResponse analizarConsumo(ConsumoRequest request) {

        FrecuenciaUso frecuenciaUso = request.frecuenciaUso() != null
                ? request.frecuenciaUso()
                : FrecuenciaUso.Media;

        MlPrediccionResponse prediccion = mlServiceClient.predecir(new MlPrediccionRequest(
                request.pais(),
                request.localidad(),
                request.tipoInmueble().name(),
                request.numeroHabitantes(),
                request.cantidadEquipos(),
                frecuenciaUso.name(),
                request.horasAltoConsumo(),
                request.usoHorarioPico(),
                request.temperaturaAmbiente(),
                request.consumoKwh()));

        MlPrediccionResponse.EstimacionFinanciera finanzas = prediccion.estimacionFinanciera();

        RegistroConsumo entidad = new RegistroConsumo();
        entidad.setFechaRegistro(LocalDate.now());
        entidad.setPais(request.pais());
        entidad.setLocalidad(request.localidad());
        entidad.setTemperaturaAmbiente(request.temperaturaAmbiente().setScale(0, RoundingMode.HALF_UP).intValue());
        entidad.setTipoInmueble(request.tipoInmueble());
        entidad.setNumeroHabitantes(request.numeroHabitantes());
        entidad.setConsumoKwh(request.consumoKwh());
        entidad.setUsoHorarioPico(request.usoHorarioPico());
        entidad.setCantidadEquipos(request.cantidadEquipos());
        entidad.setFrecuenciaUso(frecuenciaUso);
        entidad.setHorasAltoConsumo(request.horasAltoConsumo());
        entidad.setCategoria(prediccion.categoria());
        entidad.setCostoEstimado(finanzas.costoActualMensual());
        RegistroConsumo guardado = repository.save(entidad);

        // Retornar DTO de respuesta
        return new AnalisisResponse(
                guardado.getId(),
                prediccion.categoria(),
                prediccion.probabilidad(),
                request.consumoKwh(),
                finanzas.costoActualMensual(),
                finanzas.ahorroMonetarioMensual(),
                finanzas.porcentajeReduccionEstimado(),
                prediccion.recomendaciones());
    }

}
