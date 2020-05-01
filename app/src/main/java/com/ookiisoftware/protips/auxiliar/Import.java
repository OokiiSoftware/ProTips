package com.ookiisoftware.protips.auxiliar;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ookiisoftware.protips.R;
import com.ookiisoftware.protips.activity.LoginActivity;
import com.ookiisoftware.protips.modelo.Activites;
import com.ookiisoftware.protips.modelo.Punter;
import com.ookiisoftware.protips.modelo.Post;
import com.ookiisoftware.protips.modelo.Tipster;
import com.ookiisoftware.protips.modelo.Usuario;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;

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

    static void notificacao(Context context, Intent intent, String titulo, String texto) {
        Import.Alert.msg(TAG, "Notificacao", titulo, texto);
        String CHANNEL_ID = "tips";

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteViews notificacaoSimples = new RemoteViews(context.getPackageName(), R.layout.notification_simples);
        RemoteViews notificacaoExpandida = new RemoteViews(context.getPackageName(), R.layout.notification_expandida);

        notificacaoExpandida.setOnClickPendingIntent(R.id.notification_titulo, pendingIntent);

        notificacaoSimples.setTextViewText(R.id.notification_titulo, titulo);
        notificacaoExpandida.setTextViewText(R.id.notification_titulo, titulo);
        notificacaoSimples.setTextViewText(R.id.notification_subtitulo, texto);
        notificacaoExpandida.setTextViewText(R.id.notification_texto, texto);
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager == null)
            return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, titulo, NotificationManager.IMPORTANCE_DEFAULT);

            //Roxo (notification_light_color)
            channel.setLightColor(Color.rgb(80, 0, 255));
            channel.setDescription(texto);
            channel.setSound(Constantes.notification.SOUND_DEFAULT, Constantes.notification.audioAttributes);
            channel.setVibrationPattern(Constantes.notification.VIBRATION);

            manager = context.getSystemService(NotificationManager.class);
            if (manager == null)
                return;
            manager.createNotificationChannel(channel);
        }
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification_icon_dark)
                .setCustomContentView(notificacaoSimples)
                .setCustomBigContentView(notificacaoExpandida)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setContentIntent(pendingIntent)
                .setSound(Constantes.notification.SOUND_DEFAULT)
                .setVibrate(Constantes.notification.VIBRATION)
                .setAutoCancel(true)
                .build();

        manager.notify(1, notification);
    }

    public static void organizarTituloTollbar(Activity activity, AppCompatTextView text_1, AppCompatTextView text_2, CharSequence title) {
        String[] titulo_da_pagina = title.toString().split(" ");
        if (titulo_da_pagina.length > 1) {
            text_1.setText(titulo_da_pagina[0]);
            text_1.setTypeface(Import.getFonteNormal(activity));
            text_2.setText(titulo_da_pagina[1]);
            text_2.setTypeface(Import.getFonteBold(activity));
        } else {
            text_1.setText(titulo_da_pagina[0]);
            text_1.setTypeface(Import.getFonteBold(activity));
            text_2.setText("");
        }
    }

    private static Typeface getFonteNormal(Context context){
        return Typeface.createFromAsset(context.getAssets(), "bebasneue_regular.otf");
    }

    private static Typeface getFonteBold(Context context){
        return Typeface.createFromAsset(context.getAssets(), "bebasneue_bold.otf");
    }

    /*public static boolean SalvarMensagemNoDispositivo(Context context, Mensagem mensagem) {
        try {
//            SQLiteMensagem db = new SQLiteMensagem(context);
//            db.update(mensagem);
            return true;
        } catch (Exception e){
            return false;
        }
    }*/

    /*public static boolean SalvarConversaNoDispositivo(Context context, Conversa conversa) {
        try {
            SQLiteConversa db = new SQLiteConversa(context);
//            db.update(context, conversa);
            return true;
        } catch (Exception e){
            return false;
        }
    }*/

    /*static void RemoverMensagemDoFirebase(DatabaseReference reference, Mensagem mensagem) {
        DatabaseReference refTemp = reference;
        refTemp = refTemp.child(mensagem.getData_de_envio());
        refTemp.removeValue();
    }*/

    public static String getSQLiteDatabaseName(Context context){
        SharedPreferences pref = context.getSharedPreferences("info", MODE_PRIVATE);
        return pref.getString(Constantes.SQLITE_BANCO_DE_DADOS, Criptografia.criptografar(getFirebase.getEmail()));
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

    public static void logOut(Activity activity) {
        limparDados();
        getFirebase.getAuth().signOut();
        LoginManager.getInstance().logOut();
        irProLogin(activity);
    }

    public static void irProLogin(Activity activity) {
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    public static void reiniciarApp(Activity activity) {
        limparDados();
        activity.finishAffinity();
        irProLogin(activity);
    }

    private static void limparDados() {
        get.tipsters.setPostes(new ArrayList<Post>());
        get.tipsters.setPuntersPendentes(new ArrayList<Punter>());
        get.tipsters.setTipsters(new ArrayList<Tipster>());
        get.tipsters.setTipstersAux(new ArrayList<Tipster>());
        get.punter.setPunters(new ArrayList<Punter>());
        get.punter.setPuntersAux(new ArrayList<Punter>());
    }

    //endregion

    public static Activites activites;

    public static class get {
        private static Random random = new Random();
        private static Locale locale = new Locale("pt", "BR");

        public static String randomString() {
            StringBuilder builder = new StringBuilder();
            char ch;
            for (int i = 0; i < 30; i++) {
                ch = (char) Math.floor(26 * random.nextDouble() + 65);
                builder.append(ch);
            }
            return builder.toString();
        }

        public static String Data() {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", locale);
            return dateFormat.format(c.getTime());
        }

        public static boolean hasConection(Activity activity) {
            final ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                if (Build.VERSION.SDK_INT < 23) {
                    final NetworkInfo ni = cm.getActiveNetworkInfo();
                    if (ni != null) {
                        return (ni.isConnected() && (ni.getType() == ConnectivityManager.TYPE_WIFI || ni.getType() == ConnectivityManager.TYPE_MOBILE));
                    }
                } else {
                    final Network n = cm.getActiveNetwork();
                    if (n != null) {
                        final NetworkCapabilities nc = cm.getNetworkCapabilities(n);
                        if (nc != null) {
                            return (nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI));
                        }
                    }
                }
            }
            return false;
        }

        public static class calendar {
            static Calendar calendario() {
                return Calendar.getInstance();
            }
            public static int dia () {
                return calendario().get(Calendar.DAY_OF_MONTH);
            }
            public static int mes () {
                return calendario().get(Calendar.MONTH);
            }
            public static int ano () {
                return calendario().get(Calendar.YEAR);
            }
        }

        public static class myComparator implements Comparator<Post> {
            public int compare(Post left, Post right) {
                return right.getData().compareTo(left.getData());
            }
        }

        public static class tipsters {
            private static ArrayList<Punter> puntersPendentes = new ArrayList<>();
            private static ArrayList<Tipster> tipstersAux = new ArrayList<>();
            private static ArrayList<Tipster> tipsters = new ArrayList<>();
            private static ArrayList<Post> postes = new ArrayList<>();

            // Esse Auxiliar serve para restaurar a lista principal no momento
            // de pesquisa onde a lista principal 'tipsters' sofre uma Clear()
            public static ArrayList<Punter> getPuntersPendentes() {
                return puntersPendentes;
            }
            public static ArrayList<Tipster> getAllAux() {
                return tipstersAux;
            }
            public static ArrayList<Tipster> getAll() {
                return tipsters;
            }

            static void setPuntersPendentes(ArrayList<Punter> value) {
                puntersPendentes = value;
            }

            static void setTipstersAux(ArrayList<Tipster> value) {
                tipstersAux = value;
            }

            public static void setTipsters(ArrayList<Tipster> value) {
                tipsters = value;
            }

            static void setPostes(ArrayList<Post> value) {
                postes = value;
            }

            public static ArrayList<Post> postes () {
                Collections.sort(postes, new myComparator());
                return postes;
            }

            public static Tipster findTipster(String value) {
                for (Tipster i : getAll())
                    if (i.getDados().getId().equals(value))
                        return i;
                return null;
            }

            public static Punter findPuntersPendentes(String value) {
                for (Punter i : getPuntersPendentes())
                    if (i.getDados().getId().equals(value))
                        return i;
                return null;
            }

            public static Post findPost(String value) {
                for (Post i : postes())
                    if (i.getId().equals(value))
                        return i;
                return null;
            }
        }

        public static class punter {
            private static ArrayList<Punter> punters = new ArrayList<>();
            private static ArrayList<Punter> puntersAux = new ArrayList<>();

            public static ArrayList<Punter> getAllAux () {
                return puntersAux;
            }
            public static ArrayList<Punter> getAll() {
                return punters;
            }

            public static void setPunters(ArrayList<Punter> value) {
                punters = value;
            }
            static void setPuntersAux(ArrayList<Punter> value) {
                puntersAux = value;
            }

            public static Punter find(String value) {
                for (Punter i : getAll())
                    if (i.getDados().getId().equals(value))
                        return i;
                return null;
            }

        }
    }

    public static class Alert{
        public static void toast(Activity activity, String texto) {
            Toast.makeText(activity, texto, Toast.LENGTH_LONG).show();
        }

        public static void snakeBar(View view, String texto, String buttonText, View.OnClickListener clickListener) {
            Snackbar.make(view, texto, Snackbar.LENGTH_INDEFINITE).setAction(buttonText, clickListener).show();
        }
        public static void snakeBar(Activity activity, String texto) {
            try {
                if (activity.getCurrentFocus() == null)
                    throw new Exception();

                Snackbar.make(activity.getCurrentFocus(), texto, Snackbar.LENGTH_LONG)
                        .setAction(activity.getResources().getString(R.string.fechar), null).show();
            } catch (Exception ignored) {
                toast(activity, texto);
            }
        }

        public static void msg(String tag, String titulo, String texto) {
            Log.e(tag, "msg: " + titulo + ": " + texto);
        }

        public static void msg(String tag, String titulo, String texto, String msg) {
            Log.e(tag, "msg: " + titulo + ": " + texto + ": " + msg);
        }

        public static void erro(String tag, Exception ex) {
            String msg = "erro:";
            msg += "\nMensagem: "+ ex.getMessage();
            msg += "\nLocalizedMessage: "+ ex.getLocalizedMessage();
            msg += "\n-------";
            Log.e(tag, msg);
        }

        public static void erro(String tag, String metodo, Exception ex) {
            String msg = "erro:";
            msg += "\nMetodo: " + metodo;
            msg += "\nMensagem: "+ ex.getMessage();
            msg += "\nLocalizedMessage: "+ ex.getLocalizedMessage();
            msg += "\n-------";
            Log.e(tag, msg);
        }
        public static void erro(String tag, String titulo, String texto) {
            Log.e(tag, "erro: " + titulo + ": " + texto);
        }
    }

    public static class getFirebase {
        private static FirebaseAuth firebaseAuth;
        private static DatabaseReference firebase;
        private static Punter punter;
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
                firebaseStorage = FirebaseStorage.getInstance().getReference();
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

        public static void setUser(Context context, Punter user) {
            if (user != null) {
                SharedPreferences pref = context.getSharedPreferences("info", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString(Constantes.user.logado.NOME, user.getDados().getNome());
                editor.putString(Constantes.user.logado.EMAIL, user.getDados().getEmail());
                editor.putString(Constantes.user.logado.TIPNAME, user.getDados().getId());
                editor.putString(Constantes.user.logado.FOTO, user.getDados().getFoto());

                editor.putBoolean(Constantes.intent.PRIMEIRO_LOGIN, false);
                editor.apply();
            }

            punter = user;
        }

        public static void setUser(Context context, Tipster user) {
            if (user != null) {
                SharedPreferences pref = context.getSharedPreferences("info", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString(Constantes.user.logado.NOME, user.getDados().getNome());
                editor.putString(Constantes.user.logado.EMAIL, user.getDados().getEmail());
                editor.putString(Constantes.user.logado.TIPNAME, user.getDados().getId());
                editor.putString(Constantes.user.logado.FOTO, user.getDados().getFoto());

                editor.putBoolean(Constantes.intent.PRIMEIRO_LOGIN, false);
                editor.apply();
            }

            tipster = user;
        }

        public static Punter getPunter() {
            return punter;
        }

        public static Tipster getTipster() {
            return tipster;
        }

        public static Usuario getUsuario() {
            if (isTipster())
                return getTipster().getDados();
            else if (getPunter() != null)
                return getPunter().getDados();
            return null;
        }

        public static boolean isTipster() {
            return tipster != null;
        }

        public static String getUltinoEmail(Context context) {
            SharedPreferences pref = context.getSharedPreferences("info", MODE_PRIVATE);
            return pref.getString(Constantes.user.logado.ULTIMO_EMAIL, "");
        }

        public static void setUltinoEmail(Context context, String email) {
            SharedPreferences.Editor editor = context.getSharedPreferences("info", MODE_PRIVATE).edit();
            editor.putString(Constantes.user.logado.ULTIMO_EMAIL, email);
            editor.apply();
        }

        public static void setGerencia(Activity activity, boolean isGerente) {
            SharedPreferences.Editor editor = activity.getSharedPreferences("info", MODE_PRIVATE).edit();
            editor.putBoolean(Constantes.presset.IS_GERENTE, isGerente);
            editor.apply();
        }

        public static boolean isGerente(Activity activity) {
            return activity.getSharedPreferences("info", MODE_PRIVATE).getBoolean(Constantes.presset.IS_GERENTE, false);
        }
    }

}
