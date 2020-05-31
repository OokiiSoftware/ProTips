package com.ookiisoftware.protips.auxiliar.notification;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.ookiisoftware.protips.R;
import com.ookiisoftware.protips.auxiliar.Constantes;
import com.ookiisoftware.protips.auxiliar.Import;
import com.ookiisoftware.protips.modelo.Notificacao;
import com.ookiisoftware.protips.modelo.Post;
import com.ookiisoftware.protips.modelo.User;

public class MyNotificationManager {

//    private static final String TAG = "MyNotificationManager";
    private Context context;
    @SuppressLint("StaticFieldLeak")
    private static MyNotificationManager instance;

    private MyNotificationManager(Context context) {
        this.context = context;
    }

    public static synchronized MyNotificationManager getInstance(Context context) {
        if (instance == null)
            instance = new MyNotificationManager(context);
        return instance;
    }

    public void create(String titulo, String texto, String channelId, Intent intent) {
        RemoteViews notificacaoSimples = new RemoteViews(context.getPackageName(), R.layout.notification_simples);
        RemoteViews notificacaoExpandida = new RemoteViews(context.getPackageName(), R.layout.notification_expandida);

        notificacaoSimples.setTextViewText(R.id.notification_titulo, titulo);
        notificacaoExpandida.setTextViewText(R.id.notification_titulo, titulo);
        notificacaoSimples.setTextViewText(R.id.notification_subtitulo, texto);
        notificacaoExpandida.setTextViewText(R.id.notification_texto, texto);

        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(context, Constantes.notification.CHANNEL_ID_DEFAULT)
                .setSmallIcon(R.drawable.ic_notification_icon_dark)
                .setSound(Constantes.notification.SOUND_DEFAULT)
                .setVibrate(Constantes.notification.VIBRATION)
                .setCustomContentView(notificacaoSimples)
                .setCustomBigContentView(notificacaoExpandida)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setSound(Constantes.notification.SOUND_DEFAULT)
                .setVibrate(Constantes.notification.VIBRATION)
                .setContentTitle(titulo)
                .setContentText(texto)
                .setChannelId(channelId)
                .setAutoCancel(true);

        if (intent != null) {
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            notificacaoSimples.setOnClickPendingIntent(R.id.notification_titulo, pendingIntent);
            notificacaoExpandida.setOnClickPendingIntent(R.id.notification_titulo, pendingIntent);
            nBuilder.setContentIntent(pendingIntent);
        }

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (manager != null) {
            manager.notify(1, nBuilder.build());
        }
    }

    public void sendNewPost(Post post, User user) {
        if (post == null)
            return;
        String texto = post.getTitulo();
        texto += "\n" +  context.getResources().getString(R.string.esporte) + ": " + post.getEsporte();
        texto += "\n" + context.getResources().getString(R.string.odd_atual) + ": " + post.getOdd_atual();

        if (post.getCampeonato() != null && !post.getCampeonato().isEmpty())
            texto += "\n" + context.getResources().getString(R.string.campeonato) + ": " + post.getCampeonato();

        if (post.getOdd_minima() != null && !post.getOdd_minima().isEmpty() && post.getOdd_maxima() != null && !post.getOdd_maxima().isEmpty())
            texto += "\n" + context.getResources().getString(R.string.odd) + ": " + post.getOdd_minima() + " - " + post.getOdd_maxima();
        texto += "\n" + context.getResources().getString(R.string.horario) + ": " + post.getHorario_minimo() + " - " + post.getHorario_maximo();

        try {
            Notificacao notificacao = new Notificacao();
            notificacao.setTitle(context.getResources().getString(R.string.novo_poste) + ": " + Import.getFirebase.getUsuario().getNome());
            notificacao.setBody(texto);
            notificacao.setDe(Import.getFirebase.getId());
            notificacao.setPara(user.getDados().getId());
            notificacao.setToken(user.getDados().getToken());
            notificacao.setChannel(Constantes.notification.id.NOVO_POST);
            notificacao.setAction(Constantes.notification.action.OPEN_MAIN);
            notificacao.enviar();
        } catch (Exception ignored) {}

        /*AsyncTask.execute(() -> {
            int SDK_INT = Build.VERSION.SDK_INT;
            if (SDK_INT > 4) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                try {
                    String CHAR_SET = "UTF-8";
                    String jsonResponse = "";

                    String jsonBody = "{" +
                            "\"app_id\": \"2e0c04c8-1e0e-4c79-ae0a-115c77e4e456\"," +
                            "\"filters\": [{\"field\": \"tag\",\"key\": \"" + Constantes.oneSignal.tag.FIREBASE_ID + "\",\"relation\": \"m\",\"value\": \"" + userUid + "\"}]," +
                            "\"data\": { \"foo\": \"bar\" }," +
                            "\"contents\": { \"en\": \"" + "texto" + "\" }" +
                            "}";

                    URL url = new URL("https://onesignal.com/api/v1/notifications");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setUseCaches(false);
                    con.setDoOutput(true);
                    con.setDoInput(true);

                    con.setRequestProperty(Constantes.oneSignal.request.CONTENT_TYPE, "application/json; charset=UTF-8");
                    con.setRequestProperty(Constantes.oneSignal.request.AUTORIZACAO, "Basic Njg2NTU5MjAtMDYwNi00YWVhLWIxMTEtNjk3ZTU5NjdkZTkx");
                    con.setRequestMethod("POST");

                    byte[] sendBytes = jsonBody.getBytes(CHAR_SET);
                    con.setFixedLengthStreamingMode(sendBytes.length);

                    OutputStream outputStream = con.getOutputStream();
                    outputStream.write(sendBytes);

                    int httpResponse = con.getResponseCode();
                    if (httpResponse >= HttpURLConnection.HTTP_OK && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                        Scanner scanner = new Scanner(con.getInputStream(), CHAR_SET);
                        jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                        scanner.close();
                    } else {
                        Scanner scanner = new Scanner(con.getErrorStream(), CHAR_SET);
                        jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                        scanner.close();
                    }
                    Import.Alert.d(TAG, "sendNewPost", jsonResponse);
                } catch (Exception e) {
                    Import.Alert.e(TAG, "sendNewPost", e);
                }
            }
        });*/
    }

    public void sendSolicitacao(User user) {
        try {
            String titulo = context.getResources().getString(R.string.app_name);
            String texto = context.getResources().getString(R.string.nova_solicitação_filiado) + "\n" +
                    Import.getFirebase.getUsuario().getNome();

            Notificacao notificacao = new Notificacao();
            notificacao.setTitle(titulo);
            notificacao.setBody(texto);
            notificacao.setDe(Import.getFirebase.getId());
            notificacao.setPara(user.getDados().getId());
            notificacao.setToken(user.getDados().getToken());
            notificacao.setChannel(Constantes.notification.id.NOVO_PUNTER_PENDENTE);
            notificacao.setAction(Constantes.notification.action.OPEN_NOTIFICATION);
            notificacao.enviar();
        } catch (Exception ignored) {}
    }

    public void sendSolicitacaoAceita(User user) {
        try {
            String titulo = context.getResources().getString(R.string.app_name);
            String texto = context.getResources().getString(R.string.filiado_aceito) + "\n" +
                    context.getResources().getString(R.string.tipster) + ": " + Import.getFirebase.getUsuario().getNome();

            Notificacao notificacao = new Notificacao();
            notificacao.setTitle(titulo);
            notificacao.setBody(texto);
            notificacao.setDe(Import.getFirebase.getId());
            notificacao.setPara(user.getDados().getId());
            notificacao.setToken(user.getDados().getToken());
            notificacao.setChannel(Constantes.notification.id.NOVO_PUNTER_ACEITO);
            notificacao.setAction(Constantes.notification.action.OPEN_MAIN);
            notificacao.enviar();
        } catch (Exception ignored) {}
    }

    public static void criarChannelNotification(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(Constantes.notification.CHANNEL_ID_DEFAULT,
                    Constantes.notification.CHANNEL_NOME, NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager == null)
                return;

            channel.enableLights(true);
            channel.enableVibration(true);
            //Roxo (notification_light_color)
            channel.setLightColor(Color.rgb(80, 0, 255));
            channel.setVibrationPattern(Constantes.notification.VIBRATION);
            channel.setDescription(Constantes.notification.CHANNEL_DESCRICAO);
            channel.setSound(Constantes.notification.SOUND_DEFAULT, Constantes.notification.audioAttributes);

            manager.createNotificationChannel(channel);
        }
    }

}
