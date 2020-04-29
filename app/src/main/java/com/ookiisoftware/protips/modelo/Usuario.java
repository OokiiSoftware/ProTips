package com.ookiisoftware.protips.modelo;

import com.google.firebase.database.Exclude;
import com.ookiisoftware.protips.auxiliar.Constantes;
import com.ookiisoftware.protips.auxiliar.Import;

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
    private boolean bloqueado;

    public Usuario() {
        endereco = new Endereco();
        nascimento = new Data();
    }

    public void atualizarNumero(String numero, boolean isTipster) {
        String child = isTipster ? Constantes.firebase.child.TIPSTERS : Constantes.firebase.child.PUNTERS;
        Import.getFirebase.getReference()
                .child(Constantes.firebase.child.USUARIO)
                .child(child)
                .child(getId())
                .child(Constantes.firebase.child.DADOS)
                .child(Constantes.firebase.child.TELEFONE)
                .setValue(numero);
        setTelefone(numero);
    }

    public enum Categoria {
        Apostador, Tipster
    }

    public Punter toPunter() {
        Punter item = new Punter();
        item.setDados(this);
        return item;
    }

    public Tipster toTipster() {
        Tipster item = new Tipster();
        item.setDados(this);
        return item;
    }

    public void bloquear() {
        Import.getFirebase.getReference()
                .child(Constantes.firebase.child.USUARIO)
                .child(Constantes.firebase.child.PUNTERS)
                .child(getId())
                .child(Constantes.firebase.child.DADOS)
                .child(Constantes.firebase.child.BLOQUEADO)
                .setValue(true);
    }

    public void desbloquear() {
        Import.getFirebase.getReference()
                .child(Constantes.firebase.child.USUARIO)
                .child(Constantes.firebase.child.PUNTERS)
                .child(getId())
                .child(Constantes.firebase.child.DADOS)
                .child(Constantes.firebase.child.BLOQUEADO)
                .setValue(false);
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

    public boolean isBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

    //endregion

}
