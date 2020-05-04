package com.ookiisoftware.protips.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ookiisoftware.protips.R;
import com.ookiisoftware.protips.adapter.PostAdapter;
import com.ookiisoftware.protips.auxiliar.Import;
import com.ookiisoftware.protips.modelo.Post;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;

public class FeedFragment extends Fragment {

    //region Variáveis

//    private static final String TAG = "InicioFragment";
    private RecyclerView recyclerView;
    private TextView novos_postes;

    private Activity activity;
    private boolean scrollInTop;

    private PostAdapter adapter;
    public SwipeRefreshLayout refreshLayout;

    //endregion

    public FeedFragment(){}
    public FeedFragment(Activity activity) {
        this.activity = activity;
    }

    //region Overrides

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        Init(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    //endregion

    //region Métodos

    private void Init(View view) {
        recyclerView = view.findViewById(R.id.recycler);
        refreshLayout = view.findViewById(R.id.swipeRefresh);
        novos_postes = view.findViewById(R.id.tv_novos_postes);

        final boolean isTipster = Import.getFirebase.isTipster();

        if (isTipster) {
            for (Post item : Import.getFirebase.getTipster().getPostes().values())
                if (!Import.get.tipsters.postes().contains(item))
                    Import.get.tipsters.postes().add(item);
            refreshLayout.setEnabled(false);
        } else {
            refreshLayout.setOnRefreshListener(() -> {
                Import.activites.getMainActivity().feedUpdate();
                haveNewPostes(false);
            });
        }

        adapter = new PostAdapter(activity, Import.get.tipsters.postes()) {
            @Override
            @SuppressLint("ClickableViewAccessibility")
            public boolean onTouch(View v, MotionEvent event) {
                ImageViewTouch viewTouch = (ImageViewTouch) v;
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            if (viewTouch.getScale() > 1) {
                                recyclerView.suppressLayout(true);
                                refreshLayout.setEnabled(false);
                                Import.activites.getMainActivity().viewPager.setPagingEnabled(false);
                            }
                            break;
                        }
                        case MotionEvent.ACTION_UP: {
                            recyclerView.suppressLayout(false);
                            refreshLayout.setEnabled(!isTipster);
                            Import.activites.getMainActivity().viewPager.setPagingEnabled(true);
                            break;
                        }
                    }
                return super.onTouch(v, event);
            }
        };
        recyclerView.setAdapter(adapter);

        //region setListener
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    scrollInTop = getCurrentItem() <= 3;
                }
            }
        });

        novos_postes.setOnClickListener(v -> {
            Import.activites.getMainActivity().feedUpdate();
            haveNewPostes(false);
        });

        //endregion
    }

    private int getCurrentItem() {
        if (recyclerView.getLayoutManager() != null)
            return ((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        return adapter.getItemCount();
    }

    public void adapterUpdate() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public boolean scrollInTop() {
        if (adapter.getItemCount() == 0)
            return true;
        return scrollInTop;
    }

    public void rollToTop() {
        recyclerView.smoothScrollToPosition(0);
    }

    public void haveNewPostes(boolean sim) {
        if (sim)
            novos_postes.setVisibility(View.VISIBLE);
        else
            novos_postes.setVisibility(View.GONE);
    }

    //endregion

}
