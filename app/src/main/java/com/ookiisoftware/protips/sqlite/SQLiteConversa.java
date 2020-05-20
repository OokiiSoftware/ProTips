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
import com.ookiisoftware.protips.modelo.Contato;
import com.ookiisoftware.protips.modelo.Conversa;

import java.util.ArrayList;

public class SQLiteConversa extends SQLiteOpenHelper {

    private static final String TAG = "SQLiteConversa";

    private static final int VERSAO_BANCO = Constantes.sqlite.SQLITE_BANCO_DE_DADOS_VERSAO;
    private static final String TABELA_CONVERSAS = "conversas";

    private final String CONTATO_ID = "id_contato";
    private final String CONTATO_NOME = "nome_contato";
    private final String CONTATO_IMAGE_URI = "foto_contato";
    private final String ULTIMA_MENSAGEM = "ultima_mensagem";
    private final String DATA = "data";
    private final String LIDO = "lido";

    public SQLiteConversa(@Nullable Context context) {
        super(context, Import.get.SQLiteDatabaseName(context), null, VERSAO_BANCO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABELA_CONVERSAS + " ("
                + CONTATO_ID + " text,"
                + CONTATO_NOME + " text,"
                + CONTATO_IMAGE_URI + " text,"
                + ULTIMA_MENSAGEM + " text,"
                + DATA + " text,"
                + LIDO + " int"
                + ")";
        db.execSQL(sql);
//        Log.e("SQLiteContato", sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) { }
/*
    private void add(Conversa conversa) {
        SQLiteDatabase db = this.getWritableDatabase();
        onCreate(db);

        ContentValues values = new ContentValues();
        values.put(CONTATO_ID, conversa.getId());
        values.put(CONTATO_NOME, conversa.getNome_contato());
        values.put(CONTATO_IMAGE_URI, conversa.getFoto());
        values.put(ULTIMA_MENSAGEM, conversa.getUltima_msg());
        values.put(DATA, conversa.getData());
        values.put(LIDO, conversa.getLido());
        db.insert(TABELA_CONVERSAS, null, values);
        db.close();
    }

    public void remove(String id) {
        id = Criptografia.criptografar(id);
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABELA_CONVERSAS, CONTATO_ID + " = ?", new String[]{id});
        db.close();
    }

    public Conversa get(String busca) {
        busca = Criptografia.criptografar(busca);
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            String indice = CONTATO_ID + " = ?";
            String[] query = {CONTATO_ID, CONTATO_NOME, CONTATO_IMAGE_URI, ULTIMA_MENSAGEM, DATA, LIDO};
            Cursor cursor = db.query(TABELA_CONVERSAS, query, indice, new String[]{busca}, null, null, null);
            Conversa conversa = null;
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                conversa = new Conversa(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getInt(5));
                db.close();
                cursor.close();
            }
            if (conversa != null)
                Descriptografar(conversa);
            return conversa;
        } catch (Exception e) {
            Log.e(TAG, "get: " + e.getMessage());
            return null;
        }
    }

    public ArrayList<Conversa> getAll() {
        ArrayList<Conversa> conversas = new ArrayList<>();
        try {
            String[] query = {CONTATO_ID, CONTATO_NOME, CONTATO_IMAGE_URI, ULTIMA_MENSAGEM, DATA, LIDO};

            SQLiteDatabase db = this.getWritableDatabase();
            onCreate(db);
            Cursor cursor = db.query(TABELA_CONVERSAS, query, null, null, null, null, DATA + " DESC");

            if (cursor.moveToFirst()) {
                do {
                    Conversa conversa = new Conversa();
                    conversa.setId(cursor.getString(0));
                    conversa.setNome_contato(cursor.getString(1));
                    conversa.setFoto(cursor.getString(2));
                    conversa.setUltima_msg(cursor.getString(3));
                    conversa.setData(cursor.getString(4));
                    conversa.setLido(cursor.getInt(5));
                    Descriptografar(conversa);
                    conversas.add(conversa);
                } while (cursor.moveToNext());
                db.close();
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "getAll: " + e.getMessage());
        }

        return conversas;
    }

    public void update(Context context, Conversa conversa) {
        Criptografar(conversa);
        // Vou verificar se tenho ele nos meus contatos
        // se não tiver, coloco o nome da conversa com o TipName dele
        if (conversa.getNome_contato() == null) {
            SQLiteContato dbContato = new SQLiteContato(context);
            Contato contato = dbContato.get(conversa.getId());
            if (contato == null)
                conversa.setNome_contato(conversa.getId());
            else
                conversa.setNome_contato(contato.getNome());
        }

        //================= Agora vou verificar se a conversa já existe
        if (get(Criptografia.descriptografar(conversa.getId())) == null)
            add(conversa);
        else {
            SQLiteDatabase db = this.getWritableDatabase();

            //  Só atualiza o dado que não está com valor null
            ContentValues values = new ContentValues();
            if (conversa.getNome_contato() != null)
                values.put(CONTATO_NOME, conversa.getNome_contato());
            if (conversa.getFoto() != null)
                values.put(CONTATO_NOME, conversa.getFoto());
            if (conversa.getUltima_msg() != null)
                values.put(ULTIMA_MENSAGEM, conversa.getUltima_msg());
            if (conversa.getData() != null)
                values.put(DATA, conversa.getData());
            if (conversa.getLido() > 0)
                values.put(LIDO, conversa.getLido());
            db.update(TABELA_CONVERSAS, values, CONTATO_ID + " = ?", new String[]{conversa.getId()});
            db.close();
        }
    }

    private void Criptografar(Conversa conversa) {
//        conversa.setId(Criptografia.criptografar(conversa.getId()));
        if (conversa.getNome_contato() != null)
            conversa.setNome_contato(Criptografia.criptografar(conversa.getNome_contato()));
//        conversa.setUltima_msg(Criptografia.criptografar(conversa.getUltima_msg()));
//        conversa.setData(Criptografia.criptografar(conversa.getData()));
        if (conversa.getFoto() != null)
            conversa.setFoto(Criptografia.criptografar(conversa.getFoto()));
    }

    private void Descriptografar(Conversa conversa) {
        conversa.setId(Criptografia.descriptografar(conversa.getId()));
        conversa.setNome_contato(Criptografia.descriptografar(conversa.getNome_contato()));
        conversa.setUltima_msg(Criptografia.descriptografar(conversa.getUltima_msg()));
        conversa.setData(Criptografia.descriptografar(conversa.getData()));
        conversa.setFoto(Criptografia.descriptografar(conversa.getFoto()));
    }
    */
}
