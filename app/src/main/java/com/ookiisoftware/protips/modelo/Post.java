package com.ookiisoftware.protips.modelo;

import android.app.Activity;
import android.net.Uri;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.database.DatabaseReference;
import com.ookiisoftware.protips.R;
import com.ookiisoftware.protips.adapter.PostAdapter;
import com.ookiisoftware.protips.auxiliar.Const;
import com.ookiisoftware.protips.auxiliar.Criptografia;
import com.ookiisoftware.protips.auxiliar.Import;
import com.ookiisoftware.protips.auxiliar.notification.MyNotificationManager;

import java.util.Comparator;
import java.util.HashMap;

public class Post {

    //region Variáveis
    private static final String TAG = "Post";

    private String id;
    private String id_tipster;
    private String titulo;
    private String link;
    private String descricao;
    private String texto;
    private String foto;
    private String odd_maxima;
    private String odd_minima;
    private String odd_atual;
    private String unidade;
    private String horario_maximo;
    private String horario_minimo;
    private String data;
    private String esporte;
    private String linha;
    private String mercado;
    private String campeonato;
    private boolean publico;

    private HashMap<String,String> bom, ruim;
    //endregion

    public Post() {
        bom = new HashMap<>();
        ruim = new HashMap<>();
    }

    //region Métodos

    public void postar(final Activity activity, final ProgressBar progressBar, boolean isFotoLocal) {
        if (isFotoLocal) {
            try {
                Import.getFirebase.getStorage()
                        .child(Const.firebase.child.POSTES)
                        .child(getId())
                        .putFile(Uri.parse(getFoto()))
                        .addOnSuccessListener(taskSnapshot -> {
                            if (taskSnapshot.getMetadata() != null && taskSnapshot.getMetadata().getReference() != null)
                                taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnCompleteListener(task -> {
                                    if (task.getResult() != null) {
                                        setFoto(task.getResult().toString());
                                        postar(activity);
                                    }
                                });
                        }).addOnFailureListener(e -> {
                    Import.Alert.snakeBar(activity, activity.getResources().getString(R.string.post_erro));
                    progressBar.setVisibility(View.GONE);
                });

            } catch (Exception e) {
                Import.Alert.snakeBar(activity, activity.getResources().getString(R.string.post_erro));
                Import.Alert.e(TAG, "", e);
            }
        } else {
            postar(activity);
        }
    }

    private void postar(final Activity activity) {
        String id = getId_tipster() == null ? Import.getFirebase.getId() : getId_tipster();
        Import.getFirebase.getReference()
                .child(Const.firebase.child.USUARIO)
                .child(id)
                .child(Const.firebase.child.POSTES)
                .child(Criptografia.criptografar(getData()))
                .setValue(this)
                .addOnSuccessListener(aVoid -> {
                    for (User user : Import.get.seguidores.getAll()) {
                        MyNotificationManager.getInstance(activity).sendNewPost(Post.this, user);
                    }

                    activity.finish();
                })
                .addOnFailureListener(e -> {
                    Import.Alert.snakeBar(activity, activity.getResources().getString(R.string.post_erro));
                });

        Import.get.seguindo.add(this);
        Import.getFirebase.getTipster().getPostes().put(getId(), this);
    }

    public void addBom(String id) {
        Import.getFirebase.getReference()
                .child(Const.firebase.child.USUARIO)
                .child(getId_tipster())
                .child(Const.firebase.child.POSTES)
                .child(Criptografia.criptografar(getData()))
                .child(Const.firebase.child.BOM)
                .child(id)
                .setValue(id);
        removeRuim(id);
        if (!getBom().containsValue(id)) {
            getBom().put(id, id);
            getRuim().remove(id);
        }
    }

    public void addRuim(String id) {
        Import.getFirebase.getReference()
                .child(Const.firebase.child.USUARIO)
                .child(getId_tipster())
                .child(Const.firebase.child.POSTES)
                .child(Criptografia.criptografar(getData()))
                .child(Const.firebase.child.RUIM)
                .child(id)
                .setValue(id);
        removeBom(id);
        if (!getRuim().containsValue(id)) {
            getRuim().put(id, id);
            getBom().remove(id);
        }
    }

    public void removeBom(String id) {
        Import.getFirebase.getReference()
                .child(Const.firebase.child.USUARIO)
                .child(getId_tipster())
                .child(Const.firebase.child.POSTES)
                .child(Criptografia.criptografar(getData()))
                .child(Const.firebase.child.BOM)
                .child(id)
                .removeValue();
        getBom().remove(id);
    }

    public void removeRuim(String id) {
        Import.getFirebase.getReference()
                .child(Const.firebase.child.USUARIO)
                .child(getId_tipster())
                .child(Const.firebase.child.POSTES)
                .child(Criptografia.criptografar(getData()))
                .child(Const.firebase.child.RUIM)
                .child(id)
                .removeValue();
        getRuim().remove(id);
    }

    public void excluir(final PostAdapter adapter) {
        String id = getId_tipster();
        DatabaseReference ref = Import.getFirebase.getReference()
                .child(Const.firebase.child.USUARIO)
                .child(id)
                .child(Const.firebase.child.POSTES);

        ref.child(Criptografia.criptografar(getData()))
                .removeValue()
                .addOnSuccessListener(aVoid -> {

                    Import.getFirebase.getStorage()
                            .child(Const.firebase.child.POSTES)
                            .child(getId()).delete();

                    Import.get.seguindo.remove(this);
                    Import.getFirebase.getTipster().getPostes().remove(getId());
                    adapter.notifyDataSetChanged();
        });
    }

    //endregion

    //region gets sets

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId_tipster() {
        return id_tipster;
    }

    public void setId_tipster(String id_tipster) {
        this.id_tipster = id_tipster;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public HashMap<String,String> getBom() {
        if (bom == null)
            bom = new HashMap<>();
        return bom;
    }

    public void setBom(HashMap<String,String> bom) {
        this.bom = bom;
    }

    public HashMap<String,String> getRuim() {
        if (ruim == null)
            ruim = new HashMap<>();
        return ruim;
    }

    public void setRuim(HashMap<String,String> ruim) {
        this.ruim = ruim;
    }

    public String getOdd_atual() {
        return odd_atual;
    }

    public void setOdd_atual(String odd_atual) {
        this.odd_atual = odd_atual;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public String getCampeonato() {
        return campeonato;
    }

    public void setCampeonato(String campeonato) {
        this.campeonato = campeonato;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getOdd_maxima() {
        return odd_maxima;
    }

    public void setOdd_maxima(String odd_maxima) {
        this.odd_maxima = odd_maxima;
    }

    public String getOdd_minima() {
        return odd_minima;
    }

    public void setOdd_minima(String odd_minima) {
        this.odd_minima = odd_minima;
    }

    public String getHorario_maximo() {
        return horario_maximo;
    }

    public void setHorario_maximo(String horario_maximo) {
        this.horario_maximo = horario_maximo;
    }

    public String getHorario_minimo() {
        return horario_minimo;
    }

    public void setHorario_minimo(String horario_minimo) {
        this.horario_minimo = horario_minimo;
    }

    public String getEsporte() {
        return esporte;
    }

    public void setEsporte(String esporte) {
        this.esporte = esporte;
    }

    public String getLinha() {
        return linha;
    }

    public void setLinha(String linha) {
        this.linha = linha;
    }

    public boolean isPublico() {
        return publico;
    }

    public void setPublico(boolean publico) {
        this.publico = publico;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    //Use getDescricao
    @Deprecated
    public String getTexto() {
        return texto;
    }

    //Use setDescricao
    @Deprecated
    public void setTexto(String texto) {
        this.texto = texto;
        if (descricao == null || descricao.isEmpty())
            descricao = texto;
    }

    //Use getLinha
    @Deprecated
    public String getMercado() {
        return mercado;
    }

    //Use setLinha
    @Deprecated
    public void setMercado(String mercado) {
        this.mercado = mercado;
        if (linha == null || linha.isEmpty())
            linha = mercado;
    }

    //endregion

    public static class sortByDate implements Comparator<Post> {
        public int compare(Post left, Post right) {
            return right.getData().compareTo(left.getData());
        }
    }
}
