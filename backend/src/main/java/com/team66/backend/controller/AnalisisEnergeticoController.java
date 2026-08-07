package com.team66.backend.controller;

import com.team66.backend.dto.AnalisisResponse;
import com.team66.backend.dto.ConsumoRequest;
import com.team66.backend.service.AnalisisEnergeticoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analisis-energetico")
public class AnalisisEnergeticoController {
    
    private final AnalisisEnergeticoService service;

    public AnalisisEnergeticoController(AnalisisEnergeticoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<AnalisisResponse> analizar(@Valid @RequestBody ConsumoRequest request) {
        AnalisisResponse response = service.analizarConsumo(request);
        return ResponseEntity.ok(response);
    }
}
