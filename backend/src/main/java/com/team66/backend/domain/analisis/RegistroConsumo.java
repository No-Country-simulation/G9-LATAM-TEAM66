package com.team66.backend.domain.analisis;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Entity;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "registro_consumo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroConsumo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDate fechaRegistro;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_inmueble", nullable = false)
    private TipoInmueble tipoInmueble;

    @Column(name = "consumo_kwh", nullable = false, precision = 8, scale = 2)
    private BigDecimal consumoKwh;

    @Column(name = "uso_horario_pico", nullable = false)
    private Boolean usoHorarioPico;

    @Column(name = "cantidad_equipos", nullable = false)
    private Integer cantidadEquipos;

    @Enumerated(EnumType.STRING)
    @Column(name = "frecuencia_uso", nullable = false)
    private FrecuenciaUso frecuenciaUso;

    @Column(name = "horas_alto_consumo", nullable = false)
    private Integer horasAltoConsumo;

    @Enumerated(EnumType.STRING)
    @Column(name = "categoria", nullable = false)
    private CategoriaEnergetica categoria;
}