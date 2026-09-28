package com.examplepdv.service;

import com.examplepdv.model.*;
import com.examplepdv.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class DatabaseResetService {

    private static final Logger log = LoggerFactory.getLogger(DatabaseResetService.class);

    @PersistenceContext
    private EntityManager entityManager;

    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProdutoRepository produtoRepository;
    private final CaixaRepository caixaRepository;
    private final MovimentoCaixaRepository movimentoCaixaRepository;
    private final VendaRepository vendaRepository;
    private final ItemVendaRepository itemVendaRepository;
    private final MotoboyRepository motoboyRepository;
    private final PedidoDeliveryRepository pedidoDeliveryRepository;
    private final EmpresaUtilizadoraRepository empresaUtilizadoraRepository;
    private final PasswordEncoder passwordEncoder;
    private final DataSource dataSource;

    public DatabaseResetService(UsuarioRepository usuarioRepository,
                                CategoriaRepository categoriaRepository,
                                ProdutoRepository produtoRepository,
                                CaixaRepository caixaRepository,
                                MovimentoCaixaRepository movimentoCaixaRepository,
                                VendaRepository vendaRepository,
                                ItemVendaRepository itemVendaRepository,
                                MotoboyRepository motoboyRepository,
                                PedidoDeliveryRepository pedidoDeliveryRepository,
                                EmpresaUtilizadoraRepository empresaUtilizadoraRepository,
                                PasswordEncoder passwordEncoder,
                                DataSource dataSource) {
        this.usuarioRepository = usuarioRepository;
        this.categoriaRepository = categoriaRepository;
        this.produtoRepository = produtoRepository;
        this.caixaRepository = caixaRepository;
        this.movimentoCaixaRepository = movimentoCaixaRepository;
        this.vendaRepository = vendaRepository;
        this.itemVendaRepository = itemVendaRepository;
        this.motoboyRepository = motoboyRepository;
        this.pedidoDeliveryRepository = pedidoDeliveryRepository;
        this.empresaUtilizadoraRepository = empresaUtilizadoraRepository;
        this.passwordEncoder = passwordEncoder;
        this.dataSource = dataSource;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        try {
            log.info("Iniciando rotina de auto-start do primeiro deploy (provisionamento inicial)...");
            removerCheckConstraintsUsuarios();
            garantirTresUsuariosPadrao();
            inicializarEmpresaUtilizadoraPadrao();

            if (categoriaRepository.count() == 0 || produtoRepository.count() == 0) {
                inicializarCatalogoPadrao();
            }

            if (motoboyRepository.count() == 0) {
                inicializarMotoboysEDelivery();
            }

            log.info("Auto-start concluído! Os 3 usuários padrão (admin, gestor, operador) e o catálogo inicial estão prontos para uso.");
        } catch (Exception e) {
            log.error("Erro na rotina de auto-start do banco de dados: ", e);
        }
    }

    public void removerCheckConstraintsUsuarios() {
        if (dataSource == null) {
            return;
        }
        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {
            // Tenta remover constraints antigas conhecidas caso existam (legado H2)
            try { stmt.execute("ALTER TABLE usuarios DROP CONSTRAINT IF EXISTS CONSTRAINT_3"); } catch (Exception ignored) {}
            try { stmt.execute("ALTER TABLE usuarios DROP CONSTRAINT IF EXISTS CONSTRAINT_3E"); } catch (Exception ignored) {}
            try { stmt.execute("ALTER TABLE usuarios DROP CONSTRAINT IF EXISTS CONSTRAINT_4"); } catch (Exception ignored) {}

            try (ResultSet rs = stmt.executeQuery(
                    "SELECT CONSTRAINT_NAME FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS " +
                    "WHERE UPPER(TABLE_NAME) = 'USUARIOS' AND UPPER(CONSTRAINT_TYPE) = 'CHECK'")) {
                List<String> toDrop = new ArrayList<>();
                while (rs.next()) {
                    toDrop.add(rs.getString(1));
                }
                for (String cName : toDrop) {
                    try {
                        stmt.execute("ALTER TABLE usuarios DROP CONSTRAINT " + cName);
                        log.info("Check constraint antiga '{}' removida com sucesso da tabela usuarios.", cName);
                    } catch (Exception e) {
                        log.warn("Não foi possível remover constraint '{}': {}", cName, e.getMessage());
                    }
                }
            } catch (Exception e) {
                log.warn("Verificação de constraints de usuários concluída.");
            }
        } catch (Exception e) {
            log.warn("Conexão para limpeza de constraints: {}", e.getMessage());
        }
    }

    public void garantirTresUsuariosPadrao() {
        // 1. Administrador (admin / admin123)
        Usuario admin = usuarioRepository.findByUsername("admin").orElse(new Usuario());
        admin.setNome("Administrador do Sistema");
        admin.setUsername("admin");
        admin.setSenha(passwordEncoder.encode("admin123"));
        admin.setPerfil(Perfil.ADMIN);
        admin.setAtivo(true);
        usuarioRepository.save(admin);
        log.info("Usuário 'admin' verificado e provisionado.");

        // 2. Gestor / Gerente (gestor / gestor123)
        Usuario gestor = usuarioRepository.findByUsername("gestor").orElse(new Usuario());
        gestor.setNome("Gerente / Gestor");
        gestor.setUsername("gestor");
        gestor.setSenha(passwordEncoder.encode("gestor123"));
        gestor.setPerfil(Perfil.GESTOR);
        gestor.setAtivo(true);
        usuarioRepository.save(gestor);
        log.info("Usuário 'gestor' verificado e provisionado.");

        // 3. Operador de Caixa (operador / operador123)
        Usuario operador = usuarioRepository.findByUsername("operador").orElse(new Usuario());
        operador.setNome("Operador de Caixa");
        operador.setUsername("operador");
        operador.setSenha(passwordEncoder.encode("operador123"));
        operador.setPerfil(Perfil.OPERADOR);
        operador.setAtivo(true);
        usuarioRepository.save(operador);
        log.info("Usuário 'operador' verificado e provisionado.");
    }

    public void inicializarEmpresaUtilizadoraPadrao() {
        if (empresaUtilizadoraRepository.count() == 0) {
            EmpresaUtilizadora empresa = new EmpresaUtilizadora();
            empresa.setId(1L);
            empresa.setRazaoSocial("EXEMPLO COMÉRCIO VAREJISTA LTDA");
            empresa.setNomeFantasia("MERCADO EXEMPLO PDV");
            empresa.setCnpj("12.345.678/0001-90");
            empresa.setInscricaoEstadual("123.456.789.111");
            empresa.setInscricaoMunicipal("987654");
            empresa.setLogradouro("Av. das Nações Unidas");
            empresa.setNumero("1000");
            empresa.setComplemento("Sala 1");
            empresa.setBairro("Centro");
            empresa.setCidade("Salto");
            empresa.setUf("SP");
            empresa.setCep("13320-000");
            empresa.setTelefone("(11) 4028-0000");
            empresa.setEmail("fiscal@examplepdv.com.br");
            empresa.setAmbienteSefaz(AmbienteSefaz.HOMOLOGACAO);
            empresa.setTipoCertificado(TipoCertificado.A1);
            empresa.setCaminhoCertificado("/certificados/certificado_a1.pfx");
            empresa.setSenhaCertificado("123456");
            empresa.setCscId("000001");
            empresa.setCscCodigo("35A4B298C10F4D3EA78019C842B104F5");
            empresa.setSerieNfce(1);
            empresa.setProximoNumeroNfce(1L);
            empresaUtilizadoraRepository.save(empresa);
            log.info("Empresa Utilizadora inicializada com parâmetros fiscais de homologação.");
        }
    }

    public void inicializarCatalogoPadrao() {
        Categoria bebidas = new Categoria("Bebidas");
        Categoria lanches = new Categoria("Lanches & Salgados");
        Categoria mercearia = new Categoria("Mercearia & Doces");
        Categoria cafe = new Categoria("Cafeteria");
        categoriaRepository.saveAll(Arrays.asList(bebidas, lanches, mercearia, cafe));

        List<Produto> produtosIniciais = Arrays.asList(
                new Produto("Refrigerante Coca-Cola 350ml", "7891000100101", new BigDecimal("5.50"), 50, bebidas),
                new Produto("Água Mineral sem Gás 500ml", "7891000100102", new BigDecimal("3.00"), 100, bebidas),
                new Produto("Suco Natural de Laranja 400ml", "7891000100103", new BigDecimal("7.50"), 30, bebidas),
                new Produto("Cerveja Lata 350ml", "7891000100104", new BigDecimal("6.00"), 40, bebidas),
                new Produto("Salgado Assado Frango c/ Catupiry", "7891000200201", new BigDecimal("8.00"), 25, lanches),
                new Produto("Pão de Queijo Mineiro Tradicional", "7891000200202", new BigDecimal("4.50"), 60, lanches),
                new Produto("Sanduíche Natural de Frango", "7891000200203", new BigDecimal("9.90"), 20, lanches),
                new Produto("Barra de Chocolate ao Leite 90g", "7891000300301", new BigDecimal("6.90"), 35, mercearia),
                new Produto("Biscoito Recheado 130g", "7891000300302", new BigDecimal("4.20"), 45, mercearia),
                new Produto("Café Expresso Tradicional 50ml", "7891000400401", new BigDecimal("4.00"), 80, cafe),
                new Produto("Cappuccino Especial 150ml", "7891000400402", new BigDecimal("7.00"), 50, cafe)
        );
        produtoRepository.saveAll(produtosIniciais);
        log.info("Catálogo inicial de produtos e categorias cadastrado.");
    }

    public void inicializarMotoboysEDelivery() {
        Motoboy moto1 = new Motoboy("Carlos Silva", "(11) 98765-4321", "ABC-1D23");
        Motoboy moto2 = new Motoboy("Marcos Oliveira", "(11) 97654-3210", "XYZ-9K87");
        Motoboy moto3 = new Motoboy("Rafael Souza", "(11) 96543-2109", "JKL-4H56");
        motoboyRepository.saveAll(Arrays.asList(moto1, moto2, moto3));

        PedidoDelivery ped1 = new PedidoDelivery();
        ped1.setCodigoPedido("IF-8421");
        ped1.setPlataforma(PlataformaDelivery.IFOOD);
        ped1.setClienteNome("Camila Santos");
        ped1.setClienteTelefone("(11) 99887-1122");
        ped1.setEnderecoEntrega("Rua Marechal Deodoro, 450 - Apto 32");
        ped1.setItensDescricao("2x Refrigerante Coca-Cola 350ml\n1x Salgado Assado Frango");
        ped1.setValorTotal(new BigDecimal("19.00"));
        ped1.setTaxaEntrega(new BigDecimal("5.00"));
        ped1.setFormaPagamento("Cartão de Crédito (App)");
        ped1.setStatus(StatusDelivery.EM_PREPARO);
        ped1.setDataHora(LocalDateTime.now().minusMinutes(20));

        PedidoDelivery ped2 = new PedidoDelivery();
        ped2.setCodigoPedido("99-3105");
        ped2.setPlataforma(PlataformaDelivery.NOVENOVE_FOOD);
        ped2.setClienteNome("Fernando Lima");
        ped2.setClienteTelefone("(11) 98877-3344");
        ped2.setEnderecoEntrega("Av. Dom Pedro II, 1200 - Centro");
        ped2.setItensDescricao("2x Pão de Queijo Mineiro\n1x Café Expresso");
        ped2.setValorTotal(new BigDecimal("13.00"));
        ped2.setTaxaEntrega(new BigDecimal("4.00"));
        ped2.setFormaPagamento("Cartão na Entrega (Débito)");
        ped2.setStatus(StatusDelivery.SAIU_PARA_ENTREGA);
        ped2.setMotoboy(moto1);
        ped2.setSaiuComMaquina(true);
        ped2.setObservacao("Cliente solicitou máquina de cartão");
        ped2.setDataHora(LocalDateTime.now().minusMinutes(35));

        pedidoDeliveryRepository.saveAll(Arrays.asList(ped1, ped2));
        log.info("Motoboys e pedidos de delivery iniciais provisionados.");
    }

    @Transactional
    public void resetDatabase() {
        log.info("Iniciando reset do banco de dados para estado de fábrica...");

        try {
            removerCheckConstraintsUsuarios();

            // 1. Limpeza em ordem de integridade referencial (filhos primeiro)
            entityManager.createNativeQuery("DELETE FROM itens_venda").executeUpdate();
            entityManager.createNativeQuery("DELETE FROM vendas").executeUpdate();
            entityManager.createNativeQuery("DELETE FROM movimentos_caixa").executeUpdate();
            entityManager.createNativeQuery("DELETE FROM caixas").executeUpdate();
            entityManager.createNativeQuery("DELETE FROM pedidos_delivery").executeUpdate();
            entityManager.createNativeQuery("DELETE FROM motoboys").executeUpdate();
            entityManager.createNativeQuery("DELETE FROM produtos").executeUpdate();
            entityManager.createNativeQuery("DELETE FROM categorias").executeUpdate();

            entityManager.clear();

            // 2. Provisiona os 3 usuários padrão
            garantirTresUsuariosPadrao();

            // 3. Remove outros usuários mantendo apenas admin, gestor e operador
            for (Usuario u : usuarioRepository.findAll()) {
                if (!"admin".equals(u.getUsername()) && !"operador".equals(u.getUsername()) && !"gestor".equals(u.getUsername())) {
                    usuarioRepository.delete(u);
                }
            }

            // 4. Cadastra catálogo inicial
            inicializarCatalogoPadrao();

            // 5. Cadastra motoboys e pedidos de exemplo
            inicializarMotoboysEDelivery();

            // 6. Restaura parâmetros fiscais da empresa
            inicializarEmpresaUtilizadoraPadrao();

            log.info("Reset do banco de dados concluído com sucesso.");
        } catch (Exception e) {
            log.error("Erro fatal ao resetar banco de dados: ", e);
            throw new RuntimeException("Falha ao resetar banco de dados: " + e.getMessage(), e);
        }
    }
}
