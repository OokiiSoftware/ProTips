package com.ookiisoftware.protips.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.ookiisoftware.protips.fragment.FeedFragment;
import com.ookiisoftware.protips.fragment.NotificationsFragment;
import com.ookiisoftware.protips.fragment.PerfilFragment;
import com.ookiisoftware.protips.fragment.TipstersFragment;
import com.ookiisoftware.protips.modelo.Activites;
import com.ookiisoftware.protips.modelo.Post;
import com.ookiisoftware.protips.modelo.Punter;
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
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, ViewPager.OnPageChangeListener, NavigationView.OnNavigationItemSelectedListener {

    //region Variáveis

    private static final String TAG = "MainActivity";
    private Activity activity;
    private String meuId;
//    private boolean isTipster;

    public CustomViewPager viewPager;
    private DrawerLayout drawer;
    private BottomNavigationView navView;

    // Itens do Menu
    private MenuItem menu_pesquisa;

    private SectionsPagerAdapter sectionsPagerAdapter;
    private static final int FRAGMENT_FEED = 0;
    private static final int FRAGMENT_TIPSTER = 1;
    private static final int FRAGMENT_PERFIL = 2;
    private boolean inPrimeiroPlano;

    private DatabaseReference refAllTipsters;
    private ChildEventListener eventAllTipsters;

    // Titulo personalizado pra ActionBar
    private AppCompatTextView action_bar_titulo_1, action_bar_titulo_2;

    public FeedFragment feedFragment;
    public PerfilFragment perfilFragment;
    public TipstersFragment tipstersFragment;
    public NotificationsFragment notificationsFragment;

    //endregion

    //region Overrides

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;
        startService(new Intent(activity, SegundoPlanoService.class));

        Import.Alert.msg(TAG, "Timezone1", TimeZone.getDefault().getID());
        Import.Alert.msg(TAG, "Timezone2", TimeZone.getDefault().getDisplayName());

        Init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        inPrimeiroPlano = true;
//        if (isTipster)
//            getMyPunters();
//        else
            refAllTipsters.addChildEventListener(eventAllTipsters);
    }

    @Override
    protected void onStop() {
        super.onStop();
        inPrimeiroPlano = false;
//        if (!isTipster)
            refAllTipsters.removeEventListener(eventAllTipsters);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
//        menu_new_feed = menu.findItem(R.id.menu_postar_feed);
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
                feedFragment.refreshLayout.setEnabled(newText.isEmpty());
//                if (isTipster)
//                    tipstersFragment.getPunterAdapter().getFilter().filter(newText);
//                else
                    tipstersFragment.getTipsterAdapter().getFilter().filter(newText);
                return false;
            }
        });

        SwithMenu(0);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START, true);
                break;
            case R.id.menu_verificar_atualizacao:
                verificar_atualizacao();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_menu_feed:
                viewPager.setCurrentItem(FRAGMENT_FEED);
                break;
            case R.id.nav_menu_tipster:
                viewPager.setCurrentItem(FRAGMENT_TIPSTER);
                break;
            case R.id.nav_menu_perfil:
                viewPager.setCurrentItem(FRAGMENT_PERFIL);
                break;
            case R.id.menu_lateral_batepapo: {
                Intent intent = new Intent(activity, BatepapoActivity.class);
                startActivity(intent);
                drawer.closeDrawer(GravityCompat.START, true);
                break;
            }
            case R.id.menu_lateral_logout: {
                Import.logOut(activity);
                break;
            }
        }

        return true;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
        CharSequence title = sectionsPagerAdapter.getPageTitle(position);
        if (title != null)
            Import.organizarTituloTollbar(activity, action_bar_titulo_1, action_bar_titulo_2, title);

        switch (position) {
            case 0:
                navView.setSelectedItemId(R.id.nav_menu_feed);
                break;
            case 1:
                navView.setSelectedItemId(R.id.nav_menu_tipster);
                break;
            case 2:
                navView.setSelectedItemId(R.id.nav_menu_perfil);
                break;
        }
        SwithMenu(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {}

    @Override
    public void onBackPressed() {
        if(viewPager.getCurrentItem() != FRAGMENT_FEED)
            viewPager.setCurrentItem(FRAGMENT_FEED);
        else {
            if (feedFragment.scrollInTop())
                super.onBackPressed();
            else
                feedFragment.rollToTop();
        }
    }

    //endregion

    //region Métodos

    private void Init() {
        //region findViewById
        Toolbar toolbar = findViewById(R.id.toolbar);
        NavigationView navigationView = findViewById(R.id.navigationView);
        viewPager = findViewById(R.id.viewPager);
        navView = findViewById(R.id.bottonNavigationView);
        drawer = findViewById(R.id.drawer);
        //endregion

        meuId = Import.getFirebase.getId();
        int pageSelect = 0;
        try {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                boolean primeiroLogin = bundle.getBoolean(Constantes.intent.PRIMEIRO_LOGIN);
                pageSelect = bundle.getInt(Constantes.intent.PAGE_SELECT);
                if (primeiroLogin) {
                    Intent intent = new Intent(activity, PerfilActivity.class);
                    intent.putExtra(Constantes.intent.PRIMEIRO_LOGIN, true);
                    startActivity(intent);
                }
            }

            feedFragment = new FeedFragment(activity);
            tipstersFragment = new TipstersFragment(activity);
            notificationsFragment = new NotificationsFragment(activity);
            perfilFragment = new PerfilFragment(activity);

//            isTipster = Import.getFirebase.isTipster();
            Import.activites = new Activites();
            Import.activites.setMainActivity(this);
        } catch (Exception ex) {
            Import.Alert.erro(TAG, ex);
            Import.Alert.toast(activity, getResources().getString(R.string.erro_init_main_activity));
            Import.irProLogin(activity);
        }

        //region Toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_menu));
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setCustomView(R.layout.custom_action_bar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        action_bar_titulo_1 = findViewById(R.id.action_bar_titulo_1);
        action_bar_titulo_2 = findViewById(R.id.action_bar_titulo_2);
        //endregion

        //region NavigationView
        navigationView.setNavigationItemSelectedListener(this);
        navView.setOnNavigationItemSelectedListener(this);
        navView.inflateMenu(R.menu.nav_menu_main);
        //endregion

        //region viewPager
        sectionsPagerAdapter = new SectionsPagerAdapter(
                getSupportFragmentManager(),
                FragmentPagerAdapter.POSITION_UNCHANGED,
                activity, feedFragment, tipstersFragment, perfilFragment, notificationsFragment);

        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.addOnPageChangeListener(this);
        viewPager.setCurrentItem(pageSelect);
        //endregion

        getAllTipsters();
    }

    private void SwithMenu(int position) {
        /*switch (position) {
            case 0:
            case 1:
            case 2:
            case 3:
                break;
        }*/
        if (menu_pesquisa != null)
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
                    if (item != null) {
                        if (item.getDados().isBloqueado())
                            return;
                        if (item.getDados().getId().equals(meuId))
                            return;
                        Tipster item_2 = Import.get.tipsters.findTipster(item.getDados().getId());
                        if (item_2 == null) {
                            Import.get.tipsters.getAll().add(item);
                            Import.get.tipsters.getAllAux().add(item);
                        } else {
                            Import.get.tipsters.getAll().set(Import.get.tipsters.getAll().indexOf(item_2), item);
                            Import.get.tipsters.getAllAux().set(Import.get.tipsters.getAllAux().indexOf(item_2), item);
                        }
                        if (Import.getFirebase.getPunter().getTipsters().containsValue(item.getDados().getId())) {
                            for (Post post : item.getPostes().values()) {
                                if (Import.get.tipsters.findPost(post.getId()) == null)
                                    Import.get.tipsters.postes().add(post);
                            }
                        } else {
                            for (Post post : item.getPostes().values())
                                if (post.isPublico() && Import.get.tipsters.findPost(post.getId()) == null)
                                    Import.get.tipsters.postes().add(post);
                        }
                        tipstersFragment.adapterUpdate();
                        feedFragment.adapterUpdate();
                    }
                } catch (Exception ex) {
                    Import.Alert.erro(TAG, ex);
                    Import.Alert.msg(TAG, "onChildAdded", dataSnapshot.getKey());
                }
                try {
                    feedFragment.refreshLayout.setRefreshing(false);
                    tipstersFragment.refreshLayout.setRefreshing(false);
                } catch (Exception ignored) {}
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getKey();
                Import.get.tipsters.getAll().remove(Import.get.tipsters.findTipster(value));
                Import.get.tipsters.getAllAux().remove(Import.get.tipsters.findTipster(value));
                Import.Alert.msg(TAG, "onChildRemoved", value);
                tipstersFragment.adapterUpdate();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
    }

    private void getMyPunters() {
        for (String s : Import.getFirebase.getTipster().getPunters().values()) {
            DatabaseReference ref =  Import.getFirebase.getReference()
                    .child(Constantes.firebase.child.USUARIO)
                    .child(Constantes.firebase.child.PUNTERS)
                    .child(s);

            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Punter item = dataSnapshot.getValue(Punter.class);
                    if (item != null) {
                        Punter item2 = Import.get.punter.find(item.getDados().getId());
                        if (item2 == null) {
                            Import.get.punter.getAll().add(item);
                            Import.get.punter.getAllAux().add(item);
                        } else {
                            Import.get.punter.getAll().set(Import.get.punter.getAll().indexOf(item2), item);
                            Import.get.punter.getAllAux().set(Import.get.punter.getAllAux().indexOf(item2), item);
                        }
                    }
                    try {
                        tipstersFragment.adapterUpdate();
                        tipstersFragment.refreshLayout.setRefreshing(false);
                    } catch (Exception e) {
                        Import.Alert.erro(TAG, e);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {}
            });
        }
    }

    public void feedUpdate() {
        onStop();
        onStart();
    }

    public boolean isInPrimeiroPlano() {
        return inPrimeiroPlano;
    }

    private void verificar_atualizacao() {
        final Dialog dialog = new Dialog(activity);
        dialog.setTitle(getResources().getString(R.string.verificando_atualizacao));
        final ProgressBar progressBar = new ProgressBar(activity);
        dialog.setContentView(progressBar);

        final DatabaseReference ref = Import.getFirebase.getReference()
                .child(Constantes.firebase.child.VERSAO);

        final ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Long> values = new ArrayList<>();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Long item = data.getValue(Long.class);
                    if (item != null) {
                        values.add(item);
                    }
                }
                if (values.size() > 0) {
                    final long ultima = values.get(values.size() -1);
                    if (ultima > Constantes.APP_VERSAO) {
                        dialog.dismiss();
                        final AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
                        dialog.setTitle(getResources().getString(R.string.atualizacao_disponivel));
                        dialog.setPositiveButton(getResources().getString(R.string.baixar), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String appName = "protips_" + ultima + ".apk";
                                Import.Alert.toast(activity, "aguarde");
                                Import.Alert.msg(TAG, "verificar_atualizacao", appName);
                                Import.getFirebase.getStorage()
                                        .child(Constantes.firebase.child.APP)
                                        .child(appName)
                                        .getDownloadUrl()
                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                                startActivity(intent);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Import.Alert.erro(TAG, "verificar_atualizacao", e);
                                                atualizacao_erro();
                                            }
                                        });
                            }
                        });
                        dialog.show();
                    } else {
                        sem_atualizacao();
                    }
                } else {
                    sem_atualizacao();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}

            private void atualizacao_erro() {
                Import.Alert.toast(activity, "erro ao baixar");
            }

            private void sem_atualizacao() {
                Import.Alert.toast(activity, "sem atualização");
                dialog.dismiss();
            }
        };

        ref.addListenerForSingleValueEvent(eventListener);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                ref.removeEventListener(eventListener);
            }
        });
        dialog.show();
    }

    //endregion
}
