package com.ookiisoftware.protips.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.ookiisoftware.protips.auxiliar.Constantes;
import com.ookiisoftware.protips.auxiliar.Criptografia;
import com.ookiisoftware.protips.auxiliar.Import;
import com.ookiisoftware.protips.modelo.Mensagem;

import java.util.ArrayList;

public class SQLiteMensagem extends SQLiteOpenHelper {

    private static final String TAG = "SQLiteMensagem";

    private static final int VERSAO_BANCO = Constantes.SQLITE_BANCO_DE_DADOS_VERSAO;
    private static final String TABELA_MENSAGENS = "mensagens";

    private final String ID = "id";
    private final String ID_REMETENTE = "id_remetente";
    private final String MENSAGEM = "mensagem";
    private final String DATA = "data";
    private final String STATUS = "status";
    private final String ARQUIVO = "arquivo";

    public SQLiteMensagem(@Nullable Context context) {
        super(context, Import.getSQLiteDatabaseName(context), null, VERSAO_BANCO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS "+ TABELA_MENSAGENS +" ("
                + ID + " text,"
                + ID_REMETENTE + " text,"
                + MENSAGEM + " text,"
                + DATA + " text,"
                + STATUS + " integer,"
                + ARQUIVO + " integer"
                +")";
        db.execSQL(sql);
//        Log.e("SQLiteContato", sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {}
/*
    private boolean add(Mensagem mensagem){
        try{
//            Criptografar(mensagem);
            SQLiteDatabase db = this.getWritableDatabase();
            onCreate(db);

            ContentValues values = new ContentValues();
            values.put(ID, mensagem.getId_conversa());
            values.put(ID_REMETENTE, mensagem.getId_remetente());
            values.put(MENSAGEM, mensagem.getMensagem());
            values.put(DATA, mensagem.getData_de_envio());
            values.put(STATUS, mensagem.getStatus());
            values.put(ARQUIVO, mensagem.getArquivo());
            db.insert(TABELA_MENSAGENS, null, values);
            db.close();
            return true;
        }catch (Exception e){
            Log.e(TAG, "add: " + e.getMessage());
            return false;
        }
    }

    public boolean remove(String id) {
        try {
            id = Criptografia.criptografar(id);
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABELA_MENSAGENS, ID + " = ?", new String[] { id });
            db.close();
            return true;
        }catch (Exception e){
            Log.e(TAG, "remove: " + e.getMessage());
            return false;
        }
    }

    public Mensagem get(String busca){
        try {
            busca = Criptografia.criptografar(busca);
            SQLiteDatabase db = this.getReadableDatabase();
            String indice = DATA + " = ?";
            String[] query = {ID, ID_REMETENTE, MENSAGEM, DATA, STATUS, ARQUIVO};
            Cursor cursor = db.query(TABELA_MENSAGENS, query, indice, new String[]{busca}, null, null, null);
            Mensagem mensagem = null;
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                mensagem = new Mensagem(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4), cursor.getInt(5));
                db.close();
                cursor.close();
            }
            if (mensagem != null)
                Descriptografar(mensagem);
            return mensagem;
        }catch (Exception e){
            Log.e(TAG, "get: " + e.getMessage());
            return null;
        }
    }

    public ArrayList<Mensagem> getAll(String id_conversa) {
        ArrayList<Mensagem> mensagems = new ArrayList<>();
        try{
            String id = ID + " = ?";
            String[] query = {ID, ID_REMETENTE, MENSAGEM, DATA, STATUS, ARQUIVO};

            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.query(TABELA_MENSAGENS, query, id, new String[]{id_conversa}, null, null, null);

            if (cursor.moveToFirst()) {
                do {
                    Mensagem mensagem = new Mensagem();
                    mensagem.setId_conversa(cursor.getString(0));
                    mensagem.setId_remetente(cursor.getString(1));
                    mensagem.setMensagem(cursor.getString(2));
                    mensagem.setData_de_envio(cursor.getString(3));
                    mensagem.setStatus(cursor.getInt(4));
                    mensagem.setArquivo(cursor.getInt(5));
                    Descriptografar(mensagem);
                    mensagems.add(mensagem);
                } while (cursor.moveToNext());

                db.close();
                cursor.close();
            }
        } catch (Exception e){
            Log.e(TAG, "getAll: " + e.getMessage());
        }

        return mensagems;
    }

    public boolean update(Mensagem mensagem) {// já recebo criptografada
        try {
            Criptografar(mensagem);
            if (get(Criptografia.descriptografar(mensagem.getData_de_envio())) == null)
                return add(mensagem);
            else {
                SQLiteDatabase db = this.getWritableDatabase();

                ContentValues values = new ContentValues();
//            values.put(ID, mensagem.getId_conversa());
//            values.put(ID_REMETENTE, mensagem.getId_remetente());
//            values.put(MENSAGEM, mensagem.getMensagem());
//            values.put(DATA, mensagem.getData_de_envio());
                values.put(STATUS, mensagem.getStatus());
//            values.put(ARQUIVO, mensagem.getArquivo());
                db.update(TABELA_MENSAGENS, values, DATA + " = ?", new String[] { mensagem.getData_de_envio() });
                db.close();
                return true;
            }
        } catch (Exception e){
            Log.e(TAG, "update: " + e.getMessage());
            return false;
        }
    }

    public static void Criptografar(Mensagem mensagem){
        if (mensagem.getArquivo() == 0){
//            mensagem.setId_conversa(Criptografia.criptografar(mensagem.getId_conversa()));
//            mensagem.setId_remetente(Criptografia.criptografar(mensagem.getId_remetente()));
            mensagem.setMensagem(Criptografia.criptografar(mensagem.getMensagem()));
            mensagem.setData_de_envio(Criptografia.criptografar(mensagem.getData_de_envio()));
            mensagem.setArquivo(1);
        }
    }

    public static void Descriptografar(Mensagem mensagem){
        if (mensagem.getArquivo() == 1){
            mensagem.setId_conversa(Criptografia.descriptografar(mensagem.getId_conversa()));
            mensagem.setId_remetente(Criptografia.descriptografar(mensagem.getId_remetente()));
            mensagem.setMensagem(Criptografia.descriptografar(mensagem.getMensagem()));
            mensagem.setData_de_envio(Criptografia.descriptografar(mensagem.getData_de_envio()));
            mensagem.setArquivo(0);
        }
    }
    */
}
