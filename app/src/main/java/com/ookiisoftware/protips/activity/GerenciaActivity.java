package com.ookiisoftware.protips.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.ookiisoftware.protips.R;
import com.ookiisoftware.protips.adapter.CustomViewPager;
import com.ookiisoftware.protips.adapter.SectionsPagerAdapter;
import com.ookiisoftware.protips.auxiliar.Constantes;
import com.ookiisoftware.protips.auxiliar.Import;
import com.ookiisoftware.protips.auxiliar.TerceiroPlanoService;
import com.ookiisoftware.protips.fragment.GInicioFragment;
import com.ookiisoftware.protips.fragment.GSolicitacoesFragment;
import com.ookiisoftware.protips.modelo.Activites;

public class GerenciaActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, ViewPager.OnPageChangeListener, NavigationView.OnNavigationItemSelectedListener {

    //region Variáveis

//    private static final String TAG = "GerenciaActivity";
    private Activity activity;

    private DrawerLayout drawer;
    private CustomViewPager viewPager;
    private BottomNavigationView navView;
    private SectionsPagerAdapter sectionsPagerAdapter;

    public GInicioFragment inicioFragment;
    public GSolicitacoesFragment solicitacoesFragment;

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
                viewPager.setCurrentItem(0);
                break;
            case R.id.nav_menu_tipster:
                viewPager.setCurrentItem(1);
                break;
            case R.id.nav_menu_perfil:
                viewPager.setCurrentItem(2);
                break;
            case R.id.menu_lateral_logout: {
                Import.logOut(activity);
                break;
            }
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
        inicioFragment = new GInicioFragment(activity);
        solicitacoesFragment = new GSolicitacoesFragment(activity);

        Import.activites = new Activites();
        Import.activites.setGerenciaActivity(this);

        //region bundle

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            page = bundle.getInt(Constantes.intent.PAGE_SELECT);
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

        //region NavigationView
        navigationView.setNavigationItemSelectedListener(this);
        navView.setOnNavigationItemSelectedListener(this);
        navView.inflateMenu(R.menu.nav_menu_gerencia);
        //endregion

        //region viewPager
        sectionsPagerAdapter = new SectionsPagerAdapter(
                getSupportFragmentManager(),
                FragmentPagerAdapter.POSITION_UNCHANGED,
                activity, inicioFragment, solicitacoesFragment);

        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.addOnPageChangeListener(this);
        viewPager.setCurrentItem(page);
        //endregion

//        getSolicitacoes();
    }

    //endregion

}
