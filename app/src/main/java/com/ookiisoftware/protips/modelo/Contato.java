package com.ookiisoftware.protips.modelo;

public class Contato {
    private String id, nome, email, image_uri;
    private int categoria;

    public Contato() {}

    public Contato(String id, String nome, String email, String image_uri, int categoria) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.image_uri = image_uri;
        this.categoria = categoria;
    }

    public static Usuario converterParaUsuario(Contato contato){
        Usuario usuario = new Usuario();
        usuario.setId(contato.getId());
        usuario.setNome(contato.getNome());
        usuario.setEmail(contato.getEmail());
//        usuario.setCategoria(contato.getCategoria());
        contato.setImage_uri(usuario.getFoto());
        return usuario;
    }

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

    public String getImage_uri() {
        return image_uri;
    }

    public void setImage_uri(String image_uri) {
        this.image_uri = image_uri;
    }

    public int getCategoria() {
        return categoria;
    }

    public void setCategoria(int categoria) {
        this.categoria = categoria;
    }
}
