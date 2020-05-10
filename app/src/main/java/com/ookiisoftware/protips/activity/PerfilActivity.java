package com.ookiisoftware.protips.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageTask;
import com.ookiisoftware.protips.R;
import com.ookiisoftware.protips.auxiliar.Constantes;
import com.ookiisoftware.protips.auxiliar.Import;
import com.ookiisoftware.protips.modelo.Data;
import com.ookiisoftware.protips.modelo.User;
import com.ookiisoftware.protips.modelo.Usuario;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.Objects;

public class PerfilActivity extends AppCompatActivity {

    //region Variáveis
    private static final String TAG = "EditFragment";
    private User currentUser;

    // Elementos do layout
    private Spinner privacidade;
    private Spinner estado;
    private ImageView foto;
    private EditText nome, email;
    private EditText tipName;
    private EditText telefone;
    private EditText info;
    private EditText nascimento;
    private ProgressBar progressBarHorizontal;
    private ProgressBar progressBar;

    public boolean isPrimeiroLogin;
    private boolean fotoUserIsLocal;
    private Activity activity;
    private String foto_uri = "";
    private StorageTask uploadTask;

    //endregion

    //region Overrides

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        activity = this;
        Init();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK && result != null && result.getUri() != null) {
                foto_uri = result.getUri().toString();
                fotoUserIsLocal = true;
                Glide.with(activity).load(foto_uri).into(foto);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                if (result != null)
                    Import.Alert.erro(TAG, "onActivityResult", result.getError().getMessage());
                Import.Alert.toast(activity, getResources().getString(R.string.erro_foto_salvar));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constantes.REQUEST_PERMISSION_STORANGE:
            case Constantes.REQUEST_PERMISSION_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    AbrirCropView();
                } else {
                    Import.Alert.toast(this, getResources().getString(R.string.permissao_camera_recusada));
                }
            }
            break;
        }
    }

    @Override
    public void onBackPressed() {
        if(isPrimeiroLogin) {
            Import.Alert.toast(activity, getResources().getString(R.string.aviso_salvar_dados));
            Import.logOut(activity);
        }
        super.onBackPressed();
    }

    //endregion

    //region Métodos

    private void Init() {
        //region findViewById
        progressBarHorizontal = findViewById(R.id.progressBarHorizontal);
        progressBar = findViewById(R.id.progressBar);
        foto = findViewById(R.id.iv_foto);
        nome = findViewById(R.id.et_nome);
        email = findViewById(R.id.et_email);
        info = findViewById(R.id.et_info);
        nascimento = findViewById(R.id.et_data);
        tipName = findViewById(R.id.et_tipname);
        telefone = findViewById(R.id.et_telefone);
        estado = findViewById(R.id.sp_estado);
        privacidade = findViewById(R.id.sp_privacidade);
        final EditText categoria = findViewById(R.id.et_categoria);
        final TextView privacidade_info = findViewById(R.id.tv_info_privacidade);
        final LinearLayout telefone_container = findViewById(R.id.ll_telefone_container);
        final Button queroSerTipster = findViewById(R.id.quero_ser_tipster);
        final Button btn_voltar = findViewById(R.id.cancelar);
        final Button btn_salvar = findViewById(R.id.tv_salvar);
        //endregion

        //region Bundle
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isPrimeiroLogin = bundle.getBoolean(Constantes.intent.PRIMEIRO_LOGIN, false);
        }
        //endregion

        //region setValues

        if (Import.getFirebase.getTipster() == null)
            currentUser = new User();
        else
            currentUser = new User(Import.getFirebase.getTipster());

        boolean isTipster = currentUser.getDados().isTipster();
        Usuario usuario = currentUser.getDados();

        email.setEnabled(false);
        telefone.setFocusable(false);
        // deixar assim até criar o método de verificação do numero de telefone
        telefone_container.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        if (!isPrimeiroLogin) {
            if (isTipster) {
                categoria.setText(getResources().getString(R.string.tipster));
                queroSerTipster.setText(getResources().getString(R.string.quero_ser_um_punter));
            } else {
                categoria.setText(getResources().getString(R.string.punter));
                categoria.setSelection(0);
            }
            if (usuario != null) {
                if (isTipster && usuario.isBloqueado()) {
                    queroSerTipster.setText(getResources().getString(R.string.em_analize));
                }
                nome.setText(usuario.getNome());
                info.setText(usuario.getInfo());
                email.setText(usuario.getEmail());
                tipName.setText(usuario.getTipname());
                estado.setSelection(usuario.getEndereco().getEstado());
                nascimento.setText(usuario.getNascimento().toString());

                if (usuario.getFoto() != null && !usuario.getFoto().isEmpty())
                    foto_uri = usuario.getFoto();
                if (usuario.getTelefone() != null && !usuario.getTelefone().isEmpty())
                    telefone.setText(usuario.getTelefone());
                else
                    telefone.setHint(getResources().getString(R.string.registrar_numero));
            }
        }
        tipName.setEnabled(isPrimeiroLogin);

        Glide.with(activity).load(foto_uri).into(foto);

        SimpleMaskFormatter formatter = new SimpleMaskFormatter(Constantes.formats.DATA);
        MaskTextWatcher watcher = new MaskTextWatcher(nascimento, formatter);
        nascimento.addTextChangedListener(watcher);
        //endregion

        //region setListener

        foto.setOnClickListener(view -> {
            String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
            if (!Import.hasPermissions(activity, PERMISSIONS)) {
                ActivityCompat.requestPermissions(activity, PERMISSIONS, Constantes.REQUEST_PERMISSION_STORANGE);
            } else {
                if (uploadTask != null && uploadTask.isInProgress()) {
                    foto.setBackground(getDrawable(R.drawable.bg_circulo_vermelho));
                    Import.Alert.toast(activity, getResources().getString(R.string.carregando_a_imagem));
                } else {
                    foto.setBackground(getDrawable(R.drawable.bg_circulo_primary_light));
                    AbrirCropView();
                }
            }
        });

        btn_salvar.setOnClickListener(view -> CriarUsuario());
        btn_voltar.setOnClickListener(view -> {
            if (!isPrimeiroLogin) {
                onBackPressed();
           } else {
                Import.Alert.toast(activity, getResources().getString(R.string.aviso_salvar_dados));
            }
        });

        privacidade_info.setOnClickListener(v -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
            dialog.setTitle(R.string.informacao);
            dialog.setMessage(R.string.info_privacidade);
            dialog.setPositiveButton(getResources().getString(R.string.ok), null);
            dialog.show();
        });
        queroSerTipster.setOnClickListener(v -> {
            if (Import.getFirebase.getUsuario() == null) {
                Import.Alert.toast(activity, getResources().getString(R.string.salve_primeiro));
            } else {
                if (Objects.equals(queroSerTipster.getText().toString(), getResources().getString(R.string.quero_ser_um_punter)))
                    QueroSerPunter();
                else {
                    QueroSerTipster(!currentUser.getDados().isBloqueado());
                }
            }
        });

        /*if (!isTipster)
            categoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                isTipster = position == Usuario.Categoria.Tipster.ordinal();
                if (position == 0) {
                    queroSerTipster.setVisibility(View.GONE);
                    bloquearUser = false;
                    solicitarSerTipster = false;
                } else
                    queroSerTipster.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });*/

        telefone.setOnClickListener(v -> {
                Intent intent = new Intent(activity, RegistrarNumeroCelActivity.class);
                startActivity(intent);
            });

        //endregion
    }

    private void AbrirCropView() {
        int ratio = 1;
        CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(ratio, ratio).start(activity);
    }

    private void CriarUsuario() {
        progressBar.setVisibility(View.VISIBLE);
        if (uploadTask != null && uploadTask.isInProgress()) {
            foto.setBackground(getDrawable(R.drawable.bg_circulo_vermelho));
            Import.Alert.toast(activity, getResources().getString(R.string.carregando_a_imagem));
        } else {
            Data data = new Data();
            data.setData(nascimento.getText().toString());

            User user = currentUser;
            user.getDados().setId(Import.getFirebase.getId());
            user.getDados().setTipname(tipName.getText().toString());
            user.getDados().setNome(nome.getText().toString());
            user.getDados().setEmail(email.getText().toString());
            user.getDados().setFoto(foto_uri);
            user.getDados().setNascimento(data);
            user.getDados().setTelefone(telefone.getText().toString());
            user.getDados().getEndereco().setEstado(estado.getSelectedItemPosition());
            user.getDados().setPrivado(privacidade.getSelectedItemPosition() == 1);
            user.getDados().setInfo(info.getText().toString());
            VerificarDadosAntesDeSalvar(user);
        }
    }

    private void VerificarDadosAntesDeSalvar(final User user) {
        if (user.getDados().getTipname().isEmpty())
            tipName.setError(getResources().getString(R.string.tipName_obrigatório));
        else if (user.getDados().getTipname().length() < 6)
            tipName.setError(getResources().getString(R.string.TipName_deve_conter_no_mínimo_6_dígitos));
        else if (user.getDados().getNome().isEmpty())
            nome.setError(getResources().getString(R.string.insira_seu_nome));
        else if (!user.getDados().getNascimento().valido())
            nascimento.setError(getResources().getString(R.string.data_incorreta));
        else if (user.getDados().getNascimento().getIdade() < 18)
            nascimento.setError(getResources().getString(R.string.idade_minima));
        else if (user.getDados().getFoto() == null || user.getDados().getFoto().isEmpty()) {
            foto.setBackground(getDrawable(R.drawable.bg_circulo_vermelho));
            Import.Alert.toast(activity, getResources().getString(R.string.foto_de_usuario_obrigatoria));
        } else {
            if (!user.getDados().getNome().equals(Import.getFirebase.getUser().getDisplayName()))
                AlterarNome(user.getDados().getNome());

            if (tipName.isEnabled()) {
                // Se estiver desabilitado não precisa verificar
                // Verifica se este tipName já existe no banco de dados
                DatabaseReference refTemp = Import.getFirebase.getReference()
                        .child(Constantes.firebase.child.IDENTIFICADOR);
                refTemp.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<String> values = new ArrayList<>();
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            String value = data.getKey();
                            if (value != null) {
                                values.add(value.toLowerCase());
                            }
                        }
                        if (values.contains(user.getDados().getTipname().toLowerCase())) {
                            tipName.setError(getResources().getString(R.string.aviso_tipname_repetido));
                            progressBar.setVisibility(View.GONE);
                            return;
                        }
                        UparFoto(user);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
            }
            else
                UparFoto(user);
        }
        progressBar.setVisibility(View.GONE);
    }

    private void QueroSerTipster(final boolean teste) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setTitle(getResources().getString(R.string.solicitacao_tipster));

        String msg = getResources().getString(R.string.entre_em_contato_com);
        msg += "\n" + getResources().getString(R.string.email) + ": ";
        msg += getResources().getString(R.string.company_email);
        msg += "\n" + getResources().getString(R.string.whats_app) + ": ";
        msg += getResources().getString(R.string.company_whats_app);

        dialog.setMessage(msg);

        if (teste) {
            dialog.setNeutralButton(getResources().getString(R.string.cancelar), null);
            dialog.setPositiveButton(getResources().getString(R.string.solicitar), (dialog1, which) -> {
                currentUser.solicitarSerTipster();
                Import.reiniciarApp(activity);
            });
        } else {
            dialog.setPositiveButton(getResources().getString(R.string.ok), null);
            dialog.setNeutralButton(getResources().getString(R.string.cancelar_solicitacao), (dialog12, which) -> {
                currentUser.solicitarSerTipsterCancelar();
                Import.reiniciarApp(activity);
            });
        }

        dialog.show();
    }

    private void QueroSerPunter() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setTitle(getResources().getString(R.string.solicitacao_punter));

        String msg = getResources().getString(R.string.aviso_exluir_conta);
        msg += "\n" + getResources().getString(R.string.aviso_recriar_conta_punter);

        dialog.setMessage(msg);
        dialog.setNeutralButton(getResources().getString(R.string.cancelar), null);
        dialog.setPositiveButton(getResources().getString(R.string.ok), (dialog1, which) -> {
            currentUser.desbloquear();
            currentUser.habilitarTipster(false);
            Import.reiniciarApp(activity);
        });
        dialog.show();
    }

    private void AlterarNome(final String nome) {
        UserProfileChangeRequest changeRequest = new UserProfileChangeRequest.Builder().setDisplayName(nome).build();
        Import.getFirebase.getUser().updateProfile(changeRequest).addOnCompleteListener(task -> Import.Alert.msg(TAG, "Nome alterado para", nome));
    }

    private void AlterarFoto(final Uri uri) {
        UserProfileChangeRequest changeRequest = new UserProfileChangeRequest.Builder().setPhotoUri(uri).build();
        Import.getFirebase.getUser().updateProfile(changeRequest).addOnCompleteListener(task -> Import.Alert.msg(TAG, "Foto alterada", uri.toString()));
    }

    private void UparFoto(final User usuario) {
        if (!fotoUserIsLocal) {
            Salvar(usuario);
        } else {
            Uri uri = Uri.parse(usuario.getDados().getFoto());
            uploadTask = Import.getFirebase.getStorage()
                    .child(Constantes.firebase.child.USUARIO)
                    .child(Constantes.firebase.child.PERFIL)
                    .child(usuario.getDados().getId())
                    .putFile(uri).addOnSuccessListener(taskSnapshot -> {
                        Handler handler = new Handler();
                        handler.postDelayed(() -> progressBarHorizontal.setProgress(0), 500);

                        if (taskSnapshot.getMetadata() != null && taskSnapshot.getMetadata().getReference() != null)
                            taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnCompleteListener(task -> {
                                if (task.getResult() != null) {
                                    AlterarFoto(task.getResult());
                                    usuario.getDados().setFoto(task.getResult().toString());
                                    Salvar(usuario);
                                }
                            });
                    }).addOnFailureListener(e -> {
                        foto.setBackground(getDrawable(R.drawable.bg_circulo_vermelho));
                        Import.Alert.toast(activity, getResources().getString(R.string.erro_foto_carregar));
                        progressBar.setVisibility(View.GONE);
                    }).addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        progressBarHorizontal.setProgress((int) progress);
                    });
        }
    }

    private void Salvar(User usuario) {
        // Salva no firebase
        usuario.salvar();
        Import.getFirebase.setUser(activity, usuario, true);
        try {
            Glide.with(activity).load(usuario.getDados().getFoto()).into(foto);
            SalvarComum();
        } catch (Exception ignored){}
    }

    private void SalvarComum() {
        tipName.setEnabled(false);
        isPrimeiroLogin = false;
        progressBar.setVisibility(View.GONE);
        Import.Alert.snakeBar(activity, getResources().getString(R.string.usuario_salvo));
    }

    //endregion

}
