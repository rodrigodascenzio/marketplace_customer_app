package com.nuppin.User.pedido;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nuppin.model.OrderItem;
import com.nuppin.Util.Util;
import com.nuppin.R;

import java.util.List;

public class RvPedidoDetalheAdapter extends RecyclerView.Adapter<RvPedidoDetalheAdapter.RvPedidoDetalheAdapterViewHolder> {

    private List<OrderItem> orderItems;

     RvPedidoDetalheAdapter() {
    }

    class RvPedidoDetalheAdapterViewHolder extends RecyclerView.ViewHolder{

        final TextView quantidade, nome, preco, prodObs, tamanho;
        private RecyclerView recyclerView;
        private OrderExtrasAdapter adapter;
        private Context ctx;

        RvPedidoDetalheAdapterViewHolder(View view) {
            super(view);
            ctx = view.getContext();
            quantidade = view.findViewById(R.id.quantidade);
            nome = view.findViewById(R.id.nome);
            preco = view.findViewById(R.id.preco);
            prodObs = view.findViewById(R.id.obs);
            tamanho = view.findViewById(R.id.tamanho);
            recyclerView = view.findViewById(R.id.recycler);
        }
    }

    @NonNull
    public RvPedidoDetalheAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.rv_request_detail;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new RvPedidoDetalheAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RvPedidoDetalheAdapterViewHolder holder, int position) {
        holder.prodObs.setVisibility(View.GONE);
        holder.recyclerView.setVisibility(View.GONE);
        holder.tamanho.setVisibility(View.GONE);

        if (orderItems.get(position).getExtra() != null && orderItems.get(position).getExtra().size() > 0) {
            holder.recyclerView.setVisibility(View.VISIBLE);
        }

        if (orderItems.get(position).getSize_name() != null && !orderItems.get(position).getSize_name().isEmpty()) {
            holder.tamanho.setVisibility(View.VISIBLE);
            holder.tamanho.setText("Tamanho: "+orderItems.get(position).getSize_name());
        }

        holder.nome.setText(String.valueOf(orderItems.get(position).getName()));
        holder.preco.setText(Util.formaterPrice(orderItems.get(position).getTotal_amount()));
        holder.quantidade.setText(String.valueOf(orderItems.get(position).getQuantity()));
        if (orderItems.get(position).getNote() != null && !orderItems.get(position).getNote().equals("")) {
            holder.prodObs.setVisibility(View.VISIBLE);
            holder.prodObs.setText(holder.prodObs.getContext().getResources().getString(R.string.obs_prod, orderItems.get(position).getNote()));
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(holder.ctx, RecyclerView.VERTICAL, false);
        holder.recyclerView.setLayoutManager(layoutManager);
        holder.adapter = new OrderExtrasAdapter();
        holder.recyclerView.setAdapter(holder.adapter);
        holder.adapter.setExtras(orderItems.get(position).getExtra());
    }

    @Override
    public int getItemCount() {
        if (null == orderItems)
            return 0;
        return orderItems.size();
    }

    public void setOrder(List<OrderItem> map) {
        orderItems = map;
        notifyDataSetChanged();
    }
}


