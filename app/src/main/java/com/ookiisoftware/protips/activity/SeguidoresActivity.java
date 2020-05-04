package com.ookiisoftware.protips.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.ookiisoftware.protips.R;
import com.ookiisoftware.protips.adapter.PunterAdapter;
import com.ookiisoftware.protips.adapter.TipsterAdapter;
import com.ookiisoftware.protips.auxiliar.Constantes;
import com.ookiisoftware.protips.auxiliar.Import;
import com.ookiisoftware.protips.modelo.Punter;
import com.ookiisoftware.protips.modelo.Tipster;

import java.util.ArrayList;

public class SeguidoresActivity extends AppCompatActivity {

    //region Variáveis
//    private static final String TAG = "SeguidoresActivity";

    private Activity activity;
    private ArrayList<Punter> punters;
    private ArrayList<Tipster> tipsters;
    private TipsterAdapter tipsterAdapter;
    private PunterAdapter punterAdapter;

    private TextView tv_punters;
    private TextView tv_tipsters;
    private RecyclerView recyclerPunters;
    private RecyclerView recyclerTipsters;

    //endregion

    //region Overrides

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seguidores);
        activity = this;
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapterUpdate();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    //endregion

    //region Métodos

    private void init() {
        //region findViewById
        final Toolbar toolbar = findViewById(R.id.toolbar);
        recyclerPunters = findViewById(R.id.recyclerPunters);
        recyclerTipsters = findViewById(R.id.recyclerTipsters);
        tv_punters = findViewById(R.id.tv_punters);
        tv_tipsters = findViewById(R.id.tv_tipsters);
        //endregion

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle(getResources().getString(R.string.titulo_meus_seguidores));
        }

        punters = Import.get.punter.getAll();
        tipsters = Import.get.tipsters.getAll();

        punterAdapter = new PunterAdapter(activity, punters) {
            @Override
            public void onClick(View v) {
                int position = recyclerPunters.getChildAdapterPosition(v);
                Punter item = punterAdapter.getItem(position);

                Intent intent = new Intent(activity, PerfilPunterActivity.class);
                intent.putExtra(Constantes.intent.USER_ID, item.getDados().getId());
                activity.startActivity(intent);
            }
        };
        tipsterAdapter = new TipsterAdapter(activity, tipsters) {
            @Override
            public void onClick(View v) {
                int position = recyclerTipsters.getChildAdapterPosition(v);
                Tipster item = tipsterAdapter.getItem(position);

                Intent intent = new Intent(activity, PerfilTipsterActivity.class);
                intent.putExtra(Constantes.intent.USER_ID, item.getDados().getId());
                activity.startActivity(intent);
            }
        };

        recyclerPunters.setAdapter(punterAdapter);
        recyclerTipsters.setAdapter(tipsterAdapter);
    }

    public void adapterUpdate() {
        if (punters.size() == 0) {
            tv_punters.setVisibility(View.GONE);
            recyclerPunters.setVisibility(View.GONE);
        } else {
            tv_punters.setVisibility(View.VISIBLE);
            recyclerPunters.setVisibility(View.VISIBLE);
        }
        if (tipsters.size() == 0) {
            tv_tipsters.setVisibility(View.GONE);
            recyclerTipsters.setVisibility(View.GONE);
        } else {
            tv_tipsters.setVisibility(View.VISIBLE);
            recyclerTipsters.setVisibility(View.VISIBLE);
        }

        if (tipsterAdapter != null)
            tipsterAdapter.notifyDataSetChanged();
        if (punterAdapter != null)
            punterAdapter.notifyDataSetChanged();
    }

    //endregion
}
