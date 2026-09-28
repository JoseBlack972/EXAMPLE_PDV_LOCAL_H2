package com.examplepdv.controller;

import com.examplepdv.dto.TempoRealCaixaDTO;
import com.examplepdv.service.GestaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/gestao")
@PreAuthorize("hasAnyRole('GESTOR', 'ADMIN')")
public class GestaoApiController {

    private final GestaoService gestaoService;

    public GestaoApiController(GestaoService gestaoService) {
        this.gestaoService = gestaoService;
    }

    @GetMapping("/tempo-real")
    public ResponseEntity<TempoRealCaixaDTO> obterTempoReal() {
        return ResponseEntity.ok(gestaoService.obterTempoRealCaixa());
    }
}
