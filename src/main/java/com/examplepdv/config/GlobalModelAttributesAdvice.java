package com.examplepdv.config;

import com.examplepdv.model.EmpresaUtilizadora;
import com.examplepdv.service.EmpresaUtilizadoraService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributesAdvice {

    private final EmpresaUtilizadoraService empresaService;

    public GlobalModelAttributesAdvice(EmpresaUtilizadoraService empresaService) {
        this.empresaService = empresaService;
    }

    @ModelAttribute("empresa")
    public EmpresaUtilizadora populateEmpresa() {
        return empresaService.obterEmpresa();
    }
}
