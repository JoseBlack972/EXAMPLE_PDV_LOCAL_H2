package com.examplepdv.controller;

import com.examplepdv.dto.VendaDTO;
import com.examplepdv.model.Caixa;
import com.examplepdv.model.EmpresaUtilizadora;
import com.examplepdv.model.Produto;
import com.examplepdv.model.Usuario;
import com.examplepdv.model.Venda;
import com.examplepdv.service.CaixaService;
import com.examplepdv.service.EmpresaUtilizadoraService;
import com.examplepdv.service.ProdutoService;
import com.examplepdv.service.UsuarioService;
import com.examplepdv.service.VendaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class PdvController {

    private final ProdutoService produtoService;
    private final CaixaService caixaService;
    private final VendaService vendaService;
    private final UsuarioService usuarioService;
    private final EmpresaUtilizadoraService empresaService;

    public PdvController(ProdutoService produtoService,
                         CaixaService caixaService,
                         VendaService vendaService,
                         UsuarioService usuarioService,
                         EmpresaUtilizadoraService empresaService) {
        this.produtoService = produtoService;
        this.caixaService = caixaService;
        this.vendaService = vendaService;
        this.usuarioService = usuarioService;
        this.empresaService = empresaService;
    }

    @GetMapping({"/", "/pdv"})
    public String pdv(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<Caixa> caixaOpt = caixaService.obterCaixaAberto();
        List<Produto> produtos = produtoService.listarAtivos();
        EmpresaUtilizadora empresa = empresaService.obterEmpresa();

        Usuario usuario = null;
        if (userDetails != null) {
            usuario = usuarioService.buscarPorUsername(userDetails.getUsername()).orElse(null);
        }

        model.addAttribute("caixaAberto", caixaOpt.orElse(null));
        model.addAttribute("usuarioLogado", usuario);
        model.addAttribute("produtos", produtos);
        model.addAttribute("empresa", empresa);

        return "pdv";
    }

    @PostMapping("/api/pdv/finalizar")
    @ResponseBody
    public ResponseEntity<?> finalizarVenda(@RequestBody VendaDTO dto,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (userDetails == null) {
                return ResponseEntity.status(401).body(Map.of("erro", "Usuário não autenticado"));
            }

            Usuario usuario = usuarioService.buscarPorUsername(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

            Venda venda = vendaService.realizarVenda(dto, usuario);
            EmpresaUtilizadora empresa = empresaService.obterEmpresa();

            Map<String, Object> resp = new HashMap<>();
            resp.put("sucesso", true);
            resp.put("vendaId", venda.getId());
            resp.put("total", venda.getValorTotal());
            resp.put("formaPagamento", venda.getFormaPagamento().getDescricao());
            resp.put("troco", venda.getTroco());
            resp.put("dataHora", venda.getDataHora().toString());
            resp.put("razaoSocial", empresa.getRazaoSocial());
            resp.put("cnpj", empresa.getCnpj());
            resp.put("ie", empresa.getInscricaoEstadual());
            resp.put("endereco", empresa.getEnderecoFormatado());
            resp.put("serieNfce", empresa.getSerieNfce());
            resp.put("ambienteSefaz", empresa.getAmbienteSefaz().name());

            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }
}
