package com.ookiisoftware.protips.adapter;

import android.app.Activity;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ookiisoftware.protips.R;
import com.ookiisoftware.protips.auxiliar.Import;
import com.ookiisoftware.protips.modelo.Post;
import com.ookiisoftware.protips.modelo.User;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.Holder> implements View.OnClickListener, Filterable {

    //region Variáveis
//    private final static String TAG = "TipsterAdapter";
    private Activity activity;
    private ArrayList<User> data;
    private ArrayList<User> dataFull;
    //endregion

    protected UserAdapter(Activity activity, ArrayList<User> data) {
        this.activity = activity;
        this.data = data;
        dataFull = new ArrayList<>();
    }

    //region Overrides

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_tipster, parent, false);
        view.setOnClickListener(this);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        final User item = data.get(position);
        Uri path = Uri.parse(item.getDados().getFoto());
        String post_quant = item.getPostes().size() + "";

        if (item.getDados().isTipster()) {
            holder.tipster_container.setVisibility(View.VISIBLE);
            int bom = 0;
            int ruim = 0;
            for (Post i : item.getPostes().values()) {
                bom += i.getBom().size();
                ruim += i.getRuim().size();
            }
            String sBom = "" + bom;
            String sRuim = "" + ruim;

            holder.bom.setText(sBom);
            holder.ruim.setText(sRuim);
            holder.post_quant.setText(post_quant);
        } else {
            holder.tipster_container.setVisibility(View.GONE);
        }
        holder.nome.setText(item.getDados().getNome());
        holder.info.setText(item.getDados().getInfo());

        Glide.with(activity).load(path).into(holder.foto);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onClick(View v) {}

    @Override
    public Filter getFilter() {
        return listFilter;
    }

    //endregion

    public User getItem(int position) {
        return data.get(position);
    }

    private Filter listFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            dataFull = new ArrayList<>(Import.get.tipsters.getAllAux());
            ArrayList<User> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(dataFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (User item : dataFull) {
                    if (item.getDados().getNome().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            data.clear();
            data.addAll((ArrayList) results.values);
            notifyDataSetChanged();
        }
    };

    static class Holder extends RecyclerView.ViewHolder {
        LinearLayout tipster_container;
        ImageView foto, online;
        TextView bom, ruim;
        TextView nome, info;
        TextView post_quant;

        Holder(@NonNull View itemView) {
            super(itemView);
            tipster_container = itemView.findViewById(R.id.tipster_container);
            foto = itemView.findViewById(R.id.iv_foto);
            nome = itemView.findViewById(R.id.nome);
            online = itemView.findViewById(R.id.online);
            info = itemView.findViewById(R.id.more_info);
            bom = itemView.findViewById(R.id.tv_bom);
            ruim = itemView.findViewById(R.id.tv_ruim);
            post_quant = itemView.findViewById(R.id.post_quant);
        }
    }
}
