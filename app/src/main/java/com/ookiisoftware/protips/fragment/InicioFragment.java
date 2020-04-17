package com.ookiisoftware.protips.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ookiisoftware.protips.R;
import com.ookiisoftware.protips.adapter.PostAdapter;
import com.ookiisoftware.protips.auxiliar.Import;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;

public class InicioFragment extends Fragment {

    //region Variáveis

//    private static final String TAG = "InicioFragment";
    private RecyclerView recyclerView;

    private Activity activity;
    private boolean scrollInTop;

    private PostAdapter adapter;
    public SwipeRefreshLayout refreshLayout;

    //endregion

    public InicioFragment(){}
    public InicioFragment (Activity activity) {
        this.activity = activity;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inicio, container, false);
        Init(view);
        return view;
    }

    private void Init(View view) {
        recyclerView = view.findViewById(R.id.recycler);
        refreshLayout = view.findViewById(R.id.swipe_refresh);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Import.activites.getMainActivity().feedUpdate();
            }
        });

        if (Import.getFirebase.isTipster())
            Import.get.tipsters.postes().addAll(Import.getFirebase.getTipster().getPostes().values());

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
                            refreshLayout.setEnabled(true);
                            Import.activites.getMainActivity().viewPager.setPagingEnabled(true);
                            break;
                        }
                    }
                return super.onTouch(v, event);
            }
        };
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    scrollInTop = getCurrentItem() > adapter.getItemCount() -3;
                }
            }
        });
    }

    private int getCurrentItem() {
        if (recyclerView.getLayoutManager() != null)
            return ((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        return adapter.getItemCount();
    }

    public void adapterUpdate() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(adapter.getItemCount()-1);
        }
    }

    public boolean scrollInTop() {
        if (adapter.getItemCount() == 0)
            return true;
        return scrollInTop;
    }

    public void rollToTop() {
        if (adapter.getItemCount() > 0)
            recyclerView.smoothScrollToPosition(adapter.getItemCount()-1);
    }
}
