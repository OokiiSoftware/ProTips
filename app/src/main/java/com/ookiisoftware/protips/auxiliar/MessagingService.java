package com.ookiisoftware.protips.auxiliar;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.ookiisoftware.protips.R;

public class MessagingService extends FirebaseMessagingService {

    private static final String TAG = "SegundoPlanoService";

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

    }

    private void recuperarToken () {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnSuccessListener(instanceIdResult -> {
                    // Get new Instance ID token
                    String token = instanceIdResult.getToken();

                    // Log and toast
//                    String msg = getString(R.string.msg_token_fmt, token);
//                    Import.Alert.msg(TAG, "recuperarToken \n", msg);
//                    Import.Alert.toast(null, msg);
                })
                .addOnFailureListener(e -> {
                    Import.Alert.erro(TAG, "getInstanceId failed", e);
                });
    }
}
