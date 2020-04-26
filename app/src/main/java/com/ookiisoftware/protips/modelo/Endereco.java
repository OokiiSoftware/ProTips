package com.ookiisoftware.protips.modelo;

public class Endereco {

    private int Pais;
    private int estado;
    private String cidade;
    private String bairro;
    private String rua;
    private String numero;
    private String cep;

    public Endereco() {}
    public Endereco(Endereco endereco) {
        setPais(endereco.getPais());
        setEstado(endereco.getEstado());
        setCidade(endereco.getCidade());
        setBairro(endereco.getBairro());
        setRua(endereco.getRua());
        setCep(endereco.getCep());
    }

    public void setEndereco(Endereco endereco) {
        setPais(endereco.getPais());
        setEstado(endereco.getEstado());
        setCidade(endereco.getCidade());
        setBairro(endereco.getBairro());
        setRua(endereco.getRua());
        setCep(endereco.getCep());
    }

    public int getPais() {
        return Pais;
    }

    public void setPais(int pais) {
        Pais = pais;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }
}
