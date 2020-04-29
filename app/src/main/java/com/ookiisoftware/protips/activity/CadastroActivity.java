package com.ookiisoftware.protips.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.ookiisoftware.protips.R;
import com.ookiisoftware.protips.auxiliar.Constantes;
import com.ookiisoftware.protips.modelo.Usuario;
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
        cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {
                    String nome = et_nome.getText().toString();
                    String usuario = et_email.getText().toString();
                    String senha = et_senha.getText().toString();
                    String senha_2 = et_confirmar_senha.getText().toString();
                    if(nome.isEmpty())
                        et_nome.setError(getResources().getString(R.string.campo_obrigatório));
                    else if(usuario.isEmpty())
                        et_email.setError(getResources().getString(R.string.campo_obrigatório));
                    else if(senha.isEmpty())
                        et_senha.setError(getResources().getString(R.string.campo_obrigatório));
                    else if(senha_2.isEmpty())
                        et_confirmar_senha.setError(getResources().getString(R.string.campo_obrigatório));
                    else if(!senha.equals(senha_2))
                        et_confirmar_senha.setError(getResources().getString(R.string.aviso_cadastro_senhas_diferentes));
                    else {
                        progressBar.setVisibility(View.VISIBLE);
                        OrganizarDados(nome, usuario, senha);
                    }
                }
            }
        });

        foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (random.nextInt(5)){
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
                        foto.setBackground(getDrawable(R.drawable.bg_circulo_branco));
                        break;
                }
            }
        });
        //endregion
    }

    private void OrganizarDados(String nome, String email, String senha) {
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha(senha);

        SalvarDadosNoFirebase(usuario);
    }

    private void SalvarDadosNoFirebase(final Usuario usuario) {
        final FirebaseAuth firebaseAuth = Import.getFirebase.getAuth();
        firebaseAuth.createUserWithEmailAndPassword(usuario.getEmail(), usuario.getSenha())
        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful() && task.getResult() != null && task.getResult().getUser() != null) {
                    final FirebaseUser user = task.getResult().getUser();
                    final UserProfileChangeRequest changeRequest = new UserProfileChangeRequest.Builder()
                            .setDisplayName(usuario.getNome())
                            .build();
                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                user.updateProfile(changeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        SharedPreferences pref = getSharedPreferences("info", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = pref.edit();
                                        editor.putString(Constantes.user.logado.EMAIL, usuario.getEmail());

                                        Import.Alert.toast(activity, getResources().getString(R.string.cadastro_realizado));
                                        editor.apply();
                                        finish();
                                    }
                                });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Import.Alert.toast(activity, getResources().getString(R.string.erro_email_enviado));
                        }
                    });
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
            }
        });
    }

    //endregion

}
