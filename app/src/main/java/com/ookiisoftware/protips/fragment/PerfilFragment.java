package com.ookiisoftware.protips.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.ookiisoftware.protips.R;
import com.ookiisoftware.protips.activity.PostActivity;
import com.ookiisoftware.protips.activity.PerfilActivity;
import com.ookiisoftware.protips.activity.SeguidoresActivity;
import com.ookiisoftware.protips.activity.SeguindoActivity;
import com.ookiisoftware.protips.adapter.CustomNestedScrollView;
import com.ookiisoftware.protips.adapter.CustomViewPager;
import com.ookiisoftware.protips.adapter.PostPerfilAdapter;
import com.ookiisoftware.protips.adapter.SectionsPagerAdapter;
import com.ookiisoftware.protips.auxiliar.Const;
import com.ookiisoftware.protips.auxiliar.Criptografia;
import com.ookiisoftware.protips.auxiliar.Import;
import com.ookiisoftware.protips.auxiliar.OnSwipeListener;
import com.ookiisoftware.protips.modelo.Post;
import com.ookiisoftware.protips.modelo.PostPerfil;
import com.ookiisoftware.protips.modelo.UserDados;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.ArrayList;
import java.util.Collections;

import static android.app.Activity.RESULT_OK;

public class PerfilFragment extends Fragment {

    //region Variáveis
    private static final String TAG = "PerfilFragment";

    private AppCompatActivity activity;
    private OnSwipeListener onSwipeListener = new OnSwipeListener() {

        @Override
        public void onTouchUp() {
            if (dialog != null)
                if (dialog.isShowing())
                    dialog.dismiss();
        }
    };

    private String foto_path;

    private PostPerfilFragment postPerfilFragment1;
    private PostPerfilFragment postPerfilFragment2;

    private SectionsPagerAdapter pagerAdapter;
    private PostPerfilAdapter perfilAdapter;
    private ArrayList<PostPerfil> postPerfils;
    private ArrayList<Post> posts;
    private boolean isTipster;

    private Dialog dialog;
    private CustomViewPager viewPager;
    private ImageView btn_notificacao;
    private TextView notification_quant;
//    private RecyclerView recyclerView;
    private CustomNestedScrollView scrollView;

    //endregion

    public PerfilFragment(){}
    public PerfilFragment(AppCompatActivity activity) {
        this.activity = activity;
    }

    //region Overrides

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);
        Init(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateNotificacao();
        adapterUpdate();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (dialog != null) {
            final ImageView foto = dialog.findViewById(R.id.iv_foto);
//            final ConstraintLayout constraintLayout = dialog.findViewById(R.id.constraint);

            if(requestCode == Const.permissions.STORANGE && resultCode == RESULT_OK) {
                Import.Alert.d(TAG, "onActivityResult", "resultCode == RESULT_OK");
                if (data == null || data.getData() == null) {
                    dialog.dismiss();
                } else {
                    Uri uri = data.getData();
                    foto_path = uri.toString();
                    Glide.with(activity).load(uri).into(foto);
                }
            }
            else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK && result != null && result.getUri() != null) {
                    foto_path = result.getUri().toString();
                    Glide.with(activity).asBitmap().load(foto_path).into(foto);
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    if (result != null)
                        Import.Alert.e(TAG, "onActivityResult", result.getError().getMessage());
                    Import.Alert.toast(activity, getResources().getString(R.string.erro_foto_salvar));
                }
            } else {
                if (dialog != null)
                    dialog.dismiss();
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    //endregion

    //region Métodos

    @SuppressLint("ClickableViewAccessibility")
    private void Init(View view) {
        //region findViewById
        ImageView foto_user = view.findViewById(R.id.iv_foto);
        TextView txt_tipname = view.findViewById(R.id.tv_tipname);
        TextView txt_nome = view.findViewById(R.id.tv_nome);
        TextView txt_email = view.findViewById(R.id.tv_email);
        TextView posts_no_perfil = view.findViewById(R.id.tv_posts_no_perfil);
        TextView btn_seguidores = view.findViewById(R.id.tv_seguidores);

        ImageView btn_newPost = view.findViewById(R.id.iv_new_post);
        ImageView btn_newPostPerfil = view.findViewById(R.id.iv_new_post_perfil);
        ImageView btn_planilha = view.findViewById(R.id.iv_planilha);
        ImageView btn_3_perfil_fragment = view.findViewById(R.id.btn_3_perfil_fragment);

//        recyclerView = view.findViewById(R.id.recycler);
        scrollView = view.findViewById(R.id.scrollView);
        btn_notificacao = view.findViewById(R.id.iv_new_punter);
        notification_quant = view.findViewById(R.id.tv_notification_quant);

        TabLayout tabs = view.findViewById(R.id.tabs);

        viewPager = view.findViewById(R.id.viewPager);
        //endregion

        //region setValues

        isTipster = Import.getFirebase.isTipster();
        UserDados user = Import.getFirebase.getTipster().getDados();

        if (user == null)
            return;

        txt_nome.setText(user.getNome());
        txt_email.setText(user.getEmail());
        txt_tipname.setText(user.getTipname());
        Glide.with(activity).load(user.getFoto()).into(foto_user);

        if (isTipster) {
            postPerfils = new ArrayList<>(Import.getFirebase.getTipster().getPost_perfil().values());
            posts = new ArrayList<>(Import.getFirebase.getTipster().getPostes().values());

            Collections.sort(postPerfils, new PostPerfil.orderByDate());
            /*perfilAdapter = new PostPerfilAdapter(activity, postPerfils, true, onSwipeListener) {
                @Override
                public void onClick(View v) {
                    int position = recyclerView.getChildAdapterPosition(v);

                    if (perfilAdapter.isMyPost(position)) {
                        Toolbar toolbar = v.findViewById(R.id.toolbar);
                        if (toolbar.getVisibility() == View.VISIBLE)
                            toolbar.setVisibility(View.GONE);
                        else
                            toolbar.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public boolean onLongClick(View v) {
                    int position = recyclerView.getChildAdapterPosition(v);
                    PostPerfil item = postPerfils.get(position);
                    popupPhoto(item.getFoto());
                    scrollView.setScrollingEnabled(false);
                    recyclerView.suppressLayout(true);

                    Import.activites.getMainActivity().setPagingEnabled(false);
                    return super.onLongClick(v);
                }
            };*/
//            recyclerView.setAdapter(perfilAdapter);
//            recyclerView.setOnTouchListener(onSwipeListener);

            btn_newPostPerfil.setVisibility(View.VISIBLE);
//            recyclerView.setVisibility(View.VISIBLE);
            btn_seguidores.setText(activity.getResources().getString(R.string.titulo_meus_filiados));
            posts_no_perfil.setVisibility(View.VISIBLE);

            //region viewPager
//            if (postPerfilFragment1 == null)
                postPerfilFragment1 = new PostPerfilFragment(activity, postPerfils, true);
//            if (postPerfilFragment2 == null)
                postPerfilFragment2 = new PostPerfilFragment(activity, posts);

            Import.Alert.d(TAG, "init", "" + viewPager.getLayoutParams().height);

            pagerAdapter = new SectionsPagerAdapter(activity.getSupportFragmentManager(), activity, postPerfilFragment1, postPerfilFragment2);
            viewPager.setAdapter(pagerAdapter);

            //region Tabs
            tabs.setSelectedTabIndicatorColor(ContextCompat.getColor(activity, R.color.colorAccent));
            tabs.setTabTextColors(activity.getResources().getColor(R.color.brancoDark), activity.getResources().getColor(R.color.colorAccent));
            tabs.setupWithViewPager(viewPager);
            //endregion

            //endregion

        } else {
            posts_no_perfil.setVisibility(View.GONE);
            btn_seguidores.setText(activity.getResources().getString(R.string.titulo_meus_tipsters));
            btn_newPost.setVisibility(View.GONE);
            btn_planilha.setVisibility(View.GONE);
//            recyclerView.setVisibility(View.GONE);
            btn_newPostPerfil.setVisibility(View.GONE);
        }

        //endregion

        //region setListener
        foto_user.setOnClickListener(view1 -> {
            Intent intent = new Intent(activity, PerfilActivity.class);
            startActivity(intent);
        });

        if (isTipster) {
            btn_newPost.setOnClickListener(v -> {
                Intent intent = new Intent(activity, PostActivity.class);
                startActivity(intent);
            });

            btn_newPostPerfil.setOnClickListener(v -> postPerfil());
        }
        btn_notificacao.setOnClickListener(v -> {
            removeNotification();
            Import.activites.getMainActivity().setPagePosition(Const.classes.fragments.pagerPosition.NOTIFICATIONS);
        });
        btn_3_perfil_fragment.setOnClickListener(v -> {

        });
        btn_seguidores.setOnClickListener(v -> {
            Intent intent;
            if (isTipster)
                intent = new Intent(activity, SeguidoresActivity.class);
            else
                intent = new Intent(activity, SeguindoActivity.class);
            activity.startActivity(intent);
        });

        //endregion
    }

    public void updateNotificacao() {
        int quant = Import.get.solicitacao.getAll().size();
        if (quant > 0) {
            btn_notificacao.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_sms_2));
            String quantS = "" + quant;
            notification_quant.setText(quantS);
            notification_quant.setVisibility(View.VISIBLE);
        } else {
            notification_quant.setVisibility(View.GONE);
            btn_notificacao.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_sms));
        }
    }

    public void adapterUpdate() {
        if (perfilAdapter != null) {
            if (postPerfils != null) {
                postPerfils.clear();
                postPerfils.addAll(Import.getFirebase.getTipster().getPost_perfil().values());
            }
            perfilAdapter.notifyDataSetChanged();
        }
    }

    private void removeNotification() {
        if (isTipster)
            Import.notificacaoCancel(activity, Const.notification.id.NOVO_PUNTER_PENDENTE);
        else
            Import.notificacaoCancel(activity, Const.notification.id.NOVO_PUNTER_ACEITO);
    }

    private void postPerfil() {
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_post_perfil);
        dialog.setCancelable(false);
//        if (dialog.getWindow() != null)
//            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final EditText titulo = dialog.findViewById(R.id.et_titulo);
        final EditText texto = dialog.findViewById(R.id.et_texto);
        final ImageView foto = dialog.findViewById(R.id.iv_foto);
        final RelativeLayout rl = dialog.findViewById(R.id.rl_1);
        final Button postar = dialog.findViewById(R.id.ok_button);
        final Button cancel = dialog.findViewById(R.id.cancel_button);
        Import.abrirCropView(activity, this, 0);
        rl.setVisibility(View.VISIBLE);

        foto.setOnClickListener(v -> Import.abrirCropView(activity, this, 0));
        cancel.setOnClickListener(v -> dialog.dismiss());
        postar.setOnClickListener(v -> {
            PostPerfil post = new PostPerfil();
            post.setTitulo(titulo.getText().toString());
            post.setTexto(texto.getText().toString());
            post.setData(Import.get.Data());
            post.setId(Criptografia.criptografar(post.getData()));
            post.setId_tipster(Import.getFirebase.getId());
            post.setFoto(foto_path);

            if (post.getFoto() == null || post.getFoto().isEmpty()) {
                return;
            }
            post.salvar(activity, null, true);
            postPerfils.add(post);
            Collections.sort(postPerfils, new PostPerfil.orderByDate());
            adapterUpdate();
            dialog.dismiss();
        });
        dialog.setOnDismissListener(dialog -> foto_path = null);

        dialog.show();
    }

    private void popupPhoto(String uri) {
        try {
            if (uri == null || uri.isEmpty())
                return;
            dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.popup_foto);
            dialog.setOnDismissListener(dialog -> {
                scrollView.setScrollingEnabled(true);
//                recyclerView.suppressLayout(false);
                Import.activites.getMainActivity().setPagingEnabled(true);
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

    //endregion
}