package com.examplepdv.model;

import jakarta.persistence.*;

@Entity
@Table(name = "empresa_utilizadora")
public class EmpresaUtilizadora {

    @Id
    private Long id = 1L;

    @Column(nullable = false)
    private String razaoSocial = "EXEMPLO COMÉRCIO VAREJISTA LTDA";

    @Column(nullable = false)
    private String nomeFantasia = "MERCADO EXEMPLO PDV";

    @Column(nullable = false, length = 20)
    private String cnpj = "12.345.678/0001-90";

    @Column(nullable = false, length = 20)
    private String inscricaoEstadual = "123.456.789.111";

    private String inscricaoMunicipal = "987654";

    // Endereço do estabelecimento
    private String logradouro = "Av. das Nações Unidas";
    private String numero = "1000";
    private String complemento = "Sala 1";
    private String bairro = "Centro";
    private String cidade = "Salto";
    private String uf = "SP";
    private String cep = "13320-000";
    private String telefone = "(11) 4028-0000";
    private String email = "fiscal@examplepdv.com.br";

    // Identidade Visual / Whitelabel (Customizável pelo Gestor)
    @Column(length = 50)
    private String iconeBootstrap = "bi-shop"; // Ícone padrão: bi-shop, bi-cart4, bi-bag-check, etc.

    @Lob
    @Column(columnDefinition = "TEXT")
    private String logoUrl = ""; // URL externa ou imagem Base64 do logotipo

    // Configuração Fiscal SEFAZ
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AmbienteSefaz ambienteSefaz = AmbienteSefaz.HOMOLOGACAO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoCertificado tipoCertificado = TipoCertificado.A1;

    private String caminhoCertificado = "/certificados/certificado_a1.pfx";
    private String senhaCertificado = "123456";

    // Código de Segurança do Contribuinte (CSC para QR Code do NFC-e)
    private String cscId = "000001";
    private String cscCodigo = "35A4B298C10F4D3EA78019C842B104F5";

    // Parâmetros de Emissão
    private Integer serieNfce = 1;
    private Long proximoNumeroNfce = 1L;

    public EmpresaUtilizadora() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getInscricaoEstadual() {
        return inscricaoEstadual;
    }

    public void setInscricaoEstadual(String inscricaoEstadual) {
        this.inscricaoEstadual = inscricaoEstadual;
    }

    public String getInscricaoMunicipal() {
        return inscricaoMunicipal;
    }

    public void setInscricaoMunicipal(String inscricaoMunicipal) {
        this.inscricaoMunicipal = inscricaoMunicipal;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIconeBootstrap() {
        return iconeBootstrap != null && !iconeBootstrap.isBlank() ? iconeBootstrap : "bi-shop";
    }

    public void setIconeBootstrap(String iconeBootstrap) {
        this.iconeBootstrap = iconeBootstrap;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public boolean temLogoPersonalizada() {
        return logoUrl != null && !logoUrl.isBlank();
    }

    public AmbienteSefaz getAmbienteSefaz() {
        return ambienteSefaz;
    }

    public void setAmbienteSefaz(AmbienteSefaz ambienteSefaz) {
        this.ambienteSefaz = ambienteSefaz;
    }

    public TipoCertificado getTipoCertificado() {
        return tipoCertificado;
    }

    public void setTipoCertificado(TipoCertificado tipoCertificado) {
        this.tipoCertificado = tipoCertificado;
    }

    public String getCaminhoCertificado() {
        return caminhoCertificado;
    }

    public void setCaminhoCertificado(String caminhoCertificado) {
        this.caminhoCertificado = caminhoCertificado;
    }

    public String getSenhaCertificado() {
        return senhaCertificado;
    }

    public void setSenhaCertificado(String senhaCertificado) {
        this.senhaCertificado = senhaCertificado;
    }

    public String getCscId() {
        return cscId;
    }

    public void setCscId(String cscId) {
        this.cscId = cscId;
    }

    public String getCscCodigo() {
        return cscCodigo;
    }

    public void setCscCodigo(String cscCodigo) {
        this.cscCodigo = cscCodigo;
    }

    public Integer getSerieNfce() {
        return serieNfce;
    }

    public void setSerieNfce(Integer serieNfce) {
        this.serieNfce = serieNfce;
    }

    public Long getProximoNumeroNfce() {
        return proximoNumeroNfce;
    }

    public void setProximoNumeroNfce(Long proximoNumeroNfce) {
        this.proximoNumeroNfce = proximoNumeroNfce;
    }

    public String getEnderecoFormatado() {
        StringBuilder sb = new StringBuilder();
        if (logradouro != null) sb.append(logradouro);
        if (numero != null && !numero.isBlank()) sb.append(", ").append(numero);
        if (complemento != null && !complemento.isBlank()) sb.append(" - ").append(complemento);
        if (bairro != null && !bairro.isBlank()) sb.append(" - ").append(bairro);
        if (cidade != null && !cidade.isBlank()) sb.append(" - ").append(cidade);
        if (uf != null && !uf.isBlank()) sb.append("/").append(uf);
        if (cep != null && !cep.isBlank()) sb.append(" - CEP: ").append(cep);
        return sb.toString();
    }
}
