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
import com.ookiisoftware.protips.modelo.Punter;

import java.util.ArrayList;

public class PunterAdapter extends RecyclerView.Adapter<PunterAdapter.Holder> implements View.OnClickListener, Filterable {

    //region Variáveis
    private Activity activity;
    private ArrayList<Punter> data;
    private ArrayList<Punter> dataFull;
    //endregion

    protected PunterAdapter(Activity activity, ArrayList<Punter> data) {
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
        final Punter item = data.get(position);
        Uri path = Uri.parse(item.getDados().getFoto());

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

    private Filter listFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            dataFull = new ArrayList<>(Import.get.punter.getAllAux());
            ArrayList<Punter> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(dataFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Punter item : dataFull) {
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

    public Punter getItem(int position) {
        return data.get(position);
    }

    static class Holder extends RecyclerView.ViewHolder {
        LinearLayout green_red;
        ImageView foto, online;
//        TextView bom, ruim;
        TextView nome, info;

        Holder(@NonNull View itemView) {
            super(itemView);
            green_red = itemView.findViewById(R.id.ll_green_red);
            foto = itemView.findViewById(R.id.iv_foto);
            nome = itemView.findViewById(R.id.nome);
            online = itemView.findViewById(R.id.online);
            info = itemView.findViewById(R.id.more_info);
//            bom = itemView.findViewById(R.id.bom);
//            ruim = itemView.findViewById(R.id.ruim);

            green_red.setVisibility(View.GONE);
        }
    }
}
