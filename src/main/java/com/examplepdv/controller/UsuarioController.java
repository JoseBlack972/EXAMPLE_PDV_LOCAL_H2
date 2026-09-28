package com.examplepdv.controller;

import com.examplepdv.model.Perfil;
import com.examplepdv.model.Usuario;
import com.examplepdv.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuarios")
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        return "usuarios/list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("perfis", Perfil.values());
        return "usuarios/form";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("usuario") Usuario usuario,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("perfis", Perfil.values());
            return "usuarios/form";
        }

        try {
            usuarioService.salvar(usuario);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Usuário salvo com sucesso!");
            return "redirect:/usuarios";
        } catch (IllegalArgumentException e) {
            model.addAttribute("perfis", Perfil.values());
            model.addAttribute("mensagemErro", e.getMessage());
            return "usuarios/form";
        }
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return usuarioService.buscarPorId(id)
                .map(user -> {
                    model.addAttribute("usuario", user);
                    model.addAttribute("perfis", Perfil.values());
                    return "usuarios/form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("mensagemErro", "Usuário não encontrado!");
                    return "redirect:/usuarios";
                });
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id,
                          @AuthenticationPrincipal UserDetails userDetails,
                          RedirectAttributes redirectAttributes) {
        try {
            usuarioService.excluir(id, userDetails.getUsername());
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Usuário excluído com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", e.getMessage());
        }
        return "redirect:/usuarios";
    }
}
