// Gestão de Tema (Modo Claro / Escuro), Tela Cheia e Impressão de Cupom
document.addEventListener("DOMContentLoaded", () => {
    // 1. Alternador de Tema
    const btnThemeToggle = document.getElementById("btnThemeToggle");
    const themeIcon = document.getElementById("themeIcon");

    function aplicarTema(tema) {
        document.documentElement.setAttribute("data-bs-theme", tema);
        localStorage.setItem("pdv_theme", tema);
        if (themeIcon) {
            if (tema === "dark") {
                themeIcon.className = "bi bi-sun-fill text-warning";
            } else {
                themeIcon.className = "bi bi-moon-stars-fill text-white";
            }
        }
    }

    const temaSalvo = localStorage.getItem("pdv_theme") || "light";
    aplicarTema(temaSalvo);

    if (btnThemeToggle) {
        btnThemeToggle.addEventListener("click", () => {
            const temaAtual = document.documentElement.getAttribute("data-bs-theme");
            const novoTema = temaAtual === "dark" ? "light" : "dark";
            aplicarTema(novoTema);
        });
    }

    // 2. Alternador de Tela Cheia
    const btnFullscreenToggle = document.getElementById("btnFullscreenToggle");
    const fullscreenIcon = document.getElementById("fullscreenIcon");

    function atualizarIconeFullscreen() {
        if (!fullscreenIcon) return;
        if (document.fullscreenElement) {
            fullscreenIcon.className = "bi bi-fullscreen-exit text-warning";
            btnFullscreenToggle.setAttribute("title", "Sair da Tela Cheia");
        } else {
            fullscreenIcon.className = "bi bi-arrows-fullscreen text-white";
            btnFullscreenToggle.setAttribute("title", "Modo Tela Cheia");
        }
    }

    if (btnFullscreenToggle) {
        btnFullscreenToggle.addEventListener("click", () => {
            if (!document.fullscreenElement) {
                document.documentElement.requestFullscreen().catch(err => {
                    console.warn("Fullscreen request error:", err);
                });
            } else {
                if (document.exitFullscreen) {
                    document.exitFullscreen();
                }
            }
        });

        document.addEventListener("fullscreenchange", atualizarIconeFullscreen);
    }

    // 3. Auto-dismiss alerts
    const alerts = document.querySelectorAll(".alert-dismissible");
    alerts.forEach(alert => {
        setTimeout(() => {
            const bsAlert = bootstrap.Alert.getOrCreateInstance(alert);
            if (bsAlert) bsAlert.close();
        }, 5000);
    });
});

// Função Global de Impressão Direta em Impressoras Térmicas (NFC-e / Cupom)
// Isola a impressão em iframe oculto para evitar página em branco causada por modais Bootstrap
window.imprimirCupomFiscal = function(elementId = "conteudoComprovante") {
    const comprovanteEl = document.getElementById(elementId);
    if (!comprovanteEl) {
        window.print();
        return;
    }

    let iframe = document.getElementById("iframeImpressaoCupom");
    if (!iframe) {
        iframe = document.createElement("iframe");
        iframe.id = "iframeImpressaoCupom";
        iframe.style.position = "fixed";
        iframe.style.top = "-9999px";
        iframe.style.left = "-9999px";
        iframe.style.width = "80mm";
        iframe.style.height = "100px";
        iframe.style.border = "none";
        document.body.appendChild(iframe);
    }

    const doc = iframe.contentWindow.document;
    doc.open();
    doc.write(`
        <!DOCTYPE html>
        <html lang="pt-BR">
        <head>
            <meta charset="utf-8">
            <title>DANFE NFC-e - Cupom Fiscal</title>
            <style>
                @page {
                    size: 80mm auto;
                    margin: 0;
                }
                * {
                    box-sizing: border-box;
                    -webkit-print-color-adjust: exact !important;
                    print-color-adjust: exact !important;
                }
                html, body {
                    margin: 0;
                    padding: 3mm 4mm;
                    background: #ffffff !important;
                    color: #000000 !important;
                    font-family: 'Courier New', Courier, monospace;
                    font-size: 11px;
                    line-height: 1.25;
                    width: 74mm;
                }
                table {
                    width: 100%;
                    border-collapse: collapse;
                }
                th, td {
                    padding: 2px 0;
                }
                .text-center { text-align: center; }
                .text-end { text-align: right; }
                .fw-bold { font-weight: bold; }
                .text-uppercase { text-transform: uppercase; }
                .text-muted { color: #555; }
                .border-bottom { border-bottom: 1px dashed #000; }
                .border-top { border-top: 1px dashed #000; }
                .my-1 { margin-top: 3px; margin-bottom: 3px; }
                .py-1 { padding-top: 3px; padding-bottom: 3px; }
                .pb-1 { padding-bottom: 3px; }
                .mb-1 { margin-bottom: 3px; }
                .pb-2 { padding-bottom: 5px; }
                .mb-2 { margin-bottom: 5px; }
                .d-flex {
                    display: flex;
                    justify-content: space-between;
                }
                .justify-content-between {
                    justify-content: space-between;
                }
                svg {
                    display: block;
                    margin: 4px auto;
                }
            </style>
        </head>
        <body>
            ${comprovanteEl.innerHTML}
        </body>
        </html>
    `);
    doc.close();

    setTimeout(() => {
        iframe.contentWindow.focus();
        iframe.contentWindow.print();
    }, 250);
};
