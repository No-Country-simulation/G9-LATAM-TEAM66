package com.team66.backend.service;

import com.team66.backend.dto.AnalisisResponse;
import com.team66.backend.dto.CategoriaEnergetica;
import com.team66.backend.dto.ConsumoRequest;
import com.team66.backend.dto.FrecuenciaUso;
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

    public static final BigDecimal TARIFA_REFERENCIA = new BigDecimal("0.75");
    private final RegistroConsumoRepository repository;

    public AnalisisEnergeticoService(RegistroConsumoRepository repository) {
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
        String diagnostico;
        List<String> recomendaciones = new ArrayList<>();

        if (request.consumoKwh().compareTo(new BigDecimal("400")) > 0 || request.horasAltoConsumo() > 8) {
            categoria = CategoriaEnergetica.Ineficiente;
            diagnostico = "Tu consumo energético es alto comparado con el promedio esperado.";
            recomendaciones.add("Reducir el uso de equipos durante los horarios pico");
            recomendaciones.add("Evaluar equipos antiguos con alto consumo energetico");
            recomendaciones.add("Distribuir las actividades de mayor consumo a lo largo del dia");
        } else if (request.consumoKwh().compareTo(new BigDecimal("250")) > 0) {
            categoria = CategoriaEnergetica.Moderado;
            diagnostico = "Tu consumo energético está dentro de un rango moderado, con oportunidades de mejora.";
            recomendaciones.add("Desconectar aparatos en modo de espera (consumo vampiro)");
            recomendaciones.add("Optimizar las horas de uso de iluminacion y electrodomesticos");
        } else {
            categoria = CategoriaEnergetica.Eficiente;
            diagnostico = "Tu consumo energético es eficiente comparado con el promedio esperado.";
            recomendaciones.add("Mantener los habitos actuales de consumo consciente");
        }

        // Persistir en la base de datos
        RegistroConsumo entidad = new RegistroConsumo();
        entidad.setFechaRegistro(LocalDate.now());
        entidad.setTipoInmueble(request.tipoInmueble());
        entidad.setConsumoKwh(request.consumoKwh());
        entidad.setUsoHorarioPico(request.usoHorarioPico());
        entidad.setCantidadEquipos(request.cantidadEquipos());
        entidad.setFrecuenciaUso(request.frecuenciaUso() != null ? request.frecuenciaUso() : FrecuenciaUso.Media);
        entidad.setHorasAltoConsumo(request.horasAltoConsumo());
        entidad.setCategoria(categoria);
        entidad.setCostoEstimado(costoEstimado);
        RegistroConsumo guardado = repository.save(entidad);

        // Retornar DTO de respuesta
        return new AnalisisResponse(
                guardado.getId(),
                categoria,
                request.consumoKwh(),
                costoEstimado,
                diagnostico,
                recomendaciones);
    }

}
