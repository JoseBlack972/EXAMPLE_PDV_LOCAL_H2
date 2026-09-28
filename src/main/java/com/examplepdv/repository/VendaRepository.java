package com.examplepdv.repository;

import com.examplepdv.model.Caixa;
import com.examplepdv.model.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VendaRepository extends JpaRepository<Venda, Long> {
    List<Venda> findByCaixaOrderByIdDesc(Caixa caixa);
    List<Venda> findAllByOrderByIdDesc();
    List<Venda> findByDataHoraBetweenOrderByIdDesc(LocalDateTime inicio, LocalDateTime fim);
    List<Venda> findByDataHoraBetweenOrderByDataHoraAsc(LocalDateTime inicio, LocalDateTime fim);
}
