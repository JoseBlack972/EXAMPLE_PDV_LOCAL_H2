# 🏪 Sistema PDV & Delivery

Aplicação de **Ponto de Venda (PDV) e Delivery** desenvolvida com **Java e Spring Boot**, criada como projeto prático para estudo e desenvolvimento de conhecimentos em Backend.

O sistema reúne funcionalidades de autenticação, controle de acesso por perfil, produtos, estoque, caixa, vendas, delivery e gestão, utilizando **H2** para execução local e estrutura preparada para utilização com **PostgreSQL** em ambientes de deploy.

---

## 🚀 Sobre o projeto

O objetivo deste projeto é desenvolver uma aplicação prática utilizando tecnologias e conceitos comuns no desenvolvimento Backend com Java.

A aplicação foi construída utilizando:

* ☕ Java 17
* 🌱 Spring Boot
* 🔐 Spring Security
* 🗄️ Spring Data JPA / Hibernate
* 💾 H2 Database
* 🐘 PostgreSQL
* 🐳 Docker
* 📦 Docker Compose
* 🛠️ Maven
* 🌐 Thymeleaf

---

## ✅ Funcionalidades implementadas

### 🔐 Autenticação e usuários

* Login de usuários
* Controle de acesso por perfil
* Perfis de usuário
* Proteção das áreas da aplicação

### 🛒 PDV

* Registro de vendas
* Adição e remoção de produtos
* Cálculo de valores
* Controle da operação de caixa
* Fluxo de atendimento

### 📦 Produtos e estoque

* Cadastro de produtos
* Consulta de produtos
* Controle de estoque
* Atualização de quantidade
* Importação de produtos/estoque através de CSV

### 💰 Caixa

* Abertura de caixa
* Fechamento de caixa
* Operações de caixa
* Sangrias
* Suprimentos

### 🛵 Delivery

* Gerenciamento de pedidos
* Controle das operações de delivery
* Organização dos pedidos

### 📊 Gestão e relatórios

* Área de gestão
* Relatórios de vendas
* Consultas por período
* Informações para acompanhamento da operação

---

## 👥 Perfis de acesso

A aplicação possui diferentes níveis de acesso:

| Perfil     | Descrição                                        |
| ---------- | ------------------------------------------------ |
| `ADMIN`    | Acesso administrativo e gerenciamento do sistema |
| `GESTOR`   | Gestão, estoque e relatórios                     |
| `OPERADOR` | Operação do PDV e caixa                          |

O controle de permissões é realizado através do **Spring Security**.

---

## 🏗️ Arquitetura simplificada

```text
                    ┌─────────────────────┐
                    │      Usuário        │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │   Spring Security   │
                    │ Autenticação / Role │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │    Spring Boot      │
                    │   Controllers       │
                    │   Services          │
                    │   Repositories      │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │   JPA / Hibernate   │
                    └──────────┬──────────┘
                               │
                    ┌──────────┴──────────┐
                    ▼                     ▼
              ┌───────────┐       ┌────────────┐
              │    H2     │       │ PostgreSQL │
              │   Local   │       │  Deploy    │
              └───────────┘       └────────────┘
```

---

# 🗄️ Banco de dados

## H2 — Ambiente local

Para facilitar o desenvolvimento e testes locais, a aplicação utiliza **H2 Database**.

Quando executada com Docker, os dados são armazenados em:

```text
/data/pdvdb
```

O Docker utiliza um volume para manter os dados mesmo quando o container é reiniciado.

## PostgreSQL — Deploy

O projeto também possui configuração para utilização com **PostgreSQL**, permitindo separar o ambiente de desenvolvimento local do ambiente de deploy.

---

# 🐳 Executando com Docker

## Requisitos

Antes de iniciar, tenha instalado:

* Docker
* Docker Compose

Verifique:

```bash
docker --version
docker compose version
```

## Iniciar a aplicação

Dentro da pasta do projeto:

```bash
docker compose up --build
```

Ou em segundo plano:

```bash
docker compose up --build -d
```

Depois acesse:

```text
http://localhost:8080
```

---

## 🛑 Parar a aplicação

Para parar os containers mantendo os dados:

```bash
docker compose down
```

Para remover também o volume do banco e iniciar novamente do zero:

```bash
docker compose down -v
```

> ⚠️ O comando `docker compose down -v` remove o volume utilizado para persistência dos dados. Utilize somente quando quiser realmente reinicializar o banco.

---

# ☕ Executando sem Docker

## Requisitos

* Java 17
* Maven

Verifique:

```bash
java -version
mvn -version
```

Execute:

```bash
mvn clean spring-boot:run
```

A aplicação será iniciada na porta:

```text
8080
```

O banco H2 será armazenado localmente na pasta:

```text
./data/
```

---

# 🗃️ Console H2

Durante o desenvolvimento local, o projeto possui suporte ao console web do H2.

Acesse:

```text
http://localhost:8080/h2-console
```

### Configuração local

JDBC URL:

```text
jdbc:h2:file:./data/pdvdb
```

Usuário:

```text
sa
```

Senha:

```text
```

> ⚠️ O console H2 e essas configurações são destinados ao **ambiente de desenvolvimento/local**. Não devem ser expostos dessa forma em um ambiente de produção.

---

# 🔑 Credenciais de demonstração

Para facilitar a avaliação do projeto em ambiente local, existem usuários de demonstração:

| Usuário    | Perfil   |
| ---------- | -------- |
| `admin`    | ADMIN    |
| `gestor`   | GESTOR   |
| `operador` | OPERADOR |

### ⚠️ Importante

As credenciais disponibilizadas pelo projeto são **exclusivamente para demonstração e desenvolvimento local**.

**Não utilize essas credenciais em produção.**

Em um ambiente real, as credenciais devem ser configuradas de forma segura, preferencialmente através de variáveis de ambiente, secrets ou outro mecanismo apropriado de gerenciamento de credenciais.

---

# 📥 Importação de estoque

O projeto possui suporte à importação de dados de estoque através de arquivo CSV.

Exemplo de arquivo disponível no projeto:

```text
cafeteria_estoque_50_produtos.csv
```

Também existem scripts auxiliares para povoamento inicial dos dados.

---

# 🧪 Testes

O projeto possui dependências de testes do ecossistema Spring, incluindo suporte para:

* Spring Boot Test
* Spring Security Test

A evolução do projeto inclui ampliar a cobertura com:

* Testes unitários
* Testes de integração
* Testes de segurança
* Testes dos principais fluxos do PDV

---

# 🚧 Próximas evoluções

Algumas funcionalidades estão planejadas para futuras versões do projeto e **não devem ser consideradas implementadas na versão atual**.

### Integrações externas

* 🔌 Integrações com APIs externas
* 🛵 Integrações com plataformas de delivery
* 🧾 Integração com emissão fiscal/NFC-e
* 🔐 Suporte a certificados digitais A1/A3
* ⚙️ Integrações com serviços externos

### Inteligência Artificial

* 🤖 Experimentação de recursos de IA
* 🤖 Assistente para operações do sistema
* 🔗 Integração com APIs de IA

Esses itens fazem parte do **roadmap de estudo e evolução do projeto**.

---

# 🔒 Segurança

Este projeto possui finalidade principalmente **educacional e de portfólio**.

Antes de utilizar a aplicação em um ambiente real, recomenda-se implementar, entre outros:

* Gerenciamento seguro de credenciais
* Variáveis de ambiente
* Secrets
* HTTPS
* Configurações específicas para produção
* Desativação do console H2 em produção
* Política adequada de senhas
* Controle de permissões mais granular
* Migrações de banco de dados
* Monitoramento e logs
* Hardening do container Docker

---

# 📋 Roadmap técnico

```text
[x] Aplicação Spring Boot
[x] Autenticação
[x] Controle de acesso
[x] Produtos
[x] Estoque
[x] PDV
[x] Caixa
[x] Delivery
[x] Relatórios
[x] Banco H2
[x] Docker
[x] Docker Compose
[ ] Ampliar testes automatizados
[ ] Testes de integração
[ ] Migração com Flyway/Liquibase
[ ] Melhorias de segurança
[ ] API REST documentada
[ ] CI/CD
[ ] Observabilidade
[ ] Integrações externas
[ ] PostgreSQL como ambiente principal de produção
```

---

# 📚 O que estou praticando com este projeto

Este projeto faz parte do meu processo de evolução como desenvolvedor Backend.

Principais conceitos praticados:

* Java
* Spring Boot
* Spring MVC
* Spring Security
* Injeção de dependências
* Arquitetura em camadas
* JPA / Hibernate
* Persistência de dados
* SQL
* H2
* PostgreSQL
* Docker
* Docker Compose
* Maven
* Autenticação e autorização
* Controle de acesso
* Manipulação de arquivos CSV
* Desenvolvimento de aplicações web

---

# 🎯 Objetivo do projeto

O objetivo principal é transformar conhecimentos teóricos em uma aplicação prática, evoluindo progressivamente a arquitetura, segurança, testes, persistência e implantação.

O projeto continuará sendo desenvolvido conforme novos conhecimentos forem adquiridos.

> **Aprender → Desenvolver → Testar → Melhorar → Evoluir**

---

## 👨‍💻 Autor

**José V. Black**

Desenvolvedor Backend em formação, com foco em:

`Java` `Spring Boot` `Python` `SQL` `Docker`

🔗 GitHub: https://github.com/JoseBlack972

---

⭐ Se este projeto foi útil ou interessante, fique à vontade para explorar o código e acompanhar sua evolução.
