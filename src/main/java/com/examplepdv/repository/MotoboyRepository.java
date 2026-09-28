package com.examplepdv.repository;

import com.examplepdv.model.Motoboy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MotoboyRepository extends JpaRepository<Motoboy, Long> {
    List<Motoboy> findByAtivoTrueOrderByNomeAsc();
}
