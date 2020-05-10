package com.ookiisoftware.protips.modelo;

import android.app.Activity;
import android.net.Uri;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.database.DatabaseReference;
import com.ookiisoftware.protips.R;
import com.ookiisoftware.protips.adapter.PostAdapter;
import com.ookiisoftware.protips.auxiliar.Constantes;
import com.ookiisoftware.protips.auxiliar.Criptografia;
import com.ookiisoftware.protips.auxiliar.Import;

import java.util.Comparator;
import java.util.HashMap;

public class Post {

    //region Variáveis
    private String id;
    private String id_tipster;
    private String titulo;
    private String texto;
    private String foto;
    private String odd_maxima;
    private String odd_minima;
    private String horario_maximo;
    private String horario_minimo;
    private String data;
    private String esporte;
    private String mercado;
    private boolean publico;

    private HashMap<String,String> bom, ruim;
    //endregion

    public Post() {
        bom = new HashMap<>();
        ruim = new HashMap<>();
    }

    //region Métodos

    public void salvar(final Activity activity, final ProgressBar progressBar, boolean isFotoLocal) {
        if (isFotoLocal) {
            Import.getFirebase.getStorage()
                    .child(Constantes.firebase.child.POSTES)
                    .child(getId())
                    .putFile(Uri.parse(getFoto()))
                    .addOnSuccessListener(taskSnapshot -> {
                        if (taskSnapshot.getMetadata() != null && taskSnapshot.getMetadata().getReference() != null)
                            taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnCompleteListener(task -> {
                                if (task.getResult() != null) {
                                    setFoto(task.getResult().toString());
                                    salvar();
                                    activity.finish();
                                }
                            });
                    }).addOnFailureListener(e -> {
                        Import.Alert.snakeBar(activity, activity.getResources().getString(R.string.post_erro));
                        progressBar.setVisibility(View.GONE);
                    });
        } else {
            salvar();
        }
    }

    private void salvar() {
        String id = getId_tipster() == null ? Import.getFirebase.getId() : getId_tipster();
        Import.getFirebase.getReference()
                .child(Constantes.firebase.child.USUARIO)
                .child(id)
                .child(Constantes.firebase.child.POSTES)
                .child(Criptografia.criptografar(getData()))
                .setValue(this);

        Import.get.seguindo.add(this);
        Import.getFirebase.getTipster().getPostes().put(getId(), this);
    }

    public void addBom(String id) {
        Import.getFirebase.getReference()
                .child(Constantes.firebase.child.USUARIO)
//                .child(Constantes.firebase.child.TIPSTERS)
                .child(getId_tipster())
                .child(Constantes.firebase.child.POSTES)
                .child(Criptografia.criptografar(getData()))
                .child(Constantes.firebase.child.BOM)
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
                .child(Constantes.firebase.child.USUARIO)
//                .child(Constantes.firebase.child.TIPSTERS)
                .child(getId_tipster())
                .child(Constantes.firebase.child.POSTES)
                .child(Criptografia.criptografar(getData()))
                .child(Constantes.firebase.child.RUIM)
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
                .child(Constantes.firebase.child.USUARIO)
//                .child(Constantes.firebase.child.TIPSTERS)
                .child(getId_tipster())
                .child(Constantes.firebase.child.POSTES)
                .child(Criptografia.criptografar(getData()))
                .child(Constantes.firebase.child.BOM)
                .child(id)
                .removeValue();
        getBom().remove(id);
    }

    public void removeRuim(String id) {
        Import.getFirebase.getReference()
                .child(Constantes.firebase.child.USUARIO)
//                .child(Constantes.firebase.child.TIPSTERS)
                .child(getId_tipster())
                .child(Constantes.firebase.child.POSTES)
                .child(Criptografia.criptografar(getData()))
                .child(Constantes.firebase.child.RUIM)
                .child(id)
                .removeValue();
        getRuim().remove(id);
    }

    public void excluir(final PostAdapter adapter) {
        String id = getId_tipster();
        DatabaseReference ref = Import.getFirebase.getReference()
                .child(Constantes.firebase.child.USUARIO)
                .child(id)
                .child(Constantes.firebase.child.POSTES);

        ref.child(Criptografia.criptografar(getData()))
                .removeValue()
                .addOnSuccessListener(aVoid -> {

                    Import.getFirebase.getStorage()
                            .child(Constantes.firebase.child.POSTES)
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

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
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

    public String getMercado() {
        return mercado;
    }

    public void setMercado(String mercado) {
        this.mercado = mercado;
    }

    public boolean isPublico() {
        return publico;
    }

    public void setPublico(boolean publico) {
        this.publico = publico;
    }

    //endregion

    public static class sortByDate implements Comparator<Post> {
        public int compare(Post left, Post right) {
            return right.getData().compareTo(left.getData());
        }
    }
}
