package com.examplepdv.controller;

import com.examplepdv.dto.FechamentoCaixaDTO;
import com.examplepdv.dto.MovimentoCaixaDTO;
import com.examplepdv.model.Caixa;
import com.examplepdv.model.MovimentoCaixa;
import com.examplepdv.model.TipoMovimentoCaixa;
import com.examplepdv.model.Usuario;
import com.examplepdv.service.CaixaService;
import com.examplepdv.service.UsuarioService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/caixa")
public class CaixaController {

    private final CaixaService caixaService;
    private final UsuarioService usuarioService;

    public CaixaController(CaixaService caixaService, UsuarioService usuarioService) {
        this.caixaService = caixaService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String status(Model model) {
        Optional<Caixa> caixaOpt = caixaService.obterCaixaAberto();
        if (caixaOpt.isPresent()) {
            Caixa caixa = caixaOpt.get();
            List<MovimentoCaixa> movimentos = caixaService.listarMovimentos(caixa);
            model.addAttribute("caixa", caixa);
            model.addAttribute("movimentos", movimentos);
        } else {
            model.addAttribute("caixa", null);
        }
        return "caixa/status";
    }

    @GetMapping("/abertura")
    public String formAbertura(Model model, RedirectAttributes redirectAttributes) {
        if (caixaService.obterCaixaAberto().isPresent()) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Já existe um caixa aberto!");
            return "redirect:/caixa";
        }
        model.addAttribute("saldoInicial", BigDecimal.ZERO);
        return "caixa/abertura";
    }

    @PostMapping("/abertura")
    public String abrirCaixa(@RequestParam("saldoInicial") BigDecimal saldoInicial,
                             @RequestParam(value = "observacao", required = false) String observacao,
                             @AuthenticationPrincipal UserDetails userDetails,
                             RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = usuarioService.buscarPorUsername(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

            caixaService.abrirCaixa(usuario, saldoInicial, observacao);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Caixa aberto com sucesso!");
            return "redirect:/pdv";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", e.getMessage());
            return "redirect:/caixa/abertura";
        }
    }

    @GetMapping("/fechamento")
    public String formFechamento(Model model, RedirectAttributes redirectAttributes) {
        Optional<Caixa> caixaOpt = caixaService.obterCaixaAberto();
        if (caixaOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Nenhum caixa aberto para fechar!");
            return "redirect:/caixa";
        }
        model.addAttribute("caixa", caixaOpt.get());
        model.addAttribute("dto", new FechamentoCaixaDTO());
        return "caixa/fechamento";
    }

    @PostMapping("/fechamento")
    public String fecharCaixa(@ModelAttribute("dto") FechamentoCaixaDTO dto,
                              RedirectAttributes redirectAttributes) {
        try {
            caixaService.fecharCaixa(dto);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Caixa fechado com sucesso!");
            return "redirect:/caixa";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", e.getMessage());
            return "redirect:/caixa/fechamento";
        }
    }

    @GetMapping("/movimento")
    public String formMovimento(Model model, RedirectAttributes redirectAttributes) {
        if (caixaService.obterCaixaAberto().isEmpty()) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Nenhum caixa aberto para registrar movimentação!");
            return "redirect:/caixa";
        }
        model.addAttribute("dto", new MovimentoCaixaDTO());
        model.addAttribute("tipos", List.of(TipoMovimentoCaixa.SUPRIMENTO, TipoMovimentoCaixa.SANGRIA));
        return "caixa/movimento";
    }

    @PostMapping("/movimento")
    public String registrarMovimento(@ModelAttribute("dto") MovimentoCaixaDTO dto,
                                     RedirectAttributes redirectAttributes) {
        try {
            caixaService.registrarMovimento(dto);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Movimentação registrada com sucesso!");
            return "redirect:/caixa";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", e.getMessage());
            return "redirect:/caixa/movimento";
        }
    }
}
