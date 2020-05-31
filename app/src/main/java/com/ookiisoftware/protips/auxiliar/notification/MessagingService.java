package com.ookiisoftware.protips.auxiliar.notification;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ookiisoftware.protips.R;
import com.ookiisoftware.protips.activity.MainActivity;
import com.ookiisoftware.protips.auxiliar.Constantes;
import com.ookiisoftware.protips.auxiliar.Import;

public class MessagingService extends FirebaseMessagingService {

    private static final String TAG = "MessagingService";

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null) {
            String titulo = remoteMessage.getNotification().getTitle();
            String texto = remoteMessage.getNotification().getBody();
            String action = remoteMessage.getNotification().getClickAction();
            String channelId = remoteMessage.getNotification().getChannelId();

            if (channelId == null)
                channelId = "";

            Intent intent = null;
            if (action != null && !action.isEmpty()) {
                intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                switch (action) {
                    case Constantes.notification.action.OPEN_MAIN:
                        break;
                    case Constantes.notification.action.OPEN_NOTIFICATION:
                        intent.putExtra(Constantes.intent.PAGE_SELECT, Constantes.classes.fragments.pagerPosition.NOTIFICATIONS);
                        break;
                }
            }

            MyNotificationManager.getInstance(getApplicationContext()).create(titulo, texto, channelId, intent);

            /*String notificationBody = "";
            String notificationTitle = "";
            String notificationData = "";
            try{
                notificationData = remoteMessage.getData().toString();
                notificationTitle = remoteMessage.getNotification().getTitle();
                notificationBody = remoteMessage.getNotification().getBody();
            }catch (NullPointerException e){
                Import.Alert.e(TAG, "onMessageReceived: NullPointerException", e );
            }
            Import.Alert.d(TAG, "onMessageReceived: data: ", notificationData);
            Import.Alert.d(TAG, "onMessageReceived: notification body: ", notificationBody);
            Import.Alert.d(TAG, "onMessageReceived: notification title: ", notificationTitle);


            String dataType = remoteMessage.getData().get(getString(R.string.data_type));
            if(dataType.equals(getString(R.string.direct_message))){
                Import.Alert.d(TAG, "onMessageReceived: new incoming message", "");
                String title = remoteMessage.getData().get(getString(R.string.data_title));
                String message = remoteMessage.getData().get(getString(R.string.data_message));
                String messageId = remoteMessage.getData().get(getString(R.string.data_message_id));
                sendMessageNotification(title, message, messageId);
            }*/
        }
    }

}
