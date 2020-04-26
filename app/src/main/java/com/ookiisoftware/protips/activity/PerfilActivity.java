package com.ookiisoftware.protips.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

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
import com.ookiisoftware.protips.adapter.TextAdapter;
import com.ookiisoftware.protips.auxiliar.Constantes;
import com.ookiisoftware.protips.auxiliar.Import;
import com.ookiisoftware.protips.modelo.Punter;
import com.ookiisoftware.protips.modelo.Data;
import com.ookiisoftware.protips.modelo.Esporte;
import com.ookiisoftware.protips.modelo.Tipster;
import com.ookiisoftware.protips.modelo.Usuario;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.Calendar;

public class PerfilActivity extends AppCompatActivity {

    //region Variáveis
    private static final String TAG = "EditFragment";

    // Elementos do layout
    private Button queroSerTipster;
    private Spinner mercado;
    private Spinner esporte;
    private ImageView foto;
    private EditText nome, email;
    private EditText tipName;
    private EditText nascimento;
    private TextView esportes_add;
    private TextView mercados_add;
    private ProgressBar progressBar;
    private RecyclerView esportesRecycler;
    private RecyclerView mercadosRecycler;
    private TextAdapter esportesAdapter;
    private TextAdapter mercadosAdapter;

//    private Esporte currentEsporte;
    private ArrayList<String> mercados;
    private ArrayList<Esporte> esportes;

    public boolean isPrimeiroLogin;
    private boolean fotoUserIsLocal;
    private boolean isTipster;
    private Activity activity;
    private FirebaseUser user;
    private String foto_uri = "";
    private StorageTask uploadTask;

    private Data dataTemp = new Data();
    private Data dataNascimento;

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
            Import.LogOut(activity);
        }
        super.onBackPressed();
    }

    //endregion

    //region Métodos

    private void Init() {
        //region findViewById
        progressBar = findViewById(R.id.progressBar);
        queroSerTipster = findViewById(R.id.quero_ser_tipster);
        foto = findViewById(R.id.iv_foto);
        nome = findViewById(R.id.nome);
        email = findViewById(R.id.et_email);
        nascimento = findViewById(R.id.tv_data);
        tipName = findViewById(R.id.tipname);
        esporte = findViewById(R.id.tv_esporte);
        mercado = findViewById(R.id.tv_mercado);
        esportes_add = findViewById(R.id.esportes_add);
        mercados_add = findViewById(R.id.mercados_add);
        esportesRecycler = findViewById(R.id.esportes);
        mercadosRecycler = findViewById(R.id.mercados);
        final EditText telefone = findViewById(R.id.telefone);
        final Spinner categoria = findViewById(R.id.categoria);
        final Spinner estado = findViewById(R.id.estado);
        final Spinner privacidade = findViewById(R.id.privacidade);
        final TextView privacidade_info = findViewById(R.id.privacidade_info);
        LinearLayout tipster_container = findViewById(R.id.tipster_container);
        Button btn_cancelar = findViewById(R.id.cancelar);
        Button btn_salvar = findViewById(R.id.salvar);
        //endregion

        //region Bundle
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isPrimeiroLogin = bundle.getBoolean(Constantes.intent.PRIMEIRO_LOGIN, false);
        }
        //endregion

        //region setValues

        mercados = new ArrayList<>();
        esportes = new ArrayList<>();
        email.setEnabled(false);

        isTipster = Import.getFirebase.isTipster();
        if (!isPrimeiroLogin) {
            Usuario usuario;
            if (isTipster) {
                esportes.addAll(Import.getFirebase.getTipster().getEsportes());
                usuario = Import.getFirebase.getTipster().getDados();
                categoria.setSelection(1);
                tipster_container.setVisibility(View.VISIBLE);
                mercadosAdapter = new TextAdapter(activity, mercados, false) {
                    @Override
                    public boolean onLongClick(View v) {
                        int itemPosition = mercadosRecycler.getChildAdapterPosition(v);
                        confirmExlusao(mercados.get(itemPosition), null);
                        return super.onLongClick(v);
                    }
                };
                mercadosRecycler.setAdapter(mercadosAdapter);
                esportesAdapter = new TextAdapter(activity, esportes) {
                    @Override
                    public void onClick(View v) {
                        int itemPosition = esportesRecycler.getChildAdapterPosition(v);
                        Esporte currentEsporte = esportes.get(itemPosition);
                        mercados.clear();
                        mercados.addAll(currentEsporte.getMercados());
                        mercadosAdapter.notifyDataSetChanged();
                        if (mercados.contains(mercado.getSelectedItem().toString()))
                            mercados_add.setText(getResources().getString(R.string._menos));
                        else
                            mercados_add.setText(getResources().getString(R.string._mais));
                    }

                    @Override
                    public boolean onLongClick(View v) {
                        int itemPosition = esportesRecycler.getChildAdapterPosition(v);
                        Esporte e =  esportes.get(itemPosition);
                        confirmExlusao(e.getNome(), e);
                        return super.onLongClick(v);
                    }
                };
                esportesRecycler.setAdapter(esportesAdapter);
                queroSerTipster.setVisibility(View.GONE);
            } else {
                usuario = Import.getFirebase.getPunter().getDados();
                categoria.setSelection(0);
                tipster_container.setVisibility(View.GONE);
            }
            if (usuario != null) {
                tipName.setText(usuario.getTipname());
                estado.setSelection(usuario.getEndereco().getEstado());
                nascimento.setText(usuario.getNascimento().toString());
                dataTemp = dataNascimento = usuario.getNascimento();
            }
        }
        tipName.setEnabled(isPrimeiroLogin);
        categoria.setEnabled(!isTipster);

        //region Firebase
        user = Import.getFirebase.getUser();
        nome.setText(user.getDisplayName());
        email.setText(user.getEmail());

        if (user.getPhotoUrl() != null)
            foto_uri = user.getPhotoUrl().toString();
        if (user.getPhoneNumber() != null)
            telefone.setText(user.getPhoneNumber());

        Glide.with(activity).load(foto_uri).into(foto);
        //endregion

        //endregion

        //region setListener
        foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {
                    String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
                    if (!Import.hasPermissions(activity, PERMISSIONS)) {
                        ActivityCompat.requestPermissions(activity, PERMISSIONS, Constantes.REQUEST_PERMISSION_STORANGE);
                    } else {
                        if (uploadTask != null && uploadTask.isInProgress()) {
                            foto.setBackground(getDrawable(R.drawable.bg_circulo_vermelho));
                            Import.Alert.toast(activity, getResources().getString(R.string.carregando_a_imagem));
                        } else {
                            foto.setBackground(getDrawable(R.drawable.bg_circulo_colorPrimaryLight));
                            AbrirCropView();
                        }
                    }
                }
            }
        });

        esportes_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (esporte.getSelectedItemPosition() != 0) {
                    String name = esporte.getSelectedItem().toString();
                    Esporte e = findEsporte(name);

                    if (e == null) {
                        e = new Esporte();
                        e.setNome(name);
                        esportes.add(e);
                        esportesAdapter.notifyDataSetChanged();
                        mercados.clear();
                        mercadosAdapter.notifyDataSetChanged();
                        esportes_add.setText(getResources().getString(R.string._menos));
                        mercados_add.setText(getResources().getString(R.string._mais));
                    } else {
                        confirmExlusao(e.getNome(), e);
                    }
                }
            }
        });
        mercados_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mercado.getSelectedItemPosition() != 0) {
                    String item = mercado.getSelectedItem().toString();

                    if (mercados.contains(item)) {
                        confirmExlusao(item, null);
                    } else {
                        mercados.add(item);
                        mercadosAdapter.notifyDataSetChanged();
                        mercados_add.setText(getResources().getString(R.string._menos));
                    }
                }
            }
        });
        nascimento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    Calendar maxDate = Calendar.getInstance();
                    maxDate.set(Import.get.calendar.ano() - 18, Import.get.calendar.mes(), Import.get.calendar.dia());

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        DatePickerDialog picker = new DatePickerDialog(activity);
                        picker.getDatePicker().setMaxDate(maxDate.getTimeInMillis());
                        picker.updateDate(dataNascimento.getAno(), dataNascimento.getMes(), dataNascimento.getDia());

                        picker.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                String format = dayOfMonth + "/" + month + "/" + year;
                                dataTemp.setAno(year);
                                dataTemp.setMes(month);
                                dataTemp.setDia(dayOfMonth);
                                dataNascimento = new Data(dataTemp);
                                nascimento.setText(format);
                            }
                        });
                        picker.show();
                    } else {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
                        CalendarView calendarView = new CalendarView(activity);
                        Calendar calendar = Calendar.getInstance();

                        dialog.setView(calendarView);
                        calendarView.setMaxDate(maxDate.getTimeInMillis());

                        calendar.set(dataNascimento.getAno(), dataNascimento.getMes(), dataNascimento.getDia());
                        calendarView.setDate(calendar.getTimeInMillis());
                        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                            @Override
                            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                                dataTemp.setAno(year);
                                dataTemp.setMes(month);
                                dataTemp.setDia(dayOfMonth);
                            }
                        });

                        dialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String format = dataTemp.getDia() + "/" + dataTemp.getMes() + "/" + dataTemp.getAno();
                                dataNascimento = new Data(dataTemp);
                                nascimento.setText(format);
                            }
                        });
                        dialog.setNegativeButton(getResources().getString(R.string.cancelar), null);
                        dialog.show();
                    }
                }
            }
        });

        btn_salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {
                    if (uploadTask != null && uploadTask.isInProgress()) {
                        foto.setBackground(getDrawable(R.drawable.bg_circulo_vermelho));
                        Import.Alert.toast(activity, getResources().getString(R.string.carregando_a_imagem));
                    } else {
                        Usuario usuario = new Usuario();
                        usuario.setId(user.getUid());
                        usuario.setTipname(tipName.getText().toString());
                        usuario.setNome(nome.getText().toString());
                        usuario.setEmail(email.getText().toString());
                        usuario.setFoto(foto_uri);
                        usuario.setNascimento(dataNascimento);
                        usuario.setTelefone(telefone.getText().toString());
                        usuario.getEndereco().setEstado(estado.getSelectedItemPosition());
                        usuario.setPrivado(privacidade.getSelectedItemPosition() == 1);
                        VerificarDadosAntesDeSalvar(usuario);
                    }
                }
            }
        });
        btn_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isPrimeiroLogin) {
                    onBackPressed();
               } else {
                    Import.Alert.toast(activity, getResources().getString(R.string.aviso_salvar_dados));
                }
            }
        });

        privacidade_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
                    dialog.setTitle(R.string.informacao);
                    dialog.setMessage(R.string.privacidade_info);
                    dialog.setPositiveButton(getResources().getString(R.string.ok), null);
                    dialog.show();
                }
            }
        });

        esporte.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (esportes != null && position < esportes.size())
                    if (findEsporte(esportes.get(position).getNome()) == null)
                        esportes_add.setText(getResources().getString(R.string._mais));
                    else
                        esportes_add.setText(getResources().getString(R.string._menos));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        mercado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mercados.contains(mercado.getItemAtPosition(position).toString()))
                    mercados_add.setText(getResources().getString(R.string._menos));
                else
                    mercados_add.setText(getResources().getString(R.string._mais));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        if (!isTipster)
            categoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                isTipster = position == Usuario.Categoria.Tipster.ordinal();
                if (position == 0) {
                    queroSerTipster.setVisibility(View.GONE);
                } else
                    queroSerTipster.setVisibility(View.VISIBLE);
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
            tipName.setError(getResources().getString(R.string.tipName_obrigatório));
        else if (usuario.getTipname().length() < 6)
            tipName.setError(getResources().getString(R.string.TipName_deve_conter_no_mínimo_6_dígitos));
        else if (usuario.getNome().isEmpty())
            nome.setError(getResources().getString(R.string.insira_seu_nome));
        else if (usuario.getNascimento().getIdade() < 18)
            nascimento.setError(getResources().getString(R.string.idade_minima));
        else if (usuario.getFoto() == null || usuario.getFoto().isEmpty()) {
            foto.setBackground(getDrawable(R.drawable.bg_circulo_vermelho));
            Import.Alert.toast(activity, getResources().getString(R.string.foto_de_usuario_obrigatoria));
        } else {
            if (!usuario.getNome().equals(user.getDisplayName()))
                AlterarNome(usuario.getNome());
            if (!usuario.getTelefone().equals(user.getPhoneNumber()))
                AlterarTelefone(usuario.getNome());

            if (tipName.isEnabled()) {
                // Se estiver desabilitado não precisa verificar
                // Verifica se este id já existe no banco de dados
                DatabaseReference refTemp = Import.getFirebase.getReference()
                        .child(Constantes.firebase.child.IDENTIFICADOR)
                        .child(usuario.getTipname());
                refTemp.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            tipName.setError(getResources().getString(R.string.aviso_tipname_repetido));
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

    private void AlterarTelefone(String nome) {
//        user.updatePhoneNumber().addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//
//            }
//        });
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

    private void Salvar(Punter usuario) {
        if (Import.getFirebase.getPunter() != null)
            usuario.setTipsters(Import.getFirebase.getPunter().getTipsters());
        // Salva no firebase
        usuario.salvar();
        Import.getFirebase.setUser(activity, usuario);
        Glide.with(activity).load(usuario.getDados().getFoto()).into(foto);

        SalvarComum();
    }
    private void Salvar(Tipster usuario) {
        usuario.setEsportes(esportes);
        if (Import.getFirebase.getTipster() != null) {
            usuario.setPostes(Import.getFirebase.getTipster().getPostes());
            usuario.setPunters(Import.getFirebase.getTipster().getPunters());
        }
        // Salva no firebase
        usuario.salvar();
        Import.getFirebase.setUser(activity, usuario);
        Glide.with(activity).load(usuario.getDados().getFoto()).into(foto);

        SalvarComum();
    }
    private void SalvarComum() {
        tipName.setEnabled(false);
        isPrimeiroLogin = false;
        Import.Alert.snakeBar(getCurrentFocus(), getResources().getString(R.string.usuario_salvo));
    }

    private void confirmExlusao(final String itemName, final Esporte esporte) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setTitle(itemName);
        dialog.setMessage(getResources().getString(R.string.remover_item));
        dialog.setPositiveButton(getResources().getString(R.string.sim), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (esporte == null) {
//                    currentEsporte.getMercados().remove(itemName);
                    mercados.remove(itemName);
                    mercadosAdapter.notifyDataSetChanged();
                    mercados_add.setText(getResources().getString(R.string._mais));
                } else {
                    esportes.remove(esporte);
                    esportesAdapter.notifyDataSetChanged();
//                    currentEsporte = null;
                    mercados.clear();
                    mercadosAdapter.notifyDataSetChanged();
                    esportes_add.setText(getResources().getString(R.string._mais));
                    mercados_add.setText(getResources().getString(R.string._mais));
                }
            }
        });
        dialog.setNegativeButton(getResources().getString(R.string.nao), null);
        dialog.show();
    }

    private Esporte findEsporte(String name) {
        for (Esporte e : esportes)
            if (e.getNome().equals(name))
                return e;
        return null;
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
                            foto.setBackground(getDrawable(R.drawable.bg_circulo_vermelho));
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
