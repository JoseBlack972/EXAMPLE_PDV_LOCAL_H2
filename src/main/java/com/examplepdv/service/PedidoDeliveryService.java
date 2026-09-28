package com.examplepdv.service;

import com.examplepdv.model.Motoboy;
import com.examplepdv.model.PedidoDelivery;
import com.examplepdv.model.StatusDelivery;
import com.examplepdv.repository.PedidoDeliveryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoDeliveryService {

    private final PedidoDeliveryRepository pedidoDeliveryRepository;
    private final MotoboyService motoboyService;

    public PedidoDeliveryService(PedidoDeliveryRepository pedidoDeliveryRepository, MotoboyService motoboyService) {
        this.pedidoDeliveryRepository = pedidoDeliveryRepository;
        this.motoboyService = motoboyService;
    }

    public List<PedidoDelivery> listarTodos() { return pedidoDeliveryRepository.findAllByOrderByIdDesc(); }
    public Optional<PedidoDelivery> buscarPorId(Long id) { return pedidoDeliveryRepository.findById(id); }

    @Transactional
    public PedidoDelivery salvar(PedidoDelivery pedido) {
        if (pedido.getDataHora() == null) {
            pedido.setDataHora(LocalDateTime.now());
        }
        return pedidoDeliveryRepository.save(pedido);
    }

    @Transactional
    public PedidoDelivery atualizarStatus(Long id, StatusDelivery novoStatus) {
        PedidoDelivery pedido = pedidoDeliveryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado ID: " + id));
        pedido.setStatus(novoStatus);
        return pedidoDeliveryRepository.save(pedido);
    }

    @Transactional
    public PedidoDelivery despacharPedido(Long id, Long motoboyId, boolean saiuComMaquina) {
        PedidoDelivery pedido = pedidoDeliveryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado ID: " + id));
        Motoboy motoboy = motoboyService.buscarPorId(motoboyId)
                .orElseThrow(() -> new IllegalArgumentException("Motoboy não encontrado ID: " + motoboyId));
        pedido.setMotoboy(motoboy);
        pedido.setSaiuComMaquina(saiuComMaquina);
        pedido.setStatus(StatusDelivery.SAIU_PARA_ENTREGA);
        return pedidoDeliveryRepository.save(pedido);
    }
}
