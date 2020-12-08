package com.ookiisoftware.protips.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.ookiisoftware.protips.R;
import com.ookiisoftware.protips.auxiliar.Const;
import com.ookiisoftware.protips.auxiliar.Import;
import com.ookiisoftware.protips.modelo.UserDados;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.google.firebase.auth.PhoneAuthProvider.*;

public class RegistrarNumeroCelActivity extends AppCompatActivity {

    //region Variáveis

    private static final String TAG = "RegistrarNumeroCelActivity";
    private static final String VERIFICATION_ID = "VerificationId";
    private static final String MUMERO = "numero";

    private Activity activity;
    private String numero;

    private OnVerificationStateChangedCallbacks callbacks;
    private String mVerificationId = "";
    private ForceResendingToken mResendToken;

    private EditText telefone;
    private EditText codigo;
    private TextView btn_ok;
    private TextView texto;
    private ProgressBar progressBar;

    //endregion

    //region Overrides

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_numero_cel);
        activity = this;
        init();

        if (savedInstanceState != null) {
            String s = savedInstanceState.getString(VERIFICATION_ID);
            if (s != null) {
                mVerificationId = s;
                numero = savedInstanceState.getString(MUMERO);
                telefone.setText(numero);
                codigoEnviado();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(VERIFICATION_ID, mVerificationId);
        outState.putString(MUMERO, numero);
        super.onSaveInstanceState(outState);
    }

    //endregion

    //region Métodos

    private void init() {
        //region findViewById
        final TextView erro = findViewById(R.id.tv_erro);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        progressBar = findViewById(R.id.progressBar);
        btn_ok = findViewById(R.id.tv_enviar_codigo);
        telefone = findViewById(R.id.et_telefone);
        codigo = findViewById(R.id.et_codigo);
        texto = findViewById(R.id.tv_texto);
        //endregion

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        progressBar.setVisibility(View.GONE);
        erro.setVisibility(View.GONE);

        SimpleMaskFormatter formatter = new SimpleMaskFormatter(Const.formats.TELEFONE);
        MaskTextWatcher watcher = new MaskTextWatcher(telefone, formatter);
        telefone.addTextChangedListener(watcher);

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
                progressBar.setVisibility(View.GONE);
                Import.getFirebase.getUser()
                        .updatePhoneNumber(phoneAuthCredential)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
//                                boolean isTipster = Import.getFirebase.isTipster();
                                UserDados userDados = Import.getFirebase.getUsuario();
                                if (userDados != null)
                                    userDados.atualizarNumero(numero);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                erro.setText(getResources().getString(R.string.erro_atualizar_numero));
                                erro.setVisibility(View.VISIBLE);
                                Import.Alert.e(TAG, "onVerificationCompleted erro", e);
                            }
                        });
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.

                progressBar.setVisibility(View.GONE);
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Import.Alert.e(TAG, "onVerificationFailed 1", e);
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Import.Alert.e(TAG, "onVerificationFailed 2", e);
                } else
                    Import.Alert.e(TAG, "onVerificationFailed 3", e);
                erro.setText(getResources().getString(R.string.erro_enviar_codigo));
                erro.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull ForceResendingToken token) {
                super.onCodeSent(verificationId, token);
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
                codigoEnviado();
                Import.Alert.d(TAG, "init", "onCodeSent");
            }
        };

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                erro.setVisibility(View.GONE);
                if (Objects.equals(getResources().getString(R.string.ok), btn_ok.getText().toString())) {
                    parte_2();
                } else
                    parte_1();
            }
        });
    }

    private void parte_1() {
        numero = telefone.getText().toString();
        numero = numero
                .replace("(", "")
                .replace(")", "")
                .replace(" ", "")
                .replace("-", "");
        if (numero.isEmpty()) {
            telefone.setError(getResources().getString(R.string._exclamação));
        } else if (numero.length() <  14) {
            telefone.setError(getResources().getString(R.string.numero_invalido));
        } else {
            getInstance().verifyPhoneNumber(numero, 60, TimeUnit.SECONDS, activity, callbacks);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void parte_2() {
        String cod = codigo.getText().toString();
        if (cod.isEmpty()) {
            codigo.setError(getResources().getString(R.string._exclamação));
        } else if (cod.length() !=  mVerificationId.length()) {
            codigo.setError(getResources().getString(R.string.numero_invalido));
        } else {
            btn_ok.setEnabled(false);
        }
    }

    private void codigoEnviado() {
        texto.setText(getResources().getString(R.string.info_registrar_codigo));
        telefone.setFocusable(false);
        progressBar.setVisibility(View.GONE);
        codigo.setVisibility(View.VISIBLE);
        btn_ok.setText(getResources().getString(R.string.ok));
    }

    //endregion

}
