package com.ookiisoftware.protips.modelo;

import android.widget.DatePicker;

import androidx.annotation.NonNull;

import com.google.firebase.database.Exclude;
import com.ookiisoftware.protips.auxiliar.Import;

import java.util.Calendar;

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
    public Data(DatePicker date) {
        ano = date.getYear();
        mes = date.getMonth();
        dia = date.getDayOfMonth();
    }

    public void setData(DatePicker date) {
        ano = date.getYear();
        mes = date.getMonth();
        dia = date.getDayOfMonth();
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
