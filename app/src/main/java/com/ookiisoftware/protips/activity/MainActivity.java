package com.ookiisoftware.protips.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.ookiisoftware.protips.R;
import com.ookiisoftware.protips.adapter.CustomViewPager;
import com.ookiisoftware.protips.adapter.SectionsPagerAdapter;
import com.ookiisoftware.protips.auxiliar.Constantes;
import com.ookiisoftware.protips.auxiliar.Import;
import com.ookiisoftware.protips.auxiliar.SegundoPlanoService;
import com.ookiisoftware.protips.fragment.InicioFragment;
import com.ookiisoftware.protips.fragment.PerfilFragment;
import com.ookiisoftware.protips.fragment.TipstersFragment;
import com.ookiisoftware.protips.modelo.Activites;
import com.ookiisoftware.protips.modelo.Post;
import com.ookiisoftware.protips.modelo.Tipster;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SearchView;
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

    public CustomViewPager viewPager;
    private DrawerLayout drawer;
    private BottomNavigationView navView;

    // Itens do Menu
    MenuItem menu_new_feed;
    MenuItem menu_pesquisa;

    private SectionsPagerAdapter sectionsPagerAdapter;
    private int NavCurrentItem = 0;

    private DatabaseReference refMeusClientes;
    private ChildEventListener eventMeusClientes;
    private DatabaseReference refAllTipsters;
    private ChildEventListener eventAllTipsters;

    // Titulo personalizado pra ActionBar
    private AppCompatTextView action_bar_titulo_1, action_bar_titulo_2;

    public InicioFragment inicioFragment;
    private TipstersFragment tipstersFragment;
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
                Intent intent = new Intent(activity, perfilActivity.class);
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
        if (Import.getFirebase.isTipster())
            refMeusClientes.addChildEventListener(eventMeusClientes);
        else
            refAllTipsters.addChildEventListener(eventAllTipsters);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (Import.getFirebase.isTipster())
            refMeusClientes.removeEventListener(eventMeusClientes);
        else
            refAllTipsters.removeEventListener(eventAllTipsters);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu_new_feed = menu.findItem(R.id.menu_postar_feed);
        menu_pesquisa = menu.findItem(R.id.menu_pesquisa);

        SearchView et_pesquisa = (SearchView) menu_pesquisa.getActionView();
        et_pesquisa.setImeOptions(EditorInfo.IME_ACTION_DONE);
        et_pesquisa.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                tipstersFragment.refreshLayout.setEnabled(newText.isEmpty());
                inicioFragment.refreshLayout.setEnabled(newText.isEmpty());
                tipstersFragment.getAdapter().getFilter().filter(newText);
                return false;
            }
        });

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
                Intent intent = new Intent(activity, PostActivity.class);
                startActivity(intent);
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
    public void onBackPressed() {
        if(viewPager.getCurrentItem() != 0)
            viewPager.setCurrentItem(0);
        else {
            if (inicioFragment.scrollInTop())
                super.onBackPressed();
            else
                inicioFragment.rollToTop();
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
        tipstersFragment = new TipstersFragment(activity);
        perfilFragment = new PerfilFragment(activity);

        Import.activites = new Activites();
        Import.activites.setMainActivity(this);

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
        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), 0, activity, inicioFragment, tipstersFragment, perfilFragment);
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.addOnPageChangeListener(this);
        //endregion

        getAllTipsters();
        getMyTips();
    }

    private void SwithMenu(int position) {
        switch (position) {
            case 0: {
//                inicio_postar_feed.setVisible(Import.getFirebase.isTipster());
                break;
            }
            case 1:
            case 2:
            case 3:
                break;
        }
        menu_new_feed.setVisible(position == 0 && Import.getFirebase.isTipster());
        menu_pesquisa.setVisible(position == 1);
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
                    if (item != null){
                        Tipster item_2 = Import.get.tipsters.FindTipster(item.getDados().getId());
                        if (item_2 == null) {
                            Import.get.tipsters.getAll().add(item);
                            Import.get.tipsters.getAllAux().add(item);
                        } else {
                            Import.get.tipsters.getAll().set(Import.get.tipsters.getAll().indexOf(item_2), item);
                            Import.get.tipsters.getAllAux().set(Import.get.tipsters.getAllAux().indexOf(item_2), item);
                        }
                        if (Import.getFirebase.getApostador().getTipsters().contains(item.getDados().getId())) {
                            for (Post post : item.getPostes().values()) {
                                if (Import.get.tipsters.FindPost(post.getId()) == null)
                                    Import.get.tipsters.postes().add(post);
                            }
                        }
                        tipstersFragment.adapterUpdate();
                        inicioFragment.adapterUpdate();
                    }
                    inicioFragment.refreshLayout.setRefreshing(false);
                    tipstersFragment.refreshLayout.setRefreshing(false);
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
                Import.get.tipsters.getAllAux().remove(Import.get.tipsters.FindTipster(value));
                Import.Alert.msg(TAG, "onChildRemoved", value);
                tipstersFragment.adapterUpdate();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
    }

    private void getMyTips() {
        refMeusClientes = Import.getFirebase.getReference()
                .child(Constantes.firebase.child.USUARIO)
                .child(Constantes.firebase.child.TIPSTERS)
                .child(Import.getFirebase.getId())
                .child(Constantes.firebase.child.APOSTADORES);

        eventMeusClientes = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String value = dataSnapshot.getValue(String.class);
                if (value != null) {
                    Import.getFirebase.getReference()
                            .child(Constantes.firebase.child.USUARIO)
                            .child(Constantes.firebase.child.TIPSTERS)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                    Tipster item = dataSnapshot.getValue(Tipster.class);
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
//                String value = dataSnapshot.getKey();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
    }

    public void feedUpdate() {
        onStop();
        onStart();
    }

    //endregion
}
