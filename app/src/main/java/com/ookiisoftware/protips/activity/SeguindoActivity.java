package com.ookiisoftware.protips.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.ookiisoftware.protips.R;
import com.ookiisoftware.protips.adapter.UserAdapter;
import com.ookiisoftware.protips.auxiliar.Const;
import com.ookiisoftware.protips.auxiliar.Import;
import com.ookiisoftware.protips.modelo.User;

import java.util.ArrayList;

public class SeguindoActivity extends AppCompatActivity {

    //region Variáveis
//    private static final String TAG = "SeguindoActivity";

    private Activity activity;
    private UserAdapter adapter;
    private ArrayList<User> data;

    //endregion

    //region Overrides

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seguindo);
        activity = this;
        init();
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
        final RecyclerView recyclerView = findViewById(R.id.recycler);
        final SwipeRefreshLayout refreshLayout = findViewById(R.id.swipeRefresh);
        //endregion

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle(getResources().getString(R.string.titulo_seguindo));
        }

        data = new ArrayList<>(Import.get.seguindo.getAll());
        adapter = new UserAdapter(activity, data) {
            @Override
            public void onClick(View v) {
                int position = recyclerView.getChildAdapterPosition(v);
                User item = adapter.getItem(position);

                Intent intent;
                if (item.getDados().isTipster())
                    intent = new Intent(activity, PerfilTipsterActivity.class);
                else
                    intent = new Intent(activity, PerfilPunterActivity.class);
                intent.putExtra(Const.intent.USER_ID, item.getDados().getId());
                activity.startActivity(intent);
            }
        };

        recyclerView.setAdapter(adapter);

        refreshLayout.setOnRefreshListener(() -> {
            adapterUpdate();
            refreshLayout.setRefreshing(false);
        });
    }

    private void adapterUpdate() {
        if (data != null) {
            data.clear();
            data.addAll(Import.get.seguindo.getAll());
        }

        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    //endregion
}
