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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.ookiisoftware.protips.R;
import com.ookiisoftware.protips.auxiliar.Constantes;
import com.ookiisoftware.protips.auxiliar.Import;

import java.util.Objects;

public class RecuperarSenhaActivity extends AppCompatActivity {

    private static final String TAG = "RecuperarSenhaActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_senha);

        final Activity activity = this;
        final Toolbar toolbar = findViewById(R.id.toolbar);
        final ProgressBar progressBar = findViewById(R.id.progressBar);
        final EditText et_email = findViewById(R.id.et_email);
        final TextView enviar = findViewById(R.id.tv_enviar_email);
        final TextView info = findViewById(R.id.tv_info);

        progressBar.setVisibility(View.GONE);
        info.setVisibility(View.GONE);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
            et_email.setText(bundle.getString(Constantes.intent.EMAIL));

        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    enviar.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    Import.getFirebase.getAuth()
                            .sendPasswordResetEmail(et_email.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Import.Alert.snakeBar(activity, getResources().getString(R.string.verifique_seu_email));
                                    info.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.GONE);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    String texto = getResources().getString(R.string.erro_email_enviado);
                                    if (Objects.equals(e.getMessage(), "The email address is badly formatted."))
                                        texto = getResources().getString(R.string.email_invalido);

                                    Import.Alert.snakeBar(activity, texto);
                                    enviar.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.GONE);
                                    Import.Alert.erro(TAG, e);
                                }
                            });
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
