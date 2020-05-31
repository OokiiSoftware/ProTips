package com.ookiisoftware.protips.modelo;

import androidx.annotation.NonNull;

import com.google.firebase.database.Exclude;
import com.ookiisoftware.protips.auxiliar.Import;

public class Data {

    private int ano;
    private int mes;
    private int dia;

    public Data() {}
    public Data(Data data) {
        setAno(data.getAno());
        setMes(data.getMes());
        setDia(data.getDia());
    }
    public Data(int dia, int mes, int ano) {
        this.ano = ano;
        this.mes = mes;
        this.dia = dia;
    }

    //format = 01/01/2000
    public boolean setData(String date) {
        try {
            String[] teste = date.split("/");
            int dia = Integer.parseInt(teste[0]);
            int mes = Integer.parseInt(teste[1]);
            int ano = Integer.parseInt(teste[2]);

            if (dia == 0 || mes == 0 || dia > 31 || mes > 12 || ano < 1900)
                return false;

            setDia(dia);
            setMes(mes);
            setAno(ano);
            return true;
        } catch (Exception ignored) {}

        return false;
    }

    @NonNull
    @Override
    public String toString() {
        return dia + "/" + mes + "/" + ano;
    }

    @Exclude
    public int getIdade() {
        return Import.get.calendar.ano() - ano;
    }

    public boolean valido() {
        boolean b= (dia > 0 && dia <= 31) && (mes > 0 && mes <= 12) && ano >= 1900;
        return b;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public int getDia() {
        return dia;
    }

    public void setDia(int dia) {
        this.dia = dia;
    }
}
