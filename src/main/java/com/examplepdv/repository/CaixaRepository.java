package com.examplepdv.repository;

import com.examplepdv.model.Caixa;
import com.examplepdv.model.StatusCaixa;
import com.examplepdv.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CaixaRepository extends JpaRepository<Caixa, Long> {
    Optional<Caixa> findFirstByStatusOrderByIdDesc(StatusCaixa status);
    Optional<Caixa> findFirstByUsuarioAndStatusOrderByIdDesc(Usuario usuario, StatusCaixa status);
    List<Caixa> findAllByOrderByIdDesc();
    List<Caixa> findByDataAberturaBetweenOrderByDataAberturaDesc(LocalDateTime inicio, LocalDateTime fim);
}
