package com.examplepdv.controller;

import com.examplepdv.dto.ImportacaoCsvDTO;
import com.examplepdv.model.Produto;
import com.examplepdv.service.CategoriaService;
import com.examplepdv.service.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;
    private final CategoriaService categoriaService;

    public ProdutoController(ProdutoService produtoService, CategoriaService categoriaService) {
        this.produtoService = produtoService;
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("produtos", produtoService.listarAtivos());
        return "produtos/list";
    }

    @GetMapping("/novo")
    @PreAuthorize("hasRole('ADMIN')")
    public String novo(Model model) {
        model.addAttribute("produto", new Produto());
        model.addAttribute("categorias", categoriaService.listarTodas());
        return "produtos/form";
    }

    @PostMapping("/salvar")
    @PreAuthorize("hasRole('ADMIN')")
    public String salvar(@Valid @ModelAttribute("produto") Produto produto,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("categorias", categoriaService.listarTodas());
            return "produtos/form";
        }

        try {
            produtoService.salvar(produto);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Produto salvo com sucesso!");
            return "redirect:/produtos";
        } catch (IllegalArgumentException e) {
            model.addAttribute("categorias", categoriaService.listarTodas());
            model.addAttribute("mensagemErro", e.getMessage());
            return "produtos/form";
        }
    }

    @GetMapping("/editar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return produtoService.buscarPorId(id)
                .map(prod -> {
                    model.addAttribute("produto", prod);
                    model.addAttribute("categorias", categoriaService.listarTodas());
                    return "produtos/form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("mensagemErro", "Produto não encontrado!");
                    return "redirect:/produtos";
                });
    }

    @GetMapping("/excluir/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            produtoService.excluir(id);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Produto excluído com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", e.getMessage());
        }
        return "redirect:/produtos";
    }

    // Funcionalidade de Importação de Estoque via CSV para Gestor e Admin
    @GetMapping("/importar-csv")
    @PreAuthorize("hasAnyRole('GESTOR', 'ADMIN')")
    public String paginaImportarCsv(Model model) {
        return "produtos/importar-csv";
    }

    @PostMapping("/importar-csv")
    @PreAuthorize("hasAnyRole('GESTOR', 'ADMIN')")
    public String processarImportarCsv(@RequestParam("arquivo") MultipartFile arquivo,
                                       RedirectAttributes redirectAttributes) {
        if (arquivo == null || arquivo.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Por favor, selecione um arquivo .CSV válido com a lista de produtos.");
            return "redirect:/produtos/importar-csv";
        }

        try {
            ImportacaoCsvDTO resultado = produtoService.importarCsv(arquivo.getInputStream());
            redirectAttributes.addFlashAttribute("resultadoImportacao", resultado);

            if (resultado.getCriados() > 0 || resultado.getAtualizados() > 0) {
                redirectAttributes.addFlashAttribute("mensagemSucesso",
                        String.format("Importação de estoque concluída! %d novos produtos cadastrados, %d produtos atualizados.",
                                resultado.getCriados(), resultado.getAtualizados()));
            } else if (resultado.getFalhas() > 0) {
                redirectAttributes.addFlashAttribute("mensagemErro",
                        String.format("O arquivo continha %d erros de validação e nenhum produto foi cadastrado.", resultado.getFalhas()));
            } else {
                redirectAttributes.addFlashAttribute("mensagemAviso", "Nenhum produto foi processado no arquivo enviado.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao processar o arquivo CSV: " + e.getMessage());
        }

        return "redirect:/produtos/importar-csv";
    }

    @GetMapping("/modelo-csv")
    @ResponseBody
    public ResponseEntity<byte[]> baixarModeloCsv() {
        String csvExemplo = "nome;codigo_barras;preco;estoque;categoria;ativo\n" +
                            "Refrigerante Guaraná Antarctica 350ml;7891000100105;5.00;60;Bebidas;true\n" +
                            "Coxinha de Frango com Catupiry;7891000200204;8.50;30;Lanches & Salgados;true\n" +
                            "Café com Leite Especial 150ml;7891000400403;5.00;40;Cafeteria;true\n" +
                            "Bombom de Chocolate Branco 20g;7891000300303;2.50;100;Mercearia & Doces;true\n";
        byte[] bytes = csvExemplo.getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"modelo_estoque_pdv.csv\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(bytes);
    }
}
