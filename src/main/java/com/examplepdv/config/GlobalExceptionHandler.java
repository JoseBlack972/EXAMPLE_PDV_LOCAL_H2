package com.examplepdv.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, Model model) {
        log.error("Erro interno capturado na aplicação: ", ex);
        model.addAttribute("message", ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName());
        return "error";
    }
}
