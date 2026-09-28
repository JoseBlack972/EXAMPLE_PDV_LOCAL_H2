package com.examplepdv.service;

import com.examplepdv.model.EmpresaUtilizadora;
import com.examplepdv.repository.EmpresaUtilizadoraRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmpresaUtilizadoraService {

    private final EmpresaUtilizadoraRepository empresaRepository;

    public EmpresaUtilizadoraService(EmpresaUtilizadoraRepository empresaRepository) {
        this.empresaRepository = empresaRepository;
    }

    @PostConstruct
    @Transactional
    public void inicializarEmpresaPadrao() {
        if (!empresaRepository.existsById(1L)) {
            EmpresaUtilizadora padrao = new EmpresaUtilizadora();
            padrao.setId(1L);
            empresaRepository.save(padrao);
        }
    }

    @Transactional(readOnly = true)
    public EmpresaUtilizadora obterEmpresa() {
        return empresaRepository.findById(1L).orElseGet(() -> {
            EmpresaUtilizadora nova = new EmpresaUtilizadora();
            nova.setId(1L);
            return empresaRepository.save(nova);
        });
    }

    @Transactional
    public EmpresaUtilizadora salvarEmpresa(EmpresaUtilizadora dados) {
        dados.setId(1L);
        return empresaRepository.save(dados);
    }

    @Transactional
    public EmpresaUtilizadora atualizarIdentidadeVisual(String nomeFantasia, String iconeBootstrap, String logoUrl) {
        EmpresaUtilizadora empresa = obterEmpresa();
        if (nomeFantasia != null && !nomeFantasia.isBlank()) {
            empresa.setNomeFantasia(nomeFantasia);
        }
        if (iconeBootstrap != null && !iconeBootstrap.isBlank()) {
            empresa.setIconeBootstrap(iconeBootstrap);
        }
        if (logoUrl != null) {
            empresa.setLogoUrl(logoUrl.trim());
        }
        return empresaRepository.save(empresa);
    }
}
