package com.ookiisoftware.protips.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.ookiisoftware.protips.R;
import com.ookiisoftware.protips.adapter.CustomViewPager;
import com.ookiisoftware.protips.adapter.SectionsPagerAdapter;
import com.ookiisoftware.protips.auxiliar.Const;
import com.ookiisoftware.protips.auxiliar.Import;
import com.ookiisoftware.protips.auxiliar.notification.MyNotificationManager;
import com.ookiisoftware.protips.auxiliar.notification.TerceiroPlanoService;
import com.ookiisoftware.protips.fragment.NotificationsFragment;
import com.ookiisoftware.protips.fragment.TipstersFragment;
import com.ookiisoftware.protips.modelo.Activites;
import com.ookiisoftware.protips.modelo.User;

public class GerenciaActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, ViewPager.OnPageChangeListener, NavigationView.OnNavigationItemSelectedListener {

    //region Variáveis

    private static final String TAG = "GerenciaActivity";
    private Activity activity;

    private DrawerLayout drawer;
    private CustomViewPager viewPager;
    private BottomNavigationView navView;
    private SectionsPagerAdapter sectionsPagerAdapter;

    public TipstersFragment tipstersFragment;
    public NotificationsFragment notificationsFragment;
//    public GInicioFragment inicioFragment;
//    public GSolicitacoesFragment solicitacoesFragment;

    private DatabaseReference refAllTipsters;
    private ChildEventListener eventAllTipsters;

    // Titulo personalizado pra ActionBar
    private AppCompatTextView action_bar_titulo_1, action_bar_titulo_2;

    //endregion

    //region Overrides

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;
        startService(new Intent(activity, TerceiroPlanoService.class));
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        refAllTipsters.addChildEventListener(eventAllTipsters);
    }

    @Override
    protected void onStop() {
        super.onStop();
        refAllTipsters.removeEventListener(eventAllTipsters);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawer.openDrawer(GravityCompat.START, true);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_menu_feed:
                viewPager.setCurrentItem(Const.classes.fragments.pagerPosition.INICIO);
                break;
            case R.id.nav_menu_tipster:
                viewPager.setCurrentItem(Const.classes.fragments.pagerPosition.TIPSTER_SOLICITACAO);
                break;
            case R.id.nav_menu_perfil:
                viewPager.setCurrentItem(2);
                break;
            case R.id.menu_lateral_logout: {
                Import.logOut(activity);
                break;
            }
            case R.id.menu_verificar_atualizacao:
                Import.verificar_atualizacao(activity);
                break;
        }
        return false;
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
    }

    @Override
    public void onPageScrollStateChanged(int state) {}

    //endregion

    //region Nétodos

    private void init() {
        //region findViewById
        Toolbar toolbar = findViewById(R.id.toolbar);
        NavigationView navigationView = findViewById(R.id.navigationView);
        viewPager = findViewById(R.id.viewPager);
        navView = findViewById(R.id.bottonNavigationView);
        drawer = findViewById(R.id.drawer);
        //endregion

        int page = 0;
        tipstersFragment = new TipstersFragment(activity, true);
        notificationsFragment = new NotificationsFragment(activity, true);
//        inicioFragment = new GInicioFragment(activity);
//        solicitacoesFragment = new GSolicitacoesFragment(activity);

        Import.activites = new Activites();
        Import.activites.setGerenciaActivity(this);

        //region bundle

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            page = bundle.getInt(Const.intent.PAGE_SELECT);
        }

        //endregion

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

        MyNotificationManager.criarChannelNotification(activity);

        //region NavigationView
        navigationView.setNavigationItemSelectedListener(this);
        navView.setOnNavigationItemSelectedListener(this);
        navView.inflateMenu(R.menu.nav_menu_gerencia);
        //endregion

        //region viewPager
        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),
                activity, tipstersFragment, notificationsFragment);

        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.addOnPageChangeListener(this);
        viewPager.setCurrentItem(page);
        //endregion

        getAllTipsters();
    }

    private void getAllTipsters() {
        refAllTipsters = Import.getFirebase.getReference()
                .child(Const.firebase.child.USUARIO);

        eventAllTipsters = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                try {
                    User item = dataSnapshot.getValue(User.class);
                    if (item == null)
                        return;
                    if (item.getDados().isBloqueado() && item.getDados().isTipster())
                        Import.get.solicitacao.add(item);

                    if (item.getDados().isTipster()) {
                        Import.get.tipsters.add(item);
                    }

                    tipstersFragment.adapterUpdate();
                    notificationsFragment.adapterUpdate();
                } catch (Exception ex) {
                    Import.Alert.e(TAG, ex);
                    Import.Alert.d(TAG, "onChildAdded", dataSnapshot.getKey());
                }
                try {
                    tipstersFragment.refreshLayout.setRefreshing(false);
                } catch (Exception ignored) {}
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getKey();

                Import.get.tipsters.remove(key);
                Import.Alert.d(TAG, "onChildRemoved", key);
                tipstersFragment.adapterUpdate();
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
