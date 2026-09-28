package com.examplepdv.controller;

import com.examplepdv.model.EmpresaUtilizadora;
import com.examplepdv.service.EmpresaUtilizadoraService;
import com.examplepdv.service.VendaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/vendas")
public class VendaController {

    private final VendaService vendaService;
    private final EmpresaUtilizadoraService empresaService;

    public VendaController(VendaService vendaService, EmpresaUtilizadoraService empresaService) {
        this.vendaService = vendaService;
        this.empresaService = empresaService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("vendas", vendaService.listarTodas());
        return "vendas/list";
    }

    @GetMapping("/{id}")
    public String detalhe(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        EmpresaUtilizadora empresa = empresaService.obterEmpresa();
        return vendaService.buscarPorId(id)
                .map(venda -> {
                    model.addAttribute("venda", venda);
                    model.addAttribute("empresa", empresa);
                    return "vendas/detalhe";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("mensagemErro", "Venda não encontrada!");
                    return "redirect:/vendas";
                });
    }
}
