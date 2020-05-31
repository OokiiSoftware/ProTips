package com.ookiisoftware.protips.modelo;

import com.ookiisoftware.protips.auxiliar.Constantes;
import com.ookiisoftware.protips.auxiliar.Import;

public class Notificacao {

    //region Variaveis
    private String title;
    private String body;
    private String timestamp;
    private String de;
    private String para;
    private String token;
    private String action;
    private String channel;
    //endregion

    public Notificacao() {
        updateData();
    }

    private void updateData() {
        timestamp = Import.get.Data();
    }

    public void enviar() {
        Import.getFirebase.getReference()
                .child(Constantes.firebase.child.MESSAGES)
                .child(getPara())
                .push()
                .setValue(this);
    }

    //region get set

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getDe() {
        return de;
    }

    public void setDe(String de) {
        this.de = de;
    }

    public String getPara() {
        return para;
    }

    public void setPara(String para) {
        this.para = para;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    //endregion

}
