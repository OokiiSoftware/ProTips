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

public class MyNotificationManager {

    private Context context;
    @SuppressLint("StaticFieldLeak")
    private static MyNotificationManager instance;

    private MyNotificationManager(Context context) {
        this.context = context;
    }

    static synchronized MyNotificationManager getInstance(Context context) {
        if (instance == null)
            instance = new MyNotificationManager(context);
        return instance;
    }

    void create(String titulo, String texto, Intent intent) {
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
                .setAutoCancel(true);

        if (intent != null) {
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            notificacaoSimples.setOnClickPendingIntent(R.id.notification_titulo, pendingIntent);
            notificacaoExpandida.setOnClickPendingIntent(R.id.notification_titulo, pendingIntent);
            nBuilder.setContentIntent(pendingIntent);
        }

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (manager != null) {
            manager.notify(1, nBuilder.build());
        }
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
