package com.ookiisoftware.protips.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageTask;
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
import java.util.HashMap;
import java.util.Objects;

public class PerfilActivity extends AppCompatActivity {

    //region Variáveis
    private static final String TAG = "EditFragment";

    // Elementos do layout
    private Spinner privacidade;
    private Spinner mercado;
    private Spinner esporte;
    private Spinner estado;
    private ImageView foto;
    private EditText nome, email;
    private EditText tipName;
    private EditText telefone;
    private EditText info;
    private EditText nascimento;
    private TextView esportes_add;
    private TextView mercados_add;
    private TextView selecionado;
    private ProgressBar progressBarHorizontal;
    private ProgressBar progressBar;
    private RecyclerView esportesRecycler;
    private RecyclerView mercadosRecycler;
    private TextAdapter esportesAdapter;
    private TextAdapter mercadosAdapter;

    private Esporte currentEsporte;
//    private ArrayList<String> mercados;
    private HashMap<String, Esporte> esportes;

    public boolean isPrimeiroLogin;
    private boolean fotoUserIsLocal;
    private boolean isTipster;
    private boolean bloquearUser;
    private boolean solicitarSerTipster;
    private Activity activity;
    private FirebaseUser user;
    private String foto_uri = "";
    private StorageTask uploadTask;

//    private Data dataTemp = new Data();
//    private Data dataNascimento;

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
        esporte = findViewById(R.id.sp_esporte);
        mercado = findViewById(R.id.sp_mercado);
        selecionado = findViewById(R.id.tv_selecionado);
        esportes_add = findViewById(R.id.tv_esportes_add);
        mercados_add = findViewById(R.id.tv_mercados_add);
        esportesRecycler = findViewById(R.id.rv_esportes);
        mercadosRecycler = findViewById(R.id.rv_mercados);
        telefone = findViewById(R.id.et_telefone);
        estado = findViewById(R.id.sp_estado);
        privacidade = findViewById(R.id.sp_privacidade);
        final Spinner categoria = findViewById(R.id.sp_categoria);
        final TextView privacidade_info = findViewById(R.id.tv_info_privacidade);
        final LinearLayout telefone_container = findViewById(R.id.ll_telefone_container);
        final LinearLayout tipster_container = findViewById(R.id.ll_tipster_container);
        final Button queroSerTipster = findViewById(R.id.quero_ser_tipster);
        final Button btn_voltar = findViewById(R.id.cancelar);
        final Button btn_salvar = findViewById(R.id.salvar);
        //endregion

        //region Bundle
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isPrimeiroLogin = bundle.getBoolean(Constantes.intent.PRIMEIRO_LOGIN, false);
        }
        //endregion

        //region setValues

        esportes = new HashMap<>();
        email.setEnabled(false);
        telefone.setFocusable(false);
        // deixar assim até criar o método de verificação do numero de telefone
        telefone_container.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        isTipster = Import.getFirebase.isTipster();
        if (!isPrimeiroLogin) {
            Usuario usuario;
            if (isTipster) {
                esportes.putAll(Import.getFirebase.getTipster().getEsportes());
                usuario = Import.getFirebase.getTipster().getDados();
                categoria.setSelection(1);

                mercadosRecycler.setAdapter(mercadosAdapter);
                esportesAdapter = new TextAdapter(activity, esportes) {
                    @Override
                    public void onClick(View v) {
                        int itemPosition = esportesRecycler.getChildAdapterPosition(v);
                        currentEsporte = esportesAdapter.getEsporte(itemPosition);
                        mercadosAdapter = new TextAdapter(activity, currentEsporte != null ? currentEsporte.getMercados() : null, false) {
                            @Override
                            public boolean onLongClick(View v) {
                                int itemPosition = mercadosRecycler.getChildAdapterPosition(v);
                                String nome = mercadosAdapter.getMercado(itemPosition);
                                confirmExlusao(currentEsporte.getMercados().get(nome), null);
                                return super.onLongClick(v);
                            }
                        };
                        mercadosRecycler.setAdapter(mercadosAdapter);
                        mercadosAdapter.notifyDataSetChanged();

                        atualizarSelecionado(currentEsporte.getNome(), true);
                        if (currentEsporte.getMercados().containsValue(mercado.getSelectedItem().toString()))
                            mercados_add.setText(getResources().getString(R.string._menos));
                        else
                            mercados_add.setText(getResources().getString(R.string._mais));
                    }

                    @Override
                    public boolean onLongClick(View v) {
                        int itemPosition = esportesRecycler.getChildAdapterPosition(v);
                        Esporte e =  esportesAdapter.getEsporte(itemPosition);
                        confirmExlusao(e.getNome(), e);
                        return super.onLongClick(v);
                    }
                };
                esportesRecycler.setAdapter(esportesAdapter);
                queroSerTipster.setText(getResources().getString(R.string.quero_ser_um_punter));
                queroSerTipster.setVisibility(View.VISIBLE);
                tipster_container.setVisibility(View.VISIBLE);
            } else {
                usuario = Import.getFirebase.getPunter().getDados();
                categoria.setSelection(0);
                tipster_container.setVisibility(View.GONE);
            }
            if (usuario != null) {
                if (usuario.isBloqueado()) {
                    tipster_container.setVisibility(View.GONE);
                    queroSerTipster.setVisibility(View.VISIBLE);
                    queroSerTipster.setText(getResources().getString(R.string.em_analize));
                }
                info.setText(usuario.getInfo());
                tipName.setText(usuario.getTipname());
                estado.setSelection(usuario.getEndereco().getEstado());
                nascimento.setText(usuario.getNascimento().toString());
            }
        } else {
            tipster_container.setVisibility(View.GONE);
        }
        tipName.setEnabled(isPrimeiroLogin);
        categoria.setEnabled(!isTipster);

        user = Import.getFirebase.getUser();
        nome.setText(user.getDisplayName());
        email.setText(user.getEmail());

        boolean telefoneNull = false;
        if (user.getPhotoUrl() != null)
            foto_uri = user.getPhotoUrl().toString();
        if (user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty())
            telefone.setText(user.getPhoneNumber());
        else {
            telefoneNull = true;
            telefone.setHint(getResources().getString(R.string.registrar_numero));
        }

        Glide.with(activity).load(foto_uri).into(foto);

        SimpleMaskFormatter formatter = new SimpleMaskFormatter(Constantes.formats.DATA);
        MaskTextWatcher watcher = new MaskTextWatcher(nascimento, formatter);
        nascimento.addTextChangedListener(watcher);
        //endregion

        //region setListener

        foto.setOnClickListener(view -> {
            {
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
            }
        });

        esportes_add.setOnClickListener(v -> {
            if (esporte.getSelectedItemPosition() != 0) {
                String name = esporte.getSelectedItem().toString();
                Esporte e = esportes.get(name);

                if (e == null) {
                    e = new Esporte();
                    e.setNome(name);
                    esportes.put(e.getNome(), e);
                    esportesAdapter.notifyDataSetChanged();
                    esportes_add.setText(getResources().getString(R.string._menos));
                } else {
                    confirmExlusao(e.getNome(), e);
                }
            }
        });
        mercados_add.setOnClickListener(v -> {
            if (mercadosAdapter != null) {
                if (mercado.getSelectedItemPosition() != 0){
                    String item = mercado.getSelectedItem().toString();

                    if (currentEsporte != null)
                        if (currentEsporte.getMercados().containsValue(item)) {
                            confirmExlusao(item, null);
                        } else {
                            currentEsporte.getMercados().put(item, item);
                            mercadosAdapter.notifyDataSetChanged();
                            mercados_add.setText(getResources().getString(R.string._menos));
                        }
                } else {
                    Import.Alert.snakeBar(activity, getResources().getString(R.string.selecione_mercado));
                }
            } else {
                Import.Alert.snakeBar(activity, getResources().getString(R.string.selecione_esporte));
            }
        });
        /*nascimento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Calendar maxDate = Calendar.getInstance();
                    maxDate.set(Import.get.calendar.ano() - 18, Import.get.calendar.mes(), Import.get.calendar.dia());

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        DatePickerDialog picker = new DatePickerDialog(activity);
                        picker.getDatePicker().setMaxDate(maxDate.getTimeInMillis());
                        try {
                            picker.updateDate(dataNascimento.getAno(), dataNascimento.getMes(), dataNascimento.getDia());
                        } catch (Exception e) {
                            Import.Alert.erro(TAG, e);
                        }

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

                        try {
                            calendar.set(dataNascimento.getAno(), dataNascimento.getMes(), dataNascimento.getDia());
                            calendarView.setDate(calendar.getTimeInMillis());
                        } catch (Exception e) {
                            Import.Alert.erro(TAG, e);
                        }
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
                } catch (Exception e) {
                    Import.Alert.erro(TAG, e);
                }
            }
        });*/

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
            } else
            if (Objects.equals(queroSerTipster.getText().toString(), getResources().getString(R.string.quero_ser_um_punter)))
                QueroSerPunter();
            else {
                Usuario usuario = Import.getFirebase.getUsuario();
                QueroSerTipster(usuario == null || !usuario.isBloqueado());
            }
        });

        esporte.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String nome = ((String) esporte.getSelectedItem());
                Esporte e = esportes.get(nome);
                if (e == null)
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
                if (currentEsporte != null && currentEsporte.getMercados().containsValue(mercado.getItemAtPosition(position).toString()))
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
                    bloquearUser = false;
                    solicitarSerTipster = false;
                } else
                    queroSerTipster.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        if (telefoneNull)
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

    private boolean CriarUsuario() {
        progressBar.setVisibility(View.VISIBLE);
        if (uploadTask != null && uploadTask.isInProgress()) {
            foto.setBackground(getDrawable(R.drawable.bg_circulo_vermelho));
            Import.Alert.toast(activity, getResources().getString(R.string.carregando_a_imagem));
        } else {
            Data data = getData(nascimento.getText().toString());

            Usuario usuario = new Usuario();
            usuario.setId(user.getUid());
            usuario.setTipname(tipName.getText().toString());
            usuario.setNome(nome.getText().toString());
            usuario.setEmail(email.getText().toString());
            usuario.setFoto(foto_uri);
            usuario.setNascimento(data);
            usuario.setTelefone(telefone.getText().toString());
            usuario.getEndereco().setEstado(estado.getSelectedItemPosition());
            usuario.setPrivado(privacidade.getSelectedItemPosition() == 1);
            usuario.setBloqueado(bloquearUser);
            usuario.setInfo(info.getText().toString());
            return VerificarDadosAntesDeSalvar(usuario);
        }
        return false;
    }

    private boolean VerificarDadosAntesDeSalvar(final Usuario usuario) {
        if (usuario.getTipname().isEmpty())
            tipName.setError(getResources().getString(R.string.tipName_obrigatório));
        else if (usuario.getTipname().length() < 6)
            tipName.setError(getResources().getString(R.string.TipName_deve_conter_no_mínimo_6_dígitos));
        else if (usuario.getNome().isEmpty())
            nome.setError(getResources().getString(R.string.insira_seu_nome));
        else if (usuario.getNascimento() == null)
            nascimento.setError(getResources().getString(R.string.data_incorreta));
        else if (usuario.getNascimento().getIdade() < 18)
            nascimento.setError(getResources().getString(R.string.idade_minima));
        else if (usuario.getFoto() == null || usuario.getFoto().isEmpty()) {
            foto.setBackground(getDrawable(R.drawable.bg_circulo_vermelho));
            Import.Alert.toast(activity, getResources().getString(R.string.foto_de_usuario_obrigatoria));
        } else {
            if (!usuario.getNome().equals(user.getDisplayName()))
                AlterarNome(usuario.getNome());

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
                        if (values.contains(usuario.getTipname().toLowerCase())) {
                            tipName.setError(getResources().getString(R.string.aviso_tipname_repetido));
                            progressBar.setVisibility(View.GONE);
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
            return true;
        }
        progressBar.setVisibility(View.GONE);
        return false;
    }

    private void atualizarSelecionado(String name, boolean mostrar) {
        if (mostrar)
            selecionado.setVisibility(View.VISIBLE);
        else
            selecionado.setVisibility(View.GONE);
        String s = "(" + name + ")";
        selecionado.setText(s);
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
                bloquearUser = true;
                solicitarSerTipster = true;
                if (CriarUsuario())
                    Import.reiniciarApp(activity);
            });
        } else {
            dialog.setPositiveButton(getResources().getString(R.string.ok), null);
            dialog.setNeutralButton(getResources().getString(R.string.cancelar_solicitacao), (dialog12, which) -> {
                bloquearUser = false;
                solicitarSerTipster = false;
                isTipster = false;
                if (CriarUsuario()) {
                    if (Import.getFirebase.getTipster() != null) {
                        Import.getFirebase.getTipster().solicitarSerTipsterCancelar();
                        Import.getFirebase.getTipster().excluir();
                        Import.getFirebase.setUser(activity, (Tipster) null);
                    }
                    Import.reiniciarApp(activity);
                }
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
            bloquearUser = false;
            solicitarSerTipster = false;
            isTipster = false;
            if (CriarUsuario()) {
                if (Import.getFirebase.getTipster() != null) {
                    Import.getFirebase.getTipster().excluir();
                    Import.getFirebase.setUser(activity, (Tipster) null);
                }
                Import.reiniciarApp(activity);
            }
        });
        dialog.show();
    }

    private void AlterarNome(final String nome) {
        UserProfileChangeRequest changeRequest = new UserProfileChangeRequest.Builder().setDisplayName(nome).build();
        user.updateProfile(changeRequest).addOnCompleteListener(task -> Import.Alert.msg(TAG, "Nome alterado para", nome));
    }

    private void AlterarFoto(final Uri uri) {
        UserProfileChangeRequest changeRequest = new UserProfileChangeRequest.Builder().setPhotoUri(uri).build();
        user.updateProfile(changeRequest).addOnCompleteListener(task -> Import.Alert.msg(TAG, "Foto alterada", uri.toString()));
    }

    private void confirmExlusao(final String itemName, final Esporte esporte) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setTitle(itemName);
        dialog.setMessage(getResources().getString(R.string.remover_item));
        dialog.setPositiveButton(getResources().getString(R.string.sim), (dialog1, which) -> {
            if (esporte == null) {
                currentEsporte.getMercados().remove(itemName);
                if (mercadosAdapter != null)
                    mercadosAdapter.notifyDataSetChanged();
                mercados_add.setText(getResources().getString(R.string._mais));
            } else {
                esportes.remove(esporte.getNome());
                esportesAdapter.notifyDataSetChanged();
                if (mercadosAdapter != null)
                    mercadosAdapter.notifyDataSetChanged();
                currentEsporte = null;
                atualizarSelecionado("", false);
                esportes_add.setText(getResources().getString(R.string._mais));
                mercados_add.setText(getResources().getString(R.string._mais));
            }
        });
        dialog.setNegativeButton(getResources().getString(R.string.nao), null);
        dialog.show();
    }

    private Data getData(String s) {
        try {
            String[] teste = s.split("/");
            Data data = new Data();
            int dia = Integer.parseInt(teste[0]);
            int mes = Integer.parseInt(teste[1]);
            int ano = Integer.parseInt(teste[2]);

            if (dia == 0 || mes == 0 || dia > 31 || mes > 12 || ano < 1900)
                return null;

            data.setDia(dia);
            data.setMes(mes);
            data.setAno(ano);
            return data;
        } catch (Exception ignored) {
            return null;
        }
    }

    private void UparFoto(final Usuario usuario) {
        if (!fotoUserIsLocal) {
            if (isTipster)
                Salvar(usuario.toTipster());
            else
                Salvar(usuario.toPunter());
        }
        else {
            Uri uri = Uri.parse(usuario.getFoto());
            uploadTask = Import.getFirebase.getStorage()
                    .child(Constantes.firebase.child.USUARIO)
                    .child(Constantes.firebase.child.PERFIL)
                    .child(usuario.getId())
                    .putFile(uri).addOnSuccessListener(taskSnapshot -> {
                        Handler handler = new Handler();
                        handler.postDelayed(() -> progressBarHorizontal.setProgress(0), 500);

                        if (taskSnapshot.getMetadata() != null && taskSnapshot.getMetadata().getReference() != null)
                            taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnCompleteListener(task -> {
                                if (task.getResult() != null) {
                                    AlterarFoto(task.getResult());
                                    usuario.setFoto(task.getResult().toString());
                                    if (isTipster)
                                        Salvar(usuario.toTipster());
                                    else
                                        Salvar(usuario.toPunter());
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

    private void Salvar(Punter usuario) {
        if (Import.getFirebase.getPunter() != null)
            usuario.setTipsters(Import.getFirebase.getPunter().getTipsters());
        // Salva no firebase
        usuario.salvar();
        Import.getFirebase.setUser(activity, usuario);
        try {
            Glide.with(activity).load(usuario.getDados().getFoto()).into(foto);
            SalvarComum();
        } catch (Exception ignored){}
    }

    private void Salvar(Tipster usuario) {
        usuario.setEsportes(esportes);
        if (Import.getFirebase.getTipster() != null) {
            usuario.setPuntersPendentes(Import.getFirebase.getTipster().getPuntersPendentes());
            usuario.setPostes(Import.getFirebase.getTipster().getPostes());
            usuario.setPunters(Import.getFirebase.getTipster().getPunters());
        }
        // Salva no firebase
        usuario.salvar();
        Import.getFirebase.setUser(activity, usuario);
        try {
            if (solicitarSerTipster) {
                if (Import.getFirebase.getPunter() != null)
                    Import.getFirebase.getPunter().bloquear();
                usuario.solicitarSerTipster();
            }
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
