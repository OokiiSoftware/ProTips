package com.ookiisoftware.protips.modelo;

import com.google.firebase.database.Exclude;

public class Usuario {

    private String info;
    private String id;
    private String nome;
    private String email;
    private String tipname;
    private String senha;
    private String foto;
    private String telefone;
    private Endereco endereco;
    private Data nascimento;
    private boolean privado;

    public Usuario() {
        endereco = new Endereco();
    }

    public enum Categoria {
        Apostador, Tipster
    }

    public Punter toApostador() {
        Punter item = new Punter();
        item.setDados(this);
//        if (getId() != null) item.getDados().setId(getId());
//        if (getTipname() != null) item.getDados().setTipname(getTipname());
//        if (getEmail() != null) item.getDados().setEmail(getEmail());
//        if (getFoto() != null) item.getDados().setFoto(getFoto());
//        if (getInfo() != null) item.getDados().setInfo(getInfo());
//        if (getNome() != null) item.getDados().setNome(getNome());
//        if (getTelefone() != null) item.getDados().setTelefone(getTelefone());
//        if (getSenha() != null) item.getDados().setSenha(getSenha());
//        item.getDados().setCategoria(getCategoria());
        return item;
    }

    public Tipster toTipster() {
        Tipster item = new Tipster();
        item.setDados(this);
//        if (getId() != null) item.getDados().setId(getId());
//        if (getTipname() != null) item.getDados().setTipname(getTipname());
//        if (getEmail() != null) item.getDados().setEmail(getEmail());
//        if (getFoto() != null) item.getDados().setFoto(getFoto());
//        if (getInfo() != null) item.getDados().setInfo(getInfo());
//        if (getNome() != null) item.getDados().setNome(getNome());
//        if (getSenha() != null) item.getDados().setSenha(getSenha());
//        item.getDados().setCategoria(getCategoria());
        return item;
    }

    //region gets sets

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getInfo() {
        return info == null ? "" : info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getTipname() {
        return tipname;
    }

    public void setTipname(String tipname) {
        this.tipname = tipname;
    }

    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public boolean isPrivado() {
        return privado;
    }

    public void setPrivado(boolean privado) {
        this.privado = privado;
    }

    public Data getNascimento() {
        return nascimento;
    }

    public void setNascimento(Data nascimento) {
        this.nascimento = nascimento;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    //endregion

}
