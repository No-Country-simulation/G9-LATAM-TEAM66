package com.team66.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
    LocalDateTime timestamp,
    Integer status,
    String error,
    String mensaje,
    @JsonProperty("errores_validacion") Map<String, String> erroresValidacion) {

  // Constructor de conveniencia para errores simples sin lista de campos
  public ErrorResponse(Integer status, String error, String mensaje) {
    this(LocalDateTime.now(), status, error, mensaje, null);
  }
}
