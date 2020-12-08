package com.ookiisoftware.protips.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import com.ookiisoftware.protips.adapter.PostAdapter;
import com.ookiisoftware.protips.adapter.PostPerfilAdapter;
import com.ookiisoftware.protips.auxiliar.Import;
import com.ookiisoftware.protips.auxiliar.OnSwipeListener;
import com.ookiisoftware.protips.modelo.Post;
import com.ookiisoftware.protips.modelo.PostPerfil;

import java.util.ArrayList;

public class PostPerfilFragment extends Fragment {

    //region Variáveis

    private static final String TAG = "PostPerfilFragment";

    private PostPerfilAdapter adapterP;
    private PostAdapter adapter;
    private Activity activity;
    private PerfilTipsterActivity activityP;
    private ArrayList<PostPerfil> postPerfils;
    private ArrayList<Post> posts;
    private boolean inList;

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

    public PostPerfilFragment(PerfilTipsterActivity activityP, ArrayList<PostPerfil> postPerfils, boolean inList) {
        this.inList = inList;
        this.activityP = activityP;
        this.postPerfils = postPerfils;
    }

    PostPerfilFragment(Activity activityP, ArrayList<PostPerfil> postPerfils, boolean inList) {
        this.inList = inList;
        this.activity = activityP;
        this.postPerfils = postPerfils;
    }

    PostPerfilFragment(Activity activity, ArrayList<Post> posts) {
        this.activity = activity;
        this.posts = posts;
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

        if (postPerfils != null) {
            adapterP = new PostPerfilAdapter(activityP, postPerfils, !inList, onSwipeListener) {
                @Override
                public boolean onLongClick(View v) {
                    if (!inList) {
                        int position = recyclerView.getChildAdapterPosition(v);
                        PostPerfil item = postPerfils.get(position);
                        popupPhoto(item.getFoto());
                        recyclerView.suppressLayout(true);
                        activityP.refreshLayout.setEnabled(false);
                        activityP.viewPager.setPagingEnabled(false);
                    }
                    return super.onLongClick(v);
                }
            };
            recyclerView.setAdapter(adapterP);
            Import.Alert.d(TAG, "init", "-------------- 1");
        } else if (posts != null) {
            adapter = new PostAdapter(activity, posts){};
            recyclerView.setAdapter(adapter);
            Import.Alert.d(TAG, "init", "-------------- 2");
        }
        Import.Alert.d(TAG, "init", "-------------- 0");

        int spanCount;
        if (inList) {
            spanCount = 1;
        } else
            spanCount = 2;

        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        recyclerView.setOnTouchListener(onSwipeListener);
    }

    private void popupPhoto(String uri) {
        try {
            if (uri == null || uri.isEmpty())
                return;
            dialog = new Dialog(activityP);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.popup_foto);
            dialog.setOnDismissListener(dialog -> {
                recyclerView.suppressLayout(false);
                activityP.refreshLayout.setEnabled(true);
                activityP.viewPager.setPagingEnabled(true);
            });
            if (dialog.getWindow() != null)
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

            ImageView foto = dialog.findViewById(R.id.iv_foto);
            foto.setVisibility(View.VISIBLE);
            Glide.with(activityP).load(uri).into(foto);
            foto.requestLayout();
        } catch (Exception ignored) {}
    }

    public void adapterUpdate() {
        if (adapterP != null)
            adapterP.notifyDataSetChanged();
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    //endregion

}
