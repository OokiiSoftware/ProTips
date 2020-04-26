package com.ookiisoftware.protips.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.ookiisoftware.protips.R;
import com.ookiisoftware.protips.adapter.SectionsPagerAdapter;

import java.util.Objects;

public class BatepapoActivity extends AppCompatActivity {

//    private static final String TAG = "BatepapoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batepapo);
        Init();
    }

    private void Init() {
        ViewPager viewPager;
        Toolbar toolbar;
        TabLayout tabs;
        {
            toolbar = findViewById(R.id.batepapo_toolbar);
            viewPager = findViewById(R.id.batepapo_view_pager);
            tabs = findViewById(R.id.batepapo_tabs);
        }// Pegar elementos do layout pelo ID

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), 0, this);

        {
            setSupportActionBar(toolbar);
            Objects.requireNonNull(getSupportActionBar()).setTitle(getResources().getString(R.string.titulo_batepapo));
//            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        }// ActionBar

        {
            viewPager.setAdapter(sectionsPagerAdapter);
            tabs.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.colorAccent));
            tabs.setTabTextColors(ContextCompat.getColor(this, R.color.colorPrimaryLight), ContextCompat.getColor(this, R.color.text_dark));
            tabs.setupWithViewPager(viewPager);
        }// Tabs
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_batepapo, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }
            case R.id.menu_batepapo_pesquisa: {

                return true;
            }
            case R.id.menu_batepapo_add_contato: {
                AbrirDialogParaAddContatos();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean tenhoEsteContato;
    private void AbrirDialogParaAddContatos() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Adicionar um TipFrend");
        alert.setMessage("Digite o TipName do seu amigo");
        alert.setCancelable(false);

        final EditText editText = new EditText(this);
        alert.setView(editText);
        tenhoEsteContato = false;
        /*{
            alert.setPositiveButton("Adicionar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    final String tipName = Criptografia.criptografar(editText.getText().toString());
                    if (tipName.isEmpty())
                        editText.setError("Insira um TipName");
                    else {
                        {
                            Import.getFirebase.getReference().child(Constantes.USUARIO)
                                    .child(Import.getFirebase.getId())
                                    .child(Constantes.CONTATO)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot data : dataSnapshot.getChildren()){
                                                if (data.getValue() != null){
                                                    if (data.getValue().toString().equals(tipName)){
                                                        tenhoEsteContato = true;
                                                        break;
                                                    }
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        }// Verificar se eu já tenho este contato

                        if(!tenhoEsteContato) {

                            Import.getFirebase.getReference()
                                    .child(Constantes.USUARIO)
                                    .child(tipName)
                                    .child(Constantes.USUARIO_DADOS)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.getValue() != null) {
                                                {
                                                    Usuario usuarioContato = dataSnapshot.getValue(Usuario.class);
                                                    if (usuarioContato != null){
                                                        SQLiteContato db = new SQLiteContato(BatepapoActivity.this);
                                                        Contato contato = Usuario.converterParaContato(usuarioContato);
                                                        db.update(contato);
                                                    }
                                                }// Salvar no banco de dados local

                                                {
                                                    Import.getFirebase.getReference()
                                                            .child(Constantes.USUARIO)
                                                            .child(Import.getFirebase.getId())
                                                            .child(Constantes.CONTATO)
                                                            .push()
                                                            .setValue(tipName);
                                                }// Salvar nos meus contatos no firebase
                                            } else
                                                Alert();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                        }
                                    });
                        }// Se eu não tenho este contato, procuro na lista de usuários
                    }
                }
            });
            alert.setNegativeButton("Cancelar", null);
        }*/// Botões
        alert.create();
        alert.show();
    }


    private void Alert() {
        Toast.makeText(this, "Tiper não encontrado", Toast.LENGTH_LONG).show();
    }

}
