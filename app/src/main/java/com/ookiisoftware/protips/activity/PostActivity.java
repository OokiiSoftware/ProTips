package com.ookiisoftware.protips.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ookiisoftware.protips.R;
import com.ookiisoftware.protips.auxiliar.Constantes;
import com.ookiisoftware.protips.auxiliar.Import;
import com.ookiisoftware.protips.modelo.Post;

public class PostActivity extends AppCompatActivity {

    //region Variáveis

//    private static final String TAG = "PostActivity";
    private Activity activity;
    private String foto_path;

    private final String FOTO_PATH = "photo_path_saved";

    private ImageView foto;
    private ProgressBar progressBar;

    //endregion

    //region Overrides

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        activity = this;
        Init();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(FOTO_PATH, foto_path);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        foto_path = savedInstanceState.getString(FOTO_PATH);
        if (foto_path != null && !foto_path.isEmpty())
            Glide.with(activity).load(foto_path).into(foto);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constantes.REQUEST_PERMISSION_STORANGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            foto_path = uri.toString();
            Glide.with(activity).load(uri).into(foto);
        }
    }

    //endregion

    //region Métodos

    private void Init () {
        //region findViewById
        foto = findViewById(R.id.foto);
        progressBar = findViewById(R.id.progressBar);
        final TextView textLength = findViewById(R.id.popup_length);
        final EditText titulo = findViewById(R.id.titulo);
        final EditText texto = findViewById(R.id.texto);
        final EditText odd_max = findViewById(R.id.odd_max);
        final EditText odd_min = findViewById(R.id.odd_min);
        final FloatingActionButton fab = findViewById(R.id.fab);
        final EditText horario_minimo = findViewById(R.id.btn_horario_minimo);
        final EditText horario_maximo = findViewById(R.id.btn_horario_maximo);
        final Spinner esporte = findViewById(R.id.esporte);
        final Spinner mercado = findViewById(R.id.mercado);
        Toolbar toolbar = findViewById(R.id.toolbar);
        //endregion

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.titulo_criar_tip));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        ArrayAdapter mercadoAdapter = ArrayAdapter.createFromResource(activity, R.array.mercado, R.layout.item_spinner);
        mercado.setAdapter(mercadoAdapter);

        ArrayAdapter esporteAdapter = ArrayAdapter.createFromResource(activity, R.array.esporte, R.layout.item_spinner);
        esporte.setAdapter(esporteAdapter);

        //region setListener

        texto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String valor = texto.getText().length() + "/200";
                textLength.setText(valor);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
        foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pegarFotoDaGaleria();
            }
        });

        horario_minimo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    final int hora = toHour(horario_minimo.getText().toString());
                    final int min = toMinute(horario_minimo.getText().toString());
                    TimePickerDialog dialog = new TimePickerDialog(activity, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            horario_minimo.setText(dateToString(hourOfDay, minute));
                        }
                    }, hora, min, true);
                    dialog.show();
                }
            }
        });
        horario_maximo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    final int hora = toHour(horario_maximo.getText().toString());
                    final int min = toMinute(horario_maximo.getText().toString());
                    TimePickerDialog dialog = new TimePickerDialog(activity, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            horario_maximo.setText(dateToString(hourOfDay, minute));
                        }

                    }, hora, min, true);
                    dialog.show();
                }
            }

        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {
                    progressBar.setVisibility(View.VISIBLE);
                    Post post = new Post();
                    post.setTitulo(titulo.getText().toString());
                    post.setTexto(texto.getText().toString());
                    post.setOdd_maxima(odd_max.getText().toString());
                    post.setOdd_minima(odd_min.getText().toString());
                    post.setHorario_maximo(horario_maximo.getText().toString());
                    post.setHorario_minimo(horario_minimo.getText().toString());
                    post.setFoto(foto_path);
                    post.setEsporte(esporte.getSelectedItemPosition());
                    post.setMercado(mercado.getSelectedItemPosition());
                    post.setId_tipster(Import.getFirebase.getId());
                    post.setId(Import.get.randomString());
                    post.setData(Import.get.Data());
                    verificar(post);
                }
            }
        });

        //endregion
    }

    private void verificar(Post post) {
        if (post.getTitulo() == null || post.getTitulo().isEmpty()) {
            Import.Alert.snakeBar(activity.getCurrentFocus(), activity.getResources().getString(R.string.post_titulo_obrigatorio));
            progressBar.setVisibility(View.GONE);
        } else if (post.getTexto() == null || post.getTexto().isEmpty()) {
            Import.Alert.snakeBar(activity.getCurrentFocus(), activity.getResources().getString(R.string.post_texto_obrigatorio));
            progressBar.setVisibility(View.GONE);
        } else if (post.getFoto() == null || post.getFoto().isEmpty()) {
            Import.Alert.snakeBar(activity.getCurrentFocus(), activity.getResources().getString(R.string.post_foto_obrigatorio));
            progressBar.setVisibility(View.GONE);
        } else if (post.getOdd_maxima() == null || post.getOdd_maxima().isEmpty()) {
            Import.Alert.snakeBar(activity.getCurrentFocus(), activity.getResources().getString(R.string.post_odd_maxima_obrigatorio));
            progressBar.setVisibility(View.GONE);
        } else if (post.getOdd_minima() == null || post.getOdd_minima().isEmpty()) {
            Import.Alert.snakeBar(activity.getCurrentFocus(), activity.getResources().getString(R.string.post_odd_minima_obrigatorio));
            progressBar.setVisibility(View.GONE);
        } else if (post.getEsporte() == 0) {
            Import.Alert.snakeBar(activity.getCurrentFocus(), activity.getResources().getString(R.string.post_esporte_obrigatorio));
            progressBar.setVisibility(View.GONE);
        } else if (post.getMercado() == 0) {
            Import.Alert.snakeBar(activity.getCurrentFocus(), activity.getResources().getString(R.string.post_mercado_obrigatorio));
            progressBar.setVisibility(View.GONE);
        } else {
            post.salvar(activity, progressBar,true);
        }
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

    private void pegarFotoDaGaleria() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, Constantes.REQUEST_PERMISSION_STORANGE);
    }

    //endregion

}
