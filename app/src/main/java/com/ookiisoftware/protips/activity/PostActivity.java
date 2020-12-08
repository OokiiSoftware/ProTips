package com.ookiisoftware.protips.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ookiisoftware.protips.R;
import com.ookiisoftware.protips.auxiliar.Const;
import com.ookiisoftware.protips.auxiliar.Import;
import com.ookiisoftware.protips.modelo.AutoComplete;
import com.ookiisoftware.protips.modelo.Post;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.LinkedList;
import java.util.List;

public class PostActivity extends AppCompatActivity {

    //region Variáveis

    private static final String TAG = "PostActivity";
    private static final String TITULO = "354354";
    private static final String TEXTO = "34534";
    private static final String ODD_MAX = "7564";
    private static final String ODD_MIN = "45557";
    private static final String HOTARIO_MIN = "6785";
    private static final String HOTARIO_MAX = "45634";
    private static final String ESPORTE = "346486";
    private static final String MERCADO = "4537";
    private static final String PUBLICO = "6453";
    private static final String FOTO_PATH = "85675";

    private Activity activity;
    private String foto_path;

//    private ImageView foto;
    private ProgressBar progressBar;

    private TextView foto;
    private EditText link;
    private EditText titulo;
    private EditText descricao;
    private EditText odd_max;
    private EditText odd_min;
    private EditText odd_atual;
    private EditText unidades;
    private EditText horario_min;
    private EditText horario_max;
    private AutoCompleteTextView esporte;
    private AutoCompleteTextView mercado;
    private AutoCompleteTextView campeonato;
    private CheckBox tipPublico;

    private TextView textLength;

//    private Spinner esporte;
//    private Spinner mercado;

    private AutoComplete autoComplete;
//    private List<String> mercados = new LinkedList<>();
//    private List<String> esportes = new LinkedList<>();

    //endregion

    //region Overrides

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        activity = this;
        init();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(FOTO_PATH, foto_path);
        outState.putString(TITULO, titulo.getText().toString());
        outState.putString(TEXTO, descricao.getText().toString());
        outState.putString(ODD_MAX, odd_max.getText().toString());
        outState.putString(ODD_MIN, odd_min.getText().toString());
        outState.putString(HOTARIO_MIN, horario_min.getText().toString());
        outState.putString(HOTARIO_MAX, horario_max.getText().toString());

        outState.putString(ESPORTE, esporte.getText().toString());
        outState.putString(MERCADO, mercado.getText().toString());
        outState.putBoolean(PUBLICO, tipPublico.isChecked());

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        foto_path = savedInstanceState.getString(FOTO_PATH);
//        if (foto_path != null && !foto_path.isEmpty())
//            Glide.with(activity).load(foto_path).into(foto);

        titulo.setText(savedInstanceState.getString(TITULO));
        descricao.setText(savedInstanceState.getString(TEXTO));
        odd_max.setText(savedInstanceState.getString(ODD_MAX));
        odd_min.setText(savedInstanceState.getString(ODD_MIN));
        horario_min.setText(savedInstanceState.getString(HOTARIO_MIN));
        horario_max.setText(savedInstanceState.getString(HOTARIO_MAX));

        esporte.setSelection(savedInstanceState.getInt(ESPORTE));
        esporte.setSelection(savedInstanceState.getInt(MERCADO));
        tipPublico.setChecked(savedInstanceState.getBoolean(PUBLICO));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_post, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_ajuda: {
                AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
                dialog.setTitle(getResources().getString(R.string.informacao));
                dialog.setMessage(getResources().getString(R.string.info_tip_publico));
                dialog.setPositiveButton(getResources().getString(R.string.ok), null);
                dialog.show();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Const.permissions.STORANGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            foto_path = uri.toString();
            foto.setText(foto_path);
//            Glide.with(activity).load(uri).into(foto);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK && result != null && result.getUri() != null) {
                foto_path = result.getUri().toString();
                foto.setText(foto_path);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                if (result != null)
                    Import.Alert.e(TAG, "onActivityResult", result.getError().getMessage());
                Import.Alert.toast(activity, getResources().getString(R.string.erro_foto_salvar));
            }
        }
    }

    //endregion

    //region Métodos

    private void init() {
        //region findViewById
        foto = findViewById(R.id.tv_foto);
        progressBar = findViewById(R.id.progressBar);
        tipPublico = findViewById(R.id.cb_tip_publico);

        titulo = findViewById(R.id.et_titulo);
        link = findViewById(R.id.et_link);
        descricao = findViewById(R.id.et_texto);
        odd_max = findViewById(R.id.et_odd_max);
        odd_min = findViewById(R.id.et_odd_min);
        odd_atual = findViewById(R.id.et_odd_atual);
        unidades = findViewById(R.id.et_unidades);
        horario_min = findViewById(R.id.et_horario_min);
        horario_max = findViewById(R.id.et_horario_max);

        textLength = findViewById(R.id.tv_popup_length);

        esporte = findViewById(R.id.et_esporte);
        mercado = findViewById(R.id.et_mercado);
        campeonato = findViewById(R.id.et_campeonato);

        TextView postar = findViewById(R.id.tv_postar);
        Toolbar toolbar = findViewById(R.id.toolbar);
        //endregion

        //region autoComplete
        autoComplete = Import.get.autoComplete();
        List<String> esportes = new LinkedList<>(autoComplete.getEsportes().values());
        List<String> mercados = new LinkedList<>(autoComplete.getLinhas().values());
        List<String> campeonatos = new LinkedList<>(autoComplete.getCampeonatos().values());

        ArrayAdapter<String> esporteAdapter = new ArrayAdapter<>(activity, android.R.layout.select_dialog_item, esportes);
        ArrayAdapter<String> mercadoAdapter = new ArrayAdapter<>(activity, android.R.layout.select_dialog_item, mercados);
        ArrayAdapter<String> campeonatoAdapter = new ArrayAdapter<>(activity, android.R.layout.select_dialog_item, campeonatos);

        //Usado para especificar o número mínimo de caracteres que o usuário deve digitar para exibir a dica suspensa
        esporte.setThreshold(1);
        mercado.setThreshold(1);
        campeonato.setThreshold(1);
        esporte.setAdapter(esporteAdapter);
        mercado.setAdapter(mercadoAdapter);
        campeonato.setAdapter(campeonatoAdapter);
        //endregion

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.titulo_criar_tip));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        /*esportes.addAll(_esportes.getEsportes().keySet());
        Collections.sort(esportes, new Esporte.sortByNome());
        if (esportes.size() > 0) {
            HashMap<String, String> item = _esportes.getEsportes().get(esportes.get(0));
            if (item != null) {
                mercados.addAll(item.values());
            }
        }

        SpinnerAdapter esporteAdapter = new SpinnerStringAdapter(activity, android.R.layout.simple_spinner_item, esportes);
        esporte.setAdapter(esporteAdapter);

        //region setListener

        esporte.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> item = _esportes.getEsportes().get(esportes.get(position));
                if (item != null) {
                    mercados.clear();
                    mercados.addAll(item.values());
                    Collections.sort(mercados, new Esporte.sortByNome());
                    SpinnerAdapter mercadoAdapter = new SpinnerStringAdapter(activity, android.R.layout.simple_spinner_item, mercados);
                    mercado.setAdapter(mercadoAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });*/

        descricao.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (s.charAt(start) == '\n')
                        descricao.getText().delete(start, start + 1);
                } catch (Exception e) {
                    Import.Alert.e("Post", "onTextChanged", e);
                }

                String valor = descricao.getText().length() + "/200";
                textLength.setText(valor);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        foto.setOnClickListener(v -> Import.abrirCropView(activity, 0));

        horario_min.setOnClickListener(v -> {
            final int hora = toHour(horario_min.getText().toString());
            final int min = toMinute(horario_min.getText().toString());
            TimePickerDialog dialog = new TimePickerDialog(activity, (view, hourOfDay, minute) -> horario_min.setText(dateToString(hourOfDay, minute)), hora, min, true);
            dialog.show();
        });
        horario_max.setOnClickListener(v -> {
            final int hora = toHour(horario_max.getText().toString());
            final int min = toMinute(horario_max.getText().toString());
            TimePickerDialog dialog = new TimePickerDialog(activity, (view, hourOfDay, minute) -> horario_max.setText(dateToString(hourOfDay, minute)), hora, min, true);
            dialog.show();
        });

        tipPublico.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (Import.getFirebase.getTipster().getPostes(Import.get.calendar.hoje(), true).size() >= 5) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
                    dialog.setTitle(getResources().getString(R.string.informacao));
                    dialog.setMessage(getResources().getString(R.string.info_tip_publico_maximo_atingido));
                    dialog.setPositiveButton(getResources().getString(R.string.ok), null);
                    dialog.show();
                    tipPublico.setChecked(false);
                }
            }
        });

        postar.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            Post post = new Post();
            post.setHorario_maximo(horario_max.getText().toString());
            post.setHorario_minimo(horario_min.getText().toString());
            post.setLinha(mercado.getText().toString());
            post.setEsporte(esporte.getText().toString());
            post.setCampeonato(campeonato.getText().toString());
            post.setOdd_maxima(odd_max.getText().toString());
            post.setOdd_minima(odd_min.getText().toString());
            post.setOdd_atual(odd_atual.getText().toString());
            post.setUnidade(unidades.getText().toString());
            post.setId_tipster(Import.getFirebase.getId());
            post.setTitulo(titulo.getText().toString());
            post.setDescricao(descricao.getText().toString());
            post.setLink(link.getText().toString());
            post.setPublico(tipPublico.isChecked());
            post.setId(Import.get.randomString());
            post.setData(Import.get.Data());
            post.setFoto(foto_path);

            postar.setEnabled(verificar(post));
        });

        //endregion
    }

    private boolean verificar(Post post) {
        if (post.getTitulo() == null || post.getTitulo().isEmpty()) {
            Import.Alert.snakeBar(activity, activity.getResources().getString(R.string.post_titulo_obrigatorio));
            progressBar.setVisibility(View.GONE);
        } /*else if (post.getDescricao() == null || post.getDescricao().isEmpty()) {
            Import.Alert.snakeBar(activity, activity.getResources().getString(R.string.post_texto_obrigatorio));
            progressBar.setVisibility(View.GONE);
        }*/ else if (post.getFoto() == null || post.getFoto().isEmpty()) {
            Import.Alert.snakeBar(activity, activity.getResources().getString(R.string.post_foto_obrigatorio));
            progressBar.setVisibility(View.GONE);
        } else if (post.getOdd_atual() == null || post.getOdd_atual().isEmpty()) {
            Import.Alert.snakeBar(activity, activity.getResources().getString(R.string.post_odd_atual_obrigatorio));
            progressBar.setVisibility(View.GONE);
        } else if (post.getUnidade() == null || post.getUnidade().isEmpty()) {
            Import.Alert.snakeBar(activity, activity.getResources().getString(R.string.post_unidades_obrigatorio));
            progressBar.setVisibility(View.GONE);
        } else if (post.getEsporte() == null || post.getEsporte().isEmpty()) {
            Import.Alert.snakeBar(activity, activity.getResources().getString(R.string.post_esporte_obrigatorio));
            progressBar.setVisibility(View.GONE);
        } else if (post.getLink() == null || post.getLink().isEmpty()) {
            Import.Alert.snakeBar(activity, activity.getResources().getString(R.string.post_link_obrigatorio));
            progressBar.setVisibility(View.GONE);
        } /*else if (post.getOdd_minima() == null || post.getOdd_minima().isEmpty()) {
            Import.Alert.snakeBar(activity, activity.getResources().getString(R.string.post_odd_minima_obrigatorio));
            progressBar.setVisibility(View.GONE);
        }*/ else {
            post.postar(activity, progressBar,true);

            //region Salva este novo dado no banco de dados
            if (!autoComplete.getEsportes().containsKey(post.getEsporte())) {
                AutoComplete.add(Const.firebase.child.ESPORTES, post.getEsporte());
            }
            if (!autoComplete.getLinhas().containsKey(post.getLinha())) {
                AutoComplete.add(Const.firebase.child.LINHAS, post.getLinha());
            }
            if (!autoComplete.getCampeonatos().containsKey(post.getCampeonato())) {
                AutoComplete.add(Const.firebase.child.CAMPEONATOS, post.getCampeonato());
            }
            //endregion
            return false;
        }
        return true;
    }

    private int toHour(String valor) {
        return Integer.parseInt(valor.replace(" h", "").split(":")[0]);
    }

    private int toMinute(String valor) {
        return Integer.parseInt(valor.replace(" h", "").split(":")[1]);
    }

    private String dateToString(int hourOfDay, int minute) {
        return hourOfDay + ":" + minute + " h";
    }

    //endregion

}
