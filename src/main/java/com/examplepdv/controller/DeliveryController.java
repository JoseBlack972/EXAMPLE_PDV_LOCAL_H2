package com.examplepdv.controller;

import com.examplepdv.model.*;
import com.examplepdv.service.MotoboyService;
import com.examplepdv.service.PedidoDeliveryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/delivery")
public class DeliveryController {

    private final PedidoDeliveryService pedidoDeliveryService;
    private final MotoboyService motoboyService;

    public DeliveryController(PedidoDeliveryService pedidoDeliveryService,
                              MotoboyService motoboyService) {
        this.pedidoDeliveryService = pedidoDeliveryService;
        this.motoboyService = motoboyService;
    }

    @GetMapping
    public String painelDelivery(Model model) {
        List<PedidoDelivery> pedidos = pedidoDeliveryService.listarTodos();
        List<Motoboy> motoboys = motoboyService.listarAtivos();

        long qtdNovos = pedidos.stream()
                .filter(p -> p.getStatus() == StatusDelivery.NOVO || p.getStatus() == StatusDelivery.EM_PREPARO)
                .count();

        long qtdEmRota = pedidos.stream()
                .filter(p -> p.getStatus() == StatusDelivery.SAIU_PARA_ENTREGA)
                .count();

        long qtdMaquinas = pedidos.stream()
                .filter(p -> p.isSaiuComMaquina() && p.getStatus() == StatusDelivery.SAIU_PARA_ENTREGA)
                .count();

        long qtdEntregues = pedidos.stream()
                .filter(p -> p.getStatus() == StatusDelivery.ENTREGUE)
                .count();

        model.addAttribute("pedidos", pedidos);
        model.addAttribute("motoboys", motoboys);
        model.addAttribute("qtdNovos", qtdNovos);
        model.addAttribute("qtdEmRota", qtdEmRota);
        model.addAttribute("qtdMaquinas", qtdMaquinas);
        model.addAttribute("qtdEntregues", qtdEntregues);
        model.addAttribute("plataformas", PlataformaDelivery.values());
        model.addAttribute("novoPedido", new PedidoDelivery());

        return "delivery/pedidos";
    }

    @PostMapping("/salvar")
    public String salvarPedido(@ModelAttribute PedidoDelivery pedido, RedirectAttributes redirectAttributes) {
        try {
            if (pedido.getCodigoPedido() == null || pedido.getCodigoPedido().trim().isEmpty()) {
                String prefix = pedido.getPlataforma() == PlataformaDelivery.IFOOD ? "IF-" :
                                pedido.getPlataforma() == PlataformaDelivery.NOVENOVE_FOOD ? "99-" : "WPP-";
                pedido.setCodigoPedido(prefix + (System.currentTimeMillis() % 10000));
            }
            if (pedido.getTaxaEntrega() == null) {
                pedido.setTaxaEntrega(BigDecimal.ZERO);
            }
            pedido.setStatus(StatusDelivery.NOVO);
            pedidoDeliveryService.salvar(pedido);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Pedido Delivery #" + pedido.getCodigoPedido() + " registrado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao salvar pedido: " + e.getMessage());
        }
        return "redirect:/delivery";
    }

    @PostMapping("/{id}/status")
    public String atualizarStatus(@PathVariable Long id,
                                  @RequestParam("status") StatusDelivery status,
                                  RedirectAttributes redirectAttributes) {
        try {
            pedidoDeliveryService.atualizarStatus(id, status);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Status do pedido atualizado para: " + status.getDescricao());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", e.getMessage());
        }
        return "redirect:/delivery";
    }

    @PostMapping("/{id}/despachar")
    public String despacharPedido(@PathVariable Long id,
                                  @RequestParam("motoboyId") Long motoboyId,
                                  @RequestParam(value = "saiuComMaquina", defaultValue = "false") boolean saiuComMaquina,
                                  RedirectAttributes redirectAttributes) {
        try {
            pedidoDeliveryService.despacharPedido(id, motoboyId, saiuComMaquina);
            redirectAttributes.addFlashAttribute("mensagemSucesso", 
                    "Pedido despachado! " + (saiuComMaquina ? "⚠️ Saiu com MÁQUINA DE CARTÃO." : "Sem máquina de cartão."));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", e.getMessage());
        }
        return "redirect:/delivery";
    }

    @GetMapping("/motoboys")
    public String listarMotoboys(Model model) {
        model.addAttribute("motoboys", motoboyService.listarTodos());
        model.addAttribute("novoMotoboy", new Motoboy());
        return "delivery/motoboys";
    }

    @PostMapping("/motoboys/salvar")
    public String salvarMotoboy(@ModelAttribute Motoboy motoboy, RedirectAttributes redirectAttributes) {
        try {
            motoboyService.salvar(motoboy);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Motoboy salvo com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", e.getMessage());
        }
        return "redirect:/delivery/motoboys";
    }

    @GetMapping("/motoboys/{id}/status")
    public String alternarStatusMotoboy(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        motoboyService.alternarStatus(id);
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Status do motoboy atualizado!");
        return "redirect:/delivery/motoboys";
    }
}
