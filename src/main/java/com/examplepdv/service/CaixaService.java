package com.examplepdv.service;

import com.examplepdv.dto.FechamentoCaixaDTO;
import com.examplepdv.dto.MovimentoCaixaDTO;
import com.examplepdv.model.*;
import com.examplepdv.repository.CaixaRepository;
import com.examplepdv.repository.MovimentoCaixaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CaixaService {

    private final CaixaRepository caixaRepository;
    private final MovimentoCaixaRepository movimentoCaixaRepository;

    public CaixaService(CaixaRepository caixaRepository, MovimentoCaixaRepository movimentoCaixaRepository) {
        this.caixaRepository = caixaRepository;
        this.movimentoCaixaRepository = movimentoCaixaRepository;
    }

    public Optional<Caixa> obterCaixaAberto() {
        return caixaRepository.findFirstByStatusOrderByIdDesc(StatusCaixa.ABERTO);
    }

    public List<Caixa> listarTodos() {
        return caixaRepository.findAllByOrderByIdDesc();
    }

    public Optional<Caixa> buscarPorId(Long id) {
        return caixaRepository.findById(id);
    }

    @Transactional
    public Caixa abrirCaixa(Usuario usuario, BigDecimal saldoInicial, String observacao) {
        if (obterCaixaAberto().isPresent()) {
            throw new IllegalStateException("Já existe um caixa aberto no momento!");
        }

        Caixa caixa = new Caixa(usuario, saldoInicial != null ? saldoInicial : BigDecimal.ZERO, observacao);
        caixa = caixaRepository.save(caixa);

        if (saldoInicial != null && saldoInicial.compareTo(BigDecimal.ZERO) > 0) {
            MovimentoCaixa movimento = new MovimentoCaixa(
                    caixa,
                    TipoMovimentoCaixa.SUPRIMENTO,
                    saldoInicial,
                    "Saldo inicial de abertura de caixa"
            );
            movimentoCaixaRepository.save(movimento);
        }

        return caixa;
    }

    @Transactional
    public Caixa fecharCaixa(FechamentoCaixaDTO dto) {
        Caixa caixa = obterCaixaAberto()
                .orElseThrow(() -> new IllegalStateException("Nenhum caixa aberto para fechar!"));

        caixa.setStatus(StatusCaixa.FECHADO);
        caixa.setDataFechamento(LocalDateTime.now());
        caixa.setSaldoFinal(dto.getSaldoFinalInformado());
        if (dto.getObservacao() != null && !dto.getObservacao().trim().isEmpty()) {
            caixa.setObservacao(
                    (caixa.getObservacao() != null ? caixa.getObservacao() + " | " : "") +
                    "Fechamento: " + dto.getObservacao()
            );
        }

        return caixaRepository.save(caixa);
    }

    @Transactional
    public MovimentoCaixa registrarMovimento(MovimentoCaixaDTO dto) {
        Caixa caixa = obterCaixaAberto()
                .orElseThrow(() -> new IllegalStateException("Nenhum caixa aberto para registrar movimentação!"));

        if (dto.getValor() == null || dto.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor do movimento deve ser maior que zero!");
        }

        if (dto.getTipo() == TipoMovimentoCaixa.SANGRIA) {
            BigDecimal saldoAtual = caixa.getSaldoDinheiroEsperado();
            if (dto.getValor().compareTo(saldoAtual) > 0) {
                throw new IllegalArgumentException("Saldo insuficiente em dinheiro no caixa para esta sangria! Disponível: R$ " + saldoAtual);
            }
            caixa.setTotalSangrias(caixa.getTotalSangrias().add(dto.getValor()));
        } else if (dto.getTipo() == TipoMovimentoCaixa.SUPRIMENTO) {
            caixa.setTotalSuprimentos(caixa.getTotalSuprimentos().add(dto.getValor()));
        }

        caixaRepository.save(caixa);

        MovimentoCaixa movimento = new MovimentoCaixa(
                caixa,
                dto.getTipo(),
                dto.getValor(),
                dto.getMotivo()
        );

        return movimentoCaixaRepository.save(movimento);
    }

    public List<MovimentoCaixa> listarMovimentos(Caixa caixa) {
        return movimentoCaixaRepository.findByCaixaOrderByDataHoraDesc(caixa);
    }
}
