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

import java.util.ArrayList;

public class SQLiteContato extends SQLiteOpenHelper {

//    private static final String TAG = "SQLiteContato";

    private static final int VERSAO_BANCO = Constantes.SQLITE_BANCO_DE_DADOS_VERSAO;
    private static final String TABELA_CONTATOS = "contatos";

    private final String ID = "id";
    private final String NOME = "nome";
    private final String EMAIL = "email";
    private final String IMAGE_URI = "image";
    private final String CATEGORIA = "categoria";

    public SQLiteContato(@Nullable Context context) {
        super(context, Import.get.SQLiteDatabaseName(context), null, VERSAO_BANCO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        db.execSQL("DROP TABLE IF EXISTS " + TABELA_CONTATOS);
        String sql = "CREATE TABLE IF NOT EXISTS "+TABELA_CONTATOS+" ("
                + ID + " text primary key,"
                + NOME + " text,"
                + EMAIL + " text,"
                + IMAGE_URI + " text,"
                + CATEGORIA + " integer"
                +")";
        db.execSQL(sql);
//        Log.e("SQLiteContato", sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {}
/*
    private boolean add1(Contato contato) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            onCreate(db);

            if(contato.getImage_uri() == null)
                contato.setImage_uri("");
            ContentValues values = new ContentValues();
            values.put(ID, contato.getId());
            values.put(NOME, contato.getNome());
            values.put(EMAIL, contato.getEmail());
            values.put(IMAGE_URI, contato.getImage_uri());
            values.put(CATEGORIA, contato.getCategoria());
            db.insert(TABELA_CONTATOS, null, values);
            db.close();
            return true;
        } catch (Exception e){
            Log.e(TAG, "update: " + e.getMessage());
            return false;
        }
    }

    public void remove(String id) {
        id = Criptografia.criptografar(id);
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABELA_CONTATOS, ID + " = ?", new String[] { id });
        db.close();
    }

    // Busca pelo id, se não encontrar nada busca pelo email
    public Contato get1(String busca){
        busca = Criptografia.criptografar(busca);
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String indice = ID + " = ?";
            String[] query = {ID, NOME, EMAIL, IMAGE_URI, CATEGORIA};
            Cursor cursor = db.query(TABELA_CONTATOS, query, indice, new String[]{busca}, null, null, null);
            Contato contato;
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                contato = new Contato(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4));
                db.close();
                cursor.close();
            }
            else {
                indice = EMAIL + " = ?";
                cursor = db.query(TABELA_CONTATOS, query, indice, new String[]{busca}, null, null, null);
                contato = null;
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    contato = new Contato(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4));
                    db.close();
                    cursor.close();
                }
            }
            if (contato == null)
                Log.e(TAG, "Nenhum contato encontrado");
            else
                Descriptografar(contato);
            return contato;
        }catch (Exception e){
            Log.e(TAG, "get: " + e.getMessage());
            return null;
        }
    }


    public ArrayList<Contato> getAll(String ignore) {
        ArrayList<Contato> contatos = new ArrayList<>();
        try{
            String[] query = {ID, NOME, EMAIL, IMAGE_URI, CATEGORIA};

            SQLiteDatabase db = this.getWritableDatabase();
            onCreate(db);
            Cursor cursor = db.query(TABELA_CONTATOS, query, null, null, null, null, NOME);

            if (cursor.moveToFirst()) {
                do {
                    if (!cursor.getString(0).equals(ignore)){
                        Contato contato = new Contato();
                        contato.setId(cursor.getString(0));
                        contato.setNome(cursor.getString(1));
                        contato.setEmail(cursor.getString(2));
                        contato.setImage_uri(cursor.getString(3));
                        Descriptografar(contato);
                        contatos.add(contato);
                    }
                } while (cursor.moveToNext());

                db.close();
                cursor.close();
            }
        } catch (Exception e){
            Log.e(TAG, "getAll: " + e.getMessage());
        }

        return contatos;
    }

    public boolean update1(Contato contato) {
        try {
            Criptografar(contato);
            // O metodo 'get' por padrão criptografa a busca pra procurar no db, então trnho que descriptografar aqui
            if (get(Criptografia.descriptografar(contato.getId())) == null) {
                return add(contato);
            }
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(ID, contato.getId());
            values.put(NOME, contato.getNome());
            values.put(EMAIL, contato.getEmail());
            values.put(IMAGE_URI, contato.getImage_uri());
            values.put(CATEGORIA, contato.getCategoria());
            db.update(TABELA_CONTATOS, values, ID + " = ?", new String[] { contato.getId() });
            db.close();
            return true;
        } catch (Exception e){
            Log.e(TAG, "update: " + e.getMessage());
            return false;
        }
    }

    private void Criptografar(Contato contato){
        contato.setImage_uri(Criptografia.criptografar(contato.getImage_uri()));
        contato.setEmail(Criptografia.criptografar(contato.getEmail()));
        contato.setNome(Criptografia.criptografar(contato.getNome()));
        contato.setId(Criptografia.criptografar(contato.getId()));
    }
    private void Descriptografar(Contato contato){
        contato.setImage_uri(Criptografia.descriptografar(contato.getImage_uri()));
        contato.setEmail(Criptografia.descriptografar(contato.getEmail()));
        contato.setNome(Criptografia.descriptografar(contato.getNome()));
        contato.setId(Criptografia.descriptografar(contato.getId()));
    }
    */
}
