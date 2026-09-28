package com.examplepdv.controller;

import com.examplepdv.model.Produto;
import com.examplepdv.service.ProdutoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoApiController {

    private final ProdutoService produtoService;

    public ProdutoApiController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping
    public List<Produto> listarTodos() {
        return produtoService.listarAtivos();
    }

    @GetMapping("/codigo/{codigoBarras}")
    public ResponseEntity<Produto> buscarPorCodigoBarras(@PathVariable String codigoBarras) {
        return produtoService.buscarPorCodigoBarras(codigoBarras)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/busca")
    public List<Produto> buscarPorNome(@RequestParam(value = "q", defaultValue = "") String q) {
        return produtoService.buscarPorNomeOuCodigo(q);
    }

    @GetMapping("/pesquisa")
    public List<Produto> pesquisarPorNomeOuCodigo(@RequestParam(value = "termo", defaultValue = "") String termo) {
        return produtoService.buscarPorNomeOuCodigo(termo);
    }
}
