package com.examplepdv.controller;

import com.examplepdv.dto.RelatorioGestaoDTO;
import com.examplepdv.dto.TempoRealCaixaDTO;
import com.examplepdv.model.EmpresaUtilizadora;
import com.examplepdv.model.Usuario;
import com.examplepdv.service.EmpresaUtilizadoraService;
import com.examplepdv.service.GestaoService;
import com.examplepdv.service.UsuarioService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/gestao")
@PreAuthorize("hasAnyRole('GESTOR', 'ADMIN')")
public class GestaoController {

    private final GestaoService gestaoService;
    private final EmpresaUtilizadoraService empresaService;
    private final UsuarioService usuarioService;

    public GestaoController(GestaoService gestaoService, EmpresaUtilizadoraService empresaService, UsuarioService usuarioService) {
        this.gestaoService = gestaoService;
        this.empresaService = empresaService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String dashboard(
            @RequestParam(required = false, defaultValue = "HOJE") String periodo,
            @RequestParam(required = false, defaultValue = "CONSOLIDADO") String tipo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {

        String nomeGestor = "Gestor";
        if (userDetails != null) {
            Usuario u = usuarioService.buscarPorUsername(userDetails.getUsername()).orElse(null);
            if (u != null) nomeGestor = u.getNome();
        }

        TempoRealCaixaDTO tempoReal = gestaoService.obterTempoRealCaixa();
        RelatorioGestaoDTO relatorio = gestaoService.gerarRelatorio(periodo, tipo, dataInicio, dataFim, nomeGestor);

        model.addAttribute("tempoReal", tempoReal);
        model.addAttribute("relatorio", relatorio);
        model.addAttribute("periodo", periodo.toUpperCase());
        model.addAttribute("tipo", tipo.toUpperCase());
        model.addAttribute("dataInicio", relatorio.getDataInicio());
        model.addAttribute("dataFim", relatorio.getDataFim());
        model.addAttribute("empresa", empresaService.obterEmpresa());

        return "gestao/dashboard";
    }

    @GetMapping("/imprimir")
    public String imprimirRelatorio(
            @RequestParam(required = false, defaultValue = "HOJE") String periodo,
            @RequestParam(required = false, defaultValue = "CONSOLIDADO") String tipo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {

        String nomeGestor = "Gestor";
        if (userDetails != null) {
            Usuario u = usuarioService.buscarPorUsername(userDetails.getUsername()).orElse(null);
            if (u != null) nomeGestor = u.getNome();
        }

        RelatorioGestaoDTO relatorio = gestaoService.gerarRelatorio(periodo, tipo, dataInicio, dataFim, nomeGestor);
        model.addAttribute("relatorio", relatorio);
        model.addAttribute("empresa", empresaService.obterEmpresa());

        return "gestao/imprimir";
    }

    // Tela para o Gestor alterar Ícones e Logotipos da empresa
    @GetMapping("/identidade-visual")
    public String paginaIdentidadeVisual(Model model) {
        model.addAttribute("empresa", empresaService.obterEmpresa());
        return "gestao/identidade-visual";
    }

    @PostMapping("/identidade-visual")
    public String salvarIdentidadeVisual(
            @RequestParam String nomeFantasia,
            @RequestParam String iconeBootstrap,
            @RequestParam(required = false, defaultValue = "") String logoUrl,
            RedirectAttributes redirectAttributes) {
        try {
            empresaService.atualizarIdentidadeVisual(nomeFantasia, iconeBootstrap, logoUrl);
            redirectAttributes.addFlashAttribute("mensagemSucesso", 
                    "Identidade visual (Ícone e Logotipo) atualizada com sucesso! A alteração foi espelhada em todo o sistema.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao atualizar identidade visual: " + e.getMessage());
        }
        return "redirect:/gestao";
    }
}
