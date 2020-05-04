package com.ookiisoftware.protips.modelo;

import android.app.Activity;
import android.net.Uri;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.database.DatabaseReference;
import com.ookiisoftware.protips.R;
import com.ookiisoftware.protips.auxiliar.Constantes;
import com.ookiisoftware.protips.auxiliar.Criptografia;
import com.ookiisoftware.protips.auxiliar.Import;

public class PostPerfil {

    private String id;
    private String foto;
    private String titulo;
    private String texto;
    private String data;
    private String id_tipster;

    public PostPerfil() {}

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
                                    activity.finish();
                                    Import.activites.getMainActivity().perfilFragment.adapterUpdate();
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
                .child(Constantes.firebase.child.TIPSTERS)
                .child(id)
                .child(Constantes.firebase.child.POSTES_PERFIL)
                .child(Criptografia.criptografar(getData()))
                .setValue(this);
    }

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

}
