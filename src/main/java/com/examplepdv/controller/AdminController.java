package com.examplepdv.controller;

import com.examplepdv.model.AmbienteSefaz;
import com.examplepdv.model.EmpresaUtilizadora;
import com.examplepdv.model.TipoCertificado;
import com.examplepdv.service.DatabaseResetService;
import com.examplepdv.service.EmpresaUtilizadoraService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);
    private final DatabaseResetService databaseResetService;
    private final EmpresaUtilizadoraService empresaService;

    public AdminController(DatabaseResetService databaseResetService, EmpresaUtilizadoraService empresaService) {
        this.databaseResetService = databaseResetService;
        this.empresaService = empresaService;
    }

    // Configuração da Empresa Utilizadora / Dados Fiscais (NFC-e, Certificado, CSC)
    @GetMapping("/empresa")
    public String paginaEmpresa(Model model) {
        model.addAttribute("empresa", empresaService.obterEmpresa());
        model.addAttribute("tiposCertificado", TipoCertificado.values());
        model.addAttribute("ambientesSefaz", AmbienteSefaz.values());
        return "admin/empresa";
    }

    @PostMapping("/empresa")
    public String salvarEmpresa(@ModelAttribute("empresa") EmpresaUtilizadora dados, RedirectAttributes redirectAttributes) {
        try {
            empresaService.salvarEmpresa(dados);
            redirectAttributes.addFlashAttribute("mensagemSucesso", 
                    "Configurações da empresa e parâmetros fiscais salvos com sucesso! As alterações já estão ativas para todos os operadores e cupons fiscais.");
        } catch (Exception e) {
            log.error("Erro ao salvar configuração da empresa: ", e);
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao salvar configurações: " + e.getMessage());
        }
        return "redirect:/admin/empresa";
    }

    // Menu de Ajuda: Manual de Utilização do PDV
    @GetMapping("/ajuda")
    public String paginaAjuda(Model model) {
        model.addAttribute("empresa", empresaService.obterEmpresa());
        return "admin/ajuda";
    }

    // Menu de Readme / Guia Whitelabel & Migração de Banco de Dados
    @GetMapping("/readme")
    public String paginaReadme(Model model) {
        model.addAttribute("empresa", empresaService.obterEmpresa());
        return "admin/readme";
    }

    // Reset da Aplicação
    @GetMapping("/reset")
    public String paginaReset() {
        return "admin/reset";
    }

    @PostMapping("/reset-app")
    public String resetApp(RedirectAttributes redirectAttributes) {
        try {
            databaseResetService.resetDatabase();
            redirectAttributes.addFlashAttribute("mensagemSucesso", 
                    "Aplicação resetada com sucesso! O banco de dados retornou ao estado original com dados padrão de fábrica.");
        } catch (Exception e) {
            log.error("Erro ao resetar aplicativo: ", e);
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao executar o reset: " + e.getMessage());
        }
        return "redirect:/pdv";
    }
}
