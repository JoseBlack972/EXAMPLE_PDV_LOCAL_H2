# 🏪 Sistema PDV & Delivery

Sistema de **Ponto de Venda (PDV)** desenvolvido com **Java e Spring Boot**, com autenticação, controle de acesso por perfil, gerenciamento de produtos, estoque, caixa, vendas, delivery e relatórios.

O projeto foi desenvolvido com foco em **Backend Java, persistência de dados, segurança, Docker e arquitetura de aplicações web**.

---

## 🎯 Sobre o projeto

O objetivo deste projeto é simular um sistema de PDV utilizado por uma cafeteria/comércio, permitindo controlar operações como:

* 🛒 Registro de vendas
* 📦 Gerenciamento de produtos e estoque
* 💰 Controle de caixa
* 👥 Controle de usuários e permissões
* 🚚 Gestão de delivery
* 📊 Relatórios gerenciais
* 📥 Importação de estoque via CSV
* 🔐 Autenticação e autorização por perfil
* 💾 Persistência de dados
* 🐳 Execução utilizando Docker

O projeto possui suporte a **H2 para desenvolvimento/local** e estrutura preparada para utilização com **PostgreSQL em ambientes de deploy**.

---

## 🛠️ Tecnologias

### Backend



\

### Banco de dados

\

### Infraestrutura


\

### Web

* Thymeleaf
* HTML
* CSS
* JavaScript
* Spring MVC

---

## 🏗️ Arquitetura

A aplicação segue uma arquitetura baseada no ecossistema Spring:

```text
                    ┌──────────────────┐
                    │    Navegador     │
                    └────────┬─────────┘
                             │
                             ▼
                    ┌──────────────────┐
                    │   Spring MVC     │
                    │   Controllers    │
                    └────────┬─────────┘
                             │
                             ▼
                    ┌──────────────────┐
                    │    Services      │
                    │ Regras de negócio│
                    └────────┬─────────┘
                             │
                             ▼
                    ┌──────────────────┐
                    │   Spring Data    │
                    │       JPA        │
                    └────────┬─────────┘
                             │
                             ▼
                    ┌──────────────────┐
                    │       H2         │
                    │   Local/Dev      │
                    └──────────────────┘
```

Para ambientes de deploy, o projeto também possui configuração para utilização com PostgreSQL.

---

## 👥 Perfis de acesso

O sistema possui controle de acesso baseado em funções utilizando Spring Security.

| Perfil       | Responsabilidades                                      |
| ------------ | ------------------------------------------------------ |
| **ADMIN**    | Acesso administrativo e gerenciamento geral do sistema |
| **GESTOR**   | Gestão, relatórios e acompanhamento das operações      |
| **OPERADOR** | Operação do PDV, caixa e vendas                        |

> As permissões são controladas pela camada de segurança da aplicação.

---

## 📦 Principais funcionalidades

### 🛒 PDV

* Registro de vendas
* Seleção de produtos
* Controle de quantidade
* Cálculo de valores
* Operação de caixa

### 📦 Produtos e estoque

* Cadastro de produtos
* Gerenciamento de estoque
* Importação de produtos via CSV
* Controle de quantidade

### 💰 Caixa

* Abertura de caixa
* Fechamento de caixa
* Sangrias
* Suprimentos
* Acompanhamento das operações

### 🚚 Delivery

* Gerenciamento de pedidos
* Controle de entregas
* Cadastro de motoboys

### 📊 Gestão

* Relatórios de vendas
* Indicadores de operação
* Acompanhamento de caixa
* Consultas gerenciais

### 🔐 Segurança

* Autenticação de usuários
* Controle de acesso por perfil
* Proteção de áreas administrativas
* Integração com Spring Security

---

## 🗄️ Banco de dados

### Ambiente local

Por padrão, o projeto pode utilizar o H2 em modo persistente:

```text
jdbc:h2:file:/data/pdvdb
```

Quando executado com Docker, os dados são armazenados em um volume:

```text
Docker Volume
      │
      ▼
   /data
      │
      ▼
   H2 Database
```

Isso permite manter os dados mesmo após a parada do container.

### PostgreSQL

O projeto também possui configuração para utilização de PostgreSQL em ambientes de deploy.

---

## 🐳 Executando com Docker

### Requisitos

* Docker
* Docker Compose

Clone o projeto:

```bash
git clone https://github.com/JoseBlack972/EXAMPLE_PDV_LOCAL_H2.git
```

Entre no diretório:

```bash
cd EXAMPLE_PDV_LOCAL_H2
```

Execute:

```bash
docker compose up --build
```

Ou em segundo plano:

```bash
docker compose up --build -d
```

A aplicação estará disponível em:

```text
http://localhost:8080
```

---

## 🛑 Comandos Docker

### Ver logs

```bash
docker compose logs -f
```

### Parar a aplicação

Os dados são preservados:

```bash
docker compose down
```

### Remover containers e volume do banco

⚠️ Isso remove os dados persistidos pelo volume:

```bash
docker compose down -v
```

---

## ☕ Executando sem Docker

### Requisitos

* Java 17
* Maven

Execute:

```bash
mvn clean spring-boot:run
```

A aplicação será iniciada em:

```text
http://localhost:8080
```

O banco H2 local será armazenado em:

```text
./data/pdvdb.mv.db
```

---

## 🗃️ Console H2

Durante o desenvolvimento local, o projeto disponibiliza o console H2:

```text
http://localhost:8080/h2-console
```

Configuração:

```text
Driver: org.h2.Driver

JDBC URL:
jdbc:h2:file:/data/pdvdb

User:
sa

Password:
em branco
```

> ⚠️ O console H2 é destinado ao desenvolvimento/local. Em um ambiente de produção, ele deve permanecer desabilitado.

---

## 📥 Importação de estoque

O projeto possui suporte à importação de estoque utilizando arquivos CSV.

Exemplo:

```text
CSV
 │
 ▼
Importação
 │
 ▼
Validação
 │
 ▼
Produtos
 │
 ▼
Estoque
```

Também está disponível um arquivo de exemplo:

```text
cafeteria_estoque_50_produtos.csv
```

---

## 🧪 Testes

O projeto utiliza as ferramentas de testes do ecossistema Spring.

Dependências relacionadas a testes incluem:

* Spring Boot Test
* Spring Security Test

A evolução do projeto inclui a implementação de testes unitários, testes de integração e testes relacionados à segurança.

---

## 🚀 Deploy

O projeto possui configuração para deploy utilizando Docker e PostgreSQL.

Arquivo:

```text
render.yaml
```

Arquitetura prevista:

```text
          Internet
              │
              ▼
       ┌──────────────┐
       │ Render / Web │
       │   Service    │
       └──────┬───────┘
              │
              ▼
       ┌──────────────┐
       │ Spring Boot  │
       │   + Docker   │
       └──────┬───────┘
              │
              ▼
       ┌──────────────┐
       │ PostgreSQL   │
       └──────────────┘
```

---

## 🔒 Segurança

Este projeto possui credenciais de demonstração para facilitar a execução local.

**Não utilize credenciais de demonstração em ambientes públicos ou de produção.**

Para uma implantação real, recomenda-se utilizar:

* Variáveis de ambiente
* Senhas fortes
* Secrets
* Perfis separados para desenvolvimento e produção
* Desativação do console H2 em produção

---

## 📌 Próximas melhorias

* [ ] Implementar cobertura maior de testes
* [ ] Adicionar testes de integração
* [ ] Adicionar migrations com Flyway
* [ ] Separar configurações `dev` e `prod`
* [ ] Melhorar gerenciamento de credenciais
* [ ] Executar container com usuário não-root
* [ ] Melhorar documentação da API
* [ ] Adicionar CI/CD com GitHub Actions
* [ ] Adicionar screenshots da aplicação
* [ ] Melhorar observabilidade e logs
* [ ] Evoluir integração com PostgreSQL

---

## 📚 Objetivos de aprendizado

Este projeto faz parte da minha evolução no desenvolvimento Backend e tem como principais objetivos praticar:

* Java
* Spring Boot
* Spring MVC
* Spring Security
* Spring Data JPA
* Hibernate
* Bancos relacionais
* Docker
* Docker Compose
* Maven
* Git/GitHub
* Desenvolvimento de aplicações web
* Regras de negócio
* Autenticação e autorização

---

## 👨‍💻 Autor

**José V. Black**

Desenvolvedor Backend em formação, com foco em:

```text
Java
Spring Boot
Python
SQL
Docker
APIs REST
```

GitHub:

https://github.com/JoseBlack972

---

## ⭐ Projeto em destaque

Este projeto representa uma das principais aplicações práticas da minha jornada de estudos em **Java e desenvolvimento Backend**, reunindo desenvolvimento web, persistência de dados, segurança, Docker e regras de negócio em uma única aplicação.
