package com.team66.backend.model;

import com.team66.backend.dto.CategoriaEnergetica;
import com.team66.backend.dto.FrecuenciaUso;
import com.team66.backend.dto.TipoInmueble;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "registro_consumo")
public class RegistroConsumo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDate fechaRegistro;

    @Column(name = "pais", length = 25)
    private String pais;

    @Column(name = "localidad", length = 30)
    private String localidad;

    @Column(name = "temperatura_ambiente")
    private Integer temperaturaAmbiente;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_inmueble", nullable = false)
    private TipoInmueble tipoInmueble;

    @Column(name = "numero_habitantes")
    private Integer numeroHabitantes;

    @Column(name = "cantidad_equipos", nullable = false)
    private Integer cantidadEquipos;

    @Enumerated(EnumType.STRING)
    @Column(name = "frecuencia_uso", nullable = false)
    private FrecuenciaUso frecuenciaUso;

    @Column(name = "horas_alto_consumo", nullable = false)
    private Integer horasAltoConsumo;

    @Column(name = "uso_horario_pico", nullable = false)
    private Boolean usoHorarioPico;

    @Column(name = "consumo_kwh", nullable = false, precision = 8, scale = 2)
    private BigDecimal consumoKwh;

    @Enumerated(EnumType.STRING)
    @Column(name = "categoria", nullable = false)
    private CategoriaEnergetica categoria;

    @Column(name = "costo_estimado", precision = 12, scale = 2)
    private BigDecimal costoEstimado;

    public RegistroConsumo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public Integer getTemperaturaAmbiente() {
        return temperaturaAmbiente;
    }

    public void setTemperaturaAmbiente(Integer temperaturaAmbiente) {
        this.temperaturaAmbiente = temperaturaAmbiente;
    }

    public TipoInmueble getTipoInmueble() {
        return tipoInmueble;
    }

    public void setTipoInmueble(TipoInmueble tipoInmueble) {
        this.tipoInmueble = tipoInmueble;
    }

    public Integer getNumeroHabitantes() {
        return numeroHabitantes;
    }

    public void setNumeroHabitantes(Integer numeroHabitantes) {
        this.numeroHabitantes = numeroHabitantes;
    }

    public Integer getCantidadEquipos() {
        return cantidadEquipos;
    }

    public void setCantidadEquipos(Integer cantidadEquipos) {
        this.cantidadEquipos = cantidadEquipos;
    }

    public FrecuenciaUso getFrecuenciaUso() {
        return frecuenciaUso;
    }

    public void setFrecuenciaUso(FrecuenciaUso frecuenciaUso) {
        this.frecuenciaUso = frecuenciaUso;
    }

    public Integer getHorasAltoConsumo() {
        return horasAltoConsumo;
    }

    public void setHorasAltoConsumo(Integer horasAltoConsumo) {
        this.horasAltoConsumo = horasAltoConsumo;
    }

    public Boolean getUsoHorarioPico() {
        return usoHorarioPico;
    }

    public void setUsoHorarioPico(Boolean usoHorarioPico) {
        this.usoHorarioPico = usoHorarioPico;
    }

    public BigDecimal getConsumoKwh() {
        return consumoKwh;
    }

    public void setConsumoKwh(BigDecimal consumoKwh) {
        this.consumoKwh = consumoKwh;
    }

    public CategoriaEnergetica getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaEnergetica categoria) {
        this.categoria = categoria;
    }

    public BigDecimal getCostoEstimado() {
        return costoEstimado;
    }

    public void setCostoEstimado(BigDecimal costoEstimado) {
        this.costoEstimado = costoEstimado;
    }
}
