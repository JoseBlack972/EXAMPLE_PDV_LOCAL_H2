package com.examplepdv.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ImportacaoCsvDTO implements Serializable {

    private int totalLinhas = 0;
    private int criados = 0;
    private int atualizados = 0;
    private int falhas = 0;
    private List<String> mensagens = new ArrayList<>();
    private List<String> erros = new ArrayList<>();

    public ImportacaoCsvDTO() {}

    public void incrementarCriados() {
        this.criados++;
    }

    public void incrementarAtualizados() {
        this.atualizados++;
    }

    public void incrementarFalhas() {
        this.falhas++;
    }

    public void adicionarMensagem(String msg) {
        this.mensagens.add(msg);
    }

    public void adicionarErro(String erro) {
        this.erros.add(erro);
    }

    public int getTotalLinhas() {
        return totalLinhas;
    }

    public void setTotalLinhas(int totalLinhas) {
        this.totalLinhas = totalLinhas;
    }

    public int getCriados() {
        return criados;
    }

    public void setCriados(int criados) {
        this.criados = criados;
    }

    public int getAtualizados() {
        return atualizados;
    }

    public void setAtualizados(int atualizados) {
        this.atualizados = atualizados;
    }

    public int getFalhas() {
        return falhas;
    }

    public void setFalhas(int falhas) {
        this.falhas = falhas;
    }

    public List<String> getMensagens() {
        return mensagens;
    }

    public void setMensagens(List<String> mensagens) {
        this.mensagens = mensagens;
    }

    public List<String> getErros() {
        return erros;
    }

    public void setErros(List<String> erros) {
        this.erros = erros;
    }
}
