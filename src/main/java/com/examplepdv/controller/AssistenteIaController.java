package com.examplepdv.controller;

import com.examplepdv.service.AssistenteIaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ia")
@PreAuthorize("authenticated()")
public class AssistenteIaController {

    private final AssistenteIaService assistenteIaService;

    public AssistenteIaController(AssistenteIaService assistenteIaService) {
        this.assistenteIaService = assistenteIaService;
    }

    @PostMapping("/assistente")
    public ResponseEntity<AssistenteIaService.RespostaIaDTO> consultarAssistente(@RequestBody Map<String, String> payload) {
        String mensagem = payload != null ? payload.get("mensagem") : "";
        return ResponseEntity.ok(assistenteIaService.processarConsulta(mensagem));
    }
}
