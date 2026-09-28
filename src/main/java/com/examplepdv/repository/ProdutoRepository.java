package com.examplepdv.repository;

import com.examplepdv.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    List<Produto> findByAtivoTrueOrderByNomeAsc();
    Optional<Produto> findByCodigoBarrasAndAtivoTrue(String codigoBarras);
    Optional<Produto> findByCodigoBarras(String codigoBarras);
    List<Produto> findByNomeContainingIgnoreCaseAndAtivoTrue(String nome);
    boolean existsByCodigoBarras(String codigoBarras);

    @Query("SELECT p FROM Produto p WHERE p.ativo = true AND (" +
           "LOWER(p.nome) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
           "p.codigoBarras LIKE CONCAT('%', :termo, '%')) " +
           "ORDER BY CASE WHEN p.codigoBarras = :termo THEN 0 " +
           "WHEN LOWER(p.nome) = LOWER(:termo) THEN 1 " +
           "WHEN LOWER(p.nome) LIKE LOWER(CONCAT(:termo, '%')) THEN 2 " +
           "ELSE 3 END, p.nome ASC")
    List<Produto> buscarPorNomeOuCodigoBarras(@Param("termo") String termo);
}
