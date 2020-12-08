package com.ookiisoftware.protips.auxiliar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.facebook.login.LoginManager;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.ookiisoftware.protips.R;
import com.ookiisoftware.protips.activity.LoginActivity;
import com.ookiisoftware.protips.modelo.Activites;
//import com.ookiisoftware.protips.modelo.Punter;
import com.ookiisoftware.protips.modelo.AutoComplete;
import com.ookiisoftware.protips.modelo.Post;
import com.ookiisoftware.protips.modelo.User;
import com.ookiisoftware.protips.modelo.UserDados;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;

public class Import {
    private static final String TAG = "Import";
    public static Activites activites;

    //region Métodos

    public static void verificar_atualizacao(Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.setTitle(activity.getResources().getString(R.string.verificando_atualizacao));
        final ProgressBar progressBar = new ProgressBar(activity);
        dialog.setContentView(progressBar);

        final DatabaseReference ref = Import.getFirebase.getReference()
                .child(Const.firebase.child.VERSAO);

        final ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Long> values = new ArrayList<>();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Long item = data.getValue(Long.class);
                    if (item != null) {
                        values.add(item);
                    }
                }
                if (values.size() > 0) {
                    final long ultima = values.get(values.size() -1);
                    if (ultima > Const.APP_VERSAO) {
                        dialog.dismiss();
                        final AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
                        dialog.setTitle(activity.getResources().getString(R.string.atualizacao_disponivel));
                        dialog.setPositiveButton(activity.getResources().getString(R.string.baixar), (dialog1, which) -> {
                            String appName = "protips_" + ultima + ".apk";
                            Import.Alert.toast(activity, "aguarde");
                            Import.Alert.d(TAG, "verificar_atualizacao", appName);
                            Import.getFirebase.getStorage()
                                    .child(Const.firebase.child.APP)
                                    .child(appName)
                                    .getDownloadUrl()
                                    .addOnSuccessListener(uri -> {
                                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                        activity.startActivity(intent);
                                    })
                                    .addOnFailureListener(e -> {
                                        Import.Alert.e(TAG, "verificar_atualizacao", e);
                                        atualizacao_erro();
                                    });
                        });
                        dialog.show();
                    } else {
                        sem_atualizacao();
                    }
                } else {
                    sem_atualizacao();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}

            private void atualizacao_erro() {
                Import.Alert.toast(activity, "erro ao baixar");
            }

            private void sem_atualizacao() {
                Import.Alert.toast(activity, "sem atualização");
                dialog.dismiss();
            }
        };

        ref.addListenerForSingleValueEvent(eventListener);
        dialog.setOnDismissListener(dialog12 -> ref.removeEventListener(eventListener));
        dialog.show();
    }

    public static void abrirLink(Activity activity, String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            activity.startActivity(intent);
        } catch (Exception e) {
            Alert.e(TAG, "abrirLink", e);
            Alert.toast(activity, activity.getResources().getString(R.string.erro_abrir_link));
        }
    }

    public static void abrirCropView(Activity activity, int ratio) {
        if (ratio > 0)
            CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(ratio, ratio).start(activity);
        else
            CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(activity);
    }

    public static void abrirCropView(Activity activity, Fragment fragment, int ratio) {
        if (ratio > 0)
            CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(ratio, ratio).start(activity, fragment);
        else
            CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(activity, fragment);
    }

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
            Alert.e(TAG, ex);
        }
        return reborn;
    }

    public static void notificacaoCancel(Activity activity, String id) {
        try {
            String ns = Context.NOTIFICATION_SERVICE;
            NotificationManager manager = (NotificationManager) activity.getSystemService(ns);
            if (manager != null) {
                manager.cancel(Integer.parseInt(id));
            }
        } catch (Exception ignored) {}
    }

    public static void organizarTituloTollbar(Activity activity, AppCompatTextView text_1, AppCompatTextView text_2, CharSequence title) {
        String[] titulo_da_pagina = title.toString().split(" ");
        if (titulo_da_pagina.length > 1) {
            text_1.setText(titulo_da_pagina[0]);
            text_1.setTypeface(Import.get.FonteNormal(activity));
            text_2.setText(titulo_da_pagina[1]);
            text_2.setTypeface(Import.get.FonteBold(activity));
        } else {
            text_1.setText(titulo_da_pagina[0]);
            text_1.setTypeface(Import.get.FonteBold(activity));
            text_2.setText("");
        }
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
        if (getFirebase.getTipster() != null)
            getFirebase.getTipster().logout();
        getFirebase.setGerencia(activity, false);
        irProLogin(activity);
    }

    public static void irProLogin(Activity activity) {
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    public static void reiniciarApp(Activity activity) {
        limparDados();
        Import.getFirebase.deleteTipster(activity);
        Import.getFirebase.setUser(activity, (User) null, false);
        activity.finishAffinity();
        irProLogin(activity);
    }

    private static void limparDados() {
        get.seguindo.resetAll();
        get.tipsters.resetAll();
        get.seguidores.resetAll();
        get.solicitacao.resetAll();
    }

    //endregion

    public static class get {
        private static Random random = new Random();
        private static AutoComplete autoComplete;
        private static Locale locale = new Locale("pt", "BR");

        public static void setAutoComplete(AutoComplete autoComplete) {
            get.autoComplete = autoComplete;
        }

        public static void fotoDaGaleria(Activity activity) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            activity.startActivityForResult(intent, Const.permissions.STORANGE);
        }
        private static Typeface FonteNormal(Context context){
            return Typeface.createFromAsset(context.getAssets(), "bebasneue_regular.otf");
        }

        private static Typeface FonteBold(Context context){
            return Typeface.createFromAsset(context.getAssets(), "bebasneue_bold.otf");
        }

        public static String SQLiteDatabaseName(Context context){
            SharedPreferences pref = context.getSharedPreferences("info", MODE_PRIVATE);
            return pref.getString(Const.sqlite.SQLITE_BANCO_DE_DADOS, Criptografia.criptografar(getFirebase.getEmail()));
        }

        public static AutoComplete autoComplete() {
            return autoComplete;
        }

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
            public static String hoje() {
                int mes = mes() + 1;// O mês retorna o valor com 1 a menos
                int dia = dia();
                String mesS = "" + mes;
                String diaS = "" + dia;
                if (mesS.length() == 1)
                    mesS = "0" + mesS;
                if (diaS.length() == 1)
                    diaS = "0" + diaS;

                return ano() + "-" + mesS + "-" + diaS;
            }
        }

        public static class tipsters {
            private static ArrayList<User> usersAux = new ArrayList<>();
            private static ArrayList<User> users = new ArrayList<>();
            private static ArrayList<Post> postes = new ArrayList<>();

            public static ArrayList<User> getAllAux() {
                return usersAux;
            }
            public static ArrayList<User> getAll() {
                return users;
            }

            public static ArrayList<Post> getPostes () {
                return postes;
            }

            public static void remove(String key) {
                User item = findUser(key);
                users.remove(item);
                usersAux.remove(item);
            }
            public static void remove(Post post) {
                postes.remove(post);
            }

            public static void add(User user) {
                User item = findUser(user.getDados().getId());
                if (item == null) {
                    users.add(user);
                    usersAux.add(user);
                } else {
                    users.set(users.indexOf(item), user);
                    usersAux.set(usersAux.indexOf(item), user);
                }
                Collections.sort(users, new User.sortByMedia());
            }
            public static void add(Post post) {
                Post item = null;
                for (Post p : postes)
                    if (p.getId().equals(post.getId())) {
                        item = p;
                        break;
                    }
                if (item == null) {
                    postes.add(post);
                    Collections.sort(postes, new Post.sortByDate());
                }
            }
            public static void addAll(HashMap<String, Post> posts) {
                postes.addAll(posts.values());
                Collections.sort(postes, new Post.sortByDate());
            }

            public static User get(String key) {
                return findUser(key);
            }

            private static User findUser(String key) {
                for (User p : users)
                    if (p.getDados().getId().equals(key)) {
                        return p;
                    }
                return null;
            }

            private static void resetAll() {
                usersAux.clear();
                users.clear();
                postes.clear();
            }
        }
        public static class seguindo {
            private static ArrayList<User> usersAux = new ArrayList<>();
            private static ArrayList<User> users = new ArrayList<>();
            private static ArrayList<Post> postes = new ArrayList<>();

            public static ArrayList<User> getAllAux() {
                return usersAux;
            }
            public static ArrayList<User> getAll() {
                return users;
            }

            public static ArrayList<Post> getPostes () {
                return postes;
            }

            public static void remove(String key) {
                User item = findUser(key);
                users.remove(item);
                usersAux.remove(item);
            }
            public static void remove(Post post) {
                postes.remove(findPost(post.getId()));
            }

            public static void add(User user) {
                User item = findUser(user.getDados().getId());
                if (item == null) {
                    users.add(user);
                    usersAux.add(user);
                } else {
                    users.set(users.indexOf(item), user);
                    usersAux.set(usersAux.indexOf(item), user);
                }
                Collections.sort(users, new User.sortByMedia());
            }
            public static void add(Post post) {
                Post item = null;
                for (Post p : postes)
                    if (p.getId().equals(post.getId())) {
                        item = p;
                        break;
                    }
                if (item == null) {
                    postes.add(post);
                    Collections.sort(postes, new Post.sortByDate());
                }
            }
            public static void addAll(HashMap<String, Post> posts) {
                for (Post p : posts.values()) {
                    Post i = findPost(p.getId());
                    if (i == null)
                        postes.add(p);
                }
                Collections.sort(postes, new Post.sortByDate());
            }

            public static User get(String key) {
                return findUser(key);
            }

            private static User findUser(String key) {
                for (User p : users)
                    if (p.getDados().getId().equals(key)) {
                        return p;
                    }
                return null;
            }
            private static Post findPost(String key) {
                for (Post p : postes)
                    if (p.getId().equals(key))
                        return p;
                    return null;
            }

            private static void resetAll() {
                usersAux.clear();
                users.clear();
                postes.clear();
            }
        }
        public static class seguidores {
            private static ArrayList<User> usersAux = new ArrayList<>();
            private static ArrayList<User> users = new ArrayList<>();

            public static ArrayList<User> getAllAux() {
                return usersAux;
            }
            public static ArrayList<User> getAll() {
                return users;
            }

            public static void remove(String key) {
                User item = findUser(key);
                users.remove(item);
                usersAux.remove(item);
            }

            public static void add(User user) {
                User item = findUser(user.getDados().getId());
                if (item == null) {
                    users.add(user);
                    usersAux.add(user);
                } else {
                    users.set(users.indexOf(item), user);
                    usersAux.set(usersAux.indexOf(item), user);
                }
                Collections.sort(users, new User.sortByMedia());
            }

            public static User get(String key) {
                return findUser(key);
            }

            private static User findUser(String key) {
                for (User p : users)
                    if (p.getDados().getId().equals(key)) {
                        return p;
                    }
                return null;
            }

            private static void resetAll() {
                usersAux.clear();
                users.clear();
            }
        }
        public static class solicitacao {
            private static ArrayList<User> users = new ArrayList<>();

            public static ArrayList<User> getAll() {
                return users;
            }

            public static void remove(String key) {
                User item = findUser(key);
                users.remove(item);
            }

            public static void add(User user) {
                User item = findUser(user.getDados().getId());
                if (item == null) {
                    users.add(user);
                } else {
                    users.set(users.indexOf(item), user);
                }
                Collections.sort(users, new User.sortByMedia());
            }

            public static User get(String key) {
                return findUser(key);
            }

            private static User findUser(String key) {
                for (User p : users)
                    if (p.getDados().getId().equals(key)) {
                        return p;
                    }
                return null;
            }

            private static void resetAll() {
                users.clear();
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

        public static void d(String tag, String titulo, String texto) {
            Log.e(tag, "msg: " + titulo + ": " + texto);
        }

        public static void d(String tag, String titulo, String texto, String msg) {
            Log.e(tag, "msg: " + titulo + ": " + texto + ": " + msg);
        }

        public static void e(String tag, Exception ex) {
            String msg = "erro:";
            msg += "\nMensagem: "+ ex.getMessage();
            msg += "\nLocalizedMessage: "+ ex.getLocalizedMessage();
            msg += "\n-------";
            Log.e(tag, msg);
        }

        public static void e(String tag, String metodo, Exception ex) {
            String msg = "erro:";
            msg += "\nMetodo: " + metodo;
            msg += "\nMensagem: "+ ex.getMessage();
            msg += "\nLocalizedMessage: "+ ex.getLocalizedMessage();
            msg += "\n-------";
            Log.e(tag, msg);
        }
        public static void e(String tag, String titulo, String texto) {
            Log.e(tag, "erro: " + titulo + ": " + texto);
        }
    }

    public static class getFirebase {
        private static FirebaseAuth auth;
        private static DatabaseReference reference;
        private static String token;

        private static User tipster;

        private static StorageReference firebaseStorage;

        //region get

        public static DatabaseReference getReference() {
            if(reference == null)
                reference = FirebaseDatabase.getInstance().getReference();
            return reference;
        }

        public static FirebaseAuth getAuth() {
            if(auth == null)
                auth = FirebaseAuth.getInstance();
            return auth;
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

        public static User getTipster() {
            return tipster;
        }

        public static UserDados getUsuario() {
                return getTipster().getDados();
        }

        public static String getUltinoEmail(Context context) {
            SharedPreferences pref = context.getSharedPreferences("info", MODE_PRIVATE);
            return pref.getString(Const.user.logado.ULTIMO_EMAIL, "");
        }

        public static boolean isTipster() {
            if (getTipster() == null)
                return false;
            return getTipster().getDados().isTipster();
        }

        public static boolean isGerente(Activity activity) {
            return activity.getSharedPreferences("info", MODE_PRIVATE).getBoolean(Const.presset.IS_GERENTE, false);
        }

        public static boolean isFilePresent(Context context, String fileName) {
            String path = context.getFilesDir().getAbsolutePath() + "/" + fileName;
            File file = new File(path);
            return file.exists();
        }

        public static String getToken() {
            return token;
        }

        //endregion

        //region set

        public static void setUser(Context context, User user, boolean save) {
            if (save) {
                Gson gson = new Gson();
                String json = gson.toJson(user);

                create(context, Const.files.USER_JSON, json);
            }

            tipster = user;
        }

        public static void setUltinoEmail(Context context, String email) {
            SharedPreferences.Editor editor = context.getSharedPreferences("info", MODE_PRIVATE).edit();
            editor.putString(Const.user.logado.ULTIMO_EMAIL, email);
            editor.apply();
        }

        public static void setGerencia(Activity activity, boolean isGerente) {
            SharedPreferences.Editor editor = activity.getSharedPreferences("info", MODE_PRIVATE).edit();
            editor.putBoolean(Const.presset.IS_GERENTE, isGerente);
            editor.apply();
        }

        public static void setToken(String token) {
            getFirebase.token = token;
        }

        //endregion

//        public static void deletePunter(Context context) {
//            context.deleteFile(Constantes.files.PUNTER_JSON);
//        }

        public static void deleteTipster(Context context) {
            context.deleteFile(Const.files.USER_JSON);
        }

        public static void saveUser(Context context) {
            setUser(context, getTipster(), true);
//            if (isTipster()) {
//            } else {
//                setUser(context, getPunter(), true);
//            }
        }

        public static String read(Context context, String fileName) {
            try {
                FileInputStream fis = context.openFileInput(fileName);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader bufferedReader = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
                return sb.toString();
            } catch (IOException fileNotFound) {
                return null;
            }
        }

        private static boolean create(Context context, String fileName, String jsonString){
            try {
                FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
                if (jsonString != null) {
                    fos.write(jsonString.getBytes());
                }
                fos.close();
                return true;
            } catch (IOException fileNotFound) {
                Alert.e(TAG, "create", fileNotFound);
                return false;
            }
        }

    }

}
