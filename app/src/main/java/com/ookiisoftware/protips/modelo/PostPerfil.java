package com.ookiisoftware.protips.modelo;

import android.app.Activity;
import android.net.Uri;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.ookiisoftware.protips.R;
import com.ookiisoftware.protips.adapter.PostAdapter;
import com.ookiisoftware.protips.auxiliar.Constantes;
import com.ookiisoftware.protips.auxiliar.Criptografia;
import com.ookiisoftware.protips.auxiliar.Import;

import java.util.Comparator;

public class PostPerfil {

    //region Variáveis
    private String id;
    private String foto;
    private String titulo;
    private String texto;
    private String data;
    private String id_tipster;
    //endregion

    //region Métodos

    public void salvar(final Activity activity, final ProgressBar progressBar, boolean isFotoLocal) {
        if (isFotoLocal) {
            Import.getFirebase.getStorage()
                    .child(Constantes.firebase.child.POSTES_PERFIL)
                    .child(getId())
                    .putFile(Uri.parse(getFoto()))
                    .addOnSuccessListener(taskSnapshot -> {
                        if (taskSnapshot.getMetadata() != null && taskSnapshot.getMetadata().getReference() != null)
                            taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnCompleteListener(task -> {
                                if (task.getResult() != null) {
                                    setFoto(task.getResult().toString());
                                    salvar();
                                }
                            });
                    }).addOnFailureListener(e -> {
                        Import.Alert.snakeBar(activity, activity.getResources().getString(R.string.post_erro));
                        if (progressBar != null)
                            progressBar.setVisibility(View.GONE);
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
                .child(id)
                .child(Constantes.firebase.child.POSTES_PERFIL)
                .child(Criptografia.criptografar(getData()))
                .setValue(this);

        Import.getFirebase.getTipster().getPost_perfil().put(getId(), this);
        Import.activites.getMainActivity().perfilFragment.adapterUpdate();
    }

    public void excluir(final PostAdapter adapter) {
        String id = getId_tipster();
        final DatabaseReference ref = Import.getFirebase.getReference()
                .child(Constantes.firebase.child.USUARIO)
                .child(id)
                .child(Constantes.firebase.child.POSTES_PERFIL);

        ChildEventListener eventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ref.child(Criptografia.criptografar(getData())).removeValue();

                Import.getFirebase.getStorage()
                        .child(Constantes.firebase.child.POSTES_PERFIL)
                        .child(getId()).delete();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Post item = dataSnapshot.getValue(Post.class);
                if (item != null && item.getId().equals(getId())) {
                    Import.getFirebase.getTipster().getPost_perfil().remove(getId());
                    adapter.notifyDataSetChanged();
                }
                ref.removeEventListener(this);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        ref.addChildEventListener(eventListener);
    }

    //endregion

    //region gets sets

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getId_tipster() {
        return id_tipster;
    }

    public void setId_tipster(String id_tipster) {
        this.id_tipster = id_tipster;
    }

    //endregion

    public static class orderByDate implements Comparator<PostPerfil> {
        public int compare(PostPerfil left, PostPerfil right) {
            return right.getData().compareTo(left.getData());
        }
    }

}
