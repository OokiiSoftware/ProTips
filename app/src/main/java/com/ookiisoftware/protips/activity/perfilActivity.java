package com.ookiisoftware.protips.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.ookiisoftware.protips.R;
import com.ookiisoftware.protips.auxiliar.Constantes;
import com.ookiisoftware.protips.auxiliar.Import;
import com.ookiisoftware.protips.modelo.Apostador;
import com.ookiisoftware.protips.modelo.Tipster;
import com.ookiisoftware.protips.modelo.Usuario;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class perfilActivity extends AppCompatActivity {

    //region Variáveis
    private static final String TAG = "EditFragment";

    // Elementos do layout
    private Spinner spinner;
    private ImageView foto_user;
    private EditText txt_nome, txt_email;
    private EditText txt_tipName;
    private ProgressBar progressBar;
    private LinearLayout img_foto_bg;
    private Button btn_cancelar;

    public boolean isPrimeiroLogin;
    private boolean fotoUserIsLocal;
    private boolean isTipster;
    private Activity activity;
    private FirebaseUser user;
    private String foto_uri = "";
    private StorageTask uploadTask;
    //endregion

    //region Overrides

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
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
                Glide.with(activity).load(foto_uri).into(foto_user);
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
            Import.LogOut(activity);
        }
        super.onBackPressed();
    }

/*
onActivityResult em fragments
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for(Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment != null){
                fragment.onActivityResult(requestCode, resultCode, data);
                break;
            }
        }
    }*/

    //endregion

    //region Métodos

    private void Init() {
        //region findViewById
        progressBar = findViewById(R.id.progressBar);
        img_foto_bg = findViewById(R.id.edit_foto_bg);
        foto_user = findViewById(R.id.edit_ic_user);
        txt_nome = findViewById(R.id.edit_usuario_nome);
        txt_email = findViewById(R.id.edit_usuario_email);
        spinner = findViewById(R.id.edit_usuario_spinner);
        txt_tipName = findViewById(R.id.edit_usuario_tipname);
        btn_cancelar = findViewById(R.id.edit_btn_cancelat);
        Button btn_salvar = findViewById(R.id.edit_btn_salvar);
        //endregion

        activity = this;

        //region Bundle
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isPrimeiroLogin = bundle.getBoolean(Constantes.PRIMEIRO_LOGIN, false);
        }
        //endregion

        txt_email.setEnabled(false);

        // Spinner
        ArrayAdapter adapter = ArrayAdapter.createFromResource(activity, R.array.usuario_sou_um, R.layout.item_spinner);
        spinner.setAdapter(adapter);

        // Firebase
        user = Import.getFirebase.getUser();
        isTipster = Import.getFirebase.isTipster();
        if (!isPrimeiroLogin) {
            if (isTipster) {
                Tipster userU = Import.getFirebase.getTipster();
                if (userU != null) {
                    txt_tipName.setText(userU.getDados().getTipname());
                    spinner.setSelection(userU.getDados().getCategoria());
                }
            } else {
                Apostador userU = Import.getFirebase.getApostador();
                if (userU != null) {
                    txt_tipName.setText(userU.getDados().getTipname());
                    spinner.setSelection(userU.getDados().getCategoria());
                }
            }
        }
        txt_tipName.setEnabled(isPrimeiroLogin);

        txt_nome.setText(user.getDisplayName());
        txt_email.setText(user.getEmail());

        if (user.getPhotoUrl() != null)
            foto_uri = user.getPhotoUrl().toString();

        Glide.with(activity).load(foto_uri).into(foto_user);

        //region setListener
        foto_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
                if (!Import.hasPermissions(activity, PERMISSIONS)) {
                    ActivityCompat.requestPermissions(activity, PERMISSIONS, Constantes.REQUEST_PERMISSION_STORANGE);
                } else {
                    if (uploadTask != null && uploadTask.isInProgress()) {
                        img_foto_bg.setBackground(getDrawable(R.drawable.bg_circulo_vermelho));
                        Import.Alert.toast(activity, getResources().getString(R.string.carregando_a_imagem));
                    } else {
                        img_foto_bg.setBackground(getDrawable(R.drawable.bg_circulo_azul_light));
                        AbrirCropView();
                    }
                }
            }
        });

        btn_salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (uploadTask != null && uploadTask.isInProgress()) {
                    img_foto_bg.setBackground(getDrawable(R.drawable.bg_circulo_vermelho));
                    Import.Alert.toast(activity, getResources().getString(R.string.carregando_a_imagem));
                } else {
                    Usuario usuario = new Usuario();
                    usuario.setId(user.getUid());
                    usuario.setTipname(txt_tipName.getText().toString());
                    usuario.setNome(txt_nome.getText().toString());
                    usuario.setEmail(txt_email.getText().toString());
                    usuario.setFoto(foto_uri);
                    usuario.setCategoria(spinner.getSelectedItemPosition());
                    VerificarDadosAntesDeSalvar(usuario);
                }
            }
        });
        btn_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isPrimeiroLogin)
                    onBackPressed();
                else
                    Import.Alert.toast(activity, getResources().getString(R.string.aviso_salvar_dados));
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                isTipster = position == Usuario.Categoria.Tipster.ordinal();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        //endregion
    }

    private void AbrirCropView() {
        int ratio = 1;
        CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(ratio, ratio).start(activity);
    }

    private void VerificarDadosAntesDeSalvar(final Usuario usuario) {
        if (usuario.getTipname().isEmpty())
            txt_tipName.setError(getResources().getString(R.string.tipName_obrigatório));
        else if (usuario.getTipname().length() < 6)
            txt_tipName.setError(getResources().getString(R.string.TipName_deve_conter_no_mínimo_6_dígitos));
        else if (usuario.getNome().isEmpty())
            txt_nome.setError(getResources().getString(R.string.insira_seu_nome));
        else if (usuario.getFoto() == null || usuario.getFoto().isEmpty()) {
            img_foto_bg.setBackground(getDrawable(R.drawable.bg_circulo_vermelho));
            Import.Alert.toast(activity, getResources().getString(R.string.foto_de_usuario_obrigatoria));
        } else {
            if (!usuario.getNome().equals(user.getDisplayName()))
                AlterarNome(usuario.getNome());

            if (txt_tipName.isEnabled()) {
                // Se estiver desabilitado não precisa verificar
                // Verifica se este id já existe no banco de dados
                DatabaseReference refTemp = Import.getFirebase.getReference()
                        .child(Constantes.firebase.child.IDENTIFICADOR)
                        .child(usuario.getTipname());
                refTemp.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            txt_tipName.setError(getResources().getString(R.string.aviso_tipname_repetido));
                            return;
                        }
                        UparFoto(usuario);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
            }
            else
                UparFoto(usuario);
        }
    }

    private void AlterarNome(final String nome) {
        UserProfileChangeRequest changeRequest = new UserProfileChangeRequest.Builder().setDisplayName(nome).build();
        user.updateProfile(changeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Import.Alert.msg(TAG, "Nome alterado para", nome);
            }
        });
    }

    private void AlterarFoto(final Uri uri) {
        UserProfileChangeRequest changeRequest = new UserProfileChangeRequest.Builder().setPhotoUri(uri).build();
        user.updateProfile(changeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Import.Alert.msg(TAG, "Foto alterada", uri.toString());
            }
        });
    }

    private void Salvar(Apostador usuario) {
        // Salva no firebase
        usuario.salvar();
        Import.getFirebase.setUser(activity, usuario);
        Glide.with(activity).load(usuario.getDados().getFoto()).into(foto_user);

        SalvarComum();
    }
    private void Salvar(Tipster usuario) {
        // Salva no firebase
        usuario.salvar();
        Import.getFirebase.setUser(activity, usuario);
        Glide.with(activity).load(usuario.getDados().getFoto()).into(foto_user);

        SalvarComum();
    }
    private void SalvarComum() {
        txt_tipName.setEnabled(false);
        isPrimeiroLogin = false;
        Import.Alert.snakeBar(getCurrentFocus(), getResources().getString(R.string.usuario_salvo));
    }

    private void UparFoto(final Usuario usuario) {
        if (!fotoUserIsLocal) {
            if (isTipster)
                Salvar(usuario.toTipster());
            else
                Salvar(usuario.toApostador());
        }
        else {
            Uri uri = Uri.parse(usuario.getFoto());
            uploadTask = Import.getFirebase.getStorage()
                    .child(Constantes.firebase.child.USUARIO)
                    .child(Constantes.firebase.child.PERFIL)
                    .child(usuario.getId())
                    .putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setProgress(0);
                                }
                            }, 500);

                            if (taskSnapshot.getMetadata() != null && taskSnapshot.getMetadata().getReference() != null)
                                taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.getResult() != null) {
                                            AlterarFoto(task.getResult());
                                            usuario.setFoto(task.getResult().toString());
                                            if (isTipster)
                                                Salvar(usuario.toTipster());
                                            else
                                                Salvar(usuario.toApostador());
                                        }
                                    }
                                });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            img_foto_bg.setBackground(getDrawable(R.drawable.bg_circulo_vermelho));
                            Import.Alert.toast(activity, getResources().getString(R.string.erro_foto_carregar));
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressBar.setProgress((int) progress);
                        }
                    });
        }
    }

    //endregion

}
