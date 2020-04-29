package com.ookiisoftware.protips.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.ookiisoftware.protips.R;
import com.ookiisoftware.protips.auxiliar.Import;

import java.util.concurrent.TimeUnit;

import static com.google.firebase.auth.PhoneAuthProvider.*;

public class RegistrarNumeroCelActivity extends AppCompatActivity {

    //region Variáveis

    private static final String TAG = "RegistrarNumeroCelActivity";
    private static final String VERIFICATION_ID = "VerificationId";

    private Activity activity;
    private int parte;
    private String numero;

    private OnVerificationStateChangedCallbacks callbacks;
    private String mVerificationId = "";
    private ForceResendingToken mResendToken;

    //endregion

    //region Overrides

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_numero_cel);
        activity = this;
        Init();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(VERIFICATION_ID, mVerificationId);
        super.onSaveInstanceState(outState);
    }

    //endregion

    //region Métodos

    private void Init() {
        final TextView texto = findViewById(R.id.tv_texto);
        final EditText telefone = findViewById(R.id.et_telefone);
        final EditText codigo = findViewById(R.id.et_codigo);
        final TextView btn_ok = findViewById(R.id.tv_enviar_codigo);
        parte = 0;

        callbacks = new OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                texto.setText(getResources().getString(R.string.info_numero_registrado));
                Import.getFirebase.getUser().updatePhoneNumber(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            boolean isTipster = Import.getFirebase.isTipster();
                            Import.getFirebase.getUsuario().atualizarNumero(numero, isTipster);
                        }
                    }
                });
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Import.Alert.erro(TAG, e);
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Import.Alert.erro(TAG, e);
                }

                // Show a message and update the UI
                // ...
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull ForceResendingToken token) {
                super.onCodeSent(verificationId, token);
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                texto.setText(getResources().getString(R.string.info_registrar_codigo));
                telefone.setFocusable(false);
                codigo.setVisibility(View.VISIBLE);
                btn_ok.setText(getResources().getString(R.string.ok));
            }
        };

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (parte) {
                    case 0: {
                        numero = telefone.getText().toString();
                        if (numero.isEmpty()) {
                            telefone.setError(getResources().getString(R.string._exclamação));
                        } else if (numero.length() <  11) {
                            telefone.setError(getResources().getString(R.string.numero_invalido));
                        } else {
                            getInstance().verifyPhoneNumber(
                                    numero, 60, TimeUnit.SECONDS, activity, callbacks
                            );
                            parte++;
                        }
                    }
                    case 1: {
                        String cod = codigo.getText().toString();
                        if (cod.isEmpty()) {
                            codigo.setError(getResources().getString(R.string._exclamação));
                        } else if (cod.length() !=  mVerificationId.length()) {
                            codigo.setError(getResources().getString(R.string.numero_invalido));
                        } else {
                            btn_ok.setEnabled(false);
                            parte++;
                        }
                    }
                }
            }
        });
    }

    //endregion

}
