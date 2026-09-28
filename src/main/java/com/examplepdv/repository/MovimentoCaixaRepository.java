package com.examplepdv.repository;

import com.examplepdv.model.Caixa;
import com.examplepdv.model.MovimentoCaixa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimentoCaixaRepository extends JpaRepository<MovimentoCaixa, Long> {
    List<MovimentoCaixa> findByCaixaOrderByDataHoraDesc(Caixa caixa);
    List<MovimentoCaixa> findByDataHoraBetweenOrderByDataHoraDesc(LocalDateTime inicio, LocalDateTime fim);
}
