package com.ookiisoftware.protips.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.ookiisoftware.protips.R;
import com.ookiisoftware.protips.adapter.SectionsPagerAdapter;
import com.ookiisoftware.protips.auxiliar.Constantes;
import com.ookiisoftware.protips.auxiliar.Import;
import com.ookiisoftware.protips.auxiliar.SegundoPlanoService;
import com.ookiisoftware.protips.fragment.InicioFragment;
import com.ookiisoftware.protips.fragment.PerfilFragment;
import com.ookiisoftware.protips.fragment.TipsFragment;
import com.ookiisoftware.protips.modelo.Post;
import com.ookiisoftware.protips.modelo.Tipster;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import java.util.Objects;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, ViewPager.OnPageChangeListener, NavigationView.OnNavigationItemSelectedListener {

    //region Variáveis

    private static final String TAG = "MainActivity";
    private Activity activity;

    private ViewPager viewPager;
    private DrawerLayout drawer;
    private BottomNavigationView navView;
    private Dialog popup_post;

    // Itens do Menu
    MenuItem inicio_postar_feed;

    private SectionsPagerAdapter sectionsPagerAdapter;
    private int NavCurrentItem = 0;
    private String foto_post_path;

    private DatabaseReference refMeusTipsters;
    private ChildEventListener eventMeusTipsters;
    private DatabaseReference refAllTipsters;
    private ChildEventListener eventAllTipsters;

    // Titulo personalizado pra ActionBar
    private AppCompatTextView action_bar_titulo_1, action_bar_titulo_2;

    private InicioFragment inicioFragment;
    private TipsFragment tipsFragment;
    private PerfilFragment perfilFragment;

    //endregion

    //region Overrides

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(getApplicationContext(), SegundoPlanoService.class));

        activity = this;
        Import.Alert.msg(TAG, "Timezone1", TimeZone.getDefault().getID());
        Import.Alert.msg(TAG, "Timezone2", TimeZone.getDefault().getDisplayName());

        try {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                Intent intent = new Intent(activity, UserEditActivity.class);
                intent.putExtra(Constantes.PRIMEIRO_LOGIN, true);
                startActivity(intent);
            }
            Init();
        } catch (Exception ex) {
            Import.Alert.erro(TAG, ex);
            Import.Alert.toast(activity, getResources().getString(R.string.erro_init_main_activity));
            Import.IrProLogin(activity);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
//        refMeusTipsters.addChildEventListener(eventMeusTipsters);
        if (!Import.getFirebase.isTipster())
            refAllTipsters.addChildEventListener(eventAllTipsters);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!Import.getFirebase.isTipster())
            refAllTipsters.removeEventListener(eventAllTipsters);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        inicio_postar_feed = menu.getItem(0);
        SwithMenu(0);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home: {
                drawer.openDrawer(GravityCompat.START, true);
                break;
            }
            case R.id.menu_postar_feed: {
                PopupPost();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_menu_inicio:
                NavCurrentItem = 0;
                break;
            case R.id.nav_menu_dashboard:
                NavCurrentItem = 1;
                break;
            case R.id.nav_menu_perfil:
                NavCurrentItem = 2;
                break;
            case R.id.nav_menu_notifications:
                NavCurrentItem = 3;
                break;
            case R.id.menu_lateral_batepapo: {
                Intent intent = new Intent(activity, BatepapoActivity.class);
                startActivity(intent);
                drawer.closeDrawer(GravityCompat.START, true);
                break;
            }
            case R.id.menu_lateral_logout: {
                Import.LogOut(activity);
                break;
            }
        }
        viewPager.setCurrentItem(NavCurrentItem);

        return true;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
        String[] titulo_da_pagina = Objects.requireNonNull(sectionsPagerAdapter.getPageTitle(position)).toString().split(" ");
        if (titulo_da_pagina.length > 1){
            action_bar_titulo_1.setText(titulo_da_pagina[0]);
            action_bar_titulo_1.setTypeface(Import.getFonteNormal(activity));
            action_bar_titulo_2.setText(titulo_da_pagina[1]);
            action_bar_titulo_2.setTypeface(Import.getFonteBold(activity));
        } else {
            action_bar_titulo_1.setText(titulo_da_pagina[0]);
            action_bar_titulo_1.setTypeface(Import.getFonteBold(activity));
            action_bar_titulo_2.setText("");
        }
        switch (position) {
            case 0:
                navView.setSelectedItemId(R.id.nav_menu_inicio);
                break;
            case 1:
                navView.setSelectedItemId(R.id.nav_menu_dashboard);
                break;
            case 2:
                navView.setSelectedItemId(R.id.nav_menu_perfil);
                break;
            case 3:
                navView.setSelectedItemId(R.id.nav_menu_notifications);
                break;
        }
        SwithMenu(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constantes.REQUEST_PERMISSION_STORANGE && resultCode == RESULT_OK && data != null && data.getData() != null){
            if (popup_post != null) {
                Uri uri = data.getData();
//                Import.Alert.msg(TAG, "onActivityResult", uri);
                ImageView imageView = popup_post.findViewById(R.id.foto);
                Glide.with(activity).load(uri).into(imageView);
                foto_post_path = uri.toString();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(viewPager.getCurrentItem() != 0)
            viewPager.setCurrentItem(0);
        else {
            super.onBackPressed();
        }
    }

    //endregion

    //region Métodos

    private void Init() {
        //region findViewById
        Toolbar toolbar = findViewById(R.id.toolbar);
        NavigationView navigationView = findViewById(R.id.menu_lateral);
        viewPager = findViewById(R.id.view_pager_main_activity);
        navView = findViewById(R.id.nav_view_main_activity);
        drawer = findViewById(R.id.drawer_layout);
        //endregion

        inicioFragment = new InicioFragment(activity);
        tipsFragment = new TipsFragment(activity);
        perfilFragment = new PerfilFragment();

        //region Toolbar
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        action_bar_titulo_1 = findViewById(R.id.action_bar_titulo_1);
        action_bar_titulo_2 = findViewById(R.id.action_bar_titulo_2);
        //endregion

        //region NavigationView
        navigationView.setNavigationItemSelectedListener(this);
        navView.setOnNavigationItemSelectedListener(this);
        //endregion

        //region SectionsPagerAdapter
        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), 0, activity, inicioFragment, tipsFragment, perfilFragment);
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.addOnPageChangeListener(this);
        //endregion

//        getMyTipsters();
        getAllTipsters();
    }

    private void SwithMenu(int position) {
        switch (position) {
            case 0: {
                if (Import.getFirebase.isTipster())
                    inicio_postar_feed.setVisible(true);
                break;
            }
            case 1:
            case 2:
            case 3:
                inicio_postar_feed.setVisible(false);
                break;
        }
    }

    private void PopupPost () {
        popup_post = new Dialog(activity);
        popup_post.requestWindowFeature(Window.FEATURE_NO_TITLE);

        popup_post.setCanceledOnTouchOutside(false);
        popup_post.setContentView(R.layout.popup_post);
        pegarFotoDaGaleria();
        
        //region findViewById
        final TextView textLength = popup_post.findViewById(R.id.popup_length);
        final EditText editText = popup_post.findViewById(R.id.texto);
        final Button btn_positivo = popup_post.findViewById(R.id.popup_btn_positivo);
        final Button btn_negativo = popup_post.findViewById(R.id.popup_btn_negativo);
        final ImageView foto = popup_post.findViewById(R.id.foto);
        //endregion

        //region Listener
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String valor = editText.getText().length() + "/200";
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
        btn_positivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String texto = editText.getText().toString();
                if (texto.isEmpty() || foto_post_path == null)
                    return;

                Post post = new Post();
                post.setTexto(texto);
                post.setFoto(foto_post_path);
                post.setId(Import.get.randomString());
                post.setData(Import.get.Data());
                post.salvar(activity,true);
                popup_post.dismiss();
                Import.get.tipsters.postes().add(post);
                inicioFragment.adapterUpdate();
            }
        });
        btn_negativo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup_post.dismiss();
            }
        });
        //endregion

        popup_post.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                popup_post = null;
            }
        });
        popup_post.show();
    }

    private void getAllTipsters() {
        refAllTipsters = Import.getFirebase.getReference()
                .child(Constantes.firebase.child.USUARIO)
                .child(Constantes.firebase.child.TIPSTERS);

        eventAllTipsters = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                try {
                    Tipster item = dataSnapshot.getValue(Tipster.class);
                    if (item != null)
                        if (Import.get.tipsters.FindTipster(item.getDados().getId()) == null) {
                            Import.get.tipsters.getAll().add(item);
                            tipsFragment.adapterUpdate();
                            if (Import.getFirebase.getApostador().getTipsters().contains(item.getDados().getId())){
                                Import.get.tipsters.postes().addAll(item.getPostes());
                                inicioFragment.adapterUpdate();
                            }
                        }
                } catch (Exception ex) {
                    Import.Alert.erro(TAG, ex);
                    Import.Alert.msg(TAG, "onChildAdded", dataSnapshot.getKey());
                }
//                for (DataSnapshot data : dataSnapshot.getChildren())
                /*{
                    String value = dataSnapshot.getKey();
                    Import.Alert.msg(TAG, "onChildAdded", value);
                    if (value != null) {
                        Import.getFirebase.getReference()
                                .child(Constantes.firebase.child.USUARIO)
                                .child(Constantes.firebase.child.TIPSTERS)
                                .child(value)
//                                .child(Constantes.firebase.child.DADOS)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Tipster item = dataSnapshot.getValue(Tipster.class);
                                        if (item != null)
                                            if (Import.get.tipsters.FindTipster(item.getDados().getId()) == null)
                                                Import.get.tipsters.tipsters().add(item);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                                });
                    }
                }*/
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getKey();
                Import.get.tipsters.getAll().remove(Import.get.tipsters.FindTipster(value));
                Import.Alert.msg(TAG, "onChildRemoved", value);
                tipsFragment.adapterUpdate();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
    }

    private void getMyTipsters() {
        refMeusTipsters = Import.getFirebase.getReference()
                .child(Constantes.firebase.child.USUARIO)
                .child(Constantes.firebase.child.APOSTADOR)
                .child(Import.getFirebase.getId())
                .child(Constantes.firebase.child.TIPSTERS);

        eventMeusTipsters = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String value = dataSnapshot.getValue(String.class);
                if (value != null) {
                    Import.getFirebase.getReference()
                            .child(Constantes.firebase.child.USUARIO)
                            .child(Constantes.firebase.child.TIPSTERS)
//                            .child(value)
//                            .child(Constantes.firebase.child.DADOS)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Tipster item = dataSnapshot.getValue(Tipster.class);
//                                    if (item != null)
//                                        if (Import.get.tipsters.FindMyTipster(item.getDados().getId()) == null)
//                                            Import.get.tipsters.meusTipsters().add(item);
//                                        tipsFragment.adapterUpdate();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {}
                            });

                    Import.getFirebase.getReference()
                            .child(Constantes.firebase.child.USUARIO)
                            .child(Constantes.firebase.child.TIPSTERS)
                            .child(value)
                            .child(Constantes.firebase.child.POSTES)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot data : dataSnapshot.getChildren()){
                                        Post item = data.getValue(Post.class);
                                        if (item != null) {
                                            Post item2 = Import.get.tipsters.FindPost(item.getId());
                                            if (item2 == null) {
                                                Import.get.tipsters.postes().add(item);
                                            } else {
                                                Import.get.tipsters.postes().set(Import.get.tipsters.postes().indexOf(item2), item);
                                            }
                                            inicioFragment.adapterUpdate();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {}
                            });
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getKey();
//                Import.get.tipsters.meusTipsters().remove(Import.get.tipsters.FindMyTipster(value));
//                Import.Alert.msg(TAG, "onChildRemoved", value);
//                tipsFragment.adapterUpdate();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
    }

    private void pegarFotoDaGaleria() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, Constantes.REQUEST_PERMISSION_STORANGE);
    }

    //endregion
}
