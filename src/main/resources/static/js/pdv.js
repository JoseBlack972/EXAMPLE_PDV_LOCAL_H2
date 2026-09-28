document.addEventListener("DOMContentLoaded", () => {
    let carrinho = [];
    let produtosCatalogo = [];

    // Elementos da Aba Código de Barras (Exclusivo)
    const codigoBarrasInput = document.getElementById("codigoBarrasInput");
    const btnBuscarCodigo = document.getElementById("btnBuscarCodigo");
    const quantidadeInputCodigo = document.getElementById("quantidadeInputCodigo");

    // Elementos da Aba Lista de Produtos (Busca por Nome & Seleção por Mouse)
    const filtroNomeProduto = document.getElementById("filtroNomeProduto");
    const btnLimparFiltro = document.getElementById("btnLimparFiltro");
    const quantidadeInputLista = document.getElementById("quantidadeInputLista");
    const corpoCatalogoProdutos = document.getElementById("corpoCatalogoProdutos");
    const contadorProdutosCatalogo = document.getElementById("contadorProdutosCatalogo");

    // Elementos da Tabela de Venda e Totais
    const corpoCarrinho = document.getElementById("corpoCarrinho");
    const contadorItens = document.getElementById("contadorItens");
    const displayTotal = document.getElementById("displayTotal");
    const displaySubtotal = document.getElementById("displaySubtotal");
    const displayQtdTotal = document.getElementById("displayQtdTotal");

    const btnFinalizarVenda = document.getElementById("btnFinalizarVenda");
    const btnCancelarVenda = document.getElementById("btnCancelarVenda");

    // Modal Pagamento
    const modalPagamentoEl = document.getElementById("modalPagamento");
    const modalPagamento = modalPagamentoEl ? new bootstrap.Modal(modalPagamentoEl) : null;
    const modalTotalPagar = document.getElementById("modalTotalPagar");
    const valorRecebidoInput = document.getElementById("valorRecebido");
    const valorTroco = document.getElementById("valorTroco");
    const cpfCnpjInput = document.getElementById("cpfCnpjInput");
    const btnConfirmarPagamento = document.getElementById("btnConfirmarPagamento");
    const erroPagamento = document.getElementById("erroPagamento");

    const secaoDinheiro = document.getElementById("secaoDinheiro");
    const secaoPix = document.getElementById("secaoPix");
    const secaoCartao = document.getElementById("secaoCartao");

    // Modal Comprovante NFC-e
    const modalComprovanteEl = document.getElementById("modalComprovante");
    const modalComprovante = modalComprovanteEl ? new bootstrap.Modal(modalComprovanteEl) : null;

    function formatarMoeda(valor) {
        return Number(valor).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
    }

    // 1. CARREGAMENTO E FILTRO DO CATÁLOGO DE PRODUTOS (ABA 2)
    async function carregarCatalogoProdutos() {
        try {
            const resp = await fetch("/api/produtos");
            if (resp.ok) {
                produtosCatalogo = await resp.json();
                renderizarCatalogo(produtosCatalogo);
            }
        } catch (err) {
            console.error("Erro ao carregar lista de produtos:", err);
        }
    }

    function renderizarCatalogo(lista) {
        if (!corpoCatalogoProdutos) return;
        corpoCatalogoProdutos.innerHTML = "";

        if (!lista || lista.length === 0) {
            corpoCatalogoProdutos.innerHTML = `
                <tr>
                    <td colspan="6" class="text-center py-4 text-muted">
                        <i class="bi bi-search display-6 d-block mb-2"></i>
                        Nenhum produto encontrado com este nome.
                    </td>
                </tr>
            `;
            if (contadorProdutosCatalogo) contadorProdutosCatalogo.innerText = "0 produtos";
            return;
        }

        if (contadorProdutosCatalogo) {
            contadorProdutosCatalogo.innerText = `${lista.length} produto(s) listado(s)`;
        }

        lista.forEach(prod => {
            const tr = document.createElement("tr");
            tr.style.cursor = "pointer";
            tr.title = "Clique com o mouse para adicionar à venda";

            tr.innerHTML = `
                <td><code>${prod.codigoBarras}</code></td>
                <td><strong class="text-dark">${prod.nome}</strong></td>
                <td><span class="badge bg-secondary-subtle text-secondary border">${prod.categoria ? prod.categoria.nome : 'Geral'}</span></td>
                <td class="text-end fw-bold text-success">${formatarMoeda(prod.preco)}</td>
                <td class="text-center">
                    <span class="${prod.estoque <= 5 ? 'badge bg-danger' : 'badge bg-light text-dark border'}">
                        ${prod.estoque} un.
                    </span>
                </td>
                <td class="text-center">
                    <button type="button" class="btn btn-sm btn-primary fw-bold px-3 btn-selecionar-mouse">
                        <i class="bi bi-plus-lg me-1"></i> Adicionar
                    </button>
                </td>
            `;

            // Clique com o mouse em qualquer parte da linha adiciona o produto
            tr.addEventListener("click", () => {
                const qtd = parseInt(quantidadeInputLista ? quantidadeInputLista.value : 1) || 1;
                adicionarItemAoCarrinho(prod, qtd);
            });

            corpoCatalogoProdutos.appendChild(tr);
        });
    }

    // Filtragem em tempo real na aba de Lista de Produtos
    if (filtroNomeProduto) {
        filtroNomeProduto.addEventListener("input", (e) => {
            const termo = e.target.value.toLowerCase().trim();
            if (!termo) {
                renderizarCatalogo(produtosCatalogo);
                return;
            }
            const filtrados = produtosCatalogo.filter(p => p.nome.toLowerCase().includes(termo));
            renderizarCatalogo(filtrados);
        });
    }

    if (btnLimparFiltro) {
        btnLimparFiltro.addEventListener("click", () => {
            if (filtroNomeProduto) {
                filtroNomeProduto.value = "";
                renderizarCatalogo(produtosCatalogo);
                filtroNomeProduto.focus();
            }
        });
    }

    // Carrega o catálogo logo no início
    carregarCatalogoProdutos();

    // 2. BUSCA EXCLUSIVA POR CÓDIGO DE BARRAS (ABA 1)
    async function processarCodigoBarras() {
        if (!codigoBarrasInput) return;
        const codigo = codigoBarrasInput.value.trim();
        if (!codigo) return;

        try {
            const resp = await fetch(`/api/produtos/codigo/${encodeURIComponent(codigo)}`);
            if (resp.ok) {
                const produto = await resp.json();
                const qtd = parseInt(quantidadeInputCodigo ? quantidadeInputCodigo.value : 1) || 1;
                adicionarItemAoCarrinho(produto, qtd);
                codigoBarrasInput.value = "";
                if (quantidadeInputCodigo) quantidadeInputCodigo.value = 1;
                codigoBarrasInput.focus();
            } else {
                alert(`Código de barras não cadastrado ou produto inativo: "${codigo}"`);
                codigoBarrasInput.select();
            }
        } catch (err) {
            console.error("Erro ao buscar código de barras:", err);
            alert("Erro de conexão ao buscar produto por código de barras.");
        }
    }

    if (codigoBarrasInput) {
        codigoBarrasInput.addEventListener("keydown", (e) => {
            if (e.key === "Enter") {
                e.preventDefault();
                processarCodigoBarras();
            }
        });
    }

    if (btnBuscarCodigo) {
        btnBuscarCodigo.addEventListener("click", () => {
            processarCodigoBarras();
        });
    }

    // Foco automático ao trocar de aba
    const tabCodigoBtn = document.getElementById("tab-codigo-tab");
    const tabListaBtn = document.getElementById("tab-lista-tab");

    if (tabCodigoBtn) {
        tabCodigoBtn.addEventListener("shown.bs.tab", () => {
            if (codigoBarrasInput) codigoBarrasInput.focus();
        });
    }
    if (tabListaBtn) {
        tabListaBtn.addEventListener("shown.bs.tab", () => {
            if (filtroNomeProduto) filtroNomeProduto.focus();
        });
    }

    // 3. CARRINHO DE COMPRAS E CÁLCULOS
    function atualizarTotais() {
        let total = 0;
        let qtdTotal = 0;

        carrinho.forEach(item => {
            total += item.preco * item.quantidade;
            qtdTotal += item.quantidade;
        });

        displayTotal.innerText = formatarMoeda(total);
        displaySubtotal.innerText = formatarMoeda(total);
        displayQtdTotal.innerText = `${qtdTotal} un.`;
        contadorItens.innerText = `${carrinho.length} item(ns)`;

        btnFinalizarVenda.disabled = carrinho.length === 0;

        if (carrinho.length === 0) {
            corpoCarrinho.innerHTML = `
                <tr id="linhaVazia">
                    <td colspan="7" class="text-center py-5 text-muted">
                        <i class="bi bi-cart-x display-6 d-block mb-2"></i>
                        Nenhum item adicionado à venda.
                    </td>
                </tr>
            `;
        }
    }

    function renderizarCarrinho() {
        if (carrinho.length === 0) {
            atualizarTotais();
            return;
        }

        corpoCarrinho.innerHTML = "";
        carrinho.forEach((item, index) => {
            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td class="text-muted small">${index + 1}</td>
                <td><code>${item.codigo}</code></td>
                <td class="fw-semibold">${item.nome}</td>
                <td class="text-center">
                    <div class="input-group input-group-sm justify-content-center" style="max-width: 120px; margin: auto;">
                        <button class="btn btn-outline-secondary btn-diminuir" data-index="${index}">-</button>
                        <span class="input-group-text bg-transparent px-3">${item.quantidade}</span>
                        <button class="btn btn-outline-secondary btn-aumentar" data-index="${index}">+</button>
                    </div>
                </td>
                <td class="text-end">${formatarMoeda(item.preco)}</td>
                <td class="text-end fw-bold">${formatarMoeda(item.preco * item.quantidade)}</td>
                <td class="text-center">
                    <button class="btn btn-outline-danger btn-sm btn-remover" data-index="${index}" title="Remover item">
                        <i class="bi bi-trash"></i>
                    </button>
                </td>
            `;
            corpoCarrinho.appendChild(tr);
        });

        document.querySelectorAll(".btn-aumentar").forEach(btn => {
            btn.addEventListener("click", () => {
                const idx = parseInt(btn.getAttribute("data-index"));
                carrinho[idx].quantidade += 1;
                renderizarCarrinho();
            });
        });

        document.querySelectorAll(".btn-diminuir").forEach(btn => {
            btn.addEventListener("click", () => {
                const idx = parseInt(btn.getAttribute("data-index"));
                if (carrinho[idx].quantidade > 1) {
                    carrinho[idx].quantidade -= 1;
                } else {
                    carrinho.splice(idx, 1);
                }
                renderizarCarrinho();
            });
        });

        document.querySelectorAll(".btn-remover").forEach(btn => {
            btn.addEventListener("click", () => {
                const idx = parseInt(btn.getAttribute("data-index"));
                carrinho.splice(idx, 1);
                renderizarCarrinho();
            });
        });

        atualizarTotais();
    }

    function adicionarItemAoCarrinho(produto, quantidade) {
        if (!produto) return;
        const qtd = parseInt(quantidade) || 1;

        if (produto.estoque !== undefined && produto.estoque <= 0) {
            alert(`Aviso: O produto "${produto.nome}" está com estoque zerado no momento.`);
        }

        const existente = carrinho.find(it => it.produtoId === produto.id);
        if (existente) {
            existente.quantidade += qtd;
        } else {
            carrinho.push({
                produtoId: produto.id,
                codigo: produto.codigoBarras,
                nome: produto.nome,
                preco: Number(produto.preco),
                quantidade: qtd
            });
        }

        renderizarCarrinho();
    }

    if (btnCancelarVenda) {
        btnCancelarVenda.addEventListener("click", () => {
            if (carrinho.length === 0) return;
            if (confirm("Deseja realmente cancelar a venda atual e esvaziar o carrinho?")) {
                carrinho = [];
                renderizarCarrinho();
                if (codigoBarrasInput) codigoBarrasInput.focus();
            }
        });
    }

    // 4. MODAL PAGAMENTO E FINALIZAÇÃO
    function abrirModalPagamento() {
        if (carrinho.length === 0) return;

        let total = carrinho.reduce((acc, item) => acc + (item.preco * item.quantidade), 0);
        modalTotalPagar.innerText = formatarMoeda(total);
        valorRecebidoInput.value = total.toFixed(2);
        valorTroco.innerText = formatarMoeda(0);
        if (cpfCnpjInput) cpfCnpjInput.value = "";
        erroPagamento.classList.add("d-none");

        document.getElementById("fpDinheiro").checked = true;
        alternarFormaPagamento("DINHEIRO");

        modalPagamento.show();
        setTimeout(() => valorRecebidoInput.select(), 500);
    }

    if (btnFinalizarVenda) {
        btnFinalizarVenda.addEventListener("click", abrirModalPagamento);
    }

    document.addEventListener("keydown", (e) => {
        if (e.key === "F2") {
            e.preventDefault();
            abrirModalPagamento();
        }
    });

    function alternarFormaPagamento(forma) {
        secaoDinheiro.classList.add("d-none");
        secaoPix.classList.add("d-none");
        secaoCartao.classList.add("d-none");

        if (forma === "DINHEIRO") {
            secaoDinheiro.classList.remove("d-none");
            recalcularTroco();
        } else if (forma === "PIX") {
            secaoPix.classList.remove("d-none");
            btnConfirmarPagamento.disabled = false;
        } else {
            secaoCartao.classList.remove("d-none");
            btnConfirmarPagamento.disabled = false;
        }
    }

    document.querySelectorAll("input[name='formaPagamento']").forEach(radio => {
        radio.addEventListener("change", (e) => {
            alternarFormaPagamento(e.target.value);
        });
    });

    function recalcularTroco() {
        let total = carrinho.reduce((acc, item) => acc + (item.preco * item.quantidade), 0);
        let recebido = parseFloat(valorRecebidoInput.value) || 0;
        let troco = recebido - total;

        if (troco >= 0) {
            valorTroco.innerText = formatarMoeda(troco);
            valorTroco.className = "fs-5 fw-bold text-success";
            btnConfirmarPagamento.disabled = false;
        } else {
            valorTroco.innerText = `Falta ${formatarMoeda(Math.abs(troco))}`;
            valorTroco.className = "fs-5 fw-bold text-danger";
            btnConfirmarPagamento.disabled = true;
        }
    }

    if (valorRecebidoInput) {
        valorRecebidoInput.addEventListener("input", recalcularTroco);
    }

    if (btnConfirmarPagamento) {
        btnConfirmarPagamento.addEventListener("click", async () => {
            const formaSelecionada = document.querySelector("input[name='formaPagamento']:checked").value;
            const total = carrinho.reduce((acc, item) => acc + (item.preco * item.quantidade), 0);
            const recebido = formaSelecionada === "DINHEIRO" ? parseFloat(valorRecebidoInput.value) || total : total;
            const troco = formaSelecionada === "DINHEIRO" ? recebido - total : 0;
            const cpfCnpj = cpfCnpjInput ? cpfCnpjInput.value.trim() : "";

            const payload = {
                formaPagamento: formaSelecionada,
                valorRecebido: recebido,
                troco: troco,
                cpfCnpj: cpfCnpj,
                itens: carrinho.map(it => ({
                    produtoId: it.produtoId,
                    quantidade: it.quantidade,
                    precoUnitario: it.preco
                }))
            };

            btnConfirmarPagamento.disabled = true;
            btnConfirmarPagamento.innerHTML = `<span class="spinner-border spinner-border-sm me-1"></span> Emitindo NFC-e...`;

            try {
                const resp = await fetch("/api/pdv/finalizar", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(payload)
                });

                const data = await resp.json();

                if (resp.ok && data.sucesso) {
                    modalPagamento.hide();

                    document.getElementById("reciboVendaId").innerText = data.vendaId;
                    if (data.razaoSocial) document.getElementById("reciboRazaoSocial").innerText = data.razaoSocial;
                    if (data.cnpj) document.getElementById("reciboCnpj").innerText = data.cnpj;
                    if (data.ie) document.getElementById("reciboIe").innerText = data.ie;
                    if (data.endereco) document.getElementById("reciboEndereco").innerText = data.endereco;
                    if (data.serieNfce) document.getElementById("reciboSerie").innerText = data.serieNfce;
                    if (data.ambienteSefaz) document.getElementById("reciboAmbiente").innerText = data.ambienteSefaz === "PRODUCAO" ? "Produção" : "Homologação";
                    document.getElementById("reciboDataHora").innerText = new Date().toLocaleString('pt-BR');
                    document.getElementById("reciboTotal").innerText = formatarMoeda(data.total);
                    document.getElementById("reciboFormaPagto").innerText = data.formaPagamento;
                    document.getElementById("reciboValorRecebido").innerText = formatarMoeda(recebido);
                    document.getElementById("reciboTroco").innerText = formatarMoeda(data.troco || 0);
                    document.getElementById("reciboTributos").innerText = formatarMoeda(data.total * 0.1845);

                    const reciboConsumidor = document.getElementById("reciboConsumidor");
                    if (cpfCnpj) {
                        reciboConsumidor.innerHTML = `<strong>${cpfCnpj}</strong>`;
                    } else {
                        reciboConsumidor.innerText = "CONSUMIDOR NÃO IDENTIFICADO";
                    }

                    const chaveFicticia = `3526 0912 3456 7800 0190 6500 1000 0000 0${data.vendaId} 1234 5678`;
                    document.getElementById("reciboChaveAcesso").innerText = chaveFicticia;

                    const reciboItens = document.getElementById("reciboItens");
                    reciboItens.innerHTML = "";
                    carrinho.forEach((it, idx) => {
                        const tr = document.createElement("tr");
                        tr.innerHTML = `
                            <td>${idx + 1}</td>
                            <td>${it.nome} (${it.codigo})</td>
                            <td class="text-center">${it.quantidade} UN</td>
                            <td class="text-end">${formatarMoeda(it.preco)}</td>
                            <td class="text-end fw-bold">${formatarMoeda(it.preco * it.quantidade)}</td>
                        `;
                        reciboItens.appendChild(tr);
                    });

                    carrinho = [];
                    renderizarCarrinho();
                    carregarCatalogoProdutos(); // Atualiza estoque na aba de catálogo
                    modalComprovante.show();
                } else {
                    erroPagamento.innerText = data.erro || "Falha ao processar venda.";
                    erroPagamento.classList.remove("d-none");
                }
            } catch (err) {
                console.error("Erro ao finalizar venda:", err);
                erroPagamento.innerText = "Erro ao se comunicar com o servidor.";
                erroPagamento.classList.remove("d-none");
            } finally {
                btnConfirmarPagamento.disabled = false;
                btnConfirmarPagamento.innerHTML = `<i class="bi bi-check-lg"></i> Confirmar & Emitir Cupom Fiscal`;
            }
        });
    }

    if (modalComprovanteEl) {
        modalComprovanteEl.addEventListener("hidden.bs.modal", () => {
            if (codigoBarrasInput) codigoBarrasInput.focus();
        });
    }

    // 5. AGENTE DE IA DO CAIXA (ASSISTENTE ON-DEMAND)
    const chatCorpoIA = document.getElementById("chatCorpoIA");
    const inputPerguntaIA = document.getElementById("inputPerguntaIA");
    const btnEnviarPerguntaIA = document.getElementById("btnEnviarPerguntaIA");

    async function enviarPerguntaIA(texto) {
        const pergunta = (texto || (inputPerguntaIA ? inputPerguntaIA.value : "")).trim();
        if (!pergunta) return;

        if (inputPerguntaIA) inputPerguntaIA.value = "";

        adicionarMensagemChat("user", pergunta);

        const typingId = "typingIndicator_" + Date.now();
        const typingEl = document.createElement("div");
        typingEl.id = typingId;
        typingEl.className = "d-flex gap-2 mb-3";
        typingEl.innerHTML = `
            <div class="flex-shrink-0">
                <span class="badge rounded-circle bg-primary p-2 fs-6"><i class="bi bi-robot"></i></span>
            </div>
            <div class="bg-body border rounded-3 p-2 shadow-sm text-muted small">
                <span class="spinner-grow spinner-grow-sm me-1 text-primary"></span> Assistente IA consultando informações...
            </div>
        `;
        if (chatCorpoIA) {
            chatCorpoIA.appendChild(typingEl);
            chatCorpoIA.scrollTop = chatCorpoIA.scrollHeight;
        }

        try {
            const resp = await fetch("/api/ia/assistente", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ mensagem: pergunta })
            });

            if (resp.ok) {
                const data = await resp.json();
                const typingNode = document.getElementById(typingId);
                if (typingNode) typingNode.remove();

                adicionarMensagemChat("ai", data.mensagem, data.sugestoes);
            } else {
                throw new Error("Erro na resposta do servidor");
            }
        } catch (err) {
            console.error("Erro IA:", err);
            const typingNode = document.getElementById(typingId);
            if (typingNode) typingNode.remove();
            adicionarMensagemChat("ai", "Desculpe, ocorreu uma instabilidade momentânea na conexão com o Assistente de IA. Por favor, tente novamente.");
        }
    }

    function formatarTextoMarkdown(txt) {
        if (!txt) return "";
        return txt
            .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
            .replace(/\*(.*?)\*/g, '<em>$1</em>')
            .replace(/`([^`]+)`/g, '<code>$1</code>')
            .replace(/\n/g, '<br>');
    }

    function adicionarMensagemChat(tipo, texto, sugestoes) {
        if (!chatCorpoIA) return;
        const msgDiv = document.createElement("div");
        msgDiv.className = "d-flex gap-2 mb-3";

        if (tipo === "user") {
            msgDiv.className += " justify-content-end";
            msgDiv.innerHTML = `
                <div class="bg-primary text-white rounded-3 p-2 shadow-sm" style="max-width: 80%; font-size: 0.85rem;">
                    ${texto}
                </div>
                <div class="flex-shrink-0">
                    <span class="badge rounded-circle bg-secondary p-2 fs-6"><i class="bi bi-person-fill"></i></span>
                </div>
            `;
        } else {
            let chipsHtml = "";
            if (sugestoes && sugestoes.length > 0) {
                chipsHtml = `<div class="d-flex flex-wrap gap-1 mt-2">` +
                    sugestoes.map(s => `<button type="button" class="btn btn-outline-primary btn-sm py-0 px-2 chip-sugestao" style="font-size: 0.72rem;">${s}</button>`).join('') +
                    `</div>`;
            }

            msgDiv.innerHTML = `
                <div class="flex-shrink-0">
                    <span class="badge rounded-circle bg-primary p-2 fs-6"><i class="bi bi-robot"></i></span>
                </div>
                <div class="bg-body border rounded-3 p-3 shadow-sm" style="max-width: 88%; font-size: 0.85rem;">
                    <div>${formatarTextoMarkdown(texto)}</div>
                    ${chipsHtml}
                </div>
            `;
        }

        chatCorpoIA.appendChild(msgDiv);
        chatCorpoIA.scrollTop = chatCorpoIA.scrollHeight;

        msgDiv.querySelectorAll(".chip-sugestao").forEach(btn => {
            btn.addEventListener("click", () => {
                enviarPerguntaIA(btn.innerText);
            });
        });
    }

    if (btnEnviarPerguntaIA) {
        btnEnviarPerguntaIA.addEventListener("click", () => enviarPerguntaIA());
    }

    if (inputPerguntaIA) {
        inputPerguntaIA.addEventListener("keydown", (e) => {
            if (e.key === "Enter") {
                e.preventDefault();
                enviarPerguntaIA();
            }
        });
    }

    document.querySelectorAll(".chip-sugestao").forEach(btn => {
        btn.addEventListener("click", () => {
            enviarPerguntaIA(btn.innerText);
        });
    });
});
