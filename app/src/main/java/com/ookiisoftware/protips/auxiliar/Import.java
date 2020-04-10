package com.ookiisoftware.protips.auxiliar;

import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ookiisoftware.protips.R;
import com.ookiisoftware.protips.activity.LoginActivity;
import com.ookiisoftware.protips.modelo.Apostador;
import com.ookiisoftware.protips.modelo.Conversa;
import com.ookiisoftware.protips.modelo.Mensagem;
import com.ookiisoftware.protips.modelo.Post;
import com.ookiisoftware.protips.modelo.Tipster;
import com.ookiisoftware.protips.modelo.Usuario;
import com.ookiisoftware.protips.sqlite.SQLiteConversa;
import com.ookiisoftware.protips.sqlite.SQLiteMensagem;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class Import {
    private static final String TAG = "Import";

    //region Métodos

    public static String splitData(String data){
        String[] dataUltimaMensagem = data.split(" ");// yyyy-MM-dd HH:mm:ss:SSS
        String[] hora = dataUltimaMensagem[1].split(":");// HH:mm:ss:SSS
        return hora[0] + ":" + hora[1];// HH:mm
    }

    public static String reorder (String data) {
        String reborn = "";
        try {
            String retFormat = "HH:mm dd/MM";
            String format = "yyyy-MM-dd HH:mm:ss";
            DateFormat df = new SimpleDateFormat(retFormat, get.locale);
            Date date = new SimpleDateFormat(format, get.locale).parse(data);
            if (date != null)
                reborn = df.format(date);
        } catch (ParseException ex) {
            Alert.erro(TAG, ex);
        }
        return reborn;
    }

    static void Notificacao(Context context, Intent intent, int icone, String titulo, String texto){
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteViews notificacaoSimples = new RemoteViews(context.getPackageName(), R.layout.notification_simples);
        RemoteViews notificacaoExpandida = new RemoteViews(context.getPackageName(), R.layout.notification_expandida);

        notificacaoExpandida.setOnClickPendingIntent(R.id.notification_titulo, pendingIntent);

        notificacaoSimples.setTextViewText(R.id.notification_titulo, titulo);
        notificacaoExpandida.setTextViewText(R.id.notification_titulo, titulo);
        notificacaoSimples.setTextViewText(R.id.notification_subtitulo, texto);
        notificacaoExpandida.setTextViewText(R.id.notification_texto, texto);

        Notification notification = new NotificationCompat.Builder(context)
                .setSmallIcon(icone)
                .setCustomContentView(notificacaoSimples)
                .setCustomBigContentView(notificacaoExpandida)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setContentIntent(pendingIntent)
                .setSound(Constantes.NOTIFICACAO_SOUND_PATH)
                .setVibrate(Constantes.NOTIFICACAO_VIBRACAO)
                .setAutoCancel(true)
                .build();

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, notification);

        /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(titulo, texto, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }else{}*/
    }

    public static boolean SalvarMensagemNoDispositivo(Context context, Mensagem mensagem) {
        try {
            SQLiteMensagem db = new SQLiteMensagem(context);
//            db.update(mensagem);
            return true;
        } catch (Exception e){
            return false;
        }
    }
    public static boolean SalvarConversaNoDispositivo(Context context, Conversa conversa) {
        try {
            SQLiteConversa db = new SQLiteConversa(context);
//            db.update(context, conversa);
            return true;
        } catch (Exception e){
            return false;
        }
    }

    static void RemoverMensagemDoFirebase(DatabaseReference reference, Mensagem mensagem) {
        DatabaseReference refTemp = reference;
        refTemp = refTemp.child(mensagem.getData_de_envio());
        refTemp.removeValue();
    }

    public static String getSQLiteDatabaseName(Context context){
        SharedPreferences pref = context.getSharedPreferences("info", Context.MODE_PRIVATE);
        return pref.getString(Constantes.SQLITE_BANCO_DE_DADOS, Criptografia.criptografar(getFirebase.getEmail()));
    }

    public static Typeface getFonteNormal(Context context){
        return Typeface.createFromAsset(context.getAssets(), "candara.ttf");
    }
    public static Typeface getFonteBold(Context context){
        return Typeface.createFromAsset(context.getAssets(), "candara-bold.ttf");
    }

    public static void Popup1 (final Activity activity, String _titulo) {

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.setCanceledOnTouchOutside(false);
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.popup_post, null);

        final TextView textLength = layout.findViewById(R.id.popup_length);
        final EditText editText = layout.findViewById(R.id.texto);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                textLength.setText(editText.getText().length() + "/200");
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        final Button btn_positivo = layout.findViewById(R.id.popup_btn_positivo);
        final Button btn_negativo = layout.findViewById(R.id.popup_btn_negativo);
        btn_positivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btn_negativo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.setContentView(layout);
        dialog.show();
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void LogOut(Activity activity) {
        getFirebase.getAuth().signOut();
        IrProLogin(activity);
    }

    public static void IrProLogin(Activity activity) {
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    //endregion

    public static class get{
        private static Random random = new Random();
        private static Locale locale = new Locale("pt", "BR");

        public static String randomString() {
            StringBuilder builder = new StringBuilder();
            char ch;
            for (int i = 0; i < 10; i++) {
                ch = (char) Math.floor(26 * random.nextDouble() + 65);
                builder.append(ch);
            }
            return builder.toString();
        }

        public static String Data(){
            Calendar c = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", locale);
            return dateFormat.format(c.getTime());
        }

        public static class tipsters {
            private static ArrayList<Tipster> meusTipsters = new ArrayList<>();
            private static ArrayList<Tipster> tipsters = new ArrayList<>();
            private static ArrayList<Post> postes = new ArrayList<>();

            public static ArrayList<Tipster> meusTipsters () {
                return meusTipsters;
            }
            public static ArrayList<Tipster> getAll() {
                return tipsters;
            }

            public static ArrayList<Post> postes () {
                return postes;
            }

            public static Tipster FindMyTipster(String item) {
                for (Tipster i : meusTipsters())
                    if (i.getDados().getId().equals(item))
                        return i;
                return null;
            }
            public static Tipster FindTipster(String item) {
                for (Tipster i : getAll())
                    if (i.getDados().getId().equals(item))
                        return i;
                return null;
            }
            public static Post FindPost(String item) {
                for (Post i : postes())
                    if (i.getId().equals(item))
                        return i;
                return null;
            }
        }
    }

    public static class Alert{
        public static void toast(Activity activity, String texto) {
            Toast.makeText(activity, texto, Toast.LENGTH_LONG).show();
        }

        public static void snakeBar(View view, String texto){
            Snackbar.make(view, texto, Snackbar.LENGTH_LONG).setAction("Fechar", null).show();
        }

        public static void msg(String tag, String titulo, String texto){
//            tag = "\n" + tag;
            Log.e(tag, "msg: " + titulo + ": " + texto);
        }

        public static void msg(String tag, String titulo, String texto, String msg){
//            tag = "\n" + tag;
            Log.e(tag, "msg: " + titulo + ": " + texto + ": " + msg);
        }

        public static void erro(String tag, Exception ex){
            String msg = "erro:";
            msg += "\nMensagem: "+ ex.getMessage();
            msg += "\nLocalizedMessage: "+ ex.getLocalizedMessage();
            msg += "\n-------";
//            tag = "\n" + tag;
            Log.e(tag, msg);
        }
        public static void erro(String tag, String titulo, String texto){
            Log.e(tag, "erro: " + titulo + ": " + texto);
        }
    }

    public static class getFirebase {
        private static FirebaseAuth firebaseAuth;
        private static DatabaseReference firebase;
        private static Apostador apostador;
        private static Tipster tipster;

        private static StorageReference firebaseStorage;

        public static DatabaseReference getReference() {
            if(firebase == null)
                firebase = FirebaseDatabase.getInstance().getReference();
            return firebase;
        }

        public static FirebaseAuth getAuth() {
            if(firebaseAuth == null)
                firebaseAuth = FirebaseAuth.getInstance();
            return firebaseAuth;
        }

        public static StorageReference getStorage() {
            if(firebaseStorage == null)
                firebaseStorage = FirebaseStorage.getInstance().getReference( );
            return firebaseStorage;
        }

        public static FirebaseUser getUser() {
            return getAuth().getCurrentUser();
        }

        public static Uri getFoto() {
            return getUser().getPhotoUrl();
        }

        public static String getId() {
            return getUser().getUid();
        }

        public static String getEmail() {
            return getUser().getEmail();
        }

        public static void setUser(Context context, Apostador user) {
            SharedPreferences pref = context.getSharedPreferences("info", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(Constantes.user.logado.NOME, user.getDados().getNome());
            editor.putString(Constantes.user.logado.EMAIL, user.getDados().getEmail());
            editor.putString(Constantes.user.logado.TIPNAME, user.getDados().getId());
            editor.putString(Constantes.user.logado.FOTO, user.getDados().getFoto());
            editor.putInt(Constantes.user.logado.CATEGORIA, user.getDados().getCategoria());

            editor.putBoolean(Constantes.PRIMEIRO_LOGIN, false);
            editor.apply();

            apostador = user;
        }
        public static void setUser(Context context, Tipster user) {
            SharedPreferences pref = context.getSharedPreferences("info", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(Constantes.user.logado.NOME, user.getDados().getNome());
            editor.putString(Constantes.user.logado.EMAIL, user.getDados().getEmail());
            editor.putString(Constantes.user.logado.TIPNAME, user.getDados().getId());
            editor.putString(Constantes.user.logado.FOTO, user.getDados().getFoto());
            editor.putInt(Constantes.user.logado.CATEGORIA, user.getDados().getCategoria());

            editor.putBoolean(Constantes.PRIMEIRO_LOGIN, false);
            editor.apply();

            tipster = user;
        }

        public static Apostador getApostador() {
            return apostador;
        }
        public static Tipster getTipster() {
            return tipster;
        }

        public static boolean isTipster() {
            return tipster != null;
        }
    }

    public static class getUsuario1 {
        private static Usuario usuarioConversa;
        private static String id, nome, email;
        private static int categoria = -1;
        private static boolean primeiroLogin;
//        private static Uri foto;

        public static boolean setUsuarioLogado(Context context, Usuario usuarioD) {
            try {
                Usuario usuario = new Usuario();
                usuario.setFoto(usuarioD.getFoto());
                usuario.setEmail(usuarioD.getEmail());
                usuario.setNome(usuarioD.getNome());
                usuario.setId(usuarioD.getId());
                usuario.setCategoria(usuarioD.getCategoria());
//                Usuario.Criptografar(usuario);

                SharedPreferences pref = context.getSharedPreferences("info", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
//                editor.putString(Constantes.NOME, usuario.getNome());
//                editor.putString(Constantes.EMAIL, usuario.getEmail());
//                editor.putString(Constantes.TIPNAME, usuario.getId());
//                editor.putString(Constantes.FOTO, usuario.getImage_uri());
//                editor.putInt(Constantes.CATEGORIA, usuario.getCategoria());

                editor.putString(Constantes.SQLITE_BANCO_DE_DADOS, usuario.getEmail());
                editor.putBoolean(Constantes.PRIMEIRO_LOGIN, false);
                editor.apply();
                return true;
            }catch (Exception e){
                Log.e(TAG, "setUsuarioLogado: " + e.getMessage());
                return false;
            }
        }

        static Usuario getUsuarioConversa() {
            return usuarioConversa;
        }
        public static void setUsuarioConversa(Usuario _usuarioConversa) {
            usuarioConversa = _usuarioConversa;
        }

        //====================================================

        /*public static Uri getFoto(Context context){
            SharedPreferences pref = context.getSharedPreferences("info", Context.MODE_PRIVATE);
            if(foto == null)
                foto = Uri.parse(pref.getString(Config.USUARIO_LOGADO_FOTO, ""));
            return foto;
        }*/
        public static String getId(Context context){
            SharedPreferences pref = context.getSharedPreferences("info", Context.MODE_PRIVATE);
            if(id == null)
                id = pref.getString(Constantes.user.logado.TIPNAME, "");
            return id;
        }
        public static String getNome(Context context){
            SharedPreferences pref = context.getSharedPreferences("info", Context.MODE_PRIVATE);
            if(nome == null)
                nome = pref.getString(Constantes.user.logado.NOME, "");
            return Criptografia.descriptografar(nome);
        }
        public static String getEmail(Context context){
            SharedPreferences pref = context.getSharedPreferences("info", Context.MODE_PRIVATE);
            if(email == null)
                email = pref.getString(Constantes.user.logado.EMAIL, "");
            return Criptografia.descriptografar(email);
        }
        public static int getCategoria(Context context){
            SharedPreferences pref = context.getSharedPreferences("info", Context.MODE_PRIVATE);
            if(categoria  < 0)
                categoria = pref.getInt(Constantes.user.logado.CATEGORIA, 0);
            return categoria;
        }
        public static boolean isPrimeiroLogin(Context context){
            SharedPreferences pref = context.getSharedPreferences("info", Context.MODE_PRIVATE);
            return pref.getBoolean(Constantes.PRIMEIRO_LOGIN, true);
        }
    }
}
