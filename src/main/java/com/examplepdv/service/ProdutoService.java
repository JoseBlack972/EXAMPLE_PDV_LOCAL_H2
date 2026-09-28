package com.examplepdv.service;

import com.examplepdv.dto.ImportacaoCsvDTO;
import com.examplepdv.model.Categoria;
import com.examplepdv.model.Produto;
import com.examplepdv.repository.CategoriaRepository;
import com.examplepdv.repository.ProdutoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class ProdutoService {

    private static final Logger log = LoggerFactory.getLogger(ProdutoService.class);

    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProdutoService(ProdutoRepository produtoRepository, CategoriaRepository categoriaRepository) {
        this.produtoRepository = produtoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public List<Produto> listarAtivos() {
        return produtoRepository.findByAtivoTrueOrderByNomeAsc();
    }

    public Optional<Produto> buscarPorId(Long id) {
        return produtoRepository.findById(id);
    }

    public Optional<Produto> buscarPorCodigoBarras(String codigoBarras) {
        return produtoRepository.findByCodigoBarrasAndAtivoTrue(codigoBarras.trim());
    }

    public List<Produto> buscarPorNome(String nome) {
        return produtoRepository.findByNomeContainingIgnoreCaseAndAtivoTrue(nome.trim());
    }

    public List<Produto> buscarPorNomeOuCodigo(String termo) {
        if (termo == null || termo.trim().isEmpty()) {
            return listarAtivos();
        }
        return produtoRepository.buscarPorNomeOuCodigoBarras(termo.trim());
    }

    @Transactional
    public Produto salvar(Produto produto) {
        if (produto.getId() == null) {
            if (produtoRepository.existsByCodigoBarras(produto.getCodigoBarras())) {
                throw new IllegalArgumentException("Código de barras já cadastrado!");
            }
        } else {
            Produto existente = produtoRepository.findById(produto.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));
            if (!existente.getCodigoBarras().equals(produto.getCodigoBarras()) &&
                produtoRepository.existsByCodigoBarras(produto.getCodigoBarras())) {
                throw new IllegalArgumentException("Código de barras já cadastrado!");
            }
        }
        return produtoRepository.save(produto);
    }

    @Transactional
    public void excluir(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));
        produto.setAtivo(false); // soft delete para preservar histórico de vendas
        produtoRepository.save(produto);
    }

    @Transactional
    public ImportacaoCsvDTO importarCsv(InputStream inputStream) {
        ImportacaoCsvDTO resultado = new ImportacaoCsvDTO();
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String linha;
            int numeroLinha = 0;
            String separador = ";";
            boolean cabecalhoVerificado = false;

            // Mapeamento de colunas
            int colNome = 0;
            int colCodigo = 1;
            int colPreco = 2;
            int colEstoque = 3;
            int colCategoria = 4;
            int colAtivo = 5;

            while ((linha = reader.readLine()) != null) {
                numeroLinha++;
                if (numeroLinha == 1 && linha.startsWith("\uFEFF")) {
                    linha = linha.substring(1); // remove BOM UTF-8
                }
                linha = linha.trim();
                if (linha.isEmpty()) {
                    continue;
                }

                // Detecta o separador na primeira linha
                if (!cabecalhoVerificado) {
                    if (linha.contains(";")) {
                        separador = ";";
                    } else if (linha.contains(",")) {
                        separador = ",";
                    } else if (linha.contains("\t")) {
                        separador = "\t";
                    }

                    String[] partesCabecalho = dividirLinhaCsv(linha, separador);
                    boolean ehCabecalho = false;
                    for (int i = 0; i < partesCabecalho.length; i++) {
                        String col = partesCabecalho[i].trim().toLowerCase();
                        if (col.contains("nome") || col.contains("produto") || col.contains("descri")) {
                            colNome = i;
                            ehCabecalho = true;
                        } else if (col.contains("cod") || col.contains("barras") || col.contains("ean")) {
                            colCodigo = i;
                            ehCabecalho = true;
                        } else if (col.contains("prec") || col.contains("valor")) {
                            colPreco = i;
                            ehCabecalho = true;
                        } else if (col.contains("est") || col.contains("qtd") || col.contains("quant")) {
                            colEstoque = i;
                            ehCabecalho = true;
                        } else if (col.contains("cat") || col.contains("grupo")) {
                            colCategoria = i;
                            ehCabecalho = true;
                        } else if (col.contains("ativ") || col.contains("status")) {
                            colAtivo = i;
                            ehCabecalho = true;
                        }
                    }

                    cabecalhoVerificado = true;
                    if (ehCabecalho) {
                        continue; // pula linha de cabeçalho
                    }
                }

                resultado.setTotalLinhas(resultado.getTotalLinhas() + 1);
                String[] colunas = dividirLinhaCsv(linha, separador);

                if (colunas.length <= Math.max(colNome, Math.max(colCodigo, colPreco))) {
                    resultado.incrementarFalhas();
                    resultado.adicionarErro("Linha " + numeroLinha + ": Quantidade insuficiente de colunas obrigatórias.");
                    continue;
                }

                try {
                    String nome = colunas[colNome].trim();
                    String codigoBarras = colunas[colCodigo].trim().replaceAll("[^0-9a-zA-Z]", "");
                    String strPreco = colunas[colPreco].trim().replace("R$", "").replace(" ", "").trim();
                    
                    if (strPreco.contains(",") && strPreco.contains(".")) {
                        strPreco = strPreco.replace(".", "").replace(",", ".");
                    } else if (strPreco.contains(",")) {
                        strPreco = strPreco.replace(",", ".");
                    }
                    BigDecimal preco = new BigDecimal(strPreco);

                    int estoque = 0;
                    if (colEstoque < colunas.length && !colunas[colEstoque].trim().isEmpty()) {
                        String strEstoque = colunas[colEstoque].trim().replaceAll("[^0-9]", "");
                        if (!strEstoque.isEmpty()) {
                            estoque = Integer.parseInt(strEstoque);
                        }
                    }

                    Categoria categoria = null;
                    if (colCategoria < colunas.length && !colunas[colCategoria].trim().isEmpty()) {
                        String nomeCat = colunas[colCategoria].trim();
                        categoria = categoriaRepository.findByNomeIgnoreCase(nomeCat)
                                .orElseGet(() -> {
                                    Categoria nova = new Categoria(nomeCat);
                                    return categoriaRepository.save(nova);
                                });
                    }

                    boolean ativo = true;
                    if (colAtivo < colunas.length && !colunas[colAtivo].trim().isEmpty()) {
                        String strAtivo = colunas[colAtivo].trim().toLowerCase();
                        ativo = "true".equals(strAtivo) || "1".equals(strAtivo) || "sim".equals(strAtivo) || "s".equals(strAtivo) || "ativo".equals(strAtivo);
                    }

                    if (nome.isEmpty() || codigoBarras.isEmpty()) {
                        resultado.incrementarFalhas();
                        resultado.adicionarErro("Linha " + numeroLinha + ": Nome ou Código de Barras está em branco.");
                        continue;
                    }

                    // Upsert: atualiza se já existir, ou cria novo
                    Optional<Produto> prodOpt = produtoRepository.findByCodigoBarras(codigoBarras);
                    if (prodOpt.isPresent()) {
                        Produto existente = prodOpt.get();
                        existente.setNome(nome);
                        existente.setPreco(preco);
                        existente.setEstoque(estoque);
                        if (categoria != null) {
                            existente.setCategoria(categoria);
                        }
                        existente.setAtivo(ativo);
                        produtoRepository.save(existente);
                        resultado.incrementarAtualizados();
                        resultado.adicionarMensagem("Atualizado: " + nome + " (EAN: " + codigoBarras + ")");
                    } else {
                        Produto novo = new Produto();
                        novo.setNome(nome);
                        novo.setCodigoBarras(codigoBarras);
                        novo.setPreco(preco);
                        novo.setEstoque(estoque);
                        novo.setCategoria(categoria);
                        novo.setAtivo(ativo);
                        produtoRepository.save(novo);
                        resultado.incrementarCriados();
                        resultado.adicionarMensagem("Cadastrado: " + nome + " (EAN: " + codigoBarras + ")");
                    }

                } catch (Exception ex) {
                    resultado.incrementarFalhas();
                    resultado.adicionarErro("Linha " + numeroLinha + ": Erro ao processar dados (" + ex.getMessage() + ")");
                }
            }
        } catch (Exception e) {
            log.error("Erro fatal ao ler CSV de produtos: ", e);
            throw new RuntimeException("Falha na leitura do arquivo CSV: " + e.getMessage(), e);
        }

        return resultado;
    }

    private String[] dividirLinhaCsv(String linha, String separador) {
        List<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean entreAspas = false;

        char sepChar = separador.charAt(0);

        for (int i = 0; i < linha.length(); i++) {
            char c = linha.charAt(i);
            if (c == '"') {
                entreAspas = !entreAspas;
            } else if (c == sepChar && !entreAspas) {
                tokens.add(sb.toString().trim().replaceAll("^\"|\"$", ""));
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        tokens.add(sb.toString().trim().replaceAll("^\"|\"$", ""));
        return tokens.toArray(new String[0]);
    }
}
