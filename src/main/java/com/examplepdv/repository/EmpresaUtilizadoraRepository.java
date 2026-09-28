package com.examplepdv.repository;

import com.examplepdv.model.EmpresaUtilizadora;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpresaUtilizadoraRepository extends JpaRepository<EmpresaUtilizadora, Long> {
}
