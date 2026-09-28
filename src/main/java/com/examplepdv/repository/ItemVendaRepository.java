package com.examplepdv.repository;

import com.examplepdv.model.ItemVenda;
import com.examplepdv.model.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemVendaRepository extends JpaRepository<ItemVenda, Long> {
    List<ItemVenda> findByVenda(Venda venda);
}
