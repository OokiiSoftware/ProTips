package com.ookiisoftware.protips.modelo;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.ookiisoftware.protips.auxiliar.Constantes;
import com.ookiisoftware.protips.auxiliar.Import;

public class Usuario {

    private String info;
    private String id, nome, email, tipname, senha, foto;
    private int categoria;

    public enum Categoria {
        Apostador, Tipster
    }

    public Apostador toApostador() {
        Apostador item = new Apostador();
        if (getId() != null) item.getDados().setId(getId());
        if (getTipname() != null) item.getDados().setTipname(getTipname());
        if (getEmail() != null) item.getDados().setEmail(getEmail());
        if (getFoto() != null) item.getDados().setFoto(getFoto());
        if (getInfo() != null) item.getDados().setInfo(getInfo());
        if (getNome() != null) item.getDados().setNome(getNome());
        if (getSenha() != null) item.getDados().setSenha(getSenha());
        item.getDados().setCategoria(getCategoria());
        return item;
    }

    public Tipster toTipster() {
        Tipster item = new Tipster();
        if (getId() != null) item.getDados().setId(getId());
        if (getTipname() != null) item.getDados().setTipname(getTipname());
        if (getEmail() != null) item.getDados().setEmail(getEmail());
        if (getFoto() != null) item.getDados().setFoto(getFoto());
        if (getInfo() != null) item.getDados().setInfo(getInfo());
        if (getNome() != null) item.getDados().setNome(getNome());
        if (getSenha() != null) item.getDados().setSenha(getSenha());
        item.getDados().setCategoria(getCategoria());
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
        return info;
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

    public int getCategoria() {
        return categoria;
    }

    public void setCategoria(int categoria) {
        this.categoria = categoria;
    }

    //endregion

}
