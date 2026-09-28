# 🏪 Sistema PDV (Ponto de Venda) & Delivery - H2 Database & Docker

Aplicação corporativa de Ponto de Venda (PDV), Gestão Fiscal (NFC-e, Certificado A1/A3, CSC), Gestão de Delivery (iFood / 99Food), **Importação de Estoque em Lote via CSV** e **Portal Executivo do Gestor com Monitor em Tempo Real e Relatórios**, configurado para rodar com **Banco de Dados H2 embutido/arquivo persistente** e **Docker** local com contêiner único e leve.

---

## 🚀 Como Executar Localmente com Docker

Não é necessário instalar nem configurar nenhum banco de dados externo (PostgreSQL ou MySQL). O H2 roda embutido na própria aplicação e persiste os dados no volume Docker montado em `/data`.

### 1. Iniciar no Docker
No terminal, dentro da pasta do projeto (`example-pdv`):

```bash
docker compose up --build
```

Ou em segundo plano (*detached mode*):
```bash
docker compose up --build -d
```

### 2. Acessar a Aplicação
Abra o navegador em:
👉 **[http://localhost:8080](http://localhost:8080)**

---

## 🗄️ Acesso ao Console Web do H2

O console web do banco H2 está habilitado para consultas e administração direta:
- **URL do Console:** [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
- **Driver Class:** `org.h2.Driver`
- **JDBC URL:** `jdbc:h2:file:/data/pdvdb`
- **User Name:** `sa`
- **Password:** *(deixe em branco)*

*(Nota: se estiver rodando localmente sem Docker via Maven, use a JDBC URL: `jdbc:h2:file:./data/pdvdb`)*

---

## 👤 Credenciais e Usuários Padrão (Auto-Start)

No primeiro deploy ou inicialização, a aplicação detecta o banco novo e provisiona automaticamente os 3 perfis de usuário padrão com catálogo inicial de produtos e motoboys:

| Usuário | Senha | Perfil | Destino Padrão & Permissões |
|---|---|---|---|
| **gestor** | `gestor123` | **GESTOR** | **Portal de Gestão & Relatórios (`/gestao`)**. Relatórios de vendas (Dia, Semana, Mês), impressão A4, monitor de caixa em tempo real e Importação de Estoque via CSV. |
| **admin** | `admin123` | **ADMIN** | Acesso completo a todas as áreas: PDV, Caixa, Produtos, Usuários, Delivery, Gestão, Configuração da Empresa, Manuais e Reset do Sistema. |
| **operador** | `operador123` | **OPERADOR** | Frente de Caixa (PDV), Leitura EAN, Abas Rápidas, Abertura/Fechamento de Caixa, Sangrias/Suprimentos, Delivery e Agente de IA do Caixa. |

---

## 🛠️ Comandos Úteis do Docker

- **Ver logs em tempo real:**
  ```bash
  docker compose logs -f
  ```

- **Parar a aplicação mantendo os dados:**
  ```bash
  docker compose down
  ```

- **Parar e limpar o banco de dados (reiniciar zerado):**
  ```bash
  docker compose down -v
  ```

- **Executar diretamente com Docker Run (sem Docker Compose):**
  ```bash
  docker build -t example-pdv .
  docker run -d -p 8080:8080 -v example-pdv-data:/data --name example-pdv example-pdv
  ```

---

## ☕ Execução sem Docker (Direto na Máquina Host)

Requisitos: Java 17 e Maven instalados.

```bash
mvn clean spring-boot:run
```
A aplicação iniciará na porta 8080 e criará o arquivo do banco H2 na pasta local `./data/pdvdb.mv.db`.
