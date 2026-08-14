package com.team66.backend.service;

import com.team66.backend.dto.AnalisisResponse;
import com.team66.backend.dto.CategoriaEnergetica;
import com.team66.backend.dto.ConsumoRequest;
import com.team66.backend.dto.FrecuenciaUso;
import com.team66.backend.model.RegistroConsumo;
import com.team66.backend.repository.RegistroConsumoReposotory;
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

    public static final BigDecimal TARIFA_REFERENCIA = new BigDecimal("0.75");
    private final RegistroConsumoReposotory repository;

    public AnalisisEnergeticoService(RegistroConsumoReposotory repository) {
        this.repository = repository;
    }

    public AnalisisResponse analizarConsumo(ConsumoRequest request) {
        // Estimación $0.75 por kWh
        BigDecimal costoEstimado = request.consumoKwh()
                .multiply(TARIFA_REFERENCIA)
                .setScale(2, RoundingMode.HALF_UP);

        // Clasificación preliminar por reglas simples
        // Actualizar con Data Science
        CategoriaEnergetica categoria;
        double probabilidad;
        List<String> recomendaciones = new ArrayList<>();

        if (request.consumoKwh().compareTo(new BigDecimal("400")) > 0 || request.horasAltoConsumo() > 8) {
            categoria = CategoriaEnergetica.Ineficiente;
            probabilidad = 0.81;
            recomendaciones.add("Reducir el uso de equipos durante los horarios pico");
            recomendaciones.add("Evaluar equipos antiguos con alto consumo energetico");
            recomendaciones.add("Distribuir las actividades de mayor consumo a lo largo del dia");
        } else if (request.consumoKwh().compareTo(new BigDecimal("250")) > 0) {
            categoria = CategoriaEnergetica.Moderado;
            probabilidad = 0.75;
            recomendaciones.add("Desconectar aparatos en modo de espera (consumo vampiro)");
            recomendaciones.add("Optimizar las horas de uso de iluminacion y electrodomesticos");
        } else {
            categoria = CategoriaEnergetica.Eficiente;
            probabilidad = 0.90;
            recomendaciones.add("Mantener los habitos actuales de consumo consciente");
        }

        // Persistir en la base de datos
        RegistroConsumo entidad = new RegistroConsumo();
        entidad.setFechaRegistro(LocalDate.now());
        entidad.setTipoInmueble(request.tipoInmueble());
        entidad.setConsumoKwh(request.consumoKwh());
        entidad.setUsoHorarioPico(request.usoHorarioPico());
        entidad.setCantidadEquipos(request.cantidadEquipos());
        entidad.setFrecuenciaUso(request.frecuenciaUso() != null ? request.frecuenciaUso() : FrecuenciaUso.Fijo);
        entidad.setHorasAltoConsumo(request.horasAltoConsumo());
        entidad.setCategoria(categoria);
        repository.save(entidad);

        // Retornar DTO de respuesta
        return new AnalisisResponse(
                categoria,
                probabilidad,
                recomendaciones,
                costoEstimado);
    }

}
