package com.ookiisoftware.protips.modelo;

import android.app.Activity;
import android.net.Uri;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;
import com.ookiisoftware.protips.R;
import com.ookiisoftware.protips.auxiliar.Constantes;
import com.ookiisoftware.protips.auxiliar.Criptografia;
import com.ookiisoftware.protips.auxiliar.Import;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
    private int esporte;
    private int mercado;
    private boolean publico;

    private List<String> bom, ruim;
    //endregion

    public Post() {
        bom = new LinkedList<>();
        ruim = new LinkedList<>();
    }

    public void salvar(final Activity activity, final ProgressBar progressBar, boolean isFotoLocal) {
        if (isFotoLocal) {
            Import.getFirebase.getStorage()
                    .child(Constantes.firebase.child.POSTES)
                    .child(getId())
                    .putFile(Uri.parse(getFoto()))
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            if (taskSnapshot.getMetadata() != null && taskSnapshot.getMetadata().getReference() != null)
                                taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.getResult() != null) {
                                            setFoto(task.getResult().toString());
                                            salvar();
                                            activity.finish();
                                            Import.get.tipsters.postes().add(Post.this);
                                            Import.getFirebase.getTipster().getPostes().put(getId(), Post.this);
                                            Import.activites.getMainActivity().feedFragment.adapterUpdate();
                                        }
                                    }
                                });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Import.Alert.snakeBar(activity.getCurrentFocus(), activity.getResources().getString(R.string.post_erro));
                    progressBar.setVisibility(View.GONE);
                }
            });
        } else {
            salvar();
        }
    }

    private void salvar() {
        String id = getId_tipster() == null ? Import.getFirebase.getId() : getId_tipster();
        DatabaseReference reference = Import.getFirebase.getReference();
        reference
                .child(Constantes.firebase.child.USUARIO)
                .child(Constantes.firebase.child.TIPSTERS)
                .child(id)
                .child(Constantes.firebase.child.POSTES)
                .child(Criptografia.criptografar(getData()))
                .setValue(this);
    }

    public void atualizar() {
        Import.Alert.msg("Post", "data", Criptografia.criptografar(getData()));
        final DatabaseReference reference = Import.getFirebase.getReference()
                .child(Constantes.firebase.child.USUARIO)
                .child(Constantes.firebase.child.TIPSTERS)
                .child(getId_tipster())
                .child(Constantes.firebase.child.POSTES)
                .child(Criptografia.criptografar(getData()));

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Post item = dataSnapshot.getValue(Post.class);
                String myId = Import.getFirebase.getId();
                if (item != null) {
                    if (getBom().contains(myId) && !item.getBom().contains(myId))
                        item.getBom().add(myId);
                    else if (!getBom().contains(myId))
                        item.getBom().remove(myId);

                    if (getRuim().contains(myId) && !item.getRuim().contains(myId))
                        item.getRuim().add(myId);
                    else if (!getRuim().contains(myId))
                        item.getRuim().remove(myId);
                    item.salvar(null, null, false);
                } else
                    Import.Alert.msg("Post", "atualizar", "item null");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

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

    public List<String> getBom() {
        if (bom == null)
            bom = new ArrayList<>();
        return bom;
    }

    public void setBom(List<String> bom) {
        this.bom = bom;
    }

    public List<String> getRuim() {
        if (ruim == null)
            ruim = new ArrayList<>();
        return ruim;
    }

    public void setRuim(List<String> ruim) {
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

    public int getEsporte() {
        return esporte;
    }

    public void setEsporte(int esporte) {
        this.esporte = esporte;
    }

    public int getMercado() {
        return mercado;
    }

    public void setMercado(int mercado) {
        this.mercado = mercado;
    }

    public boolean isPublico() {
        return publico;
    }

    public void setPublico(boolean publico) {
        this.publico = publico;
    }

    //endregion

}
