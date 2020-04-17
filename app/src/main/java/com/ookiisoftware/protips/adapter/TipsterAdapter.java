package com.ookiisoftware.protips.adapter;

import android.app.Activity;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ookiisoftware.protips.R;
import com.ookiisoftware.protips.auxiliar.Import;
import com.ookiisoftware.protips.modelo.Post;
import com.ookiisoftware.protips.modelo.Tipster;

import java.util.ArrayList;

public class TipsterAdapter extends RecyclerView.Adapter<TipsterAdapter.Holder> implements View.OnClickListener, Filterable {

    //region Variáveis
//    private final static String TAG = "TipsterAdapter";
    private Activity activity;
    private ArrayList<Tipster> list;
    private ArrayList<Tipster> listFull;
    //endregion

    public TipsterAdapter(Activity activity, ArrayList<Tipster> list) {
        this.activity = activity;
        this.list = list;
        listFull = new ArrayList<>();
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
        final Tipster item = list.get(position);
        Uri path = Uri.parse(item.getDados().getFoto());
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
        holder.nome.setText(item.getDados().getNome());
        holder.info.setText(item.getDados().getInfo());

        Glide.with(activity).load(path).into(holder.foto);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onClick(View v) {}

    @Override
    public Filter getFilter() {
        return listFilter;
    }

    //endregion

    private Filter listFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            listFull = new ArrayList<>(Import.get.tipsters.getAllAux());
            ArrayList<Tipster> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(listFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Tipster item : listFull) {
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
            list.clear();
            list.addAll((ArrayList) results.values);
            notifyDataSetChanged();
        }
    };

    static class Holder extends RecyclerView.ViewHolder {
        ImageView foto, online;
        TextView bom, ruim;
        TextView nome, info;

        Holder(@NonNull View itemView) {
            super(itemView);
            foto = itemView.findViewById(R.id.foto);
            nome = itemView.findViewById(R.id.nome);
            online = itemView.findViewById(R.id.online);
            info = itemView.findViewById(R.id.more_info);
            bom = itemView.findViewById(R.id.bom);
            ruim = itemView.findViewById(R.id.ruim);
        }
    }
}
