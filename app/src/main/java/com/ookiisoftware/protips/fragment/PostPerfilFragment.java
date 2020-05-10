package com.ookiisoftware.protips.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ookiisoftware.protips.R;
import com.ookiisoftware.protips.activity.PerfilTipsterActivity;
import com.ookiisoftware.protips.adapter.PostPerfilAdapter;
import com.ookiisoftware.protips.auxiliar.OnSwipeListener;
import com.ookiisoftware.protips.modelo.PostPerfil;

import java.util.ArrayList;
import java.util.Collections;

public class PostPerfilFragment extends Fragment {

    //region Variáveis

//    private static final String TAG = "PostPerfilFragment";

    private PostPerfilAdapter adapter;
    private PerfilTipsterActivity activity;
    private ArrayList<PostPerfil> postPerfils;
    private final boolean inList;

    private OnSwipeListener onSwipeListener = new OnSwipeListener() {
        @Override
        public void onTouchUp() {
            if (dialog != null)
                if (dialog.isShowing())
                    dialog.dismiss();
        }
    };

    private RecyclerView recyclerView;
    private Dialog dialog;
    //endregion

    public PostPerfilFragment() {
        inList = false;
    }

    public PostPerfilFragment(PerfilTipsterActivity activity, ArrayList<PostPerfil> postPerfils, boolean inList) {
        this.activity = activity;
        this.inList = inList;
        this.postPerfils = postPerfils;
    }

    //region Overrides

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler, container, false);
        init(view);
        return view;
    }

    //endregion

    //region Metodos

    @SuppressLint("ClickableViewAccessibility")
    private void init(View view) {
        recyclerView = view.findViewById(R.id.recycler);

        adapter = new PostPerfilAdapter(activity, postPerfils, !inList, onSwipeListener) {
            @Override
            public boolean onLongClick(View v) {
                if (!inList) {
                    int position = recyclerView.getChildAdapterPosition(v);
                    PostPerfil item = postPerfils.get(position);
                    popupPhoto(item.getFoto());
                    recyclerView.suppressLayout(true);
                    activity.refreshLayout.setEnabled(false);
                    activity.viewPager.setPagingEnabled(false);
                }
                return super.onLongClick(v);
            }
        };
        recyclerView.setOnTouchListener(onSwipeListener);

        int spanCount;
        if (inList) {
            spanCount = 1;
        } else
            spanCount = 2;

        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);

        recyclerView.setAdapter(adapter);
    }

    private void popupPhoto(String uri) {
        try {
            if (uri == null || uri.isEmpty())
                return;
            dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.popup_foto);
            dialog.setOnDismissListener(dialog -> {
                recyclerView.suppressLayout(false);
                activity.refreshLayout.setEnabled(true);
                activity.viewPager.setPagingEnabled(true);
            });
            if (dialog.getWindow() != null)
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

            ImageView foto = dialog.findViewById(R.id.iv_foto);
            foto.setVisibility(View.VISIBLE);
            Glide.with(activity).load(uri).into(foto);
            foto.requestLayout();
        } catch (Exception ignored) {}
    }

    public void adapterUpdate() {
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    //endregion

}
