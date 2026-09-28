package com.examplepdv.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        Model model) {
        if (error != null) {
            model.addAttribute("mensagemErro", "Usuário ou senha inválidos!");
        }
        if (logout != null) {
            model.addAttribute("mensagemSucesso", "Você foi desconectado com sucesso.");
        }
        return "login";
    }

    @GetMapping("/acesso-negado")
    public String acessoNegado(Model model) {
        model.addAttribute("mensagemErro", "Você não tem permissão para acessar este recurso.");
        return "acesso-negado";
    }
}
