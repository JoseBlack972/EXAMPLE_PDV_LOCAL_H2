package com.examplepdv.service;

import com.examplepdv.model.*;
import com.examplepdv.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AssistenteIaService {

    private final ProdutoRepository produtoRepository;
    private final CaixaService caixaService;
    private final VendaRepository vendaRepository;
    private final MovimentoCaixaRepository movimentoRepository;
    private final EmpresaUtilizadoraService empresaService;

    public AssistenteIaService(ProdutoRepository produtoRepository,
                               CaixaService caixaService,
                               VendaRepository vendaRepository,
                               MovimentoCaixaRepository movimentoRepository,
                               EmpresaUtilizadoraService empresaService) {
        this.produtoRepository = produtoRepository;
        this.caixaService = caixaService;
        this.vendaRepository = vendaRepository;
        this.movimentoRepository = movimentoRepository;
        this.empresaService = empresaService;
    }

    public static class RespostaIaDTO {
        private String mensagem;
        private List<String> sugestoes;
        private Map<String, Object> dadosContexto;

        public RespostaIaDTO(String mensagem, List<String> sugestoes) {
            this.mensagem = mensagem;
            this.sugestoes = sugestoes;
        }

        public String getMensagem() { return mensagem; }
        public List<String> getSugestoes() { return sugestoes; }
        public Map<String, Object> getDadosContexto() { return dadosContexto; }
        public void setDadosContexto(Map<String, Object> dadosContexto) { this.dadosContexto = dadosContexto; }
    }

    private String normalizarTexto(String texto) {
        if (texto == null) return "";
        String semAcentos = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return semAcentos.toLowerCase().trim();
    }

    @Transactional(readOnly = true)
    public RespostaIaDTO processarConsulta(String pergunta) {
        if (pergunta == null || pergunta.isBlank()) {
            return new RespostaIaDTO(
                    "Olá, operador! Sou o **Agente de IA do Caixa**. Posso ajudar com preços de produtos, consulta de estoque, procedimentos de sangria/suprimento, cálculos de troco e regras de emissão fiscal. Em que posso ser útil agora?",
                    Arrays.asList("Consultar preço de produto", "Como fazer Sangria?", "Situação do meu Caixa", "Atalhos do Teclado")
            );
        }

        String pNorm = normalizarTexto(pergunta);

        // 1. Dúvidas sobre Caixa Atual / Situação do Caixa
        if (pNorm.contains("meu caixa") || pNorm.contains("saldo do caixa") || pNorm.contains("quanto vendi") || 
            pNorm.contains("saldo da gaveta") || pNorm.contains("resumo do caixa")) {
            return responderSituacaoCaixa();
        }

        // 2. Dúvidas Operacionais: Sangria
        if (pNorm.contains("sangria") || pNorm.contains("retirar dinheiro") || pNorm.contains("tirar dinheiro")) {
            return new RespostaIaDTO(
                    "💵 **Como realizar uma Sangria (Retirada de Dinheiro):**\n\n" +
                    "1. Clique no botão **'Sangria / Suprimento'** na barra superior ou acesse o menu **Caixa &rarr; Movimentação**.\n" +
                    "2. Selecione o tipo **'SANGRIA (Retirada de Dinheiro)'**.\n" +
                    "3. Digite o valor a ser retirado e o motivo obrigatório (ex: *Recolhimento para o cofre*, *Pagamento de fornecedor*).\n" +
                    "4. O sistema verifica se há saldo suficiente em dinheiro na gaveta antes de autorizar.\n" +
                    "5. O valor é subtraído imediatamente do saldo físico do caixa.",
                    Arrays.asList("Como fazer Suprimento?", "Como fechar o caixa?", "Situação do meu Caixa")
            );
        }

        // 3. Dúvidas Operacionais: Suprimento
        if (pNorm.contains("suprimento") || pNorm.contains("adicionar troco") || pNorm.contains("colocar dinheiro") || pNorm.contains("fundo de troco")) {
            return new RespostaIaDTO(
                    "📥 **Como realizar um Suprimento (Entrada de Troco):**\n\n" +
                    "1. Clique no botão **'Sangria / Suprimento'** na barra do caixa.\n" +
                    "2. Escolha **'SUPRIMENTO (Entrada de Dinheiro)'**.\n" +
                    "3. Informe o valor que está entrando na gaveta e o motivo (ex: *Troco adicional em moedas*).\n" +
                    "4. Clique em confirmar. O saldo esperado do caixa será recalculado instantaneamente.",
                    Arrays.asList("Como fazer Sangria?", "Situação do meu Caixa", "Como fechar o caixa?")
            );
        }

        // 4. Dúvidas Operacionais: Fechamento de Caixa
        if (pNorm.contains("fechar caixa") || pNorm.contains("fechamento") || pNorm.contains("encerrar turno")) {
            return new RespostaIaDTO(
                    "🔒 **Procedimento para Fechamento de Caixa:**\n\n" +
                    "1. No final do seu turno, clique no botão vermelho **'Fechar Caixa'**.\n" +
                    "2. Conte as cédulas e moedas físicas na gaveta.\n" +
                    "3. Digite o valor exato no campo **'Saldo Final em Dinheiro Informado'**.\n" +
                    "4. O sistema fará a apuração contra o saldo esperado (Inicial + Vendas Dinheiro + Suprimentos - Sangrias) e apontará se houve sobra, falta (quebra) ou conferência exata.\n" +
                    "5. Confirme o fechamento para emitir o relatório de turno.",
                    Arrays.asList("Situação do meu Caixa", "Como fazer Sangria?", "Atalhos do Teclado")
            );
        }

        // 5. Cancelamento de Item ou Venda
        if (pNorm.contains("cancelar") || pNorm.contains("excluir item") || pNorm.contains("remover produto")) {
            return new RespostaIaDTO(
                    "❌ **Cancelamento de Itens ou Venda:**\n\n" +
                    "- **Para remover um item:** Na tabela do carrinho, clique no ícone da lixeira vermelha ao lado do produto ou reduza a quantidade até zero.\n" +
                    "- **Para cancelar toda a venda atual:** Clique no botão **'Cancelar Venda'** (abaixo do botão de finalizar). Uma confirmação será solicitada para limpar todo o carrinho com segurança.",
                    Arrays.asList("Como dar desconto?", "Atalhos do Teclado", "Como finalizar venda?")
            );
        }

        // 6. Formas de Pagamento e CPF na Nota
        if (pNorm.contains("cpf") || pNorm.contains("cnpj") || pNorm.contains("nota paulista") || pNorm.contains("identificar")) {
            return new RespostaIaDTO(
                    "🆔 **CPF / CNPJ na Nota Fiscal:**\n\n" +
                    "Ao pressionar **F2** para finalizar a venda, preencha o campo opcional **'CPF / CNPJ na Nota Fiscal'** antes de confirmar o pagamento.\n" +
                    "Se o cliente não quiser informar o documento, deixe o campo em branco e o cupom será emitido como *'CONSUMIDOR NÃO IDENTIFICADO'*.",
                    Arrays.asList("Como funciona o PIX?", "Cálculo de Troco", "Finalizar Venda (F2)")
            );
        }

        if (pNorm.contains("pix")) {
            EmpresaUtilizadora emp = empresaService.obterEmpresa();
            return new RespostaIaDTO(
                    "⚡ **Pagamento via PIX:**\n\n" +
                    "1. Na tela de pagamento (F2), selecione a opção **'PIX'**.\n" +
                    "2. O QR Code dinâmico e a chave da empresa (*" + emp.getEmail() + "*) serão exibidos.\n" +
                    "3. Solicite ao cliente que faça a leitura no app do banco e confirme o recebimento antes de emitir a NFC-e.",
                    Arrays.asList("Dinheiro e Troco", "Cartão de Crédito", "Finalizar Venda")
            );
        }

        // 7. Atalhos de Teclado
        if (pNorm.contains("atalho") || pNorm.contains("tecla") || pNorm.contains("f2") || pNorm.contains("enter")) {
            return new RespostaIaDTO(
                    "⌨️ **Principais Atalhos de Teclado no PDV:**\n\n" +
                    "- <kbd class='bg-success text-white px-2 py-1 rounded'>F2</kbd> : Abre diretamente a tela de pagamento e finalização da venda.\n" +
                    "- <kbd class='bg-dark text-white px-2 py-1 rounded'>Enter</kbd> : No campo de código de barras, busca e insere o produto no carrinho.\n" +
                    "- <kbd class='bg-primary text-white px-2 py-1 rounded'>Clique</kbd> : Na aba 'Lista de Produtos', clica em qualquer linha para adicionar.\n" +
                    "- <kbd class='bg-secondary text-white px-2 py-1 rounded'>Ctrl + P</kbd> : Imprime o cupom fiscal eletrônico na impressora térmica.",
                    Arrays.asList("Como finalizar venda?", "Consultar produto", "Situação do meu Caixa")
            );
        }

        // 8. Cálculos de Troco ou Desconto
        RespostaIaDTO calcResp = tentarCalcularTrocoOuDesconto(pNorm);
        if (calcResp != null) {
            return calcResp;
        }

        // 9. Consulta de Produtos no Catálogo
        return consultarProdutosIa(pergunta, pNorm);
    }

    private RespostaIaDTO responderSituacaoCaixa() {
        Optional<Caixa> cOpt = caixaService.obterCaixaAberto();
        if (cOpt.isEmpty()) {
            return new RespostaIaDTO(
                    "⚠️ **Seu caixa está FECHADO no momento.**\n\n" +
                    "Para iniciar as vendas, clique em **'Abrir Caixa Agora'** e informe o fundo de troco inicial.",
                    Arrays.asList("Como abrir o caixa?", "Atalhos do Teclado", "Consultar produtos")
            );
        }

        Caixa c = cOpt.get();
        List<Venda> vendas = vendaRepository.findByCaixaOrderByIdDesc(c);
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal din = BigDecimal.ZERO;
        for (Venda v : vendas) {
            BigDecimal val = v.getValorTotal() != null ? v.getValorTotal() : BigDecimal.ZERO;
            total = total.add(val);
            if (v.getFormaPagamento() == FormaPagamento.DINHEIRO) din = din.add(val);
        }

        List<MovimentoCaixa> movs = movimentoRepository.findByCaixaOrderByDataHoraDesc(c);
        BigDecimal sangrias = BigDecimal.ZERO;
        BigDecimal suprimentos = BigDecimal.ZERO;
        for (MovimentoCaixa m : movs) {
            if (m.getTipo() == TipoMovimentoCaixa.SANGRIA) sangrias = sangrias.add(m.getValor());
            else if (m.getTipo() == TipoMovimentoCaixa.SUPRIMENTO) suprimentos = suprimentos.add(m.getValor());
        }

        BigDecimal gaveta = c.getSaldoInicial().add(din).add(suprimentos).subtract(sangrias);

        String msg = String.format(
                "📊 **Situação Atual do Caixa #%d** (Operador: %s):\n\n" +
                "- **Fundo Inicial:** R$ %.2f\n" +
                "- **Vendas Realizadas:** %d vendas (Total R$ %.2f)\n" +
                "- **Vendas em Dinheiro:** R$ %.2f\n" +
                "- **Suprimentos:** R$ %.2f | **Sangrias:** R$ %.2f\n" +
                "- **💰 Saldo Estimado na Gaveta:** **R$ %.2f**\n\n" +
                "Tudo operando normalmente!",
                c.getId(), c.getUsuario().getNome(),
                c.getSaldoInicial(),
                vendas.size(), total,
                din, suprimentos, sangrias,
                gaveta
        );

        return new RespostaIaDTO(msg, Arrays.asList("Como fazer Sangria?", "Como fechar o caixa?", "Atalhos do Teclado"));
    }

    private RespostaIaDTO tentarCalcularTrocoOuDesconto(String texto) {
        // Cálculo inteligente de troco
        if (texto.contains("troco") || texto.contains("pago") || texto.contains("pagou") || texto.contains("nota")) {
            List<Double> numeros = new ArrayList<>();
            Matcher m = Pattern.compile("(\\d+(?:[.,]\\d+)?)").matcher(texto);
            while (m.find()) {
                try {
                    numeros.add(Double.parseDouble(m.group(1).replace(",", ".")));
                } catch (Exception ignored) {}
            }
            if (numeros.size() >= 2) {
                double val1 = numeros.get(0);
                double val2 = numeros.get(1);
                double recebido = Math.max(val1, val2);
                double total = Math.min(val1, val2);
                double troco = recebido - total;
                return new RespostaIaDTO(
                        String.format("🧮 **Cálculo de Troco:**\n\n" +
                                "- Valor da Venda: **R$ %.2f**\n" +
                                "- Valor Recebido: **R$ %.2f**\n" +
                                "- **➡️ Troco a devolver:** **R$ %.2f**", total, recebido, troco),
                        Arrays.asList("Finalizar Venda (F2)", "Situação do meu Caixa")
                );
            }
        }

        // Cálculo inteligente de desconto
        if (texto.contains("desconto") || texto.contains("%")) {
            List<Double> numeros = new ArrayList<>();
            Matcher m = Pattern.compile("(\\d+(?:[.,]\\d+)?)").matcher(texto);
            while (m.find()) {
                try {
                    numeros.add(Double.parseDouble(m.group(1).replace(",", ".")));
                } catch (Exception ignored) {}
            }
            if (numeros.size() >= 2) {
                double perc = Math.min(numeros.get(0), numeros.get(1));
                double total = Math.max(numeros.get(0), numeros.get(1));
                double descValor = total * (perc / 100.0);
                double totalFinal = total - descValor;
                return new RespostaIaDTO(
                        String.format("🏷️ **Cálculo de Desconto:**\n\n" +
                                "- Valor Original: R$ %.2f\n" +
                                "- Desconto de %.1f%%: - R$ %.2f\n" +
                                "- **➡️ Valor Final com Desconto:** **R$ %.2f**", total, perc, descValor, totalFinal),
                        Arrays.asList("Finalizar Venda (F2)", "Consultar produto")
                );
            }
        }

        return null;
    }

    private RespostaIaDTO consultarProdutosIa(String perguntaOriginal, String pNorm) {
        List<Produto> todos = produtoRepository.findAll();
        List<Produto> correspondentes = new ArrayList<>();

        // Remove palavras comuns de busca
        String termo = pNorm.replaceAll("\\b(quanto|custa|qual|o|a|preco|valor|de|tem|temos|estoque|produto|da|do|unidade)\\b", "").trim();

        if (termo.length() >= 2) {
            for (Produto p : todos) {
                if (!Boolean.TRUE.equals(p.isAtivo())) continue;
                String pNomeNorm = normalizarTexto(p.getNome());
                if (pNomeNorm.contains(termo) || p.getCodigoBarras().equals(termo)) {
                    correspondentes.add(p);
                }
            }
        }

        if (!correspondentes.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("🔍 **Produtos Encontrados no Estoque:**\n\n");

            for (Produto prod : correspondentes) {
                String alerta = "";
                if (prod.getEstoque() <= 0) {
                    alerta = " ❌ *(Estoque ZERADO)*";
                } else if (prod.getEstoque() <= 5) {
                    alerta = " ⚠️ *(Estoque Baixo: " + prod.getEstoque() + " un)*";
                } else {
                    alerta = " *(Estoque: " + prod.getEstoque() + " un)*";
                }

                sb.append(String.format("- **%s**\n", prod.getNome()));
                sb.append(String.format("  - Preço: **R$ %.2f** | EAN: ` %s ` %s\n", prod.getPreco(), prod.getCodigoBarras(), alerta));
            }

            sb.append("\n*Dica: Você pode bipar o código de barras ou clicar na aba 'Lista de Produtos' para adicioná-lo diretamente à venda.*");

            return new RespostaIaDTO(sb.toString(), Arrays.asList("Como finalizar a venda?", "Situação do meu Caixa", "Outro produto"));
        }

        // Resposta padrão caso nenhuma intenção específica seja encontrada
        return new RespostaIaDTO(
                "Entendi sua dúvida sobre *\"" + perguntaOriginal + "\"*. \n\n" +
                "Como Assistente do Caixa, posso te ajudar diretamente com:\n" +
                "1. **Preço e Estoque:** Digite o nome de qualquer produto (ex: *Coca*, *Água*, *Pão*).\n" +
                "2. **Operações:** Digite *Sangria*, *Suprimento*, *Fechar Caixa* ou *Cancelar*.\n" +
                "3. **Cálculos:** Digite *Troco de 50 para 35* ou *Desconto de 10% em 80*.\n" +
                "4. **Caixa Atual:** Digite *Situação do meu Caixa*.",
                Arrays.asList("Consultar Refrigerante", "Como fazer Sangria?", "Situação do meu Caixa", "Atalhos do Teclado")
        );
    }
}
