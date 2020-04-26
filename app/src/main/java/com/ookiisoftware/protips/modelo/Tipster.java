package com.ookiisoftware.protips.modelo;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.ookiisoftware.protips.auxiliar.Constantes;
import com.ookiisoftware.protips.auxiliar.Import;

import java.util.ArrayList;
import java.util.HashMap;

public class Tipster {

    private Usuario dados;
    private HashMap<String, Post> postes;
    private ArrayList<Esporte> esportes;
    private ArrayList<String> punters;
    private ArrayList<String> puntersPendentes;

    public enum Status {
        Pendente, Seguindo, Nao_Seguindo
    }
    public enum Acao {
        Seguir, Desseguir, Remover_Pendente, Aceitar
    }

    public Tipster() {
        punters = new ArrayList<>();
        esportes = new ArrayList<>();
        postes = new HashMap<>();
        dados = new Usuario();
    }

    public void salvar() {
        DatabaseReference reference = Import.getFirebase.getReference();
        reference
                .child(Constantes.firebase.child.USUARIO)
                .child(Constantes.firebase.child.TIPSTERS)
                .child(dados.getId())
                .setValue(this);

        reference
                .child(Constantes.firebase.child.IDENTIFICADOR)
                .child(dados.getTipname())
                .setValue(dados.getId());
    }

    public void solicitar(final Punter punter, final Acao acao) {
        Import.getFirebase.getReference()
                .child(Constantes.firebase.child.USUARIO)
                .child(Constantes.firebase.child.TIPSTERS)
                .child(dados.getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null)
                            return;
                        Tipster item = dataSnapshot.getValue(Tipster.class);
                        if (item == null)
                            return;

                        String id_punter = punter.getDados().getId();
                        switch (acao) {
                            case Seguir: {
                                    if (!item.getPunters().contains(id_punter) && !item.getPuntersPendentes().contains(id_punter))
                                        item.getPuntersPendentes().add(id_punter);
                                    dataSnapshot.getRef()
                                            .child(Constantes.firebase.child.PUNTERS_PENDENTES)
                                            .setValue(item.getPuntersPendentes());
                                    break;
                                }
                            case Desseguir: {
                                    item.getPunters().remove(id_punter);
                                    dataSnapshot.getRef()
                                            .child(Constantes.firebase.child.PUNTERS)
                                            .setValue(item.getPunters());
                                    break;
                                }
                            case Remover_Pendente: {
                                    item.getPuntersPendentes().remove(id_punter);
                                    dataSnapshot.getRef()
                                            .child(Constantes.firebase.child.PUNTERS_PENDENTES)
                                            .setValue(item.getPuntersPendentes());
                                    break;
                                }
                            case Aceitar: {
                                if (!item.getPunters().contains(id_punter)) {
                                    item.getPunters().add(id_punter);
                                    dataSnapshot.getRef()
                                            .child(Constantes.firebase.child.PUNTERS)
                                            .setValue(item.getPunters());
                                    solicitar(punter, Acao.Remover_Pendente);

//                                    Import.getFirebase.getTipster().getPunters().add(id_punter);
//                                    Import.get.punter.getAll().add(punter);
//
//                                    Import.getFirebase.getTipster().getPuntersPendentes().remove(id_punter);
//                                    Import.get.tipsters.getPuntersPendentes().remove(punter);
//                                    Import.activites.getMainActivity().tipstersFragment.adapterUpdate();
//                                    Import.activites.getMainActivity().notificationsFragment.adapterUpdate();
                                }
                                break;
                            }
                        }

                        dataSnapshot.getRef().setValue(item);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });

    }

    //region gets sets

    public Usuario getDados() {
        return dados;
    }

    public void setDados(Usuario dados) {
        this.dados = dados;
    }

    public HashMap<String, Post> getPostes() {
        return postes;
    }

    public void setPostes(HashMap<String, Post> postes) {
        this.postes = postes;
    }

    public ArrayList<Esporte> getEsportes() {
        if (esportes == null)
            esportes = new ArrayList<>();
        return esportes;
    }

    public void setEsportes(ArrayList<Esporte> esportes) {
        this.esportes = esportes;
    }

    public ArrayList<String> getPunters() {
        if (punters == null)
            punters = new ArrayList<>();
        return punters;
    }

    public void setPunters(ArrayList<String> punters) {
        this.punters = punters;
    }

    public ArrayList<String> getPuntersPendentes() {
        if (puntersPendentes == null)
            puntersPendentes = new ArrayList<>();
        return puntersPendentes;
    }

    public void setPuntersPendentes(ArrayList<String> puntersPendentes) {
        this.puntersPendentes = puntersPendentes;
    }

    //endregion
}
