package com.examplepdv.repository;

import com.examplepdv.model.PedidoDelivery;
import com.examplepdv.model.StatusDelivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PedidoDeliveryRepository extends JpaRepository<PedidoDelivery, Long> {
    List<PedidoDelivery> findAllByOrderByIdDesc();
    List<PedidoDelivery> findByStatusOrderByIdDesc(StatusDelivery status);
}
