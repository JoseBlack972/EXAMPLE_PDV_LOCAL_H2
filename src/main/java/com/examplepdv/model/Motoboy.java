package com.examplepdv.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

@Entity
@Table(name = "motoboys")
public class Motoboy implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome do motoboy é obrigatório")
    @Column(nullable = false)
    private String nome;

    @NotBlank(message = "Telefone é obrigatório")
    @Column(nullable = false)
    private String telefone;

    private String placaMoto;

    private boolean ativo = true;

    public Motoboy() {}

    public Motoboy(String nome, String telefone, String placaMoto) {
        this.nome = nome;
        this.telefone = telefone;
        this.placaMoto = placaMoto;
        this.ativo = true;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getPlacaMoto() { return placaMoto; }
    public void setPlacaMoto(String placaMoto) { this.placaMoto = placaMoto; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
}
