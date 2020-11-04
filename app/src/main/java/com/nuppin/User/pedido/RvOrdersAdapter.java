package com.nuppin.User.pedido;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.button.MaterialButton;
import com.nuppin.Util.AppConstants;
import com.nuppin.model.Order;
import com.nuppin.Util.Util;
import com.nuppin.R;

import java.util.List;

public class RvOrdersAdapter extends RecyclerView.Adapter<RvOrdersAdapter.RvOrdersAdapterViewHolder>{

    private RvOrdenOnClickListener listener;
    private List<Order> ordList;

    RvOrdersAdapter(RvOrdenOnClickListener handler) {
        listener = handler;
    }

    public class RvOrdersAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView ordId, ordStatus, stoNome, ordData, valorTotal;
        SimpleDraweeView img;
        MaterialButton avaliar;

        RvOrdersAdapterViewHolder(View view) {
            super(view);
            ordId = view.findViewById(R.id.ordId);
            ordStatus = view.findViewById(R.id.ordStatus);
            stoNome = view.findViewById(R.id.nomeLoja);
            img = view.findViewById(R.id.fotoLoja);
            avaliar = view.findViewById(R.id.btnAvaliar);
            ordData = view.findViewById(R.id.ordData);
            valorTotal = view.findViewById(R.id.ordValorTotal);

            avaliar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int adapterPosition = getAdapterPosition();
                    listener.onClickAvaliar(ordList.get(adapterPosition));
                }
            });

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            listener.onClick(ordList.get(adapterPosition));
        }
    }

    @NonNull
    public RvOrdersAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.rv_request;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new RvOrdersAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RvOrdersAdapterViewHolder holder, int position) {
        holder.avaliar.setVisibility(View.GONE);

        switch (ordList.get(position).getStatus()) {
            case AppConstants.STATUS_PENDING:
                holder.ordStatus.setText(R.string.pedido_aguardando_aprovacao);
                //todo - liberar cancelar com alguns parametros, como tempo excedido por exemplo
                break;
            case AppConstants.STATUS_ACCEPTED:
                if (ordList.get(position).getCategory_company_id().equals("1")) {
                    //todo - liberar cancelar com alguns parametros, como tempo excedido por exemplo
                    holder.ordStatus.setText(R.string.pedido_em_preparo);
                }else {
                    holder.ordStatus.setText(R.string.pedido_em_separacao);
                    //todo - liberar cancelar com alguns parametros, como tempo excedido por exemplo
                }
                break;
            case AppConstants.STATUS_DELIVERY:
                holder.ordStatus.setText(R.string.pedido_saiu_entrega);
                break;
            case AppConstants.STATUS_RELEASED:
                holder.ordStatus.setText(R.string.liberado_retirada);
                break;
            case AppConstants.STATUS_CONCLUDED_NOT_RATED:
            case AppConstants.STATUS_CONCLUDED:
                if (ordList.get(position).getRating() == 0) {
                    holder.avaliar.setVisibility(View.VISIBLE);
                }
                if (ordList.get(position).getType().equals("delivery")) {
                    holder.ordStatus.setText(R.string.pedido_entregue);
                }else {
                    holder.ordStatus.setText(R.string.pedido_retirado);
                }
                break;
            case AppConstants.STATUS_CANCELED_BY_ROBOT:
            case AppConstants.STATUS_CANCELED_BY_USER:
            case AppConstants.STATUS_CANCELED_REFUSED:
                holder.ordStatus.setText(R.string.pedido_cancelado);
                break;
            //todo - liberar para avaliar caso de cancelamento com alguns parametros, como tempo para cancelar por exemplo
        }
        holder.ordId.setText(ordList.get(position).getOrder_id());
        holder.stoNome.setText(ordList.get(position).getCompany_name());
        Util.hasPhoto(ordList.get(position),holder.img);
        holder.ordData.setText(Util.timestampFormatDayMonthYear(ordList.get(position).getCreated_date()));
        holder.valorTotal.setText(Util.formaterPrice(ordList.get(position).getTotal_amount()));
    }

    @Override
    public int getItemCount() {
        if (null == ordList)
            return 0;
        return ordList.size();
    }

    public void setOrder(List<Order> order) {
        ordList = order;
        notifyDataSetChanged();
    }

    public interface RvOrdenOnClickListener {
        void onClick(Order order);
        void onClickAvaliar(Order order);
    }

}

