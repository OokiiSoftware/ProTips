package com.ookiisoftware.protips.modelo;

import com.google.firebase.database.DatabaseReference;
import com.ookiisoftware.protips.auxiliar.Constantes;
import com.ookiisoftware.protips.auxiliar.Import;

import java.util.HashMap;

public class Tipster {

    private Usuario dados;
    private HashMap<String, Post> postes;
    private HashMap<String, Esporte> esportes;
    private HashMap<String, String> punters;
    private HashMap<String,String> puntersPendentes;

    public enum Acao {
        Seguir, Desseguir, Remover_Pendente, Aceitar

    }

    public Tipster() {
        punters = new HashMap<>();
        esportes = new HashMap<>();
        postes = new HashMap<>();
        dados = new Usuario();
    }

    public void solicitarSerTipster() {
        Import.getFirebase.getReference()
                .child(Constantes.firebase.child.SOLICITACAO_NOVO_TIPSTER)
                .child(dados.getId())
                .setValue(getDados().getTipname());
    }

    public void solicitarSerTipsterCancelar() {
        Import.getFirebase.getReference()
                .child(Constantes.firebase.child.SOLICITACAO_NOVO_TIPSTER)
                .child(dados.getId())
                .removeValue();
        getDados().toPunter().getDados().desbloquear();
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

    /*public void solicitar1(final Punter punter, final Acao acao) {
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
                                    if (!item.getPunters().contains(id_punter) && !item.getPuntersPendentes().values().contains(id_punter))
                                        item.getPuntersPendentes().values().add(id_punter);
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

                                    String meuId = Import.getFirebase.getId();
                                    punter.addTipster(meuId);
                                    solicitar1(punter, Acao.Remover_Pendente);

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
    }*/

    public void addSolicitacao(String id) {
        Import.getFirebase.getReference()
                .child(Constantes.firebase.child.USUARIO)
                .child(Constantes.firebase.child.TIPSTERS)
                .child(dados.getId())
                .child(Constantes.firebase.child.PUNTERS_PENDENTES)
                .child(id)
                .setValue(id);
    }

    public void removerSolicitacao(String id) {
        Import.getFirebase.getReference()
                .child(Constantes.firebase.child.USUARIO)
                .child(Constantes.firebase.child.TIPSTERS)
                .child(dados.getId())
                .child(Constantes.firebase.child.PUNTERS_PENDENTES)
                .child(id)
                .removeValue();
    }

    public void addPunter(Punter punter) {
        String id = punter.getDados().getId();
        Import.getFirebase.getReference()
                .child(Constantes.firebase.child.USUARIO)
                .child(Constantes.firebase.child.TIPSTERS)
                .child(dados.getId())
                .child(Constantes.firebase.child.PUNTERS)
                .child(id)
                .setValue(id);

        punter.addTipster(Import.getFirebase.getId());
        removerSolicitacao(id);
    }

    public void removerPunter(Punter punter) {
        String id = punter.getDados().getId();
        Import.getFirebase.getReference()
                .child(Constantes.firebase.child.USUARIO)
                .child(Constantes.firebase.child.TIPSTERS)
                .child(dados.getId())
                .child(Constantes.firebase.child.PUNTERS)
                .child(id)
                .removeValue();
        punter.removerTipster(id);
    }

    public void addEsporte(Esporte esporte) {
        String nome = esporte.getNome();
        Import.getFirebase.getReference()
                .child(Constantes.firebase.child.USUARIO)
                .child(Constantes.firebase.child.TIPSTERS)
                .child(dados.getId())
                .child(Constantes.firebase.child.ESPORTES)
                .child(nome)
                .setValue(esporte);
        getEsportes().put(nome, esporte);
    }

    public void removerEsporte(Esporte esporte) {
        String nome = esporte.getNome();
        Import.getFirebase.getReference()
                .child(Constantes.firebase.child.USUARIO)
                .child(Constantes.firebase.child.TIPSTERS)
                .child(dados.getId())
                .child(Constantes.firebase.child.ESPORTES)
                .child(nome)
                .removeValue();
        getEsportes().remove(nome);
    }

    public void addMercado(String esporte, String mercado) {
        Import.getFirebase.getReference()
                .child(Constantes.firebase.child.USUARIO)
                .child(Constantes.firebase.child.TIPSTERS)
                .child(dados.getId())
                .child(Constantes.firebase.child.ESPORTES)
                .child(esporte)
                .child(mercado)
                .setValue(mercado);

        Esporte e = getEsportes().get(esporte);
        if (e != null)
            e.getMercados().put(mercado, mercado);
    }

    public void removerMercado(String esporte, String mercado) {
        Import.getFirebase.getReference()
                .child(Constantes.firebase.child.USUARIO)
                .child(Constantes.firebase.child.TIPSTERS)
                .child(dados.getId())
                .child(Constantes.firebase.child.ESPORTES)
                .child(esporte)
                .child(mercado)
                .removeValue();

        Esporte e = getEsportes().get(esporte);
        if (e != null)
            e.getMercados().remove(mercado);
    }

    public void excluir() {
        Import.getFirebase.getReference()
                .child(Constantes.firebase.child.USUARIO)
                .child(Constantes.firebase.child.TIPSTERS)
                .child(dados.getId())
                .removeValue();
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

    public HashMap<String, Esporte> getEsportes() {
        if (esportes == null)
            esportes = new HashMap<>();
        return esportes;
    }

    public void setEsportes(HashMap<String, Esporte> esportes) {
        this.esportes = esportes;
    }

    public HashMap<String, String> getPunters() {
        if (punters == null)
            punters = new HashMap<>();
        return punters;
    }

    public void setPunters(HashMap<String, String> punters) {
        this.punters = punters;
    }

    public HashMap<String,String> getPuntersPendentes() {
        if (puntersPendentes == null)
            puntersPendentes = new HashMap<>();
        return puntersPendentes;
    }

    public void setPuntersPendentes(HashMap<String,String> puntersPendentes) {
        this.puntersPendentes = puntersPendentes;
    }

    //endregion

}
