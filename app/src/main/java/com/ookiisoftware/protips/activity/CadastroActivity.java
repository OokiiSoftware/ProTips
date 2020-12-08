package com.ookiisoftware.protips.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ookiisoftware.protips.R;
import com.ookiisoftware.protips.auxiliar.Const;
import com.ookiisoftware.protips.modelo.UserDados;
import com.ookiisoftware.protips.auxiliar.Import;

import java.util.Objects;
import java.util.Random;

public class CadastroActivity extends AppCompatActivity {

    //region Variáveis

//    private final String TAG = "CadastroActivity";
    private Random random = new Random();
    private Activity activity;

    private EditText et_nome, et_email, et_senha, et_confirmar_senha;
    private ProgressBar progressBar;
    private ImageView foto;

    private int radAtual;
    private int rad;
    //endregion

    //region Override

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        activity = this;
        Init();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    //endregion

    //region Métodos

    private void Init() {
        //region findViewById
        progressBar = findViewById(R.id.progressBar);
        et_nome = findViewById(R.id.et_nome);
        et_email = findViewById(R.id.et_email);
        et_senha = findViewById(R.id.et_senha);
        foto = findViewById(R.id.iv_foto);
        et_confirmar_senha = findViewById(R.id.et_confirmar_senha);
        FloatingActionButton cadastrar = findViewById(R.id.fab_cadastrar);
        //endregion

        progressBar.setVisibility(View.INVISIBLE);

        //region setListener
        cadastrar.setOnClickListener(view -> {
            String nome = et_nome.getText().toString();
            String email = et_email.getText().toString();
            String senha = et_senha.getText().toString();
            String senha_2 = et_confirmar_senha.getText().toString();
            if (rad == 2 && radAtual == 5) {
                UserDados userDados = new UserDados();
                userDados.setEmail(email);
                userDados.setSenha(senha);
                login(userDados);
            } else {
                if(nome.isEmpty())
                    et_nome.setError(getResources().getString(R.string.campo_obrigatório));
                else if(email.isEmpty())
                    et_email.setError(getResources().getString(R.string.campo_obrigatório));
                else if(senha.isEmpty())
                    et_senha.setError(getResources().getString(R.string.campo_obrigatório));
                else if(senha_2.isEmpty())
                    et_confirmar_senha.setError(getResources().getString(R.string.campo_obrigatório));
                else if(!senha.equals(senha_2))
                    et_confirmar_senha.setError(getResources().getString(R.string.aviso_cadastro_senhas_diferentes));
                else {
                    OrganizarDados(nome, email, senha);
                }
            }
        });

        foto.setOnClickListener(view -> {
            radAtual = random.nextInt(6);
            switch (radAtual) {
                case 0:
                    foto.setBackground(getDrawable(R.drawable.bg_circulo_amarelo));
                    break;
                case 1:
                    foto.setBackground(getDrawable(R.drawable.bg_circulo_vermelho));
                    break;
                case 2:
                    foto.setBackground(getDrawable(R.drawable.bg_circulo_primary_light));
                    break;
                case 3:
                    foto.setBackground(getDrawable(R.drawable.bg_circulo_primary_light_2));
                    break;
                case 4:
                    foto.setBackground(getDrawable(R.drawable.bg_circulo_verde));
                    break;
                case 5:
                    foto.setBackground(getDrawable(R.drawable.bg_circulo_preto));
                    rad++;
                    break;
            }
        });
        //endregion
    }

    private void OrganizarDados(String nome, String email, String senha) {
        UserDados userDados = new UserDados();
        userDados.setNome(nome);
        userDados.setEmail(email);
        userDados.setSenha(senha);
        criarContaNoFirebase(userDados);
    }

    private void criarContaNoFirebase(final UserDados userDados) {
        progressBar.setVisibility(View.VISIBLE);
        final FirebaseAuth firebaseAuth = Import.getFirebase.getAuth();
        firebaseAuth.createUserWithEmailAndPassword(userDados.getEmail(), userDados.getSenha())
        .addOnCompleteListener(this, task -> {
            if(task.isSuccessful() && task.getResult() != null && task.getResult().getUser() != null) {
                final FirebaseUser user = task.getResult().getUser();
                final UserProfileChangeRequest changeRequest = new UserProfileChangeRequest.Builder()
                        .setDisplayName(userDados.getNome())
                        .build();
                user.sendEmailVerification().addOnCompleteListener(task12 -> {
                    if (task12.isSuccessful()) {
                        user.updateProfile(changeRequest).addOnCompleteListener(task1 -> {
                            SharedPreferences pref = getSharedPreferences("info", MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString(Const.user.logado.EMAIL, userDados.getEmail());

                            Import.Alert.toast(activity, getResources().getString(R.string.cadastro_realizado));
                            editor.apply();
                            finish();
                        });
                    }
                }).addOnFailureListener(e -> Import.Alert.toast(activity, getResources().getString(R.string.erro_email_enviado)));
            } else{
                progressBar.setVisibility(View.INVISIBLE);
                String erro;
                try {
                    throw Objects.requireNonNull(task.getException());
                } catch (FirebaseAuthWeakPasswordException e){
                    erro = getResources().getString(R.string.cadastro_senha_fraca);
                    et_senha.setError(erro);
                } catch (FirebaseAuthInvalidCredentialsException e){
                    erro = getResources().getString(R.string.email_invalido);
                    et_email.setError(erro);
                } catch (FirebaseAuthUserCollisionException e){
                    erro = getResources().getString(R.string.cadastro_email_repetido);
                    et_email.setError(erro);
                } catch (Exception e) {
                    Import.Alert.toast(CadastroActivity.this, getResources().getString(R.string.erro_cadastro));
                }
            }
        });
    }

    private void login(final UserDados userDados) {
        progressBar.setVisibility(View.VISIBLE);
        Import.getFirebase.getAuth().signInWithEmailAndPassword(userDados.getEmail(), userDados.getSenha())
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = authResult.getUser();
                    if (user != null) {
                        if (user.isEmailVerified()) {
                            Import.getFirebase.getReference()
                                    .child(Const.firebase.child.ADMINISTRADORES)
                                    .child(user.getUid())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            try {
                                                if (dataSnapshot.getValue() == null)
                                                    throw new Exception();
                                                String uid = dataSnapshot.getValue(String.class);
                                                if (uid == null)
                                                    throw new Exception();
                                                Import.getFirebase.setGerencia(activity, true);
                                                Intent intent = new Intent(activity, GerenciaActivity.class);
                                                startActivity(intent);
                                                finish();
                                            } catch (Exception ignored) {
                                                progressBar.setVisibility(View.GONE);
                                                Import.getFirebase.getAuth().signOut();
                                                rad++;
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) { }
                                    });
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    rad++;
                });
    }

    //endregion

}
