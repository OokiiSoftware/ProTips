package com.ookiisoftware.protips.modelo;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.ookiisoftware.protips.auxiliar.Constantes;
import com.ookiisoftware.protips.auxiliar.Import;

import java.util.Comparator;
import java.util.HashMap;

public class User {

    //region Variaveis
    private Usuario dados;

    private HashMap<String, String> seguidores;
    private HashMap<String, String> seguindo;
    private HashMap<String, String> seguidoresPendentes;

    private HashMap<String, Post> postes;
    private HashMap<String, PostPerfil> post_perfil;
    //endregion

    public User() {
        seguindo = new HashMap<>();
        seguidores = new HashMap<>();
        post_perfil = new HashMap<>();
        seguidoresPendentes = new HashMap<>();
        postes = new HashMap<>();
        dados = new Usuario();
    }
    public User(User user) {
        seguindo = new HashMap<>(user.getSeguindo());
        seguidores = new HashMap<>(user.getSeguidores());
        post_perfil = new HashMap<>(user.getPost_perfil());
        seguidoresPendentes = new HashMap<>(user.getSeguidoresPendentes());
        postes = new HashMap<>(user.getPostes());
        dados = new Usuario();

        Usuario u = user.getDados();
        dados.setBloqueado(u.isBloqueado());
        dados.setTipster(u.isTipster());
        dados.setSenha(u.getSenha());
        dados.setEmail(u.getEmail());
        dados.setInfo(u.getInfo());
        dados.setTelefone(u.getTelefone());
        dados.setNascimento(u.getNascimento());
        dados.setNome(u.getNome());
        dados.setFoto(u.getFoto());
        dados.setTipname(u.getTipname());
        dados.setId(u.getId());
        dados.setEndereco(u.getEndereco());
        dados.setPrivado(u.isPrivado());
    }

    //region Metodos

    public void solicitarSerTipster() {
        Import.getFirebase.getReference()
                .child(Constantes.firebase.child.SOLICITACAO_NOVO_TIPSTER)
                .child(getDados().getId())
                .setValue(getDados().getTipname());

        habilitarTipster(true);

        getDados().setTipster(true);
        bloquear();
    }

    public void habilitarTipster(boolean b) {
        Import.getFirebase.getReference()
                .child(Constantes.firebase.child.USUARIO)
                .child(getDados().getId())
                .child(Constantes.firebase.child.DADOS)
                .child(Constantes.firebase.child.TIPSTER)
                .setValue(b);

        getDados().setTipster(b);
        desbloquear();
    }

    public void solicitarSerTipsterCancelar() {
        Import.getFirebase.getReference()
                .child(Constantes.firebase.child.SOLICITACAO_NOVO_TIPSTER)
                .child(getDados().getId())
                .removeValue();

        habilitarTipster(false);
        getDados().setTipster(false);
        desbloquear();
    }

    public void salvar() {
        DatabaseReference reference = Import.getFirebase.getReference();
        reference
                .child(Constantes.firebase.child.USUARIO)
                .child(getDados().getId())
                .setValue(this);

        reference
                .child(Constantes.firebase.child.IDENTIFICADOR)
                .child(getDados().getTipname())
                .setValue(getDados().getId());
    }

    public void addSolicitacao(String id) {
        Import.getFirebase.getReference()
                .child(Constantes.firebase.child.USUARIO)
                .child(getDados().getId())
                .child(Constantes.firebase.child.SEGUIDORES_PENDENTES)
                .child(id)
                .setValue(id);
    }

    public void removerSolicitacao(String id) {
        Import.getFirebase.getReference()
                .child(Constantes.firebase.child.USUARIO)
                .child(getDados().getId())
                .child(Constantes.firebase.child.SEGUIDORES_PENDENTES)
                .child(id)
                .removeValue();
    }

    private void addSeguindo(String id) {
        Import.getFirebase.getReference()
                .child(Constantes.firebase.child.USUARIO)
                .child(getDados().getId())
                .child(Constantes.firebase.child.SEGUINDO)
                .child(id)
                .setValue(id);
    }

    private void removerSeguindo(String id) {
        Import.getFirebase.getReference()
                .child(Constantes.firebase.child.USUARIO)
                .child(getDados().getId())
                .child(Constantes.firebase.child.SEGUINDO)
                .child(id)
                .removeValue();
    }

    public void aceitarSeguidor(@NonNull User user) {
        String id = user.getDados().getId();
        Import.getFirebase.getReference()
                .child(Constantes.firebase.child.USUARIO)
                .child(getDados().getId())
                .child(Constantes.firebase.child.SEGUIDORES)
                .child(id)
                .setValue(id);

        user.addSeguindo(Import.getFirebase.getId());
        getSeguidores().put(id, id);
        removerSolicitacao(id);
    }

    public void removerSeguidor(@NonNull User punter) {
        String id = punter.getDados().getId();
        Import.getFirebase.getReference()
                .child(Constantes.firebase.child.USUARIO)
                .child(getDados().getId())
                .child(Constantes.firebase.child.SEGUIDORES)
                .child(id)
                .removeValue();

        punter.removerSeguindo(getDados().getId());
        getSeguidores().remove(id);
    }

    public void excluir() {
        Import.getFirebase.getReference()
                .child(Constantes.firebase.child.USUARIO)
                .child(getDados().getId())
                .removeValue();
    }

    public void bloquear() {
        Import.getFirebase.getReference()
                .child(Constantes.firebase.child.USUARIO)
                .child(getDados().getId())
                .child(Constantes.firebase.child.DADOS)
                .child(Constantes.firebase.child.BLOQUEADO)
                .setValue(true);
        getDados().setBloqueado(true);
    }

    public void desbloquear() {
        Import.getFirebase.getReference()
                .child(Constantes.firebase.child.USUARIO)
                .child(getDados().getId())
                .child(Constantes.firebase.child.DADOS)
                .child(Constantes.firebase.child.BLOQUEADO)
                .setValue(false);
        getDados().setBloqueado(false);
    }

    public String media() {
        float media;
        float mediaBom = 0;
        float mediaRuim = 0;
        float bom = 0;
        float ruim = 0;
        for (Post p : getPostes().values()) {
            bom += p.getBom().size();
            ruim += p.getRuim().size();
        }
        if (getPostes().values().size() > 0) {
            mediaBom = bom / getPostes().values().size();
            mediaRuim = ruim / getPostes().values().size();
        }
        media = mediaBom - mediaRuim;
        return "" + media;
    }

    private int bomCount() {
        int count = 0;
        for (Post p : getPostes().values()) {
            count += p.getBom().size();
        }
        return count;
    }
    private int ruimCount() {
        int count = 0;
        for (Post p : getPostes().values()) {
            count += p.getRuim().size();
        }
        return count;
    }

    //endregion

    //region gets sets

    public Usuario getDados() {
        return dados;
    }

    public void setDados(Usuario dados) {
        this.dados = dados;
    }

    public HashMap<String, Post> getPostes() {
        if (postes == null)
            postes = new HashMap<>();
        return postes;
    }

    public void setPostes(HashMap<String, Post> postes) {
        this.postes = postes;
    }

    public HashMap<String, String> getSeguindo() {
        return seguindo;
    }

    public void setSeguindo(HashMap<String, String> seguindo) {
        this.seguindo = seguindo;
    }

    public HashMap<String, String> getSeguidores() {
        if (seguidores == null)
            seguidores = new HashMap<>();
        return seguidores;
    }

    public void setSeguidores(HashMap<String, String> seguidores) {
        this.seguidores = seguidores;
    }

    public HashMap<String,String> getSeguidoresPendentes() {
        if (seguidoresPendentes == null)
            seguidoresPendentes = new HashMap<>();
        return seguidoresPendentes;
    }

    public void setSeguidoresPendentes(HashMap<String,String> seguidoresPendentes) {
        this.seguidoresPendentes = seguidoresPendentes;
    }

    public HashMap<String, PostPerfil> getPost_perfil() {
        if (post_perfil == null)
            post_perfil = new HashMap<>();
        return post_perfil;
    }

    public void setPost_perfil(HashMap<String, PostPerfil> post_perfil) {
        this.post_perfil = post_perfil;
    }

    //endregion

    public static class sortByMedia implements Comparator<User> {
        private boolean reverse;
        public sortByMedia() {}
        public sortByMedia(boolean reverse) {
            this.reverse = reverse;
        }
        public int compare(User left, User right) {
            if (reverse) {
                return left.media().compareTo(right.media());
            } else {
                return right.media().compareTo(left.media());
            }
        }
    }

    public static class sortByNome implements Comparator<User> {
        private boolean reverse;
        public sortByNome(boolean reverse) {
            this.reverse = reverse;
        }
        public int compare(User left, User right) {
            if (reverse) {
                return right.getDados().getNome().compareTo(left.getDados().getNome());
            } else {
                return left.getDados().getNome().compareTo(right.getDados().getNome());
            }
        }
    }

    public static class sortByRed implements Comparator<User> {
        private boolean reverse;
        public sortByRed(boolean reverse) {
            this.reverse = reverse;
        }
        public int compare(User left, User right) {
            if (reverse) {
                return left.ruimCount() - right.ruimCount();
            } else {
                return right.ruimCount() - left.ruimCount();
            }
        }
    }

    public static class sortByGreen implements Comparator<User> {
        private boolean reverse;
        public sortByGreen(boolean reverse) {
            this.reverse = reverse;
        }
        public int compare(User left, User right) {
            if (reverse) {
                return left.bomCount() - right.bomCount();
            } else {
                return right.bomCount() - left.bomCount();
            }
        }
    }

    public static class sortByPostCount implements Comparator<User> {
        private boolean reverse;
        public sortByPostCount(boolean reverse) {
            this.reverse = reverse;
        }
        public int compare(User left, User right) {
            if (reverse) {
                return left.bomCount() - right.bomCount();
            } else {
                return right.bomCount() - left.bomCount();
            }
        }
    }

}
