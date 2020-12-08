package com.ookiisoftware.protips.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.ookiisoftware.protips.R;
import com.ookiisoftware.protips.auxiliar.Const;
//import com.ookiisoftware.protips.modelo.Punter;
import com.ookiisoftware.protips.modelo.User;
import com.ookiisoftware.protips.modelo.UserDados;
import com.ookiisoftware.protips.auxiliar.Import;

public class LoginActivity extends AppCompatActivity {

    //region Variáveis

    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 101;
    private static final int RC_SIGN_IN_FACEBOOK = 64206;

    private FirebaseAuth firebaseAuth;
    private CallbackManager callbackManagerFacebook;
    private GoogleSignInClient mGoogleSignInClient;
    private Activity activity;

    //Elementos do Layout
    private EditText et_email;
    private EditText et_senha;
    private TextView enviar_email;
    private TextView recuperarSenha;
    private ProgressBar progressBar;
    private LinearLayout splashScreen;

    //endregion

    //region Override

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        activity = this;
        Init();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser user = Import.getFirebase.getUser();
        if (user == null) {
            progressBar.setVisibility(View.INVISIBLE);
            et_email.setEnabled(true);
            et_senha.setEnabled(true);
            Import.Alert.d(TAG, "onStart", "currentUser == null");
        } else {
            progressBar.setVisibility(View.VISIBLE);
            splashScreen.setVisibility(View.VISIBLE);
            Import.Alert.d(TAG, "onStart", "auto login");
            if (user.isEmailVerified() && Import.get.hasConection(activity)) {
                boolean isGerente = Import.getFirebase.isGerente(activity);
                if (isGerente) {
                    Intent intent = new Intent(activity, GerenciaActivity.class);
                    startIntent(intent);
                } else
                    verificarJsons();
            } else {
                et_email.setEnabled(true);
                et_senha.setEnabled(true);
                splashScreen.setVisibility(View.GONE);
                progressBar.setVisibility(View.INVISIBLE);
                emailNaoVerificado(user);
                Import.Alert.d(TAG, "onStart", "email não verificado", "ou sem conexão com a internet");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Import.Alert.d(TAG, "requestCode", requestCode + "");
        // Resultado retornado ao iniciar o Intent de GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // O Login do Google foi bem-sucedido, autenticou-se com o Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                assert account != null;
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Falha no login do Google, atualize a interface do usuário adequadamente
                Import.Alert.e(TAG, "onActivityResult", e);
            }
        }
        // Passe o resultado da atividade de volta ao SDK do Facebook
        if(requestCode == RC_SIGN_IN_FACEBOOK)
            callbackManagerFacebook.onActivityResult(requestCode, resultCode, data);
    }

    //endregion

    //region Métodos

    private void Init() {
        //region findViewById
//        LoginButton btn_login_facebook = findViewById(R.id.lb_facebook);
//        ImageView btn_login_twitter = findViewById(R.id.iv_twitter);
        ImageView btn_login_google = findViewById(R.id.iv_google);
        TextView btn_cadastrar = findViewById(R.id.tv_cadastrar);
        TextView btn_login = findViewById(R.id.tv_login);
        recuperarSenha = findViewById(R.id.tv_recuperar_senha);
        splashScreen = findViewById(R.id.splash_screen);
        progressBar = findViewById(R.id.progressBar);
        et_senha = findViewById(R.id.et_senha);
        et_email = findViewById(R.id.et_usuario);
        enviar_email = findViewById(R.id.tv_enviar_email);
        //endregion

        recuperarSenha.setVisibility(View.GONE);
        enviar_email.setVisibility(View.GONE);
        et_email.setText(Import.getFirebase.getUltinoEmail(activity));

        // Initialize Firebase Auth
        firebaseAuth = Import.getFirebase.getAuth();
        callbackManagerFacebook = CallbackManager.Factory.create();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);

        //region cliques

        btn_login.setOnClickListener(view -> {
            {
                String email = et_email.getText().toString();
                String senha = et_senha.getText().toString();
                if (email.isEmpty())
                    et_email.setError("*");
                else if (senha.isEmpty())
                    et_senha.setError("*");
                else
                    OrganizarDados(email, senha);
                progressBar.setVisibility(View.VISIBLE);
            }
        });

        recuperarSenha.setOnClickListener(v -> {
            Intent intent = new Intent(activity, RecuperarSenhaActivity.class);
            intent.putExtra(Const.intent.EMAIL, et_email.getText().toString());
            startActivity(intent);
        });

        //region Cadastrar
        btn_cadastrar.setOnClickListener(view -> IrParaCadastro());
        //endregion

        /*//region Facebook
        btn_login_facebook.setReadPermissions("email", "public_profile");
        btn_login_facebook.registerCallback(callbackManagerFacebook, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                LoginComFacebook(loginResult.getAccessToken());
                progressBar.animate();
            }

            @Override
            public void onCancel() {
                Import.Alert.toast(activity, getResources().getString(R.string.login_cancelado));
            }

            @Override
            public void onError(FacebookException error) {
                Import.Alert.toast(activity, getResources().getString(R.string.erro_de_autencicacao_facebook));
            }
        });
        //endregion

        //region Twitter
        btn_login_twitter.setOnClickListener(view -> {
            LoginComTwitter();
            progressBar.setVisibility(View.VISIBLE);
        });
        //endregion*/

        //region Google
        btn_login_google.setOnClickListener(view -> {
            LoginComGoogle();
            progressBar.setVisibility(View.VISIBLE);
        });
        //endregion

        //endregion
    }

    //region Formas de fazer login

    private void LoginComGoogle(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        LoginComToken(credential);
    }

    private void LoginComTwitter() {

    }
    private void LoginComFacebook(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        LoginComToken(credential);
    }

    private void LoginComToken(AuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(authResult -> {
                    Import.Alert.d(TAG, "LoginComToken", "Sucesso");
                    VerificarLoginTipster();
                })
                .addOnFailureListener(e -> {
                    Import.Alert.e(TAG, "LoginComToken", e);
                    progressBar.setVisibility(View.INVISIBLE);
                    if (e.getMessage() != null)
                        if (e.getMessage().contains("The supplied auth credential is malformed or has expired."))
                            Import.Alert.toast(activity, getResources().getString(R.string.erro_de_autencicacao_muitas_tentativas));
                        else
                            Import.Alert.toast(activity, getResources().getString(R.string.erro_de_autencicacao));
                });
    }

    private void OrganizarDados(String email, String senha) {
        UserDados userDados = new UserDados();
        userDados.setEmail(email);
        userDados.setSenha(senha);
        LoginComEmailESenha(userDados);
    }

    private void LoginComEmailESenha(@NonNull final UserDados userDados) {
        firebaseAuth.signInWithEmailAndPassword(userDados.getEmail(), userDados.getSenha())
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = authResult.getUser();
                    if (user == null) {
                        progressBar.setVisibility(View.INVISIBLE);
                    } else {
                        if (user.isEmailVerified()) {
                            Import.getFirebase.setUltinoEmail(activity, user.getEmail());
                            VerificarLoginTipster();
                        }
                        else
                            emailNaoVerificado(user);
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.INVISIBLE);
                    Import.Alert.e(TAG, "LoginComEmailESenha", e);
                    if (e.getMessage() != null)
                        switch (e.getMessage()) {
                            case "The password is invalid or the user does not have a password.":
                                Import.Alert.toast(activity, getResources().getString(R.string.usuário_senha_incorretos));
                                recuperarSenha.setVisibility(View.VISIBLE);
                                break;
                            case "There is no user record corresponding to this identifier. The user may have been deleted.":
                                Import.Alert.toast(activity, getResources().getString(R.string.email_nao_encontrado));
                                break;
                            default:
                                Import.Alert.toast(activity, getResources().getString(R.string.erro_de_autencicacao));
                                break;
                        }
                });
    }

    //endregion

    private void IrParaCadastro() {
        Intent intent = new Intent(activity, CadastroActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    private void emailNaoVerificado(final FirebaseUser user) {
        if (hasWindowFocus()) {
            Import.Alert.snakeBar(getCurrentFocus(), getResources().getString(R.string.verifique_seu_email), getResources().getString(R.string.enviar_novo_email), v -> enviarNovoEmail(user));
        } else {
            enviar_email.setVisibility(View.VISIBLE);
            enviar_email.setOnClickListener(v -> enviarNovoEmail(user));
            Import.Alert.toast(activity, getResources().getString(R.string.verifique_seu_email));
        }
    }

    private void enviarNovoEmail(final FirebaseUser user) {
        user.sendEmailVerification().addOnCompleteListener(task -> {
            if (task.isSuccessful())
                Import.Alert.snakeBar(activity, getResources().getString(R.string.email_enviado));
        }).addOnFailureListener(e -> {
            Import.Alert.e(TAG, "enviarNovoEmail", e);
            Import.Alert.toast(activity, getResources().getString(R.string.erro_email_enviado));
        });
    }

    private void verificarJsons() {
        Intent intent = new Intent(activity, MainActivity.class);
        Gson gson = new Gson();
        /*if (Import.getFirebase.isFilePresent(activity, Constantes.files.PUNTER_JSON)) {
            Punter user = gson.fromJson(Import.getFirebase.read(activity, Constantes.files.PUNTER_JSON), Punter.class);
            Import.getFirebase.setUser(activity, user, false);

            startIntent(intent);
        } else */
        if (Import.getFirebase.isFilePresent(activity, Const.files.USER_JSON)) {
            User user = gson.fromJson(Import.getFirebase.read(activity, Const.files.USER_JSON), User.class);
            Import.getFirebase.setUser(activity, user, false);
            startIntent(intent);
        } else {
            VerificarLoginTipster();
        }
    }

    private void VerificarLoginTipster() {
        final Intent intent = new Intent(activity, MainActivity.class);
        Import.getFirebase.getReference()
                .child(Const.firebase.child.USUARIO)
                .child(Import.getFirebase.getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        try {
                            if (dataSnapshot.getValue() == null)
                                throw new Exception();
                            User item = dataSnapshot.getValue(User.class);
                            if (item == null)
                                throw new Exception();

                            Import.getFirebase.setUser(activity, item, true);
                        } catch (Exception ex){
                            intent.putExtra(Const.intent.PRIMEIRO_LOGIN, true);
                        } finally {
                            startIntent(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
    }

    private void startIntent(Intent intent){
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        finish();
    }

    //endregion

}
