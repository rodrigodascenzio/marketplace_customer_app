package com.nuppin.User.service;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nuppin.model.Service;
import com.nuppin.model.Company;
import com.nuppin.Util.Util;
import com.nuppin.R;

import java.util.List;

public class RvServicosAdapter extends RecyclerView.Adapter<RvServicosAdapter.RvServicosAdapterViewHolder> {

    private List<Service> service;
    private RvServicosOnClickListener listener;

    RvServicosAdapter(RvServicosOnClickListener handler) {
        listener = handler;
    }

    public class RvServicosAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView productName, preco, desc, duracao;
        final Button imagem;

        RvServicosAdapterViewHolder(View view) {
            super(view);
            productName = view.findViewById(R.id.nome);
            preco = view.findViewById(R.id.preco);
            imagem = view.findViewById(R.id.btn);
            desc = view.findViewById(R.id.desc);
            duracao = view.findViewById(R.id.duracao);
            imagem.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            listener.onClick(service.get(adapterPosition),null);
        }
    }

    @NonNull
    public RvServicosAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.rv_list_service;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new RvServicosAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RvServicosAdapterViewHolder holder, int position) {
        holder.productName.setText(service.get(position).getName());
        holder.preco.setText(Util.formaterPrice(service.get(position).getPrice()));
        holder.desc.setText(service.get(position).getDescription());
        holder.duracao.setText(holder.duracao.getContext().getResources().getString(R.string.duracao_servico, service.get(position).getDuration()));
    }

    @Override
    public int getItemCount() {
        if (null == service)
            return 0;
        return service.size();
    }

    public void setServicos(List<Service> service) {
        this.service = service;
        notifyDataSetChanged();
    }

    public interface RvServicosOnClickListener {
        void onClick(Service service, Company company);
    }

}


