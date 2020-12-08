package com.ookiisoftware.protips.modelo;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.ookiisoftware.protips.R;
import com.ookiisoftware.protips.auxiliar.Const;
import com.ookiisoftware.protips.auxiliar.Import;
import com.ookiisoftware.protips.auxiliar.notification.MyNotificationManager;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class User {

    //region Variaveis
    private UserDados dados;

    private HashMap<String, String> seguidores;
    private HashMap<String, String> seguindo;
    private HashMap<String, String> seguidoresPendentes;

    private HashMap<String, Post> postes;
    private HashMap<String, PostPerfil> post_perfil;

    private String postsPublicas;

    //endregion

    public User() {
        seguindo = new HashMap<>();
        seguidores = new HashMap<>();
        post_perfil = new HashMap<>();
        seguidoresPendentes = new HashMap<>();
        postes = new HashMap<>();
        dados = new UserDados();
    }
    public User(User user) {
        seguindo = new HashMap<>(user.getSeguindo());
        seguidores = new HashMap<>(user.getSeguidores());
        post_perfil = new HashMap<>(user.getPost_perfil());
        seguidoresPendentes = new HashMap<>(user.getSeguidoresPendentes());
        postes = new HashMap<>(user.getPostes());
        dados = new UserDados();

        UserDados u = user.getDados();
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

    public void salvarToken() {
        Import.getFirebase.getReference()
                .child(Const.firebase.child.USUARIO)
                .child(getDados().getId())
                .child(Const.firebase.child.DADOS)
                .child(Const.firebase.child.TOKENS)
                .setValue(getDados().getToken());
    }

    public void logout() {
        Import.getFirebase.getReference()
                .child(Const.firebase.child.USUARIO)
                .child(getDados().getId())
                .child(Const.firebase.child.TOKENS)
                .child(Import.getFirebase.getToken())
                .removeValue();
    }

    public void solicitarSerTipster() {
        Import.getFirebase.getReference()
                .child(Const.firebase.child.SOLICITACAO_NOVO_TIPSTER)
                .child(getDados().getId())
                .setValue(getDados().getTipname());

        habilitarTipster(true);

        getDados().setTipster(true);
        bloquear();
    }

    public void habilitarTipster(boolean b) {
        Import.getFirebase.getReference()
                .child(Const.firebase.child.USUARIO)
                .child(getDados().getId())
                .child(Const.firebase.child.DADOS)
                .child(Const.firebase.child.TIPSTER)
                .setValue(b);

        getDados().setTipster(b);
        desbloquear();
    }

    public void solicitarSerTipsterCancelar() {
        Import.getFirebase.getReference()
                .child(Const.firebase.child.SOLICITACAO_NOVO_TIPSTER)
                .child(getDados().getId())
                .removeValue();

        habilitarTipster(false);
        getDados().setTipster(false);
        desbloquear();
    }

    public void salvar(Activity activity) {
        DatabaseReference reference = Import.getFirebase.getReference();
        reference
                .child(Const.firebase.child.USUARIO)
                .child(getDados().getId())
                .setValue(this)
                .addOnSuccessListener(aVoid -> {
                    Import.Alert.snakeBar(activity, activity.getResources().getString(R.string.msg_dados_salvos));
                })
                .addOnFailureListener(e -> {
                    Import.Alert.snakeBar(activity, activity.getResources().getString(R.string.erro_salvar_dados));
                });

        reference
                .child(Const.firebase.child.IDENTIFICADOR)
                .child(getDados().getTipname())
                .setValue(getDados().getId());
    }

    public void addSolicitacao(Context context, User user) {
        String id = user.getDados().getId();
        Import.getFirebase.getReference()
                .child(Const.firebase.child.USUARIO)
                .child(getDados().getId())
                .child(Const.firebase.child.SEGUIDORES_PENDENTES)
                .child(id)
                .setValue(id);

        MyNotificationManager.getInstance(context).sendSolicitacao(this);
    }

    public void removerSolicitacao(String id) {
        Import.getFirebase.getReference()
                .child(Const.firebase.child.USUARIO)
                .child(getDados().getId())
                .child(Const.firebase.child.SEGUIDORES_PENDENTES)
                .child(id)
                .removeValue();
    }

    private void addSeguindo(String id) {
        Import.getFirebase.getReference()
                .child(Const.firebase.child.USUARIO)
                .child(getDados().getId())
                .child(Const.firebase.child.SEGUINDO)
                .child(id)
                .setValue(id);
    }

    private void removerSeguindo(String id) {
        Import.getFirebase.getReference()
                .child(Const.firebase.child.USUARIO)
                .child(getDados().getId())
                .child(Const.firebase.child.SEGUINDO)
                .child(id)
                .removeValue();

        getSeguindo().remove(id);
    }

    public void aceitarSeguidor(Context context, @NonNull User user) {
        String id = user.getDados().getId();
        Import.getFirebase.getReference()
                .child(Const.firebase.child.USUARIO)
                .child(getDados().getId())
                .child(Const.firebase.child.SEGUIDORES)
                .child(id)
                .setValue(id);

        user.addSeguindo(Import.getFirebase.getId());
        getSeguidores().put(id, id);
        removerSolicitacao(id);
        MyNotificationManager.getInstance(context).sendSolicitacaoAceita(user);
    }

    public void removerSeguidor(@NonNull User punter) {
        String id = punter.getDados().getId();
        Import.getFirebase.getReference()
                .child(Const.firebase.child.USUARIO)
                .child(getDados().getId())
                .child(Const.firebase.child.SEGUIDORES)
                .child(id)
                .removeValue();

        punter.removerSeguindo(getDados().getId());
        getSeguidores().remove(id);
    }

    public void excluir() {
        Import.getFirebase.getReference()
                .child(Const.firebase.child.USUARIO)
                .child(getDados().getId())
                .removeValue();
    }

    public void bloquear() {
        Import.getFirebase.getReference()
                .child(Const.firebase.child.USUARIO)
                .child(getDados().getId())
                .child(Const.firebase.child.DADOS)
                .child(Const.firebase.child.BLOQUEADO)
                .setValue(true);
        getDados().setBloqueado(true);
    }

    public void desbloquear() {
        Import.getFirebase.getReference()
                .child(Const.firebase.child.USUARIO)
                .child(getDados().getId())
                .child(Const.firebase.child.DADOS)
                .child(Const.firebase.child.BLOQUEADO)
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

    public List<Post> getPostes(@NonNull String data, boolean publucas) {
        List<Post> items = new LinkedList<>();
        for (Post item : getPostes().values()) {
            if (item.getData().contains(data)) {
                if (publucas) {
                    if (item.isPublico())
                        items.add(item);
                } else
                    items.add(item);
            }
        }
        return items;
    }

    //endregion

    //region gets sets

    public UserDados getDados() {
        return dados;
    }

    public void setDados(UserDados dados) {
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

    public String getPostsPublicas() {
        return postsPublicas;
    }

    public void setPostsPublicas(String postsPublicas) {
        this.postsPublicas = postsPublicas;
    }

    //endregion

    //region sortMethods

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

    //endregion

}
